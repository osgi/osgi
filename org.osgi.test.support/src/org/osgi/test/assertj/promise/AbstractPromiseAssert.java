/*
 * Copyright (c) OSGi Alliance (2019). All Rights Reserved.
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

package org.osgi.test.assertj.promise;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.ObjectAssert;
import org.osgi.util.promise.Promise;

public abstract class AbstractPromiseAssert<SELF extends AbstractPromiseAssert<SELF,RESULT>, RESULT>
		extends AbstractAssert<SELF,Promise<RESULT>> {

	public AbstractPromiseAssert(Promise<RESULT> actual, Class< ? > selfType) {
		super(actual, selfType);
	}

	public SELF isDone() {
		isNotNull();
		if (!actual.isDone()) {
			failWithMessage("%nExpecting%n  <%s>%nto be done.", actual);
		}
		return myself;
	}

	public SELF isNotDone() {
		isNotNull();
		if (actual.isDone()) {
			failWithMessage("%nExpecting%n  <%s>%nto not be done.", actual);
		}
		return myself;
	}

	public SELF doesResolve(long timeout, TimeUnit unit) {
		isNotNull();
		if (!actual.isDone()) {
			final CountDownLatch latch = new CountDownLatch(1);
			actual.onResolve(latch::countDown);
			try {
				if (!latch.await(timeout, unit)) {
					failWithMessage("%nExpecting%n  <%s>%nto have resolved.",
							actual);
				}
			} catch (InterruptedException e) {
				fail("unexpected exception", e);
			}
			isDone();
		}
		return myself;
	}

	public SELF doesNotResolve(long timeout, TimeUnit unit) {
		isNotDone();
		final CountDownLatch latch = new CountDownLatch(1);
		try {
			latch.await(timeout, unit);
		} catch (InterruptedException e) {
			fail("unexpected exception", e);
		}
		isNotDone();
		return myself;
	}

	Throwable getFailure() {
		try {
			return actual.getFailure();
		} catch (InterruptedException e) {
			fail("unexpected exception", e);
			return null;
		}
	}

	public AbstractThrowableAssert< ? , ? extends Throwable> hasFailedWithThrowableThat() {
		isDone();
		return assertThat(getFailure()).isNotNull();
	}

	public SELF hasFailed() {
		hasFailedWithThrowableThat();
		return myself;
	}

	public SELF hasNotFailed() {
		isNotNull();
		if (actual.isDone()) {
			Throwable fail = getFailure();
			if (fail != null) {
				failWithMessage(
						"%nExpecting%n  <%s>%nto have not failed but failed with <%s>.",
						actual, fail);
			}
		}
		return myself;
	}

	public SELF isSuccessful() {
		isDone();
		hasNotFailed();
		return myself;
	}

	RESULT getValue() {
		try {
			return actual.getValue();
		} catch (InvocationTargetException e) {
			failWithMessage(
					"%nExpecting%n  <%s>%nto have not failed but failed with <%s>.",
					actual, e.getCause());
			return null;
		} catch (InterruptedException e) {
			fail("unexpected exception", e);
			return null;
		}
	}

	public ObjectAssert<RESULT> hasValueThat() {
		isSuccessful();
		return assertThat(getValue());
	}

	public SELF hasValue(RESULT expected) {
		hasValueThat().isEqualTo(expected);
		return myself;
	}

	public SELF hasSameValue(RESULT expected) {
		hasValueThat().isSameAs(expected);
		return myself;
	}

	public SELF hasValueMatching(Predicate< ? super RESULT> predicate,
			String predicateDescription) {
		hasValueThat().matches(predicate, predicateDescription);
		return myself;
	}

	public SELF hasValueMatching(Predicate< ? super RESULT> predicate) {
		return hasValueMatching(predicate, "given");
	}
}
