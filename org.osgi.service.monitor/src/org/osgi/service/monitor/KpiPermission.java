package org.osgi.service.monitor;

import java.security.Permission;
import java.util.StringTokenizer;

/**
 * Indicates the callers authority to publish, read or reset KPIs or to start
 * monitoring jobs. The target of the permission is the identifier of the KPI,
 * the action can be <code>read</code>,<code>publish</code>,
 * <code>reset</code>,<code>startjob</code> or the combination of these
 * separated by commas.
 */
public class KpiPermission extends Permission {
    private static final int READ     = 0x1;
    private static final int RESET    = 0x2;
    private static final int PUBLISH  = 0x4;
    private static final int STARTJOB = 0x8;

    private String monId;
    private String kpiId;

    private boolean prefixMonId;
    private boolean prefixKpiId;

    private int mask;
    private int minJobInterval;

    /**
	 * Create a KpiPermission object, specifying the target and actions.
	 * 
	 * @param kpi The identifier of the KPI in [Monitorable_id]/[KPI_id] format.
	 *        The wildcard <code>*</code> is allowed in both fragments of the
	 *        target string, but only at the end of the fragments.
	 *        <p>
	 *        The following targets are valid:
	 *        <code>com.mycomp.myapp/queue_length</code>,
	 *        <code>com.mycomp.&#42;/queue*</code>,
	 *        <code>com.mycomp.myapp/*</code>, <code>&#42;/*</code>.
	 *        <p>
	 *        The following targets are invalid:
	 *        <code>*.myapp/queue_length</code>, <code>com.*.myapp/*</code>,
	 *        <code>*</code>.
	 * @param actions The allowed action(s): <code>read</code>,
	 *        <code>publish</code>, <code>startjob</code>,
	 *        <code>reset</code>, or the combination of these separated by
	 *        commas.
	 *        <p>
	 *        In case of the <code>startjob</code> action a minimal sampling
	 *        interval can be optionally defined in the following form:
	 *        <code>startjob:n</code>. Here <code>n</code> is the allowed
	 *        minimal value of the schedule parameter of time based monitoring
	 *        jobs the holder of this permission is allowed to initiate. If
	 *        <code>n</code> is not specified or 0 then the holder of this
	 *        permission is allowed to start monitoring jobs specifying any
	 *        frequency.
	 */
    public KpiPermission(String kpi, String actions) {
        super(kpi);

        int sep = kpi.indexOf('/');
        int len = kpi.length();

        if(sep == -1)
            throw new IllegalArgumentException(
                    "Invalid KPI path: should contain '/' separator.");
        if(sep == 0 || sep == kpi.length()-1)
            throw new IllegalArgumentException(
                    "Invalid KPI path: empty monitorable ID or KPI name.");

        // TODO: full ID check?  (e.g. no / in kpiId, no invalid characters)

        prefixMonId = kpi.charAt(sep-1) == '*';
        prefixKpiId = kpi.charAt(len-1) == '*';

        monId = kpi.substring(0,     prefixMonId ? sep-1 : sep);
        kpiId = kpi.substring(sep+1, prefixKpiId ? len-1 : len);

        mask = 0;
        minJobInterval = 0;

        StringTokenizer st = new StringTokenizer(actions, ",");
        while(st.hasMoreTokens()) {
            String action = st.nextToken();
            if(action.equalsIgnoreCase("read")) {
                mask |= READ;
            } else if(action.equalsIgnoreCase("reset")) {
                mask |= RESET;
            } else if(action.equalsIgnoreCase("publish")) {
                mask |= PUBLISH;
            } else if(action.toLowerCase().startsWith("startjob")) {
                minJobInterval = 0;

                int slen = "startjob".length();
                if(action.length() != slen) {
                    if(action.charAt(slen) != ':')
                        throw new IllegalArgumentException(
                                "Invalid action '" + action + "'.");

                    try {
                        minJobInterval = 
                            Integer.parseInt(action.substring(slen+1));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "Invalid parameter in startjob action '" + 
                                action + "'.");
                    }
                }
                mask |= STARTJOB;
            } else
                throw new IllegalArgumentException(
                        "Invalid action '" + action + "'");
        }
    }

    /**
	 * Create an integer hash of the object. The hash codes of KpiPermissions p1
	 * and p2 are the same if and only if <code>p1.equals(p2)</code>
	 * 
	 * @return the hash of the object
	 */
    public int hashCode() {
        return
            new Integer(mask).hashCode() ^
            new Integer(minJobInterval).hashCode() ^
            monId.hashCode() ^ new Boolean(prefixMonId).hashCode() ^
            kpiId.hashCode() ^ new Boolean(prefixKpiId).hashCode();
    }

    /**
	 * Determines the equality of two <code>KpiPermission</code> objects. Two
	 * <code>KpiPermission</code> objects are equal if their target and action
	 * strings are equal.
	 * 
	 * @param o the object being compared for equality with this object
	 * @return <code>true</code> if the two permissions are equal
	 */
    public boolean equals(Object o) {
        if(!(o instanceof KpiPermission))
            return false;

        KpiPermission other = (KpiPermission) o;

        return
            mask == other.mask && minJobInterval == other.minJobInterval &&
            monId.equals(other.monId) && prefixMonId == other.prefixMonId &&
            kpiId.equals(other.kpiId) && prefixKpiId == other.prefixKpiId;
    }

    /**
	 * Get the action string associated with this permission
	 * 
	 * @return the allowed actions separated by commas
	 */
    public String getActions() {
        StringBuffer sb = new StringBuffer();
        boolean comma = false;

        if((mask & READ) != 0) {
            comma = true;
            sb.append("read");
        }
        if((mask & RESET) != 0) {
            if(comma) sb.append(',');
            else comma = true;
            sb.append("reset");
        }
        if((mask & PUBLISH) != 0) {
            if(comma) sb.append(',');
            else comma = true;
            sb.append("publish");
        }
        if((mask & STARTJOB) != 0) {
            if(comma) sb.append(',');
            else comma = true;
            sb.append("startjob");
            if(minJobInterval != 0)
                sb.append(':').append(minJobInterval);
        }

        return sb.toString();
    }

    /**
	 * Determines if the specified permission is implied by this permission.
	 * <p>
	 * This method returns <code>false</code> if
	 * <li>the specified permission is not a KpiPermission
	 * <li> or it has a broader set of actions allowed than this one
	 * <li> or it allows initiating time based monitoring jobs with a lower
	 * minimal sampling interval
	 * <li> or the target set of Monitorables is not the same or not a subset
	 * of the target set of Monitorables of this permission
	 * <li> or the target set of KPIs is not the same or not a subset of the
	 * target set of KPIs of this permission.
	 * <p>
	 * Otherwise it returns <code>true</code>.
	 * 
	 * @param p the permission to be checked
	 * @return <code>true</code> if the given permission is implied by this
	 *         permission
	 */
    public boolean implies(Permission p) {
        if(!(p instanceof KpiPermission))
            return false;

        KpiPermission other = (KpiPermission) p;

        if((mask & other.mask) != other.mask)
            return false;

        if((other.mask & STARTJOB) != 0 && 
                minJobInterval > other.minJobInterval)
            return false;

        return
            implies(monId, prefixMonId, other.monId, other.prefixMonId) &&
            implies(kpiId, prefixKpiId, other.kpiId, other.prefixKpiId);
    }

    private boolean implies(String id, boolean prefix,
                            String oid, boolean oprefix) {

        return prefix ? oid.startsWith(id) : !oprefix && id.equals(oid);
    }
}
