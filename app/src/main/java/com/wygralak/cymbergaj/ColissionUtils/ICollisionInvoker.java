package com.wygralak.cymbergaj.ColissionUtils;

import com.wygralak.cymbergaj.Vector2;

/**
 * Created by Kamil on 2016-03-10.
 */
public interface ICollisionInvoker {
    void updateAngle(float angle);
    float getCurrentAngle();
    void updateVector(Vector2 angle);
    Vector2 getCurrentVector();

}
