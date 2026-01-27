package com.animalfarm.mlf.domain.retry;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiRetryQueueRepository {
	// 새로운 재시도 작업 등록
	int insert(ApiRetryQueueDTO apiRetryQueue);

	// 재시도 대상 조회 (상태가 PENDING이고 재시도 시간이 된 데이터들)
	List<ApiRetryQueueDTO> selectPendingRetries();

	// 상태와 다음 재시도 시간 업데이트 (실패 시 사용)
	int updateStatusAndNextRetry(ApiRetryQueueDTO apiRetryQueue);

	// 상태값만 단순 업데이트 (진행중 또는 완료 처리 시 사용)
	int updateStatus(ApiRetryQueueDTO apiRetryQueue);

	// 멱등성 키로 기존 데이터 조회 (중복 등록 방지용)
	ApiRetryQueueDTO selectByIdempotencyKey(String idempotencyKey);
}
