package com.toeicmaster.springmvc.controller;

import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
	
	@RequestMapping(value="/save-exercise-file", method=RequestMethod.POST)
	public String saveExerciseFile(HttpSession session , Model model, 
			@RequestParam("file") MultipartFile file, @RequestParam("sheetname") String sheetname,
			 @RequestParam("part") String part) {
		if(session.getAttribute("user") == null)
			return "login/login";
		Exercise exercise = new Exercise();
		ExerciseQuestion eq = new ExerciseQuestion();
		ExerciseQuestionDetail eqd = new ExerciseQuestionDetail();
		User user = (User) session.getAttribute("user");
		int temp = 0;	
				
		try {
			Workbook workbook = null;
			try {
				workbook = WorkbookFactory.create(file.getInputStream());
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Sheet worksheet = workbook.getSheet(sheetname);
			
//			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
//			XSSFSheet worksheet = workbook.getSheet(sheetname);
			
			Row row2 = (Row) worksheet.getRow(1);
			int ii = (int) Double.parseDouble(part);
			switch(ii) {
			case 1:    // photo
				{
					Row row1 = (Row) worksheet.getRow(0);
					exercise.setName(row1.getCell(0).toString());
					System.out.println(row1.getCell(0).toString());
							
					exercise.setNumberOfQuestion(row2.getCell(0).toString());			
					exercise.setPart(Integer.parseInt(part));
					
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					Date date = new Date();		
					String dateCreate = dateFormat.format(date);		
					exercise.setDateCreate(dateCreate);		
					
					exercise.setAuthor(user.getId());				
					exerciseDao.save(exercise);
						
					/*// so cau hoi
					int ii = (int) Double.parseDouble(row2.getCell(0).toString());		*/	
					
					String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
					Row row;
					for(int i=3; i<= worksheet.getLastRowNum(); i++) {
						eq = new ExerciseQuestion();
						eq.setExerciseId(exercise.getId());				
						eq.setNum(i-2);	
						eq.setSubQuestion(1);
						exerciseQuestionDao.save(eq);
						row = (Row) worksheet.getRow(i);
										
						if( row.getCell(0)==null) { eq.setAudio(null);}  //suppose excel cell is empty then its set to 0 the variable
		                   else {           
		                	   File fi = new File(row.getCell(1).toString());
		                	   System.out.println(fi.getName());
		                	   
		                	   FileInputStream input = new FileInputStream(fi);
		                	   MultipartFile audioFile = new MockMultipartFile(fi.getName(),fi.getName(), "text/plain", IOUtils.toByteArray(input));                	                   	   
		                	   
		                	   System.out.println(audioFile.getOriginalFilename());
		                	   uploadAudio(eq, absolutePath, audioFile, eq.getId());
		                   }				

						if( row.getCell(1)==null) { eq.setPhoto(null);}  //suppose excel cell is empty then its set to 0 the variable
		                   else {
		                	   File fi = new File(row.getCell(0).toString());
		                	   FileInputStream input = new FileInputStream(fi);
		                	   MultipartFile photoFile = new MockMultipartFile(fi.getName(),fi.getName(), "text/plain", IOUtils.toByteArray(input));                	                   	   

		                	   uploadPhoto(eq, absolutePath, photoFile, eq.getId());
		                   }												
						exerciseQuestionDao.save(eq);				
					
						eqd = new ExerciseQuestionDetail();	
						temp++;
						
						String option_1;
						if( row.getCell(2)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else option_1 = row.getCell(2).toString();
						
						String option_2;
						if( row.getCell(3)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else option_2 = row.getCell(3).toString();
						
						String option_3;
						if( row.getCell(4)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else option_3 = row.getCell(4).toString();
						
						String option_4;
						if( row.getCell(5)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else option_4 = row.getCell(5).toString();
						
						String correct_answer;
						if( row.getCell(6)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else correct_answer = row.getCell(6).toString();
							
						eqd.setNum(temp);
						eqd.setOption1(option_1);
						eqd.setOption2(option_2);
						eqd.setOption3(option_3);
						eqd.setOption4(option_4);
						eqd.setCorrectAnswer(correct_answer);
						eqd.setExerciseQuestionId(eq.getId());
						exerciseQuestionDetailDao.save(eqd);

					}
					break;
				}
			case 2:    // question-response
				{
					Row row1 = (Row) worksheet.getRow(0);
					exercise.setName(row1.getCell(0).toString());
					System.out.println(row1.getCell(0).toString());
							
					exercise.setNumberOfQuestion(row2.getCell(0).toString());			
					exercise.setPart(Integer.parseInt(part));
					
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					Date date = new Date();		
					String dateCreate = dateFormat.format(date);		
					exercise.setDateCreate(dateCreate);		
					
					exercise.setAuthor(user.getId());				
					exerciseDao.save(exercise);
						
					/*// so cau hoi
					int ii = (int) Double.parseDouble(row2.getCell(0).toString());		*/	
					
					String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
					Row row;
					for(int i=3; i<= worksheet.getLastRowNum(); i++) {
						eq = new ExerciseQuestion();
						eq.setExerciseId(exercise.getId());				
						eq.setNum(i-2);	
						eq.setSubQuestion(1);
						exerciseQuestionDao.save(eq);
						row = (Row) worksheet.getRow(i);
										
						if( row.getCell(0)==null) { eq.setAudio(null);}  //suppose excel cell is empty then its set to 0 the variable
		                   else {           
		                	   File fi = new File(row.getCell(0).toString());
		                	   System.out.println(fi.getName());
		                	   
		                	   FileInputStream input = new FileInputStream(fi);
		                	   MultipartFile audioFile = new MockMultipartFile(fi.getName(),fi.getName(), "text/plain", IOUtils.toByteArray(input));                	                   	   
		                	   
		                	   System.out.println(audioFile.getOriginalFilename());
		                	   uploadAudio(eq, absolutePath, audioFile, eq.getId());
		                   }															
						exerciseQuestionDao.save(eq);				
					
						eqd = new ExerciseQuestionDetail();	
						temp++;
						
						String option_1;
						if( row.getCell(1)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else option_1 = row.getCell(1).toString();
						
						String option_2;
						if( row.getCell(2)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else option_2 = row.getCell(2).toString();
						
						String option_3;
						if( row.getCell(3)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else option_3 = row.getCell(3).toString();												
						
						String correct_answer;
						if( row.getCell(4)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else correct_answer = row.getCell(4).toString();
							
						eqd.setNum(temp);
						eqd.setOption1(option_1);
						eqd.setOption2(option_2);
						eqd.setOption3(option_3);						
						eqd.setCorrectAnswer(correct_answer);
						eqd.setExerciseQuestionId(eq.getId());
						exerciseQuestionDetailDao.save(eqd);
					}
					break;
				}
			case 3:    // short converstion
				{
					Row row1 = (Row) worksheet.getRow(0);
					exercise.setName(row1.getCell(0).toString());
					System.out.println(row1.getCell(0).toString());
							
					exercise.setNumberOfQuestion(row2.getCell(0).toString());			
					exercise.setPart(Integer.parseInt(part));
					
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					Date date = new Date();		
					String dateCreate = dateFormat.format(date);		
					exercise.setDateCreate(dateCreate);		
					
					exercise.setAuthor(user.getId());				
					exerciseDao.save(exercise);
						
					// so cau hoi
					int numQues = (int) Double.parseDouble(row2.getCell(0).toString());			
					
					String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
					Row row;
					int firstquesRow = 3;
					int num = 0;
					for (int i = 0; i < numQues; i++)
					{
						row = (Row) worksheet.getRow(firstquesRow);
						temp = 0;
						num++;
						eq = new ExerciseQuestion();
						eq.setExerciseId(exercise.getId());				
						eq.setNum(num);	
						eq.setSubQuestion(3);
						eq.setParagraph(row.getCell(1).toString());
						exerciseQuestionDao.save(eq);
						
						
						if( row.getCell(0)==null) { eq.setAudio(null);}  //suppose excel cell is empty then its set to 0 the variable
		                   else {           
		                	   File fi = new File(row.getCell(0).toString());
		                	   System.out.println(fi.getName());
		                	   
		                	   FileInputStream input = new FileInputStream(fi);
		                	   MultipartFile audioFile = new MockMultipartFile(fi.getName(),fi.getName(), "text/plain", IOUtils.toByteArray(input));                	                   	   
		                	   
		                	   System.out.println(audioFile.getOriginalFilename());
		                	   uploadAudio(eq, absolutePath, audioFile, eq.getId());
		                	   exerciseQuestionDao.save(eq);		
		                   }					
						for(int j= firstquesRow; j<= firstquesRow + 2; j++) {													
							row = (Row) worksheet.getRow(j);
							eqd = new ExerciseQuestionDetail();	
							temp++;
							
							String question;
							if( row.getCell(2)==null) { question = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else question = row.getCell(2).toString();
							
							String option_1;
							if( row.getCell(3)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_1 = row.getCell(3).toString();
							
							String option_2;
							if( row.getCell(4)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_2 = row.getCell(4).toString();
							
							String option_3;
							if( row.getCell(5)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_3 = row.getCell(5).toString();
							
							String option_4;
							if( row.getCell(6)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_4 = row.getCell(6).toString();
							
							String correct_answer;
							if( row.getCell(7)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else correct_answer = row.getCell(7).toString();
								
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
					break;
				}
			case 4:    // short talk
				{
					Row row1 = (Row) worksheet.getRow(0);
					exercise.setName(row1.getCell(0).toString());
					System.out.println(row1.getCell(0).toString());
							
					exercise.setNumberOfQuestion(row2.getCell(0).toString());			
					exercise.setPart(Integer.parseInt(part));
					
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					Date date = new Date();		
					String dateCreate = dateFormat.format(date);		
					exercise.setDateCreate(dateCreate);		
					
					exercise.setAuthor(user.getId());				
					exerciseDao.save(exercise);
						
					// so cau hoi
					int numQues = (int) Double.parseDouble(row2.getCell(0).toString());			
					
					String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
					Row row;
					int firstquesRow = 3;
					int num = 0;
					for (int i = 0; i < numQues; i++)
					{
						row = (Row) worksheet.getRow(firstquesRow);
						temp = 0;
						num++;
						eq = new ExerciseQuestion();
						eq.setExerciseId(exercise.getId());				
						eq.setNum(num);	
						eq.setSubQuestion(3);
						eq.setParagraph(row.getCell(1).toString());
						exerciseQuestionDao.save(eq);
						
						
						if( row.getCell(0)==null) { eq.setAudio(null);}  //suppose excel cell is empty then its set to 0 the variable
		                   else {           
		                	   File fi = new File(row.getCell(0).toString());
		                	   System.out.println(fi.getName());
		                	   
		                	   FileInputStream input = new FileInputStream(fi);
		                	   MultipartFile audioFile = new MockMultipartFile(fi.getName(),fi.getName(), "text/plain", IOUtils.toByteArray(input));                	                   	   
		                	   
		                	   System.out.println(audioFile.getOriginalFilename());
		                	   uploadAudio(eq, absolutePath, audioFile, eq.getId());
		                	   exerciseQuestionDao.save(eq);		
		                   }					
						for(int j= firstquesRow; j<= firstquesRow + 2; j++) {													
							row = (Row) worksheet.getRow(j);
							eqd = new ExerciseQuestionDetail();	
							temp++;
							
							String question;
							if( row.getCell(2)==null) { question = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else question = row.getCell(2).toString();
							
							String option_1;
							if( row.getCell(3)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_1 = row.getCell(3).toString();
							
							String option_2;
							if( row.getCell(4)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_2 = row.getCell(4).toString();
							
							String option_3;
							if( row.getCell(5)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_3 = row.getCell(5).toString();
							
							String option_4;
							if( row.getCell(6)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_4 = row.getCell(6).toString();
							
							String correct_answer;
							if( row.getCell(7)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else correct_answer = row.getCell(7).toString();
								
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
					break;
				}
			case 5:    // incomplete sentence
				{
					Row row1 = (Row) worksheet.getRow(0);
					exercise.setName(row1.getCell(0).toString());
					System.out.println(row1.getCell(0).toString());
							
					exercise.setNumberOfQuestion(row2.getCell(0).toString());			
					exercise.setPart(Integer.parseInt(part));
					
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					Date date = new Date();		
					String dateCreate = dateFormat.format(date);		
					exercise.setDateCreate(dateCreate);		
					
					exercise.setAuthor(user.getId());				
					exerciseDao.save(exercise);
						
					/*// so cau hoi
					int ii = (int) Double.parseDouble(row2.getCell(0).toString());		*/	
					
					String absolutePath = new File("src/main/resources/static/upload").getAbsolutePath();
					Row row;
					for(int i=3; i<= worksheet.getLastRowNum(); i++) {
						eq = new ExerciseQuestion();
						eq.setExerciseId(exercise.getId());				
						eq.setNum(i-2);	
						eq.setSubQuestion(1);
						exerciseQuestionDao.save(eq);
						row = (Row) worksheet.getRow(i);
						
						eqd = new ExerciseQuestionDetail();	
						temp++;
						
						String question;
						if( row.getCell(0)==null) { question = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else question = row.getCell(0).toString();
						
						
						String option_1;
						if( row.getCell(1)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else option_1 = row.getCell(1).toString();
						
						String option_2;
						if( row.getCell(2)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else option_2 = row.getCell(2).toString();
						
						String option_3;
						if( row.getCell(3)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else option_3 = row.getCell(3).toString();
						
						String option_4;
						if( row.getCell(4)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else option_4 = row.getCell(4).toString();
						
						String correct_answer;
						if( row.getCell(5)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
			                else correct_answer = row.getCell(5).toString();
							
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
					break;
				}
			case 6:    // text completion
				{
					Row row1 = (Row) worksheet.getRow(0);
					exercise.setName(row1.getCell(0).toString());
					System.out.println(row1.getCell(0).toString());
							
					exercise.setNumberOfQuestion(row2.getCell(0).toString());			
					exercise.setPart(Integer.parseInt(part));
					
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					Date date = new Date();		
					String dateCreate = dateFormat.format(date);		
					exercise.setDateCreate(dateCreate);		
					
					exercise.setAuthor(user.getId());				
					exerciseDao.save(exercise);
						
					// so cau hoi
					int numQues = (int) Double.parseDouble(row2.getCell(0).toString());						
											
					Row row;
					int firstquesRow = 3;
					int num = 0;
					for (int i = 0; i < numQues; i++)
					{					
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
						exerciseQuestionDao.save(eq);
																	
						for(int j= firstquesRow; j< firstquesRow + numsmallQues; j++) {													
							row = (Row) worksheet.getRow(j);
							eqd = new ExerciseQuestionDetail();	
							temp++;						
							
							String option_1;
							if( row.getCell(3)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_1 = row.getCell(3).toString();
							
							String option_2;
							if( row.getCell(4)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_2 = row.getCell(4).toString();
							
							String option_3;
							if( row.getCell(5)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_3 = row.getCell(5).toString();
							
							String option_4;
							if( row.getCell(6)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_4 = row.getCell(6).toString();
							
							String correct_answer;
							if( row.getCell(7)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else correct_answer = row.getCell(7).toString();
								
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
					break;
				}
			case 7:    // single passage
				{
					Row row1 = (Row) worksheet.getRow(0);
					exercise.setName(row1.getCell(0).toString());
					System.out.println(row1.getCell(0).toString());
							
					exercise.setNumberOfQuestion(row2.getCell(0).toString());			
					exercise.setPart(Integer.parseInt(part));
					
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					Date date = new Date();		
					String dateCreate = dateFormat.format(date);		
					exercise.setDateCreate(dateCreate);		
					
					exercise.setAuthor(user.getId());				
					exerciseDao.save(exercise);
						
					// so cau hoi
					int numQues = (int) Double.parseDouble(row2.getCell(0).toString());						
											
					Row row;
					int firstquesRow = 3;
					int num = 0;
					for (int i = 0; i < numQues; i++)
					{					
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
						exerciseQuestionDao.save(eq);
																	
						for(int j= firstquesRow; j< firstquesRow + numsmallQues; j++) {													
							row = (Row) worksheet.getRow(j);
							eqd = new ExerciseQuestionDetail();	
							temp++;
							
							String question;
							if( row.getCell(1)==null) { question = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else question = row.getCell(1).toString();
							
							String option_1;
							if( row.getCell(3)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_1 = row.getCell(3).toString();
							
							String option_2;
							if( row.getCell(4)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_2 = row.getCell(4).toString();
							
							String option_3;
							if( row.getCell(5)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_3 = row.getCell(5).toString();
							
							String option_4;
							if( row.getCell(6)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_4 = row.getCell(6).toString();
							
							String correct_answer;
							if( row.getCell(7)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else correct_answer = row.getCell(7).toString();
								
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
					break;
				}
			case 8:    // double passage
				{
					Row row1 = (Row) worksheet.getRow(0);
					exercise.setName(row1.getCell(0).toString());
					System.out.println(row1.getCell(0).toString());
							
					exercise.setNumberOfQuestion(row2.getCell(0).toString());			
					exercise.setPart(Integer.parseInt(part));
					
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					Date date = new Date();		
					String dateCreate = dateFormat.format(date);		
					exercise.setDateCreate(dateCreate);		
					
					exercise.setAuthor(user.getId());				
					exerciseDao.save(exercise);
						
					// so cau hoi
					int numQues = (int) Double.parseDouble(row2.getCell(0).toString());						
											
					Row row;
					int firstquesRow = 3;
					int num = 0;
					for (int i = 0; i < numQues; i++)
					{					
						row = (Row) worksheet.getRow(firstquesRow);
						// so cau hoi nho
						int numsmallQues = (int) Double.parseDouble(row.getCell(3).toString());	
						temp = 0;
						num++;
						eq = new ExerciseQuestion();
						eq.setExerciseId(exercise.getId());				
						eq.setNum(num);	
						eq.setSubQuestion(numsmallQues);
						eq.setParagraph(row.getCell(0).toString());
						eq.setParagraph2(row.getCell(1).toString());
						exerciseQuestionDao.save(eq);
																	
						for(int j= firstquesRow; j< firstquesRow + numsmallQues; j++) {													
							row = (Row) worksheet.getRow(j);
							eqd = new ExerciseQuestionDetail();	
							temp++;
							
							String question;
							if( row.getCell(2)==null) { question = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else question = row.getCell(2).toString();
							
							String option_1;
							if( row.getCell(4)==null) { option_1 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_1 = row.getCell(4).toString();
							
							String option_2;
							if( row.getCell(5)==null) { option_2 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_2 = row.getCell(5).toString();
							
							String option_3;
							if( row.getCell(6)==null) { option_3 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_3 = row.getCell(6).toString();
							
							String option_4;
							if( row.getCell(7)==null) { option_4 = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else option_4 = row.getCell(7).toString();
							
							String correct_answer;
							if( row.getCell(8)==null) { correct_answer = "null";}  //suppose excel cell is empty then its set to 0 the variable
				                else correct_answer = row.getCell(8).toString();
								
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
					break;
				}
			}					
			workbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		model.addAttribute("user", user);
		return "redirect:/new-exercise-page";
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
	
	
	@GetMapping("/download")
	public String download(@RequestParam("url") String url, Model model, HttpServletResponse response,
			HttpServletRequest request) throws IOException {
		String[] parts = url.split("/");
		String bucketName = parts[3]; // bucket name
		String key = parts[4]; // ten file
		S3Service s3 = new S3Service();
		InputStream input = s3.getFile(bucketName, key);
		// String filePath = System.getProperty("user.dir")+"/upload-dir/" + key;
		String path = "src/main/resources/static/upload" + File.separator + key;
		System.out.println("/download: " + path);
		String filePath = storageService.store(path, input, key);
		System.out.println(filePath);

		File file = new File(filePath);
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
