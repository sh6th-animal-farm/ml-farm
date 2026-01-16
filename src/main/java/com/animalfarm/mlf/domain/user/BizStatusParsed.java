package com.animalfarm.mlf.domain.user;

import lombok.Getter;

@Getter
public class BizStatusParsed {
	private final boolean active;
	private final String status; // ACTIVE/CLOSED/SUSPENDED/UNKNOWN
	private final String message;

	private BizStatusParsed(boolean active, String status, String message) {
		this.active = active;
		this.status = status;
		this.message = message;
	}

	public static BizStatusParsed active(String msg) {
		return new BizStatusParsed(true, "ACTIVE", msg);
	}

	public static BizStatusParsed closed(String msg) {
		return new BizStatusParsed(false, "CLOSED", msg);
	}

	public static BizStatusParsed suspended(String msg) {
		return new BizStatusParsed(false, "SUSPENDED", msg);
	}

	public static BizStatusParsed unknown(String msg) {
		return new BizStatusParsed(false, "UNKNOWN", msg);
	}
}
