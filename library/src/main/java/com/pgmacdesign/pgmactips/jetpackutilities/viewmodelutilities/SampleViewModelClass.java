package com.pgmacdesign.pgmactips.jetpackutilities.viewmodelutilities;

import com.pgmacdesign.pgmactips.datamodels.SamplePojo;
import com.pgmacdesign.pgmactips.utilities.L;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Sample Usage of ViewModel and how the LiveData can be bound
 * NOTE! This class must be public
 */
public class SampleViewModelClass extends ViewModel {

    //region Static Vars
    private static final String[] TEST_STRS = {"One", "Two", "Three", "Four", "Five"};
    public static final Integer TAG_SAMPLE_POJO = -90;
    public static final Integer TAG_STRING = -91;
    public static final Integer TAG_BOOLEAN = -92;
    public static final Integer TAG_INTEGER = -93;

    //endregion

    //region Vars
    //Vars
    private MutableLiveData<Integer> randomNum;
    private MutableLiveData<String> liveString;
    private MutableLiveData<Map<Integer, Object>> liveObjects; //Int tag -- Object pair
    private int xx = 0;

    //endregion

    //region Creators

    /**
     * Setup a random number between 1000 -- 9999
     */
    public void createNumber() {
        L.m("create number");
//        this.randomNum.setValue(new Random().nextInt(8999) + 1000); //Will break if done on background thread
        this.randomNum.postValue(new Random().nextInt(8999) + 1000);
    }

    /**
     * Create a String (random from the list)
     */
    public void createString(){
        L.m("Create String");
        String s = TEST_STRS[(new Random().nextInt(TEST_STRS.length))];
//        this.liveString.setValue(s);
        this.liveString.postValue(s);
    }

    public void createListOfObjects(){
        createListOfObjects(0);
    }

    /**
     * Create a map of a random assortment of objects for testing purposes
     */
    public void createListOfObjects(int addToAge){
        L.m("createListOfObjects");
        Map<Integer, Object> aMap = new HashMap<>();
        //Sample Pojo
        SamplePojo p = new SamplePojo();
        p.setAge((1 + addToAge));
        p.setGender("Male");
        p.setName("Name");
        p.setId(123123123);
        aMap.put(TAG_SAMPLE_POJO, p);

        //String
        String s = "testing";
        aMap.put(TAG_STRING, s);

        //Integer
        Integer x = 123;
        aMap.put(TAG_INTEGER, x);

        //Bool
        Boolean b = Boolean.TRUE;
        aMap.put(TAG_BOOLEAN, b);

//        this.liveObjects.setValue(aMap);
        this.liveObjects.postValue(aMap);

    }

    //endregion

    //region Clearers
    public void clearInteger(){
        this.randomNum = null;
    }

    public void clearString(){
        this.liveString = null;
    }

    public void clearMap(){
        this.liveObjects = null;
    }

    //endregion

    //region Getters

    public MutableLiveData<Integer> getNumber(){
        if(this.randomNum == null){
            this.randomNum = new MutableLiveData<>();
            this.createNumber();
        }
        return this.randomNum;
    }
    
    public MutableLiveData<String> getRandomString(){
        if(this.liveString == null){
            this.liveString = new MutableLiveData<>();
            this.createString();
        }
        return this.liveString;
    }

    public MutableLiveData<Map<Integer, Object>> getMapData(){
        if(this.liveObjects == null){
            this.liveObjects = new MutableLiveData<>();
            this.createListOfObjects();
        }
        return this.liveObjects;
    }

    //endregion

    //region Class methods

    @Override
    protected void onCleared() {
        super.onCleared();
        this.randomNum = null;
        this.liveString = null;
        this.liveObjects = null;
        //All items cleared, clear whatever else is needed to clear here
    }



    //endregion

}
