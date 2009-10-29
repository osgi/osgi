/*
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.text;
public abstract class DateFormat extends java.text.Format {
	protected DateFormat() { }
	public final java.lang.StringBuffer format(java.lang.Object var0, java.lang.StringBuffer var1, java.text.FieldPosition var2) { return null; }
	public final java.lang.String format(java.util.Date var0) { return null; }
	public abstract java.lang.StringBuffer format(java.util.Date var0, java.lang.StringBuffer var1, java.text.FieldPosition var2);
	public static java.util.Locale[] getAvailableLocales() { return null; }
	public java.util.Calendar getCalendar() { return null; }
	public final static java.text.DateFormat getDateInstance() { return null; }
	public final static java.text.DateFormat getDateInstance(int var0) { return null; }
	public final static java.text.DateFormat getDateInstance(int var0, java.util.Locale var1) { return null; }
	public final static java.text.DateFormat getDateTimeInstance() { return null; }
	public final static java.text.DateFormat getDateTimeInstance(int var0, int var1) { return null; }
	public final static java.text.DateFormat getDateTimeInstance(int var0, int var1, java.util.Locale var2) { return null; }
	public final static java.text.DateFormat getInstance() { return null; }
	public java.text.NumberFormat getNumberFormat() { return null; }
	public final static java.text.DateFormat getTimeInstance() { return null; }
	public final static java.text.DateFormat getTimeInstance(int var0) { return null; }
	public final static java.text.DateFormat getTimeInstance(int var0, java.util.Locale var1) { return null; }
	public java.util.TimeZone getTimeZone() { return null; }
	public int hashCode() { return 0; }
	public boolean isLenient() { return false; }
	public java.util.Date parse(java.lang.String var0) throws java.text.ParseException { return null; }
	public abstract java.util.Date parse(java.lang.String var0, java.text.ParsePosition var1);
	public java.lang.Object parseObject(java.lang.String var0, java.text.ParsePosition var1) { return null; }
	public void setCalendar(java.util.Calendar var0) { }
	public void setLenient(boolean var0) { }
	public void setNumberFormat(java.text.NumberFormat var0) { }
	public void setTimeZone(java.util.TimeZone var0) { }
	public final static int AM_PM_FIELD = 14;
	public final static int DATE_FIELD = 3;
	public final static int DAY_OF_WEEK_FIELD = 9;
	public final static int DAY_OF_WEEK_IN_MONTH_FIELD = 11;
	public final static int DAY_OF_YEAR_FIELD = 10;
	public final static int DEFAULT = 2;
	public final static int ERA_FIELD = 0;
	public final static int FULL = 0;
	public final static int HOUR0_FIELD = 16;
	public final static int HOUR1_FIELD = 15;
	public final static int HOUR_OF_DAY0_FIELD = 5;
	public final static int HOUR_OF_DAY1_FIELD = 4;
	public final static int LONG = 1;
	public final static int MEDIUM = 2;
	public final static int MILLISECOND_FIELD = 8;
	public final static int MINUTE_FIELD = 6;
	public final static int MONTH_FIELD = 2;
	public final static int SECOND_FIELD = 7;
	public final static int SHORT = 3;
	public final static int TIMEZONE_FIELD = 17;
	public final static int WEEK_OF_MONTH_FIELD = 13;
	public final static int WEEK_OF_YEAR_FIELD = 12;
	public final static int YEAR_FIELD = 1;
	protected java.util.Calendar calendar;
	protected java.text.NumberFormat numberFormat;
	public static class Field extends java.text.Format.Field {
		protected Field(java.lang.String var0, int var1) { super((java.lang.String) null); }
		public int getCalendarField() { return 0; }
		public static java.text.DateFormat.Field ofCalendarField(int var0) { return null; }
		public final static java.text.DateFormat.Field AM_PM; static { AM_PM = null; }
		public final static java.text.DateFormat.Field DAY_OF_MONTH; static { DAY_OF_MONTH = null; }
		public final static java.text.DateFormat.Field DAY_OF_WEEK; static { DAY_OF_WEEK = null; }
		public final static java.text.DateFormat.Field DAY_OF_WEEK_IN_MONTH; static { DAY_OF_WEEK_IN_MONTH = null; }
		public final static java.text.DateFormat.Field DAY_OF_YEAR; static { DAY_OF_YEAR = null; }
		public final static java.text.DateFormat.Field ERA; static { ERA = null; }
		public final static java.text.DateFormat.Field HOUR0; static { HOUR0 = null; }
		public final static java.text.DateFormat.Field HOUR1; static { HOUR1 = null; }
		public final static java.text.DateFormat.Field HOUR_OF_DAY0; static { HOUR_OF_DAY0 = null; }
		public final static java.text.DateFormat.Field HOUR_OF_DAY1; static { HOUR_OF_DAY1 = null; }
		public final static java.text.DateFormat.Field MILLISECOND; static { MILLISECOND = null; }
		public final static java.text.DateFormat.Field MINUTE; static { MINUTE = null; }
		public final static java.text.DateFormat.Field MONTH; static { MONTH = null; }
		public final static java.text.DateFormat.Field SECOND; static { SECOND = null; }
		public final static java.text.DateFormat.Field TIME_ZONE; static { TIME_ZONE = null; }
		public final static java.text.DateFormat.Field WEEK_OF_MONTH; static { WEEK_OF_MONTH = null; }
		public final static java.text.DateFormat.Field WEEK_OF_YEAR; static { WEEK_OF_YEAR = null; }
		public final static java.text.DateFormat.Field YEAR; static { YEAR = null; }
	}
}

