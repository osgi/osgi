/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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

package javax.management;
public class Query {
	public final static int DIV = 3;
	public final static int EQ = 4;
	public final static int GE = 2;
	public final static int GT = 0;
	public final static int LE = 3;
	public final static int LT = 1;
	public final static int MINUS = 1;
	public final static int PLUS = 0;
	public final static int TIMES = 2;
	public Query() { } 
	public static javax.management.QueryExp and(javax.management.QueryExp var0, javax.management.QueryExp var1) { return null; }
	public static javax.management.QueryExp anySubString(javax.management.AttributeValueExp var0, javax.management.StringValueExp var1) { return null; }
	public static javax.management.AttributeValueExp attr(java.lang.String var0) { return null; }
	public static javax.management.AttributeValueExp attr(java.lang.String var0, java.lang.String var1) { return null; }
	public static javax.management.QueryExp between(javax.management.ValueExp var0, javax.management.ValueExp var1, javax.management.ValueExp var2) { return null; }
	public static javax.management.AttributeValueExp classattr() { return null; }
	public static javax.management.ValueExp div(javax.management.ValueExp var0, javax.management.ValueExp var1) { return null; }
	public static javax.management.QueryExp eq(javax.management.ValueExp var0, javax.management.ValueExp var1) { return null; }
	public static javax.management.QueryExp finalSubString(javax.management.AttributeValueExp var0, javax.management.StringValueExp var1) { return null; }
	public static javax.management.QueryExp geq(javax.management.ValueExp var0, javax.management.ValueExp var1) { return null; }
	public static javax.management.QueryExp gt(javax.management.ValueExp var0, javax.management.ValueExp var1) { return null; }
	public static javax.management.QueryExp in(javax.management.ValueExp var0, javax.management.ValueExp[] var1) { return null; }
	public static javax.management.QueryExp initialSubString(javax.management.AttributeValueExp var0, javax.management.StringValueExp var1) { return null; }
	public static javax.management.QueryExp isInstanceOf(javax.management.StringValueExp var0) { return null; }
	public static javax.management.QueryExp leq(javax.management.ValueExp var0, javax.management.ValueExp var1) { return null; }
	public static javax.management.QueryExp lt(javax.management.ValueExp var0, javax.management.ValueExp var1) { return null; }
	public static javax.management.QueryExp match(javax.management.AttributeValueExp var0, javax.management.StringValueExp var1) { return null; }
	public static javax.management.ValueExp minus(javax.management.ValueExp var0, javax.management.ValueExp var1) { return null; }
	public static javax.management.QueryExp not(javax.management.QueryExp var0) { return null; }
	public static javax.management.QueryExp or(javax.management.QueryExp var0, javax.management.QueryExp var1) { return null; }
	public static javax.management.ValueExp plus(javax.management.ValueExp var0, javax.management.ValueExp var1) { return null; }
	public static javax.management.ValueExp times(javax.management.ValueExp var0, javax.management.ValueExp var1) { return null; }
	public static javax.management.ValueExp value(double var0) { return null; }
	public static javax.management.ValueExp value(float var0) { return null; }
	public static javax.management.ValueExp value(int var0) { return null; }
	public static javax.management.ValueExp value(long var0) { return null; }
	public static javax.management.ValueExp value(java.lang.Number var0) { return null; }
	public static javax.management.StringValueExp value(java.lang.String var0) { return null; }
	public static javax.management.ValueExp value(boolean var0) { return null; }
}

