/*
 * Copyright (c) OSGi Alliance (2005, 2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.monitor;

import java.io.UnsupportedEncodingException;
import java.security.Permission;
import java.util.StringTokenizer;

/**
 * Indicates the callers authority to publish, read or reset
 * {@code StatusVariable}s, to switch event sending on or off or to start
 * monitoring jobs. The target of the permission is the identifier of the
 * {@code StatusVariable}, the action can be {@code read},
 * {@code publish}, {@code reset}, {@code startjob},
 * {@code switchevents}, or the combination of these separated by commas.
 * Action names are interpreted case-insensitively, but the canonical action
 * string returned by {@link #getActions} uses the forms defined by the action
 * constants.
 * <p>
 * If the wildcard {@code *} appears in the actions field, all legal
 * monitoring commands are allowed on the designated target(s) by the owner of
 * the permission.
 * 
 * @version $Id$
 */
public class MonitorPermission extends Permission {

    /**
	 * 
	 */
	private static final long	serialVersionUID	= -9084425194463274314L;

	/**
     * Holders of {@code MonitorPermission} with the {@code read}
     * action present are allowed to read the value of the
     * {@code StatusVariable}s specified in the permission's target field.
     */
    public static final String READ = "read";

    /**
     * Holders of {@code MonitorPermission} with the {@code reset}
     * action present are allowed to reset the value of the
     * {@code StatusVariable}s specified in the permission's target field.
     */
    public static final String RESET = "reset";

    /**
     * Holders of {@code MonitorPermission} with the {@code publish}
     * action present are {@code Monitorable} services that are allowed
     * to publish the {@code StatusVariable}s specified in the
     * permission's target field.  Note, that this permission cannot be enforced 
     * when a {@code Monitorable} registers to the framework, because the
     * Service Registry does not know about this permission.  Instead, any
     * {@code StatusVariable}s published by a {@code Monitorable}
     * without the corresponding {@code publish} permission are silently
     * ignored by {@code MonitorAdmin}, and are therefore invisible to the
     * users of the monitoring service.   
     */
    public static final String PUBLISH = "publish";

    /**
     * Holders of {@code MonitorPermission} with the {@code startjob}
     * action present are allowed to initiate monitoring jobs involving the 
     * {@code StatusVariable}s specified in the permission's target field.
     * <p>
     * A minimal sampling interval can be optionally defined in the following
     * form: {@code startjob:n}.  This allows the holder of the permission
     * to initiate time based jobs with a measurement interval of at least
     * {@code n} seconds. If {@code n} is not specified or 0 then the 
     * holder of this permission is allowed to start monitoring jobs specifying 
     * any frequency.
     */
    public static final String STARTJOB = "startjob";

    /**
     * Holders of {@code MonitorPermission} with the
     * {@code switchevents} action present are allowed to switch event
     * sending on or off for the value of the {@code StatusVariable}s
     * specified in the permission's target field.
     */
    public static final String SWITCHEVENTS = "switchevents";

    private static final int READ_FLAG         = 0x1;
    private static final int RESET_FLAG        = 0x2;
    private static final int PUBLISH_FLAG      = 0x4;
    private static final int STARTJOB_FLAG     = 0x8;
    private static final int SWITCHEVENTS_FLAG = 0x10;
    
    private static final int ALL_FLAGS = READ_FLAG | RESET_FLAG | 
        PUBLISH_FLAG | STARTJOB_FLAG | SWITCHEVENTS_FLAG;

    private String monId;
    private String varId;
    private boolean prefixMonId;
    private boolean prefixVarId;
    private int mask;
    private int minJobInterval;

    /**
     * Create a {@code MonitorPermission} object, specifying the target
     * and actions.
     * <p>
     * The {@code statusVariable} parameter is the target of the 
     * permission, defining one or more status variable names to which the
     * specified actions apply. Multiple status variable names can be selected
     * by using the wildcard {@code *} in the target string.  The wildcard
     * is allowed in both fragments, but only at the end of the fragments.
     * <p>
     * For example, the following targets are valid:
     * {@code com.mycomp.myapp/queue_length},
     * {@code com.mycomp.myapp/*}, {@code com.mycomp.&#42;/*},
     * {@code &#42;/*}, {@code &#42;/queue_length}, 
     * {@code &#42;/queue*}.
     * <p>
     * The following targets are invalid:
     * {@code *.myapp/queue_length}, {@code com.*.myapp/*},
     * {@code *}.
     * <p>
     * The {@code actions} parameter specifies the allowed action(s): 
     * {@code read}, {@code publish}, {@code startjob},
     * {@code reset}, {@code switchevents}, or the combination of 
     * these separated by commas. String constants are defined in this class for
     * each valid action.  Passing {@code &quot;*&quot;} as the action 
     * string is equivalent to listing all actions. 
     * 
     * @param statusVariable the identifier of the {@code StatusVariable}
     *        in [Monitorable_id]/[StatusVariable_id] format 
     * @param actions the list of allowed actions separated by commas, or
     *        {@code *} for all actions
     * @throws java.lang.IllegalArgumentException if either parameter is 
     *         {@code null}, or invalid with regard to the constraints
     *         defined above and in the documentation of the used actions 
     */
    public MonitorPermission(String statusVariable, String actions) 
            throws IllegalArgumentException {
        super(statusVariable);

        if(statusVariable == null)
            throw new IllegalArgumentException(
                    "Invalid StatusVariable path 'null'.");
        
        if(actions == null)
            throw new IllegalArgumentException(
                    "Invalid actions string 'null'.");
        
        int sep = statusVariable.indexOf('/');
        int len = statusVariable.length();

        if (sep == -1)
            throw new IllegalArgumentException(
                    "Invalid StatusVariable path: should contain '/' separator.");
        if (sep == 0 || sep == statusVariable.length() - 1)
            throw new IllegalArgumentException(
                    "Invalid StatusVariable path: empty monitorable ID or StatusVariable name.");

        prefixMonId = statusVariable.charAt(sep - 1) == '*';
        prefixVarId = statusVariable.charAt(len - 1) == '*';
        
        monId = statusVariable.substring(0, prefixMonId ? sep - 1 : sep);
        varId = statusVariable.substring(sep + 1, prefixVarId ? len - 1 : len);

        checkId(monId, "Monitorable ID part of the target");
        checkId(varId, "Status Variable ID part of the target");

        minJobInterval = 0;

        if(actions.equals("*"))
            mask = ALL_FLAGS;
        else {
            mask = 0;
            StringTokenizer st = new StringTokenizer(actions, ",");
            while (st.hasMoreTokens()) {
                String action = st.nextToken().trim();
                if (action.equalsIgnoreCase(READ)) {
                    addToMask(READ_FLAG, READ);
                } else if (action.equalsIgnoreCase(RESET)) {
                    addToMask(RESET_FLAG, RESET);
                } else if (action.equalsIgnoreCase(PUBLISH)) {
                    addToMask(PUBLISH_FLAG, PUBLISH);
                } else if (action.equalsIgnoreCase(SWITCHEVENTS)) {
                    addToMask(SWITCHEVENTS_FLAG, SWITCHEVENTS);
                } else if (action.toLowerCase().startsWith(STARTJOB)) {
                    minJobInterval = 0;
    
                    int slen = STARTJOB.length();
                    if (action.length() != slen) {
                        if (action.charAt(slen) != ':')
                            throw new IllegalArgumentException(
                                    "Invalid action '" + action + "'.");
    
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
                    throw new IllegalArgumentException("Invalid action '" + 
                            action + "'");
            }
        }
    }
    
    private void addToMask(int action, String actionString) {
        if((mask & action) != 0)
            throw new IllegalArgumentException("Invalid action string: " + 
                    actionString + " appears multiple times.");
        
        mask |= action;
    }

    private void checkId(String id, String idName)
            throws IllegalArgumentException {
        
        byte[] nameBytes;
        try {
            nameBytes = id.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // never happens, "UTF-8" must always be supported
            throw new IllegalStateException(e.getMessage());
        }
        if(nameBytes.length > StatusVariable.MAX_ID_LENGTH)
            throw new IllegalArgumentException(idName + " is too long (over " +
                    StatusVariable.MAX_ID_LENGTH + " bytes in UTF-8 encoding).");

        
        if (id.equals(".") || id.equals(".."))
            throw new IllegalArgumentException(idName + " is invalid.");
        
        char[] chars = id.toCharArray();
        for (int i = 0; i < chars.length; i++)
            if (StatusVariable.SYMBOLIC_NAME_CHARACTERS.indexOf(chars[i]) == -1)
                throw new IllegalArgumentException(idName +
                        " contains invalid characters.");
    }
    
    /**
     * Create an integer hash of the object. The hash codes of
     * {@code MonitorPermission}s {@code p1} and {@code p2} are 
     * the same if {@code p1.equals(p2)}.
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
     * Determines the equality of two {@code MonitorPermission} objects.
     * Two {@code MonitorPermission} objects are equal if their target
     * strings are equal and the same set of actions are listed in their action
     * strings.
     * 
     * @param o the object being compared for equality with this object
     * @return {@code true} if the two permissions are equal
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
     * Get the action string associated with this permission.  The actions are
     * returned in the following order: {@code read}, {@code reset}, 
     * {@code publish}, {@code startjob}, {@code switchevents}.
     * 
     * @return the allowed actions separated by commas, cannot be
     *         {@code null}
     */
    public String getActions() {
        StringBuffer sb = new StringBuffer();

        appendAction(sb, READ_FLAG,         READ);
        appendAction(sb, RESET_FLAG,        RESET);
        appendAction(sb, PUBLISH_FLAG,      PUBLISH);
        appendAction(sb, STARTJOB_FLAG,     STARTJOB);
        appendAction(sb, SWITCHEVENTS_FLAG, SWITCHEVENTS);

        return sb.toString();
    }

    private void appendAction(StringBuffer sb, int flag, String actionName) {
        if ((mask & flag) != 0) {
            if(sb.length() != 0)
                sb.append(',');
            sb.append(actionName);
            
            if(flag == STARTJOB_FLAG && minJobInterval != 0)
                sb.append(':').append(minJobInterval);
        }
    }

    /**
     * Determines if the specified permission is implied by this permission.
     * <p>
     * This method returns {@code false} if and only if at least one of the
     * following conditions are fulfilled for the specified permission:
     * <ul>
     * <li>it is not a {@code MonitorPermission}
     * <li>it has a broader set of actions allowed than this one
     * <li>it allows initiating time based monitoring jobs with a lower minimal
     * sampling interval
     * <li>the target set of {@code Monitorable}s is not the same nor a
     * subset of the target set of {@code Monitorable}s of this permission
     * <li>the target set of {@code StatusVariable}s is not the same
     * nor a subset of the target set of {@code StatusVariable}s of this
     * permission
     * </ul>
     * 
     * @param p the permission to be checked
     * @return {@code true} if the given permission is implied by this
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
