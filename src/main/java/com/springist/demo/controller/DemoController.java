package com.springist.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springist.demo.entity.Greeting;
import com.springist.demo.entity.User;
import com.springist.demo.service.GreetingService;
import com.springist.demo.service.UserService;;

@Controller
public class DemoController {

	private GreetingService greetingService;
	
	
    @Autowired
    private UserService userService;
	
	
	@Autowired
	public DemoController(GreetingService theGreetingService) {
		greetingService = theGreetingService;
	}
	
	@GetMapping("/home")
	public String showHome( Model theModel) {

		Greeting theGreeting = new Greeting();
		theModel.addAttribute("greeting", theGreeting);
		
		
		
		List<Greeting> theGreetings;
		greetingService.dummy();

		theGreetings = greetingService.findAll();
			
		// add to the spring model
		theModel.addAttribute("greetings", theGreetings);
		

		
		
		
		
		return "home";
	}

	@GetMapping("/")
	public String showWelcome(@RequestParam(value="searchValue", required = false) String theValue, Model theModel) {
		
		System.out.println("Buraya Welcome Controller icine geldi");
		

		
		List<Greeting> theGreetings;
		greetingService.dummy();
		if(theValue==null || theValue=="")
		{
		System.out.println("Text box ici:  "+theValue);
		theGreetings = greetingService.findAll();
		}
		else
		{ 
			System.out.println("You searched :  "+theValue);
			 theGreetings = greetingService.findByValue(theValue);
		}
//		Greeting newGreeting = new Greeting("Good morning");
//		greetingService.save(newGreeting);

		System.out.println("Buraya List Greeting sonrasina geldi");
		
		// add to the spring model
		theModel.addAttribute("greeting", theGreetings);
		

		
		return "welcome";
	}
	
	// add request mapping for /leaders

	@GetMapping("/leaders")
	public String showLeaders() {
		
		return "leaders";
	}
	
	// add request mapping for /systems
	
	@GetMapping("/systems")
	public String showSystems() {
		
		return "systems";
	}
	
	
	

	@PostMapping("/greeting/save")
	public String saveGreeting(@RequestParam("userId") int theId, @ModelAttribute("greeting") Greeting theGreeting) {
		
		LocalDateTime localDateTime = LocalDateTime.now();
		
		theGreeting.setPost_date(java.sql.Timestamp.valueOf(localDateTime));
		
		User theUser = new User();
		
		theUser = userService.findByUserId((long)theId);
		
		theGreeting.setUser(theUser);
		
		greetingService.save(theGreeting);
		
		//use a redirect to prevent duplicate submission
		
		return "redirect:/home";
				
	}
	
	@PostMapping("/greeting/search")
	public String searchGreeting(@RequestParam("searchValue") String theValue, @ModelAttribute("greeting") Greeting theGreeting) {
		
		System.out.println("Text box ici"+theValue);
		//use a redirect to prevent duplicate submission
		
		return "redirect:/";
				
	}
}










