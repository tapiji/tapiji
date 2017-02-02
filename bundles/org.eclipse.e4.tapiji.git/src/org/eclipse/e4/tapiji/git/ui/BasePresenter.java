package org.eclipse.e4.tapiji.git.ui;

public interface BasePresenter<T> {

    void init();

    void dispose();

    void setView(T view);

}
