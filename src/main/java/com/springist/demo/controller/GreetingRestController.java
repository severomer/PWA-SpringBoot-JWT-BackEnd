package com.springist.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.springist.demo.entity.Event;
import com.springist.demo.entity.EventUser;
import com.springist.demo.entity.Greeting;
import com.springist.demo.entity.User;
import com.springist.demo.service.EventService;
import com.springist.demo.service.EventUserService;
import com.springist.demo.service.GreetingService;
import com.springist.demo.service.UserService;
import com.springist.demo.user.CrmUser;

@CrossOrigin(origins = "http://localhost:8100")
@RestController
@RequestMapping("/api")
public class GreetingRestController {

    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;

	private GreetingService greetingService;
	
	private EventService eventService;
    
    private EventUserService eventUserService;
    
    @Autowired
    private UserService userService;

	
	@Autowired
	public GreetingRestController(GreetingService theGreetingService, EventService eventService, EventUserService eventUserService) {
		greetingService = theGreetingService;
		this.eventService = eventService;
		this.eventUserService=eventUserService;
	}
	
	@PostMapping("/register")
	ResponseEntity<CrmUser> createUser(@RequestBody CrmUser theCrmUser)
	{
		
		String userName = theCrmUser.getUserName();
		System.out.println("Processing registration form for: " + userName);
		

		// check the database if user already exists
        User existing = userService.findByUserName(userName);
        if (existing != null){
			System.out.println("User name already exists.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(theCrmUser);
        }
        
        // create user account        						
        userService.save(theCrmUser);
        
        System.out.println("Successfully created user: " + userName);
		return ResponseEntity.status(HttpStatus.CREATED).body(theCrmUser);
	}

	
	@GetMapping("/greetings")
	public MappingJacksonValue listAll(){
		
		List<Greeting> greetings = greetingService.findAll();
		
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(greetings);
		
		FilterProvider filters = new SimpleFilterProvider()
				.setFailOnUnknownId(false)
                .addFilter("userFilter", SimpleBeanPropertyFilter
                        .filterOutAllExcept("message","post_date","user.userName"));  //nested filter not suppoerted userName notworking
    mappingJacksonValue.setFilters(filters);
    return mappingJacksonValue;

		
//		return greetingService.findAll();
		
	}
	
	
	@PostMapping("/greetings")
	public String PostGreeting()
	{
		return "post greeting";
	}


	@GetMapping("/greetings/{userName}")
	
	public MappingJacksonValue listUser(@PathVariable String userName){
		
		List<Greeting> greetings = greetingService.findByName(userName);
		
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(greetings);
		
		FilterProvider filters = new SimpleFilterProvider()
				.setFailOnUnknownId(false)
                .addFilter("userFilter", SimpleBeanPropertyFilter
                        .serializeAll()
                        );  //nested filter not suppoerted userName notworking
    mappingJacksonValue.setFilters(filters);
    return mappingJacksonValue;

//		return greetings;
	
		
	}
	
	@GetMapping("/events/{pageSize}/{page}")
	public Page<Event> listEvents(@PathVariable Optional<Integer> pageSize, 
									@PathVariable Optional<Integer> page) {
		

        // Evaluate page size. If requested parameter is null, return initial
        // page size
        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        // Evaluate page. If requested parameter is null or less than 0 (to
        // prevent exception), return initial size. Otherwise, return value of
        // param. decreased by 1.
        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;

  //      int evalPage = INITIAL_PAGE;
		Page<Event> events =  eventService.findAllPageable(PageRequest.of(evalPage, evalPageSize));
		return events;
	}
	
	@PostMapping("/euser/{eventId}/{userId}/{attend}")
	public EventUser setEventUser(@PathVariable int userId, @PathVariable int eventId, @PathVariable boolean attend) {
		
		// @RequestBody EventUser euser
		EventUser tempEventUser = eventUserService.findByIds((long)eventId, (long)userId);

		System.out.println("REST API tempEventUser : " + tempEventUser);
		
		tempEventUser.setAttended(attend);
		
		eventUserService.save(tempEventUser);
		return tempEventUser;

	}
	

	
	@GetMapping("/events")
	public Page<Event> listFirstEvents() {
		

        // Evaluate page size. If requested parameter is null, return initial
        // page size
        int evalPageSize = 10;
        // Evaluate page. If requested parameter is null or less than 0 (to
        // prevent exception), return initial size. Otherwise, return value of
        // param. decreased by 1.
 //       int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;

        int evalPage = INITIAL_PAGE;
		Page<Event> events =  eventService.findAllPageable(PageRequest.of(evalPage, evalPageSize));
		return events;
	}
	
	
/*
	@RequestMapping("/user")
	public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
		System.out.println(principal);
		System.out.println("name" + principal.getName());	
		System.out.println("id" + principal.getAttribute("id").toString());	
		return Collections.singletonMap("login", principal.getAttribute("login"));
	}

*/
}
