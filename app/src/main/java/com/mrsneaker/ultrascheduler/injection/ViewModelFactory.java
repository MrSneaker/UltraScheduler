package com.mrsneaker.ultrascheduler.injection;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mrsneaker.ultrascheduler.viewmodel.EventViewModel;

import org.jetbrains.annotations.NotNull;

public class ViewModelFactory implements ViewModelProvider.Factory{
    private static ViewModelFactory factory;


    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();
                }
            }
        }
        return factory;
    }

    private ViewModelFactory() {

    }

    @Override
    @NotNull
    public <T extends ViewModel>  T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EventViewModel.class)) {
            return (T) new EventViewModel();
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
