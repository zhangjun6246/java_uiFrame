package com.globalegrow.util;

public class testInfo {
	public String testDescription;
	public String testCheckpoint;
	public String testCorrelation;
	public String testExceptionNote;
	public String moduleName;
	public String methodName;
	public String lastMethodName = "";
	public String methodNameFull;

	public testInfo(String moduleName) {
		this.moduleName = moduleName;
		testDescription = "";
		testCheckpoint = "";
		testCorrelation = "";
		testExceptionNote = "";
	}
}
