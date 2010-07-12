package org.osgi.service.coordination;

import java.security.*;

import org.osgi.framework.*;

/**
 * The name parameter of the Permission is a filter expression. It asserts the
 * bundle that is associated with the coordination. Additionally, the following
 * attributes can be asserted:
 * <ol>
 * <li>coordination.name - The name of the coordination</li>
 * </ol>
 * 
 * <pre>
 *   Coordinator                          INITIATE    PARTICIPATE   ADMIN   NONE
 *     alwaysFail(String)                                                    -
 *     begin(String)                          +                              -
 *     getCoordinations()                                             +      -
 *     isActive()                             +            +          +      -
 *     isFailed()                             +            +          +      -
 *     participate(Participant)                            +                 -
 *     participateOrBegin(Participant)        +    and     +                 -
 *     
 *   Coordination
 *     end()                                                                 +
 *     fail(String)                                                          +
 *     getName()                                                             +
 *     getParticipants()                                              +      -
 *     isFailed()                                                            +
 *     setTimeout(long)                       +                       +      -
 *     terminate()                                                           +
 * </pre>
 * 
 */
public class CoordinationPermission extends BasicPermission {
	private static final long	serialVersionUID	= 1L;

	final Bundle				bundle;

	/**
	 * Initiate a Coordination. An owner of this permission can initiate, end,
	 * fail, and terminate a Coordination.
	 */
	public final static String	INITIATE			= "initiate";
	/**
	 * The action string <code>get</code>.
	 */
	public final static String	ADMIN				= "admin";
	/**
	 * The action string <code>get</code>.
	 */
	public final static String	PARTICIPATE			= "participate";

	/**
	 * The name parameter specifies a filter condition. The filter asserts the
	 * bundle that initiated the Coordination. An implicit grant is made for a
	 * bundle's own coordinations.
	 * 
	 * @param filterExpression A filter expression asserting the bundle associated with the
	 *        coordination.
	 * @param actions A comma separated combination of {@link #INITIATE},
	 *        {@link #ADMIN}, {@link #PARTICIPATE}.
	 */
	public CoordinationPermission(String filterExpression, String actions) {
		super(filterExpression, actions);
		this.bundle = null;
	}

	/**
	 * The verification permission
	 * 
	 * @param bundle The bundle that will be the target of the filter
	 *        expression.
	 * @param coordinationName The name of the coordination or <code>null</code>
	 * @param actions The set of actions required, which is a combination of
	 *        {@link #INITIATE}, {@link #ADMIN}, {@link #PARTICIPATE}.
	 */
	public CoordinationPermission(Bundle bundle, String coordinationName, String actions) {
		super(coordinationName, actions);
		this.bundle = bundle;
	}


	// For BJ to have lots of fun!


}
