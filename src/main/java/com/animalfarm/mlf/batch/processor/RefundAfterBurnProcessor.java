package com.animalfarm.mlf.batch.processor;


import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.animalfarm.mlf.common.HashManager;
import com.animalfarm.mlf.domain.accounting.dto.RefundTokenLedgerDTO;
import com.animalfarm.mlf.domain.project.dto.TokenLedgerDTO;
import com.animalfarm.mlf.domain.refund.RefundDTO;
import com.animalfarm.mlf.domain.subscription.SubscriptionRepository;
import com.animalfarm.mlf.domain.token.TokenRepository;
import com.animalfarm.mlf.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@StepScope
@RequiredArgsConstructor
public class RefundAfterBurnProcessor implements ItemProcessor<RefundDTO, RefundTokenLedgerDTO> {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TokenRepository tokenRepository;

    private String currentPrevHash; // 상태 유지 (Stateful)
    
    @Value("#{jobParameters['projectId']}")
    private Long projectId;

    @Value("#{jobParameters['tokenId']}")
    private Long tokenId;

    @BeforeStep
    public void init(StepExecution stepExecution) {
        // Step 시작 전 DB에서 마지막 해시값 한 번만 조회
        String lastHash = tokenRepository.selectLastHash();
        this.currentPrevHash = (lastHash != null) ? lastHash : "0";
    }

    @Override
    public RefundTokenLedgerDTO process(RefundDTO item) throws Exception {
        // 필요한 부가 정보 조회
//        Long userId = userRepository.selectUserIdByUclId(item.getWalletId());
//        Long shId = subscriptionRepository.select(userId, item.getProjectId()).getShId();

    	Long userId = item.getUserId();
    	Long uclId = item.getWalletId();
    	Long externalRefId = item.getTransactionId();
    	Long shId = item.getShId();
    	BigDecimal amount = item.getAmount();
    	
    	BigDecimal currentBalance = item.getCurrentBalance();
    	
        // 해시 생성
        String newHash = HashManager.createHash(currentPrevHash, item.getProjectId(), item.getAmount());
        
        // 현재 해시 업데이트 (다음 아이템을 위해)
        String oldPrevHash = currentPrevHash;
        this.currentPrevHash = newHash;

        // 결과 객체(Writer에 넘길 데이터) 조립
        return RefundTokenLedgerDTO.builder()
                .refundDTO(RefundDTO.builder()
                		.userId(userId)
                		.externalRefId(externalRefId)
                		.uclId(uclId)
                		.amount(amount)
                		.shId(shId)
                		.projectId(this.projectId)
                		.refundType("ALL")
                		.reasonCode("FINAL_SETTLEMENT")
                		.status("SUCCESS")
                		.build()) // 환불 정보
                .tokenLedgerDTO(TokenLedgerDTO.builder()
                        .tokenId(this.tokenId)
                        .fromUserId(userId)
                        .toUserId(null) // 소각이므로 수신자 없음
                        .transactionId("BURN-" + System.currentTimeMillis() + "-" + userId)
                        .externalRefId(externalRefId)
                        .orderAmount(amount)
                        .status("COMPLETED")
                        .fee(BigDecimal.ZERO)
                        .transactionType("BURN")
                        .from_balanceAfter(currentBalance.subtract(amount))
                        .to_balanceAfter(BigDecimal.ZERO)
                        .prevHashValue(oldPrevHash)
                        .hashValue(newHash)
                        .build())
                .build();
    }
}