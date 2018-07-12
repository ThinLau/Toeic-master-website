package com.toeicmaster.springmvc.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.util.IOUtils;
import com.toeicmaster.springmvc.dao.ExaminationDao;
import com.toeicmaster.springmvc.dao.ExaminationQuestionDao;
import com.toeicmaster.springmvc.dao.ExaminationQuestionDetailDao;
import com.toeicmaster.springmvc.model.Examination;
import com.toeicmaster.springmvc.model.ExaminationQuestion;
import com.toeicmaster.springmvc.model.ExaminationQuestionDetail;
import com.toeicmaster.springmvc.model.Exercise;
import com.toeicmaster.springmvc.model.ExerciseQuestion;
import com.toeicmaster.springmvc.model.ExerciseQuestionDetail;
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
	private int examNumberOfQuestion = 0;
	int sameExam = 0;
	String iputOrfile = "input";

	@RequestMapping(value = "/new-examination-page", method = RequestMethod.GET)
	public String newExercisePage(HttpSession session, Model model) {
		if (session.getAttribute("user") == null)
			return "login/login";

		User user = (User) session.getAttribute("user");

		Examination exam = new Examination();
		model.addAttribute("exam", exam);
		model.addAttribute("user", user);
		model.addAttribute("module", "new-examination");
		
		if (iputOrfile == "input")
			model.addAttribute( "exam_module","create-exam-input");
		else
			model.addAttribute( "exam_module","create-exam-file");
		model.addAttribute("sameExam",sameExam);
		sameExam = 0;
		iputOrfile = "input";
		return "user/examination/create_new_examination";
	}

	@RequestMapping(value = "/save-examination", method = RequestMethod.POST)
	public String saveExercise(HttpSession session, Model model, @ModelAttribute("exam") Examination exam) {
		if (session.getAttribute("user") == null)
			return "login/login";
		
		String return_page = "";
		if(examDao.findName(exam.getName()).size() >= 1) {
			sameExam = 1;			
			model.addAttribute("sameExam", sameExam);
			return_page = "redirect:/new-examination-page";	
		} else {
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();

		String dateCreate = dateFormat.format(date);

		exam.setDateCreate(dateCreate);

		User user = (User) session.getAttribute("user");
		exam.setAuthor(user.getId());

		examDao.save(exam);

		sameExam = 0;
		model.addAttribute("exam", exam);	
		model.addAttribute("sameExam",sameExam);
		return_page =  "redirect:/new-examination/info?id=" + exam.getId();
		}
		iputOrfile = "input";
		return return_page;
	}
	
	@RequestMapping(value = "/new-examination/info", method = RequestMethod.GET)
	public String exerciseInfo(HttpSession session, @RequestParam("id") int examId, Model model) {
		if (session.getAttribute("user") == null)
			return "login/login";

		Examination exam = examDao.findById(examId);
		model.addAttribute("exam", exam);
		return "user/examination/examination_info";
	}

	@RequestMapping(value = "/update-examination", method = RequestMethod.POST)
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

		Examination exam = examDao.findById(examId);
		model.addAttribute("exam", exam);
		model.addAttribute("currQuestion", currQuestion);
		model.addAttribute("num", num);
		return result;
	}

	// save photo question.
	@RequestMapping(value = "/save-examination-photo-part/{part}/{num}/{currQuestion}", method = RequestMethod.POST)
	public String saveExaminationPhotoQuestion(HttpServletRequest request,@PathVariable("part") String partName, @PathVariable("num") int num,
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
		
		uploadfileS3(request, eq, absolutePath, audio, photo, examQuestionId);				

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
						
		Examination exam = examDao.findById(examId);	
		examNumberOfQuestion++;
		exam.setNumberOfQuestion(examNumberOfQuestion);
		examDao.save(exam);
		
		return "redirect:/new-examination/" + partName + "?id=" + examId;
	}

	// save question response question.
	@RequestMapping(value = "/save-examination-question-response-part/{part}/{num}/{currQuestion}", method = RequestMethod.POST)
	public String saveExaminationQuestionResponseQuestion(HttpServletRequest request, @PathVariable("currQuestion") int currQuestion, 
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
		
		uploadfileS3(request, eq, absolutePath, audio, null, examQuestionId);

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
		
		Examination exam = examDao.findById(examId);
		examNumberOfQuestion++;
		exam.setNumberOfQuestion(examNumberOfQuestion);
		examDao.save(exam);

		return "redirect:/new-examination/" + partName + "?id=" + examId;
	}

	// save short conversation and short talk question.
	@RequestMapping(value = "/save-examination-short-conversation-and-talk-part/{part}/{num}/{currQuestion}", method = RequestMethod.POST)
	public String saveExaminationShortConversationAndTalkQuestion(HttpServletRequest request, @PathVariable("part") String partName,
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
		
		uploadfileS3(request, eq, absolutePath, audio, null, examQuestionId);

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
		
		Examination exam = examDao.findById(examId);
		examNumberOfQuestion = examNumberOfQuestion + 3;
		exam.setNumberOfQuestion(examNumberOfQuestion);
		examDao.save(exam);

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
		
		Examination exam = examDao.findById(examId);
		examNumberOfQuestion++;
		exam.setNumberOfQuestion(examNumberOfQuestion);
		examDao.save(exam);

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

		Examination exam = examDao.findById(examId);
		examNumberOfQuestion = examNumberOfQuestion + subQuestion;
		exam.setNumberOfQuestion(examNumberOfQuestion);
		examDao.save(exam);
		
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

		Examination exam = examDao.findById(examId);
		examNumberOfQuestion = examNumberOfQuestion + subQuestion; 
		exam.setNumberOfQuestion(examNumberOfQuestion);
		examDao.save(exam);
		
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

			Examination exam = examDao.findById(examId);
			examNumberOfQuestion = examNumberOfQuestion + subQuestion; 
			exam.setNumberOfQuestion(examNumberOfQuestion);
			examDao.save(exam);
			
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

	private void uploadfileS3(HttpServletRequest request, ExaminationQuestion eq, String absolutePath, MultipartFile audio, MultipartFile photo, int  examQuestionId) {
		
		S3Service file_s3 = new S3Service();
		
		String audio_url = file_s3.uploadS3(request, audio, "exam", "audio", examQuestionId);
		eq.setAudio(audio_url);
		
		if (photo != null) {
			String photo_url = file_s3.uploadS3(request, photo, "exam","photo", examQuestionId);
			eq.setPhoto(photo_url);
		}		
	}
	
	
	
	@RequestMapping(value="/save-exam-file", method=RequestMethod.POST)
	public String saveExerciseFile(HttpSession session , Model model, 
			@RequestParam("file") MultipartFile file) {
		if(session.getAttribute("user") == null)
			return "login/login";
		Examination exam = new Examination();
		ExaminationQuestion eq = new ExaminationQuestion();
		ExaminationQuestionDetail eqd = new ExaminationQuestionDetail();
		User user = (User) session.getAttribute("user");
		int temp = 0;	
		int num = 0;
		String return_page = "redirect:/new-examination-page";
				
		try {
			Workbook workbook = null;
			try {
				workbook = WorkbookFactory.create(file.getInputStream());
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Sheet worksheet = workbook.getSheetAt(0);	
			
			Row row; 
			row = (Row) worksheet.getRow(1);		
			
						
			if (row == null || examDao.findName(row.getCell(1).toString()).size() >=1 || row.getCell(1).toString() == "") { 
				sameExam = 1;	
				model.addAttribute("sameExam", sameExam);
				return_page = "redirect:/new-examination-page";										
			} else if (row.getCell(2).toString() == "") { 
					sameExam = 2;	
					model.addAttribute("sameExam", sameExam);
					return_page = "redirect:/new-examination-page";												
				} else if (row.getCell(3).toString() == "") { 
						sameExam = 3;	
						model.addAttribute("sameExam", sameExam);
						return_page = "redirect:/new-examination-page";														
					} else {													
						exam.setName(row.getCell(1).toString());									
						DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
						Date date = new Date();
						String dateCreate = dateFormat.format(date);
						exam.setDateCreate(dateCreate);			
						exam.setNumberOfQuestion((int) Double.parseDouble(row.getCell(3).toString()));
						int timeOut = (int) Double.parseDouble(row.getCell(2).toString());
						exam.setTimeOut(timeOut);		
						exam.setAuthor(user.getId());
						examDao.save(exam);											
						
						int num_in_exam = 0;
						int numsmallQues = 0;
			//			String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
						
						//Part 1
						
						for(int i=3; i< 3+10; i++) {
							num_in_exam++;
							eq = new ExaminationQuestion();
							eq.setExamId(exam.getId());
							eq.setSubQuestion(1);
							eq.setNum(i-2);			
							eq.setPart(1);				
							examQuestionDao.save(eq);
							
							row = (Row) worksheet.getRow(i);
							/*
							if( row.getCell(1)==null) { eq.setPhoto(null);}  //suppose excel cell is empty then its set to 0 the variable
			                else {
			             	   File fi = new File(row.getCell(1).toString());
			             	   FileInputStream input = new FileInputStream(fi);
			             	   MultipartFile photoFile = new MockMultipartFile(fi.getName(),fi.getName(), "text/plain", IOUtils.toByteArray(input));                	                   	   
			
			             	   uploadPhoto(eq, absolutePath, photoFile, eq.getId());
			                }	
							
							if( row.getCell(2)==null) { eq.setAudio(null);}  //suppose excel cell is empty then its set to 0 the variable
			                   else {           
			                	   File fi = new File(row.getCell(2).toString());
			                	   System.out.println(fi.getName());
			                	   
			                	   FileInputStream input = new FileInputStream(fi);
			                	   MultipartFile audioFile = new MockMultipartFile(fi.getName(),fi.getName(), "text/plain", IOUtils.toByteArray(input));                	                   	   
			                	   
			                	   System.out.println(audioFile.getOriginalFilename());
			                	   uploadAudio(eq, absolutePath, audioFile, eq.getId());
			                   }		
							
							examQuestionDao.save(eq);*/
							
							eqd = new ExaminationQuestionDetail();	
							temp++;
							
							String option_1;
							if( row.getCell(5)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_1 = row.getCell(5).toString();
							
							String option_2;
							if( row.getCell(6)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_2 = row.getCell(6).toString();
							
							String option_3;
							if( row.getCell(7)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_3 = row.getCell(7).toString();
							
							String option_4;
							if( row.getCell(8)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_4 = row.getCell(8).toString();
							
							String correct_answer;
							if( row.getCell(9)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else correct_answer = row.getCell(9).toString();
								
							eqd.setNum(temp);
							eqd.setNumInExam(num_in_exam);
							eqd.setOption1(option_1);
							eqd.setOption2(option_2);
							eqd.setOption3(option_3);
							eqd.setOption4(option_4);
							eqd.setCorrectAnswer(correct_answer);
							eqd.setExamQuestionId(eq.getId());
							eqd.setExamId(exam.getId());
							examQuestionDetailDao.save(eqd);					
						}
						//------------------//------------------------//----------------
						// Part 2
						temp = 0;
						num = 0;
						for (int i = 13; i < (13 + 30); i++) {
							num_in_exam++;
							num++;
							eq = new ExaminationQuestion();				
							eq.setExamId(exam.getId());
							eq.setSubQuestion(1);
							eq.setNum(num);				
							eq.setPart(2);
							examQuestionDao.save(eq);
							row = (Row) worksheet.getRow(i);
								
						
							eqd = new ExaminationQuestionDetail();	
							temp++;
							
							String option_1;
							if( row.getCell(5)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_1 = row.getCell(5).toString();
							
							String option_2;
							if( row.getCell(6)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_2 = row.getCell(6).toString();
							
							String option_3;
							if( row.getCell(7)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_3 = row.getCell(7).toString();												
							
							String correct_answer;
							if( row.getCell(9)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else correct_answer = row.getCell(9).toString();
								
							eqd.setNum(temp);
							eqd.setNumInExam(num_in_exam);
							eqd.setOption1(option_1);
							eqd.setOption2(option_2);
							eqd.setOption3(option_3);						
							eqd.setCorrectAnswer(correct_answer);
							eqd.setExamQuestionId(eq.getId());
							eq.setExamId(exam.getId());
							examQuestionDetailDao.save(eqd);
						}
						//------------------------------//--------------------------------------
						// Part 3
											
						num = 0;
						for (int i = 43; i < 43+30; i=i+3)
						{
							row = (Row) worksheet.getRow(i);
							temp = 0;
							num++;
							eq = new ExaminationQuestion();
							eq.setExamId(exam.getId());				
							eq.setNum(num);	
							eq.setSubQuestion(3);
							eq.setParagraph(row.getCell(3).toString());		
							eq.setPart(3);
							examQuestionDao.save(eq);
							
							
							for(int j= i; j< i+3; j++) {		
								num_in_exam++;
								
								row = (Row) worksheet.getRow(j);
								eqd = new ExaminationQuestionDetail();	
								temp++;
								
								String question;
								if( row.getCell(4)==null) { question = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else question = row.getCell(4).toString();
								
								String option_1;
								if( row.getCell(5)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_1 = row.getCell(5).toString();
								
								String option_2;
								if( row.getCell(6)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_2 = row.getCell(6).toString();
								
								String option_3;
								if( row.getCell(7)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_3 = row.getCell(7).toString();
								
								String option_4;
								if( row.getCell(8)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_4 = row.getCell(8).toString();
								
								String correct_answer;
								if( row.getCell(9)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else correct_answer = row.getCell(9).toString();
									
								eqd.setNum(temp);
								eqd.setNumInExam(num_in_exam);
								eqd.setQuestion(question);
								eqd.setOption1(option_1);
								eqd.setOption2(option_2);
								eqd.setOption3(option_3);
								eqd.setOption4(option_4);
								eqd.setCorrectAnswer(correct_answer);
								eqd.setExamQuestionId(eq.getId());
								eqd.setExamId(exam.getId());
								examQuestionDetailDao.save(eqd);
							}			
						}
						//----------------------------//---------------------------
						//Part 4
						
						num = 0;
						for (int i = 73; i < 73+30; i=i+3)
						{
							row = (Row) worksheet.getRow(i);
							temp = 0;
							num++;
							eq = new ExaminationQuestion();
							eq.setExamId(exam.getId());				
							eq.setNum(num);	
							eq.setSubQuestion(3);
							eq.setParagraph(row.getCell(3).toString());		
							eq.setPart(4);
							examQuestionDao.save(eq);
							
											
							for(int j= i; j< i+3; j++) {		
								num_in_exam++;
								
								row = (Row) worksheet.getRow(j);
								eqd = new ExaminationQuestionDetail();	
								temp++;
								
								String question;
								if( row.getCell(4)==null) { question = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else question = row.getCell(4).toString();
								
								String option_1;
								if( row.getCell(5)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_1 = row.getCell(5).toString();
								
								String option_2;
								if( row.getCell(6)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_2 = row.getCell(6).toString();
								
								String option_3;
								if( row.getCell(7)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_3 = row.getCell(7).toString();
								
								String option_4;
								if( row.getCell(8)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_4 = row.getCell(8).toString();
								
								String correct_answer;
								if( row.getCell(9)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else correct_answer = row.getCell(9).toString();
									
								eqd.setNum(temp);
								eqd.setNumInExam(num_in_exam);
								eqd.setQuestion(question);
								eqd.setOption1(option_1);
								eqd.setOption2(option_2);
								eqd.setOption3(option_3);
								eqd.setOption4(option_4);
								eqd.setCorrectAnswer(correct_answer);
								eqd.setExamQuestionId(eq.getId());
								eqd.setExamId(exam.getId());
								examQuestionDetailDao.save(eqd);
							}			
						}
						//-------------------------------------//----------------------------------------
						//Part5
						
						num = 0;
						for(int i = 103; i < (103+40) ; i++) {
							row = (Row) worksheet.getRow(i);
							temp = 0;
							num++;
							eq = new ExaminationQuestion();
							eq.setExamId(exam.getId());				
							eq.setNum(num);	
							eq.setSubQuestion(1);				
							eq.setPart(5);
							examQuestionDao.save(eq);
							
							eqd = new ExaminationQuestionDetail();	
							temp++;
							num_in_exam++;
							
							String question;
							if( row.getCell(4)==null) { question = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else question = row.getCell(4).toString();
							
							String option_1;
							if( row.getCell(5)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_1 = row.getCell(5).toString();
							
							String option_2;
							if( row.getCell(6)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_2 = row.getCell(6).toString();
							
							String option_3;
							if( row.getCell(7)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_3 = row.getCell(7).toString();
							
							String option_4;
							if( row.getCell(8)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_4 = row.getCell(8).toString();
							
							String correct_answer;
							if( row.getCell(9)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else correct_answer = row.getCell(9).toString();
								
							eqd.setNum(temp);
							eqd.setNumInExam(num_in_exam);
							eqd.setQuestion(question);
							eqd.setOption1(option_1);
							eqd.setOption2(option_2);
							eqd.setOption3(option_3);
							eqd.setOption4(option_4);
							eqd.setCorrectAnswer(correct_answer);
							eqd.setExamId(exam.getId());
							eqd.setExamQuestionId(eq.getId());
							examQuestionDetailDao.save(eqd);
						}
						//--------------------------//-------------------------------------------
						//Part 6
									
						num = 0;
						numsmallQues = 0;
						for (int i = 143; i < (143 + 12); i = (i + numsmallQues))
						{					
							row = (Row) worksheet.getRow(i);
							
							numsmallQues = (int) Double.parseDouble(row.getCell(3).toString());	 	// so cau hoi nho
							temp = 0;
							num++;
							eq = new ExaminationQuestion();
							eq.setExamId(exam.getId());				
							eq.setNum(num);	
							eq.setSubQuestion(numsmallQues);
							eq.setParagraph(row.getCell(1).toString());
							eq.setPart(6);
							examQuestionDao.save(eq);
																		
							for(int j= i; j< i + numsmallQues; j++) {													
								row = (Row) worksheet.getRow(j);
								eqd = new ExaminationQuestionDetail();	
								temp++;	
								num_in_exam++;
													
								String option_1;
								if( row.getCell(5)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_1 = row.getCell(5).toString();
								
								String option_2;
								if( row.getCell(6)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_2 = row.getCell(6).toString();
								
								String option_3;
								if( row.getCell(7)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_3 = row.getCell(7).toString();
								
								String option_4;
								if( row.getCell(8)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_4 = row.getCell(8).toString();
								
								String correct_answer;
								if( row.getCell(9)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else correct_answer = row.getCell(9).toString();
									
								eqd.setNum(temp);
								eqd.setNumInExam(num_in_exam);
								eqd.setOption1(option_1);
								eqd.setOption2(option_2);
								eqd.setOption3(option_3);
								eqd.setOption4(option_4);
								eqd.setCorrectAnswer(correct_answer);
								eqd.setExamQuestionId(eq.getId());
								eqd.setExamId(exam.getId());
								examQuestionDetailDao.save(eqd);
							}				
						}
						//-----------------------------------//------------------------------
						//Part7
						
						num = 0;
						numsmallQues = 0;
						for (int i = 155; i < (155 + 28); i= (i + numsmallQues))
						{					
							row = (Row) worksheet.getRow(i);
							// so cau hoi nho
							numsmallQues = (int) Double.parseDouble(row.getCell(3).toString());	
							temp = 0;
							num++;
							eq = new ExaminationQuestion();
							eq.setExamId(exam.getId());				
							eq.setNum(num);	
							eq.setSubQuestion(numsmallQues);
							eq.setParagraph(row.getCell(1).toString());
							eq.setPart(7);
							examQuestionDao.save(eq);
																		
							for(int j= i; j< (i + numsmallQues); j++) {													
								row = (Row) worksheet.getRow(j);
								eqd = new ExaminationQuestionDetail();	
								temp++;
								num_in_exam++;
								
								String question;
								if( row.getCell(4)==null) { question = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else question = row.getCell(4).toString();
								
								String option_1;
								if( row.getCell(5)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_1 = row.getCell(5).toString();
								
								String option_2;
								if( row.getCell(6)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_2 = row.getCell(6).toString();
								
								String option_3;
								if( row.getCell(7)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_3 = row.getCell(7).toString();
								
								String option_4;
								if( row.getCell(8)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_4 = row.getCell(8).toString();
								
								String correct_answer;
								if( row.getCell(9)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else correct_answer = row.getCell(9).toString();
									
								eqd.setNum(temp);
								eqd.setNumInExam(num_in_exam);
								eqd.setQuestion(question);
								eqd.setOption1(option_1);
								eqd.setOption2(option_2);
								eqd.setOption3(option_3);
								eqd.setOption4(option_4);
								eqd.setCorrectAnswer(correct_answer);
								eqd.setExamQuestionId(eq.getId());
								eqd.setExamId(exam.getId());
								examQuestionDetailDao.save(eqd);
							}			
						}
						//-------------------------------------//-----------------------
						//Part8
						
						numsmallQues = 0;
						num = 0;
						for (int i = 183; i < (183 + 20); i = i+ numsmallQues)
						{					
							row = (Row) worksheet.getRow(i);
							// so cau hoi nho
							numsmallQues = (int) Double.parseDouble(row.getCell(3).toString());	
							temp = 0;
							num++;
							eq = new ExaminationQuestion();
							eq.setExamId(exam.getId());				
							eq.setNum(num);	
							eq.setSubQuestion(numsmallQues);
							eq.setParagraph(row.getCell(1).toString());
							eq.setParagraph2(row.getCell(2).toString());
							eq.setPart(8);
							examQuestionDao.save(eq);
																		
							for(int j= i; j< (i + numsmallQues); j++) {													
								row = (Row) worksheet.getRow(j);
								eqd = new ExaminationQuestionDetail();	
								temp++;
								
								String question;
								if( row.getCell(4)==null) { question = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else question = row.getCell(4).toString();
								
								String option_1;
								if( row.getCell(5)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_1 = row.getCell(5).toString();
								
								String option_2;
								if( row.getCell(6)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_2 = row.getCell(6).toString();
								
								String option_3;
								if( row.getCell(7)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_3 = row.getCell(7).toString();
								
								String option_4;
								if( row.getCell(8)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else option_4 = row.getCell(8).toString();
								
								String correct_answer;
								if( row.getCell(9)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
					                else correct_answer = row.getCell(9).toString();
									
								eqd.setNum(temp);
								eqd.setNumInExam(num_in_exam);
								eqd.setQuestion(question);
								eqd.setOption1(option_1);
								eqd.setOption2(option_2);
								eqd.setOption3(option_3);
								eqd.setOption4(option_4);
								eqd.setCorrectAnswer(correct_answer);
								eqd.setExamQuestionId(eq.getId());
								eqd.setExamId(exam.getId());
								examQuestionDetailDao.save(eqd);
							}
						}	
						return_page = "redirect:/update-examination/1?num=" + 1 + "&id=" + exam.getId();
					}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iputOrfile = "file";		
		model.addAttribute("user", user);
		return return_page;
	}
}
