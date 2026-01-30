package com.animalfarm.mlf.domain.user.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCertificateLinkDTO {

    private Long uclId;               // ucl_id (PK) = wallet_id
    private Long userId;
    private Long certificatesId;
    private String accountNo;
    private String accessToken;
    private String refreshToken;
    
    private OffsetDateTime tokenExpiredAt;
    private OffsetDateTime refreshTokenExpiredAt;
    
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}