package com.animalfarm.mlf.domain.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.domain.refund.RefundDTO;
import com.animalfarm.mlf.domain.refund.RefundRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class TokenBurnTasklet implements Tasklet {
    private final ExternalApiUtil externalApiUtil;
    
    private final RefundRepository refundRepository;
    
    @Value("${api.kh-stock.url}")
    String KH_BASE_URL;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // StepExecutionContext에서 대상 리스트를 받아옴
        Long tokenId = (Long) chunkContext.getStepContext().getJobParameters().get("tokenId");
        Long projectId = (Long) chunkContext.getStepContext().getJobParameters().get("projectId");
        
        // 거래 중지 & 토큰 소각 API 요청
        final String burnUrl = KH_BASE_URL + "/api/project/close/" + tokenId;
        String idempotencyKey = "BURN-" + tokenId;
        
        try {
        	// 증권사에서 uclId(walletId), external_ref_id(transactionId), amount 보내줌
        	List<RefundDTO> refundOriginList = externalApiUtil.callApi(burnUrl, 
        			HttpMethod.POST, 
        			null, 
        			new ParameterizedTypeReference<ApiResponse<List<RefundDTO>>>(){},
        			idempotencyKey);
        	
            // JobExecutionContext에 저장 (다음 Step으로 넘기기 위함)
    	    ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution()
    	                                              .getJobExecution().getExecutionContext();

        	if (refundOriginList != null && !refundOriginList.isEmpty()) {
        		List<Long> walletIds = refundOriginList.stream().map(RefundDTO::getWalletId).collect(Collectors.toList());
        		
        	    // walletId 리스트로 한 번에 userId, shId를 조인해서 가져옴
        	    List<RefundDTO> dbInfoList = refundRepository.selectRefundInfoByWalletIds(walletIds, projectId, tokenId);

        	    Map<Long, RefundDTO> dbInfoMap = dbInfoList.stream()
        	            .collect(Collectors.toMap(RefundDTO::getWalletId, Function.identity()));
        	    
        	    for (RefundDTO refundOrigin : refundOriginList) {
        	        RefundDTO dbInfo = dbInfoMap.get(refundOrigin.getWalletId());
        	        if (dbInfo != null) {
        	            refundOrigin.setUserId(dbInfo.getUserId());
        	            refundOrigin.setShId(dbInfo.getShId());
        	            refundOrigin.setProjectId(projectId);
        	            // DB에서 가져온 현재 잔액을 DTO에 세팅
        	            refundOrigin.setCurrentBalance(dbInfo.getCurrentBalance()); 
        	        }
        	    }
        	    // 완성된 리스트를 Context에 보관
        	    jobContext.put("refundList", refundOriginList);
        	} else {
        	    log.warn("소각 대상 데이터가 없습니다. 빈 리스트를 전달합니다.");
        	    jobContext.put("refundList", new ArrayList<>());
        	}
        	
        } catch (Exception e) {
            log.error("소각 API 실패 - 재요청 로직 필요: {}", e.getMessage());
            throw e; // 재시도 설정을 위해 에러를 던짐
        }

        return RepeatStatus.FINISHED;
    }
}