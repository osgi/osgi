package org.osgi.test.cases.converter.junit;

public class ConversionComplianceTest {

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
		protected String value;
		
		public MyObject(String value)
		{
			this.value = value;
		}
	
		public String toString() {
			return this.value;
		}
	}

	public static class ExtObject extends MyObject {
		public ExtObject() {
			super("extended");
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
}
