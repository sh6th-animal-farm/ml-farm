package com.animalfarm.mlf.domain.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.animalfarm.mlf.domain.project.dto.FarmDTO;

@Controller
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
}
