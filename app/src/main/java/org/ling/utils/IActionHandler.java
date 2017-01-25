package org.ling.utils;


/**
 * Created by Dmitry on 29.06.2016.
 */
public interface IActionHandler {

    public void onSuccessAction(Object rs);

    public void onFailAction(String s, Throwable throwable);
}
