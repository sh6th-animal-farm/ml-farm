package com.animalfarm.mlf.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.user.dto.EnterpriseVerifyRequestDTO;
import com.animalfarm.mlf.domain.user.dto.EnterpriseVerifyResponseDTO;

@RestController
@RequestMapping("/api/auth/enterprise")
public class EnterpriseAuthController {

	@Autowired
	private EnterpriseAuthService enterpriseAuthService;

	@PostMapping(value = "/verification", produces = "application/json; charset = UTF-8")
	public EnterpriseVerifyResponseDTO verify(@RequestBody
	EnterpriseVerifyRequestDTO req) {
		return enterpriseAuthService.verify(req);
	}
}
