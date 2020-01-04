package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class UserTest {
    User u1 = new User("0542244410",111,"test");
    User u2 = new User("4444",0,"test2");
    User u3 = new User();

    @Test
    public void getNameTest_1() {
        assertEquals("test",u1.getName());
        assertEquals("test2",u2.getName());
        assertEquals(null,u3.getName());
    }

    @Test
    public void getNameTest_2() {
        assertNotEquals(null,u1.getName());
        assertNotEquals(null,u2.getName());
        assertNotEquals("",u1.getName());
        assertNotEquals("",u2.getName());
        assertNotEquals("",u3.getName());
    }

    @Test
    public void getID_Test_1() {
        assertEquals(111,u1.getID());
        assertEquals(0,u2.getID());
        assertEquals(0,u3.getID());
    }

    @Test
    public void getID_Test_2() {
        assertNotEquals(null,u1.getID());
        assertNotEquals(null,u2.getID());
        assertNotEquals(null,u3.getID());
        assertNotEquals(-1,u1.getID());
        assertNotEquals(-1,u2.getID());
        assertNotEquals(-1,u3.getID());
    }
}