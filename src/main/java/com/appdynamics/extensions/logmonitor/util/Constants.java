/*
 *  Copyright 2018. AppDynamics LLC and its affiliates.
 *  All Rights Reserved.
 *  This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *  The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */
package com.appdynamics.extensions.logmonitor.util;

/**
 * @author Aditya Jagtiani
 */

public final class Constants {
    public static final String FILEPOINTER_FILENAME = "filepointer.json";
    public static final String METRIC_SEPARATOR = "|";
    public static final String SEARCH_STRING = "Search String";
    public static final String EXCLUDE_STRING = "Exclude String";
    public static final String FILESIZE_METRIC_NAME = "File size (Bytes)";
    public static final String DEFAULT_METRIC_PREFIX = "Custom Metrics|Log Monitor|";
    public static final String MONITOR_NAME = "Log Monitor";
    public static final String OCCURRENCES = "Occurrences";
    public static final String EXCLUDES = "Excludes";
    public static final String MATCHES = "Matches";
    public static final String SCHEMA_NAME = "LogSchema";
    
    public static final String PUBLISH_CUSTOM_EVENT_GLOBAL_PROPERTY = "sendMatchedLinesAsCustomEvent";
    public static final String PUBLISH_CUSTOM_EVENT_LOGFILE_PROPERTY = "sendMatchedLinesAsCustomEvent";
    public static final String PUBLISH_CUSTOM_EVENT_PATTERN_PROPERTY = "sendMatchedLinesAsCustomEvent";
    
    public static final int MAX_LENGTH_FOR_CUSTOM_EVENT_SUMMARY = 5000;
    
    public static final String CUSTOM_EVENT_SEVERITY_ERROR = "ERROR";
    public static final String CUSTOM_EVENT_SEVERITY_WARNING = "WARN";
    public static final String CUSTOM_EVENT_SEVERITY_INFO = "INFO";
    
    public static final String CUSTOM_EVENT_SEVERITY_DEFAULT = CUSTOM_EVENT_SEVERITY_WARNING;
    
    public static final String CUSTOM_EVENT_SEVERITY_PROPERTY = "severity";
    
    public static final String DEFAULT_APPLICATION = "Server & Infrastructure Monitoring";
    
}
