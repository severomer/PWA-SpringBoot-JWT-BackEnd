package com.springist.demo.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import com.springist.demo.entity.Pager;
import com.springist.demo.entity.User;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.springist.demo.entity.Event;
import com.springist.demo.entity.EventUser;
import com.springist.demo.entity.EventUserList;
import com.springist.demo.entity.Greeting;
import com.springist.demo.service.EventService;
import com.springist.demo.service.EventUserService;
import com.springist.demo.service.GreetingService;
import com.springist.demo.service.UserService;
import com.springist.demo.helpers.ZXingHelper;

@Controller
public class EventController {
	

    private static final int BUTTONS_TO_SHOW = 5;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;
    private static final int[] PAGE_SIZES = {5, 10, 20};
    
    private static final int INITIAL_USERID = 2;


	private GreetingService greetingService;
    
    private final EventService eventService;
    
    private UserService userService;
    
    private EventUserService eventUserService;
    

	public EventController(GreetingService greetingService, EventService eventService, UserService userService,
			EventUserService eventUserService) {
		this.greetingService = greetingService;
		this.eventService = eventService;
		this.userService = userService;
		this.eventUserService = eventUserService;
	}

/*
	@Autowired
	private EntityManager entityManager;

	public EventController(EventService eventService, GreetingService greetingService, UserService userService) {
		this.eventService = eventService;
		this.greetingService = greetingService;
		this.userService = userService;
	}
	
*/

    @Autowired
    private JavaMailSender javaMailSender;
    
	 @GetMapping("/events")
	    public ModelAndView showPersonsPage(@RequestParam("eventId") Optional<Integer> eventId,
	    									@RequestParam("pageSize") Optional<Integer> pageSize,
	                                        @RequestParam("page") Optional<Integer> page) {
	        ModelAndView modelAndView = new ModelAndView("events");

	        // Evaluate page size. If requested parameter is null, return initial
	        // page size
	        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
	        // Evaluate page. If requested parameter is null or less than 0 (to
	        // prevent exception), return initial size. Otherwise, return value of
	        // param. decreased by 1.
	        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
	        
	        int evalEventId = eventId.orElse(INITIAL_USERID);
	        
	//        Page<Event> events = eventService.findAllPageable(PageRequest.of(evalPage, evalPageSize));
	        Page<Event> events = eventService.findByOzelFalse(PageRequest.of(evalPage, evalPageSize));
	        Pager pager = new Pager(events.getTotalPages(), events.getNumber(), BUTTONS_TO_SHOW);

	
			
			List<Greeting> theGreetings;

//			theGreetings = greetingService.findAll();

			theGreetings = greetingService.findByEventId((long)evalEventId);
			
			modelAndView.addObject("greetings", theGreetings);

	        Event newEvent = new Event();
	        modelAndView.addObject("event", newEvent);
	        
	    	
			Greeting theGreeting = new Greeting();
			modelAndView.addObject("greeting", theGreeting);
			
		
	        
	        modelAndView.addObject("events", events);
	        modelAndView.addObject("selectedPageSize", evalPageSize);
	        modelAndView.addObject("pageSizes", PAGE_SIZES);
	        modelAndView.addObject("pager", pager);
	        return modelAndView;
	    }

	 @GetMapping("/my")
	    public ModelAndView showMyEventPage(@RequestParam("userId") Optional<Integer> userId,
	    									@RequestParam("pageSize") Optional<Integer> pageSize,
	                                        @RequestParam("page") Optional<Integer> page) {
	        ModelAndView modelAndView = new ModelAndView("events");

	        // Evaluate page size. If requested parameter is null, return initial
	        // page size
	        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
	        // Evaluate page. If requested parameter is null or less than 0 (to
	        // prevent exception), return initial size. Otherwise, return value of
	        // param. decreased by 1.
	        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
	        
	        int evalUserId = userId.orElse(INITIAL_USERID);
	        
//	        Page<Event> events = eventService.findAllPageable(PageRequest.of(evalPage, evalPageSize));
	        
	        Page<Event> events = eventService.findByUser((long) evalUserId, PageRequest.of(evalPage, evalPageSize));
	        
	        Pager pager = new Pager(events.getTotalPages(), events.getNumber(), BUTTONS_TO_SHOW);

	        Event newEvent = new Event();
	        modelAndView.addObject("event", newEvent);
	        
	    	
			Greeting theGreeting = new Greeting();
			modelAndView.addObject("greeting", theGreeting);
			
			        
	        modelAndView.addObject("events", events);
	        modelAndView.addObject("selectedPageSize", evalPageSize);
	        modelAndView.addObject("pageSizes", PAGE_SIZES);
	        modelAndView.addObject("pager", pager);
	        return modelAndView;
	    }
	 
	 @GetMapping("/joined")
	    public ModelAndView showMyJoinedPage(@RequestParam("userId") Optional<Integer> userId,
	    									@RequestParam("pageSize") Optional<Integer> pageSize,
	                                        @RequestParam("page") Optional<Integer> page) {
	        ModelAndView modelAndView = new ModelAndView("events");

	        // Evaluate page size. If requested parameter is null, return initial
	        // page size
	        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
	        // Evaluate page. If requested parameter is null or less than 0 (to
	        // prevent exception), return initial size. Otherwise, return value of
	        // param. decreased by 1.
	        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
	        
	        int evalUserId = userId.orElse(INITIAL_USERID);
	        
//	        Page<Event> events = eventService.findAllPageable(PageRequest.of(evalPage, evalPageSize));
	        User eUser = userService.findByUserId((long) evalUserId);
			EventUser eventUser = new EventUser();
			eventUser.setUser(eUser);	        
//	        Page<Event> events = eventService.findByMembers(eUser, PageRequest.of(evalPage, evalPageSize));
//	        Page<Event> events = eventService.findByEventUsers(eventUser, PageRequest.of(evalPage, evalPageSize));
	     //   Page<Event> events = eventService.findByIdIn(eventUserService.findByUserId((long) evalUserId), PageRequest.of(evalPage, evalPageSize));
	        

//	        List<Event> eventList = eventUserService.findByUserId((long) evalUserId);
	//        events = new PageImpl<Event>(eventList);
	        
	        Page<Event>  events = eventService.findByIdIn(eventUserService.findByUser(eUser), PageRequest.of(evalPage, evalPageSize));
	        
	        
	        System.out.println("EventList  :  " + events) ;
	        
	        Pager pager = new Pager(events.getTotalPages(), events.getNumber(), BUTTONS_TO_SHOW);

	        Event newEvent = new Event();
	        modelAndView.addObject("event", newEvent);
	        
	    	
			Greeting theGreeting = new Greeting();
			modelAndView.addObject("greeting", theGreeting);
			
			        
	        modelAndView.addObject("events", events);
	        modelAndView.addObject("selectedPageSize", evalPageSize);
	        modelAndView.addObject("pageSizes", PAGE_SIZES);
	        modelAndView.addObject("pager", pager);
	        return modelAndView;
	    }
	 

		

		@PostMapping("/events/save")
		public String saveEvent(@RequestParam("userId") int theId, @RequestParam("Edate") String Edate,
				@ModelAttribute("event") Event theEvent) {

			System.out.println("inpute" + Edate);
			
			
			
			LocalDateTime localDateTime = LocalDateTime.now();
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
			LocalDateTime dateTime = LocalDateTime.parse(Edate, formatter);
			
			System.out.println("formater:" + dateTime.toString() );
		
	//		theEvent.setEvent_date((Date) dateTime);
	//		theEvent.setEvent_date(java.sql.Timestamp.valueOf(localDateTime));
			theEvent.setEvent_date(java.sql.Timestamp.valueOf(dateTime));			
//			User theUser = new User();
			
//			theUser = userService.findByUserId((long)theId);
			
			theEvent.setUserId((long) theId);
		
			
			eventService.save(theEvent);
			

			return "redirect:/home";
					
		}
		
 
		

		@PostMapping("/events/update")
		public String updateEvent(@RequestParam("eventId") int theId, @RequestParam("eLat") String theLat, @RequestParam("eLng") String theLng,  
				@ModelAttribute("event") Event theEvent) {

			theEvent = eventService.findById((long) theId).get();

			theEvent.setElat(theLat);
			theEvent.setElng(theLng);
			eventService.save(theEvent);
			

			return "redirect:/home";
					
		}

		

		@PostMapping("/events/join")
		public String joinEvent(@RequestParam("eventId") int theId, @RequestParam("userId") int userId,
				@ModelAttribute("event") Event theEvent) {

			theEvent = eventService.findById((long) theId).get();
			User e = userService.findByUserId((long) userId) ;
/*
			Collection<User> newMembers = theEvent.getMembers();
			User e = userService.findByUserId((long) userId) ;
			newMembers.add(e);
			theEvent.setMembers(newMembers );
			System.out.println("Event members  :  " + theEvent.getMembers());
*/			
			EventUser eventUser = new EventUser();
			eventUser.setEvent(theEvent);
			eventUser.setUser(e);
			eventUser.setAttended(false);
			eventUser.setMemberDate(new Date());

			theEvent.addEventUser(eventUser);
			
	/*		

			// get the current hibernate session
			Session currentSession = entityManager.unwrap(Session.class);
			currentSession.save(eventUser);
	*/
			
			// eventuser tablosuna eklemek icin olusturdum ama eventservice ile de eklebildigim icin gerek kalmadi
			// ikisi birlikte calismiyor
//			eventUserService.save(eventUser);
			
			eventService.save(theEvent);
//			eventService.saveOrUpdate(theEvent);
		
			System.out.println("EventUser : " + eventUser);
			System.out.println("Event id:  " + theId + "userId  : " + userId);

			return "redirect:/eventd?eventId=" + theId;
					
		}

		

		

		@PostMapping("/events/updattend")
		public String updateAttend(@RequestParam("eventId") int theId, @RequestParam("userId") int userId,  
				@RequestParam("eventmem") String eventmem ,@RequestParam("checked") boolean checked,
				@ModelAttribute(value="tempUser") EventUser eventUser) {

//			eventmem.forEach((n) -> System.out.println("mem attend : " + n.isAttended()));
			System.out.println("mem primary key : " + eventmem + "mem event id : " + theId+ " mem user id : " + userId+ " mem checked : " + checked);

			System.out.println("eventUser : " + eventUser);
			
	//		EventUser eventUser = eventUserService.;
			EventUser tempEventUser = eventUserService.findByIds((long)theId, (long)userId);

			System.out.println("tempEeventUser : " + tempEventUser);
			
			tempEventUser.setAttended(eventUser.isAttended());
			eventUserService.save(tempEventUser);
			
			return "redirect:/eventd?eventId=" + theId;
					
		}

		

		

		@GetMapping("/events/attend")
		public ModelAndView attendEvent(@RequestParam("eventId") int theId, @RequestParam("userId") int userId,
				@ModelAttribute("event") Event theEvent, @ModelAttribute(value="tempUser") EventUser eventUser, 
				@ModelAttribute(value="tempEventMembers") EventUserList eventusers) {
			
			ModelAndView modelAndView = new ModelAndView("eventat");

			theEvent = eventService.findById((long) theId).get();
			System.out.println("attend ici"+theEvent);
			User e = userService.findByUserId((long) userId) ;
			System.out.println("attend ici"+e);

 eventusers.setEventMemberList(eventUserService.findByPrimaryKey(theEvent));
 

 List<EventUser> eventmembers = eventUserService.findByPrimaryKey(theEvent);
 
System.out.println("attend ici"+eventmembers);

boolean myBooleanVariable = false;

// myBooleanVariable = eventmembers.get(0).isAttended();
modelAndView.addObject("myBooleanVariable", myBooleanVariable);

modelAndView.addObject("event", theEvent);
modelAndView.addObject("user", e);
modelAndView.addObject("eventmembers", eventmembers);

return modelAndView;
					
		}


		
		
		
		
		
		@GetMapping("/eventd")
		  public ModelAndView showEventPage(@RequestParam("eventId") Optional<Integer> eventId) {
ModelAndView modelAndView = new ModelAndView("eventd");

int evalEventId = eventId.orElse(INITIAL_USERID);

List<Greeting> theGreetings;

//theGreetings = greetingService.findAll();

theGreetings = greetingService.findByEventId((long)evalEventId);

modelAndView.addObject("greetings", theGreetings);

//Event newEvent = new Event();
Event newEvent =  eventService.findById((long) evalEventId).get();
modelAndView.addObject("event", newEvent);


//event members debug
System.out.println("Event members:   " +  newEvent.getMembers());

Collection<User> members = newEvent.getMembers();

List<EventUser> eventmembers = eventUserService.findByPrimaryKey(newEvent);

// th:if="${not #lists.isEmpty(eventmembers)}"

if( eventmembers != null)
{
	System.out.println("Not null ici");
	
	if(eventmembers.isEmpty())
	{
		System.out.println("Is empty ici");
	}
	else
	{

}
}
else System.out.println("Null eventMemebers");

System.out.println("iften cikti");
modelAndView.addObject("eventmembers", eventmembers);
eventmembers.forEach((n) -> System.out.println("mem : " + n.isAttended()));




//System.out.println("EventUser membeers : " + eventmembers + "User 0: " + eventmembers.get(0).getUser());
modelAndView.addObject("members", members);



//owner name gostermek icin

User eUser = userService.findByUserId(newEvent.getUserId());
System.out.println("eUser: " + eUser);
modelAndView.addObject("owner", eUser.getUserName());


Greeting theGreeting = new Greeting();
modelAndView.addObject("greeting", theGreeting);



modelAndView.addObject("event", newEvent);

// Float lat = (float) 41.0;
// Float lng = (float) 29.0;

String lat = "39.9"; 
String lng = "32.8";

lat = newEvent.getElat() == null ? lat : newEvent.getElat() ;   //"40.0";
lng = newEvent.getElng() == null ? lng : newEvent.getElng(); //"28.0";

modelAndView.addObject("lat", lat);

modelAndView.addObject("lng", lng);


return modelAndView;
		}
		

		@RequestMapping(value = "qrcode/{id}", method = RequestMethod.GET)
		public void qrcode(@PathVariable("id") String id, HttpServletResponse response) throws Exception {
			response.setContentType("image/png");
			OutputStream outputStream = response.getOutputStream();
			outputStream.write(ZXingHelper.getQRCodeImage("http://35.223.67.166/eventd?eventId="+id, 200, 200));
			outputStream.flush();
			outputStream.close();
		}
		
		//eventdate i eventd.html den Date type ile alamiyoruz. String to Date conversion exception veriyor.
		
		@PostMapping("/eventd/sendemail")
		public String sendMail(@RequestParam("eventname") Optional<String> eventname, @RequestParam("eventdate") String eventdate, @RequestParam("pageaddress") String pageaddress) throws MessagingException, IOException {


	        System.out.println("Sending Email...");

	        sendEmail(eventname, eventdate, pageaddress);
			//sendEmailWithAttachment();

	        System.out.println("Done");

			return "redirect:/home";
					
		}
		// bunun calismasi icin https://myaccount.google.com/lesssecureapps den daha az guvenli uygulamalara izin veri actim
		
		void sendEmail(Optional<String> eventname, String eventdate, String pageaddress) {

	        SimpleMailMessage msg = new SimpleMailMessage();
	        msg.setTo("severomer86@gmail.com");

	        msg.setSubject("New Test with eventname and date");
	        msg.setText("Hello World \n Spring Boot Email \n" + eventname.get() + "\n" + eventdate.toString() +"\n" + pageaddress.toString());

	        javaMailSender.send(msg);

	    }
}
