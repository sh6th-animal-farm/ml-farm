package com.animalfarm.mlf.domain.token;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.domain.project.dto.ProjectNewTokenDTO;
import com.animalfarm.mlf.domain.token.dto.TokenDTO;

@Service
public class TokenService {

	@Autowired
	TokenRepository tokenRepository;

	public TokenDTO selectByProjectId(Long projectId) {
		return tokenRepository.selectByProjectId(projectId);
	}

	public void insertTokenLedger(List<ProjectNewTokenDTO> newTokenList) {
		for (ProjectNewTokenDTO newToken : newTokenList) {
			tokenRepository.insertTokenLedger(newToken);
		}
	}
}
