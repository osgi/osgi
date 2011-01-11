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
	 * A string defining the topic for the event that is sent for added nodes.
	 * <p>
	 * The value of this field is “info/dmtree/DmtEvent/ADDED”.
	 */
	static final String EVENT_TOPIC_ADDED = "info/dmtree/DmtEvent/ADDED";

	/**
	 * A string defining the topic for the event that is sent for deleted nodes.
	 * <p>
	 * The value of this field is “info/dmtree/DmtEvent/DELETED”.
	 */
	static final String EVENT_TOPIC_DELETED = "info/dmtree/DmtEvent/DELETED";

	/**
	 * A string defining the topic for the event that is sent for replaced
	 * nodes.
	 * <p>
	 * The value of this field is “info/dmtree/DmtEvent/REPLACED”.
	 */
	static final String EVENT_TOPIC_REPLACED = "info/dmtree/DmtEvent/REPLACED";

	/**
	 * A string defining the topic for the event that is sent for renamed nodes.
	 * <p>
	 * The value of this field is “info/dmtree/DmtEvent/RENAMED”.
	 */
	static final String EVENT_TOPIC_RENAMED = "info/dmtree/DmtEvent/RENAMED";

	/**
	 * A string defining the topic for the event that is sent for copied nodes.
	 * <p>
	 * The value of this field is “info/dmtree/DmtEvent/COPIED”.
	 */
	static final String EVENT_TOPIC_COPIED = "info/dmtree/DmtEvent/COPIED";

	/**
	 * A string defining the topic for the event that can be sent by plugins to
	 * indicate their intention to execute a destructive operation that might
	 * change the state of the system bundle, i.e. the framework.
	 * <p>
	 * The value of this field is “info/dmtree/DmtEvent/DESTRUCTIVE_OPERATION”.
	 */
	static final String EVENT_TOPIC_DESCTRUCTIVE_OPERATION = "info/dmtree/DmtEvent/DESTRUCTIVE_OPERATION";

	/**
	 * A string defining the property key for the “session.id” property in node
	 * related events.
	 */
	static final String EVENT_PROPERTY_SESSION_ID = "session.id";

	/**
	 * A string defining the property key for the “nodes” property in node
	 * related events.
	 */
	static final String EVENT_PROPERTY_NODES = "nodes";

	/**
	 * A string defining the property key for the “newnodes” property in node
	 * related events.
	 */
	static final String EVENT_PROPERTY_NEW_NODES = "newnodes";

	/**
	 * A string defining the property key for the “list.nodes” property in node
	 * related events.
	 */
	static final String EVENT_PROPERTY_LIST_NODES = "list.nodes";

	/**
	 * A string defining the property key for the “list.upcoming.event” property
	 * in node related events.
	 */
	static final String EVENT_PROPERTY_LIST_UPCOMING_EVENT = "list.upcoming.event";

}
