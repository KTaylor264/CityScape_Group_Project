package com.tylerschnerch.securewodenhunts.controllers;

import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tylerschnerch.securewodenhunts.models.Hunt;
import com.tylerschnerch.securewodenhunts.models.User;
import com.tylerschnerch.securewodenhunts.services.HuntService;
import com.tylerschnerch.securewodenhunts.services.LeaseService;
import com.tylerschnerch.securewodenhunts.services.UserService;
import com.tylerschnerch.securewodenhunts.validator.UserValidator;
@RequestMapping("/hunt")
@Controller


public class HuntController {
	@Autowired 
	LeaseService leaseService;
	@Autowired
	UserValidator userValidator;
	@Autowired
	UserService userService;
	@Autowired HuntService huntService;
	
	@PostMapping("/submit/{id}")
	public String createNewHunt( @PathVariable("id") Integer id, @Valid @ModelAttribute("newHunt") Hunt hunt, BindingResult result, 
			HttpSession session) {
		if (session.getAttribute("userId") == null) {
			return "redirect:/logout";
		}

		if (result.hasErrors()) {
			List<FieldError> errors = result.getFieldErrors();
		    for (FieldError error : errors ) {
		        System.out.println (error.getObjectName() + " - " + error.getDefaultMessage());
		}
			return "redirect:/hunt";

		} else { 
			System.out.println("C");
			huntService.createHunt(hunt);
			System.out.println("D");
			return "redirect:/hunt/review";
			}
	
		}
	
	@GetMapping("/review")
	public String reviewHunt(@ModelAttribute("updateHunt") Hunt hunt, Model model, HttpSession session, String username, Principal principal) {
		if (session.getAttribute("userId") == null) {
			return "redirect:/logout";
		}
		//user stuff
		username = principal.getName();
		System.out.println(username);
		System.out.println("one");
		User theUser =  userService.findByUsername(username);
		System.out.println("two");
		Integer usersId = (Integer)session.getAttribute("userId");
		System.out.println("three");
		model.addAttribute("usersId", usersId);
		System.out.println("four");
		model.addAttribute("user",theUser);
		System.out.println("five");
		model.addAttribute("updateHunt", hunt);
		System.out.println("six");
		List <Hunt> hunts = huntService.allOfAUsersHunts(usersId);
		System.out.println("six");
		ArrayList<Hunt> huntsArray = (ArrayList <Hunt>) hunts;
		System.out.println("seven");
		Hunt lastHunt = huntsArray.get(0);
		System.out.println("eight");
		Date startDate = lastHunt.getStartDate();
	    Date endDate = lastHunt.getEndDate();
	    
	    Long diffInMilliSeconds =  Math.abs(endDate.getTime() - startDate.getTime());
	    Long totalDays = TimeUnit.DAYS.convert(diffInMilliSeconds, TimeUnit.MILLISECONDS);
	    lastHunt.setTotalDays(totalDays);
	    Long daysBooked = lastHunt.getTotalDays();
	    
	    Double rate = lastHunt.getRate();
	    
	    Double serviceFee = rate * .10;
	    lastHunt.setServiceFee(serviceFee);
	    
	    Double total = daysBooked * rate;
	    lastHunt.setTotal(total);
	    
	    Double taxRate = .10;
	    Double tax =  total * taxRate;
	    lastHunt.setTax(tax);
	    
	    Double grandTotal = total + tax + lastHunt.getServiceFee();
	    lastHunt.setGrandTotal(grandTotal);
	    
	    model.addAttribute("lastHunt", lastHunt);
		
	    
	    
	    return "reviewHunt.jsp";
	}
	
	@PutMapping("/confirmed/{id}")
	public String confirmedHunt(@PathVariable("id") Integer id,@ModelAttribute("updateHunt") Hunt hunt, Model model, HttpSession session, Principal principal) {
		if (session.getAttribute("userId") == null) {
			return "redirect:/logout";
		}
			
			return "redirect:/hunt/yourhunts";
	}
	
	@GetMapping("/yourhunts")
	public String yourHunts(@ModelAttribute("yourHunts") Hunt hunt, Model model, Integer id, HttpSession session, Principal principal) {
		if (session.getAttribute("userId") == null) {
			return "redirect:/logout";
		}
		Integer usersId = (Integer)session.getAttribute("userId");
		String username = principal.getName();
		User user = userService.findByUsername(username);	
		
		List<Hunt> usersHunts = huntService.allOfAUsersHunts(usersId);
		System.out.println(usersHunts);
		username = principal.getName();
		model.addAttribute("user",user);
		model.addAttribute("usersHunts", usersHunts);
		
			return "yourHunts.jsp";
	}
	
	 @PostMapping("/delete/{id}")
	    public String deleteHunt(@PathVariable("id") Integer id, HttpSession session) {

	    	if (session.getAttribute("userId") == null) {
	    		return "redirect:/logout";
	    	}

	    	huntService.deleteHunt(id);
	    	return"redirect:/lease/all";
	    }
}
