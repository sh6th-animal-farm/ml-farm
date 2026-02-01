package com.animalfarm.mlf.common;

import java.math.BigDecimal;

import org.springframework.util.DigestUtils;

public class HashManager {

	public static String createHash(String prevHash, Long projectId, BigDecimal amount) {
		return DigestUtils.md5DigestAsHex((prevHash + projectId + amount.toString()).getBytes());
	}

	public static String resolveHash(String hash) {
		return null;
	}

}
