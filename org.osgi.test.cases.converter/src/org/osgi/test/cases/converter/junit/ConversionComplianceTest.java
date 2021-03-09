/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
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
