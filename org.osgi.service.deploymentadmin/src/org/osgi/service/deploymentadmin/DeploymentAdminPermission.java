/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.deploymentadmin;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * DeploymentAdminPermission controls access to MEG management framework functions.
 * This permission controls only Deployment Admin-specific functions;
 * framework-specific access is controlled by usual OSGi permissions
 * (<code>AdminPermission</code>, etc.) <p>
 * In addition to <code>DeploymentAdminPermission</code>, the caller
 * of Deployment Admin must hold the appropriate <code>AdminPermission</code>s.<p>
 * For example, installing a deployment package requires <code>DeploymentAdminPermission</code>
 * to access the <code>installDeploymentPackage</code> method and <code>AdminPermission</code> to access
 * the framework's install/update/uninstall methods. <p>
 * The permission uses a &lt;filter&gt; string formatted similarly to the filter in RFC 73.
 * The <code>DeploymentAdminPermission</code> filter does not use the id and location filters.
 * The "signer" filter is matched against the signer chain of the deployment package, and
 * the "name" filter is matched against the DeploymentPackage-Name header.
 * <pre>
 * DeploymentAdminPermission( "&lt;filter&gt;", "list" )<p>
 * </pre>
 * A holder of this permission can access the inventory information of the deployment
 * packages selected by the &lt;filter&gt; string. The filter selects the deployment packages
 * on which the holder of the permission can acquire detailed inventory information.
 * See {@link DeploymentAdmin#getDeploymentPackage} and
 * {@link DeploymentAdmin#listDeploymentPackages}.
 * <pre>
 * DeploymentAdminPermission( "&lt;filter&gt;", "install" )
 * </pre>
 * A holder of this permission can install/upgrade deployment packages if the deployment
 * package satisfies the &lt;filter&gt; string. See {@link DeploymentAdmin#installDeploymentPackage}.
 * <pre>
 * DeploymentAdminPermission( "&lt;filter&gt;", "uninstall" )
 * </pre>
 * A holder of this permission can uninstall deployment packages if the deployment
 * package satisfies the &lt;filter&gt; string. See {@link DeploymentPackage#uninstall}.
 * <pre>
 * DeploymentAdminPermission( "&lt;filter&gt;", "uninstallForceful" )
 * </pre>
 * A holder of this permission can forcefully uninstall deployment packages if the deployment
 * package satisfies the  string. See {@link DeploymentPackage#uninstallForceful}.
 * <pre>
 * DeploymentAdminPermission( "&lt;filter&gt;", "cancel" )
 * </pre>
 * A holder of this permission can cancel an active deployment action. This action being
 * cancelled could correspond to the install, update or uninstall of a deployment package
 * that satisfies the  string. See {@link DeploymentAdmin#cancel}<p>
 * Wildcards can be used both in the name and the signer (see RFC-95) filters.<p>
 */
public class DeploymentAdminPermission extends Permission {
    
    /**
     * Constant String to the "install" action.
     */
    public static final String ACTION_INSTALL            = "install";

    /**
     * Constant String to the "list" action.
     */
    public static final String ACTION_LIST               = "list";
    
    /**
     * Constant String to the "uninstall" action.
     */
    public static final String ACTION_UNINSTALL          = "uninstall";

    /**
     * Constant String to the "uninstallForceful" action.
     */
    public static final String ACTION_UNINSTALL_FORCEFUL = "uninstallForceful";
    
    /**
     * Constant String to the "cancel" action.
     */
    public static final String ACTION_CANCEL             = "cancel";  
    
    private static final Vector ACTIONS = new Vector();
    static {
        ACTIONS.add(ACTION_INSTALL.toLowerCase());
        ACTIONS.add(ACTION_LIST.toLowerCase());
        ACTIONS.add(ACTION_UNINSTALL.toLowerCase());
        ACTIONS.add(ACTION_UNINSTALL_FORCEFUL.toLowerCase());
        ACTIONS.add(ACTION_CANCEL.toLowerCase());
    }
    
    // used system properties
    private static final String KEYSTORE_PATH = "org.osgi.service.deploymentadmin.keystore.path";
    private static final String KEYSTORE_PWD  = "org.osgi.service.deploymentadmin.keystore.pwd";
    
    private           String actions;
    private transient Vector actionsVector;
    private transient Representation rep;
    
    /**
     * Creates a new <code>DeploymentAdminPermission</code> object for the given name (containing the name
     * of the target deployment package) and action.<p>
     * The <code>name</code> parameter identifies the target depolyment package the permission 
     * relates to. The <code>actions</code> parameter contains the comma separated list of allowed actions. 
     * @param name Target string, must not be null.
     * @param action Action string, must not be null.
     * @throws IllegalArgumentException if the filter is invalid or the list of actions 
     *         contains unknown operations
     */
    public DeploymentAdminPermission(String name, String actions) {
        // TODO canonicalize "name"
        super(name);
        this.actions = actions;
        rep = new Representation(getName());
        check();
    }

    /**
     * Checks two DeploymentAdminPermission objects for equality. 
     * Two permission objects are equal if: <p>
     * - their target filters 
     * are equal and<p>
     * - their actions are the same. 
     * @param obj The reference object with which to compare.
     * @return true if the two objects are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof DeploymentAdminPermission))
            return false;
        DeploymentAdminPermission other = (DeploymentAdminPermission) obj;
        
        Vector avThis = getActionVector();
        Vector avOther = other.getActionVector();
        boolean eqActions = (avThis.containsAll(avOther) &&
                avOther.containsAll(avThis));
        return getFilter().equals(other.getFilter()) && eqActions;
    }

    /**
     * Returns hash code for this permission object.
     * @return Hash code for this permission object.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        // TODO
        return getActionVector().hashCode();
    }

    /**
     * Returns the String representation of the action list.
     * @return Action list of this permission instance. This is a comma-separated 
     * list that reflects the action parameter of the constructor.
     * @see java.security.Permission#getActions()
     */
    public String getActions() {
        return actions;
    }

    /**
     * Checks if this DeploymentAdminPermission would imply the parameter permission.
     * @param permission Permission to check.
     * @return true if this DeploymentAdminPermission object implies the 
     * specified permission.
     * @see java.security.Permission#implies(java.security.Permission)
     */
    public boolean implies(Permission permission) {
        if (null == permission)
            return false;
        if (!(permission instanceof DeploymentAdminPermission))
            return false;
        DeploymentAdminPermission other = (DeploymentAdminPermission) permission;
        return getFilter().match(other);
    }

    /**
     * Returns a new PermissionCollection object for storing DeploymentAdminPermission 
     * objects. 
     * @return The new PermissionCollection.
     * @see java.security.Permission#newPermissionCollection()
     */
    public PermissionCollection newPermissionCollection() {
        return null;
    }
    
    /**
     * Returns a string representation of the object. Returns a string describing 
     * this Permission. The convention is to specify the class name, the permission 
     * name, and the actions in the following format:<p>
     * '("ClassName" "name" "actions")'.
     * @return string representation of the permission
     */
    public String toString() {
        return "(" + getClass().getName() + " " + getName() + " " + actions + ")"; 
    }

    private Vector getActionVector() {
        if (null != actionsVector)
            return actionsVector;
        
        actionsVector = new Vector();
        StringTokenizer t = new StringTokenizer(actions.toUpperCase(), ",");
        while (t.hasMoreTokens()) {
            String action = t.nextToken().trim();
            actionsVector.add(action.toLowerCase());
        }
        return actionsVector;
    }

    private Representation getFilter() {
        return rep; 
    }

    private void check() {
        if (!ACTIONS.containsAll(getActionVector()))
            throw new IllegalArgumentException("Illegal action");
    }

    static class Representation {
        
        static class Node {
            // TODO eliminate ROOT level (not nice)
            private static final int ROOT  = 0;
            private static final int LEAF  = 1; 
            private static final int AND   = 2;
            private static final int OR    = 3;
            private static final int NOT   = 4;
            
            // only in interior nodes
            private int                op;
            private Vector             args = new Vector();
            
            // only in leaf nodes
            private SignerChainPattern chainPattern;
            private NamePattern        namePattern;
            private String[]           leafValue;
            
            void setLeaf (String attr, String value) {
                leafValue = new String[] {attr, value};
            }
            
            void setOp(int op) {
                this.op = op;    
            }
            
            void setSignerChainPattern(String str) {
                chainPattern = new SignerChainPattern(str);    
            }
            
            void setNamePattern(String str) {
                namePattern = new NamePattern(str);    
            }
            
            void add(Node child) {
                args.add(child);
            }
            
            boolean match(Hashtable ht) {
                switch (op) {
	                case ROOT: {
	                    return ((Node) args.get(0)).match(ht);
	                }
	                case LEAF: {
	                    String attr = leafValue[0];
	                    String value = leafValue[1];
	                    if ("name".equals(attr)) {
	                        boolean b = namePattern.match((String) ht.get(attr));
if (!b) System.out.println(">>>***>>> namePattern 1:" + namePattern.pattern + "< 2:" + (String) ht.get(attr) + "<"); 
	                        return b;
	                        //return namePattern.match((String) ht.get(attr));
	                        }
	                    else if ("signer".equals(attr)) { 
	                        boolean b = chainPattern.match((String) ht.get(attr));
if (!b) System.out.println(">>>***>>> chainPattern");	                        
	                        return b;
	                    	}
	                    else 
	                        return false;
	                }
	                case AND: {
	                    boolean ret = true;
	                    for (Iterator iter = args.iterator(); iter.hasNext();) {
	                        Node node = (Node) iter.next();
	                        ret = ret && node.match(ht);
	                    }
	                    return ret;
	                }
	                case OR: {
	                    boolean ret = false;
	                    for (Iterator iter = args.iterator(); iter.hasNext();) {
	                        Node node = (Node) iter.next();
	                        ret = ret || node.match(ht);
	                    }
	                    return ret;
	                }
	                case NOT: {
	                    return !((Node) args.get(0)).match(ht);
	                }
	                default:
	                    return false;
                }
            }
        }

        private Node      rootNode;
        private Hashtable attrTable = new Hashtable();

        private char[]    filter;
        private int       index;
        
        public boolean equals(Object o) {
            if (null == o)
                return false;
            if (!(o instanceof Representation))
                return false;
            Representation other = (Representation) o;
            
            // TODO
            return new String(filter).equals(new String(other.filter));
        }
        
        public int hashCode() {
            // TODO
            return new String(filter).hashCode();
        }
        
        Representation(String filter) {
            this.filter = filter.toCharArray();
            rootNode = new Node();
            rootNode.setOp(Node.ROOT);
            parse_filter(rootNode);
        }
        
        boolean match(DeploymentAdminPermission perm) {
            Representation f = new Representation(perm.getName());
            return rootNode.match(f.attrTable);
        }
        
        private void parse_filter(Node node) {
            accept('(');
            Node newNode = new Node();    
            parse_filter_comp(newNode);
            accept(')');
            node.add(newNode);
        }

        private void parse_filter_comp(Node node) {
            switch (filter[index]) {
                case '&' :
                    accept('&');
                    node.setOp(Node.AND);
                    parse_filter_list(node);
                    break;
                case '|' :
                    // TODO not allowed in case of concrete permissions
                    accept('|');
                    node.setOp(Node.OR);
                    parse_filter_list(node);
                    break;
                case '!' :
                    // TODO not allowed in case of concrete permissions
                    accept('!');
                    node.setOp(Node.NOT);
                    parse_filter(node);
                    break;
                default :
                    node.setOp(Node.LEAF);
                    parse_operation(node);
                    break;
            }
        }

        private void parse_operation(Node node) {
            String f = new String(filter);
            int indEq = f.indexOf("=", index);
            if (-1 == indEq)
                throwException();
            String attr = f.substring(index, indEq);
            int indPar = f.indexOf(")", indEq);
            if (-1 == indEq)
                throwException();
            String value = f.substring(indEq + 1, indPar);
            if ("name".equals(attr))
                node.setNamePattern(value);
            else if ("signer".equals(attr))
                node.setSignerChainPattern(value);
            else
                throwException();
            node.setLeaf(attr, value);
            
            // it is used in case of concrete permissions 
            // and not in case of wildcarded ones
            attrTable.put(attr, value);
            
            index = indPar;
        }

        private boolean isSimple() {
            int i = index;
            while (i < filter.length) {
                if ('=' == filter[i]) {
                    if (i < filter.length - 1) {
                        return '*' != filter[i + 1];
                    } else 
                        return true;
                }
                ++i;
            }
            return false;
        }

        private void parse_filter_list(Node node) {
            parse_filter(node);
            if ('(' == filter[index])
                parse_filter_list(node);
        }

        private boolean end() {
            return index >= filter.length;
        }
        
        private void accept(char ch) {
            if (ch != filter[index])
                throw new IllegalArgumentException();
            ++index;
        }
        
        private void throwException() {
            throw new IllegalArgumentException("Error at position " + index + " in " +
            		"\"" + new String(filter) + "\"");
        }
    }

    static class SignerChainPattern {
        private SignerPattern[] patterns;
            
        SignerChainPattern(String str) {
            String[] sa = Splitter.split(str, ';', 0);
            patterns = new SignerPattern[sa.length];
            for (int i = 0; i < sa.length; i++) {
                if (i > 0 && i < sa.length - 1 && "-".equals(sa[i].trim()))
                    throw new IllegalArgumentException("'-' is not allowed in the middle of the " +
                    		"chain");
                patterns[i] = new SignerPattern(sa[i]);
            }
        }

       public boolean match(String signerChain) {
           String[] chain;
           if (null == signerChain)
               chain = new String[] {};
           else
               chain = Splitter.split(signerChain, ';', 0);
           int i = patterns.length - 1;
           int j = chain.length - 1;
           boolean ok = false;
           for (; i >= 0 && j >= 0;) {
               if (patterns[i].isMinus())
                   return true;
               if (!patterns[i].match(chain[j]))
                   return false;
               --i; --j;
           }
           return true;
       }
        
        static class SignerPattern {
            private Hashtable table      = new Hashtable();
            private boolean   exactMatch = true;
            private boolean   trusted;
            private boolean   minus;
            
            SignerPattern(String pattern) {
                String p = pattern;
                if ("-".equals(p.trim())) {
                    minus = true;
                    return;
                }
                if (pattern.startsWith("<") && pattern.endsWith(">")) {
                    p = p.substring(1, p.length() - 1);
                    trusted = true;
                }
                table = createTable(p);
            }
            
            boolean isTrusted() {
                return trusted;
            }
            
            boolean isMinus() {
                return minus;
            }
            
            private Hashtable createTable(String str) {
                Hashtable ht = new Hashtable();
                String[] pairs = Splitter.split(str, ',', 0);
                for (int i = 0; i < pairs.length; i++) {
                    pairs[i] = pairs[i].trim();
                    if ("*".equals(pairs[i])) {
                        exactMatch = false;
                    } else {
                        int ioe = pairs[i].indexOf("=");
                        String key = pairs[i].substring(0, ioe);
                        String value = pairs[i].substring(ioe + 1);
                        ht.put(key.toUpperCase(), value);
                    }
                }
                return ht;
            }
            
            public boolean match(String signer) {
                Hashtable ht = createTable(signer);
                for (Iterator iter = table.keySet().iterator(); iter.hasNext();) {
                    String key = (String) iter.next();
                    String v1 = (String) table.get(key);
                    String v2 = (String) ht.get(key);
                    if (null == v2)
                        return false;
                    ht.remove(key);
                    if ("*".equals(v1))
                        continue;
                    if (!v1.equals(v2)) 
                        return false;
                }
                if (exactMatch && !ht.keySet().isEmpty())
                    return false;
                if (trusted) 
                    ; // TODO check: signer in the keystore
                return true;
            }
        }

    }

    static class Splitter {
        public static String[] split(String input, char sep, int limit) {
            Vector v = new Vector();
            boolean limited = (limit > 0);
            int applied = 0;
            int index = 0;
            StringBuffer part = new StringBuffer();
            while (index < input.length()) {
                char ch = input.charAt(index);
                if (ch != sep)
                    part.append(ch);
                else {
                    ++applied;
                    v.add(part.toString());
                    part = new StringBuffer();
                }
                ++index;
                if (limited && applied == limit - 1)
                    break;
            }
            while (index < input.length()) {
                char ch = input.charAt(index);
                part.append(ch);
                ++index;
            }
            v.add(part.toString());
            int last = v.size();
            if (0 == limit) {
                for (int j = v.size() - 1; j >= 0; --j) {
                    String s = (String) v.elementAt(j);
                    if ("".equals(s))
                        --last;
                    else
                        break;
                }
            }
            String[] ret = new String[last];
            for (int i = 0; i < last; ++i)
                ret[i] = (String) v.elementAt(i);
            return ret;
        }
    }

    static class NamePattern {
        
        private String pattern;
        
        NamePattern(String str) {
            pattern = str;
        }
        
        public boolean match(String str) {
    		StringBuffer p = new StringBuffer(pattern);
    		StringBuffer s = new StringBuffer(str);
    		
    		while (p.length() > 0) {
    			char pch = p.charAt(0);
    			
    			if (pch == '*') {
    				closeupAsterixs(p);
    				String aa = afterAsterix(p); 
    				int pos = find(aa, s);
    				if (pos < 0)
    					return false;
    				// delete '*'
    				p.deleteCharAt(0);
    				// and replace it with the begining of s
    				if (!"".equals(aa))
    					p.insert(0, s.substring(0, pos));
    				else
    					p.insert(0, s);
    				continue;
    			}
    			char sch = s.charAt(0);
    			if (pch == sch || pch == '?') {
    				p.deleteCharAt(0);
    				s.deleteCharAt(0);
    				continue;	
    			}
    			
    			return false;
    		}
    		
    		return s.length() <= 0;
    	}

        private static void closeupAsterixs(StringBuffer p) {
            while (p.length() > 1 && '*' == p.charAt(1))
                p.deleteCharAt(1);
        }

        private static String afterAsterix(StringBuffer ap) {
            StringBuffer p = new StringBuffer(ap.toString());
            p.deleteCharAt(0);
            StringBuffer ret = new StringBuffer();
            while (p.length() > 0 && p.charAt(0) != '*') {
                ret.append(p.charAt(0));
                p.deleteCharAt(0);
            }   
            return ret.toString();
        }

        private static int find(String afterAsterix, StringBuffer s) {
            for (int i = 0; i < s.length() - afterAsterix.length() + 1; ++i) {
                boolean error = false;
                for (int j = 0; j < afterAsterix.length(); ++j) {
                    if (s.charAt(i + j) != afterAsterix.charAt(j)) {
                        if (afterAsterix.charAt(j) != '?') {
                            error = true;
                            break;
                        }
                    }
                }
                if (!error) 
                    return i;
            }
            
            return -1;
        }
    }
    
}
