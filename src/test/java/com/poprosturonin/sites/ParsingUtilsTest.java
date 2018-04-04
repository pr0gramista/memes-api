package com.poprosturonin.sites;

import com.poprosturonin.utils.ParsingUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ParsingUtilsTest {

    @Test
    public void parsingValidStringWorks() throws Exception {
        String t1 = "15";
        assertEquals(15, ParsingUtils.parseIntOrGetZero(t1));

        String t2 = " 52 ";
        assertEquals(52, ParsingUtils.parseIntOrGetZero(t2));

        String t3 = "-5 ";
        assertEquals(-5, ParsingUtils.parseIntOrGetZero(t3));
    }

    @Test
    public void parsingInvalidStringWorks() throws Exception {
        String t1 = " egokgkoekgo";
        assertEquals(0, ParsingUtils.parseIntOrGetZero(t1));

        String t2 = "";
        assertEquals(0, ParsingUtils.parseIntOrGetZero(t2));
    }
}
