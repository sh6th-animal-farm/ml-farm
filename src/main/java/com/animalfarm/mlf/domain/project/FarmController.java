package com.animalfarm.mlf.domain.project;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.project.dto.FarmDTO;

@RestController
@RequestMapping("/farm")
public class FarmController {
	@Autowired
    private FarmService farmService;

    @PostMapping("/insert")
    public ResponseEntity<String>  insertFarm(@RequestBody FarmDTO farmDto) {
    	System.out.println(farmDto);
        try {
            farmService.registerFarm(farmDto);
            return ResponseEntity.ok("success");
        } catch (RuntimeException e) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    				.body("fail: " + e.getMessage());
        } catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("system_error");
		}
    }
    
    @GetMapping("/get-coords")
    public ResponseEntity<?> getCoords(@RequestParam String address) {
        try {
            // 서비스에서 외부 API 호출 로직 수행
            Map<String, Object> coords = farmService.getCoordsFromAddress(address);
            return ResponseEntity.ok(coords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("좌표 변환 중 오류 발생");
        }
    }
}
