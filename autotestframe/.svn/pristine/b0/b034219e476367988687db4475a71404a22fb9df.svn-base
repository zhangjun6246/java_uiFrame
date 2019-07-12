package com.globalegrow.base;

import org.apache.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.Reporter;


/**
 * TestNG retry Analyzer.
 * @author kevinkong
 *
 */
public class TestngRetry implements IRetryAnalyzer {
	private static Logger logger = Logger.getLogger(TestngRetry.class);
	private int retryCount = 1;
	private static int maxRetryCount = 3;
	
	@Override
	public boolean retry(ITestResult result) {  
	    if (retryCount < maxRetryCount) {
	        retryCount++;
	        return true;
	    }
	    return false;
	}
	
}
