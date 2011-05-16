package org.osgi.test.support.signature;

public interface ISignatures {
	/**
	 * Modify the descriptor so that the differences in names do not count
	 * in the comparison. This is to handle that two signatures compare
	 * string wise if they model the same signature even if other type
	 * variable names have been used.
	 * 
	 * @param signature
	 * @return
	 */
	String normalize(String signature);
	
	/**
	 * Calculate the signature of the object o. o must be one of 
	 * Method, Field, Constructor or class.
	 * 
	 * @param o Method, Constructor, Field, or Class
	 * @return the generic descriptor
	 */
	String getSignature(Object o);
}
