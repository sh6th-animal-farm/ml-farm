package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.springframework.stereotype.Service;

import com.animalfarm.mlf.domain.project.dto.FarmDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmService {
	private final FarmRepository farmRepository;
	
	List<FarmDTO> selectAllFarm() {
		return farmRepository.selectAllFarm();
	}
}
