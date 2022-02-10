package com.snaps.mobile;

import android.graphics.Rect;

import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.mobile.activity.common.products.base.Fryingpan;
import com.snaps.mobile.utils.ui.RotateUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FrylingpanTest {

    private Fryingpan fryingpan;

    @Before
    public void setUp() {
        fryingpan = new Fryingpan();
    }

    @Test
    public void test_turn_on_fire() {
        fryingpan.turnOnFire(368f, 630f, 350f, 630f);

        assertThat(fryingpan.getWidthRatio(), is(1.051f));
        assertThat(fryingpan.getHeightRatio(), is(1f));
    }

    @Test
    public void test_flip_image_upscale() {
        SnapsLayoutControl control = new SnapsLayoutControl();
        control.x = "80";
        control.y = "70";
        control.width = "110";
        control.height = "190";
        fryingpan.turnOnFire(368f, 630f, 350f, 630f);

        fryingpan.flip(control);

        assertThat(control.x, is("84"));
        assertThat(control.y, is("70"));
        assertThat(control.width, is("116"));
        assertThat(control.height, is("190"));
    }

    @Test
    public void test_flip_clipart_upscale() {
        SnapsClipartControl control = new SnapsClipartControl();
        control.x = "60";
        control.y = "280";
        control.width = "220";
        control.height = "140";
        fryingpan.turnOnFire(368f, 630f, 350f, 630f);

        fryingpan.flip(control);

        assertThat(control.x, is("63"));
        assertThat(control.y, is("280"));
        assertThat(control.width, is("231"));
        assertThat(control.height, is("147"));
    }

    @Test
    public void test_flip_clipart_rotate_0_upscale() {
        SnapsClipartControl control = new SnapsClipartControl();
        control.x = "192";
        control.y = "293";
        control.width = "59";
        control.height = "54";
        fryingpan.turnOnFire(341f, 630f, 350f, 630f);

        fryingpan.flip(control);

        assertThat(control.x, is("187"));
        assertThat(control.y, is("293"));
        assertThat(control.width, is("57"));
        assertThat(control.height, is("53"));
    }

    /**
     * 249 241 315 287
     * "16" x="235.0" y="249.0" width="64" height="45"
     * "16" x="227.5585356075514" y="257.1537856136861" width="64" height="44.99998474121094"
     */
    @Test
    public void test_flip_clipart_rotate_16_upscale() {
        SnapsClipartControl control = new SnapsClipartControl();
        control.x = "249";
        control.y = "241";
        control.width = String.valueOf(315 - 249);
        control.height = String.valueOf(287 - 241);
        fryingpan.turnOnFire(341f, 630f, 350f, 630f);

        fryingpan.flip(control);

        assertThat(control.x, is("243"));
        assertThat(control.y, is("241"));
        assertThat(control.width, is("64"));
        assertThat(control.height, is("45"));
    }
    
    @Test
    public void test_flip_text_upscale() {
        SnapsTextControl control = new SnapsTextControl();
        control.x = "110";
        control.y = "490";
        control.width = "165";
        control.height = "40";
        control.format.fontSize = "12";
        fryingpan.turnOnFire(368f, 630f, 350f, 630f);

        fryingpan.flip(control);

        assertThat(control.x, is("116"));
        assertThat(control.y, is("490"));
        assertThat(control.width, is("173"));
        assertThat(control.height, is("42"));
        assertThat(control.format.fontSize, is("12"));
    }

    @Test
    public void test_flip_image_downscale() {
        SnapsLayoutControl control = new SnapsLayoutControl();
        control.x = "80";
        control.y = "70";
        control.width = "110";
        control.height = "190";
        fryingpan.turnOnFire(326f, 630f, 350, 630f);

        fryingpan.flip(control);

        assertThat(control.x, is("74"));
        assertThat(control.y, is("70"));
        assertThat(control.width, is("102"));
        assertThat(control.height, is("190"));
    }

    @Test
    public void test_flip_clipart_downscale() {
        SnapsClipartControl control = new SnapsClipartControl();
        control.x = "60";
        control.y = "280";
        control.width = "220";
        control.height = "140";
        fryingpan.turnOnFire(326f, 630f, 350, 630f);

        fryingpan.flip(control);

        assertThat(control.x, is("56"));
        assertThat(control.y, is("280"));
        assertThat(control.width, is("205"));
        assertThat(control.height, is("130"));
    }

    @Test
    public void test_flip_text_downscale() {
        SnapsTextControl control = new SnapsTextControl();
        control.x = "116";
        control.y = "490";
        control.width = "165";
        control.height = "40";
        control.format.fontSize = "12";
        fryingpan.turnOnFire(326f, 630f, 350, 630f);

        fryingpan.flip(control);

        assertThat(control.x, is("108"));
        assertThat(control.y, is("490"));
        assertThat(control.width, is("154"));
        assertThat(control.height, is("37"));
        assertThat(control.format.fontSize, is("11"));
    }

}
