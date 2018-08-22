/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
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
package org.osgi.impl.bundle.component.annotations;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 *
 *
 */
@Component(name = "testFactoryProperties", factory = "test", factoryProperties = "OSGI-INF/vendor.properties", factoryProperty = {
		"a=foo", "b:Integer=2", "b:Integer=3", "c:Boolean=true", "d:Long=4",
		"e:Double=5.0", "f:Float=6.0", "g:Byte=7", "h:Character=8",
		"i:Short=9", "j:String=bar"})
public class FactoryProperties {
	/**
	 */
	@Activate
	private void activate() {
		System.out.println("Hello World!");
	}

	/**
	 */
	@Deactivate
	private void deactivate() {
		System.out.println("Goodbye World!");
	}
}
