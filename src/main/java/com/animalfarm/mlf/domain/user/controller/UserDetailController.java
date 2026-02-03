package com.animalfarm.mlf.domain.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.user.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserDetailController {

	@Autowired
	private UserService userService;

	@GetMapping(value = "/me/name", produces = "text/plain; charset=UTF-8")
	public ResponseEntity<String> getMyName() {
		return ResponseEntity.ok(userService.getMyName());
	}

}
