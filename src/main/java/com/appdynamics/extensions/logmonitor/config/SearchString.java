/*
 *  Copyright 2018. AppDynamics LLC and its affiliates.
 *  All Rights Reserved.
 *  This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *  The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.logmonitor.config;

/**
 * @author Aditya Jagtiani
 */
public class SearchString {

    private String displayName;
    private String pattern;
    private Boolean matchExactString;
    private Boolean caseSensitive;
    private Boolean printMatchedString;
    private Boolean publishCustomEvent;
    private String customEventSeverity;
    private String applicationName;

    public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Boolean getMatchExactString() {
        return matchExactString;
    }

    public void setMatchExactString(Boolean matchExactString) {
        this.matchExactString = matchExactString;
    }

    public Boolean getCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public void setPrintMatchedString(Boolean printMatchedString) {
        this.printMatchedString = printMatchedString;
    }

    public Boolean getPrintMatchedString() {
        return printMatchedString;
    }

	public Boolean getPublishCustomEvent() {
		return publishCustomEvent;
	}

	public void setPublishCustomEvent(Boolean publishCustomEvent) {
		this.publishCustomEvent = publishCustomEvent;
	}

	public String getCustomEventSeverity() {
		return customEventSeverity;
	}

	public void setCustomEventSeverity(String customEventSeverity) {
		this.customEventSeverity = customEventSeverity;
	}
    
	
    
}
