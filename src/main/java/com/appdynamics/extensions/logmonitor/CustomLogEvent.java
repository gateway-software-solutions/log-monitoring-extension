package com.appdynamics.extensions.logmonitor;

import java.util.List;

/**
 * @author Mayank Gupta
 */

public class CustomLogEvent {
	
	public static String CUSTOM_EVENT_NAME = "CUSTOM";
	public static String CUSTOM_EVENT_TYPE_NAME = "LogMonitoringEvent";
	
	private String applicationName;
	private String severity;
	private String summary;
	private String comment;
	private String eventType = CUSTOM_EVENT_NAME;
	private String customEventType = CUSTOM_EVENT_TYPE_NAME;
	private List<String> propertyNameList;
	private List<String> propertyValueList;
	private String matchedPattern;
	private String logDisplayName;
	private String logFilePath;
	
	public String getLogFilePath() {
		return logFilePath;
	}
	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getCustomEventType() {
		return customEventType;
	}
	public void setCustomEventType(String customEventType) {
		this.customEventType = customEventType;
	}
	public List<String> getPropertyNameList() {
		return propertyNameList;
	}
	public void setPropertyNameList(List<String> propertyNameList) {
		this.propertyNameList = propertyNameList;
	}
	public List<String> getPropertyValueList() {
		return propertyValueList;
	}
	public void setPropertyValueList(List<String> propertyValueList) {
		this.propertyValueList = propertyValueList;
	}
	public String getMatchedPattern() {
		return matchedPattern;
	}
	public void setMatchedPattern(String matchedPattern) {
		this.matchedPattern = matchedPattern;
	}
	public String getLogDisplayName() {
		return logDisplayName;
	}
	public void setLogDisplayName(String logDisplayName) {
		this.logDisplayName = logDisplayName;
	}
	
	public static void setCustomEventName(String eventNameToSet) {
		if(null != eventNameToSet && !eventNameToSet.isEmpty()) {
			CUSTOM_EVENT_TYPE_NAME = eventNameToSet;
		}
	}
	

}
