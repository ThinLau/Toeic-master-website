package com.toeicmaster.springmvc.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.toeicmaster.springmvc.dao.ExerciseDao;
import com.toeicmaster.springmvc.dao.ExerciseQuestionDao;
import com.toeicmaster.springmvc.dao.ExerciseQuestionDetailDao;
import com.toeicmaster.springmvc.dao.UserDao;
import com.toeicmaster.springmvc.model.Exercise;
import com.toeicmaster.springmvc.model.ExerciseQuestion;
import com.toeicmaster.springmvc.model.ExerciseQuestionDetail;

@Controller
public class UpdateExerciseController {

	@Autowired
	UserDao userDao;

	@Autowired
	ExerciseDao exerciseDao;

	@Autowired
	ExerciseQuestionDao exerciseQuestionDao;

	@Autowired
	ExerciseQuestionDetailDao exerciseQuestionDetailDao;

	@Autowired
	ServletContext context;

	// update exercise info
	@RequestMapping(value = "/update-exercise/info", method = RequestMethod.GET)
	public String updateExerciseInfo(HttpSession session, @RequestParam("id") int exerciseId, Model model) {

		Exercise exercise = exerciseDao.findOne(exerciseId);
		model.addAttribute("module", "exercise-info");
		model.addAttribute("exercise", exercise);
		return "user/update/exercise/update_exercise_info";
	}

	@RequestMapping(value = "/update-exercise-info", method = RequestMethod.POST)
	public String upadateExerciseInfo(HttpSession session, Model model, @ModelAttribute("exercise") Exercise exercise) {

		Exercise entity = exerciseDao.findOne(exercise.getId());
		int exerciseId = exercise.getId();
		if (entity != null) {
			entity.setName(exercise.getName());
			entity.setNumberOfQuestion(exercise.getNumberOfQuestion());
			entity.setPart(exercise.getPart());
			exerciseDao.save(entity);
			exerciseId = entity.getId();
		}

		model.addAttribute("exercise", exercise);
		return "redirect:/update-exercise/info?id=" + exerciseId;
	}

	@RequestMapping(value = "/update-exercise-question", method = RequestMethod.GET)
	public String exerciseQuestion(HttpSession session, @RequestParam("id") int exerciseId, Model model) {

		return "redirect:/update-exercise/question?id=" + exerciseId + "&num=1";
	}

	@RequestMapping(value = "/update-exercise/question", method = RequestMethod.GET)
	private String returnQuestionPage(@RequestParam("id") int exerciseId, @RequestParam("num") int num, Model model) {
		
		Exercise exercise = exerciseDao.findOne(exerciseId);

		ExerciseQuestion eq = exerciseQuestionDao.findByNumAndExerciseId(num, exerciseId);
		
		List<ExerciseQuestionDetail> eqds = exerciseQuestionDetailDao.findByExerciseQuestionId(eq.getId());

		String viewPage = "error_page";
		int part = exercise.getPart();

		switch (part) {
		case 1: // photo
			viewPage = "user/update/exercise/update_photo_question";
			break;
		case 2: // question-response
			viewPage = "user/update/exercise/update_question_response_question";
			break;
		case 3: // short converstion
			viewPage = "user/update/exercise/update_short_conversation_and_talk_question";
			break;
		case 4: // short talk
			viewPage = "user/update/exercise/update_short_conversation_and_talk_question";
			break;
		case 5: // incomplete sentence
			viewPage = "user/update/exercise/update_incomplete_sentence_question";
			break;
		case 6: // text completion
			viewPage = "user/update/exercise/update_text_completion_question";
			break;
		case 7: // single passage
			viewPage = "user/update/exercise/update_single_passage_question";
			break;
		case 8: // double passage
			viewPage = "user/update/exercise/update_double_passage_question";
			break;
		}

		model.addAttribute("exercise", exercise);
		model.addAttribute("eq", eq); // return exercise question
		model.addAttribute("eqds", eqds); // return list exercise question detail
		model.addAttribute("num", num);
		model.addAttribute("module", "exercise-question");
		return viewPage;
	}

	// update question for each part
	// update part 1: photo
	@RequestMapping(value = "/update-photo-question-exercise/{exerciseQuestionId}", method = RequestMethod.POST)
	public String saveQuestion(Model model, @PathVariable("exerciseQuestionId") int exerciseQuestionId,
			@RequestParam("image_question") MultipartFile photo, @RequestParam("audio_question") MultipartFile audio,
			@RequestParam Map<String, String> maps) {

		// save exercise question
		ExerciseQuestion eq = exerciseQuestionDao.findOne(exerciseQuestionId);

		String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
		// if audio change will update
		if (!audio.isEmpty()) {
			// delete old audio file
			File audioFile = new File(absolutePath +"/audio/"+ eq.getAudio());
			if(audioFile.exists()) audioFile.delete();
			
			String audioStorePath = absolutePath + "/audio/" + "audio_" + exerciseQuestionId + "_update" + ".mp3";
			storeFile(audio, audioStorePath);
			eq.setAudio("audio_" + exerciseQuestionId + "_update" + ".mp3");
		}
		// if photo change will update
		if (!photo.isEmpty()) {
			
			/*
			// delete old photo file
			File photoFile = new File(absolutePath + "/photo/" + eq.getPhoto());
			if(photoFile.delete()){
    			System.out.println(photoFile.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
			String photoName = eq.getPhoto();
			int i = 0;
			if(photoName.contains("update")) { // if updated before
				// get last character
				int startIndex = photoName.indexOf("update_")+ 7;  // 7 is length of update_
				int endIndex = photoName.indexOf(".");
				i = Integer.parseInt(photoName.substring(startIndex,endIndex));
				i++;
			}
			*/
			
			String photoStorePath = absolutePath + "/photo/" + "photo_" + exerciseQuestionId + "_update"+ ".jpg";

			storeFile(photo, photoStorePath);
			eq.setPhoto("photo_" + exerciseQuestionId + "_update" + ".jpg");
		}
		// update exercise question
		exerciseQuestionDao.save(eq);

		// update exercise question detail. photo part just have one question.
		ExerciseQuestionDetail eqd = exerciseQuestionDetailDao.findByExerciseQuestionId(exerciseQuestionId).get(0);

		eqd.setNum(1);
		eqd.setOption1(maps.get("option1"));
		eqd.setOption2(maps.get("option2"));
		eqd.setOption3(maps.get("option3"));
		eqd.setOption4(maps.get("option4"));
		eqd.setCorrectAnswer(maps.get("radio_question"));
		eqd.setExerciseQuestionId(exerciseQuestionId);

		exerciseQuestionDetailDao.save(eqd);

		return "redirect:/update-exercise/question?id=" + eq.getExerciseId() + "&num=" + eq.getNum();
	}

	// update part 2: question response
	@RequestMapping(value = "/update-question-response-exercise/{exerciseQuestionId}", method = RequestMethod.POST)
	public String updateQuestionResponse(Model model, @PathVariable("exerciseQuestionId") int exerciseQuestionId,
			@RequestParam("audio_question") MultipartFile audio, @RequestParam Map<String, String> maps) {

		// save exercise question
		ExerciseQuestion eq = exerciseQuestionDao.findOne(exerciseQuestionId);

		String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
		// if audio change will update
		if (!audio.isEmpty()) {
			String audioStorePath = absolutePath + "/audio/" + "audio_" + exerciseQuestionId + "_update" + ".mp3";
			storeFile(audio, audioStorePath);
			eq.setAudio("audio_" + exerciseQuestionId + "_update" + ".mp3");
		}
		// update exercise question
		exerciseQuestionDao.save(eq);

		// update exercise question detail. photo part just have one question.
		ExerciseQuestionDetail eqd = exerciseQuestionDetailDao.findByExerciseQuestionId(exerciseQuestionId).get(0);

		eqd.setNum(1);
		eqd.setOption1(maps.get("option1"));
		eqd.setOption2(maps.get("option2"));
		eqd.setOption3(maps.get("option3"));
		eqd.setCorrectAnswer(maps.get("radio_question"));
		eqd.setExerciseQuestionId(exerciseQuestionId);

		exerciseQuestionDetailDao.save(eqd);

		return "redirect:/update-exercise/question?id=" + eq.getExerciseId() + "&num=" + eq.getNum();
	}

	// update part 3 & 4: short talk and conversation
	@RequestMapping(value = "/update-short-conversation-and-talk-exercise/{exerciseQuestionId}", method = RequestMethod.POST)
	public String updateShortConversationAndTalk(Model model,
			@PathVariable("exerciseQuestionId") int exerciseQuestionId,
			@RequestParam("audio_question") MultipartFile audio, @RequestParam Map<String, String> maps) {

		// save exercise question
		ExerciseQuestion eq = exerciseQuestionDao.findOne(exerciseQuestionId);

		String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
		// if audio change will update
		if (!audio.isEmpty()) {
			String audioStorePath = absolutePath + "/audio/" + "audio_" + exerciseQuestionId + "_update" + ".mp3";
			storeFile(audio, audioStorePath);
			eq.setAudio("audio_" + exerciseQuestionId + "_update" + ".mp3");
		}
		eq.setParagraph(maps.get("paragraph"));
		// update exercise question
		exerciseQuestionDao.save(eq);

		// update exercise question detail. photo part just have one question.
		List<ExerciseQuestionDetail> eqds = exerciseQuestionDetailDao.findByExerciseQuestionId(exerciseQuestionId);

		// save exercise question detail
		int i = 1;
		for (ExerciseQuestionDetail eqd : eqds) {
			eqd.setNum(i);
			eqd.setQuestion(maps.get("question-content-" + i));
			eqd.setOption1(maps.get("option1_" + i));
			eqd.setOption2(maps.get("option2_" + i));
			eqd.setOption3(maps.get("option3_" + i));
			eqd.setOption4(maps.get("option4_" + i));
			eqd.setCorrectAnswer(maps.get("radio_question_" + i));
			eqd.setExerciseQuestionId(exerciseQuestionId);
				
			exerciseQuestionDetailDao.save(eqd);
			i++;
		}

		return "redirect:/update-exercise/question?id=" + eq.getExerciseId() + "&num=" + eq.getNum();
	}

	// update part 5: incomplete sentence
		@RequestMapping(value = "/update-incomplete-sentence-exercise/{exerciseQuestionId}", method = RequestMethod.POST)
		public String updateIncompleteSentence(Model model, @PathVariable("exerciseQuestionId") int exerciseQuestionId,
				 @RequestParam Map<String, String> maps) {

			ExerciseQuestion eq = exerciseQuestionDao.findOne(exerciseQuestionId);

			// update exercise question detail. photo part just have one question.
			ExerciseQuestionDetail eqd = exerciseQuestionDetailDao.findByExerciseQuestionId(exerciseQuestionId).get(0);

			eqd.setNum(1);
			eqd.setQuestion(maps.get("question-content"));
			eqd.setOption1(maps.get("option1"));
			eqd.setOption2(maps.get("option2"));
			eqd.setOption3(maps.get("option3"));
			eqd.setOption4(maps.get("option4"));
			eqd.setCorrectAnswer(maps.get("radio_question"));
			eqd.setExerciseQuestionId(exerciseQuestionId);

			exerciseQuestionDetailDao.save(eqd);

			return "redirect:/update-exercise/question?id=" + eq.getExerciseId() + "&num=" + eq.getNum();
		}
	
		
		// update part 6: text complete
		@RequestMapping(value = "/update-text-complete-exercise/{exerciseQuestionId}", method = RequestMethod.POST)
		public String updateTextCompleteExercise(Model model, @PathVariable("exerciseQuestionId") int exerciseQuestionId,
				 @RequestParam Map<String, String> maps) {

			// save exercise question
			ExerciseQuestion eq = exerciseQuestionDao.findOne(exerciseQuestionId);

			eq.setParagraph(maps.get("paragraph"));
			// update exercise question
			exerciseQuestionDao.save(eq);

			// update exercise question detail.
			List<ExerciseQuestionDetail> eqds = exerciseQuestionDetailDao.findByExerciseQuestionId(exerciseQuestionId);

			// save exercise question detail
			int i = 1;
			for (ExerciseQuestionDetail eqd : eqds) {
				eqd.setNum(i);
				eqd.setOption1(maps.get("option1_" + i));
				eqd.setOption2(maps.get("option2_" + i));
				eqd.setOption3(maps.get("option3_" + i));
				eqd.setOption4(maps.get("option4_" + i));
				eqd.setCorrectAnswer(maps.get("radio_question_" + i));
				eqd.setExerciseQuestionId(exerciseQuestionId);
					
				exerciseQuestionDetailDao.save(eqd);
				i++;
			}

			return "redirect:/update-exercise/question?id=" + eq.getExerciseId() + "&num=" + eq.getNum();
		}
	
		// update part 7: single passage
		@RequestMapping(value = "/update-single-passage-exercise/{exerciseQuestionId}", method = RequestMethod.POST)
		public String updateSinglePassage(Model model, @PathVariable("exerciseQuestionId") int exerciseQuestionId,
				@RequestParam Map<String, String> maps) {

			// save exercise question
			ExerciseQuestion eq = exerciseQuestionDao.findOne(exerciseQuestionId);
			eq.setParagraph(maps.get("paragraph"));
			// update exercise question
			exerciseQuestionDao.save(eq);

			// update exercise question detail. photo part just have one question.
			List<ExerciseQuestionDetail> eqds = exerciseQuestionDetailDao.findByExerciseQuestionId(exerciseQuestionId);

			// save exercise question detail
			int i = 1;
			for (ExerciseQuestionDetail eqd : eqds) {
				eqd.setNum(i);
				eqd.setQuestion(maps.get("question-content-" + i));
				eqd.setOption1(maps.get("option1_" + i));
				eqd.setOption2(maps.get("option2_" + i));
				eqd.setOption3(maps.get("option3_" + i));
				eqd.setOption4(maps.get("option4_" + i));
				eqd.setCorrectAnswer(maps.get("radio_question_" + i));
				eqd.setExerciseQuestionId(exerciseQuestionId);
					
				exerciseQuestionDetailDao.save(eqd);
				i++;
			}

			return "redirect:/update-exercise/question?id=" + eq.getExerciseId() + "&num=" + eq.getNum();
		}

		// update part 8: double passage
		@RequestMapping(value = "/update-double-passage-exercise/{exerciseQuestionId}", method = RequestMethod.POST)
		public String updateDoublePassage(Model model, @PathVariable("exerciseQuestionId") int exerciseQuestionId,
				@RequestParam Map<String, String> maps) {

			// save exercise question
			ExerciseQuestion eq = exerciseQuestionDao.findOne(exerciseQuestionId);
			eq.setParagraph(maps.get("paragraph1"));
			eq.setParagraph2(maps.get("paragraph2"));
			// update exercise question
			exerciseQuestionDao.save(eq);

			// update exercise question detail. photo part just have one question.
			List<ExerciseQuestionDetail> eqds = exerciseQuestionDetailDao.findByExerciseQuestionId(exerciseQuestionId);

			// save exercise question detail
			int i = 1;
			for (ExerciseQuestionDetail eqd : eqds) {
				eqd.setNum(i);
				eqd.setQuestion(maps.get("question-content-" + i));
				eqd.setOption1(maps.get("option1_" + i));
				eqd.setOption2(maps.get("option2_" + i));
				eqd.setOption3(maps.get("option3_" + i));
				eqd.setOption4(maps.get("option4_" + i));
				eqd.setCorrectAnswer(maps.get("radio_question_" + i));
				eqd.setExerciseQuestionId(exerciseQuestionId);
					
				exerciseQuestionDetailDao.save(eqd);
				i++;
			}

			return "redirect:/update-exercise/question?id=" + eq.getExerciseId() + "&num=" + eq.getNum();
		}

		
	

	// store file function
	private void storeFile(MultipartFile file, String path) {

		if (!file.isEmpty()) {
			try {
				// fileName = file.getOriginalFilename();
				// System.out.println("path: "+path);
				byte[] bytes = file.getBytes();
				BufferedOutputStream buffStream = new BufferedOutputStream(new FileOutputStream(new File(path)));
				buffStream.write(bytes);
				buffStream.close();
				// System.out.println("store file success: ");
			} catch (Exception e) {
				// System.out.println("store file not success "+ e.getMessage());
			}
		} else {
			// return "Unable to upload. File is empty.";
		}
	}

}
