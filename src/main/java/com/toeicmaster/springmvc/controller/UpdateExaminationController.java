package com.toeicmaster.springmvc.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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

import com.toeicmaster.springmvc.dao.ExaminationDao;
import com.toeicmaster.springmvc.dao.ExaminationQuestionDao;
import com.toeicmaster.springmvc.dao.ExaminationQuestionDetailDao;
import com.toeicmaster.springmvc.model.Examination;
import com.toeicmaster.springmvc.model.ExaminationQuestion;
import com.toeicmaster.springmvc.model.ExaminationQuestionDetail;
import com.toeicmaster.springmvc.service.S3Service;
import com.toeicmaster.springmvc.service.StorageService;

@Controller
public class UpdateExaminationController {

	@Autowired
	ExaminationDao examDao;

	@Autowired
	ExaminationQuestionDao examQuestionDao;

	@Autowired
	ExaminationQuestionDetailDao examQuestionDetailDao;
	
	private StorageService storageService;

	@Autowired
	public void FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	@RequestMapping(value = "/update-examination/info", method = RequestMethod.GET)
	public String exerciseInfo(HttpSession session, @RequestParam("id") int examId, Model model) {

		Examination exam = examDao.findById(examId);
		model.addAttribute("exam", exam);
		return "user/update/examination/examination_info";
	}

	@RequestMapping(value = "/update-examination-info", method = RequestMethod.POST)
	public String upadateExerciseInfo(HttpSession session, Model model, @ModelAttribute("exam") Examination exam) {

		Examination entity = examDao.findById(exam.getId());
		int examId = exam.getId();
		if (entity != null) {
			entity.setName(exam.getName());
			entity.setNumberOfQuestion(exam.getNumberOfQuestion());
			entity.setTimeOut(exam.getTimeOut());
			examDao.save(entity);
			examId = entity.getId();
		}

		model.addAttribute("exam", exam);
		return "redirect:/update-examination/info?id=" + examId;
	}

	@RequestMapping(value = "/update-examination/{part}", method = RequestMethod.GET)
	public String PhotoPart(@PathVariable("part") int part, HttpSession session, @RequestParam("num") int num,
			@RequestParam("id") int examId, Model model) {

		String result = "error_page";
		Examination exam = examDao.findById(examId);
		List<ExaminationQuestion> eqs = examQuestionDao.findByExamIdAndPart(examId, part);

		int partSize = eqs.size();

		ExaminationQuestion eq = eqs.get(num - 1); // num is index of list examination question.
		List<ExaminationQuestionDetail> eqds = examQuestionDetailDao.findByExamQuestionId(eq.getId());

		switch (part) {
		case 1:
			result = "user/update/examination/photo_examination_question";
			break;
		case 2:
			result = "user/update/examination/question_response_examination_question";
			break;
		case 3:
			result = "user/update/examination/short_conversation_examination_question";
			break;
		case 4:
			result = "user/update/examination/short_talk_examination_question";
			break;
		case 5:
			result = "user/update/examination/incomplete_sentence_examination_question";
			break;
		case 6:
			result = "user/update/examination/text_completion_examination_question";
			break;
		case 7:
			result = "user/update/examination/single_passage_examination_question";
			break;
		case 8:
			result = "user/update/examination/double_passage_examination_question";
			break;

		}

		model.addAttribute("exam", exam);
		model.addAttribute("eq", eq);
		model.addAttribute("eqds", eqds);
		model.addAttribute("partSize", partSize);
		model.addAttribute("num", num);
		return result;
	}

	// update part 1: photo
	@RequestMapping(value = "/update-examination-photo-part/{examQuestionId}", method = RequestMethod.POST)
	public String saveQuestion(Model model, @PathVariable("examQuestionId") int examQuestionId,
			@RequestParam("num") int num, @RequestParam("image_question") MultipartFile photo,
			@RequestParam("audio_question") MultipartFile audio, @RequestParam Map<String, String> maps) {

		// save exercise question
		ExaminationQuestion eq = examQuestionDao.findById(examQuestionId);

		String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
		// if audio change will update
		if (!audio.isEmpty() || !photo.isEmpty()) {
			/*// delete old audio file
			File audioFile = new File(absolutePath + "/audio/" + eq.getAudio());
			if (audioFile.exists())
				audioFile.delete();

			String audioStorePath = absolutePath + "/audio/" + "exam_audio_" + examQuestionId + "_update" + ".mp3";
			storeFile(audio, audioStorePath);
			eq.setAudio("exam_audio_" + examQuestionId + "_update" + ".mp3");*/
			
			uploadfileS3(eq, absolutePath, audio, photo, examQuestionId);
		}		
		// update exercise question
		examQuestionDao.save(eq);

		// update exercise question detail. photo part just have one question.
		ExaminationQuestionDetail eqd = examQuestionDetailDao.findByExamQuestionId(examQuestionId).get(0);

		eqd.setOption1(maps.get("option1"));
		eqd.setOption2(maps.get("option2"));
		eqd.setOption3(maps.get("option3"));
		eqd.setOption4(maps.get("option4"));
		eqd.setCorrectAnswer(maps.get("radio_question"));

		examQuestionDetailDao.save(eqd);

		return "redirect:/update-examination/" + eq.getPart() + "?id=" + eq.getExamId() + "&num=" + num;
	}

	// update part 2: question response
	@RequestMapping(value = "/update-examination-question-response-part/{examQuestionId}", method = RequestMethod.POST)
	public String updateQuestionResponse(Model model, @PathVariable("examQuestionId") int examQuestionId,
			@RequestParam("num") int num, @RequestParam("audio_question") MultipartFile audio,
			@RequestParam Map<String, String> maps) {

		// save exercise question
		ExaminationQuestion eq = examQuestionDao.findById(examQuestionId);

		String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
		// if audio change will update
		if (!audio.isEmpty()) {
			/*String audioStorePath = absolutePath + "/audio/" + "exam_audio_" + examQuestionId + "_update" + ".mp3";
			storeFile(audio, audioStorePath);
			eq.setAudio("exam_audio_" + examQuestionId + "_update" + ".mp3");*/
			uploadfileS3(eq, absolutePath, audio, null, examQuestionId);
			
		}
		// update exercise question
		examQuestionDao.save(eq);

		// update exercise question detail. photo part just have one question.
		ExaminationQuestionDetail eqd = examQuestionDetailDao.findByExamQuestionId(examQuestionId).get(0);

		eqd.setOption1(maps.get("option1"));
		eqd.setOption2(maps.get("option2"));
		eqd.setOption3(maps.get("option3"));
		eqd.setCorrectAnswer(maps.get("radio_question"));

		examQuestionDetailDao.save(eqd);

		return "redirect:/update-examination/" + eq.getPart() + "?id=" + eq.getExamId() + "&num=" + num;
	}

	// update part 3 & 4: short talk and conversation
	@RequestMapping(value = "/update-examination-short-conversation-talk-part/{examQuestionId}", method = RequestMethod.POST)
	public String updateShortConversationAndTalk(Model model, @PathVariable("examQuestionId") int examQuestionId,
			@RequestParam("num") int num, @RequestParam("audio_question") MultipartFile audio,
			@RequestParam Map<String, String> maps) {

		// save exercise question
		ExaminationQuestion eq = examQuestionDao.findById(examQuestionId);

		String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
		// if audio change will update
		if (!audio.isEmpty()) {
			/*String audioStorePath = absolutePath + "/audio/" + "exam_audio_" + examQuestionId + "_update" + ".mp3";
			storeFile(audio, audioStorePath);
			eq.setAudio("exam_audio_" + examQuestionId + "_update" + ".mp3");*/
			
			uploadfileS3(eq, absolutePath, audio, null, examQuestionId);
		}
		eq.setParagraph(maps.get("paragraph"));
		// update exercise question
		examQuestionDao.save(eq);

		// update exercise question detail. photo part just have one question.
		List<ExaminationQuestionDetail> eqds = examQuestionDetailDao.findByExamQuestionId(examQuestionId);

		// save exercise question detail
		int i = 1;
		for (ExaminationQuestionDetail eqd : eqds) {
			eqd.setQuestion(maps.get("question-content-" + i));
			eqd.setOption1(maps.get("option1_" + i));
			eqd.setOption2(maps.get("option2_" + i));
			eqd.setOption3(maps.get("option3_" + i));
			eqd.setOption4(maps.get("option4_" + i));
			eqd.setCorrectAnswer(maps.get("radio_question_" + i));

			examQuestionDetailDao.save(eqd);
			i++;
		}

		return "redirect:/update-examination/" + eq.getPart() + "?id=" + eq.getExamId() + "&num=" + num;
	}

	// update part 5: incomplete sentence
	@RequestMapping(value = "update-examination-incomplete-sentence-part/{examQuestionId}", method = RequestMethod.POST)
	public String updateIncompleteSentence(Model model, @PathVariable("examQuestionId") int examQuestionId,
			@RequestParam("num") int num, @RequestParam Map<String, String> maps) {

		ExaminationQuestion eq = examQuestionDao.findById(examQuestionId);

		// update exercise question detail. photo part just have one question.
		ExaminationQuestionDetail eqd = examQuestionDetailDao.findByExamQuestionId(examQuestionId).get(0);

		eqd.setQuestion(maps.get("question-content"));
		eqd.setOption1(maps.get("option1"));
		eqd.setOption2(maps.get("option2"));
		eqd.setOption3(maps.get("option3"));
		eqd.setOption4(maps.get("option4"));
		eqd.setCorrectAnswer(maps.get("radio_question"));

		examQuestionDetailDao.save(eqd);

		return "redirect:/update-examination/" + eq.getPart() + "?id=" + eq.getExamId() + "&num=" + num;
	}

	// update part 6: text complete
	@RequestMapping(value = "/update-examination-text-completion-part/{examQuestionId}", method = RequestMethod.POST)
	public String updateTextCompleteExercise(Model model, @PathVariable("examQuestionId") int examQuestionId,
			@RequestParam("num") int num, @RequestParam Map<String, String> maps) {

		// save exercise question
		ExaminationQuestion eq = examQuestionDao.findById(examQuestionId);

		eq.setParagraph(maps.get("paragraph"));
		// update exercise question
		examQuestionDao.save(eq);

		// update exercise question detail.
		List<ExaminationQuestionDetail> eqds = examQuestionDetailDao.findByExamQuestionId(examQuestionId);

		// save exercise question detail
		int i = 1;
		for (ExaminationQuestionDetail eqd : eqds) {
			eqd.setOption1(maps.get("option1_" + i));
			eqd.setOption2(maps.get("option2_" + i));
			eqd.setOption3(maps.get("option3_" + i));
			eqd.setOption4(maps.get("option4_" + i));
			eqd.setCorrectAnswer(maps.get("radio_question_" + i));

			examQuestionDetailDao.save(eqd);
			i++;
		}

		return "redirect:/update-examination/" + eq.getPart() + "?id=" + eq.getExamId() + "&num=" + num;
	}

	// update part 7: single passage
	@RequestMapping(value = "/update-examination-single-passage-part/{examQuestionId}", method = RequestMethod.POST)
	public String updateSinglePassage(Model model, @PathVariable("examQuestionId") int examQuestionId,
			@RequestParam("num") int num, @RequestParam Map<String, String> maps) {

		// save exercise question
		ExaminationQuestion eq = examQuestionDao.findById(examQuestionId);
		eq.setParagraph(maps.get("paragraph"));
		// update exercise question
		examQuestionDao.save(eq);

		// update exercise question detail. photo part just have one question.
		List<ExaminationQuestionDetail> eqds = examQuestionDetailDao.findByExamQuestionId(examQuestionId);

		// save exercise question detail
		int i = 1;
		for (ExaminationQuestionDetail eqd : eqds) {
			eqd.setQuestion(maps.get("question-content-" + i));
			eqd.setOption1(maps.get("option1_" + i));
			eqd.setOption2(maps.get("option2_" + i));
			eqd.setOption3(maps.get("option3_" + i));
			eqd.setOption4(maps.get("option4_" + i));
			eqd.setCorrectAnswer(maps.get("radio_question_" + i));

			examQuestionDetailDao.save(eqd);
			i++;
		}

		return "redirect:/update-examination/" + eq.getPart() + "?id=" + eq.getExamId() + "&num=" + num;
	}

	// update part 8: double passage
	// update part 7: single passage
		@RequestMapping(value = "/update-examination-double-passage-part/{examQuestionId}", method = RequestMethod.POST)
		public String updateDoublePassage(Model model, @PathVariable("examQuestionId") int examQuestionId,
				@RequestParam("num") int num, @RequestParam Map<String, String> maps) {

		// save exercise question
		ExaminationQuestion eq = examQuestionDao.findById(examQuestionId);
		eq.setParagraph(maps.get("paragraph1"));
		eq.setParagraph2(maps.get("paragraph2"));
		// update exercise question
		examQuestionDao.save(eq);

		// update exercise question detail. photo part just have one question.
		List<ExaminationQuestionDetail> eqds = examQuestionDetailDao.findByExamQuestionId(examQuestionId);

		// save exercise question detail
		int i = 1;
		for (ExaminationQuestionDetail eqd : eqds) {
			eqd.setQuestion(maps.get("question-content-" + i));
			eqd.setOption1(maps.get("option1_" + i));
			eqd.setOption2(maps.get("option2_" + i));
			eqd.setOption3(maps.get("option3_" + i));
			eqd.setOption4(maps.get("option4_" + i));
			eqd.setCorrectAnswer(maps.get("radio_question_" + i));

			examQuestionDetailDao.save(eqd);
			i++;
		}

		return "redirect:/update-examination/" + eq.getPart() + "?id=" + eq.getExamId() + "&num=" + num;
	}

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
	
	private void uploadfileS3(ExaminationQuestion eq, String absolutePath, MultipartFile audio, MultipartFile photo, int  examQuestionId) {
			
			/*String audioStorePath = absolutePath + "/audio/"+ audio.getOriginalFilename();		
			Path pathAudio = Paths.get(audioStorePath);
			String fileAudioName = storageService.store(pathAudio, audio);	
			String pathAudioFile = absolutePath + "/audio/"+ File.separator + fileAudioName;
			System.out.println(pathAudioFile);*/
		
			S3Service file_s3 = new S3Service();
			
			String audio_url = file_s3.uploadS3(audio, "exam", "audio", examQuestionId);
			eq.setAudio(audio_url);
			if(photo != null) {
				String photo_url = file_s3.uploadS3(photo, "exam", "photo", examQuestionId);
				eq.setPhoto(photo_url);
			}
	
	}	
/*private void uploadAudio(ExaminationQuestion eq, String absolutePath, MultipartFile audio, int  examQuestionId) {
		
		String audioStorePath = absolutePath + "/audio/"+ audio.getOriginalFilename();
		
		Path pathAudio = Paths.get(audioStorePath);
		String fileAudioName = storageService.store(pathAudio, audio);

		S3Service audio_s3 = new S3Service();

		String pathAudioFile = absolutePath + "/audio/"+ File.separator + fileAudioName;
		System.out.println(pathAudioFile);
		String audio_url = audio_s3.uploadS3(audio, "exam", "audio", examQuestionId);
		eq.setAudio(audio_url);
	}
	private void uploadPhoto (ExaminationQuestion eq, String absolutePath, MultipartFile photo, int  examQuestionId) {
		String photoStorePath = absolutePath + "/photo/"+ photo.getOriginalFilename();
		
		Path pathPhoto = Paths.get(photoStorePath);
		String filePhotoName = storageService.store(pathPhoto, photo);

		S3Service photo_s3 = new S3Service();
		
		String pathPhotoFile = absolutePath + "/photo/"+ File.separator + filePhotoName;
		System.out.println(pathPhotoFile);			

		String photo_url = photo_s3.uploadS3(photo, "exam","photo", examQuestionId);
		eq.setPhoto(photo_url);
	}*/

}
