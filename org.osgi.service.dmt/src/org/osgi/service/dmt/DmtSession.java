package org.osgi.service.dmt;

/** @modelguid {E11CC9BD-8344-4707-8175-6CE3AD128B09} */
public interface DmtSession extends Dmt {
	/** @modelguid {82DB5CD2-6BF1-4682-9457-96266169AB47} */
	public int getLockType();

	/** @modelguid {B9811BF2-A230-410D-9C60-0635F89EFD3F} */
	public DmtPrincipal getPrincipal();

	/** @modelguid {C36A901B-6FDA-41DB-96F3-9792762B289B} */
	public int getSessionId();

	/** @modelguid {E7147F6A-B1EA-4C95-BAC1-06C522CEF8CD} */
	public String execute(String nodeUri, String data) throws DmtException;

	/** @modelguid {F40BA902-F067-40EC-AA2B-850CFAB28669} */
	public boolean isNodeUri(String nodeUri);

	/** @modelguid {DBAC3B9A-DDB3-4B66-A6C6-03934BED06BC} */
	public boolean isLeafNode(String nodeUri);

	/** @modelguid {ACD417A8-AA78-4684-AACD-C888DB3922A9} */
	public DmtAcl getNodeAcl(String nodeUri) throws DmtException;

	/** @modelguid {398FB3F2-1CF8-49DA-86B1-CC6F27FDBEEE} */
	public void setNodeAcl(String nodeUri, DmtAcl acl) throws DmtException;

	/** @modelguid {739491E3-2E32-4388-9985-49951218F3D9} */
	public DmtMetaNode getMetaNode(String nodeUri) throws DmtException;
}
