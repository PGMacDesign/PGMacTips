package com.pgmacdesign.pgmactips.stackmanagement;


/**
 * Exception used by the {@link StackStringsManager} class. This extends {@link RuntimeException} instead
 * of {@link Exception} so as to prevent the user from wrapping every call in a try catch. See link:
 * https://stackoverflow.com/questions/4519557/is-there-a-way-to-throw-an-exception-without-adding-the-throws-declaration
 *
 */
public class StackStringsManagerException extends RuntimeException {
	
	private String errorMessage;
	private String str;
	private Integer key;
	
	@Override
	public String toString(){
		return "Error Message: " + errorMessage + ", String: "
				+ str + ", Key: " + key;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getStr() {
		return str;
	}
	
	public void setStr(String str) {
		this.str = str;
	}
	
	public Integer getKey() {
		return key;
	}
	
	public void setKey(Integer key) {
		this.key = key;
	}
}
