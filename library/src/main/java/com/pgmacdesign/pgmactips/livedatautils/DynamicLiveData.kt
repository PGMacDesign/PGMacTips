package com.pgmacdesign.pgmactips.livedatautils

import androidx.lifecycle.LiveData

class DynamicLiveData<T : Any?> constructor(t : T): LiveData<T>() {

    init {
        // use post instead of set since this can be created on any thread
        postValue(t)
    }

    companion object {
        fun <T> create(t : T): LiveData<T> {
            return DynamicLiveData(t)
        }
    }
}