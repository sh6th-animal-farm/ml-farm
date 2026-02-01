package com.animalfarm.mlf.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.animalfarm.mlf.domain.accounting.dto.RefundTokenLedgerDTO;
import com.animalfarm.mlf.domain.refund.RefundRepository;
import com.animalfarm.mlf.domain.token.TokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectClosingWriter implements ItemWriter<RefundTokenLedgerDTO> {
    private final RefundRepository refundRepository;
    private final TokenRepository tokenRepository;

    @Override
    public void write(List<? extends RefundTokenLedgerDTO> items) throws Exception {
        for (RefundTokenLedgerDTO item : items) {
            // 환불 정보 저장
            refundRepository.insertRefund(item.getRefundDTO());
            
            // 토큰 원장 저장
            tokenRepository.insertTokenLedger(item.getTokenLedgerDTO());
            
            // 기존 토큰 소유자 잔고 없애기
            tokenRepository.updateTokenBalance(
                    item.getTokenLedgerDTO().getFromUserId(),
                    item.getTokenLedgerDTO().getTokenId(),
                    item.getTokenLedgerDTO().getFrom_balanceAfter()
                );
        }
    }
}