package org.osgi.test.cases.tr069todmt.plugins;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;

/**
 * @author steffen
 *
 */
public class Node implements Comparable {
	
	private String name;
	private DmtData value;
	private String type;
	private boolean leaf;

	private MetaNode metaNode;
	private MetaNode listElementMetaNode;
	private MetaNode mapElementMetaNode;
	private String title;
	
	private Node parent;
	private TreeSet<Node> children;

	
	/**
	 * @param parent
	 * @param name
	 * @param value
	 * @param type
	 */
	public Node( Node parent, String name, boolean leaf, DmtData value, String type ) {
		this.parent = parent;
		this.name = name;
		this.leaf = leaf;
		this.value = value;
		if ( value == null ) 
			this.value = DmtData.NULL_VALUE;
		this.type = type;
		if ( parent != null ) {
			parent.addChild(this);
		}
	}
	
	/**
	 * @return
	 */
	public String getURI() {
		return getPath();
	}
	
	private String getPath() {
		String path = name;
		if ( parent != null )
			path = parent.getPath() + "/" + path;
		return path;
	}
	
	private Node getChildNode( String name ) {
		if ( leaf )
			return null;
		Node node = null;
		for ( Node child : getChildren() )
			if ( name.equals( child.getName() ))
				node = child;
		return node;
	}

	void addChild( Node node ) {
		if ( leaf )
			return;
		getChildren().add(node);
		// set index as node name, if this is a list node 
		if ( isList() )
			node.setName("" + (getChildren().size() - 1));
	}

	void removeChild( Node node ) {
		if ( leaf )
			return;
		getChildren().remove(node);
		// reorder indexes if this is a list node
		if ( isList() ) {
			int index = 0;
			for (Node child : getChildren()) {
				child.setName("" + index );
				index ++;
			}
		}
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * returns the value of this node,
	 * if the node is an interior node of type DDF_LIST or DDF_MAP, 
	 * then it performs the necessary actions to return the list or map as complex values
	 * @return
	 */
	public DmtData getValue() {
		DmtData data = value;
		// check for ComplexValues
		if ( isList() ) {
			data = new DmtData( getList() );
		}
		else if ( isMap() ) {
			data = new DmtData(getMap());
		}
		return data;
	}

	/**
	 * sets the value for this node,
	 * if the node is an interior node of type DDF_LIST or DDF_MAP, 
	 * then it performs the necessary actions to set the list or map as complex values
	 * @param value 
	 * @return
	 */
	public void setValue(DmtData value) {
		if ( isList() ) {
			// clear children and set them new
			getChildren().clear();
			if ( value.getNode() instanceof Collection<?>)
				setListChildren((Collection<?>) value.getNode());
		}
		else if ( isMap() ) {
			getChildren().clear();
			if ( value.getNode() instanceof Map<?,?>)
				setMapChildren((Map<?,?>) value.getNode());
		}
		this.value = value;
	}

	/**
	 * @return
	 */
	public MetaNode getMetaNode() {
		if ( metaNode == null ) {
			if (parent != null && parent.isList())
				return parent.getListElementMetaNode();
			if (parent != null && parent.isMap())
				return parent.getMapElementMetaNode();
		}
				
		return metaNode;
	}

	/**
	 * @param metaNode
	 */
	public void setMetaNode(MetaNode metaNode) {
		this.metaNode = metaNode;
	}

	/**
	 * @return
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * @param parent
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * @return
	 */
	public TreeSet<Node> getChildren() {
		if ( children == null )
			children = new TreeSet<Node>();
		return children;
	}

	
	public String toString() {
		return getURI();
	}

	public int compareTo(Object o) {
		if ( o == null || ! (o instanceof Node) )
			return -1; 
		return this.getURI().compareTo(((Node)o).getURI());
	}
	
	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	private boolean isMap() {
		return DmtConstants.DDF_MAP.equals(getType());
	}

	private boolean isList() {
		return DmtConstants.DDF_LIST.equals(getType());
	}
	
	private Collection getList() {
		Vector v = new Vector();
		Set<Node> children = getChildren();
		if ( children.size() == 0 )
			return v;
		// use String format as default
		int format = DmtData.FORMAT_NULL;
		for (Node n : children) {
			// determine format for this list, use String format as default 
			if ( format == DmtData.FORMAT_NULL)
				format = n.getMetaNode() != null ? n.getMetaNode().getFormat() : DmtData.FORMAT_STRING;
			switch (format) {
			case DmtData.FORMAT_STRING:
				v.add(n.getValue().getString());
				break;
			case DmtData.FORMAT_BOOLEAN:
				v.add(Boolean.valueOf(n.getValue().getBoolean()));
				break;
			case DmtData.FORMAT_INTEGER:
				v.add(Integer.valueOf( n.getValue().getInt() ));
				break;
			case DmtData.FORMAT_LONG:
				v.add(Long.valueOf( n.getValue().getLong() ));
				break;
			case DmtData.FORMAT_FLOAT:
				v.add(Float.valueOf( n.getValue().getFloat() ));
				break;
			case DmtData.FORMAT_DATE_TIME:
				v.add(n.getValue().getDateTime());
				break;
			case DmtData.FORMAT_BINARY:
				v.add(n.getValue().getBinary());
				break;

			default:
				v.add(n.getValue());
				break;
			}
		}
		return v;
	}

	private void setListChildren(Collection<?> list ) {
		int index = 0; 
		for (Object element : list) {
			// create new node from element and add it as child
			Node n = new Node(this, "" + index, true, createDmtData(element), null);
			// the metadata is maintained by the parent
			index++;
		}
	}
	
	private void setMapChildren(Map<?,?> map ) {
		for (Object key : map.keySet()) {
			// create new node from element and add it as child
			// do we need a type here ?
			Node n = new Node(this, (String) key, true, createDmtData(map.get(key)), null);
			// the metadata is maintained by the parent
		}
	}

	private Map getMap() {
		Map m = new HashMap();
		Set<Node> children = getChildren();
		if ( children.size() == 0 )
			return m;
		int format = DmtData.FORMAT_NULL;
		for (Node n : children) {
			// determine format for this list, use String format as default 
			if ( format == DmtData.FORMAT_NULL)
				format = n.getMetaNode() != null ? n.getMetaNode().getFormat() : DmtData.FORMAT_STRING;
				
			switch (format) {
			case DmtData.FORMAT_STRING:
				m.put(n.getName(), n.getValue().getString());
				break;
			case DmtData.FORMAT_BOOLEAN:
				m.put(n.getName(), Boolean.valueOf(n.getValue().getBoolean()));
				break;
			case DmtData.FORMAT_INTEGER:
				m.put(n.getName(), Integer.valueOf( n.getValue().getInt() ));
				break;
			case DmtData.FORMAT_LONG:
				m.put(n.getName(), Long.valueOf( n.getValue().getLong() ));
				break;
			case DmtData.FORMAT_FLOAT:
				m.put(n.getName(), Float.valueOf( n.getValue().getFloat() ));
				break;
			case DmtData.FORMAT_DATE_TIME:
				m.put(n.getName(), n.getValue().getDateTime());
				break;
			case DmtData.FORMAT_BINARY:
				m.put(n.getName(), n.getValue().getBinary());
				break;

			default:
				m.put(n.getName(), n.getValue());
				break;
			}
		}
		return m;
	}

	public MetaNode getListElementMetaNode() {
		return listElementMetaNode;
	}

	public void setListElementMetaNode(MetaNode listElementMetaNode) {
		if ( leaf )
			return;
		this.listElementMetaNode = listElementMetaNode;
	}

	public MetaNode getMapElementMetaNode() {
		return mapElementMetaNode;
	}

	public void setMapElementMetaNode(MetaNode mapElementMetaNode) {
		if ( leaf )
			return;
		this.mapElementMetaNode = mapElementMetaNode;
	}
	
	public boolean isLeaf() {
		return leaf;
	}
	
	private DmtData createDmtData(Object value) {
		DmtData data = null;
		if ( value instanceof String )
			data = new DmtData((String) value);
		else if ( value instanceof Boolean )
			data = new DmtData(((Boolean) value).booleanValue());
		else if ( value instanceof Integer )
			data = new DmtData(((Integer) value).intValue());
		else if ( value instanceof Long )
			data = new DmtData(((Long) value).longValue());
		else if ( value instanceof Float )
			data = new DmtData(((Float) value).floatValue());
		else if ( value instanceof Date )
			data = new DmtData((Date) value);
		else if ( value instanceof byte[] )
			data = new DmtData((byte[]) value);
		else
			data = new DmtData(value);
		return data;
	}
	
}
