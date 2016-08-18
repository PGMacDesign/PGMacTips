package com.pgmacdesign.pgmacutilities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pmacdowell on 8/18/2016.
 */
public class TESTINGPOJO extends RealmObject {
    @PrimaryKey
    private long id;
    private String name;
    private int age;
    private String gender;

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
