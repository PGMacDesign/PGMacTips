package com.pgmacdesign.pgmacutilities.pojos;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * MasterDatabaseObject Class for data de/serialization into the DB
 * Created by pmacdowell on 8/19/2016.
 */
@RealmClass
public class MasterDatabaseObject extends RealmObject {
    @PrimaryKey
    private String id;

    private String jsonString;

    public MasterDatabaseObject(){}

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
