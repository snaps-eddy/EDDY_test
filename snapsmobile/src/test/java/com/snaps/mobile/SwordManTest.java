package com.snaps.mobile;

import android.graphics.Bitmap;

import com.snaps.common.utils.ui.ImageEdge;
import com.snaps.common.utils.ui.SwordMan;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SwordManTest {

    SwordMan swordMan;

    @Mock
    Bitmap bitmap;

    @Before
    public void init() {
        swordMan = new SwordMan();
    }

    @Test
    public void test_find_image_floor() {

//        int width = 320;
//        int floorPixelIndex = swordMan.findFloor();
//
//        int pixelX = floorPixelIndex % width;
//        int pixelY = floorPixelIndex / width;
//
//        int[] search4PixelResult =


    }

}
