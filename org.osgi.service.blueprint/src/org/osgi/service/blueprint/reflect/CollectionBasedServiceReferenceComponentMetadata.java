package org.osgi.service.blueprint.reflect;

/**
 * Service reference that binds to a collection of matching services from
 * the OSGi service registry.
 *
 */
public interface CollectionBasedServiceReferenceComponentMetadata extends
		ServiceReferenceComponentMetadata {
	
		/**
		 * Create natural ordering based on comparison on service objects.
		 */
		public static final int ORDER_BASIS_SERVICES = 1;
		
		/**
		 * Create natural ordering based on comparison of service reference objects.
		 */
		public static final int ORDER_BASIS_SERVICE_REFERENCES = 2;

		/**
		 * The type of collection to be created.
		 * 
		 * @return Class object for the specified collection type (List, Set, Map).
		 */
		Class getCollectionType();
	
		/**
		 * The comparator specified for ordering the collection, or null if no
		 * comparator was specified.
		 * 
		 * @return if a comparator was specified then a Value object identifying the 
		 * comparator (a ComponentValue, ReferenceValue, or ReferenceNameValue) is 
		 * returned. If no comparator was specified then null will be returned.
		 */
		Value getComparator();
		
		/**
		 * Should the collection be ordered based on natural ordering? 
		 * 
		 * @return true, iff natural-ordering based sorting was specified.
		 */
		boolean isNaturalOrderingBasedComparison();
		
		/**
		 * The basis on which to perform natural ordering, if specified.
		 * 
		 * @return one of ORDER_BASIS_SERVICES and ORDER_BASIS_SERVICE_REFERENCES
		 */
		int getNaturalOrderingComparisonBasis();
}
