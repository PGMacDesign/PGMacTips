package com.pgmacdesign.pgmacutilities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pmacdowell on 8/19/2016.
 */
public class TESTINGPOJO2 extends RealmObject {
    @PrimaryKey
    private long id;
    private String name;
    private int age;
    private String gender;
    private String okie;

    public String getOkie() {
        return okie;
    }

    public void setOkie(String okie) {
        this.okie = okie;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
