package com.pgmacdesign.pgmactips.livedatautils

import androidx.lifecycle.LiveData

class AbsentLiveData<T : Any?> constructor(): LiveData<T>() {
    init {
        // use post instead of set since this can be created on any thread
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> {
            return AbsentLiveData()
        }
    }
}