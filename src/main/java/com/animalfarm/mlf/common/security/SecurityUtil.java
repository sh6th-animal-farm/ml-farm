package com.animalfarm.mlf.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * [보안 관련 공통 유틸리티]
 * 어느 서비스에서나 현재 로그인한 유저의 정보를 쉽게 가져올 수 있도록 합니다.
 */
public class SecurityUtil {

	/**
	 * 현재 세션에서 로그인한 유저의 ID(PK)를 반환합니다.
	 */
	public static Long getCurrentUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof CustomUser)) {
			throw new RuntimeException("로그인 정보가 만료되었습니다. 다시 로그인해주세요.");
		}

		CustomUser user = (CustomUser)auth.getPrincipal();
		return user.getUserId();
	}

	/**
	 * 필요하다면 유저 객체 전체를 가져오는 메서드도 추가 가능합니다.
	 */
	/*public static CustomUser getCurrentUser() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.getPrincipal() instanceof CustomUser) {
	        return (CustomUser) auth.getPrincipal();
	    }
	    return null;
	}*/
}