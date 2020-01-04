package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ReportTest {
    Report r = new Report();

    @Test
    public void setCarNum_Test() {
        r.setCarNum("test");
        String getCarNum = r.getCarNum();
        assertEquals("test", getCarNum);
    }
    @Test
    public void getCarNum_Test() {
        String getCarNum = r.getCarNum();
        assertEquals(null, getCarNum);
        assertEquals(null, r.getCarNum());
    }

    @Test
    public void checkCanBeCanceld_Test() {
        assertEquals(false,r.checkCanBeCanceld());
        assertNotEquals(true,r.checkCanBeCanceld());
        assertNotEquals(null,r.checkCanBeCanceld());
    }

    @Test
    public void getReporterID_Test() {
        assertEquals(0,r.getReporterID());
        assertNotEquals(null,r.getReporterID());
    }

    @Test
    public void getFineAmount_Test() {
        assertEquals(0,r.getFineAmount());
        assertNotEquals(null,r.getFineAmount());
    }

    @Test
    public void getReportType_Test() {
        assertEquals(null,r.getReportType());
        assertNotEquals("",r.getReportType());
    }

    @Test
    public void getCarType_Test() {
        assertEquals(null,r.getCarType());
        assertNotEquals("",r.getCarType());
    }

    @Test
    public void getKey_Test() {
        assertEquals(null,r.getKey());
        assertNotEquals("",r.getKey());
    }

    @Test
    public void getLocation_Test() {
        assertEquals(null,r.getLocation());
        assertNotEquals("",r.getLocation());
    }

    @Test
    public void getDate_Test() {
        assertEquals(null,r.getDate());
        assertNotEquals("",r.getDate());
    }

    @Test
    public void getTime_Test() {
        assertEquals(null,r.getTime());
        assertNotEquals("",r.getTime());
    }

    @Test
    public void getCarColor_Test() {
        assertEquals(null,r.getCarColor());
        assertNotEquals("",r.getCarColor());
    }
}