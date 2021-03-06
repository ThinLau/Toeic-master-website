package com.toeicmaster.springmvc.controller;

import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.Request;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.util.IOUtils;
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
	
	int sameExer = 0;
	String inputOrfile = "input";

	String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();

	@Autowired
	public void FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	// user call create exercise page
	@RequestMapping(value = "/new-exercise-page", method = RequestMethod.GET)
	public String newExercisePage(HttpSession session, Model model) {
		if (session.getAttribute("user") == null)
			return "login/login";
		User user = (User) session.getAttribute("user");

		Exercise exercise = new Exercise();
		model.addAttribute("exercise", exercise);
		model.addAttribute("user", user);
		model.addAttribute("module", "new-exercise");
		
		if(inputOrfile == "input")
			model.addAttribute("exercise_module", "create-exer-input");
		else
			model.addAttribute("exercise_module", "create-exer-file");
		model.addAttribute("sameExer", sameExer);
		sameExer = 0;	
		inputOrfile = "input";
		return "user/exercise/create_new_exercise";
	}

	// save the exercise

	@RequestMapping(value = "/save-exercise", method = RequestMethod.POST)
	public String saveExercise(HttpSession session, Model model, @ModelAttribute("exercise") Exercise exercise) {
		if (session.getAttribute("user") == null)
			return "login/login";

		String return_page = "";
		if(exerciseDao.findName(exercise.getName()).size() >= 1) {
			sameExer = 1;			
			model.addAttribute("sameExer", sameExer);
			return_page = "redirect:/new-exercise-page";
		} else {
				
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date date = new Date();
	
			String dateCreate = dateFormat.format(date);
	
			exercise.setDateCreate(dateCreate);
	
			User user = (User) session.getAttribute("user");
			exercise.setAuthor(user.getId());			
			
			exerciseDao.save(exercise);
			
			sameExer = 0;
			inputOrfile = "input";
			model.addAttribute("sameExer", sameExer);
			model.addAttribute("exercise", exercise);
			return_page = "redirect:/new-exercise/info?id=" + exercise.getId();
		}
		return return_page;
	}

	@RequestMapping(value = "/save-exercise-file", method = RequestMethod.POST)
	public String saveExerciseFile(HttpServletRequest request, HttpSession session, Model model,
			@RequestParam("file") MultipartFile file, @RequestParam("part") String part,  @RequestParam("level") int level) {
		if (session.getAttribute("user") == null)
			return "login/login";
		Exercise exercise = new Exercise();
		ExerciseQuestion eq = new ExerciseQuestion();
		ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
		User user = (User) session.getAttribute("user");
		String return_page = "redirect:/new-exercise-page";
		int result = 0;
		try {
			Workbook workbook = null;
			try {
				workbook = WorkbookFactory.create(file.getInputStream());
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Sheet worksheet = workbook.getSheetAt(0);

			// XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
			// XSSFSheet worksheet = workbook.getSheet(sheetname);

			
			int ii = (int) Double.parseDouble(part);
			switch (ii) {
			case 1: // photo
			{
				result = part1(request, exercise, eq, eqd, user, worksheet, part, level);
				
				break;
			}
			case 2: // question-response
			{
				result = part2(request, exercise, eq, eqd, user, worksheet, part, level );				
				break;
			}
			case 3: // short converstion
			{
				result = part3(request, exercise, eq, eqd, user, worksheet, part, level);
				break;
			}
			case 4: // short talk
			{
				result = part4(request, exercise, eq, eqd, user, worksheet, part, level);
				break;
			}
			case 5: // incomplete sentence
			{
				result = part5(exercise, eq, eqd, user, worksheet, part, level);
				break;
			}
			case 6: // text completion
			{
				result = part6(exercise, eq, eqd, user, worksheet, part, level);
				break;
			}
			case 7: // single passage
			{
				result = part7(exercise, eq, eqd, user, worksheet, part, level);
				break;
			}
			case 8: // double passage
			{
				result = part8(exercise, eq, eqd, user, worksheet, part, level);
				break;
			}
			}
			workbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(result == 1) {
			sameExer = 1;	
			model.addAttribute("sameExer", sameExer);
			return_page = "redirect:/new-exercise-page";
		} else if(result == 2) {
			sameExer = 2;	
			model.addAttribute("sameExer", sameExer);
			} else {
				return_page = "redirect:/update-exercise/question?id=" + exercise.getId() + "&num=" + 1;
			}
		inputOrfile = "file";
		model.addAttribute("user", user);
		return return_page;
	} 

	@RequestMapping(value = "/update-exercise", method = RequestMethod.POST)
	public String upadateExerciseInfo(HttpSession session, Model model, @ModelAttribute("exercise") Exercise exercise) {

		Exercise entity = exerciseDao.findById(exercise.getId());
		int exerciseId = exercise.getId();
		if (entity != null) {
			entity.setName(exercise.getName());
			entity.setNumberOfQuestion(exercise.getNumberOfQuestion());
			entity.setPart(exercise.getPart());
			entity.setLevel(exercise.getLevel());
			exerciseDao.save(entity);
			exerciseId = entity.getId();
		}

		model.addAttribute("exercise", exercise);
		return "redirect:/new-exercise/info?id=" + exerciseId;
	}

	@RequestMapping(value = "/new-exercise/info", method = RequestMethod.GET)
	public String exerciseInfo(HttpSession session, @RequestParam("id") int exerciseId, Model model) {
		if (session.getAttribute("user") == null)
			return "login/login";

		Exercise exercise = exerciseDao.findById(exerciseId);
		model.addAttribute("module", "exercise-info");
		model.addAttribute("exercise", exercise);
		return "user/exercise/exercise_info";
	}

	@RequestMapping(value = "/exercise-question", method = RequestMethod.GET)
	public String exerciseQuestion(HttpSession session, @RequestParam("id") int exerciseId, Model model) {

		return "redirect:/new-exercise/question?id=" + exerciseId + "&num=1";
	}

	@RequestMapping(value = "/new-exercise/question", method = RequestMethod.GET)
	private String returnQuestionPage(@RequestParam("id") int exerciseId, @RequestParam("num") int num, Model model) {
		Exercise exercise = exerciseDao.findById(exerciseId);
		String viewPage = "error_page";
		int part = exercise.getPart();

		switch (part) {
		case 1: // photo
			viewPage = "user/exercise/photo_question";
			break;
		case 2: // question-response
			viewPage = "user/exercise/question_response_question";
			break;
		case 3: // short converstion
			viewPage = "user/exercise/short_conversation_and_talk_question";
			break;
		case 4: // short talk
			viewPage = "user/exercise/short_conversation_and_talk_question";
			break;
		case 5: // incomplete sentence
			viewPage = "user/exercise/incomplete_sentence_question";
			break;
		case 6: // text completion
			viewPage = "user/exercise/text_completion_question";
			break;
		case 7: // single passage
			viewPage = "user/exercise/single_passage_question";
			break;
		case 8: // double passage
			viewPage = "user/exercise/double_passage_question";
			break;
		}

		model.addAttribute("exercise", exercise);
		model.addAttribute("num", num);
		model.addAttribute("module", "exercise-question");
		return viewPage;
	}
	// create question for each part

	@RequestMapping(value = "/save-photo-question/{exerciseId}/{num}", method = RequestMethod.POST)
	public String saveQuestion(HttpSession session, HttpServletRequest request, Model model,
			@PathVariable("exerciseId") int exerciseId, @RequestParam("image_question") MultipartFile photo,
			@RequestParam("audio_question") MultipartFile audio, @RequestParam("radio_question") String radio,
			@RequestParam("option1") String option1, @RequestParam("option2") String option2,
			@RequestParam("option3") String option3, @PathVariable("num") int num,
			@RequestParam("option4") String option4) {
		if (session.getAttribute("user") == null)
			return "login/login";

		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		// set num

		eq.setNum(num);
		eq.setSubQuestion(1);
		eq.setExerciseId(exerciseId);
		exerciseQuestionDao.save(eq);
		int exerciseQuestionId = eq.getId();
		// String absolutePath = new
		// File("src/main/resources/static/upload").getAbsolutePath();

		// save audio file
		uploadfileS3(request, eq, audio, photo, exerciseQuestionId);

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

		/*
		 * try { File Pfile = convertMultiPartToFile(photo); File Afile =
		 * convertMultiPartToFile(audio); Afile.delete(); Pfile.delete(); } catch
		 * (IOException e) { e.printStackTrace(); }
		 */

		Exercise exercise = exerciseDao.findById(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);

		return "redirect:/new-exercise/question?id=" + exerciseId + "&num=" + (num + 1);
	}

	@RequestMapping(value = "/save-question-response-question/{exerciseId}/{num}", method = RequestMethod.POST)
	public String saveQuestionResponseQuestion(HttpServletRequest request, Model model,
			@PathVariable("exerciseId") int exerciseId, @PathVariable("num") int num,
			@RequestParam("audio_question") MultipartFile audio, @RequestParam("radio_question") String radio,
			@RequestParam("option1") String option1, @RequestParam("option2") String option2,
			@RequestParam("option3") String option3, @RequestParam("question-content") String questionContent) {

		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		// set num

		eq.setNum(num);
		eq.setSubQuestion(1);
		eq.setExerciseId(exerciseId);
		exerciseQuestionDao.save(eq); // save first then get id of this question
		int exerciseQuestionId = eq.getId();
		// save audio file

		uploadfileS3(request, eq, audio, null, exerciseQuestionId);

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
		Exercise exercise = exerciseDao.findById(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);

		return "redirect:/new-exercise/question?id=" + exerciseId + "&num=" + (num + 1);
	}

	@RequestMapping(value = "/save-short-conversation-talk-question/{exerciseId}/{num}", method = RequestMethod.POST)
	public String saveShortConversationAndTalkQuestion(HttpServletRequest request, Model model,
			@PathVariable("exerciseId") int exerciseId, @PathVariable("num") int num,
			@RequestParam("audio_question") MultipartFile audio, @RequestParam Map<String, String> maps) {

		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		// set num

		eq.setNum(num);
		eq.setSubQuestion(3); // each quesion in this part consist of 3 sub question
		eq.setExerciseId(exerciseId);
		eq.setParagraph(maps.get("paragraph"));
		exerciseQuestionDao.save(eq); // save first then get id of this question
		int exerciseQuestionId = eq.getId();
		// save audio file

		uploadfileS3(request, eq, audio, null, exerciseQuestionId);
		exerciseQuestionDao.save(eq);

		// save exercise question detail
		for (int i = 1; i <= 3; i++) {
			ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
			eqd.setNum(i);
			eqd.setQuestion(maps.get("question-content-" + i));
			eqd.setOption1(maps.get("option1_" + i));
			eqd.setOption2(maps.get("option2_" + i));
			eqd.setOption3(maps.get("option3_" + i));
			eqd.setOption4(maps.get("option4_" + i));
			eqd.setCorrectAnswer(maps.get("radio_question_" + i));
			eqd.setExerciseQuestionId(exerciseQuestionId);

			exerciseQuestionDetailDao.save(eqd);
		}

		// upadate number of question of exercise.
		Exercise exercise = exerciseDao.findById(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);

		return "redirect:/new-exercise/question?id=" + exerciseId + "&num=" + (num + 1);
	}

	@RequestMapping(value = "/save-incomplete-sentence-question/{exerciseId}/{num}", method = RequestMethod.POST)
	public String saveIncompleteSentenceQuestion(Model model, @PathVariable("exerciseId") int exerciseId,
			@PathVariable("num") int num, @RequestParam Map<String, String> maps) {
		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();

		eq.setNum(num);
		eq.setSubQuestion(1);
		eq.setExerciseId(exerciseId);
		exerciseQuestionDao.save(eq); // save first then get id of this question
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
		Exercise exercise = exerciseDao.findById(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);

		return "redirect:/new-exercise/question?id=" + exerciseId + "&num=" + (num + 1);
	}

	@RequestMapping(value = "/save-text-completion-question/{exerciseId}/{num}", method = RequestMethod.POST)
	public String saveTextCompletionQuestion(Model model, @PathVariable("exerciseId") int exerciseId,
			@PathVariable("num") int num, @RequestParam Map<String, String> maps) {

		// calculate the subquestion. each sub question has 5 name(1 radio and 4
		// option). the paragraph have 1 name.
		int subQuestion = (maps.size() - 1) / 5;

		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		// set num

		eq.setNum(num);
		eq.setSubQuestion(subQuestion);
		eq.setExerciseId(exerciseId);
		eq.setParagraph(maps.get("paragraph"));
		exerciseQuestionDao.save(eq); // save first then get id of this question
		int exerciseQuestionId = eq.getId();

		// save exercise question detail
		for (int i = 1; i <= subQuestion; i++) {
			ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
			eqd.setNum(i);
			eqd.setOption1(maps.get("option1_" + i));
			eqd.setOption2(maps.get("option2_" + i));
			eqd.setOption3(maps.get("option3_" + i));
			eqd.setOption4(maps.get("option4_" + i));
			eqd.setCorrectAnswer(maps.get("radio_question_" + i));
			eqd.setExerciseQuestionId(exerciseQuestionId);

			exerciseQuestionDetailDao.save(eqd);
		}

		// upadate number of question of exercise.
		Exercise exercise = exerciseDao.findById(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);

		return "redirect:/new-exercise/question?id=" + exerciseId + "&num=" + (num + 1);
	}

	@RequestMapping(value = "/save-single-passage-question/{exerciseId}/{num}", method = RequestMethod.POST)
	public String saveSinglePassageQuestion(Model model, @PathVariable("exerciseId") int exerciseId,
			@PathVariable("num") int num, @RequestParam Map<String, String> maps) {

		// calculate the subquestion. each sub question has 5 name(1 radio and 4
		// option). the paragraph have 1 name.
		int subQuestion = (maps.size() - 1) / 5;

		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		// set num

		eq.setNum(num);
		eq.setSubQuestion(subQuestion);
		eq.setExerciseId(exerciseId);
		eq.setParagraph(maps.get("paragraph"));
		exerciseQuestionDao.save(eq); // save first then get id of this question
		int exerciseQuestionId = eq.getId();

		// save exercise question detail
		for (int i = 1; i <= subQuestion; i++) {
			ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
			eqd.setNum(i);
			eqd.setOption1(maps.get("option1_" + i));
			eqd.setOption2(maps.get("option2_" + i));
			eqd.setOption3(maps.get("option3_" + i));
			eqd.setOption4(maps.get("option4_" + i));
			eqd.setCorrectAnswer(maps.get("radio_question_" + i));
			eqd.setQuestion(maps.get("question_content_" + i));
			eqd.setExerciseQuestionId(exerciseQuestionId);
			exerciseQuestionDetailDao.save(eqd);
		}

		// upadate number of question of exercise.
		Exercise exercise = exerciseDao.findById(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);

		return "redirect:/new-exercise/question?id=" + exerciseId + "&num=" + (num + 1);
	}

	@RequestMapping(value = "/save-double-passage-question/{exerciseId}/{num}", method = RequestMethod.POST)
	public String saveDoublePassageQuestion(Model model, @PathVariable("exerciseId") int exerciseId,
			@PathVariable("num") int num, @RequestParam Map<String, String> maps) {

		// calculate the subquestion. each sub question has 5 name(1 radio and 4
		// option). and 2 paragraph have 2 name.
		int subQuestion = (maps.size() - 2) / 5;

		// save exercise question
		ExerciseQuestion eq = new ExerciseQuestion();
		// set num

		eq.setNum(num);
		eq.setSubQuestion(subQuestion);
		eq.setExerciseId(exerciseId);
		eq.setParagraph(maps.get("paragraph1"));
		eq.setParagraph2(maps.get("paragraph2"));
		exerciseQuestionDao.save(eq); // save first then get id of this question
		int exerciseQuestionId = eq.getId();

		// save exercise question detail
		for (int i = 1; i <= subQuestion; i++) {
			ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
			eqd.setNum(i);
			eqd.setOption1(maps.get("option1_" + i));
			eqd.setOption2(maps.get("option2_" + i));
			eqd.setOption3(maps.get("option3_" + i));
			eqd.setOption4(maps.get("option4_" + i));
			eqd.setCorrectAnswer(maps.get("radio_question_" + i));
			eqd.setQuestion(maps.get("question_content_" + i));
			eqd.setExerciseQuestionId(exerciseQuestionId);
			exerciseQuestionDetailDao.save(eqd);
		}

		// upadate number of question of exercise.
		Exercise exercise = exerciseDao.findById(exerciseId);
		exercise.setNumberOfQuestion(String.valueOf(num));
		exerciseDao.save(exercise);

		return "redirect:/new-exercise/question?id=" + exerciseId + "&num=" + (num + 1);
	}

	@GetMapping("/download")
	public String download(@RequestParam("url") int type, Model model, HttpServletResponse response,
			HttpServletRequest request) throws IOException {
		/*
		 * String[] parts = url.split("/"); String bucketName = parts[3]; // bucket name
		 * String key = parts[4]; // ten file S3Service s3 = new S3Service();
		 * InputStream input = s3.getFile(bucketName, key);
		 */
		// String filePath = System.getProperty("user.dir")+"/upload-dir/" + key;
		String path = "";

		
		switch (type) {
		case 1:
		{
			path = request.getSession().getServletContext().getRealPath("/") + "/instructions/" + "Instruction_Part1.xlsx";
			break;
		}
		case 2: 
		{
			path = request.getSession().getServletContext().getRealPath("/") + "/instructions/" + "Instruction_Part2.xlsx";
			break;
		}
		case 3: 
		{
			path = request.getSession().getServletContext().getRealPath("/") + "/instructions/" + "Instruction_Part3.xlsx";
			break;
		}
		case 4: 
		{
			path = request.getSession().getServletContext().getRealPath("/") + "/instructions/" + "Instruction_Part4.xlsx";
			break;
		}
		case 5: 
		{
			path = request.getSession().getServletContext().getRealPath("/") + "/instructions/" + "Instruction_Part5.xlsx";
			break;
		}
		case 6: 
		{
			path = request.getSession().getServletContext().getRealPath("/") + "/instructions/" + "Instruction_Part6.xlsx";
			break;
		}
		case 7: 
		{
			path = request.getSession().getServletContext().getRealPath("/") + "/instructions/" + "Instruction_Part7.xlsx";
			break;
		}
		case 8: 
		{
			path = request.getSession().getServletContext().getRealPath("/") + "/instructions/" + "Instruction_Part8.xlsx";
			break;
		}
		case 9: 
		{
			path = request.getSession().getServletContext().getRealPath("/") + "/instructions/" +"Instruction_inputFile-Exam.xlsx";
			break;
		}

		default:
			break;
		}
		
		// String filePath = storageService.store(path, input, key);

		File file = new File(path);
		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		if (mimeType == null) {
			System.out.println("mimetype is not detectable, will take default");
			mimeType = "application/octet-stream";
		}
		InputStream is = new FileInputStream(file);
		// MIME type of the file
		response.setContentType("application/octet-stream");
		// Response header
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
		// Read from the file and write into the response
		OutputStream os = response.getOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) != -1) {
			os.write(buffer, 0, len);
		}
		os.flush();
		os.close();
		is.close();
		return "redirect:/";
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

	private void uploadfileS3(HttpServletRequest request, ExerciseQuestion eq, MultipartFile audio, MultipartFile photo,
			int exerciseQuestionId) {

		S3Service file_s3 = new S3Service();

		String audio_url = file_s3.uploadS3(request, audio, "exer", "audio", exerciseQuestionId);
		eq.setAudio(audio_url);

		if (photo != null) {
			String photo_url = file_s3.uploadS3(request, photo, "exer", "photo", exerciseQuestionId);
			eq.setPhoto(photo_url);
		}

		/*
		 * String audioStorePath = request.getRealPath("/") + "/audio/"+
		 * audio.getOriginalFilename(); Path pathAudio = Paths.get(audioStorePath);
		 * String fileAudioName = storageService.store(pathAudio, audio); String
		 * pathAudioFile = request.getRealPath("/") + "/audio/" + fileAudioName; String
		 * audio_url = file_s3.uploadS3(pathAudioFile, "exer", "audio",
		 * exerciseQuestionId);
		 * 
		 * // String audio_url = file_s3.uploadS3(audio, "exer", "audio",
		 * exerciseQuestionId); eq.setAudio(audio_url);
		 * 
		 * if (photo != null) { String photoStorePath =request.getRealPath("/") +
		 * "/photo/"+ photo.getOriginalFilename(); Path pathPhoto =
		 * Paths.get(photoStorePath); String filePhotoName =
		 * storageService.store(pathPhoto, photo); String pathPhotoFile =
		 * request.getRealPath("/") + "/photo/" + filePhotoName; String photo_url =
		 * file_s3.uploadS3(pathPhotoFile, "exer","photo", exerciseQuestionId);
		 * eq.setPhoto(photo_url); }
		 */
	}

	public int part1(HttpServletRequest request, Exercise exercise, ExerciseQuestion eq, ExerciseQuestionDetail eqd,
			User user, Sheet worksheet, String part, int level) {
		Row row2 = (Row) worksheet.getRow(1);
		Row row1 = (Row) worksheet.getRow(0);
		
//		System.out.println("bbbb"+row2.getCell(0).toString() + "aaaaaa");
		if (row1 == null || exerciseDao.findName(row1.getCell(0).toString()).size() >=1) { 
			return 1;
		} else if(row2 == null || row2.getCell(0) == null ||row2.getCell(0).toString() == "" || Double.parseDouble(row2.getCell(0).toString()) <= 0){
			return 2;
			} else {
			exercise.setName(row1.getCell(0).toString());
			
			exercise.setAuthor(user.getId());
			exercise.setLevel(level);
			exercise.setNumberOfQuestion(row2.getCell(0).toString());		
			exercise.setPart(Integer.parseInt(part));
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date date = new Date();
			String dateCreate = dateFormat.format(date);
			exercise.setDateCreate(dateCreate);			
			exerciseDao.save(exercise);
			
			if (row1.getCell(0) == null) { 
				exercise.setName("Exercise " + exercise.getId());
			} else
				exercise.setName(row1.getCell(0).toString());
			
			exerciseDao.save(exercise);
	
			Row row;
			int temp = 0;
			for (int i = 3; i <= worksheet.getLastRowNum(); i++) {
				eq = new ExerciseQuestion();
				eq.setExerciseId(exercise.getId());
				eq.setNum(i - 2);
				eq.setSubQuestion(1);
				exerciseQuestionDao.save(eq);
				row = (Row) worksheet.getRow(i);
	
				/*if (row.getCell(0) == null || row.getCell(1) == null) {
					eq.setAudio(null);
					eq.setPhoto(null);
				} // suppose excel cell is empty then its set to 0 the variable
				else {
					File fiAudio = new File(row.getCell(1).toString());
					File fiPhoto = new File(row.getCell(0).toString());
					FileInputStream input;
					FileInputStream input2;
					try {
						input = new FileInputStream(fiAudio);
						input2 = new FileInputStream(fiPhoto);
	
						MultipartFile audio;
						MultipartFile photo;
						try {
							audio = new MockMultipartFile(fiAudio.getName(), fiAudio.getName(), "text/plain",
									IOUtils.toByteArray(input));
							photo = new MockMultipartFile(fiPhoto.getName(), fiPhoto.getName(), "text/plain",
									IOUtils.toByteArray(input2));
	
							uploadfileS3(request, eq, audio, photo, eq.getId());
						} catch (IOException e) {
							e.printStackTrace();
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}*/
				exerciseQuestionDao.save(eq);
	
				eqd = new ExerciseQuestionDetail();
				temp++;
	
				String option_1;
				if (row.getCell(0) == null) {
					option_1 = "null";
				} // suppose excel cell is empty then its set to 0 the variable
				else
					option_1 = row.getCell(0).toString();
	
				String option_2;
				if (row.getCell(1) == null) {
					option_2 = "null";
				} // suppose excel cell is empty then its set to 0 the variable
				else
					option_2 = row.getCell(1).toString();
	
				String option_3;
				if (row.getCell(2) == null) {
					option_3 = "null";
				} // suppose excel cell is empty then its set to 0 the variable
				else
					option_3 = row.getCell(2).toString();
	
				String option_4;
				if (row.getCell(3) == null) {
					option_4 = "null";
				} // suppose excel cell is empty then its set to 0 the variable
				else
					option_4 = row.getCell(3).toString();
	
				String correct_answer;
				if (row.getCell(4) == null) {
					correct_answer = "null";
				} // suppose excel cell is empty then its set to 0 the variable
				else
					correct_answer = row.getCell(4).toString();
	
				eqd.setNum(temp);
				eqd.setOption1(option_1);
				eqd.setOption2(option_2);
				eqd.setOption3(option_3);
				eqd.setOption4(option_4);
				eqd.setCorrectAnswer(correct_answer);
				eqd.setExerciseQuestionId(eq.getId());
				exerciseQuestionDetailDao.save(eqd);
			}
			return 0;
		}
	}

	public int part2(HttpServletRequest request, Exercise exercise, ExerciseQuestion eq, ExerciseQuestionDetail eqd,
			User user, Sheet worksheet, String part, int level) throws IOException {
		Row row2 = (Row) worksheet.getRow(1);
		Row row1 = (Row) worksheet.getRow(0);
		
		if (row1 == null || row1.getCell(0).toString() == "" || exerciseDao.findName(row1.getCell(0).toString()).size() >=1) { 
			return 1;
		} else if(row2 == null || row2.getCell(0) == null ||row2.getCell(0).toString() == "" || Double.parseDouble(row2.getCell(0).toString()) <= 0){
			return 2;
			} else {
				exercise.setName(row1.getCell(0).toString());
				exercise.setAuthor(user.getId());
				exercise.setLevel(level);
				exercise.setNumberOfQuestion(row2.getCell(0).toString());		
				exercise.setPart(Integer.parseInt(part));
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();
				String dateCreate = dateFormat.format(date);
				exercise.setDateCreate(dateCreate);			
				exerciseDao.save(exercise);		
				
				if (row1.getCell(0) == null) { 
					exercise.setName("Exercise " + exercise.getId());
				} else
					exercise.setName(row1.getCell(0).toString());
				
				exerciseDao.save(exercise);
		
				Row row;
				int temp = 0;
				for (int i = 3; i <= worksheet.getLastRowNum(); i++) {
					eq = new ExerciseQuestion();
					eq.setExerciseId(exercise.getId());
					eq.setNum(i - 2);
					eq.setSubQuestion(1);
					exerciseQuestionDao.save(eq);
					row = (Row) worksheet.getRow(i);
					
					eqd = new ExerciseQuestionDetail();
					temp++;
					
					String question;
					if (row.getCell(0) == null) {
						question = "null";
					} // suppose excel cell is empty then its set to 0 the variable
					else
						question = row.getCell(0).toString();
					
					String option_1;
					if (row.getCell(1) == null) {
						option_1 = "null";
					} // suppose excel cell is empty then its set to 0 the variable
					else
						option_1 = row.getCell(1).toString();
		
					String option_2;
					if (row.getCell(2) == null) {
						option_2 = "null";
					} // suppose excel cell is empty then its set to 0 the variable
					else
						option_2 = row.getCell(2).toString();
		
					String option_3;
					if (row.getCell(3) == null) {
						option_3 = "null";
					} // suppose excel cell is empty then its set to 0 the variable
					else
						option_3 = row.getCell(3).toString();
		
					String correct_answer;
					if (row.getCell(4) == null) {
						correct_answer = "null";
					} // suppose excel cell is empty then its set to 0 the variable
					else
						correct_answer = row.getCell(4).toString();
		
					eqd.setNum(temp);
					eqd.setQuestion(question);
					eqd.setOption1(option_1);
					eqd.setOption2(option_2);
					eqd.setOption3(option_3);
					eqd.setCorrectAnswer(correct_answer);
					eqd.setExerciseQuestionId(eq.getId());
					exerciseQuestionDetailDao.save(eqd);
				}
			}
		return 0;
	}

	public int part3(HttpServletRequest request, Exercise exercise, ExerciseQuestion eq, ExerciseQuestionDetail eqd,
			User user, Sheet worksheet, String part, int level) {
		Row row2 = (Row) worksheet.getRow(1);
		Row row1 = (Row) worksheet.getRow(0);
		
		if (row1 == null || row1.getCell(0).toString() == "" || exerciseDao.findName(row1.getCell(0).toString()).size() >=1)  { 
			return 1;
		} else if(row2 == null || row2.getCell(0) == null ||row2.getCell(0).toString() == "" || Double.parseDouble(row2.getCell(0).toString()) <= 0){
			return 2;
			} else {
				exercise.setName(row1.getCell(0).toString());
			
				exercise.setAuthor(user.getId());
				exercise.setLevel(level);
				exercise.setNumberOfQuestion(row2.getCell(0).toString());		
				exercise.setPart(Integer.parseInt(part));
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();
				String dateCreate = dateFormat.format(date);
				exercise.setDateCreate(dateCreate);		
				exerciseDao.save(exercise);	
				
				if (row1.getCell(0) == null) { 
					exercise.setName("Exercise " + exercise.getId());
				} else
					exercise.setName(row1.getCell(0).toString());
				
				exerciseDao.save(exercise);
		
				// so cau hoi
				int numQues = (int) Double.parseDouble(row2.getCell(0).toString());
		
				Row row;
				int firstquesRow = 3;
				int num = 0;
				int temp;
				for (int i = 0; i < numQues; i++) {
					row = (Row) worksheet.getRow(firstquesRow);
					temp = 0;
					num++;
					eq = new ExerciseQuestion();
					eq.setExerciseId(exercise.getId());
					eq.setNum(num);
					eq.setSubQuestion(3);
					eq.setParagraph(row.getCell(0).toString());
					exerciseQuestionDao.save(eq);
		
					/*if (row.getCell(0) == null) {
						eq.setAudio(null);
					} // suppose excel cell is empty then its set to 0 the variable
					else {
						File fi = new File(row.getCell(0).toString());
						System.out.println(fi.getName());
		
						FileInputStream input;
						try {
							input = new FileInputStream(fi);
		
							MultipartFile audio;
							try {
								audio = new MockMultipartFile(fi.getName(), fi.getName(), "text/plain",
										IOUtils.toByteArray(input));
		
								uploadfileS3(request, eq, audio, null, eq.getId());
								exerciseQuestionDao.save(eq);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}*/
					for (int j = firstquesRow; j <= firstquesRow + 2; j++) {
						row = (Row) worksheet.getRow(j);
						eqd = new ExerciseQuestionDetail();
						temp++;
		
						String question;
						if (row.getCell(1) == null) {
							question = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							question = row.getCell(1).toString();
		
						String option_1;
						if (row.getCell(2) == null) {
							option_1 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_1 = row.getCell(2).toString();
		
						String option_2;
						if (row.getCell(3) == null) {
							option_2 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_2 = row.getCell(3).toString();
		
						String option_3;
						if (row.getCell(4) == null) {
							option_3 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_3 = row.getCell(4).toString();
		
						String option_4;
						if (row.getCell(5) == null) {
							option_4 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_4 = row.getCell(5).toString();
		
						String correct_answer;
						if (row.getCell(6) == null) {
							correct_answer = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							correct_answer = row.getCell(6).toString();
		
						eqd.setNum(temp);
						eqd.setQuestion(question);
						eqd.setOption1(option_1);
						eqd.setOption2(option_2);
						eqd.setOption3(option_3);
						eqd.setOption4(option_4);
						eqd.setCorrectAnswer(correct_answer);
						eqd.setExerciseQuestionId(eq.getId());
						exerciseQuestionDetailDao.save(eqd);
		
					}
					firstquesRow = firstquesRow + 3;
				}
			}
		return 0;
	}

	public int part4(HttpServletRequest request, Exercise exercise, ExerciseQuestion eq, ExerciseQuestionDetail eqd,
		User user, Sheet worksheet, String part, int level) {
		Row row2 = (Row) worksheet.getRow(1);
		Row row1 = (Row) worksheet.getRow(0);
		
		if (row1 == null || row1.getCell(0).toString() == "" || exerciseDao.findName(row1.getCell(0).toString()).size() >=1)  { 
			return 1;
		} else if(row2 == null || row2.getCell(0) == null ||row2.getCell(0).toString() == "" || Double.parseDouble(row2.getCell(0).toString()) <= 0){
			return 2;
			} else {
				exercise.setName(row1.getCell(0).toString());
				exercise.setAuthor(user.getId());
				exercise.setLevel(level);
				exercise.setNumberOfQuestion(row2.getCell(0).toString());		
				exercise.setPart(Integer.parseInt(part));
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();
				String dateCreate = dateFormat.format(date);
				exercise.setDateCreate(dateCreate);
				
				exerciseDao.save(exercise);
				
				if (row1.getCell(0) == null) { 
					exercise.setName("Exercise " + exercise.getId());
				} else
					exercise.setName(row1.getCell(0).toString());
				
				exerciseDao.save(exercise);
		
				// so cau hoi
				int numQues = (int) Double.parseDouble(row2.getCell(0).toString());
		
				Row row;
				int firstquesRow = 3;
				int num = 0;
				int temp;
				for (int i = 0; i < numQues; i++) {
					row = (Row) worksheet.getRow(firstquesRow);
					temp = 0;
					num++;
					eq = new ExerciseQuestion();
					eq.setExerciseId(exercise.getId());
					eq.setNum(num);
					eq.setSubQuestion(3);
					eq.setParagraph(row.getCell(0).toString());
					exerciseQuestionDao.save(eq);
					
					for (int j = firstquesRow; j <= firstquesRow + 2; j++) {
						row = (Row) worksheet.getRow(j);
						eqd = new ExerciseQuestionDetail();
						temp++;
		
						String question;
						if (row.getCell(1) == null) {
							question = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							question = row.getCell(1).toString();
		
						String option_1;
						if (row.getCell(2) == null) {
							option_1 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_1 = row.getCell(2).toString();
		
						String option_2;
						if (row.getCell(3) == null) {
							option_2 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_2 = row.getCell(3).toString();
		
						String option_3;
						if (row.getCell(4) == null) {
							option_3 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_3 = row.getCell(4).toString();
		
						String option_4;
						if (row.getCell(5) == null) {
							option_4 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_4 = row.getCell(5).toString();
		
						String correct_answer;
						if (row.getCell(6) == null) {
							correct_answer = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							correct_answer = row.getCell(6).toString();
		
						eqd.setNum(temp);
						eqd.setQuestion(question);
						eqd.setOption1(option_1);
						eqd.setOption2(option_2);
						eqd.setOption3(option_3);
						eqd.setOption4(option_4);
						eqd.setCorrectAnswer(correct_answer);
						eqd.setExerciseQuestionId(eq.getId());
						exerciseQuestionDetailDao.save(eqd);
		
					}
					firstquesRow = firstquesRow + 3;
				}
			}
		return 0;
	}

	public int part5(Exercise exercise, ExerciseQuestion eq, ExerciseQuestionDetail eqd, User user, Sheet worksheet,
			String part, int level) {
		Row row2 = (Row) worksheet.getRow(1);
		Row row1 = (Row) worksheet.getRow(0);
		
		if (row1 == null || row1.getCell(0).toString() == "" || exerciseDao.findName(row1.getCell(0).toString()).size() >=1)  { 
			return 1;
		}else if(row2 == null || row2.getCell(0) == null ||row2.getCell(0).toString() == "" || Double.parseDouble(row2.getCell(0).toString()) <= 0){
			return 2;
			} else {
				exercise.setName(row1.getCell(0).toString());
				exercise.setNumberOfQuestion(row2.getCell(0).toString());
				exercise.setPart(Integer.parseInt(part));
				exercise.setLevel(level);
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();
				String dateCreate = dateFormat.format(date);
				exercise.setDateCreate(dateCreate);
				exercise.setAuthor(user.getId());
				exerciseDao.save(exercise);
				
				if (row1.getCell(0) == null) { 
					exercise.setName("Exercise " + exercise.getId());
				} else
					exercise.setName(row1.getCell(0).toString());
				exerciseDao.save(exercise);
		
				/*
				 * // so cau hoi int ii = (int) Double.parseDouble(row2.getCell(0).toString());
				 */
		
				Row row;
				int temp = 0;
				for (int i = 3; i <= worksheet.getLastRowNum(); i++) {
					eq = new ExerciseQuestion();
					eq.setExerciseId(exercise.getId());
					eq.setNum(i - 2);
					eq.setSubQuestion(1);
					exerciseQuestionDao.save(eq);
					row = (Row) worksheet.getRow(i);
		
					eqd = new ExerciseQuestionDetail();
					temp++;
		
					String question;
					if (row.getCell(0) == null) {
						question = "null";
					} // suppose excel cell is empty then its set to 0 the variable
					else
						question = row.getCell(0).toString();
		
					String option_1;
					if (row.getCell(1) == null) {
						option_1 = "null";
					} // suppose excel cell is empty then its set to 0 the variable
					else
						option_1 = row.getCell(1).toString();
		
					String option_2;
					if (row.getCell(2) == null) {
						option_2 = "null";
					} // suppose excel cell is empty then its set to 0 the variable
					else
						option_2 = row.getCell(2).toString();
		
					String option_3;
					if (row.getCell(3) == null) {
						option_3 = "null";
					} // suppose excel cell is empty then its set to 0 the variable
					else
						option_3 = row.getCell(3).toString();
		
					String option_4;
					if (row.getCell(4) == null) {
						option_4 = "null";
					} // suppose excel cell is empty then its set to 0 the variable
					else
						option_4 = row.getCell(4).toString();
		
					String correct_answer;
					if (row.getCell(5) == null) {
						correct_answer = "null";
					} // suppose excel cell is empty then its set to 0 the variable
					else
						correct_answer = row.getCell(5).toString();
		
					eqd.setNum(temp);
					eqd.setQuestion(question);
					eqd.setOption1(option_1);
					eqd.setOption2(option_2);
					eqd.setOption3(option_3);
					eqd.setOption4(option_4);
					eqd.setCorrectAnswer(correct_answer);
					eqd.setExerciseQuestionId(eq.getId());
					exerciseQuestionDetailDao.save(eqd);
				}
			}
		return 0;
	}

	public int part6(Exercise exercise, ExerciseQuestion eq, ExerciseQuestionDetail eqd, User user, Sheet worksheet,
			String part, int level) {
		Row row2 = (Row) worksheet.getRow(1);
		Row row1 = (Row) worksheet.getRow(0);
		
		if (row1 == null || row1.getCell(0).toString() == "" || exerciseDao.findName(row1.getCell(0).toString()).size() >=1)  { 
			return 1;
		} else if(row2 == null || row2.getCell(0) == null ||row2.getCell(0).toString() == "" || Double.parseDouble(row2.getCell(0).toString()) <= 0){
			return 2;
			} else {
				exercise.setName(row1.getCell(0).toString());
				exercise.setNumberOfQuestion(row2.getCell(0).toString());
				exercise.setPart(Integer.parseInt(part));
				exercise.setLevel(level);
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();
				String dateCreate = dateFormat.format(date);
				exercise.setDateCreate(dateCreate);
		
				exercise.setAuthor(user.getId());
				exerciseDao.save(exercise);
				
				if (row1.getCell(0) == null) { 
					exercise.setName("Exercise " + exercise.getId());
				} else
					exercise.setName(row1.getCell(0).toString());
				exerciseDao.save(exercise);
		
				// so cau hoi
				int numQues = (int) Double.parseDouble(row2.getCell(0).toString());
		
				Row row;
				int firstquesRow = 3;
				int num = 0;
				int temp;
				for (int i = 0; i < numQues; i++) {
					row = (Row) worksheet.getRow(firstquesRow);
					// so cau hoi nho
					int numsmallQues = (int) Double.parseDouble(row.getCell(1).toString());
					temp = 0;
					num++;
					eq = new ExerciseQuestion();
					eq.setExerciseId(exercise.getId());
					eq.setNum(num);
					eq.setSubQuestion(numsmallQues);
					eq.setParagraph(row.getCell(0).toString());
					exerciseQuestionDao.save(eq);
		
					for (int j = firstquesRow; j < firstquesRow + numsmallQues; j++) {
						row = (Row) worksheet.getRow(j);
						eqd = new ExerciseQuestionDetail();
						temp++;
		
						String option_1;
						if (row.getCell(2) == null) {
							option_1 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_1 = row.getCell(2).toString();
		
						String option_2;
						if (row.getCell(3) == null) {
							option_2 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_2 = row.getCell(3).toString();
		
						String option_3;
						if (row.getCell(4) == null) {
							option_3 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_3 = row.getCell(4).toString();
		
						String option_4;
						if (row.getCell(5) == null) {
							option_4 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_4 = row.getCell(5).toString();
		
						String correct_answer;
						if (row.getCell(6) == null) {
							correct_answer = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							correct_answer = row.getCell(6).toString();
		
						eqd.setNum(temp);
						eqd.setOption1(option_1);
						eqd.setOption2(option_2);
						eqd.setOption3(option_3);
						eqd.setOption4(option_4);
						eqd.setCorrectAnswer(correct_answer);
						eqd.setExerciseQuestionId(eq.getId());
						exerciseQuestionDetailDao.save(eqd);
		
					}
					firstquesRow = firstquesRow + numsmallQues;
				}
			}
		return 0;
	}

	public int part7(Exercise exercise, ExerciseQuestion eq, ExerciseQuestionDetail eqd, User user, Sheet worksheet,
			String part, int level) {
		Row row2 = (Row) worksheet.getRow(1);
		Row row1 = (Row) worksheet.getRow(0);

		if (row1 == null || row1.getCell(0).toString() == "" || exerciseDao.findName(row1.getCell(0).toString()).size() >=1)  { 
			return 1;
		} else if(row2 == null || row2.getCell(0) == null ||row2.getCell(0).toString() == "" || Double.parseDouble(row2.getCell(0).toString()) <= 0){
			return 2;
			} else {
				exercise.setName(row1.getCell(0).toString());
				exercise.setNumberOfQuestion(row2.getCell(0).toString());
				exercise.setPart(Integer.parseInt(part));
				exercise.setLevel(level);
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();
				String dateCreate = dateFormat.format(date);
				exercise.setDateCreate(dateCreate);
		
				exercise.setAuthor(user.getId());
				exerciseDao.save(exercise);
				
				if (row1.getCell(0) == null) { 
					exercise.setName("Exercise " + exercise.getId());
				} else
					exercise.setName(row1.getCell(0).toString());
				exerciseDao.save(exercise);
		
				// so cau hoi
				int numQues = (int) Double.parseDouble(row2.getCell(0).toString());
		
				Row row;
				int firstquesRow = 3;
				int num = 0;
				int temp;
				for (int i = 0; i < numQues; i++) {
					row = (Row) worksheet.getRow(firstquesRow);
					// so cau hoi nho
					int numsmallQues = (int) Double.parseDouble(row.getCell(1).toString());
					temp = 0;
					num++;
					eq = new ExerciseQuestion();
					eq.setExerciseId(exercise.getId());
					eq.setNum(num);
					eq.setSubQuestion(numsmallQues);
					eq.setParagraph(row.getCell(0).toString());
					exerciseQuestionDao.save(eq);
		
					for (int j = firstquesRow; j < firstquesRow + numsmallQues; j++) {
						row = (Row) worksheet.getRow(j);
						eqd = new ExerciseQuestionDetail();
						temp++;
		
						String question;
						if (row.getCell(2) == null) {
							question = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							question = row.getCell(2).toString();
		
						String option_1;
						if (row.getCell(3) == null) {
							option_1 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_1 = row.getCell(3).toString();
		
						String option_2;
						if (row.getCell(4) == null) {
							option_2 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_2 = row.getCell(4).toString();
		
						String option_3;
						if (row.getCell(5) == null) {
							option_3 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_3 = row.getCell(5).toString();
		
						String option_4;
						if (row.getCell(6) == null) {
							option_4 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_4 = row.getCell(6).toString();
		
						String correct_answer;
						if (row.getCell(7) == null) {
							correct_answer = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							correct_answer = row.getCell(7).toString();
		
						eqd.setNum(temp);
						eqd.setQuestion(question);
						eqd.setOption1(option_1);
						eqd.setOption2(option_2);
						eqd.setOption3(option_3);
						eqd.setOption4(option_4);
						eqd.setCorrectAnswer(correct_answer);
						eqd.setExerciseQuestionId(eq.getId());
						exerciseQuestionDetailDao.save(eqd);
		
					}
					firstquesRow = firstquesRow + numsmallQues;
				}
			}
		return 0;
	}

	public int part8(Exercise exercise, ExerciseQuestion eq, ExerciseQuestionDetail eqd, User user, Sheet worksheet,
			String part, int level) {
		Row row1 = (Row) worksheet.getRow(0);
		Row row2 = (Row) worksheet.getRow(1);
		
		if (row1 == null || row1.getCell(0).toString() == "" || exerciseDao.findName(row1.getCell(0).toString()).size() >=1)  { 
			return 1;
		} else if(row2 == null || row2.getCell(0) == null ||row2.getCell(0).toString() == "" || Double.parseDouble(row2.getCell(0).toString()) <= 0){
			return 2;
			} else {
				exercise.setName(row1.getCell(0).toString());
				exercise.setNumberOfQuestion(row2.getCell(0).toString());
				exercise.setPart(Integer.parseInt(part));
				exercise.setLevel(level);
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();
				String dateCreate = dateFormat.format(date);
				exercise.setDateCreate(dateCreate);
		
				exercise.setAuthor(user.getId());
				exerciseDao.save(exercise);
				
				if (row1.getCell(0) == null) { 
					exercise.setName("Exercise " + exercise.getId());
				} else
					exercise.setName(row1.getCell(0).toString());
				exerciseDao.save(exercise);
		
				// so cau hoi
				int numQues = (int) Double.parseDouble(row2.getCell(0).toString());
		
				Row row;
				int firstquesRow = 3;
				int num = 0;
				int temp;
				for (int i = 0; i < numQues; i++) {
					row = (Row) worksheet.getRow(firstquesRow);
					// so cau hoi nho
					int numsmallQues = (int) Double.parseDouble(row.getCell(2).toString());
					temp = 0;
					num++;
					eq = new ExerciseQuestion();
					eq.setExerciseId(exercise.getId());
					eq.setNum(num);
					eq.setSubQuestion(numsmallQues);
					eq.setParagraph(row.getCell(0).toString());
					eq.setParagraph2(row.getCell(1).toString());
					exerciseQuestionDao.save(eq);
		
					for (int j = firstquesRow; j < firstquesRow + numsmallQues; j++) {
						row = (Row) worksheet.getRow(j);
						eqd = new ExerciseQuestionDetail();
						temp++;
		
						String question;
						if (row.getCell(3) == null) {
							question = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							question = row.getCell(3).toString();
		
						String option_1;
						if (row.getCell(4) == null) {
							option_1 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_1 = row.getCell(4).toString();
		
						String option_2;
						if (row.getCell(5) == null) {
							option_2 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_2 = row.getCell(5).toString();
		
						String option_3;
						if (row.getCell(6) == null) {
							option_3 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_3 = row.getCell(6).toString();
		
						String option_4;
						if (row.getCell(7) == null) {
							option_4 = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							option_4 = row.getCell(7).toString();
		
						String correct_answer;
						if (row.getCell(8) == null) {
							correct_answer = "null";
						} // suppose excel cell is empty then its set to 0 the variable
						else
							correct_answer = row.getCell(8).toString();
		
						eqd.setNum(temp);
						eqd.setQuestion(question);
						eqd.setOption1(option_1);
						eqd.setOption2(option_2);
						eqd.setOption3(option_3);
						eqd.setOption4(option_4);
						eqd.setCorrectAnswer(correct_answer);
						eqd.setExerciseQuestionId(eq.getId());
						exerciseQuestionDetailDao.save(eqd);
		
					}
					firstquesRow = firstquesRow + numsmallQues;
				}
			}
		return 0;
	}

	private File convertMultiPartToFile(HttpServletRequest request, MultipartFile file) throws IOException {
		File convFile = new File(request.getRealPath("/") + file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}
}
