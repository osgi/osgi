/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.monitor;

import java.security.Permission;
import java.util.StringTokenizer;

/**
 * Indicates the callers authority to publish, discover, read or reset
 * <code>StatusVariable</code>s, to switch event sending on or off or to
 * start monitoring jobs. The target of the permission is the identifier of the
 * <code>StatusVariable</code>, the action can be <code>read</code>,
 * <code>publish</code>, <code>reset</code>, <code>startjob</code>,
 * <code>switchevents</code>, <code>discover</code>, or the combination of
 * these separated by commas.
 */
public class MonitorPermission extends Permission {
    // TODO add static final serialVersionUID

    /**
     * Holders of <code>MonitorPermission</code> with the <code>read</code>
     * action present are allowed to read the value of the
     * <code>StatusVariable</code>s specified in the permission's target field.
     */
    public static final String READ = "read";

    /**
     * Holders of <code>MonitorPermission</code> with the <code>reset</code>
     * action present are allowed to reset the value of the
     * <code>StatusVariable</code>s specified in the permission's target field.
     */
    public static final String RESET = "reset";

    /**
     * Holders of <code>MonitorPermission</code> with the <code>publish</code>
     * action present are <code>Monitorable</code> services that are allowed
     * to publish the <code>StatusVariable</code>s specified in the
     * permission's target field.  Note, that this permission cannot be enforced 
     * when a <code>Monitorable</code> registers to the framework, because the
     * Service Registry does not know about this permission.  Instead, any
     * <code>StatusVariable</code>s published by a <code>Monitorable</code>
     * without the corresponding <code>publish</code> permission are silently
     * ignored by <code>MonitorAdmin</code>, and are therefore invisible to the
     * users of the monitoring service.   
     */
    public static final String PUBLISH = "publish";

    /**
     * Holders of <code>MonitorPermission</code> with the <code>startjob</code>
     * action present are allowed to initiate monitoring jobs involving the 
     * <code>StatusVariable</code>s specified in the permission's target field.
     * <p>
     * A minimal sampling interval can be optionally defined in the following
     * form: <code>startjob:n</code>.  This allows the holder of the permission
     * to initiate time based jobs with a measurement interval of at least
     * <code>n</code> seconds. If <code>n</code> is not specified or 0 then the 
     * holder of this permission is allowed to start monitoring jobs specifying 
     * any frequency.
     */
    public static final String STARTJOB = "startjob";

    /**
     * Holders of <code>MonitorPermission</code> with the
     * <code>discover</code> action present are allowed to list the currently
     * registered <code>Monitorable</code> services, and the
     * <code>StatusVariable</code>s a <code>Monitorable</code> publishes.
     * Discover rights are implied by read rights, if the target field is also 
     * valid for the <code>discover</code> action.
     * <p>
     * To list all registered <code>Monitorable</code>s and all their
     * <code>StatusVariable</code>s the permission's target field must be
     * <code>&#42;/*</code>. To allow listing the <code>StatusVariable</code>s
     * of only a specific <code>Monitorable</code>, the target field must be of
     * the form <code>[Monitorable_ID]/*</code>. In the latter case 
     * <code>[Monitorable_ID]</code> may end with <code>*</code> to allow
     * listing the <code>StatusVariable</code>s of all matching 
     * <code>Monitorable</code> services.
     */
    public static final String DISCOVER = "discover";

    /**
     * Holders of <code>MonitorPermission</code> with the
     * <code>switchevents</code> action present are allowed to switch event
     * sending on or off for the value of the <code>StatusVariable</code>s
     * specified in the permission's target field.
     */
    public static final String SWITCHEVENTS = "switchevents";

    private static final int READ_FLAG         = 0x1;
    private static final int RESET_FLAG        = 0x2;
    private static final int PUBLISH_FLAG      = 0x4;
    private static final int STARTJOB_FLAG     = 0x8;
    private static final int DISCOVER_FLAG     = 0x10;
    private static final int SWITCHEVENTS_FLAG = 0x20;

    private String monId;
    private String varId;
    private boolean prefixMonId;
    private boolean prefixVarId;
    private int mask;
    private int minJobInterval;

    /**
     * Create a <code>MonitorPermission</code> object, specifying the target
     * and actions.
     * <p>
     * The meaning of the <code>statusVariable</code> parameter is slightly
     * different depending on the <code>action</code> field, see the 
     * descriptions of the individual actions.  In general, the wildcard 
     * <code>*</code> is allowed in both fragments of the target string, but 
     * only at the end of the fragments. It is not allowed to specify a Status
     * Variable name (or prefix) if the Monitorable ID ended with a wildcard.
     * <p>
     * The following targets are valid:
     * <code>com.mycomp.myapp/queue_length</code>,
     * <code>com.mycomp.myapp/*</code>, <code>com.mycomp.&#42;/*</code>,
     * <code>&#42;/*</code>.
     * <p>
     * The following targets are invalid:
     * <code>*.myapp/queue_length</code>, <code>com.*.myapp/*</code>,
     * <code>*</code>, <code>&#42;/queue_length</code>, 
     * <code>&#42;/queue*</code>.
     * <p>
     * The <code>actions</code> parameter specifies the allowed action(s): 
     * <code>read</code>, <code>publish</code>, <code>startjob</code>,
     * <code>reset</code>, <code>switchevents</code>, <code>discover</code>, or 
     * the combination of these separated by commas. 
     * 
     * @param statusVariable the identifier of the <code>StatusVariable</code>
     *        in [Monitorable_id]/[StatusVariable_id] format 
     * @param actions the list of allowed actions separated by commas
     * @throws java.lang.IllegalArgumentException if either parameter is 
     *         <code>null</code>, or invalid with regard to the constraints
     *         defined above and in the documentation of the used actions 
     */
    public MonitorPermission(String statusVariable, String actions) 
            throws IllegalArgumentException {
        super(statusVariable);

        if(statusVariable == null)
            throw new IllegalArgumentException(
                    "Invalid StatusVariable path 'null'.");
        
        int sep = statusVariable.indexOf('/');
        int len = statusVariable.length();

        if (sep == -1)
            throw new IllegalArgumentException(
                    "Invalid StatusVariable path: should contain '/' separator.");
        if (sep == 0 || sep == statusVariable.length() - 1)
            throw new IllegalArgumentException(
                    "Invalid StatusVariable path: empty monitorable ID or StatusVariable name.");

        // TODO: full ID check? (e.g. no / in statusVariable, no invalid chars, no * within IDs)

        prefixMonId = statusVariable.charAt(sep - 1) == '*';
        prefixVarId = statusVariable.charAt(len - 1) == '*';
        
        monId = statusVariable.substring(0, prefixMonId ? sep - 1 : sep);
        varId = statusVariable.substring(sep + 1, prefixVarId ? len - 1 : len);

        if(prefixMonId && !varId.equals(""))
            throw new IllegalArgumentException(
                    "Invalid StatusVariable path: wildcard in monitorable ID " +
                    "must be followed by '*' as StatusVariable name.");
        
        mask = 0;
        minJobInterval = 0;

        StringTokenizer st = new StringTokenizer(actions, ",");
        while (st.hasMoreTokens()) {
            String action = st.nextToken();
            if (action.equalsIgnoreCase(READ)) {
                addToMask(READ_FLAG, READ);
            } else if (action.equalsIgnoreCase(RESET)) {
                addToMask(RESET_FLAG, RESET);
            } else if (action.equalsIgnoreCase(PUBLISH)) {
                addToMask(PUBLISH_FLAG, PUBLISH);
            } else if (action.equalsIgnoreCase(DISCOVER)) {
                if(!varId.equals("")) // implies prefixVarId
                    throw new IllegalArgumentException(
                            "Invalid target for 'discover' action, " + 
                            "StatusVariable name in target must be '*'.");
                addToMask(DISCOVER_FLAG, DISCOVER);
            } else if (action.equalsIgnoreCase(SWITCHEVENTS)) {
                addToMask(SWITCHEVENTS_FLAG, SWITCHEVENTS);
            } else if (action.toLowerCase().startsWith(STARTJOB)) {
                minJobInterval = 0;

                int slen = STARTJOB.length();
                if (action.length() != slen) {
                    if (action.charAt(slen) != ':')
                        throw new IllegalArgumentException("Invalid action '"
                                + action + "'.");

                    try {
                        minJobInterval = Integer.parseInt(action
                                .substring(slen + 1));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "Invalid parameter in startjob action '"
                                        + action + "'.");
                    }
                }
                addToMask(STARTJOB_FLAG, STARTJOB);
            } else
                throw new IllegalArgumentException("Invalid action '" + action
                        + "'");
        }
        
        // "read" implies "discover" for targets */*, mon*/* and monId/*
        // the varId == "" check identifies exactly these targets
        if((mask & READ_FLAG) != 0 && varId.equals(""))
            mask |= DISCOVER_FLAG;
    }
    
    private void addToMask(int action, String actionString) {
        if((mask & action) != 0)
            throw new IllegalArgumentException("Invalid action string: " + 
                    actionString + " appears multiple times.");
        
        mask |= action;
    }

    /**
     * Create an integer hash of the object. The hash codes of
     * <code>MonitorPermission</code>s <code>p1</code> and <code>p2</code> are 
     * the same if <code>p1.equals(p2)</code>.
     * 
     * @return the hash of the object
     */
    public int hashCode() {
        return new Integer(mask).hashCode()
                ^ new Integer(minJobInterval).hashCode() ^ monId.hashCode()
                ^ new Boolean(prefixMonId).hashCode()
                ^ varId.hashCode()
                ^ new Boolean(prefixVarId).hashCode();
    }

    /**
     * Determines the equality of two <code>MonitorPermission</code> objects.
     * Two <code>MonitorPermission</code> objects are equal if their target
     * strings are equal and the same set of actions are listed in their action
     * strings.
     * 
     * @param o the object being compared for equality with this object
     * @return <code>true</code> if the two permissions are equal
     */
    public boolean equals(Object o) {
        if (!(o instanceof MonitorPermission))
            return false;

        MonitorPermission other = (MonitorPermission) o;

        return mask == other.mask && minJobInterval == other.minJobInterval
                && monId.equals(other.monId)
                && prefixMonId == other.prefixMonId
                && varId.equals(other.varId)
                && prefixVarId == other.prefixVarId;
    }

    /**
     * Get the action string associated with this permission.
     * 
     * @return the allowed actions separated by commas, cannot be
     *         <code>null</code>
     */
    public String getActions() {
        StringBuffer sb = new StringBuffer();

        appendAction(sb, READ_FLAG,         READ);
        appendAction(sb, RESET_FLAG,        RESET);
        appendAction(sb, PUBLISH_FLAG,      PUBLISH);
        appendAction(sb, STARTJOB_FLAG,     STARTJOB);
        appendAction(sb, DISCOVER_FLAG,     DISCOVER);
        appendAction(sb, SWITCHEVENTS_FLAG, SWITCHEVENTS);

        return sb.toString();
    }

    private void appendAction(StringBuffer sb, int flag, String actionName) {
        if ((mask & flag) != 0) {
            if(sb.length() != 0)
                sb.append(',');
            sb.append(actionName);
        }
    }

    /**
     * Determines if the specified permission is implied by this permission.
     * <p>
     * This method returns <code>false</code> if and only if at least one of the
     * following conditions are fulfilled for the specified permission:
     * <li>it is not a <code>MonitorPermission</code>
     * <li>it has a broader set of actions allowed than this one
     * <li>it allows initiating time based monitoring jobs with a lower minimal
     * sampling interval
     * <li>the target set of <code>Monitorable</code>s is not the same nor a
     * subset of the target set of <code>Monitorable</code>s of this permission
     * <li>the target set of <code>StatusVariable</code>s is not the same
     * nor a subset of the target set of <code>StatusVariable</code>s of this
     * permission
     * <p>
     * When comparing the permitted action sets, the presence of the
     * <code>read</code> action implies the <code>discover</code> action, if
     * the target string is valid for the <code>discover</code> action as
     * well.
     * 
     * @param p the permission to be checked
     * @return <code>true</code> if the given permission is implied by this
     *         permission
     */
    public boolean implies(Permission p) {
        if (!(p instanceof MonitorPermission))
            return false;

        MonitorPermission other = (MonitorPermission) p;

        if ((mask & other.mask) != other.mask)
            return false;

        if ((other.mask & STARTJOB_FLAG) != 0
                && minJobInterval > other.minJobInterval)
            return false;

        return implies(monId, prefixMonId, other.monId, other.prefixMonId)
                && implies(varId, prefixVarId, other.varId, other.prefixVarId);
    }

    private boolean implies(String id, boolean prefix, String oid,
            boolean oprefix) {

        return prefix ? oid.startsWith(id) : !oprefix && id.equals(oid);
    }
}
