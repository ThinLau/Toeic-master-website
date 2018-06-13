package com.toeicmaster.springmvc.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.toeicmaster.springmvc.model.User;
import com.toeicmaster.springmvc.service.S3Service;
import com.toeicmaster.springmvc.service.StorageService;


@Controller
public class UserController {

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
	
	private StorageService storageService;

	@Autowired
	public void FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}
	
	// user call create exercise page
	@RequestMapping(value="/new-exercise-page", method=RequestMethod.GET)
	public String newExercisePage(HttpSession session , Model model) {
		if(session.getAttribute("user") == null)
			return "login/login";
		User user = (User) session.getAttribute("user");
		
		Exercise exercise = new Exercise();
		model.addAttribute("exercise", exercise);
		model.addAttribute("user", user);
		model.addAttribute("module", "new-exercise");
		return "user/exercise/create_new_exercise";
	}
	
	// save the exercise 
	
	@RequestMapping(value="/save-exercise", method=RequestMethod.POST)
	public String saveExercise(HttpSession session , Model model,
			@ModelAttribute("exercise") Exercise exercise) {
		if(session.getAttribute("user") == null)
			return "login/login";
		
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		
		String dateCreate = dateFormat.format(date);
		
		exercise.setDateCreate(dateCreate);
		
		User user = (User) session.getAttribute("user");
		exercise.setAuthor(user.getId());
		
		
		exerciseDao.save(exercise);
		
		model.addAttribute("exercise", exercise);
		return "redirect:/new-exercise/info?id="+exercise.getId();
	}
	
	@RequestMapping(value="/update-exercise", method=RequestMethod.POST)
	public String upadateExerciseInfo(HttpSession session , Model model,
			@ModelAttribute("exercise") Exercise exercise) {
		
		Exercise entity = exerciseDao.findOne(exercise.getId());
		int exerciseId = exercise.getId();
		if(entity != null) {
			entity.setName(exercise.getName());
			entity.setNumberOfQuestion(exercise.getNumberOfQuestion());
			entity.setPart(exercise.getPart());
			exerciseDao.save(entity);
			exerciseId = entity.getId();
		}
		
		model.addAttribute("exercise", exercise);
		return "redirect:/new-exercise/info?id="+exerciseId;
	}
	
	@RequestMapping(value="/new-exercise/info", method=RequestMethod.GET)
	public String exerciseInfo(HttpSession session , @RequestParam("id") int exerciseId,
			Model model) {
		if(session.getAttribute("user") == null)
			return "login/login";
		
		Exercise exercise = exerciseDao.findOne(exerciseId);
		model.addAttribute("module", "exercise-info");
		model.addAttribute("exercise", exercise);
		return "user/exercise/exercise_info";
	}
	
	@RequestMapping(value="/exercise-question", method=RequestMethod.GET)
	public String exerciseQuestion(HttpSession session , @RequestParam("id") int exerciseId,
			 Model model) {
		
		return "redirect:/new-exercise/question?id="+exerciseId+"&num=1";
	}
	@RequestMapping(value="/new-exercise/question", method=RequestMethod.GET)
	private String returnQuestionPage(@RequestParam("id") int exerciseId, @RequestParam("num") int num, Model model) {
		Exercise exercise = exerciseDao.findOne(exerciseId);
		String viewPage = "error_page";
		int part = exercise.getPart();
		
		switch(part) {
			case 1:    // photo
				viewPage = "user/exercise/photo_question";
				break;
			case 2:    // question-response
				viewPage = "user/exercise/question_response_question";
				break;
			case 3:    // short converstion
				viewPage = "user/exercise/short_conversation_and_talk_question";
				break;
			case 4:    // short talk
				viewPage = "user/exercise/short_conversation_and_talk_question";
				break;
			case 5:    // incomplete sentence
				viewPage = "user/exercise/incomplete_sentence_question";
				break;
			case 6:    // text completion
				viewPage = "user/exercise/text_completion_question";
				break;
			case 7:    // single passage
				viewPage = "user/exercise/single_passage_question";
				break;
			case 8:    // double passage
				viewPage = "user/exercise/double_passage_question";
				break;
		}
		
		model.addAttribute("exercise", exercise);
		model.addAttribute("num", num);
		model.addAttribute("module", "exercise-question");
		return viewPage;
	}
	// create question for each part
	
	@RequestMapping(value="/save-photo-question/{exerciseId}/{num}", method=RequestMethod.POST)
	public String saveQuestion(HttpSession session , Model model, @PathVariable("exerciseId") int exerciseId,
			@RequestParam("image_question") MultipartFile photo,@RequestParam("audio_question") MultipartFile audio,
			@RequestParam("radio_question") String radio, @RequestParam("option1") String option1,
			@RequestParam("option2") String option2, @RequestParam("option3") String option3, @PathVariable("num") int num, 
			@RequestParam("option4") String option4) {
		if(session.getAttribute("user") == null)
			return "login/login";
		
		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		//set num 
		
		eq.setNum(num);
		eq.setSubQuestion(1);
		eq.setExerciseId(exerciseId);
		exerciseQuestionDao.save(eq);
		int exerciseQuestionId = eq.getId();
		
		
		String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
		
		// save audio file
		uploadAudio(eq, absolutePath, audio, exerciseQuestionId);
		
		// save photo file
		uploadPhoto(eq, absolutePath, photo, exerciseQuestionId);
		
		exerciseQuestionDao.save(eq);
		// save exercise question detail
		ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
		eqd.setNum(1);
		eqd.setOption1(option1);
		eqd.setOption2(option2);
		eqd.setOption3(option3);
		eqd.setOption4(option4);
		eqd.setCorrectAnswer(radio);
		eqd.setExerciseQuestionId(exerciseQuestionId);
		
		exerciseQuestionDetailDao.save(eqd);

		Exercise exercise = exerciseDao.findOne(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);
		
		return "redirect:/new-exercise/question?id="+exerciseId+"&num="+(num+1);
	}
	
	@RequestMapping(value="/save-question-response-question/{exerciseId}/{num}", method=RequestMethod.POST)
	public String saveQuestionResponseQuestion( Model model, @PathVariable("exerciseId") int exerciseId,
			@PathVariable("num") int num, @RequestParam("audio_question") MultipartFile audio, @RequestParam("radio_question") String radio,
			@RequestParam("option1") String option1, @RequestParam("option2") String option2, @RequestParam("option3") String option3,  
			@RequestParam("question-content") String questionContent) {
		
		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		//set num 
		
		eq.setNum(num);
		eq.setSubQuestion(1);
		eq.setExerciseId(exerciseId);
		exerciseQuestionDao.save(eq);  // save first then get id of this question
		int exerciseQuestionId = eq.getId();
		// save audio file
		String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
		
		uploadAudio(eq, absolutePath, audio, exerciseQuestionId);

		exerciseQuestionDao.save(eq);
		
		// save exercise question detail
		ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
		eqd.setNum(1);
		eqd.setQuestion(questionContent);
		eqd.setOption1(option1);
		eqd.setOption2(option2);
		eqd.setOption3(option3);
		eqd.setCorrectAnswer(radio);
		eqd.setExerciseQuestionId(exerciseQuestionId);
		
		exerciseQuestionDetailDao.save(eqd);

		// upadate number of question of exercise. 
		Exercise exercise = exerciseDao.findOne(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);
		
		return "redirect:/new-exercise/question?id="+exerciseId+"&num="+(num+1);
	}
	
	@RequestMapping(value="/save-short-conversation-talk-question/{exerciseId}/{num}", method=RequestMethod.POST)
	public String saveShortConversationAndTalkQuestion( Model model, @PathVariable("exerciseId") int exerciseId,
			@PathVariable("num") int num, @RequestParam("audio_question") MultipartFile audio, 
			@RequestParam Map<String,String> maps) {
		
		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		//set num 
		
		
		eq.setNum(num);
		eq.setSubQuestion(3); // each quesion in this part consist of 3 sub question
		eq.setExerciseId(exerciseId);
		eq.setParagraph(maps.get("paragraph"));
		exerciseQuestionDao.save(eq);  // save first then get id of this question
		int exerciseQuestionId = eq.getId();
		// save audio file
		String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
		
		uploadAudio(eq, absolutePath, audio, exerciseQuestionId);

		exerciseQuestionDao.save(eq);
		
		// save exercise question detail
		for(int i = 1; i <= 3; i++) {
			ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
			eqd.setNum(i);
			eqd.setQuestion(maps.get("question-content-"+i));
			eqd.setOption1(maps.get("option1_"+i));
			eqd.setOption2(maps.get("option2_"+i));
			eqd.setOption3(maps.get("option3_"+i));
			eqd.setOption4(maps.get("option4_"+i));
			eqd.setCorrectAnswer(maps.get("radio_question_"+i));
			eqd.setExerciseQuestionId(exerciseQuestionId);
			
			exerciseQuestionDetailDao.save(eqd);
		}
		
		

		// upadate number of question of exercise. 
		Exercise exercise = exerciseDao.findOne(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);
		
		return "redirect:/new-exercise/question?id="+exerciseId+"&num="+(num+1);
	}
	
	@RequestMapping(value="/save-incomplete-sentence-question/{exerciseId}/{num}", method=RequestMethod.POST)
	public String saveIncompleteSentenceQuestion( Model model, @PathVariable("exerciseId") int exerciseId,
			@PathVariable("num") int num, @RequestParam Map<String,String> maps) {
		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		
		eq.setNum(num);
		eq.setSubQuestion(1);
		eq.setExerciseId(exerciseId);
		exerciseQuestionDao.save(eq);  // save first then get id of this question
		int exerciseQuestionId = eq.getId();
		
		// save exercise question detail
		ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
		eqd.setNum(1);
		eqd.setQuestion(maps.get("question-content"));
		eqd.setOption1(maps.get("option1"));
		eqd.setOption2(maps.get("option2"));
		eqd.setOption3(maps.get("option3"));
		eqd.setOption4(maps.get("option4"));
		eqd.setCorrectAnswer(maps.get("radio_question"));
		eqd.setExerciseQuestionId(exerciseQuestionId);
			
		exerciseQuestionDetailDao.save(eqd);
		
		// upadate number of question of exercise. 
		Exercise exercise = exerciseDao.findOne(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);
		
		return "redirect:/new-exercise/question?id="+exerciseId+"&num="+(num+1);
	}
	
	@RequestMapping(value="/save-text-completion-question/{exerciseId}/{num}", method=RequestMethod.POST)
	public String saveTextCompletionQuestion( Model model, @PathVariable("exerciseId") int exerciseId,
			@PathVariable("num") int num, @RequestParam Map<String,String> maps) {
		
		// calculate the subquestion. each sub question has 5 name(1 radio and 4 option). the paragraph have 1 name.
		int subQuestion = (maps.size() - 1)/5;
		
		
		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		//set num 
		
		eq.setNum(num);
		eq.setSubQuestion(subQuestion);
		eq.setExerciseId(exerciseId);
		eq.setParagraph(maps.get("paragraph"));
		exerciseQuestionDao.save(eq);  // save first then get id of this question
		int exerciseQuestionId = eq.getId();
		
		
		// save exercise question detail
		for(int i = 1; i <= subQuestion; i++) {
			ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
			eqd.setNum(i);
			eqd.setOption1(maps.get("option1_"+i));
			eqd.setOption2(maps.get("option2_"+i));
			eqd.setOption3(maps.get("option3_"+i));
			eqd.setOption4(maps.get("option4_"+i));
			eqd.setCorrectAnswer(maps.get("radio_question_"+i));
			eqd.setExerciseQuestionId(exerciseQuestionId);
			
			exerciseQuestionDetailDao.save(eqd);
		}
		
		// upadate number of question of exercise. 
		Exercise exercise = exerciseDao.findOne(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);
		
		return "redirect:/new-exercise/question?id="+exerciseId+"&num="+(num+1);
	}
	
	@RequestMapping(value="/save-single-passage-question/{exerciseId}/{num}", method=RequestMethod.POST)
	public String saveSinglePassageQuestion( Model model, @PathVariable("exerciseId") int exerciseId,
			@PathVariable("num") int num, @RequestParam Map<String,String> maps) {
		
		// calculate the subquestion. each sub question has 5 name(1 radio and 4 option). the paragraph have 1 name.
		int subQuestion = (maps.size() - 1)/5;
		
		
		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		//set num 
		
		eq.setNum(num);
		eq.setSubQuestion(subQuestion);
		eq.setExerciseId(exerciseId);
		eq.setParagraph(maps.get("paragraph"));
		exerciseQuestionDao.save(eq);  // save first then get id of this question
		int exerciseQuestionId = eq.getId();
		
		
		// save exercise question detail
		for(int i = 1; i <= subQuestion; i++) {
			ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
			eqd.setNum(i);
			eqd.setOption1(maps.get("option1_"+i));
			eqd.setOption2(maps.get("option2_"+i));
			eqd.setOption3(maps.get("option3_"+i));
			eqd.setOption4(maps.get("option4_"+i));
			eqd.setCorrectAnswer(maps.get("radio_question_"+i));
			eqd.setQuestion(maps.get("question_content_"+i));
			eqd.setExerciseQuestionId(exerciseQuestionId);
			exerciseQuestionDetailDao.save(eqd);
		}
		
		// upadate number of question of exercise. 
		Exercise exercise = exerciseDao.findOne(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);
		
		return "redirect:/new-exercise/question?id="+exerciseId+"&num="+(num+1);
	}
	
	@RequestMapping(value="/save-double-passage-question/{exerciseId}/{num}", method=RequestMethod.POST)
	public String saveDoublePassageQuestion( Model model, @PathVariable("exerciseId") int exerciseId,
			@PathVariable("num") int num, @RequestParam Map<String,String> maps) {
		
		// calculate the subquestion. each sub question has 5 name(1 radio and 4 option). and 2 paragraph have 2 name.
		int subQuestion = (maps.size() - 2)/5;
		
		
		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		//set num 
		
		eq.setNum(num);
		eq.setSubQuestion(subQuestion);
		eq.setExerciseId(exerciseId);
		eq.setParagraph(maps.get("paragraph1"));
		eq.setParagraph2(maps.get("paragraph2"));
		exerciseQuestionDao.save(eq);  // save first then get id of this question
		int exerciseQuestionId = eq.getId();
		
		
		// save exercise question detail
		for(int i = 1; i <= subQuestion; i++) {
			ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
			eqd.setNum(i);
			eqd.setOption1(maps.get("option1_"+i));
			eqd.setOption2(maps.get("option2_"+i));
			eqd.setOption3(maps.get("option3_"+i));
			eqd.setOption4(maps.get("option4_"+i));
			eqd.setCorrectAnswer(maps.get("radio_question_"+i));
			eqd.setQuestion(maps.get("question_content_"+i));
			eqd.setExerciseQuestionId(exerciseQuestionId);
			exerciseQuestionDetailDao.save(eqd);
		}
		
		// upadate number of question of exercise. 
		Exercise exercise = exerciseDao.findOne(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);
		
		return "redirect:/new-exercise/question?id="+exerciseId+"&num="+(num+1);
	}
	
	
	// store file function
	private void storeFile(MultipartFile file, String path) {
		
		if (!file.isEmpty()) {
		try {
             //fileName = file.getOriginalFilename();
			//System.out.println("path: "+path);
             byte[] bytes = file.getBytes();
             BufferedOutputStream buffStream = 
                     new BufferedOutputStream(new FileOutputStream(new File(path)));
             buffStream.write(bytes);
             buffStream.close();
             //System.out.println("store file success: ");
         } catch (Exception e) {
        	// System.out.println("store file not success "+ e.getMessage());
         }
     } else {
         //return "Unable to upload. File is empty.";
     }
	}
	
	private void uploadAudio(ExerciseQuestion eq, String absolutePath, MultipartFile audio, int  exerciseQuestionId) {
		
		String audioStorePath = absolutePath + "/audio/"+ audio.getOriginalFilename();
		
		Path pathAudio = Paths.get(audioStorePath);
		String fileAudioName = storageService.store(pathAudio, audio);

		S3Service audio_s3 = new S3Service();

		String pathAudioFile = absolutePath + "/audio/"+ File.separator + fileAudioName;
		System.out.println(pathAudioFile);
		String audio_url = audio_s3.uploadS3(pathAudioFile, "exer", "audio", exerciseQuestionId);
		eq.setAudio(audio_url);
	}
	private void uploadPhoto (ExerciseQuestion eq, String absolutePath, MultipartFile photo, int  exerciseQuestionId) {
		String photoStorePath = absolutePath + "/photo/"+ photo.getOriginalFilename();
		
		Path pathPhoto = Paths.get(photoStorePath);
		String filePhotoName = storageService.store(pathPhoto, photo);

		S3Service photo_s3 = new S3Service();
		
		String pathPhotoFile = absolutePath + "/photo/"+ File.separator + filePhotoName;
		System.out.println(pathPhotoFile);			

		String photo_url = photo_s3.uploadS3(pathPhotoFile, "exer","photo", exerciseQuestionId);
		eq.setPhoto(photo_url);
	}
	
}
