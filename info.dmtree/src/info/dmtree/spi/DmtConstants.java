package info.dmtree.spi;

public interface DmtConstants {
	
	/**
	 * A string defining a DDF URI, indicating that the node is a list subtree.
	 */
	static final String DDF_LIST_SUBTREE = "org.osgi/1.0/ListSubtree";

	/**
	 * A string defining a DDF URI, indicating that the node is a scaffold node.
	 */
	static final String DDF_SCAFFOLD = "org.osgi/1.0/ScaffoldNode";
	
	
	
	/**
	 * a string defining the topic for the event that is sent for added nodes. 
	 * The value of this field is “info/dmtree/DmtEvent/ADDED”.
	 */
	static final String EVENT_TOPIC_ADDED = "info/dmtree/DmtEvent/ADDED";

	/**
	 * a string defining the topic for the event that is sent for deleted nodes. 
	 * The value of this field is “info/dmtree/DmtEvent/DELETED”.
	 */
	static final String EVENT_TOPIC_DELETED = "info/dmtree/DmtEvent/DELETED";
	
	/**
	 * a string defining the topic for the event that is sent for replaced nodes. 
	 * The value of this field is “info/dmtree/DmtEvent/REPLACED”.
	 */
	static final String EVENT_TOPIC_REPLACED = "info/dmtree/DmtEvent/REPLACED";

	/**
	 * a string defining the topic for the event that is sent for renamed nodes. 
	 * The value of this field is “info/dmtree/DmtEvent/RENAMED”.
	 */
	static final String EVENT_TOPIC_RENAMED = "info/dmtree/DmtEvent/RENAMED";

	/**
	 * a string defining the topic for the event that is sent for copied nodes. 
	 * The value of this field is “info/dmtree/DmtEvent/COPIED”.
	 */
	static final String EVENT_TOPIC_COPIED = "info/dmtree/DmtEvent/COPIED";

	/**
	 * a string defining the property key for the “session.id” property in node related events
	 */
	static final String EVENT_PROPERTY_SESSION_ID = "session.id";
	
	/**
	 * a string defining the property key for the “nodes” property in node related events
	 */
	static final String EVENT_PROPERTY_NODES = "nodes";

	/**
	 * a string defining the property key for the “newnodes” property in node related events
	 */
	static final String EVENT_PROPERTY_NEW_NODES = "newnodes";

	/**
	 * a string defining the property key for the “list.nodes” property in node related events
	 */
	static final String EVENT_PROPERTY_LIST_NODES = "list.nodes";

	/**
	 * a string defining the property key for the “list.upcoming.event” property in node related events
	 */
	static final String EVENT_PROPERTY_LIST_UPCOMING_EVENT = "list.upcoming.event";

	
}
