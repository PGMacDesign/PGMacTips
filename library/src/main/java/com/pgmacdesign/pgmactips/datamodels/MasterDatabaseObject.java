package com.pgmacdesign.pgmactips.datamodels;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Class used in the {@link com.pgmacdesign.pgmactips.utilities.DatabaseUtilities} class
 * to write to a table. This class is used internally
 * Created by Patrick-SSD2 on 11/5/2017.
 */

public class MasterDatabaseObject extends RealmObject  {
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