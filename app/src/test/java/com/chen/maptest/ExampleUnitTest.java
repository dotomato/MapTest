package com.chen.maptest;

import android.provider.Settings;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void datetimetest() throws Exception {
        DateFormat time =  SimpleDateFormat.getDateTimeInstance();
        String datatime = time.format(new Date(1487946448L*1000L));
        assertEquals(datatime, "2017-2-24 22:27:28");
    }
}