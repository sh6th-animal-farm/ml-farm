package com.animalfarm.mlf.domain.project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.domain.project.dto.FarmDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmService {

	@Value("${api.kakao.rest.key}")
	private String kakaoApiKey;

	private final FarmRepository farmRepository;
	private final ExternalApiUtil apiUtil;

	public List<FarmDTO> selectAllFarm() {
		return farmRepository.selectAllFarm();
	}

	public void registerFarm(FarmDTO farmDTO) {
		farmRepository.insertFarm(farmDTO);
	}

	public Map<String, Object> getCoordsFromAddress(String address) {
		String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;
		Map<String, String> customHeaders = new HashMap<>(); // 단순 Map 사용
		customHeaders.put("Authorization", "KakaoAK " + kakaoApiKey);
        System.out.println(kakaoApiKey);
		
		// 이제 제네릭 타입을 사용하여 안전하게 호출할 수 있습니다.
        ParameterizedTypeReference<Map<String, Object>> typeRef = 
            new ParameterizedTypeReference<Map<String, Object>>() {};
            
		// API 응답에서 x(경도), y(위도) 추출 로직 (간략화)
        Map<String, Object> fullResponse = apiUtil.callExternalApi(url, HttpMethod.GET, null, typeRef, customHeaders);
        System.out.println(fullResponse);
		if (fullResponse != null && fullResponse.containsKey("documents")) {
			List<Map<String, Object>> documents = (List<Map<String, Object>>) fullResponse.get("documents");
			
			if (documents != null && !documents.isEmpty()) {
				Map<String, Object> firstDoc = documents.get(0);

				Map<String, Object> result = new HashMap<>();
				result.put("longitude", firstDoc.get("x")); // 경도
				result.put("latitude", firstDoc.get("y")); // 위도
				result.put("altitude", 0); // 고도는 카카오에서 제공 안 하므로 기본값 0
				
				return result;
			}
		}

		throw new RuntimeException("주소에 해당하는 좌표를 찾을 수 없습니다.");
	}
}
