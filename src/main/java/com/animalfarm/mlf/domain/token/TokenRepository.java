package com.animalfarm.mlf.domain.token;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.animalfarm.mlf.domain.project.dto.TokenLedgerDTO;
import com.animalfarm.mlf.domain.token.dto.TokenDTO;

@Mapper
public interface TokenRepository {

	TokenDTO selectByProjectId(Long projectId);

	Long selectWalletId(Long userId);

	public abstract void insertTokenLedger(TokenLedgerDTO projectNewTokenDTO);

	void insertTokenLedgerBatch(List<TokenLedgerDTO> tokenLedgerList);

	String selectLastHash();
}
