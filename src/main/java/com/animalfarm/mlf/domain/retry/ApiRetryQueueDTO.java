package com.animalfarm.mlf.domain.retry;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRetryQueueDTO {
	private Long seq;
	private String idempotencyKey;
	private String apiType; // e.g. "CANCEL_SUBSCRIPTION", "DIVIDEND"
	private String payload; // JSON data
	private String status; // PENDING, PROCESSING, COMPLETED, FAILED
	private int retryCount;
	private LocalDateTime nextRetryAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
