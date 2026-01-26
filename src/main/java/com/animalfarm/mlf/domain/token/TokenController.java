package com.animalfarm.mlf.domain.token;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.domain.token.dto.MarketDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.token.dto.TokenDTO;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TokenController {

	private final TokenService tokenService;
	private final ExternalApiUtil externalApiUtil;

//	@Value("${stock.api.key}")
//	private String apiKey;

	@GetMapping("/token/{projectId}")
	public TokenDTO selectDetail(@PathVariable("projectId")
	Long projectId) {
		return tokenService.selectByProjectId(projectId);
	}

	@GetMapping("/market")
	public ResponseEntity<?> getTokenList(){
		String url = "http://localhost:9090/api/market";

		try {
			List<MarketDTO> tokenList = externalApiUtil.callApi(
					url,
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<ApiResponse<List<MarketDTO>>>() {}
			);
			return ResponseEntity.ok(tokenList);

		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
}
