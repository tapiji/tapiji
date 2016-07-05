package org.eclipselabs.e4.tapiji.translator.ui;

public interface BasePresenter<T> {

    void init();

    void dispose();

    void setView(T view);

}
