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
import com.toeicmaster.springmvc.model.ExerciseQuestion;
import com.toeicmaster.springmvc.model.User;
import com.toeicmaster.springmvc.service.S3Service;
import com.toeicmaster.springmvc.service.StorageService;

@Controller
public class CreateExaminationController {

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
	
	private int quesNum = 0;

	@RequestMapping(value = "/new-examination-page", method = RequestMethod.GET)
	public String newExercisePage(HttpSession session, Model model) {
		if (session.getAttribute("user") == null)
			return "login/login";

		User user = (User) session.getAttribute("user");

		Examination exam = new Examination();
		model.addAttribute("exam", exam);
		model.addAttribute("user", user);
		model.addAttribute("module", "new-examination");
		return "user/examination/create_new_examination";
	}

	@RequestMapping(value = "/save-examination", method = RequestMethod.POST)
	public String saveExercise(HttpSession session, Model model, @ModelAttribute("exam") Examination exam) {
		if (session.getAttribute("user") == null)
			return "login/login";

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();

		String dateCreate = dateFormat.format(date);

		exam.setDateCreate(dateCreate);

		User user = (User) session.getAttribute("user");
		exam.setAuthor(user.getId());

		examDao.save(exam);

		model.addAttribute("exam", exam);
		return "redirect:/new-examination/info?id=" + exam.getId();
	}

	@RequestMapping(value = "/new-examination/info", method = RequestMethod.GET)
	public String exerciseInfo(HttpSession session, @RequestParam("id") int examId, Model model) {
		if (session.getAttribute("user") == null)
			return "login/login";

		Examination exam = examDao.findOne(examId);
		model.addAttribute("exam", exam);
		return "user/examination/examination_info";
	}

	@RequestMapping(value = "/update-examination", method = RequestMethod.POST)
	public String upadateExerciseInfo(HttpSession session, Model model, @ModelAttribute("exam") Examination exam) {

		Examination entity = examDao.findOne(exam.getId());
		int examId = exam.getId();
		if (entity != null) {
			entity.setName(exam.getName());
			entity.setNumberOfQuestion(exam.getNumberOfQuestion());
			entity.setTimeOut(exam.getTimeOut());
			examDao.save(entity);
			examId = entity.getId();
		}

		model.addAttribute("exam", exam);
		return "redirect:/new-examination/info?id=" + examId;
	}

	@RequestMapping(value = "/new-examination/{part}", method = RequestMethod.GET)
	public String PhotoPart(@PathVariable("part") String partName, HttpSession session, @RequestParam("id") int examId,
			Model model) {

		String result = "error_page";
		int currQuestion = 0;
		int num = 0;
		// find question number.
		// check this examination have question yet.
		int totalQuestion = examQuestionDao.findByExamId(examId).size();
		if (totalQuestion == 0) { // neu chua co cau hoi
			currQuestion = 1;
			num = 1;
		} else {
			for (ExaminationQuestion object : examQuestionDao.findByExamId(examId)) {
				currQuestion += object.getSubQuestion();
				num = object.getNum();
			}
			currQuestion++;
			num++;
		}

		switch (partName) {
		case "Photo":
			result = "user/examination/photo_examination_question";
			break;
		case "Question-Response":
			result = "user/examination/question_response_examination_question";
			break;
		case "Short-conversation":
			result = "user/examination/short_conversation_examination_question";
			break;
		case "Short-talk":
			result = "user/examination/short_talk_examination_question";
			break;
		case "Incomplete-Sentence":
			result = "user/examination/incomplete_sentence_examination_question";
			break;
		case "Text-completion":
			result = "user/examination/text_completion_examination_question";
			break;
		case "Single-passage":
			result = "user/examination/single_passage_examination_question";
			break;
		case "Double-passage":
			result = "user/examination/double_passage_examination_question";
			break;

		}

		Examination exam = examDao.findOne(examId);
		model.addAttribute("exam", exam);
		model.addAttribute("currQuestion", currQuestion);
		model.addAttribute("num", num);
		return result;
	}

	// save photo question.
	@RequestMapping(value = "/save-examination-photo-part/{part}/{num}/{currQuestion}", method = RequestMethod.POST)
	public String saveExaminationPhotoQuestion(@PathVariable("part") String partName, @PathVariable("num") int num,
			@PathVariable("currQuestion") int currQuestion, @RequestParam("id") int examId, Model model, 
			@RequestParam("audio_question") MultipartFile audio, @RequestParam("image_question") MultipartFile photo, @RequestParam Map<String, String> maps) {

		// save examination question
		ExaminationQuestion eq = new ExaminationQuestion();

		eq.setNum(num);
		eq.setSubQuestion(1);
		eq.setExamId(examId);
		eq.setPart(1);
		examQuestionDao.save(eq);
		int examQuestionId = eq.getId();
		// save audio file

		String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();

		/*String audioStorePath = absolutePath + "/audio/" + "exam_audio_" + examId + "_" + examQuestionId + ".mp3";
		storeFile(audio, audioStorePath);
		eq.setAudio("exam_audio_" + examId + "_" + examQuestionId + ".mp3");*/
		
		uploadAudio(eq, absolutePath, audio, examQuestionId);
		// save photo file
		
		uploadPhoto(eq, absolutePath, photo, examQuestionId);
		/*String photoStorePath = absolutePath + "/photo/" + "exam_photo_" + examId + "_" + examQuestionId + ".jpg";
		storeFile(photo, photoStorePath);
		eq.setPhoto("exam_photo_" + examId + "_" + examQuestionId + ".jpg");*/

		examQuestionDao.save(eq);
		// save exam question detail
		ExaminationQuestionDetail eqd = new ExaminationQuestionDetail();
		eqd.setNum(1);
		eqd.setNumInExam(currQuestion);
		eqd.setExamId(examId);
		eqd.setOption1(maps.get("option1"));
		eqd.setOption2(maps.get("option2"));
		eqd.setOption3(maps.get("option3"));
		eqd.setOption4(maps.get("option4"));
		eqd.setCorrectAnswer(maps.get("radio_question"));
		eqd.setExamQuestionId(examQuestionId);

		examQuestionDetailDao.save(eqd);

		return "redirect:/new-examination/" + partName + "?id=" + examId;
	}

	// save question response question.
	@RequestMapping(value = "/save-examination-question-response-part/{part}/{num}/{currQuestion}", method = RequestMethod.POST)
	public String saveExaminationQuestionResponseQuestion(@PathVariable("currQuestion") int currQuestion, 
			@PathVariable("part") String partName, @PathVariable("num") int num, @RequestParam("id") int examId, Model model,
			@RequestParam("audio_question") MultipartFile audio, @RequestParam Map<String, String> maps) {

		// save examination question
		ExaminationQuestion eq = new ExaminationQuestion();

		eq.setNum(num);
		eq.setSubQuestion(1);
		eq.setExamId(examId);
		eq.setPart(2);
		examQuestionDao.save(eq);
		int examQuestionId = eq.getId();
		// save audio file

		String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();

		/*String audioStorePath = absolutePath + "/audio/" + "exam_audio_" + examId + "_" + examQuestionId + ".mp3";
		storeFile(audio, audioStorePath);
		eq.setAudio("exam_audio_" + examId + "_" + examQuestionId + ".mp3");*/
		
		uploadAudio(eq, absolutePath, audio, examQuestionId);

		examQuestionDao.save(eq);
		// save exam question detail
		ExaminationQuestionDetail eqd = new ExaminationQuestionDetail();
		eqd.setNum(1);
		eqd.setNumInExam(currQuestion);
		eqd.setExamId(examId);
		eqd.setQuestion(maps.get("question-content"));
		eqd.setOption1(maps.get("option1"));
		eqd.setOption2(maps.get("option2"));
		eqd.setOption3(maps.get("option3"));
		eqd.setCorrectAnswer(maps.get("radio_question"));
		eqd.setExamQuestionId(examQuestionId);

		examQuestionDetailDao.save(eqd);

		return "redirect:/new-examination/" + partName + "?id=" + examId;
	}

	// save short conversation and short talk question.
	@RequestMapping(value = "/save-examination-short-conversation-and-talk-part/{part}/{num}/{currQuestion}", method = RequestMethod.POST)
	public String saveExaminationShortConversationAndTalkQuestion(@PathVariable("part") String partName,
			@PathVariable("num") int num, @RequestParam("id") int examId, Model model, @PathVariable("currQuestion") int currQuestion,
			@RequestParam("audio_question") MultipartFile audio, @RequestParam Map<String, String> maps) {

		// save examination question
		ExaminationQuestion eq = new ExaminationQuestion();

		eq.setNum(num);
		eq.setSubQuestion(3);
		eq.setExamId(examId);
		eq.setParagraph(maps.get("paragraph"));
		if (partName.equals("Short-conversation"))
			eq.setPart(3);
		else
			eq.setPart(4);

		examQuestionDao.save(eq);
		int examQuestionId = eq.getId();
		// save audio file

		String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();

		/*String audioStorePath = absolutePath + "/audio/" + "exam_audio_" + examId + "_" + examQuestionId + ".mp3";
		storeFile(audio, audioStorePath);
		eq.setAudio("exam_audio_" + examId + "_" + examQuestionId + ".mp3");*/
		
		uploadAudio(eq, absolutePath, audio, examQuestionId);

		examQuestionDao.save(eq);

		// save exam question detail
		for (int i = 1; i <= 3; i++) {
			ExaminationQuestionDetail eqd = new ExaminationQuestionDetail();
			eqd.setNum(i);
			eqd.setNumInExam(currQuestion + (i - 1));
			eqd.setExamId(examId);
			eqd.setQuestion(maps.get("question-content-" + i));
			eqd.setOption1(maps.get("option1_" + i));
			eqd.setOption2(maps.get("option2_" + i));
			eqd.setOption3(maps.get("option3_" + i));
			eqd.setOption4(maps.get("option4_" + i));
			eqd.setCorrectAnswer(maps.get("radio_question_" + i));
			eqd.setExamQuestionId(examQuestionId);

			examQuestionDetailDao.save(eqd);
		}

		return "redirect:/new-examination/" + partName + "?id=" + examId;
	}

	// save incomplete sentence question.
	@RequestMapping(value = "/save-examination-incomplete-sentence-part/{part}/{num}/{currQuestion}", method = RequestMethod.POST)
	public String saveExaminationIncompleteSentenceQuestion(@PathVariable("part") String partName,
			@PathVariable("num") int num, @RequestParam("id") int examId, Model model,
			@RequestParam Map<String, String> maps, @PathVariable("currQuestion") int currQuestion) {

		// save examination question
		ExaminationQuestion eq = new ExaminationQuestion();

		eq.setNum(num);
		eq.setSubQuestion(1);
		eq.setExamId(examId);
		eq.setPart(5);
		examQuestionDao.save(eq);
		int examQuestionId = eq.getId();

		// save exam question detail
		ExaminationQuestionDetail eqd = new ExaminationQuestionDetail();
		eqd.setNum(1);
		eqd.setNumInExam(currQuestion);
		eqd.setExamId(examId);
		eqd.setQuestion(maps.get("question-content"));
		eqd.setOption1(maps.get("option1"));
		eqd.setOption2(maps.get("option2"));
		eqd.setOption3(maps.get("option3"));
		eqd.setOption3(maps.get("option4"));
		eqd.setCorrectAnswer(maps.get("radio_question"));
		eqd.setExamQuestionId(examQuestionId);

		examQuestionDetailDao.save(eqd);

		return "redirect:/new-examination/" + partName + "?id=" + examId;
	}

	// save text complettion question.
	@RequestMapping(value = "/save-examination-text-completion-part/{part}/{num}/{currQuestion}", method = RequestMethod.POST)
	public String saveExaminationTextCompletionQuestion(@PathVariable("part") String partName,
			@PathVariable("num") int num, @RequestParam("id") int examId, Model model,
			@RequestParam Map<String, String> maps, @PathVariable("currQuestion") int currQuestion) {

		// calculate the subquestion. each sub question has 5 name(1 radio and 4
		// option). the paragraph have 1 name.
		int subQuestion = (maps.size() - 1) / 5;

		// save examination question
		ExaminationQuestion eq = new ExaminationQuestion();

		eq.setNum(num);
		eq.setSubQuestion(subQuestion);
		eq.setExamId(examId);
		eq.setPart(6);
		eq.setParagraph(maps.get("paragraph"));
		examQuestionDao.save(eq);
		int examQuestionId = eq.getId();

		// save exam question detail
		for (int i = 1; i <= subQuestion; i++) {
			ExaminationQuestionDetail eqd = new ExaminationQuestionDetail();
			eqd.setNum(i);
			eqd.setNumInExam(currQuestion +(i - 1));
			eqd.setExamId(examId);
			eqd.setOption1(maps.get("option1_" + i));
			eqd.setOption2(maps.get("option2_" + i));
			eqd.setOption3(maps.get("option3_" + i));
			eqd.setOption4(maps.get("option4_" + i));
			eqd.setCorrectAnswer(maps.get("radio_question_" + i));
			eqd.setExamQuestionId(examQuestionId);

			examQuestionDetailDao.save(eqd);
		}

		return "redirect:/new-examination/" + partName + "?id=" + examId;
	}

	// save text complettion question.
	@RequestMapping(value = "/save-examination-single-passage-part/{part}/{num}/{currQuestion}", method = RequestMethod.POST)
	public String saveExaminationSinglePassageQuestion(@PathVariable("part") String partName,
			@PathVariable("num") int num, @RequestParam("id") int examId, Model model,
			@RequestParam Map<String, String> maps, @PathVariable("currQuestion") int currQuestion) {

		// calculate the subquestion. each sub question has 5 name(1 radio and 4
		// option). the paragraph have 1 name.
		int subQuestion = (maps.size() - 1) / 5;

		// save examination question
		ExaminationQuestion eq = new ExaminationQuestion();

		eq.setNum(num);
		eq.setSubQuestion(subQuestion);
		eq.setExamId(examId);
		eq.setPart(7);
		eq.setParagraph(maps.get("paragraph"));
		examQuestionDao.save(eq);
		int examQuestionId = eq.getId();

		// save exam question detail
		for (int i = 1; i <= subQuestion; i++) {
			ExaminationQuestionDetail eqd = new ExaminationQuestionDetail();
			eqd.setNum(i);
			eqd.setNumInExam(currQuestion +(i - 1));
			eqd.setExamId(examId);
			eqd.setQuestion(maps.get("question_content_"+i));
			eqd.setOption1(maps.get("option1_" + i));
			eqd.setOption2(maps.get("option2_" + i));
			eqd.setOption3(maps.get("option3_" + i));
			eqd.setOption4(maps.get("option4_" + i));
			eqd.setCorrectAnswer(maps.get("radio_question_" + i));
			eqd.setExamQuestionId(examQuestionId);

			examQuestionDetailDao.save(eqd);
		}

		return "redirect:/new-examination/" + partName + "?id=" + examId;
	}

	// save text complettion question.
		@RequestMapping(value = "/save-examination-double-passage-part/{part}/{num}/{currQuestion}", method = RequestMethod.POST)
		public String saveExaminationDoublePassageQuestion(@PathVariable("part") String partName,
				@PathVariable("num") int num, @RequestParam("id") int examId, Model model,
				@RequestParam Map<String, String> maps, @PathVariable("currQuestion") int currQuestion) {

			// calculate the subquestion. each sub question has 5 name(1 radio and 4
			// option). the paragraph have 1 name.
			int subQuestion = (maps.size() - 2) / 5;

			// save examination question
			ExaminationQuestion eq = new ExaminationQuestion();

			eq.setNum(num);
			eq.setSubQuestion(subQuestion);
			eq.setExamId(examId);
			eq.setPart(8);
			eq.setParagraph(maps.get("paragraph1"));
			eq.setParagraph2(maps.get("paragraph2"));
			examQuestionDao.save(eq);
			int examQuestionId = eq.getId();

			// save exam question detail
			for (int i = 1; i <= subQuestion; i++) {
				ExaminationQuestionDetail eqd = new ExaminationQuestionDetail();
				eqd.setNum(i);
				eqd.setNumInExam(currQuestion + (i - 1));
				eqd.setExamId(examId);
				eqd.setQuestion(maps.get("question_content_"+i));
				eqd.setOption1(maps.get("option1_" + i));
				eqd.setOption2(maps.get("option2_" + i));
				eqd.setOption3(maps.get("option3_" + i));
				eqd.setOption4(maps.get("option4_" + i));
				eqd.setCorrectAnswer(maps.get("radio_question_" + i));
				eqd.setExamQuestionId(examQuestionId);

				examQuestionDetailDao.save(eqd);
			}

			return "redirect:/new-examination/" + partName + "?id=" + examId;
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

private void uploadAudio(ExaminationQuestion eq, String absolutePath, MultipartFile audio, int  examQuestionId) {
		
		String audioStorePath = absolutePath + "/audio/"+ audio.getOriginalFilename();
		
		Path pathAudio = Paths.get(audioStorePath);
		String fileAudioName = storageService.store(pathAudio, audio);

		S3Service audio_s3 = new S3Service();

		String pathAudioFile = absolutePath + "/audio/"+ File.separator + fileAudioName;
		System.out.println(pathAudioFile);
		String audio_url = audio_s3.uploadS3(pathAudioFile, "exam", "audio", examQuestionId);
		eq.setAudio(audio_url);
	}
	private void uploadPhoto (ExaminationQuestion eq, String absolutePath, MultipartFile photo, int  examQuestionId) {
		String photoStorePath = absolutePath + "/photo/"+ photo.getOriginalFilename();
		
		Path pathPhoto = Paths.get(photoStorePath);
		String filePhotoName = storageService.store(pathPhoto, photo);

		S3Service photo_s3 = new S3Service();
		
		String pathPhotoFile = absolutePath + "/photo/"+ File.separator + filePhotoName;
		System.out.println(pathPhotoFile);			

		String photo_url = photo_s3.uploadS3(pathPhotoFile, "exam","photo", examQuestionId);
		eq.setPhoto(photo_url);
	}
}
