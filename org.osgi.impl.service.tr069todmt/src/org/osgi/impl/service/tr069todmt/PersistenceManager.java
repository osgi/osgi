
package org.osgi.impl.service.tr069todmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.Uri;
import org.osgi.service.log.LogService;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 *
 */
public class PersistenceManager {

	private static final String				TEMP_TREE_FILE			= "tree.dat";

	private static final Comparator<String>	LIST_NAMES_COMPARATOR	= new Comparator<String>() {

																		public int compare(String s1, String s2) {
																			try {
																				int i1 = Integer.parseInt(s1);
																				int i2 = Integer.parseInt(s2);
																				if (i1 == i2) {
																					return 0;
																				}
																				return i1 > i2 ? 1 : -1;
																			} catch (NumberFormatException e) {
																				return -s1.compareTo(s2);
																			}
																		}

																	};

	private TR069ConnectorFactoryImpl		factory;
	private MappingTable					mappingTable;
	private MappingTable					aliases;
	private SortedSet<String>				tree;

	/**
	 * @param factory
	 */
	public PersistenceManager(TR069ConnectorFactoryImpl factory) {
		this.factory = factory;
		mappingTable = new MappingTable();
		aliases = new MappingTable();
		tree = Collections.synchronizedSortedSet(new TreeSet<String>());
		try {
			load();
		} catch (Exception e) {
			factory.log(LogService.LOG_WARNING, "PersistenceManager cannot load mappings", e);
		}
	}

	boolean isNodeUri(DmtSession session, String nodeUri) {
		if (tree.contains(nodeUri)) {
			return true;
		}
		String instanceIdUri = getInstanceIdUri(session, nodeUri);
		if (tree.contains(instanceIdUri)) {
			return true;
		}
		String aliasedUri = getAliasedUri(session, instanceIdUri);
		return session.isNodeUri(aliasedUri);
	}

	private void addMapping(DmtSession session, String aliasedUri, long instanceNumber) {
		mappingTable.put(aliasedUri, Long.valueOf(instanceNumber));
		String instanceNumberString = String.valueOf(instanceNumber);
		String[] path = Uri.toPath(aliasedUri);
		String alias = path[path.length - 1];
		if (alias.equals(instanceNumberString)) {
			return;
		}
		aliases.put(getInstanceIdUri(session, aliasedUri), alias);
	}

	String getRenamedUri(String oldUri, String newName) {
		String[] nodePath = Uri.toPath(oldUri);
		nodePath[nodePath.length - 1] = newName;
		return Uri.toUri(nodePath);
	}

	String createInteriorNode(DmtSession session, String nodeUri, int instanceNumber, boolean eager) throws DmtException {
		checkSessionLock(session);

		String instanceIdUri = getInstanceIdUri(session, nodeUri);
		String parentUri = Node.getParentUri(instanceIdUri);

		boolean isInstanceNode = Node.isMultiInstanceNode(session, parentUri);
		if (!isInstanceNode) {
			checkListOrMapUri(session, parentUri);
		}

		String aliasedNodeUri = getAliasedUri(session, nodeUri);
		if (eager) {
			if (!session.isNodeUri(aliasedNodeUri)) {
				session.createInteriorNode(aliasedNodeUri);
			}
			if (instanceNumber > -1) {
				setInstanceIdValue(session, aliasedNodeUri, instanceNumber);
			} else {
				if (isInstanceNode) {
					tree.remove(instanceIdUri);
					mappingTable.remove(aliasedNodeUri);
					aliases.remove(instanceIdUri);
					aliasedNodeUri = Node.getParentUri(aliasedNodeUri);
					parentUri = Node.getParentUri(aliasedNodeUri);
				}
				Long mapping = (Long) mappingTable.get(aliasedNodeUri);
				if (mapping != null) {
					instanceNumber = mapping.intValue();
				} else {
					String[] path = Uri.toPath(instanceIdUri);
					String name = path.length > 0 ? path[path.length - 1] : "";
					if (Utils.INSTANCE_ID_PATTERN.matcher(name).matches()) {
						instanceNumber = Integer.parseInt(name);
						if (!isInstanceNumberFree(session, parentUri, instanceNumber)) {
							instanceNumber = generateInstanceId(session, parentUri);
						}
					} else {
						instanceNumber = generateInstanceId(session, parentUri);
					}
				}
				setInstanceIdValue(session, aliasedNodeUri, instanceNumber);
			}
			tree.remove(instanceIdUri);
			mappingTable.remove(aliasedNodeUri);
			aliases.remove(instanceIdUri);
		} else {
			if (instanceNumber > -1) {
				createNodeLazily(session, parentUri, aliasedNodeUri, instanceNumber);
			} else {
				if (isInstanceNode) {
					aliasedNodeUri = Node.getParentUri(aliasedNodeUri);
					parentUri = Node.getParentUri(aliasedNodeUri);
				}
				Long mapping = (Long) mappingTable.get(aliasedNodeUri);
				if (mapping != null) {
					addInstanceIdNode(parentUri + Uri.PATH_SEPARATOR + mapping);
				} else {
					String[] path = Uri.toPath(instanceIdUri);
					String name = path.length > 0 ? path[path.length - 1] : "";
					if (Utils.INSTANCE_ID_PATTERN.matcher(name).matches()) {
						instanceNumber = Integer.parseInt(name);
						if (!isInstanceNumberFree(session, parentUri, instanceNumber)) {
							instanceNumber = generateInstanceId(session, parentUri);
						}
					} else {
						instanceNumber = generateInstanceId(session, parentUri);
					}
					createNodeLazily(session, parentUri, aliasedNodeUri, instanceNumber);
				}
			}
			instanceIdUri = getInstanceIdUri(session, nodeUri);
			if (!(session.isNodeUri(aliasedNodeUri) || tree.contains(instanceIdUri))) {
				tree.add(instanceIdUri);
			}
		}
		return Node.getNodeName(aliasedNodeUri);
	}

	private void setInstanceIdValue(DmtSession session, String aliasedNodeUri, int instanceNumber) throws DmtException {
		if (instanceNumber < 0) {
			return;
		}
		String instanceIdNodeUri = aliasedNodeUri + Uri.PATH_SEPARATOR + Utils.INSTANCE_ID;
		DmtData value = new DmtData((long) instanceNumber);
		if (session.isLeafNode(instanceIdNodeUri)) {
			session.setNodeValue(aliasedNodeUri, value);
		} else {
			session.createLeafNode(instanceIdNodeUri, value);
			tree.remove(getInstanceIdUri(session, instanceIdNodeUri));
		}
	}

	private void createNodeLazily(DmtSession session, String parentUri, String aliasedNodeUri, int instanceNumber) {
		addMapping(session, aliasedNodeUri, instanceNumber);
		addInstanceIdNode(parentUri + Uri.PATH_SEPARATOR + instanceNumber);
	}

	private void addInstanceIdNode(String nodeUri) {
		String instanceIdUri = nodeUri + Uri.PATH_SEPARATOR + Utils.INSTANCE_ID;
		if (!tree.contains(instanceIdUri)) {
			tree.add(instanceIdUri);
		}
	}

	String getNodeType(DmtSession session, String aliasedNodeUri) throws DmtException {
		if (tree.contains(getInstanceIdUri(session, aliasedNodeUri))) {
			return null;
		}
		return session.getNodeType(aliasedNodeUri);
	}

	void renameNode(DmtSession session, String nodeUri, String newName) throws DmtException {
		checkSessionLock(session);
		String aliasedUri = getAliasedUri(session, nodeUri);
		session.renameNode(aliasedUri, newName);

		String[] nodePath = Uri.toPath(aliasedUri);
		nodePath[nodePath.length - 1] = newName;

		String newUri = getRenamedUri(aliasedUri, newName);

		Object mapping = mappingTable.get(aliasedUri);
		if (mapping != null) {
			nodePath[nodePath.length - 1] = mapping.toString();
			String instanceUri = Uri.toUri(nodePath);
			aliases.rename(instanceUri, newUri);
			aliases.put(instanceUri, newName);
		}
		mappingTable.rename(aliasedUri, newUri);

		String[] nodes = tree.toArray(new String[tree.size()]);
		String instanceIdUri = getInstanceIdUri(session, nodePath);
		newUri = getRenamedUri(instanceIdUri, newName);
		String oldPath = instanceIdUri.concat(Uri.PATH_SEPARATOR);
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].startsWith(oldPath) || nodes[i].equals(instanceIdUri)) {
				tree.remove(nodes[i]);
				tree.add(nodes[i].replaceAll(instanceIdUri, newUri));
			}
		}
	}

	void setNodeValue(DmtSession session, String nodeUri, DmtData value) throws DmtException {
		checkSessionLock(session);
		createLeafNode(session, getAliasedUri(session, nodeUri), true, value);
	}

	void createLeafNode(DmtSession session, String aliasedNodeUri, boolean checkParents, DmtData value) throws DmtException {
		if (session.isLeafNode(aliasedNodeUri)) {
			if (value != null) {
				session.setNodeValue(aliasedNodeUri, value);
			}
			return;
		}
		String parentUri = Node.getParentUri(aliasedNodeUri);
		if (checkParents) {
			if (parentUri.length() > 0) {
				/* create missing parents */
				String[] path = Uri.toPath(parentUri);
				String currentAliasedNode = "";
				for (int i = 0; i < path.length; i++) {
					currentAliasedNode = (i == 0 ? currentAliasedNode : currentAliasedNode + Uri.PATH_SEPARATOR) + path[i];
					if (!session.isNodeUri(currentAliasedNode)) {
						Long instanceNumber = (Long) mappingTable.get(currentAliasedNode);
						createInteriorNode(session, currentAliasedNode, instanceNumber == null ? -1 : instanceNumber.intValue(), true);
					}
				}
			}
		} else if (aliasedNodeUri.endsWith(Utils.INSTANCE_ID)) {
			Long instanceNumber = (Long) mappingTable.get(Node.getParentUri(aliasedNodeUri));
			if (value == null && instanceNumber != null) {
				value = new DmtData(instanceNumber.longValue());
			}
		}
		if (!Node.isMultiInstanceNode(session, parentUri)) {
			throw new TR069Exception("The " + aliasedNodeUri + " is not a valid node!", TR069Exception.INVALID_PARAMETER_NAME);
		}
		session.createLeafNode(aliasedNodeUri, value);
		tree.remove(getInstanceIdUri(session, aliasedNodeUri));
	}

	private String getAliasedUri(DmtSession session, String nodeUri) {
		String[] path = Uri.toPath(nodeUri);
		String currentNode = "";
		for (int i = 0; i < path.length; i++) {
			currentNode = (i == 0 ? currentNode : currentNode + Uri.PATH_SEPARATOR) + path[i];
			/* check if the currentNode is not already aliased */
			Long instanceNumber = (Long) mappingTable.get(currentNode);
			if (instanceNumber == null) {
				String alias = getAlias(session, currentNode);
				if (alias != null) {
					currentNode = getRenamedUri(currentNode, alias);
				} else if (Utils.INSTANCE_ID_PATTERN.matcher(path[i]).matches()) {
					try {
						instanceNumber = Long.parseLong(path[i]);
						String parentUri = currentNode.length() == path[i].length() ? "" : currentNode.substring(0, currentNode.length() - path[i].length() - 1);
						String[] children = getChildNodeNames(session, parentUri, true);
						int j = 0;
						for (; j < children.length; j++) {
							if (children[j].equals(path[i])) {
								break;
							}
							String instanceIDUri = parentUri + Uri.PATH_SEPARATOR + children[j] + Uri.PATH_SEPARATOR + Utils.INSTANCE_ID;
							if (session.isNodeUri(instanceIDUri)) {
								if (instanceNumber.equals(Long.valueOf(session.getNodeValue(instanceIDUri).getLong()))) {
									currentNode = getRenamedUri(currentNode, children[j]);
									break;
								}
							}
						}
						if (j == children.length && DmtConstants.DDF_LIST.equals(getNodeType(session, parentUri))) {
							Arrays.sort(children, LIST_NAMES_COMPARATOR);
							String last = children[children.length - 1];
							try {
								int res = Integer.parseInt(last);
								if (res == Integer.MAX_VALUE) {
									throw new TR069Exception("The maximum number of " + parentUri + " children is reached!", TR069Exception.RESOURCES_EXCEEDED);
								} else {
									currentNode = getRenamedUri(currentNode, String.valueOf(++res));
								}
							} catch (NumberFormatException e) {
								/* Nothing to do here */
							}
						}
					} catch (DmtException e) {
						/* Nothing to do here */
					}
				}
			}
		}
		return currentNode;
	}

	private String getInstanceIdUri(DmtSession session, String nodeUri) {
		return getInstanceIdUri(session, Uri.toPath(nodeUri));
	}

	private String getInstanceIdUri(DmtSession session, String[] path) {
		String currentNode = "";
		for (int i = 0; i < path.length; i++) {
			currentNode = (i == 0 ? currentNode : currentNode + Uri.PATH_SEPARATOR) + path[i];
			/* check if the currentNode is not already instanceIdUri */
			String alias = (String) aliases.get(currentNode);
			if (alias == null) {
				int instanceNumber = getInstanceNumber(session, currentNode);
				if (instanceNumber > -1) {
					currentNode = getRenamedUri(currentNode, String.valueOf(instanceNumber));
				}
			}
		}
		return currentNode;
	}

	void deleteNode(DmtSession session, String nodeUri) throws DmtException {
		checkSessionLock(session);
		if (session.isNodeUri(nodeUri)) {
			session.deleteNode(nodeUri);
		}

		/* remove the whole subtree */
		String[] nodes = tree.toArray(new String[tree.size()]);
		String prefix = nodeUri.concat(Uri.PATH_SEPARATOR);
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].startsWith(prefix) || nodes[i].equals(nodeUri)) {
				tree.remove(nodes[i]);
			}
		}

		Object instanceNumber = mappingTable.remove(nodeUri);
		if (instanceNumber != null) {
			aliases.remove(getRenamedUri(nodeUri, instanceNumber.toString()));
		}
	}

	String getAlias(DmtSession session, String nodeUri) {
		String alias = (String) aliases.get(nodeUri);
		if (alias == null) {
			String aliasUri = nodeUri + Uri.PATH_SEPARATOR_CHAR + Utils.ALIAS;
			if (session.isNodeUri(aliasUri)) {
				try {
					alias = session.getNodeValue(aliasUri).getString();
					aliases.put(nodeUri, alias);
				} catch (DmtException e) {
					factory.log(LogService.LOG_WARNING, null, e);
					return null;
				}
			} else {
				return null;
			}
		}
		return alias;
	}

	int getInstanceNumber(DmtSession session, String nodeUri) {
		String aliasedUri = getAliasedUri(session, nodeUri);
		Long mapping = (Long) mappingTable.get(aliasedUri);
		if (mapping == null) {
			String instanceIDUri = aliasedUri + Uri.PATH_SEPARATOR_CHAR + Utils.INSTANCE_ID;
			if (session.isNodeUri(instanceIDUri)) {
				try {
					mapping = Long.valueOf(session.getNodeValue(instanceIDUri).getLong());
				} catch (DmtException e) {
					return -1;
				}
			} else {
				return -1;
			}
		}
		return mapping.intValue();
	}

	int generateInstanceId(DmtSession session, String parentUri) throws DmtException {
		/*
		 * The Connector must ensure that any id chosen is not actually already
		 * in use or has been handed out recently
		 */
		int[] instances = getInstances(session, parentUri);
		if (instances == null || instances.length == 0) {
			return 1;
		}
		int res = instances[instances.length - 1];
		if (res == Integer.MAX_VALUE) {
			for (int i = 1, insert; i < Integer.MAX_VALUE; i++) {
				insert = Arrays.binarySearch(instances, i);
				if (insert < 0) {
					try {
						return instances[-insert - 2] + 1;
					} catch (NumberFormatException e) {
						return 1;
					}
				}
			}
			throw new TR069Exception("The maximum number of " + parentUri + " children is reached!", TR069Exception.RESOURCES_EXCEEDED);
		} else {
			return ++res;
		}
	}

	private boolean isInstanceNumberFree(DmtSession session, String parentUri, int instanceNumber) throws DmtException {
		int[] instances = getInstances(session, parentUri);
		if (instances == null || instances.length == 0) {
			return true;
		}
		return Arrays.binarySearch(instances, instanceNumber) != 0;
	}

	String[] getChildNodeNames(DmtSession session, String aliasedParentUri, boolean aliasedNames) throws DmtException {
		ArrayList<String> children = new ArrayList<String>();
		if (!session.isLeafNode(aliasedParentUri) && session.isNodeUri(aliasedParentUri)) {
			children.addAll(Arrays.asList(session.getChildNodeNames(aliasedParentUri)));
		}
		String[] nodes = tree.toArray(new String[tree.size()]);
		String nodeUriPrefix = getInstanceIdUri(session, aliasedParentUri).concat(Uri.PATH_SEPARATOR);
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].startsWith(nodeUriPrefix)) {
				String name = nodes[i].substring(nodeUriPrefix.length());
				if (name.indexOf(Uri.PATH_SEPARATOR) == -1) {
					if (aliasedNames) {
						String alias = getAlias(session, nodes[i]);
						alias = alias == null ? name : alias;
						if (!children.contains(alias)) {
							children.add(alias);
						}
					} else {
						/* in trees paths are kept with instanceNumbers */
						if (!children.contains(name)) {
							children.add(name);
						}
					}
				}
			}
		}
		return children.toArray(new String[children.size()]);
	}

	private int[] getInstances(DmtSession session, String aliasedParentUri) throws DmtException {
		String parentType = getNodeType(session, aliasedParentUri);
		if (DmtConstants.DDF_MAP.equals(parentType) || DmtConstants.DDF_LIST.equals(parentType)) {
			String[] children = getChildNodeNames(session, aliasedParentUri, true);
			int[] result = new int[children.length];
			for (int i = 0; i < children.length; i++) {
				result[i] = getInstanceNumber(session, aliasedParentUri + Uri.PATH_SEPARATOR + children[i]);
			}
			Arrays.sort(result);
			return result;
		}
		throw new IllegalArgumentException(aliasedParentUri + " is not a map/list node");
	}

	void checkSessionLock(DmtSession session) throws TR069Exception {
		/*
		 * If a non-atomic session is used then the TR069 Connector must not
		 * attempt to lazily create objects and reject any addObject(String) and
		 * deleteObject(String) methods
		 */

		if (session.getLockType() != DmtSession.LOCK_TYPE_ATOMIC) {
			throw new TR069Exception("Cannot add/delete objects and set parameter values in a non-atomic session");
		}
	}

	void checkListOrMapUri(DmtSession session, String uri) {
		if (factory.persistenceManager.isNodeUri(session, uri)) {
			if (!Node.isMultiInstanceParent(session, uri)) {
				throw new TR069Exception("The " + uri + " is not a valid Map/List uri! Only nodes under a MAP or LIST can be created!", TR069Exception.INVALID_PARAMETER_NAME);
			}
		} else {
			throw new TR069Exception("The " + uri + " is not a valid node!", TR069Exception.INVALID_PARAMETER_NAME);
		}
	}

	private void load() throws Exception {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				factory.context.getDataFile(TEMP_TREE_FILE)
				));
		tree = (TreeSet<String>) in.readObject();
		mappingTable = (MappingTable) in.readObject();
		aliases = (MappingTable) in.readObject();
		in.close();
	}

	void close() throws Exception {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
				factory.context.getDataFile(TEMP_TREE_FILE)
				));
		out.writeObject(tree);
		out.writeObject(mappingTable);
		out.writeObject(aliases);
		out.close();
	}

}
