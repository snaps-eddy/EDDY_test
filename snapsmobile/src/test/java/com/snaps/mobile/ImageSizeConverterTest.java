package com.snaps.mobile;

import com.snaps.common.utils.ui.DynamicProductDimensions;
import com.snaps.common.utils.ui.DynamicProductImageSizeConverter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ImageSizeConverterTest {

    private DynamicProductImageSizeConverter converter;

    @Before
    public void setUp() {
        converter = new DynamicProductImageSizeConverter();
    }

    @Test
    public void test_1() {
        DynamicProductDimensions result = converter.getFitImageDimensions(100, 200, 600, 300);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(150));
        assertThat(result.getHeight() , is(300));
    }

    @Test
    public void test_2() {
        DynamicProductDimensions result = converter.getFitImageDimensions(100, 200, 300, 600);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(300));
        assertThat(result.getHeight() , is(600));
    }

    @Test
    public void test_4() {
//        1.5
        DynamicProductDimensions result = converter.getFitImageDimensions(200, 400, 300, 600);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(300));
        assertThat(result.getHeight() , is(600));
    }

    @Test
    public void test_5() {
//        0.31
        DynamicProductDimensions result = converter.getFitImageDimensions(700, 1400, 600, 300);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(150));
        assertThat(result.getHeight() , is(300));
    }

    @Test
    public void test_6() {
//        0.43
        DynamicProductDimensions result = converter.getFitImageDimensions(700, 1400, 100, 600);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(100));
        assertThat(result.getHeight() , is(200));
    }

    @Test
    public void test_7() {
        DynamicProductDimensions result = converter.getFitImageDimensions(600, 600, 600, 600);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(600));
        assertThat(result.getHeight() , is(600));
    }

    @Test
    public void test_8() {
        DynamicProductDimensions result = converter.getFitImageDimensions(700, 1400, 600, 200);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(100));
        assertThat(result.getHeight() , is(200));
    }

    @Test
    public void test_9() {
        DynamicProductDimensions result = converter.getFitImageDimensions(200, 700, 300, 300);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(86));
        assertThat(result.getHeight() , is(300));
    }

    @Test
    public void test_10() {
        DynamicProductDimensions result = converter.getFitImageDimensions(700, 200, 300, 300);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(300));
        assertThat(result.getHeight() , is(86));
    }

    @Test
    public void test_11() {
        DynamicProductDimensions result = converter.getFitImageDimensions(200, 700, 300, 400);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(114));
        assertThat(result.getHeight() , is(400));
    }

    @Test
    public void test_12() {
        DynamicProductDimensions result = converter.getFitImageDimensions(200, 700, 400, 300);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(86));
        assertThat(result.getHeight() , is(300));
    }

    @Test
    public void test_13() {
        DynamicProductDimensions result = converter.getFitImageDimensions(799, 645, 400, 400);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(400));
        assertThat(result.getHeight() , is(323));
    }

    @Test
    public void test_14() {
        DynamicProductDimensions result = converter.getFitImageDimensions(645, 799, 400, 400);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(323));
        assertThat(result.getHeight() , is(400));

    }

    @Test
    public void test_15() {
        DynamicProductDimensions result = converter.getFitImageDimensions(320, 426, 600, 600);

        assertThat(result, is(notNullValue()));
        assertThat(result.getWidth() , is(451));
        assertThat(result.getHeight() , is(600));

    }


}
