/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.meg.demo.remote;

import java.io.*;
import java.util.*;
import org.osgi.service.dmt.*;

// TODO support non-recursive copy operation if necessary
public class CommandProcessor {
	
	private DmtAdmin	fact	= null;
	private DmtSession	session	= null;

	public CommandProcessor(DmtAdmin fact) {
		this.fact = fact;
	}

	public String processCommand(String command) {
		String ret = "";
		command = command.trim();
		if (command.length() == 0)
			return "";
		
		if (command.equals("help")) {
			return help();
		}
		
		String[] args = tokenize(command);
		String cmd = args[0].toLowerCase();
		String uri = null;
		if (args.length > 1 && args[1] != null) {
			uri = args[1]; // take care if protocol changes!
		}
		try {
			if (cmd.equals("open")) {
				session = fact.getSession(args[2], uri,
						DmtSession.LOCK_TYPE_ATOMIC);
				// TODO other lock types
			}
			else if (cmd.equals("close")) {
				session.close();
				session = null;
			}
            else if (cmd.equals("commit") || cmd.equals("c")) {
                session.commit();
            }
			else if (cmd.equals("rollback") || cmd.equals("r")) {
				session.rollback();
			}
			else if (cmd.equals("isnodeuri")) {
				boolean b = session.isNodeUri(uri);
				ret = String.valueOf(b);
			}
			else if (cmd.equals("getnodevalue") || cmd.equals("gv")) {
				DmtData data = session.getNodeValue(uri);
				ret = data.toString();
			}
			else if (cmd.equals("getnodetitle") || cmd.equals("gt")) {
				ret = session.getNodeTitle(uri);
			}
			else if (cmd.equals("getnodeversion")) {
				ret = String.valueOf(session.getNodeVersion(uri));
			}
			else if (cmd.equals("getnodetimestamp")) {
				Date d = session.getNodeTimestamp(uri);
				ret = d.toString();
			}
			else if (cmd.equals("getnodesize")) {
				ret = String.valueOf(session.getNodeSize(uri));
			}
			else if (cmd.equals("getchildnodenames") || cmd.equals("gc")) {
				String[] names = session.getChildNodeNames(uri);
                if(names != null)
                    for (int i = 0; i < names.length; i++)
                        ret = ret + names[i] + "/";
			}
			else if (cmd.equals("setnodetitle") || cmd.equals("st")) {
				session.setNodeTitle(uri, args[2]);
			}
			else if (cmd.equals("setnodevalue") || cmd.equals("sv")) {
				String typedata = args[2];
				DmtData value = dmtFromString(typedata);
				session.setNodeValue(uri, value);
			}
			else if (cmd.equals("setnodetype") || cmd.equals("sty")) {
				session.setNodeType(uri, args[2]);
			}
			else if (cmd.equals("deletenode") || cmd.equals("d")) {
				session.deleteNode(uri);
			}
			else if (cmd.equals("createinteriornode") || cmd.equals("ci")) {
				session.createInteriorNode(uri);
			}
			else if (cmd.equals("createleafnode") || cmd.equals("cl")) {
				String typedata = args[2];
				DmtData value = dmtFromString(typedata);
				session.createLeafNode(uri, value);
			}
			else if (cmd.equals("copy")) {
				session.copy(uri, args[2], true);
			}
			else if (cmd.equals("renamenode") || cmd.equals("re")) {
				session.renameNode(uri, args[2]);
			}
			else if (cmd.equals("execute") || cmd.equals("x")) {
				session.execute(uri, args[2]);
			}
			else if (cmd.equals("isleafnode") || cmd.equals("il")) {
				boolean b = session.isLeafNode(uri);
				ret = String.valueOf(b);
			}
			else if (cmd.equals("setnodeacl") || cmd.equals("sa")) {
				DmtAcl acl = new DmtAcl(args[2]);
				session.setNodeAcl(uri, acl);
			}
			else if (cmd.equals("getnodeacl") || cmd.equals("ga")) {
				DmtAcl acl = session.getNodeAcl(uri);
				ret = acl == null ? "<unset>" : acl.toString();
			}
			else if (cmd.equals("geteffectivenodeacl") || cmd.equals("gea")) {
				DmtAcl acl = session.getEffectiveNodeAcl(uri);
				ret = acl.toString();
			}
			else if (cmd.equals("getmetanode") || cmd.equals("gm")) {
				DmtMetaNode mn = session.getMetaNode(uri);
				ret = mn.toString(); // TODO does it have a good tostring?
			}
			else {
				ret = "Command unknown:" + cmd;
			}
		}
		catch (DmtException e) {
			StringWriter stringWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(stringWriter));
			return stringWriter.toString();
		}
		return (ret);
	}

	private DmtData dmtFromString(String typedata) {
		// expected format eg "int:23"
		int pos = typedata.indexOf(":");
		String type = typedata.substring(0, pos);
		String data = typedata.substring(pos + 1);
		DmtData value = null;
		if (type.equals("int")) {
			value = new DmtData(Integer.parseInt(data));
		}
		else if (type.equals("boolean")) {
			value = new DmtData((new Boolean(data)).booleanValue());
		}
		else if (type.equals("chr")) {
			value = new DmtData(data);
		}
        else if (type.equals("xml")) {
            value = new DmtData(data, true);
        }
        else if (type.equals("bin")) {
            value = new DmtData(data.getBytes());
        }
        else if (type.equals("null")) {
            value = DmtData.NULL_VALUE;
        }
		return value;
	}

	private String[] tokenize(String cmd) { //string.split is 1.4
		StringTokenizer st = new StringTokenizer(cmd);
		int n = st.countTokens();
		String[] ret = new String[n];
		for (int i = 0; i < n; i++) {
			ret[i] = st.nextToken();
		}
		return ret;
	}

	private String help() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		sb.append("Command          Short Params          Returns ");
		sb.append("\n");
		sb.append("===============================================");
		sb.append("\n");
		sb.append("open                    uri serverid           ");
		sb.append("\n");
		sb.append("close                                          ");
		sb.append("\n");
        sb.append("commit            c                            ");
        sb.append("\n");
        sb.append("rollback          r                            ");
        sb.append("\n");
		sb.append("isNodeUri               uri             true/false");
		sb.append("\n");
		sb.append("getNodeValue      gv    uri             value as string");
		sb.append("\n");
		sb.append("getNodeTitle      gt    uri             title  ");
		sb.append("\n");
		sb.append("getNodeVersion          uri             version");
		sb.append("\n");
		sb.append("getNodeTimestamp        uri             time   ");
		sb.append("\n");
		sb.append("getNodeSize             uri             size   ");
		sb.append("\n");
		sb.append("getChildNodeNames gc    uri             names/ ");
		sb.append("\n");
		sb.append("setNodeTitle      st    uri title              ");
		sb.append("\n");
		sb.append("setNodeValue      sv    uri type:data_as_string "
				+ "(type can be 'int', 'chr', 'boolean')");
		sb.append("\n");
		sb.append("setNodeType       sty   uri type               ");
		sb.append("\n");
		sb.append("deleteNode        d     uri                    ");
		sb.append("\n");
		sb.append("createInterior    ci    uri                    ");
		sb.append("\n");
		sb.append("createLeaf        cl    uri type:data (as above)");
		sb.append("\n");
		sb.append("copy                   uri new_uri            ");
		sb.append("\n");
		sb.append("renameNode        re    uri new_name           ");
		sb.append("\n");
		sb.append("execute           x     uri data               ");
		sb.append("\n");
		sb.append("isLeafNode        il    uri             true/false");
		sb.append("\n");
		sb.append("setNodeAcl        sa    uri acl_as_string      ");
		sb.append("\n");
		sb.append("getNodeAcl        ga    uri             acl    ");
		sb.append("\n");
		sb.append("getEffectiveNodeAcl gea uri             effective acl");
		sb.append("\n");
		sb.append("getMetaNode       gm    uri             metainfo");
		sb.append("\n");
		return new String(sb);
	}
	
}