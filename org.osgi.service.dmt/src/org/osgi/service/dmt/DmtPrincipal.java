package org.osgi.service.dmt;

/**
 * The representation of an entity having the role of manipulating the Device
 * Management Tree.
 */
public class DmtPrincipal {
	private String	name;

	/**
	 * Create a DmtPrincipal instance. This empty constructor is based on an
	 * internal identity, such as bundle ID, code signer, subscription ID,
	 * carrier ID etc. The policies enforced for such an identity are expressed
	 * by the <code>DmtPermission</code> class. This constructor can be used
	 * by any application, and no special permission is defined to restrict its
	 * use. It is intended for use by applications not having management roles.
	 */
	public DmtPrincipal() {
		// Principals created this way are not used anywhere
		name = null;
	}

	/**
	 * Create a DmtPrincipal instance for the given named entity. This
	 * string-based constructor is used primarily by agents serving various DM
	 * protocols as well as administrative applications setting ACLs. In this
	 * case identities are external (not derivable from the device's
	 * environment), such as management server identities, determined during the
	 * authentication process. This constructor can be used only by the trusted
	 * code, and a special DmtPrincipalPermission class is defined to enforce
	 * this limitation. [TODO: check this] For example, the identity of the OMA
	 * DM server can be established during the handshake between the OMA DM
	 * agent and the server.
	 * 
	 * @param name The name associated with this principal
	 * @throws DmtException
	 */
	public DmtPrincipal(String name) throws DmtException {
		// TODO check whether caller has permission to create a named principal
		this.name = name;
	}

	/**
	 * Get the name associated with this principal.
	 * 
	 * @return The name associated with this principal, or <code>null</code>
	 *         if no name was specified at creation.
	 */
	public String getName() {
		return name;
	}
}
