package org.jenkinsci.plugins.badge;

public enum Color {
    red("#e05d44"),
    brightgreen("#44cc11"),
    green("#97CA00"),
    yellowgreen("#a4a61d"),
    yellow("#dfb317"),
    orange("#fe7d37"),
    lightgrey("#9f9f9f"),
    blue("#007ec6");

    private String code;

    private Color(String code) {
    	this.code = code;
    }

    public String code() {
    	return this.code;
	}
}
