package com.animalfarm.mlf.domain.mypage.dto;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDTO {
	private String subscription_status; // 대기중인지, 당첨됐는지, 낙첨됐는지 등 의 상태
	private Boolean is_starred;

    private Long projectId;
    private String projectName;

    // status (DB raw)
    private String projectStatus;        // PREPARING / ANNOUNCEMENT / SUBSCRIPTION / INPROGRESS / ENDED ...
    private String subscriptionStatus;   // PENDING / APPROVED / REJECTED / CANCELED (JOIN 탭에서만)
    private Boolean starred;             // STAR 탭이면 true/false (선택)

    // dates
    private OffsetDateTime projectStartDate;
    private OffsetDateTime projectEndDate;

    // view fields (서비스에서 가공)
    private String periodText;   // "YYYY. MM. DD - YYYY. MM. DD"
    private String statusText1;  // "진행중" 같은 한글 라벨
    private String statusText2;  // JOIN 탭일 때만 "청약중/당첨/낙첨/취소"
}
