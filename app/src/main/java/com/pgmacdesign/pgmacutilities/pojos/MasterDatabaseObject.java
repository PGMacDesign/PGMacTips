package com.pgmacdesign.pgmacutilities.pojos;

import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pmacdowell on 8/18/2016.
 */
public class MasterDatabaseObject extends RealmObject{
    @PrimaryKey
    private long id;
    private Map<String, Object> mapObjects;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Map<String, Object> getMapObjects() {
        return mapObjects;
    }

    public void setMapObjects(Map<String, Object> mapObjects) {
        this.mapObjects = mapObjects;
    }
}
