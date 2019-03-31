package org.dhbw.mosbach.ai.cmd.util;

public enum HasAccess {
	
	Y("Y"),
	N("N");
	
	private String hasAccess;
	
	private HasAccess(String hasAccess) {
		this.hasAccess = hasAccess;
	}
	
	public String getHasAccess() {
		return this.hasAccess;
	}
}