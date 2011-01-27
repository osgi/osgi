/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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
package org.osgi.test.cases.framework.weaving.tbx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;

/**
 * This class is used to weave the test classes
 * 
 * @author IBM
 */
public class ConfigurableWeavingHook implements WeavingHook {

	/** 
	 * The expected value of the constant in the UTF8 pool
	 * that needs to be changed.
	 */
	private String expected = "DEFAULT";
	
	/** The value to change the UTF8 constant to */
	private String changeTo = "WOVEN";
	
	/** A list of dynamic imports to add */
	private List dynamicImports = new ArrayList();
	
	/** 
	 * Set to true when this hook is called for one of the
	 * test weaving classes
	 */
	private boolean called;
	
	/** An exception to throw instead of weaving the class */
	private RuntimeException toThrow;
	
	/**
	 * Set the expected value of the Constant in the class to be woven
	 * @param expected
	 */
	public void setExpected(String expected) {
		this.expected = expected;
	}
	
	/**
	 * Set the value of the Constant to weave into the class
	 * @param expected
	 */
	public void setChangeTo(String changeTo) {
		this.changeTo = changeTo;
	}
	
	/**
	 * Add a dynamic import
	 * @param importString
	 */
	public void addImport(String importString) {
		dynamicImports.add(importString);
	}
	
	/**
	 * Has this weaving hook been called for a test class
	 * @return
	 */
	public boolean isCalled() {
		return called;
	}
	
	/** Reset {@link #isCalled()} */
	public void clearCalls() {
		called = false;
	}
	
	/**
	 * Set an exception to throw instead of weaving the class
	 * @param re
	 */
	public void setExceptionToThrow(RuntimeException re) {
		toThrow = re;
	}
	

	public void weave(WovenClass wovenClass) {
		
		// We are only interested in classes that are in the test
		if(wovenClass.getClassName().startsWith(TestConstants.TESTCLASSES_PACKAGE)) {
		    
			called = true;
			//If there is an exception, throw it and prevent it being thrown again
			if(toThrow != null) {
				try {
					throw toThrow;
				} finally {
					toThrow = null;
				}
			}
			// Load the class and change the UTF8 constant
			ClassParser parser = new ClassParser(new ByteArrayInputStream(
					wovenClass.getBytes()), null);
			
			try {
				//Create a new class based on the old one
				ClassGen generator = new ClassGen(parser.parse());

				//Create a new constant
				ConstantPoolGen factory = new ConstantPoolGen();
				Constant c = factory.getConstant(factory.addUtf8(changeTo));
				
				//Find the old constant
				int location = generator.getConstantPool().lookupUtf8(expected);
				
				if(location < 0)
					throw new RuntimeException("Unable to locate the expected " + expected +
							" in the constant pool " + generator.getConstantPool());
				
				//Replace the constant
				generator.getConstantPool().setConstant(location, c);				
				
				//Get the new class as a byte[]
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				generator.getJavaClass().dump(baos);
				
				//Add any imports and set the new class bytes
				for(int i = 0; i < dynamicImports.size(); i++) {
					wovenClass.getDynamicImports().add(dynamicImports.get(i));
				}
				wovenClass.setBytes(baos.toByteArray());
				
			} catch (Exception e) {
				//Throw on any IllegalArgumentException as this comes from
				//The dynamic import. Anything else is an error and should be
				//wrapped and thrown.
				if(e instanceof IllegalArgumentException)
					throw (IllegalArgumentException)e;
				else 
					throw new RuntimeException(e);
			} 
		}
	}

	/**
	 * Register this hook using the supplied context
	 * @param ctx
	 * @return
	 */
	public ServiceRegistration register(BundleContext ctx) {
		return register(ctx, 0);
	}
	
	/**
	 * Register this hook using the supplied context and ranking
	 * @param ctx
	 * @param rank
	 * @return
	 */
	public ServiceRegistration register(BundleContext ctx, int rank) {
		Hashtable table = new Hashtable();
		table.put(Constants.SERVICE_RANKING, new Integer(rank));
		return ctx.registerService(WeavingHook.class.getName(), this, table);
	}
}
