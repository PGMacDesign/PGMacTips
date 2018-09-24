package com.pgmacdesign.pgmactips.datamodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Sample POJO that demonstrates serialization
 * Created by pmacdowell on 8/18/2016.
 */
public class SamplePojo {


    public static enum MyFauxTestEnum {
        One, Two, Three
    }

	@SerializedName("id")
    private long id;
	@SerializedName("name")
    private String name;
	@SerializedName("age")
    private int age;
    @SerializedName("gender")
    private String gender;
    @SerializedName("strs")
    private List<String> strs;
    @SerializedName("fauxEnums")
    private List<MyFauxTestEnum> fauxEnums;

    public List<MyFauxTestEnum> getFauxEnums() {
        return fauxEnums;
    }

    public void setFauxEnums(List<MyFauxTestEnum> fauxEnums) {
        this.fauxEnums = fauxEnums;
    }

    public List<String> getStrs() {
        return strs;
    }

    public void setStrs(List<String> strs) {
        this.strs = strs;
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
