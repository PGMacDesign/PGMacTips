package com.pgmacdesign.pgmacutilities.stackmanagement;

/**
 * Created by Patrick-SSD2 on 11/5/2017.
 */

public class StackManagerException extends Exception {
	
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
