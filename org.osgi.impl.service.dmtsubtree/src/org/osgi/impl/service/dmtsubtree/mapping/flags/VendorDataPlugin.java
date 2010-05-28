package org.osgi.impl.service.dmtsubtree.mapping.flags;


/**
 * This is just a flag-interface that is used to register
 * the plugin with or without its mapped path.
 * If the property "mappedNodePath" is missing, then this plugin is in pending state.
 * That means that there is no matching rootnode configured at this point in time. 
 * @author steffen
 *
 */
public class VendorDataPlugin implements MappedPath {
}
