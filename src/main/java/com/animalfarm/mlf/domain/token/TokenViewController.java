package com.animalfarm.mlf.domain.token;

import java.util.List;

import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.domain.token.dto.TokenListDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenViewController {

	@Autowired
	private TokenService tokenService;
	private final ExternalApiUtil externalApiUtil;

	@GetMapping
	public String tokenListPage(Model model) {
		List<TokenListDTO> list = tokenService.selectAll();
		if (!list.isEmpty()) {
			System.out.println("Data Type: " + list.get(0).getClass().getName());
		}
		model.addAttribute("tokenList", tokenService.selectAll());
		model.addAttribute("contentPage", "/WEB-INF/views/token/token_list.jsp");

		return "layout";
	}

	@GetMapping("/{id}")
	public String tokenDetailPage(@PathVariable Long id, Model model) throws JsonProcessingException {

		// 유효하지 않은 토큰 번호인 경우 404 (Not Found)
		// boolean isOk = tokenService.checkTokenStatus(id);
		// if (!isOk) {
		// 	return "error/404";
		// }

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()); // 자바 8 날짜/시간 모듈 등록
		mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜를 'yyyy-MM-ddThh:mm:ss' 형태로 변환

		model.addAttribute("tokenId", id);
		model.addAttribute("tokenDetail", tokenService.selectByTokenId(id));
		model.addAttribute("orderBuyList", mapper.writeValueAsString(tokenService.selectAllOrderBuyPrice(id)));
		model.addAttribute("orderSellList", mapper.writeValueAsString(tokenService.selectAllOrderSellPrice(id)));
		model.addAttribute("tradeList", mapper.writeValueAsString(tokenService.selectAllTradePrice(id)));
		model.addAttribute("ohlcv", tokenService.selectTokenOhlcv(id));
		model.addAttribute("contentPage", "/WEB-INF/views/token/token_detail.jsp");

		return "layout";
	}
}
