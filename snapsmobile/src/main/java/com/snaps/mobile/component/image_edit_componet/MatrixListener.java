package com.snaps.mobile.component.image_edit_componet;

import android.graphics.Matrix;
import android.graphics.Path;

/**
 * Created by ysjeong on 2017. 6. 7..
 */

public interface MatrixListener {
    void notifyMatrix(Path path, Matrix matrix);
}
