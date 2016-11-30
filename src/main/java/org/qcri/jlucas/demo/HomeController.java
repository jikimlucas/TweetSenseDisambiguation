package org.qcri.jlucas.demo;

import org.qcri.jlucas.demo.model.AverageScore;
import org.qcri.jlucas.demo.model.FinalOutput;
import org.qcri.jlucas.demo.model.Question;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
	
	private List<FinalOutput> finalOutputs=new ArrayList<FinalOutput>();
	private JordanDisambiguarityService jordanDisambiguarityService = new JordanDisambiguarityService();

	@RequestMapping(value="/")
	public String loadHomePage(Model model){
		model.addAttribute("finalOutputs", finalOutputs);
		
		return "index";
	}
	
	
	@RequestMapping(value="/getOutput",method=RequestMethod.POST)
	public String validateText(@ModelAttribute("question")Question question, Model model){
		try {
			if(jordanDisambiguarityService != null){
				FinalOutput a = jordanDisambiguarityService.processItem(question.getText());
				if(a != null){
					finalOutputs.add(a);
				}

			}
			else{
				System.out.println("jordanDisambiguarityService null");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/";
	}
}
