/*
 *  Copyright 2018. AppDynamics LLC and its affiliates.
 *  All Rights Reserved.
 *  This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *  The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.logmonitor.config;

import java.util.regex.Pattern;

/**
 * @author Aditya Jagtiani
 * @author mayank
 */
public class SearchPattern {

    private String displayName;
    private Pattern pattern;
    private boolean caseSensitive;
    private boolean printMatchedString;
    private boolean publishCustomEvent = true;
    private String customEventSeverity;
    private String applicationName;

//    public SearchPattern(String displayName, Pattern pattern, Boolean caseSensitive, Boolean printMatchedString) {
//        this.displayName = displayName;
//        this.pattern = pattern;
//        this.caseSensitive = caseSensitive;
//        this.printMatchedString = printMatchedString;
//    }
    
    public SearchPattern(String displayName, Pattern pattern, Boolean caseSensitive, Boolean printMatchedString,Boolean publishCustomEvent,String customEventSeverity) {
        this.displayName = displayName;
        this.pattern = pattern;
        this.caseSensitive = caseSensitive;
        this.printMatchedString = printMatchedString;
        if(null != publishCustomEvent)
        	this.publishCustomEvent = publishCustomEvent;
        this.customEventSeverity = customEventSeverity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean getCaseSensitive() {
        return caseSensitive;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public boolean getPrintMatchedString() {
        return printMatchedString;
    }

	public boolean isPublishCustomEvent() {
		return publishCustomEvent;
	}

	public String getCustomEventSeverity() {
		return customEventSeverity;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
    
	
   
    
}