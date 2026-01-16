package com.animalfarm.mlf.domain.token;

import org.apache.ibatis.annotations.Mapper;

import com.animalfarm.mlf.domain.token.dto.TokenDTO;

@Mapper
public interface TokenRepository {

	TokenDTO selectByProjectId(Long projectId);

}
