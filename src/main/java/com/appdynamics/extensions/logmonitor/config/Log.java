/*
 *  Copyright 2018. AppDynamics LLC and its affiliates.
 *  All Rights Reserved.
 *  This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *  The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.logmonitor.config;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * @author Aditya Jagtiani
 */

public class Log {

    private String displayName;

    private String logDirectory;

    private String logName;

    private String encoding;

    private List<SearchString> searchStrings;
    
    private List<ExcludeString> excludeStrings;
    
    private Boolean publishCustomEvent = Boolean.TRUE;
    
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

    public String getLogDirectory() {
        return logDirectory;
    }

    public void setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public List<SearchString> getSearchStrings() {
        return searchStrings;
    }

    public void setSearchStrings(List<SearchString> searchStrings) {
        this.searchStrings = searchStrings;
    }
    

    public List<ExcludeString> getExcludeStrings() {
		return excludeStrings;
	}

	public void setExcludeStrings(List<ExcludeString> excludeStrings) {
		this.excludeStrings = excludeStrings;
	}

	public Boolean getPublishCustomEvent() {
		return publishCustomEvent;
	}

	public void setPublishCustomEvent(Boolean publishCustomEvent) {
		this.publishCustomEvent = publishCustomEvent;
	}

	@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
