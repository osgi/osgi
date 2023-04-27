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
package org.osgi.test.cases.converter.felix;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public interface MyGenericInterfaceOptional {

	Optional<String> text();

	Optional<String> textOptional();

	Optional<Optional<String>> textOptionalOptional();

	Optional<String> textNullSet();

	Optional<String> textNotSet();

	OptionalInt oInt();

	OptionalInt oIntNullSet();

	OptionalInt oIntNotSet();

	default OptionalInt optionalIntNotSetDefaultEmptyOptional() {
		return OptionalInt.empty();
	}

	OptionalDouble oDouble();

	OptionalDouble oDoubleNullSet();

	OptionalDouble oDoubleNotSet();

	default OptionalDouble optionalDoubleNotSetDefaultEmptyOptional() {
		return OptionalDouble.empty();
	}

	OptionalLong oLong();

	OptionalLong oLongNullSet();

	OptionalLong oLongNotSet();

	default OptionalLong optionalLongNotSetDefaultEmptyOptional() {
		return OptionalLong.empty();
	}

	default Optional<String> textNotSetDefaultEmptyOptional() {
		return Optional.empty();
	}

	default Optional<String> textNullSetDefaultEmptyOptional() {
		return Optional.of("value");
	}

	default Optional<String> textSetDefaultEmptyOptional() {
		return Optional.empty();
	}

	OptionalInt oIntBadValue();

	OptionalDouble oDoubleBadValue();

	OptionalLong oLongBadValue();
}
