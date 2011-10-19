package org.osgi.impl.service.tr069todmt;

import static org.osgi.impl.service.tr069todmt.Converter.*;

import java.util.*;
import java.util.regex.*;

import org.osgi.service.dmt.*;
import org.osgi.service.tr069todmt.*;

/**
 * @author aqute
 * 
 */
public class TR069ConnectorImpl implements TR069Connector {
	// object ::= NAME INSTANCE? ( parameter | object ) *
	// parameter ::= NAME VALUE
	// path ::= parameter-path | object-path | table-path
	// object-path ::= segment+
	// segment ::= NAME ’.’ ( instance ’.’ )?
	// parameter-path ::= object-path NAME
	// table-path ::= segment* NAME ’.’
	// instance ::= INTEGER

	static final String NAME = "[\\p{L}_][-\\p{L}\\p{Digit}\\p{CombiningDiacriticalMarks}_]*";
	static final String segment = NAME + "(\\.(\\d+|\\[" + NAME + "\\]))?";
	static final String object_path = "(" + segment + ")+";
	static final String parameter_path = object_path + "(" + NAME + ")";
	static final String table_path = "(" + segment + ")*(" + NAME + ")\\.";

	final DmtSession session;
	final Node root;
	final TR069FactoryImpl factory;

	/**
	 * @param factory 
	 * @param session
	 */
	public TR069ConnectorImpl(TR069FactoryImpl factory, DmtSession session) {
		this.session = session;
		this.root = new Node(session);
		this.factory =factory;
	}

	/**
	 * 
	 */

	@Override
	public void setParameterValue(String parameterPath, String value, int type)
			throws TR069Exception {
		try {
			if (parameterPath.endsWith(".Alias")) {
				setAlias(parameterPath, value);
				return;
			}

			if (parameterPath.endsWith("NumberOfEntries"))
				error("Cannot set a NumberOfEntries parameter",
						TR069Exception.NON_WRITABLE_PARAMETER);

			Node node = root.getDescendantFromPath(parameterPath, true);
			if (!node.exists())
				error("Non existent node for setParameterValue "
						+ node.getUri(), TR069Exception.INVALID_PARAMETER_NAME);

			DmtData data = convert(value, node.getMetaNode(), type);
			node.setValue(data);
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	/**
	 * 
	 */
	@Override
	public ParameterValue getParameterValue(String parameterPath)
			throws TR069Exception {

		try {
			if (parameterPath.endsWith(".Alias"))
				return getAlias(parameterPath);

			if (parameterPath.endsWith("NumberOfEntries"))
				return getNumberOfEntries(parameterPath);

			Node node = root.getDescendantFromPath(parameterPath, false);
			DmtData data = node.getDmtValue();
			return convert(data, node.getMetaNode());
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	@Override
	public Collection<ParameterInfo> getParameterNames(
			String objectOrTablePath, boolean nextLevel) throws TR069Exception {
		try {
			Node subTree = root.getDescendantFromPath(objectOrTablePath, false);
			List<ParameterInfo> result = new ArrayList<ParameterInfo>();
			if (nextLevel) {
				result.addAll(subTree.getChildren());
			} else {
				addAll(result, subTree);
			}
			return Collections.unmodifiableCollection(result);
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	private void addAll(List<ParameterInfo> result, Node subTree) {

	}

	static Pattern TABLE_PATH_WITH_ALIAS = Pattern.compile( table_path + "(\\[("+NAME+")\\])?");
	
	@Override
	public String addObject(String tablePath) throws TR069Exception {
		try {
			Matcher matcher = TABLE_PATH_WITH_ALIAS.matcher(tablePath);
			if (!matcher.matches()) 
				error("Not a proper table path",
						TR069Exception.NON_WRITABLE_PARAMETER);
			
			String basePath = matcher.group(1);
			Node node = root.getDescendantFromPath(basePath, false);
			if ( !node.exists())
				error("Table does not exist",
						TR069Exception.INVALID_PARAMETER_NAME);
				
			String alias = matcher.group(6); // TODO correct index
			if ( alias != null ) {
				Node child = node.getDescendantFromPath(alias, false);
				if ( !child.exists())
					return alias;
				error("Alias used already exists "+alias,
						TR069Exception.INVALID_PARAMETER_NAME);
			}

			// limit the number of attempts
			for (int i=0; i<1000; i++) {
				String id = ""+factory.assignId();
				Node child = node.getDescendantFromPath(id, false);
				if ( !child.exists())
					return id;
			}
			error("Could not find a suitable id",
					TR069Exception.INTERNAL_ERROR);
			return null;
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	@Override
	public void deleteObject(String objectName) throws TR069Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String toURI(String path, boolean create) throws TR069Exception {
		try {
			Node child = root.getDescendantFromPath(path, create);
			return child.getUri();
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	public void close() {

	}

	@Override
	public String toPath(String uri) throws TR069Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private void setAlias(String parameterPath, String value) {
		// TODO Auto-generated method stub

	}

	private ParameterValue getAlias(String parameterPath) {

		return null;
	}

	private ParameterValue getNumberOfEntries(String parameterPath) {
		return null;
	}

	private void error(String string, int nonWritableParameter) {
		// TODO Auto-generated method stub

	}

}
