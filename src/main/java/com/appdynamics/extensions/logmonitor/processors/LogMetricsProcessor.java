/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.logmonitor.processors;

import static com.appdynamics.extensions.logmonitor.util.Constants.EXCLUDES;
import static com.appdynamics.extensions.logmonitor.util.Constants.EXCLUDE_STRING;
import static com.appdynamics.extensions.logmonitor.util.Constants.FILESIZE_METRIC_NAME;
import static com.appdynamics.extensions.logmonitor.util.Constants.MATCHES;
import static com.appdynamics.extensions.logmonitor.util.Constants.METRIC_SEPARATOR;
import static com.appdynamics.extensions.logmonitor.util.Constants.OCCURRENCES;
import static com.appdynamics.extensions.logmonitor.util.Constants.SEARCH_STRING;
import static com.appdynamics.extensions.logmonitor.util.LogMonitorUtil.closeRandomAccessFile;
import static com.appdynamics.extensions.logmonitor.util.LogMonitorUtil.createExcludePattern;
import static com.appdynamics.extensions.logmonitor.util.LogMonitorUtil.createPattern;
import static com.appdynamics.extensions.logmonitor.util.LogMonitorUtil.getCurrentFileCreationTimeStamp;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bitbucket.kienerj.OptimizedRandomAccessFile;
import org.slf4j.Logger;

import com.appdynamics.extensions.eventsservice.EventsServiceDataManager;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.logmonitor.CustomLogEvent;
import com.appdynamics.extensions.logmonitor.config.FilePointer;
import com.appdynamics.extensions.logmonitor.config.Log;
import com.appdynamics.extensions.logmonitor.config.SearchPattern;
import com.appdynamics.extensions.logmonitor.metrics.LogMetrics;
import com.appdynamics.extensions.metrics.Metric;
import com.appdynamics.extensions.util.MetricPathUtils;

/**
 * @author Aditya Jagtiani
 */

public class LogMetricsProcessor implements Runnable {
    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(LogMetricsProcessor.class);
    private OptimizedRandomAccessFile randomAccessFile;
    private Log log;
    private CountDownLatch latch;
    private File currentFile;
    private List<SearchPattern> searchPatterns;
    private List<SearchPattern> excludePatterns;
    private Map<Pattern, String> replacers;
    private LogMetrics logMetrics;
    private EventsServiceDataManager eventsServiceDataManager;
    private LogEventsProcessor logEventsProcessor;
    private boolean publishCustomEvent;
    private int offset;

    LogMetricsProcessor(OptimizedRandomAccessFile randomAccessFile, Log log, CountDownLatch latch, LogMetrics logMetrics,
                        File currentFile, EventsServiceDataManager eventsServiceDataManager,boolean publishCustomEvent,
                        int offset) {
        this.randomAccessFile = randomAccessFile;
        this.log = log;
        this.latch = latch;
        this.logMetrics = logMetrics;
        this.currentFile = currentFile;
        this.searchPatterns = createPattern(this.log.getSearchStrings());
        if(null != this.log.getExcludeStrings() && this.log.getExcludeStrings().size() > 0) {
        	this.excludePatterns = createExcludePattern(this.log.getExcludeStrings());
        }
        this.eventsServiceDataManager = eventsServiceDataManager;
        this.publishCustomEvent = publishCustomEvent;
        this.offset = offset;
    }

    public void run() {
        try {
            processLogFile();
        } catch (Exception ex) {
            LOGGER.error("Error encountered while processing log file : {}", log.getDisplayName(), ex);
        } finally {
            closeRandomAccessFile(randomAccessFile);
            latch.countDown();
        }
    }

    private void processLogFile() throws Exception {
        long currentFilePointer = randomAccessFile.getFilePointer();
        String currentLine;
        setBaseOccurrenceCountForConfiguredPatterns();
        setBaseOccurrenceCountForExcludePatterns();
        if (eventsServiceDataManager != null) {
            logEventsProcessor = new LogEventsProcessor(eventsServiceDataManager, offset, log);
        }
        while ((currentLine = randomAccessFile.readLine()) != null) {
            incrementWordCountIfSearchStringMatched(searchPatterns, excludePatterns, currentLine);
            currentFilePointer = randomAccessFile.getFilePointer();
        }
        long currentFileCreationTime = getCurrentFileCreationTimeStamp(currentFile);
        String metricName = getLogNamePrefix() + FILESIZE_METRIC_NAME;
        logMetrics.add(metricName, new Metric(metricName,
                String.valueOf(randomAccessFile.length()), logMetrics.getMetricPrefix() + METRIC_SEPARATOR
                + metricName));
        updateCurrentFilePointer(currentFile.getPath(), currentFilePointer, currentFileCreationTime);
        LOGGER.info(String.format("Successfully processed log file [%s]",
                randomAccessFile));
    }

    private void setBaseOccurrenceCountForConfiguredPatterns() {
        for (SearchPattern searchPattern : searchPatterns) {
            String currentKey = getSearchStringPrefix() + searchPattern.getDisplayName() + METRIC_SEPARATOR;
            String metricName = currentKey + OCCURRENCES;
            logMetrics.add(metricName, new Metric(metricName, String.valueOf(BigInteger.ZERO),
                    logMetrics.getMetricPrefix() + METRIC_SEPARATOR + metricName));
        }
    }
    
    private void setBaseOccurrenceCountForExcludePatterns() {
            String currentKey = getExcludeStringPrefix();
            String metricName = currentKey + EXCLUDES;
            logMetrics.add(metricName, new Metric(metricName, String.valueOf(BigInteger.ZERO),
                    logMetrics.getMetricPrefix() + METRIC_SEPARATOR + metricName));
    }

//    private void incrementWordCountIfSearchStringMatched(List<SearchPattern> searchPatterns, String stringToCheck) {
//        for (SearchPattern searchPattern : searchPatterns) {
//            Matcher matcher = searchPattern.getPattern().matcher(stringToCheck);
//            String currentKey = getSearchStringPrefix() + searchPattern.getDisplayName() + METRIC_SEPARATOR;
//
//            while (matcher.find()) {
//                BigInteger occurrences = new BigInteger(logMetrics.getMetrics().get(currentKey + OCCURRENCES)
//                        .getMetricValue());
//                String metricName = currentKey + OCCURRENCES;
//                LOGGER.info("Match found for pattern: {} in log: {}", searchPattern.getDisplayName(), log.getDisplayName());
//                logMetrics.add(metricName, new Metric(metricName, String.valueOf(occurrences.add(BigInteger.ONE)),
//                        logMetrics.getMetricPrefix() + METRIC_SEPARATOR + metricName));
//
//                if (searchPattern.getPrintMatchedString()) {
//                    String path;
//                    LOGGER.info("Adding actual matches to the queue for printing for log: {}", log.getDisplayName());
//                    String replacedWord = matcher.group().trim();
//                    if (searchPattern.getCaseSensitive()) {
//                        metricName = currentKey+MATCHES+METRIC_SEPARATOR+replacedWord;
//                        path = MetricPathUtils.buildMetricPath(currentKey,MATCHES,replacedWord);
//                    } else {
//                        metricName = currentKey+MATCHES+METRIC_SEPARATOR+WordUtils.capitalizeFully(replacedWord);
//                        path = MetricPathUtils.buildMetricPath(currentKey,MATCHES,WordUtils.capitalizeFully(replacedWord));
//                    }
//                    logMetrics.add(metricName, logMetrics.getMetricPrefix() + METRIC_SEPARATOR + path);
//                }
//
//                if (logEventsProcessor != null) {
//                    logMetrics.addLogEvent(logEventsProcessor.processLogEvent(searchPattern, randomAccessFile, stringToCheck));
//                } else {
//                    LOGGER.info("This data does not have to be sent to the events service, skipping.");
//                }
//            }
//        }
//    }
    
    private void incrementWordCountIfSearchStringMatched(List<SearchPattern> searchPatterns, List<SearchPattern> excludePatterns, String stringToCheck) {
    	
    	boolean exclude = false;
    	if(null != excludePatterns && excludePatterns.size() > 0) {
    		for(SearchPattern excludePattern : excludePatterns) {
    			Matcher excludeMatcher = excludePattern.getPattern().matcher(stringToCheck);
    			if(excludeMatcher.find()) {
    				exclude = true;
    				LOGGER.debug("The string is being excluded as it matches one of exclude patterns. {}",excludePattern.getPattern());
    				
    				String excludeKey = getExcludeStringPrefix();
    				String excludeMetricName = excludeKey + EXCLUDES;
    				BigInteger excludes = new BigInteger(logMetrics.getMetrics().get(excludeMetricName)
                            .getMetricValue());
    				logMetrics.add(excludeMetricName, new Metric(excludeMetricName, String.valueOf(excludes.add(BigInteger.ONE)),
                            logMetrics.getMetricPrefix() + METRIC_SEPARATOR + excludeMetricName));
    				break;
    			}
    		}
    	}
    	
    	if(!exclude) {
	        for (SearchPattern searchPattern : searchPatterns) {
	            Matcher matcher = searchPattern.getPattern().matcher(stringToCheck);
	            String currentKey = getSearchStringPrefix() + searchPattern.getDisplayName() + METRIC_SEPARATOR;
	
	            while (matcher.find()) {
	                BigInteger occurrences = new BigInteger(logMetrics.getMetrics().get(currentKey + OCCURRENCES)
	                        .getMetricValue());
	                String metricName = currentKey + OCCURRENCES;
	                LOGGER.info("Match found for pattern: {} in log: {}", searchPattern.getDisplayName(), log.getDisplayName());
	                logMetrics.add(metricName, new Metric(metricName, String.valueOf(occurrences.add(BigInteger.ONE)),
	                        logMetrics.getMetricPrefix() + METRIC_SEPARATOR + metricName));
	
	                if (searchPattern.getPrintMatchedString()) {
	                    String path;
	                    LOGGER.info("Adding actual matches to the queue for printing for log: {}", log.getDisplayName());
	                    String replacedWord = matcher.group().trim();
	                    if (searchPattern.getCaseSensitive()) {
	                        metricName = currentKey+MATCHES+METRIC_SEPARATOR+replacedWord;
	                        path = MetricPathUtils.buildMetricPath(currentKey,MATCHES,replacedWord);
	                    } else {
	                        metricName = currentKey+MATCHES+METRIC_SEPARATOR+WordUtils.capitalizeFully(replacedWord);
	                        path = MetricPathUtils.buildMetricPath(currentKey,MATCHES,WordUtils.capitalizeFully(replacedWord));
	                    }
	                    logMetrics.add(metricName, logMetrics.getMetricPrefix() + METRIC_SEPARATOR + path);
	                }
	
	                if (logEventsProcessor != null) {
	                    logMetrics.addLogEvent(logEventsProcessor.processLogEvent(searchPattern, randomAccessFile, stringToCheck));
	                } else {
	                    LOGGER.info("This data does not have to be sent to the events service, skipping.");
	                }
	                
	                if(isPublishCustomEvent() && log.getPublishCustomEvent() && searchPattern.isPublishCustomEvent()) {
	                	
	                	CustomLogEvent customLogEvent = new CustomLogEvent();
	                	customLogEvent.setSeverity(searchPattern.getCustomEventSeverity());
	                	customLogEvent.setSummary(stringToCheck);
	                	customLogEvent.setMatchedPattern(String.format("%s (%s)",
	                			searchPattern.getDisplayName(),searchPattern.getPattern().toString()));
	                	customLogEvent.setLogDisplayName(log.getDisplayName());
	                	
	                	if(!StringUtils.isBlank(searchPattern.getApplicationName())) {
	                		customLogEvent.setApplicationName(searchPattern.getApplicationName());
	                	}else if(!StringUtils.isBlank(log.getApplicationName())) {
	                		customLogEvent.setApplicationName(log.getApplicationName());
	                	}
	                	
	                	customLogEvent.setLogFilePath(currentFile.getAbsolutePath());
	                	
	                	logMetrics.addCustomLogEvent(customLogEvent);
	                	
	                }else {
	                	LOGGER.info("This data does not have to be sent as custom event, skipping.");
	                }
	                
	            }
	        }
    	}
    }

    private void updateCurrentFilePointer(String filePath, long lastReadPosition, long creationTimestamp) {
        FilePointer filePointer = new FilePointer();
        filePointer.setFilename(filePath);
        filePointer.setFileCreationTime(creationTimestamp);
        filePointer.updateLastReadPosition(lastReadPosition);
        logMetrics.updateFilePointer(filePointer);
    }

    private String getExcludeStringPrefix() {
        return String.format("%s%s%s", getLogNamePrefix(),
                EXCLUDE_STRING, METRIC_SEPARATOR);
    }
    
    private String getSearchStringPrefix() {
        return String.format("%s%s%s", getLogNamePrefix(),
                SEARCH_STRING, METRIC_SEPARATOR);
    }

    private String getLogNamePrefix() {
        String displayName = StringUtils.isBlank(log.getDisplayName()) ?
                log.getLogName() : log.getDisplayName();
        return displayName + METRIC_SEPARATOR;
    }

	public boolean isPublishCustomEvent() {
		return publishCustomEvent;
	}
    
    
}