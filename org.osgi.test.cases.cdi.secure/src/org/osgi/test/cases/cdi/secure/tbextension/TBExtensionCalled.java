package org.osgi.test.cases.cdi.secure.tbextension;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Documented
@Retention(RUNTIME)
@Target({
		TYPE, FIELD, METHOD, PARAMETER
})
@Qualifier
public @interface TBExtensionCalled {
}
