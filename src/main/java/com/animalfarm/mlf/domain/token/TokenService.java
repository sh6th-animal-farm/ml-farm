package com.animalfarm.mlf.domain.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.domain.token.dto.TokenDTO;

@Service
public class TokenService {

	@Autowired
	TokenRepository tokenRepository;

	public TokenDTO selectByProjectId(Long projectId) {
		return tokenRepository.selectByProjectId(projectId);
	}

}
