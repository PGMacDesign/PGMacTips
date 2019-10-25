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
	@SerializedName("someLong")
    private long someLong;
	@SerializedName("someDouble")
    private double someDouble;
	@SerializedName("someFloat")
    private float someFloat;
    @SerializedName("gender")
    private String gender;
    @SerializedName("strs")
    private List<String> strs;
    @SerializedName("fauxEnums")
    private List<MyFauxTestEnum> fauxEnums;
    @SerializedName("fauxEnum")
    private MyFauxTestEnum fauxEnum;
	@SerializedName("someArrayOfStrings")
	private String[] someArrayOfStrings;
	@SerializedName("someArrayOfInts")
	private int[] someArrayOfInts;
	@SerializedName("someArrayOfLongs")
	private long[] someArrayOfLongs;
	@SerializedName("someArrayOfDoubles")
	private double[] someArrayOfDoubles;
	@SerializedName("someArrayOfFloats")
	private float[] someArrayOfFloats;
	@SerializedName("someArray")
	private Object[] someArray;
	
	public long[] getSomeArrayOfLongs() {
		return someArrayOfLongs;
	}
	
	public void setSomeArrayOfLongs(long[] someArrayOfLongs) {
		this.someArrayOfLongs = someArrayOfLongs;
	}
	
	public double[] getSomeArrayOfDoubles() {
		return someArrayOfDoubles;
	}
	
	public void setSomeArrayOfDoubles(double[] someArrayOfDoubles) {
		this.someArrayOfDoubles = someArrayOfDoubles;
	}
	
	public float[] getSomeArrayOfFloats() {
		return someArrayOfFloats;
	}
	
	public void setSomeArrayOfFloats(float[] someArrayOfFloats) {
		this.someArrayOfFloats = someArrayOfFloats;
	}
	
	public int[] getSomeArrayOfInts() {
		return someArrayOfInts;
	}
	
	public void setSomeArrayOfInts(int[] someArrayOfInts) {
		this.someArrayOfInts = someArrayOfInts;
	}
	
	public Object[] getSomeArray() {
		return someArray;
	}
	
	public void setSomeArray(Object[] someArray) {
		this.someArray = someArray;
	}
	
	public String[] getSomeArrayOfStrings() {
		return someArrayOfStrings;
	}
	
	public void setSomeArrayOfStrings(String[] someArrayOfStrings) {
		this.someArrayOfStrings = someArrayOfStrings;
	}
	
	public MyFauxTestEnum getFauxEnum() {
		return fauxEnum;
	}
	
	public void setFauxEnum(MyFauxTestEnum fauxEnum) {
		this.fauxEnum = fauxEnum;
	}
	
	public float getSomeFloat() {
		return someFloat;
	}
	
	public void setSomeFloat(float someFloat) {
		this.someFloat = someFloat;
	}
	
	public long getSomeLong() {
		return someLong;
	}
	
	public void setSomeLong(long someLong) {
		this.someLong = someLong;
	}
	
	public double getSomeDouble() {
		return someDouble;
	}
	
	public void setSomeDouble(double someDouble) {
		this.someDouble = someDouble;
	}
	
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

    public String overideCustomSuperLongNameStringReturnThingyUsedForTesting(){
		return this.name;
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
