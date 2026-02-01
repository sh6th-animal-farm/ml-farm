package com.animalfarm.mlf.domain.token;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;

import com.animalfarm.mlf.domain.project.dto.ProjectNewTokenDTO;
import com.animalfarm.mlf.domain.token.dto.TokenDTO;
import com.animalfarm.mlf.domain.token.dto.TokenDetailDTO;

@Mapper
public interface TokenRepository {

	TokenDTO selectByProjectId(Long projectId);

	Long selectWalletId(Long userId);

	public abstract void insertTokenLedger(ProjectNewTokenDTO projectNewTokenDTO);

	public abstract String selectLastHash();
}
