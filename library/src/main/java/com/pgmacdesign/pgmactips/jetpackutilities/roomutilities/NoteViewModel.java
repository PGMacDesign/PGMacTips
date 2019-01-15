package com.pgmacdesign.pgmactips.jetpackutilities.roomutilities;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

/**
 * Using AndroidViewModel instead of AndroidModel so as to gain the application context instead
 */
public class NoteViewModel extends AndroidViewModel {

    public NoteViewModel(Application application){
        super(application);
        // TODO: 1/14/2019 left off on: https://www.youtube.com/watch?v=IZ881Gg7m8s&list=PLlxmoA0rQ-LyVuVR1LFvpR1K8A0HsIBYx&index=8 
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
