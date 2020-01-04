package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LatLngTest {
    LatLng l1 = new LatLng();
    LatLng l2 = new LatLng(0.0,0.0);
    LatLng l3 = new LatLng(3.33333333333,4.4444444444444);
    @Test
    public void getLatitude_Test() {
        assertEquals(null, l1.getLatitude());

        double d1_1 = l2.getLatitude();
        assertNotEquals(0.1, d1_1);

        double d1_2 = l3.getLatitude();
        assertNotEquals(3.333333, d1_2);
    }

    @Test
    public void getLongitude_Test() {
        assertEquals(null, l1.getLongitude());

        double d2_1 = l2.getLongitude();
        assertNotEquals(0.1, d2_1);

        double d2_2 = l3.getLongitude();
        assertNotEquals(4.44444444, d2_2);
    }
}