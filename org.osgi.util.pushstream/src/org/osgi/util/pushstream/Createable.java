package org.osgi.util.pushstream;

/**
 * The final stage of a Builder, used to create the final object
 *
 * @param <R> the type to be created
 */
public interface Createable<R> {

	/**
	 * @return the object being built
	 */
	R create();
}
