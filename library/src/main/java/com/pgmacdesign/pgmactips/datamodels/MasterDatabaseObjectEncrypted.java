package com.pgmacdesign.pgmactips.datamodels;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MasterDatabaseObjectEncrypted extends RealmObject {
	@PrimaryKey
	@SerializedName("id")
	private String id;
	
	@SerializedName("jsonString")
	private String jsonString;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getJsonString() {
		return jsonString;
	}
	
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
}