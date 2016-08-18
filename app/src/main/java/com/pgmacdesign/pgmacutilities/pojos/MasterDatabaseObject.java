package com.pgmacdesign.pgmacutilities.pojos;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by pmacdowell on 8/18/2016.
 */
@RealmClass
public class MasterDatabaseObject extends RealmObject{
    //@PrimaryKey
    //private long id;
    @PrimaryKey
    private String id;
    //String is the package name and the object is the object to place
    //@SerializedName("mapObjects")

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
