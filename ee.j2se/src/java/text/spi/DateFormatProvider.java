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

package java.text.spi;
public abstract class DateFormatProvider extends java.util.spi.LocaleServiceProvider {
	protected DateFormatProvider() { } 
	public abstract java.text.DateFormat getDateInstance(int var0, java.util.Locale var1);
	public abstract java.text.DateFormat getDateTimeInstance(int var0, int var1, java.util.Locale var2);
	public abstract java.text.DateFormat getTimeInstance(int var0, java.util.Locale var1);
}

