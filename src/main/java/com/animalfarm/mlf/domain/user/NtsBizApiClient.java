package com.animalfarm.mlf.domain.user;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class NtsBizApiClient {

	@Value("${nts.serviceKey}")
	private String serviceKey;

	@Value("${nts.baseUrl}")
	private String baseUrl;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper om = new ObjectMapper();

	/**
	 * 상태조회: POST /status?serviceKey=...
	 * body: { "b_no": ["1234567890"] }
	 */
	public String status(String bNo) {
		URI uri = UriComponentsBuilder
			.fromHttpUrl(baseUrl + "/status")
			.queryParam("serviceKey", serviceKey)
			.build(true) // 인코딩 유지
			.toUri();

		Map<String, Object> body = new HashMap<>();
		body.put("b_no", Collections.singletonList(bNo));

		return postJson(uri, body);
	}

	/**
	 * 진위확인: POST /validate?serviceKey=...
	 * body: { "businesses": [ { "b_no":"", "start_dt":"", "p_nm":"", "b_nm":"" } ] }
	 */
	public String validate(String bNo, String startDt, String pNm, String bNm) {
		URI uri = UriComponentsBuilder
			.fromHttpUrl(baseUrl + "/validate")
			.queryParam("serviceKey", serviceKey)
			.build(true)
			.toUri();

		Map<String, Object> biz = new LinkedHashMap<>();
		biz.put("b_no", bNo);
		biz.put("start_dt", startDt);
		biz.put("p_nm", pNm);
		// b_nm은 선택값이라 null이면 빈문자 처리(요청 형식 깨지지 않게)
		biz.put("b_nm", bNm == null ? "" : bNm);

		Map<String, Object> body = new HashMap<>();
		body.put("businesses", Collections.singletonList(biz));

		return postJson(uri, body);
	}

	private String postJson(URI uri, Object body) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			HttpEntity<Object> req = new HttpEntity<>(body, headers);

			ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.POST, req, String.class);

			if (!res.getStatusCode().is2xxSuccessful()) {
				throw new RuntimeException("NTS API 실패: " + res.getStatusCode() + " / " + res.getBody());
			}
			return res.getBody();
		} catch (RestClientResponseException e) {
			// 4xx/5xx 본문 포함
			throw new RuntimeException("NTS API 오류: " + e.getRawStatusCode() + " / " + e.getResponseBodyAsString(), e);
		} catch (Exception e) {
			throw new RuntimeException("NTS API 호출 실패: " + e.getMessage(), e);
		}
	}

	/**
	 * 상태조회 파싱
	 * 보통 response.data[0].b_stt_cd / b_stt 를 내려줌.
	 * - "01": 계속사업자
	 * - "02": 휴업자
	 * - "03": 폐업자
	 *
	 * ※ 혹시 응답 필드가 다르면 여기만 살짝 맞추면 됨.
	 */
	public BizStatusParsed parseStatus(String rawJson) {
		try {
			JsonNode root = om.readTree(rawJson);
			JsonNode data0 = root.path("data").isArray() && root.path("data").size() > 0
				? root.path("data").get(0)
				: null;

			if (data0 == null || data0.isMissingNode()) {
				return BizStatusParsed.unknown("상태조회 응답에 data가 없습니다.");
			}

			String bSttCd = text(data0, "b_stt_cd"); // "01"/"02"/"03"
			String bStt = text(data0, "b_stt"); // "계속사업자"/"휴업자"/"폐업자" 등
			if (bSttCd == null) {
				// 혹시 코드가 없고 문자열만 오는 경우 대비
				if (bStt != null) {
					if (bStt.contains("계속")) {
						return BizStatusParsed.active("계속사업자(정상)입니다.");
					}
					if (bStt.contains("휴업")) {
						return BizStatusParsed.suspended("휴업 사업자입니다.");
					}
					if (bStt.contains("폐업")) {
						return BizStatusParsed.closed("폐업 사업자입니다.");
					}
				}
				return BizStatusParsed.unknown("사업자 상태 코드를 찾지 못했습니다.");
			}

			switch (bSttCd) {
				case "01":
					return BizStatusParsed.active("계속사업자(정상)입니다.");
				case "02":
					return BizStatusParsed.suspended("휴업 사업자입니다.");
				case "03":
					return BizStatusParsed.closed("폐업 사업자입니다.");
				default:
					return BizStatusParsed
						.unknown("알 수 없는 상태 코드: " + bSttCd + (bStt != null ? (" (" + bStt + ")") : ""));
			}
		} catch (Exception e) {
			return BizStatusParsed.unknown("상태조회 파싱 실패: " + e.getMessage());
		}
	}

	/**
	 * 진위확인 파싱
	 * 흔한 응답: data[0].valid = "01"(일치) / "02"(불일치) 같은 형태 또는 valid_msg
	 * 환경마다 조금 달라서 "valid", "valid_msg" 둘 다 커버.
	 */
	public boolean parseValidateOk(String rawJson) {
		try {
			JsonNode root = om.readTree(rawJson);
			JsonNode data0 = root.path("data").isArray() && root.path("data").size() > 0
				? root.path("data").get(0)
				: null;

			if (data0 == null || data0.isMissingNode()) {
				return false;
			}

			String valid = text(data0, "valid"); // "01"/"02" 등
			String validMsg = text(data0, "valid_msg"); // "일치"/"불일치" 같은 메시지

			// 1) 코드 기준
			if (valid != null) {
				// 일반적으로 "01"이 진위확인 통과로 쓰이는 경우가 많음
				// 만약 너희 응답이 다르면 여기만 바꾸면 됨.
				return "01".equals(valid) || "Y".equalsIgnoreCase(valid) || "true".equalsIgnoreCase(valid);
			}

			// 2) 메시지 기준(보조)
			if (validMsg != null) {
				return validMsg.contains("일치") || validMsg.toLowerCase().contains("valid");
			}

			return false;
		} catch (Exception e) {
			return false;
		}
	}

	private String text(JsonNode node, String field) {
		JsonNode v = node.get(field);
		if (v == null || v.isNull() || v.isMissingNode()) {
			return null;
		}
		String s = v.asText();
		return (s == null || s.trim().isEmpty()) ? null : s.trim();
	}
}
