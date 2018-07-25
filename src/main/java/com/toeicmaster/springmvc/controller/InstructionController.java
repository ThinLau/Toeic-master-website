package com.toeicmaster.springmvc.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.toeicmaster.springmvc.dao.InstructionDao;
import com.toeicmaster.springmvc.model.Instruction;

@Controller
public class InstructionController {

	@Autowired
	InstructionDao instructionDao;

	Page<Instruction> instructions;

	int pageSize = 6;
	int currentPage = 0;
	int totalPage = 0;
	String insName = null;
	// exercise homepage
	@RequestMapping(value = "/instruction/homepage", method = RequestMethod.GET)
	public String exerciseHomepage(@RequestParam("type") String type, Model model) {
		currentPage = 0;
		model.addAttribute("module", "instruction");
		paging(model, type, insName);
		insName = null;
		return "instruction/instruction_homepage";
	}

	// paging method
	private void paging(Model model, String type, String insName) {
		// get all excerise and paging	

		if(insName == null) {
			instructions = instructionDao.findByType(type, new PageRequest(currentPage, pageSize));
			totalPage = instructions.getContent().size();
		}
		else {
			instructions = instructionDao.findByTypeIgnoreCaseContainingAndNameIgnoreCaseContaining(type, insName, new PageRequest(currentPage, pageSize));
			totalPage = instructions.getContent().size();
		}
		

		model.addAttribute("totalPage", totalPage);
		model.addAttribute("currentPage", currentPage + 1);
		model.addAttribute("type", type);
		model.addAttribute("instructions", instructions);
	}

	// exercise homepage with paging
	@RequestMapping(value = "homepage/{page}", method = RequestMethod.GET)
	public String exercisePage(@RequestParam("type") String type, @PathVariable("page") int page, Model model) {

		currentPage = page - 1;
		model.addAttribute("module", "instruction");
		paging(model, type, insName);
		return "instruction/instruction_homepage";
	}
	
	@RequestMapping(value = "/search-instruction", method = RequestMethod.POST)
	public String saveExercise(HttpSession session, Model model, @RequestParam("type") String type, @RequestParam("search") String search) {
		if(search == "" || search == null){
			insName = null;
		} else 
			insName = search;
		
		currentPage = 0;
		paging(model, type, insName);
		return "redirect:/instruction/homepage?type=" + type;
	}
	

	@RequestMapping(value = "/see-the-instructions/{id}", method = RequestMethod.GET)
	public String seeTheInstructions(@PathVariable("id") int instructionId, Model model) {

		Instruction instruction = instructionDao.getInstruById(instructionId);

		model.addAttribute("module", "instruction");
		model.addAttribute("instruction", instruction);
		return "instruction/see_the_instructions";
	}

	
	// get update instruction page
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String update(@RequestParam("id") int instructionId, Model model) {
		Instruction ins = instructionDao.getInstruById(instructionId);
		model.addAttribute("instruction", ins);
		return "user/update/instruction/update_instruction";
	}

	@RequestMapping(value = "/update-instruction", method = RequestMethod.POST)
	public String saveExercise(HttpSession session, Model model, @RequestParam Map<String, String> maps,
			@RequestParam("id") int instructionId) {
	
		Instruction entity = instructionDao.getInstruById(instructionId);
		if(entity != null) {
			entity.setName(maps.get("name"));
			entity.setType(maps.get("type"));
			entity.setContent(maps.get("content"));
		}
		instructionDao.save(entity);
		
		return "redirect:/instruction/update?id="+instructionId;
	}

}
