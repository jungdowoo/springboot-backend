package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationController {

	
	@GetMapping("/api/location")
	public String location() {
		return "";
	}
}
