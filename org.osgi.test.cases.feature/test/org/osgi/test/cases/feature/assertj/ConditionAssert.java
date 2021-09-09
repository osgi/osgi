package org.osgi.test.cases.feature.assertj;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.regex.Pattern;

import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Condition;
import org.assertj.core.api.ObjectAssertFactory;

public interface ConditionAssert {
	String regex_startWith_Expecting = "(?si).*Expecting.*";

	default <T> AbstractThrowableAssert<?, ?> failingHas(Condition<T> condition, T actual, String msg, Object... args) {
		return failingHas(condition, actual, String.format(msg, args));
	}

	default <T> AbstractThrowableAssert<?, ?> failingHas(Condition<T> condition, T actual, String msg) {
		String regex = regex_expecting_X_M_Y(actual, ConditionMethod.Has, msg);
		return failingHas(condition, actual).hasMessageMatching(regex);
	}

	default <T> AbstractThrowableAssert<?, ?> failingHas(Condition<T> condition, T actual) {
		return assertThatThrownBy(() -> passingHas(condition, actual)).isInstanceOf(AssertionError.class);
	}

	default <T> AbstractThrowableAssert<?, ?> failingIs(Condition<T> condition, T actual, String msg, Object... args) {
		return failingIs(condition, actual, String.format(msg, args));
	}

	default <T> AbstractThrowableAssert<?, ?> failingIs(Condition<T> condition, T actual, String msg) {
		String regex = regex_expecting_X_M_Y(actual, ConditionMethod.Is, msg);
		return assertThatThrownBy(() -> passingIs(condition, actual)).isInstanceOf(AssertionError.class)
			.hasMessageMatching(regex);
	}

	default <T> void passingHas(Condition<T> condition, T actual) {
		ObjectAssertFactory<T> factory = new ObjectAssertFactory<>();
		factory.createAssert(actual)
			.has(condition);
	}

	default <T> void passingIs(Condition<T> condition, T actual) {
		ObjectAssertFactory<T> factory = new ObjectAssertFactory<>();
		factory.createAssert(actual)
			.is(condition);
	}

	default String regex_expecting_X_M_Y(Object x, ConditionMethod m, Object y) {
		return String.format(regex_startWith_Expecting + "%s.*" + m + ".*%s.*", Pattern.quote(x.toString()), y);
	}

	default String rexex_expecting_X_M_Y_Z(Object x, ConditionMethod m, Object y, Object z) {
		return regex_expecting_X_M_Y(x, m, String.format("%s.*%s", y, z));
	}

}
