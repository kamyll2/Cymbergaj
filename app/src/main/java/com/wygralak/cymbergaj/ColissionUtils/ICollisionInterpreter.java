package com.wygralak.cymbergaj.ColissionUtils;

/**
 * Created by Kamil on 2016-03-10.
 */
public interface ICollisionInterpreter {
    boolean checkForCollisionAndHandle(ICollisionInvoker invoker, float x, float y);
}
