package org.osgi.service.blueprint.reflect;

/**
 * Metadata for an entry. An entry is the member of a MapMetadata so that it
 * can be treated as a CollectionMetadata with entries.
 *
 * Defined in the <code>entry</code> element.
 *
 */
public interface MapEntry {
	/**
	 * Keys must be non-null.
	 *
	 * Defined in the <code>key</code> attribute or element.
	 *
	 * @return the metadata for the key
	 */
	NonNullMetadata getKey();

	/**
	 * Return the metadata for the value.
	 *
	 * Defined in the <code>value</code> attribute or element<.
	 *
	 * @return the metadata for the value
	 */

	Metadata getValue();
}
