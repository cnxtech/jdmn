/**
 * Copyright 2016 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.gs.dmn.feel.lib;

import org.junit.Test;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class DefaultFEELLibTest extends BaseFEELLibTest<BigDecimal, XMLGregorianCalendar, XMLGregorianCalendar, XMLGregorianCalendar, Duration> {
    @Override
    protected DefaultFEELLib getLib() {
        return new DefaultFEELLib();
    }

    //
    // Time operator functions
    //
    @Test
    public void testTimeEqual() {
        super.testTimeEqual();

        assertFalse(getLib().timeEqual(makeTime("12:00:00"), makeTime("12:00:00+00:00")));
        assertFalse(getLib().timeEqual(makeTime("00:00:00+00:00"), makeTime("00:00:00@Etc/UTC")));
        assertTrue(getLib().timeEqual(makeTime("00:00:00Z"), makeTime("00:00:00+00:00")));
        assertFalse(getLib().timeEqual(makeTime("00:00:00Z"), makeTime("00:00:00@Etc/UTC")));
    }

    //
    // Date and time operator functions
    //
    @Test
    public void testDateTimeEqual() {
        super.testDateTimeEqual();

        assertFalse(getLib().dateTimeEqual(makeDateAndTime("2018-12-08T12:00:00"), makeDateAndTime("2018-12-08T12:00:00+00:00")));
        assertFalse(getLib().dateTimeEqual(makeDateAndTime("2018-12-08T00:00:00+00:00"), makeDateAndTime("2018-12-08T00:00:00@Etc/UTC")));
        assertTrue(getLib().dateTimeEqual(makeDateAndTime("2018-12-08T12:00:00Z"), makeDateAndTime("2018-12-08T12:00:00+00:00")));
        assertFalse(getLib().dateTimeEqual(makeDateAndTime("2018-12-08T00:00:00Z"), makeDateAndTime("2018-12-08T00:00:00@Etc/UTC")));
    }

    //
    // Conversion functions
    //
    @Test
    public void testDate() {
        super.testDate();

        assertNull(getLib().date("01211-12-31"));

        assertEqualsTime("2016-08-01", getLib().date(makeDate("2016-08-01")));
        assertEqualsTime("2017-10-11", getLib().date(getLib().date("2017-10-11")));
    }

    @Test
    public void testTime() {
        super.testTime();

        assertEqualsTime("12:00:00Z", getLib().time(makeTime("12:00:00Z")));
        assertEqualsTime("12:00:00", getLib().time(
                makeNumber("12"), makeNumber("00"), makeNumber("00"),
                null));
        assertEqualsTime("00:00:00Z", getLib().time(getLib().date("2017-08-10")));
    }

    @Test
    public void testDateTime() {
        super.testDateTime();

        // Test date
        assertEqualsTime("2016-08-01T00:00:00", getLib().dateAndTime("2016-08-01"));

        // Missing Z
        assertEqualsTime("-2016-01-30T09:05:00", getLib().dateAndTime("-2016-01-30T09:05:00"));
        assertEqualsTime("-2017-02-28T02:02:02", getLib().dateAndTime("-2017-02-28T02:02:02"));

        assertEqualsTime("2016-08-01T11:00:00", getLib().dateAndTime("2016-08-01T11:00:00"));
        assertEqualsTime("2011-12-03T10:15:30@Europe/Paris", getLib().dateAndTime("2011-12-03T10:15:30@Europe/Paris"));
    }

    @Test
    public void testString() {
        assertEquals("2016-08-01", getLib().string(makeDate("2016-08-01")));
        assertEquals("11:00:00Z", getLib().string(makeTime("11:00:00Z")));
        assertEquals("2016-08-01T11:00:00Z", getLib().string(makeDateAndTime("2016-08-01T11:00:00Z")));
        assertEquals("123.45", getLib().string(makeNumber("123.45")));
        assertEquals("true", getLib().string(true));

        assertEquals("999999999-12-31", getLib().string(getLib().date("999999999-12-31")));
        assertEquals("-999999999-12-31", getLib().string(getLib().date("-999999999-12-31")));
        assertEquals("999999999-12-31", getLib().string(getLib().date(makeNumber(999999999), makeNumber(12), makeNumber(31))));
        assertEquals("-999999999-12-31", getLib().string(getLib().date(makeNumber(-999999999), makeNumber(12), makeNumber(31))));

        assertEquals("00:01:00@Etc/UTC", getLib().string(getLib().time("00:01:00@Etc/UTC")));
        assertEquals("00:01:00@Europe/Paris", getLib().string(getLib().time("00:01:00@Europe/Paris")));
        assertEquals("10:20:00@Europe/Paris", getLib().string(getLib().time(getLib().dateAndTime("2017-08-10T10:20:00@Europe/Paris"))));
        assertEquals("11:20:00@Asia/Dhaka", getLib().string(getLib().time(getLib().dateAndTime("2017-09-04T11:20:00@Asia/Dhaka"))));
        assertEquals("11:59:45+02:45:55", getLib().string(getLib().time(makeNumber(11), makeNumber(59), makeNumber(45), getLib().duration("PT2H45M55S"))));
        assertEquals("11:59:45-02:45:55", getLib().string(getLib().time(makeNumber(11), makeNumber(59), makeNumber(45), getLib().duration("-PT2H45M55S"))));
        assertEquals(makeTime("00:00:00Z"), getLib().time(getLib().date("2017-08-10")));

        assertEquals("99999-12-31T11:22:33", getLib().string(getLib().dateAndTime("99999-12-31T11:22:33")));
        assertEquals("-99999-12-31T11:22:33", getLib().string(getLib().dateAndTime("-99999-12-31T11:22:33")));
        assertEquals("2011-12-31T10:15:30@Europe/Paris", getLib().string(getLib().dateAndTime("2011-12-31T10:15:30@Europe/Paris")));
        assertEquals("2011-12-31T10:15:30@Etc/UTC", getLib().string(getLib().dateAndTime("2011-12-31T10:15:30@Etc/UTC")));
        assertEquals("2011-12-31T10:15:30.987@Europe/Paris", getLib().string(getLib().dateAndTime("2011-12-31T10:15:30.987@Europe/Paris")));
        assertEquals("2011-12-31T10:15:30.123456789@Europe/Paris", getLib().string(getLib().dateAndTime("2011-12-31T10:15:30.123456789@Europe/Paris")));
        assertEquals("999999999-12-31T23:59:59.999999999@Europe/Paris", getLib().string(getLib().dateAndTime("999999999-12-31T23:59:59.999999999@Europe/Paris")));
        assertEquals("-999999999-12-31T23:59:59.999999999+02:00", getLib().string(getLib().dateAndTime("-999999999-12-31T23:59:59.999999999+02:00")));
        assertEquals("2017-01-01T23:59:01@Europe/Paris", getLib().string(getLib().dateAndTime(getLib().date("2017-01-01"), getLib().time("23:59:01@Europe/Paris"))));
        assertEquals("2017-01-01T23:59:01.123456789@Europe/Paris", getLib().string(getLib().dateAndTime(getLib().date("2017-01-01"), getLib().time("23:59:01.123456789@Europe/Paris"))));
        assertEquals("2017-09-05T09:15:30.987654321@Europe/Paris", getLib().string(getLib().dateAndTime(getLib().dateAndTime("2017-09-05T10:20:00"), getLib().time("09:15:30.987654321@Europe/Paris"))));
        assertEquals("2017-09-05T09:15:30.987654321@Europe/Paris", getLib().string(getLib().dateAndTime(getLib().dateAndTime("2017-09-05T10:20:00-01:00"), getLib().time("09:15:30.987654321@Europe/Paris"))));
        assertEquals("2017-09-05T09:15:30.987654321@Europe/Paris", getLib().string(getLib().dateAndTime(getLib().dateAndTime("2017-09-05T10:20:00@Europe/Paris"), getLib().time("09:15:30.987654321@Europe/Paris"))));
    }

    //
    // Time properties
    //
    @Test
    public void testTimeProperties() {
        assertEqualsNumber(getLib().number("12"), getLib().hour(getLib().time("12:01:02Z")));
        assertEqualsNumber(getLib().number("1"), getLib().minute(getLib().time("12:01:02Z")));
        assertEqualsNumber(getLib().number("2"), getLib().second(getLib().time("12:01:02Z")));
        assertEquals(null, getLib().timeOffset(getLib().time("12:01:02Z@Etc/UTC")));
        assertEquals(getLib().duration("P0Y0M0DT0H0M0.000S"), getLib().timeOffset(getLib().time("12:01:02Z")));
        assertEquals(null, getLib().timeOffset(getLib().time("12:01:02")));
        assertEquals(null, getLib().timezone(getLib().time("12:01:02Z@Etc/UTC")));
        assertEquals("Etc/UTC", getLib().timezone(getLib().time("12:01:02@Etc/UTC")));
        assertEquals(null, getLib().timezone(getLib().time("12:01:02")));
    }

    //
    // Date and time properties
    //
    @Test
    public void testDateAndTimeProperties() {
        assertEqualsNumber(getLib().number("2018"), getLib().year(getLib().dateAndTime("2018-12-10T12:01:02Z")));
        assertEqualsNumber(getLib().number("12"), getLib().month(getLib().dateAndTime("2018-12-10T12:01:02Z")));
        assertEqualsNumber(getLib().number("10"), getLib().day(getLib().dateAndTime("2018-12-10T12:01:02Z")));
        assertEqualsNumber(getLib().number("1"), getLib().weekday(getLib().dateAndTime("2018-12-10T12:01:02Z")));
        assertEqualsNumber(getLib().number("12"), getLib().hour(getLib().dateAndTime("2018-12-10T12:01:02Z")));
        assertEqualsNumber(getLib().number("1"), getLib().minute(getLib().dateAndTime("2018-12-10T12:01:02Z")));
        assertEqualsNumber(getLib().number("2"), getLib().second(getLib().dateAndTime("2018-12-10T12:01:02Z")));
        assertEquals(null, getLib().timeOffset(getLib().dateAndTime("2018-12-10T12:01:02Z@Etc/UTC")));
        assertEquals(getLib().duration("P0Y0M0DT0H0M0.000S"), getLib().timeOffset(getLib().dateAndTime("2018-12-10T12:01:02Z")));
        assertEquals(null, getLib().timeOffset(getLib().dateAndTime("2018-12-10T12:01:02")));
        assertEquals(null, getLib().timezone(getLib().dateAndTime("2018-12-10T12:01:02Z@Etc/UTC")));
        assertEquals("Etc/UTC", getLib().timezone(getLib().dateAndTime("2018-12-10T12:01:02@Etc/UTC")));
        assertEquals(null, getLib().timezone(getLib().dateAndTime("2018-12-10T12:01:02")));
    }

    @Test
    public void testDuration() {
        super.testDuration();
    }

    @Test
    public void testYearsAndMonthsDuration() {
        super.testYearsAndMonthsDuration();
    }

    //
    // Date time operators
    //
    @Test
    public void testDateSubtract() {
        super.testDateSubtract();
        assertEqualsTime("P0Y0M0DT0H0M0.000S", getLib().dateSubtract(makeDate("2016-08-01"), makeDate("2016-08-01")).toString());
        assertEqualsTime("-P0Y0M2DT0H0M0.000S", getLib().dateSubtract(makeDate("2016-08-01"), makeDate("2016-08-03")).toString());
    }
}

