/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.util.converter;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings("javadoc")
public class UtilTest {
	@ParameterizedTest(name = "methodName=\"{0}\", key=\"{1}\"")
	@CsvSource({
			"'',''", //
			"a,a", //
			"ab,ab", //
			"abc,abc", //
			"a\u0008bc,a\bbc", //

			"$_$,-", //
			"$_,.", //
			"_$,.", //
			"x$_$,x-", //
			"$_$x,-x", //
			"abc$_$abc,abc-abc", //
			"$$_$x,$.x", //
			"$_$$,-", //
			"$_$$$,-$", //
			"$,''", //
			"$$,$", //
			"_,.", //
			"$_,.", //

			"myProperty143,myProperty143", //
			"$new,new", //
			"n$ew,new", //
			"new$,new", //
			"my$$prop,my$prop", //
			"dot_prop,dot.prop", //
			"_secret,.secret", //
			"another__prop,another_prop", //
			"three___prop,three_.prop", //
			"four_$__prop,four._prop", //
			"five_$_prop,five..prop" //
	})
	public void testMangling(String methodName, String key) {
		assertThat(Util.unMangleName(methodName)).isEqualTo(key);
    }
}
