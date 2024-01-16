package com.appdynamics.extensions.logmonitor.processors;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.controller.ControllerInfo;
import com.appdynamics.extensions.controller.CookiesCsrf;
import com.appdynamics.extensions.http.HttpClientUtils;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.logmonitor.CustomLogEvent;
import com.appdynamics.extensions.logmonitor.util.Constants;

public class CustomEventProcessor {
	
	private static final Logger logger = ExtensionsLoggerFactory.getLogger(CustomEventProcessor.class);
	private ControllerInfo controllerInfo;
    private CloseableHttpClient httpClient;
    private String baseURL;
	private CookiesCsrf cookiesCsrf;
	
	
	public CustomEventProcessor(MonitorContextConfiguration monitorContextConfiguration ) {
		
		this.controllerInfo = monitorContextConfiguration.getContext().getControllerInfo();
		setBaseURL(monitorContextConfiguration.getContext().getControllerClient().getBaseURL());
		setHttpClient(monitorContextConfiguration.getContext().getControllerClient().getHttpClient());
		
	}
	
	
/*	public void postCustomEvent() {
		
		CloseableHttpResponse response = null;
		
		try {
			
			StringBuilder restAPIPath = new StringBuilder();
			restAPIPath.append(getBaseURL());
			restAPIPath.append("controller/rest/applications/");
			restAPIPath.append(controllerInfo.getApplicationName());
			restAPIPath.append("/events");
			
			HttpPost httpPost = new HttpPost(getBaseURL() + "controller/rest/applications/Test/events");
			
			final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		    final UsernamePasswordCredentials credentials = 
		        new UsernamePasswordCredentials(controllerInfo.getUsername()+"@"+controllerInfo.getAccount(), controllerInfo.getPassword());
		    
		    credsProvider.setCredentials(AuthScope.ANY, credentials);
			
			final List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("severity", "ERROR"));
			params.add(new BasicNameValuePair("summary", "the log line found in the file"));
			params.add(new BasicNameValuePair("comment", "Event generated using log extension"));
			params.add(new BasicNameValuePair("eventtype", "CUSTOM"));
			params.add(new BasicNameValuePair("customeventtype", "LogMonitoringEvent"));
			params.add(new BasicNameValuePair("propertynames", "patternRegexMatched"));
			params.add(new BasicNameValuePair("propertyvalues", "regexMatchedCanBeShownHere"));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			
			httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
			
			response = (CloseableHttpResponse) httpClient.execute(httpPost);
			
			StatusLine statusLine = response.getStatusLine();
            String responseString = null;
            if (statusLine != null && statusLine.getStatusCode() == 200) {
                responseString = EntityUtils.toString(response.getEntity());
                logger.debug("Response for url [{}] is [{}]", getBaseURL(), responseString);
            } else if (statusLine != null) {
                logger.error("The controller API returned an invalid response {}"
                        , statusLine.getStatusCode());
            }
			
		}catch(Throwable e){
			logger.error("ERROR WHILE POSTING CUSTOM EVENT", e);
		}finally {
            HttpClientUtils.closeHttpResponse(response);
        }
	}
*/
	public int postCustomEvent(CustomLogEvent customLogEvent) {
		
		CloseableHttpResponse response = null;
		int status = 0;
		
		try {
			
			StringBuilder restAPIPath = new StringBuilder();
			restAPIPath.append(getBaseURL());
			restAPIPath.append("controller/rest/applications/");
			if(!StringUtils.isBlank(customLogEvent.getApplicationName())) {
				restAPIPath.append(customLogEvent.getApplicationName());
			}else if(!StringUtils.isBlank(controllerInfo.getApplicationName())) {
				restAPIPath.append(controllerInfo.getApplicationName());
			}else {
				logger.warn("Application is neither set in config.yml nor in controller-info.xml. Using default value of {}",Constants.DEFAULT_APPLICATION);
				restAPIPath.append(Constants.DEFAULT_APPLICATION);
			}
			restAPIPath.append("/events");
			
			logger.debug("Posting custom event at: {}",restAPIPath.toString());
			
			HttpPost httpPost = new HttpPost(restAPIPath.toString());
			
			final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		    final UsernamePasswordCredentials credentials = 
		        new UsernamePasswordCredentials(controllerInfo.getUsername()+"@"+controllerInfo.getAccount(), controllerInfo.getPassword());
		    
		    credsProvider.setCredentials(AuthScope.ANY, credentials);
			
			final List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("severity", customLogEvent.getSeverity()));
			params.add(new BasicNameValuePair("summary", getSummaryMeesage(customLogEvent)));
			params.add(new BasicNameValuePair("comment", String.format("Event generated using log extension.\nTo set policy for this event, use %s as custom event type.",customLogEvent.getCustomEventType())));
			params.add(new BasicNameValuePair("eventtype", customLogEvent.getEventType()));
			params.add(new BasicNameValuePair("customeventtype", customLogEvent.getCustomEventType()));
			params.add(new BasicNameValuePair("propertynames", "patternRegexMatched"));
			params.add(new BasicNameValuePair("propertyvalues", customLogEvent.getMatchedPattern()));
			params.add(new BasicNameValuePair("propertynames", "logFilePath"));
			params.add(new BasicNameValuePair("propertyvalues", customLogEvent.getLogFilePath()));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			
			httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
			
			response = (CloseableHttpResponse) httpClient.execute(httpPost);
			
			StatusLine statusLine = response.getStatusLine();
            String responseString = null;
            if (statusLine != null && statusLine.getStatusCode() == 200) {
                responseString = EntityUtils.toString(response.getEntity());
                logger.debug("Response for url [{}] is [{}]", restAPIPath.toString(), responseString);
                status = 1;
            } else if (statusLine != null) {
                logger.error("The controller API returned an invalid response {}"
                        , statusLine.getStatusCode());
                logger.error("The body posted to controller {}"
                        , EntityUtils.toString(httpPost.getEntity()));
            }
			
		}catch(Throwable e){
			logger.error("ERROR WHILE POSTING CUSTOM EVENT", e);
		}finally {
            HttpClientUtils.closeHttpResponse(response);
        }
		return status;
	}


	public String getBaseURL() {
		return baseURL;
	}


	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}


	public CookiesCsrf getCookiesCsrf() {
		return cookiesCsrf;
	}


	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}


	public void setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	private String getSummaryMeesage(CustomLogEvent customLogEvent) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Nodename: ");
		builder.append(controllerInfo.getNodeName().trim());
		builder.append(System.lineSeparator());
		builder.append("Log: ");
		builder.append(customLogEvent.getLogDisplayName().trim());
		builder.append(System.lineSeparator());
		builder.append("LogFile: ");
		builder.append(customLogEvent.getLogFilePath().trim());
		builder.append(System.lineSeparator());
		builder.append("Pattern: ");
		builder.append(customLogEvent.getMatchedPattern().trim());
		builder.append(System.lineSeparator());
		builder.append("MatchedLine: ");
		builder.append(customLogEvent.getSummary().trim());
//		builder.append(System.lineSeparator());
		
		logger.debug("the event summary to post: \n{}",builder.toString());
		
		return builder.toString();
	}
	

}
