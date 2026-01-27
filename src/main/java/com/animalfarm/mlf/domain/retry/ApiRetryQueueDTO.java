package com.animalfarm.mlf.domain.retry;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	private Long retryId;
	private String idempotencyKey;
	private String apiType; // e.g. "CANCEL_SUBSCRIPTION", "DIVIDEND"
	private String payload; // JSON data
	private String query; // String data
	private String status; // PENDING, PROCESSING, COMPLETED, FAILED
	private int retryCount;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime nextRetryAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime createdAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime updatedAt;
}
