package com.pgmacdesign.pgmacutilities.stackmanagement;

/**
 * Exception used by the {@link StackManager} class. This extends {@link RuntimeException} instead
 * of {@link Exception} so as to prevent the user from wrapping every call in a try catch. See link:
 * https://stackoverflow.com/questions/4519557/is-there-a-way-to-throw-an-exception-without-adding-the-throws-declaration
 * Created by Patrick-SSD2 on 11/5/2017.
 */
public class StackManagerException extends RuntimeException {
	
	private String errorMessage;
	private String enumToString;
	private Integer key;
	
	@Override
	public String toString(){
		return "Error Message: " + errorMessage + ", Enum: "
				+ enumToString + ", Key: " + key;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getEnumToString() {
		return enumToString;
	}
	
	public void setEnumToString(String enumToString) {
		this.enumToString = enumToString;
	}
	
	public Integer getKey() {
		return key;
	}
	
	public void setKey(Integer key) {
		this.key = key;
	}
}
