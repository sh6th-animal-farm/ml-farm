package com.animalfarm.mlf.domain.subscription.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionHistDTO {
	private Long shId;                 // 청약 이력 고유 ID 
    private Long projectId;            // 프로젝트 ID 
    private Long userId;               // 투자자(개인/기업) ID
    
    private OffsetDateTime subscriptionDate; // 청약 신청 일시 
    
    private BigDecimal subscriptionAmount;   // 청약 금액
    
    // 청약 상태: PENDING, APPROVED, REJECTED, CANCELED
    private String subscriptionStatus; 
    
    // 결제 상태: RESERVED, PAID, FAILED, REFUNDED 
    private String paymentStatus;      
    
    // 증권사 체결/주문번호
    private String externalRefId;      
    
    private OffsetDateTime canceledAt; // 취소 일시 

    // UI 출력을 위한 추가 필드 (필요 시 활용)
    private String projectName;        // 프로젝트명 
    private String userNickname;       // 투자자 닉네임
}
