package com.pgmacdesign.pgmactips.jetpackutilities.lifecycleutilities;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;

import androidx.lifecycle.Lifecycle;

@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {
        CustomAnnotationsBase.Dependencies.Android_Lifecycle,
        CustomAnnotationsBase.Dependencies.Android_Lifecycle_Annotations})
/**
 * POJO class that is used in {@link LifeCycleListener} to send back upon passed listener
 */
public class LifeCycleListenerPojo <E extends Enum<E>> {

    @SerializedName("nameOfActivity")
    private String nameOfActivity;
    @SerializedName("nameOfFragment")
    private String nameOfFragment;
    @SerializedName("customEnumValue")
    private E customEnumValue;
    @SerializedName("eventTriggered")
    private Lifecycle.Event eventTriggered;

    public LifeCycleListenerPojo(){}

    public LifeCycleListenerPojo(@Nullable String nameOfActivity,
                                 @Nullable String nameOfFragment,
                                 @Nullable E customEnumValue,
                                 @Nullable Lifecycle.Event eventTriggered){
        this.nameOfActivity = nameOfActivity;
        this.nameOfFragment = nameOfFragment;
        this.customEnumValue = customEnumValue;
        this.eventTriggered = eventTriggered;
    }

    public String getNameOfActivity() {
        return nameOfActivity;
    }

    public void setNameOfActivity(String nameOfActivity) {
        this.nameOfActivity = nameOfActivity;
    }

    public String getNameOfFragment() {
        return nameOfFragment;
    }

    public void setNameOfFragment(String nameOfFragment) {
        this.nameOfFragment = nameOfFragment;
    }

    public E getCustomEnumValue() {
        return customEnumValue;
    }

    public void setCustomEnumValue(E customEnumValue) {
        this.customEnumValue = customEnumValue;
    }

    public Lifecycle.Event getEventTriggered() {
        return eventTriggered;
    }

    public void setEventTriggered(Lifecycle.Event eventTriggered) {
        this.eventTriggered = eventTriggered;
    }
}
