package com.pgmacdesign.pgmactips.settings;

import com.google.gson.annotations.SerializedName;
import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;

import java.lang.reflect.Type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Settings object as used in the {@link SettingsINI} class
 * @param <T> T extends Object; If using raw types, box into objects
 */
public class SettingsObject <T extends Object> {

    /**
     * Constructor for the Settings Object.
     * @param uniqueStringKey Unique String key to write (hash map / dictionary style)
     * @param objToWrite
     * @param classToCast
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.GSON)
    public SettingsObject(@NonNull String uniqueStringKey, @NonNull T objToWrite, @Nullable Class classToCast){
        this.uniqueStringKey = uniqueStringKey;
        this.objToWrite = objToWrite;
        this.classToCast = classToCast;
        this.typeToCast = null;
    }

    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.GSON)
    public SettingsObject(@NonNull String uniqueStringKey, @NonNull T objToWrite, @Nullable Type typeToCast){
        this.uniqueStringKey = uniqueStringKey;
        this.objToWrite = objToWrite;
        this.typeToCast = typeToCast;
        this.classToCast = null;
    }

    public SettingsObject(@NonNull String uniqueStringKey, @NonNull T objToWrite){
        this.uniqueStringKey = uniqueStringKey;
        this.objToWrite = objToWrite;
        this.typeToCast = null;
        this.classToCast = null;
    }

    @SerializedName("uniqueStringKey")
    private String uniqueStringKey;
    @SerializedName("objToWrite")
    private T objToWrite;
    @SerializedName("classToCast")
    private Class classToCast;
    @SerializedName("typeToCast")
    private Type typeToCast;

    public String getUniqueStringKey() {
        return uniqueStringKey;
    }

    public T getObjToWrite() {
        return objToWrite;
    }

    public Class getClassToCast() {
        return classToCast;
    }

    public Type getTypeToCast() {
        return typeToCast;
    }
}
