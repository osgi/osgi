package org.osgi.test.cases.converter.junit;

import java.util.Date;


/**
 * Chapter 148.5  - Customizing Converter
 */
public class ConverterComplianceTest {

	/**
	 * For Scalar conversion test purpose
	 */
	public static enum Animal
	{
		CAT,
		CROCODILE,
		DOG,
		eAGLe,
		FROG
	}

	public static class MyObject
	{			
		public final String value;
		
		public MyObject(String value)
		{
			this.value = value;
		}
	}

	public static class MyOtherObject
	{			
		public static MyOtherObject valueOf(String value)
		{
			MyOtherObject otherObject = new MyOtherObject();
			otherObject.setValue(value);
			return otherObject;
		}
		
		private String value;
		
		private MyOtherObject() {}

		private void setValue(String value)
		{
			this.value = value;
		}
		
		public String getValue()
		{
			return this.value;
		}
	}
	
	public interface MyInterfaceProvidingLong
	{
		long getLong();
	}
	
	public interface MyInterfaceProvidingTwoInts
	{
		int getFirstInt();
		
		int getSecondInt();
	}
	
	public abstract static class MyAbstractImplementation 
	{
		abstract String getMyDateFormat();
	}
	
	public static class MyImplementation extends MyAbstractImplementation
	implements MyInterfaceProvidingLong, MyInterfaceProvidingTwoInts
	{
		int firstInt = 14282;
		int secondInt = -1599828768;
		long longValue = 61343418070000l;
		
		@Override
		public int getFirstInt() {
			return firstInt;
		}

		@Override
		public int getSecondInt() {
			return secondInt;
		}

		@Override
		public long getLong() {
			return longValue;
		}

		@Override
		String getMyDateFormat() {
			
			return "yyMMddHHmmssZ";
		}
	}
	
	public static class MyBean
	{
		boolean enabled;
		Date startDate;
		
		boolean getEnabled()
		{
			return enabled;
		}
		
		void setEnabled(boolean e)
		{
			enabled = e;
		}
		
		Date getStartDate()
		{
			return startDate;
		}
		
		void setStartDate(Date d)
		{
			startDate = d;
		}
	}
}
