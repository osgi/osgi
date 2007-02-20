package org.osgi.test.cases.util;

import java.util.*;

/**
 * Assert methods.
 *
 * <p>This class was inspired by the Assert class in the JUnit test
 * framework.
 */

public class Assert {

    /**
	 * Passes a test. Default implementation does nothing at all,
	 * but could be overridden to i.e. log all the good things that
	 * happen
	 */
    public void pass(String message) {
    }

    /**
	 * Fails a test with the given message. Throws an AssertionFailedError
	 * by default, but could be overridden to do something else.
	 */
    public void fail(String message) {
        throw new AssertionFailedError(message);
    }

    public void failException(String message, Class expected) {
        fail(message + " expected:[" + expected.getName() + "] and got nothing");
    }

    /**
	 * Asserts that a condition is true. If it isn't it throws
	 * an AssertionFailedError with the given message.
	 */
    public void assertTrue(String message, boolean condition) {
        if(!condition) {
            fail(message);
        }
        else {
            pass(message);
        }
    }

    /**
	 * Asserts that two Dictionaries of properties are equal. If they are not
	 * an AssertionFailedError is thrown.
	 */
    public void assertEqualProperties(String message, Dictionary expected, Dictionary actual)
    {
        if (expected == actual)
        {
            passNotEquals(message, expected, actual);
            return;
        }

        if ((expected == null) || (actual == null))
        {
            failNotEquals(message, expected, actual);
            return;
        }

        if (expected.size() != actual.size())
        {
            failNotEquals(message, expected, actual);
            return;
        }

        Enumeration e = expected.keys();
        while (e.hasMoreElements())
        {
            Object key = e.nextElement();
            Object expectedValue = expected.get(key);
            Object actualValue = actual.get(key);

            if (!objectEquals(expectedValue, actualValue))
            {
                failNotEquals(message, expected, actual);
                return;
            }
        }

        passNotEquals(message, expected, actual);
    }

    /**
	 * Asserts that two objects are equal. If they are not
	 * an AssertionFailedError is thrown.
	 */
    public void assertEquals(String message, Object expected, Object actual)
    {
        if (objectEquals(expected, actual))
        {
            passNotEquals(message, expected, actual);
        }
        else
        {
            failNotEquals(message, expected, actual);
        }
    }

    /**
     * Asserts that two objects are equal. If they are not
     * an AssertionFailedError is thrown.
     */
    public void assertEquals(String message, Comparator comparator, Object expected, Object actual)
    {
    	if (objectEquals(comparator, expected, actual))
    	{
    		passNotEquals(message, expected, actual);
    	}
    	else
    	{
    		failNotEquals(message, expected, actual);
    	}
    }
    
    /**
     * Compare two objects for equality. This method calls Arrays.equals if the object types are arrays.
     *
     */
    public boolean objectEquals(Object expected, Object actual)
    {
    	return objectEquals(null, expected, actual);
    }
    
    /**
	 * Compare two objects for equality. This method calls Arrays.equals if the object types are arrays.
	 *
	 */
    public boolean objectEquals(Comparator comparator, Object expected, Object actual)
    {
        if (expected == actual)
        {
            return true;
        }

        if ((expected == null) || (actual == null))
        {
            return false;
        }

        if (expected.equals(actual))
        {
        	return true;
        }

		if ( expected instanceof List && actual instanceof List )
			return objectEquals( comparator, (List)expected, (List)actual );
		
		if ( expected instanceof Dictionary && actual instanceof Dictionary )
			return objectEquals( comparator, (Dictionary)expected, (Dictionary)actual );
		
		try
		{
			Class clazz = expected.getClass();
			if (clazz.isArray())
			{
				Class type = clazz.getComponentType();
				
				if (type.isPrimitive())
				{
					if (type.equals(Integer.TYPE))
					{
						return Arrays.equals((int[])expected, (int[])actual);
					}
					else
						if (type.equals(Long.TYPE))
						{
							return Arrays.equals((long[])expected, (long[])actual);
						}
						else
							if (type.equals(Byte.TYPE))
							{
								return Arrays.equals((byte[])expected, (byte[])actual);
							}
							else
								if (type.equals(Short.TYPE))
								{
									return Arrays.equals((short[])expected, (short[])actual);
								}
								else
									if (type.equals(Character.TYPE))
									{
										return Arrays.equals((char[])expected, (char[])actual);
									}
									else
										if (type.equals(Float.TYPE))
										{
											return Arrays.equals((float[])expected, (float[])actual);
										}
										else
											if (type.equals(Double.TYPE))
											{
												return Arrays.equals((double[])expected, (double[])actual);
											}
											else
												if (type.equals(Boolean.TYPE))
												{
													return Arrays.equals((boolean[])expected, (boolean[])actual);
												}
				}
				else    /* non-primitive array object */
				{
					return Arrays.equals((Object[])expected, (Object[])actual);
				}
			}
			
			/* well it did not match any of the above types
			 * do we have a comparator to compare them?
			 */
			if (comparator != null) {
				if (comparator.compare(expected, actual) == 0) {
					return true;
				}
			}
		}
		catch (ClassCastException e)
		{
		}

        return false;
    }

    /**
	 * Asserts that two doubles are equal concerning a epsilon. If the expected
	 * value is infinity then the epsilon value is ignored.
	 */
    public void assertEquals(String message, double expected, double actual, double epsilon) {
        // handle infinity specially since subtracting to infinite values gives NaN and the
        // the following test fails
        if(Double.isInfinite(expected)) {
            if(!(expected == actual)) {
                failNotEquals(message, new Double(expected), new Double(actual));
            }
            else {
                passNotEquals(message, new Double(expected), new Double(actual));
            }
        }
        else if(!(Math.abs(expected - actual) <= epsilon)) { // Because comparison with NaN always returns false
            failNotEquals(message, new Double(expected), new Double(actual));
        }
        else {
            passNotEquals(message, new Double(expected), new Double(actual));
        }
    }

    /**
	 * Asserts that two floats are equal concerning a epsilon. If the expected
	 * value is infinity then the epsilon value is ignored.
	 */
    public void assertEquals(String message, float expected, float actual, float epsilon) {
        // handle infinity specially since subtracting to infinite values gives NaN and the
        // the following test fails
        if(Float.isInfinite(expected)) {
            if(!(expected == actual)) {
                failNotEquals(message, new Float(expected), new Float(actual));
            }
            else {
                passNotEquals(message, new Float(expected), new Float(actual));
            }
        }
        else if(!(Math.abs(expected - actual) <= epsilon)) {
            failNotEquals(message, new Float(expected), new Float(actual));
        }
        else {
            passNotEquals(message, new Float(expected), new Float(actual));
        }
    }

    /**
	 * Asserts that two longs are equal.
	 */
    public void assertEquals(String message, long expected, long actual) {
        assertEquals(message, new Long(expected), new Long(actual));
    }

    /**
	 * Asserts that two booleans are equal.
	 */
    public void assertEquals(String message, boolean expected, boolean actual) {
        assertEquals(message, new Boolean(expected), new Boolean(actual));
    }

    /**
	 * Asserts that two bytes are equal.
	 */
    public void assertEquals(String message, byte expected, byte actual) {
        assertEquals(message, new Byte(expected), new Byte(actual));
    }

    /**
	 * Asserts that two chars are equal.
	 */
    public void assertEquals(String message, char expected, char actual) {
        assertEquals(message, new Character(expected), new Character(actual));
    }

    /**
	 * Asserts that two shorts are equal.
	 */
    public void assertEquals(String message, short expected, short actual) {
        assertEquals(message, new Short(expected), new Short(actual));
    }

    /**
	 * Asserts that two ints are equal.
	 */
    public void assertEquals(String message, int expected, int actual) {
        assertEquals(message, new Integer(expected), new Integer(actual));
    }

    /**
	 * Asserts that an object isn't null.
	 */
    public void assertNotNull(String message, Object object) {
        assertTrue(message, object != null);
    }

    /**
	 * Asserts that an object is null.
	 */
    public void assertNull(String message, Object object) {
        assertTrue(message, object == null);
    }

    /**
	 * Asserts that two objects refer to the same object. If they are not
	 * an AssertionFailedError is thrown.
	 */
    public void assertSame(String message, Object expected, Object actual) {
        if(expected == actual) {
            passNotSame(message, expected, actual);
        }
        else {
            failNotSame(message, expected, actual);
        }
    }

    /**
	 * Asserts that two objects does not refer to the same object.
	 * If they are an AssertionFailedError is thrown.
	 */
    public void assertNotSame(String message, Object expected, Object actual) {
        if(expected != actual) {
            pass(message);
        }
        else {
            fail(message + " expected different objects");
        }
    }


    /**
	 * Method for logging exceptions. The tested code may throw an
	 * exception that is a subclass of the specified, so the parameter
	 * <code>want</code> specifies the expected class. If the parameter
	 * <code>got</code> is of the wanted type (or a subtype of the
	 * wanted type), the classname of <code>want</code> is logged. If
	 * <code>got</code> is of an unexpected type, the classname of
	 * <code>got</code> is logged.
	 *
	 * @param message the log description
	 * @param want the exception that is specified to be thrown
	 * @param got the exception that was thrown
	 */
    public void assertException(String message, Class want, Throwable got) {
        String formatted = "";

        if(message != null) {
            formatted = message + " ";
        }

        if(want.isInstance(got)) {
            pass(formatted + want.getName());
        }
        else {
            fail(formatted + "expected:[" + want.getName()
                 + "] but was:[" + got.getClass().getName() + "]");
        }
    }


    /*** Formatting methods ***/



    private void failNotEquals(String message, Object expected, Object actual) {
        String formatted = "";

        if(message != null) {
            formatted = message + " ";
        }

        fail(formatted + "expected:[" + expected + "] but was:[" + actual + "]");
    }

    private void failNotSame(String message, Object expected, Object actual) {
        String formatted = "";

        if(message != null) {
            formatted = message + " ";
        }

        fail(formatted + "expected same");
    }

    private void passNotEquals(String message, Object expected, Object actual) {
        String formatted = "";

        if(message != null) {
            formatted = message + " ";
        }

        pass(formatted + "expected:[" + expected + "] and correctly got:[" + actual + "]");
    }

    private void passNotSame(String message, Object expected, Object actual) {
        String formatted = "";

        if(message != null) {
            formatted = message + " ";
        }

        pass(formatted + "same as expected");
    }
	
	
	
	boolean objectEquals(Comparator comparator, List expected, List actual ) {
		if ( expected.size() != actual.size() )
			return false;
		
		boolean result = true;
		
		for ( int i=0; result && i<expected.size(); i++ )
			result = objectEquals( comparator,  expected.get(i), actual.get(i) );
			
		return result;
	}
		
	boolean objectEquals(Comparator comparator, Dictionary expected, Dictionary actual ) {
		if ( expected.size() != actual.size() )
			return false;
		
		boolean result = true;
		
		for ( Enumeration e=expected.keys(); result && e.hasMoreElements(); ) {
			Object key = e.nextElement();
			Object expectedValue = expected.get(key);
			Object actualValue = actual.get(key);
			result = objectEquals( comparator, expectedValue, actualValue );
		}
		return result;
	}
}

