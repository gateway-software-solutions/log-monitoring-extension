/*
 *  Copyright 2018. AppDynamics LLC and its affiliates.
 *  All Rights Reserved.
 *  This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *  The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.logmonitor;

import static com.appdynamics.extensions.logmonitor.util.Constants.SCHEMA_NAME;
import static com.appdynamics.extensions.logmonitor.util.LogMonitorUtil.getFinalMetricList;
import static com.appdynamics.extensions.logmonitor.util.LogMonitorUtil.prepareEventsForPublishing;

import java.util.List;

import org.slf4j.Logger;

import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.logmonitor.config.Log;
import com.appdynamics.extensions.logmonitor.metrics.LogMetrics;
import com.appdynamics.extensions.logmonitor.processors.CustomEventProcessor;
import com.appdynamics.extensions.logmonitor.processors.FilePointerProcessor;
import com.appdynamics.extensions.logmonitor.processors.LogFileManager;

/**
 * @author Aditya Jagtiani
 */

public class LogMonitorTask implements AMonitorTaskRunnable {
    private static Logger LOGGER = ExtensionsLoggerFactory.getLogger(LogMonitorTask.class);
    private MetricWriteHelper metricWriteHelper;
    private MonitorContextConfiguration monitorContextConfiguration;
    private Log log;
    private FilePointerProcessor filePointerProcessor;

    public LogMonitorTask(MonitorContextConfiguration monitorContextConfiguration, MetricWriteHelper metricWriteHelper,
                          Log log, FilePointerProcessor filePointerProcessor) {
        this.monitorContextConfiguration = monitorContextConfiguration;
        this.metricWriteHelper = metricWriteHelper;
        this.log = log;
        this.filePointerProcessor = filePointerProcessor;
    }

    public void run() {
        try {
            populateAndPrintMetrics();
        } catch (Exception ex) {
            LOGGER.error("Log monitoring task failed for Log: " + log.getDisplayName(), ex);
        }
    }

    public void onTaskComplete() {
        LOGGER.info("Completed the Log Monitoring task for log : " + log.getDisplayName());
    }

    private void populateAndPrintMetrics() throws Exception {
        LogFileManager logFileManager = new LogFileManager(filePointerProcessor, log, monitorContextConfiguration);
        LogMetrics logMetrics = logFileManager.processLogMetrics();
        publishEvents(logMetrics);
        LOGGER.info("Number of custom events to be published {}",logMetrics.getCustomEventsToBePublished().size());
        LOGGER.info("Printing {} metrics for Log {}", logMetrics.getMetrics().size(), log.getDisplayName());
        metricWriteHelper.transformAndPrintMetrics(getFinalMetricList(logMetrics.getMetrics()));
        filePointerProcessor.updateFilePointerFile();
        publishCustomEvents(logMetrics);
    }

    private void publishEvents(LogMetrics logMetrics) {
        List<LogEvent> events = logMetrics.getEventsToBePublished();
        if(events.size() != 0) {
            List<String> eventsToBePublished = prepareEventsForPublishing(events);
            monitorContextConfiguration.getContext().getEventsServiceDataManager().publishEvents(SCHEMA_NAME, eventsToBePublished);
        }
        else {
            LOGGER.info("No events to publish for log {}, skipping", log.getDisplayName());
        }
    }
    
    private void publishCustomEvents(LogMetrics logMetrics) {
    	
    	List<CustomLogEvent> events = logMetrics.getCustomEventsToBePublished();
    	
    	if(null != events && events.size() != 0) {
    		CustomEventProcessor  customEventProcessor = new CustomEventProcessor(this.monitorContextConfiguration);
    		
    		int publishCount = 0;
    		for (CustomLogEvent customLogEvent : events) {
    			publishCount = publishCount + customEventProcessor.postCustomEvent(customLogEvent);
			}
    		
    		LOGGER.info("******** Number of custom events to be published for {} is {}",log.getDisplayName(),events.size());
    		LOGGER.info("******** Number of custom events successfully published for {} is {}",log.getDisplayName(),publishCount);
    		
    	}
    	
    }
}