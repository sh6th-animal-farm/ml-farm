package com.animalfarm.mlf.domain.accounting.dto;

import com.animalfarm.mlf.domain.project.dto.TokenLedgerDTO;
import com.animalfarm.mlf.domain.refund.RefundDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RefundTokenLedgerDTO {
	private RefundDTO refundDTO;
	private TokenLedgerDTO tokenLedgerDTO;
}
