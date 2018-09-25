package com.pgmacdesign.pgmactips.unuseddeprecatedcode;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.gson.annotations.SerializedName;
import com.pgmacdesign.pgmactips.utilities.DatabaseUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Class is a work in progress, will be updating soon
 * Created by pmacdowell on 9/25/2018.
 */
class InstanceStateManagement {

    //region Vars
    private Context context;
    private DatabaseUtilities dbUtils;
    //endregion

    //region Constructors && Init

    /**
     * Constructor for use with Context
     * @param context
     */
    public InstanceStateManagement(@NonNull Context context){
        this.context = context;
        this.dbUtils = null;
        this.init();
    }

    /**
     * Overloaded Constructor to allow for custom dbUtils instance
     * @param dbUtilitiesInstance {@link DatabaseUtilities}
     */
    public InstanceStateManagement(@NonNull DatabaseUtilities dbUtilitiesInstance){
        this.context = null;
        this.dbUtils = dbUtilitiesInstance;
        this.init();
    }

    private void init(){
        if(this.context == null && this.dbUtils == null){
            //todo stuff?
        }
        if(this.dbUtils == null && this.context != null){
            this.dbUtils = new DatabaseUtilities(context);
        }

        //More stuff will go here
    }
    //endregion

    //region InstanceStatePojoParent Map Management

    /**
     * Get the Map of String, {@link InstanceStatePojoParent.InstanceStatePojo} objects to use.
     * If the Map is null or empty, will return an Hashmap
     * (will never return null)
     * @return Map of String, {@link InstanceStatePojoParent.InstanceStatePojo}
     */
    public Map<String, InstanceStatePojoParent.InstanceStatePojo> getInstanceStatePojoMap(){
        InstanceStatePojoParent p = getParentPojo();
        Map<String, InstanceStatePojoParent.InstanceStatePojo> p2 = p.getInstanceStatePojosMap();
        return (p2 == null) ? new HashMap<String, InstanceStatePojoParent.InstanceStatePojo>() : p2;
    }

    /**
     * Set a new Instance State pojo Map to the currently persisted {@link java.util.Map}
     * (Overwrite the existing Map)
     * @param pojo {@link InstanceStatePojoParent.InstanceStatePojo}
     * @return boolean, true if it saved successfully, false if it did not
     */
    public boolean setInstanceStateMap(Map<String, InstanceStatePojoParent.InstanceStatePojo> pojo){
        InstanceStatePojoParent p = getParentPojo();
        p.setInstanceStatePojosMap(pojo);
        return (dbUtils.persistObject(InstanceStatePojoParent.class, p));
    }

    /**
     * Set a new Instance State pojo to the currently persisted {@link java.util.Map}
     * (Overwrite the existing Map)
     * @param str Key to use in identifying the value
     * @param pojo {@link InstanceStatePojoParent.InstanceStatePojo} value
     * @return boolean, true if it saved successfully, false if it did not
     */
    public boolean setInstanceStateMap(@NonNull String str,
                                       @NonNull InstanceStatePojoParent.InstanceStatePojo pojo){
        InstanceStatePojoParent p = getParentPojo();
        p.setInstanceStatePojosMap(str, pojo);
        return (dbUtils.persistObject(InstanceStatePojoParent.class, p));
    }

    /**
     * Append a new Instance State pojo to the currently persisted {@link java.util.List}
     * (Will not overwrite the existing Map)
     * @param str Key to use in identifying the value
     * @param pojo {@link InstanceStatePojoParent.InstanceStatePojo}
     * @return boolean, true if it saved successfully, false if it did not
     */
    public boolean appendToInstanceStateMap(@NonNull String str,
                                            @NonNull InstanceStatePojoParent.InstanceStatePojo pojo){
        InstanceStatePojoParent p = getParentPojo();
        p.appendToInstanceStatePojosMap(str, pojo);
        return (dbUtils.persistObject(InstanceStatePojoParent.class, p));
    }
    //endregion

    //region InstanceStatePojoParent List Management

    /**
     * Get the list of {@link InstanceStatePojoParent.InstanceStatePojo} objects to use.
     * If the list is null or empty, will return an empty list
     * (will never return null)
     * @return List of {@link InstanceStatePojoParent.InstanceStatePojo}
     */
    public List<InstanceStatePojoParent.InstanceStatePojo> getInstanceStatePojoList(){
        InstanceStatePojoParent p = getParentPojo();
        List<InstanceStatePojoParent.InstanceStatePojo> p2 = p.getInstanceStatePojosList();
        return (p2 == null) ? new ArrayList<InstanceStatePojoParent.InstanceStatePojo>() : p2;
    }

    /**
     * Set a new Instance State pojo list to the currently persisted {@link java.util.List}
     * (Overwrite the existing list)
     * @param pojo {@link InstanceStatePojoParent.InstanceStatePojo}
     * @return boolean, true if it saved successfully, false if it did not
     */
    public boolean setInstanceStateList(List<InstanceStatePojoParent.InstanceStatePojo> pojo){
        InstanceStatePojoParent p = getParentPojo();
        p.setInstanceStatePojosList(pojo);
        return (dbUtils.persistObject(InstanceStatePojoParent.class, p));
    }

    /**
     * Set a new Instance State pojo to the currently persisted {@link java.util.List}
     * (Overwrite the existing list)
     * @param pojo {@link InstanceStatePojoParent.InstanceStatePojo}
     * @return boolean, true if it saved successfully, false if it did not
     */
    public boolean setInstanceStateList(InstanceStatePojoParent.InstanceStatePojo pojo){
        InstanceStatePojoParent p = getParentPojo();
        p.setInstanceStatePojosList(pojo);
        return (dbUtils.persistObject(InstanceStatePojoParent.class, p));
    }

    /**
     * Append a new Instance State pojo to the currently persisted {@link java.util.List}
     * (Will not overwrite the existing list)
     * @param pojo {@link InstanceStatePojoParent.InstanceStatePojo}
     * @return boolean, true if it saved successfully, false if it did not
     */
    public boolean appendToInstanceStateList(InstanceStatePojoParent.InstanceStatePojo pojo){
        InstanceStatePojoParent p = getParentPojo();
        p.appendToInstanceStatePojosList(pojo);
        return (dbUtils.persistObject(InstanceStatePojoParent.class, p));
    }
    //endregion

    //region Simple Object Getters from the DB

    /**
     * Get the {@link InstanceStatePojoParent.InstanceStatePojo}
     * object from the DB or create an empty one and return it
     * @return {@link InstanceStatePojoParent.InstanceStatePojo}
     */
    private InstanceStatePojoParent.InstanceStatePojo getChildPojo(){
        InstanceStatePojoParent.InstanceStatePojo p = (InstanceStatePojoParent.InstanceStatePojo)
                dbUtils.getPersistedObject(InstanceStatePojoParent.InstanceStatePojo.class);
        if(p == null){
            p = new InstanceStatePojoParent.InstanceStatePojo();
        }
        return p;
    }

    /**
     * Get the {@link InstanceStatePojoParent}
     * object from the DB or create an empty one and return it
     * @return {@link InstanceStatePojoParent}
     */
    private InstanceStatePojoParent getParentPojo(){
        InstanceStatePojoParent p = (InstanceStatePojoParent)
                dbUtils.getPersistedObject(InstanceStatePojoParent.class);
        if(p == null){
            p = new InstanceStatePojoParent();
        }
        return p;
    }
    //endregion

    //region InstanceStatePojoParent Data Model

    /**
     * InstanceStatePojoParent Class
     */
    public static class InstanceStatePojoParent {
        //region Parent Class Vars
        @SerializedName("instanceStatePojosList")
        private List<InstanceStatePojoParent.InstanceStatePojo> instanceStatePojosList;
        @SerializedName("instanceStatePojosMap")
        private Map<String, InstanceStatePojoParent.InstanceStatePojo> instanceStatePojosMap;
        //endregion

        //region Parent Class Setters and Getters
        public List<InstanceStatePojoParent.InstanceStatePojo> getInstanceStatePojosList() {
            return instanceStatePojosList;
        }

        /**
         * Insert a list of InstanceStatePojos
         * @param instanceStatePojosList
         */
        public void setInstanceStatePojosList(List<InstanceStatePojoParent.InstanceStatePojo> instanceStatePojosList) {
            this.instanceStatePojosList = instanceStatePojosList;
        }

        /**
         * Overloaded to allow for single insertion in shorthand
         * Note, WILL override if list exists
         * @param instanceStatePojo
         */
        public void setInstanceStatePojosList(@NonNull InstanceStatePojoParent.InstanceStatePojo instanceStatePojo) {
            this.instanceStatePojosList = new ArrayList<>();
            this.instanceStatePojosList.add(instanceStatePojo);
        }

        /**
         * Overloaded to allow for single insertion in shorthand
         * Note, Will NOT override if list exists
         * @param instanceStatePojo
         */
        public void appendToInstanceStatePojosList(@NonNull InstanceStatePojoParent.InstanceStatePojo instanceStatePojo) {
            if(this.instanceStatePojosList == null){
                this.instanceStatePojosList = new ArrayList<>();
            }
            this.instanceStatePojosList.add(instanceStatePojo);
        }

        public Map<String, InstanceStatePojoParent.InstanceStatePojo> getInstanceStatePojosMap() {
            return instanceStatePojosMap;
        }

        public void setInstanceStatePojosMap(Map<String, InstanceStatePojoParent.InstanceStatePojo> instanceStatePojosMap) {
            this.instanceStatePojosMap = instanceStatePojosMap;
        }

        /**
         * Overloaded to allow for single map insertion in shorthand
         * Note, WILL override if map exists
         * @param str Key
         * @param pojo Value
         */
        public void setInstanceStatePojosMap(@NonNull String str, @NonNull InstanceStatePojoParent.InstanceStatePojo pojo) {
            this.instanceStatePojosMap = new HashMap<>();
            this.instanceStatePojosMap.put(str, pojo);
        }

        /**
         * Overloaded to allow for single map insertion in shorthand
         * Note, Will NOT override if map exists
         * @param str Key
         * @param pojo Value
         */
        public void appendToInstanceStatePojosMap(@NonNull String str, @NonNull InstanceStatePojoParent.InstanceStatePojo pojo) {
            if(this.instanceStatePojosMap == null){
                this.instanceStatePojosMap = new HashMap<>();
            }
            this.instanceStatePojosMap.put(str, pojo);
        }
        //endregion

        /**
         * Instance State Pojo class as a child so multiples can be set
         */
        public static class InstanceStatePojo<T extends View, E extends Enum> {

            //region Local Enums

            /**
             * Instance types to be used for setting the items back into place
             */
            public static enum InstanceTypes {
                TypeActivity, TypeFragment, TypeOther1, TypeOther2, TypeOther3
            }

            //endregion

            //region Misc Values
            @SerializedName("shouldReloadInOnResume")
            private boolean shouldReloadInOnResume;
            @SerializedName("shouldReloadAfterReboot")
            private boolean shouldReloadAfterReboot;
            @SerializedName("shouldReloadAfterStateChange")
            private boolean shouldReloadAfterStateChange;
            @SerializedName("dateInMillisecondsToExpireData")
            private long dateInMillisecondsToExpireData;
            @SerializedName("instanceType")
            private InstanceStatePojoParent.InstanceStatePojo.InstanceTypes instanceType;
            //endregion

            //region Enums and Views
            @SerializedName("miscMap")
            private Map<String, Object> miscMap;
            @SerializedName("viewsListToStore")
            private List<T> viewsListToStore;
            @SerializedName("viewsMapToStore")
            private Map<String, T> viewsMapToStore;
            @SerializedName("enumsListToStore")
            private List<E> enumsListToStore;
            @SerializedName("enumsMapToStore")
            private Map<String, E> enumsMapToStore;
            @SerializedName("stackEnumValuesToStore")
            private Stack<E> stackEnumValuesToStore;
            @SerializedName("listOfStacksOfEnumValuesToStore")
            private List<Stack<E>> listOfStacksOfEnumValuesToStore;
            //endregion

            //region Raw types and Boxed Raw Types
            @SerializedName("stringListValuesToStore")
            private List<String> stringListValuesToStore;
            @SerializedName("integerListValuesToStore")
            private List<Integer> integerListValuesToStore;
            @SerializedName("booleanListValuesToStore")
            private List<Boolean> booleanListValuesToStore;
            @SerializedName("doubleListValuesToStore")
            private List<Double> doubleListValuesToStore;
            @SerializedName("floatListValuesToStore")
            private List<Float> floatListValuesToStore;
            @SerializedName("stringMapValuesToStore")
            private Map<String, String> stringMapValuesToStore;
            @SerializedName("integerMapValuesToStore")
            private Map<String, Integer> integerMapValuesToStore;
            @SerializedName("booleanMapValuesToStore")
            private Map<String, Boolean> booleanMapValuesToStore;
            @SerializedName("doubleMapValuesToStore")
            private Map<String, Double> doubleMapValuesToStore;
            @SerializedName("floatMapValuesToStore")
            private Map<String, Float> floatMapValuesToStore;
            @SerializedName("stringToStore")
            private String stringToStore;
            @SerializedName("integerToStore")
            private Integer integerToStore;
            @SerializedName("booleanToStore")
            private Boolean booleanToStore;
            @SerializedName("doubleToStore")
            private Double doubleToStore;
            @SerializedName("floatToStore")
            private Float floatToStore;
            //endregion

            //region Setters and Getters


            public boolean isShouldReloadInOnResume() {
                return shouldReloadInOnResume;
            }

            public void setShouldReloadInOnResume(boolean shouldReloadInOnResume) {
                this.shouldReloadInOnResume = shouldReloadInOnResume;
            }

            public boolean isShouldReloadAfterReboot() {
                return shouldReloadAfterReboot;
            }

            public void setShouldReloadAfterReboot(boolean shouldReloadAfterReboot) {
                this.shouldReloadAfterReboot = shouldReloadAfterReboot;
            }

            public boolean isShouldReloadAfterStateChange() {
                return shouldReloadAfterStateChange;
            }

            public void setShouldReloadAfterStateChange(boolean shouldReloadAfterStateChange) {
                this.shouldReloadAfterStateChange = shouldReloadAfterStateChange;
            }

            public long getDateInMillisecondsToExpireData() {
                return dateInMillisecondsToExpireData;
            }

            public void setDateInMillisecondsToExpireData(long dateInMillisecondsToExpireData) {
                this.dateInMillisecondsToExpireData = dateInMillisecondsToExpireData;
            }

            public InstanceStatePojoParent.InstanceStatePojo.InstanceTypes getInstanceType() {
                return instanceType;
            }

            public void setInstanceType(InstanceStatePojoParent.InstanceStatePojo.InstanceTypes instanceType) {
                this.instanceType = instanceType;
            }

            public Map<String, Object> getMiscMap() {
                return miscMap;
            }

            public void setMiscMap(Map<String, Object> miscMap) {
                this.miscMap = miscMap;
            }

            public List<T> getViewsListToStore() {
                return viewsListToStore;
            }

            public void setViewsListToStore(List<T> viewsListToStore) {
                this.viewsListToStore = viewsListToStore;
            }

            public Map<String, T> getViewsMapToStore() {
                return viewsMapToStore;
            }

            public void setViewsMapToStore(Map<String, T> viewsMapToStore) {
                this.viewsMapToStore = viewsMapToStore;
            }

            public List<E> getEnumsListToStore() {
                return enumsListToStore;
            }

            public void setEnumsListToStore(List<E> enumsListToStore) {
                this.enumsListToStore = enumsListToStore;
            }

            public Map<String, E> getEnumsMapToStore() {
                return enumsMapToStore;
            }

            public void setEnumsMapToStore(Map<String, E> enumsMapToStore) {
                this.enumsMapToStore = enumsMapToStore;
            }

            public Stack<E> getStackEnumValuesToStore() {
                return stackEnumValuesToStore;
            }

            public void setStackEnumValuesToStore(Stack<E> stackEnumValuesToStore) {
                this.stackEnumValuesToStore = stackEnumValuesToStore;
            }

            public List<Stack<E>> getListOfStacksOfEnumValuesToStore() {
                return listOfStacksOfEnumValuesToStore;
            }

            public void setListOfStacksOfEnumValuesToStore(List<Stack<E>> listOfStacksOfEnumValuesToStore) {
                this.listOfStacksOfEnumValuesToStore = listOfStacksOfEnumValuesToStore;
            }

            public List<String> getStringListValuesToStore() {
                return stringListValuesToStore;
            }

            public void setStringListValuesToStore(List<String> stringListValuesToStore) {
                this.stringListValuesToStore = stringListValuesToStore;
            }

            public List<Integer> getIntegerListValuesToStore() {
                return integerListValuesToStore;
            }

            public void setIntegerListValuesToStore(List<Integer> integerListValuesToStore) {
                this.integerListValuesToStore = integerListValuesToStore;
            }

            public List<Boolean> getBooleanListValuesToStore() {
                return booleanListValuesToStore;
            }

            public void setBooleanListValuesToStore(List<Boolean> booleanListValuesToStore) {
                this.booleanListValuesToStore = booleanListValuesToStore;
            }

            public List<Double> getDoubleListValuesToStore() {
                return doubleListValuesToStore;
            }

            public void setDoubleListValuesToStore(List<Double> doubleListValuesToStore) {
                this.doubleListValuesToStore = doubleListValuesToStore;
            }

            public List<Float> getFloatListValuesToStore() {
                return floatListValuesToStore;
            }

            public void setFloatListValuesToStore(List<Float> floatListValuesToStore) {
                this.floatListValuesToStore = floatListValuesToStore;
            }

            public Map<String, String> getStringMapValuesToStore() {
                return stringMapValuesToStore;
            }

            public void setStringMapValuesToStore(Map<String, String> stringMapValuesToStore) {
                this.stringMapValuesToStore = stringMapValuesToStore;
            }

            public Map<String, Integer> getIntegerMapValuesToStore() {
                return integerMapValuesToStore;
            }

            public void setIntegerMapValuesToStore(Map<String, Integer> integerMapValuesToStore) {
                this.integerMapValuesToStore = integerMapValuesToStore;
            }

            public Map<String, Boolean> getBooleanMapValuesToStore() {
                return booleanMapValuesToStore;
            }

            public void setBooleanMapValuesToStore(Map<String, Boolean> booleanMapValuesToStore) {
                this.booleanMapValuesToStore = booleanMapValuesToStore;
            }

            public Map<String, Double> getDoubleMapValuesToStore() {
                return doubleMapValuesToStore;
            }

            public void setDoubleMapValuesToStore(Map<String, Double> doubleMapValuesToStore) {
                this.doubleMapValuesToStore = doubleMapValuesToStore;
            }

            public Map<String, Float> getFloatMapValuesToStore() {
                return floatMapValuesToStore;
            }

            public void setFloatMapValuesToStore(Map<String, Float> floatMapValuesToStore) {
                this.floatMapValuesToStore = floatMapValuesToStore;
            }

            public String getStringToStore() {
                return stringToStore;
            }

            public void setStringToStore(String stringToStore) {
                this.stringToStore = stringToStore;
            }

            public Integer getIntegerToStore() {
                return integerToStore;
            }

            public void setIntegerToStore(Integer integerToStore) {
                this.integerToStore = integerToStore;
            }

            public Boolean getBooleanToStore() {
                return booleanToStore;
            }

            public void setBooleanToStore(Boolean booleanToStore) {
                this.booleanToStore = booleanToStore;
            }

            public Double getDoubleToStore() {
                return doubleToStore;
            }

            public void setDoubleToStore(Double doubleToStore) {
                this.doubleToStore = doubleToStore;
            }

            public Float getFloatToStore() {
                return floatToStore;
            }

            public void setFloatToStore(Float floatToStore) {
                this.floatToStore = floatToStore;
            }

            //endregion
        }
    }

    //endregion
}
