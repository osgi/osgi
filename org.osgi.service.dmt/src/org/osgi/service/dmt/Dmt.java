package org.osgi.service.dmt;

import java.util.Date;

/** @modelguid {B53A25FF-173A-465D-9545-3BDD250B7DD9} */
public interface Dmt  {
	/** @modelguid {9C089E98-A5F6-4E0E-BD3B-B28AA87A9780} */
   public void close()throws DmtException;

	/** @modelguid {7C3B4ACA-AE84-4C19-B968-DB31FD0ACD5A} */
   public void rollback()throws DmtException;
	/** @modelguid {8C6382EC-D7B6-468E-8A5A-939FEFDED4F2} */
   public DmtData getNodeValue(String nodeUri) throws DmtException;
	/** @modelguid {3DC24CDA-85EE-45C1-96EF-975C5AACF65B} */
   public String getNodeTitle(String nodeUri) throws DmtException;
	/** @modelguid {53870056-FBC5-47A7-B4FD-29D0C3DB9334} */
   public int getNodeVersion(String nodeUri) throws DmtException;
	/** @modelguid {A5B1C9BE-276B-4AD1-A44F-1A528333AF3B} */
   public Date getNodeTimestamp(String nodeUri) throws DmtException;
	/** @modelguid {E1093A0D-FF36-4481-9A6E-CBDB883D44C2} */
   public int getNodeSize(String nodeUri) throws DmtException;
	/** @modelguid {633F84D7-A538-4BC2-9A4E-CEFC1AF7B9FA} */
   public void setNodeTitle(String nodeUri, String title) throws DmtException;
	/** @modelguid {D4B139C4-34AD-47D7-A0FD-4C68B2DE8199} */
   public void setNode(String nodeUri, DmtData data) throws DmtException;
	/** @modelguid {311316F8-EC4C-456A-9311-08E20675D842} */
   public void setNode(String nodeUri, DmtData data, String type) throws DmtException;
	/** @modelguid {E2820B75-740A-4C44-9F42-AEBFC1F8D79B} */
   public String [] getChildNodeNames(String nodeUri) throws DmtException;

	/** @modelguid {50C78D8E-9042-47BF-9F20-7C8EE04EAC05} */
   public void deleteNode(String nodeUri) throws DmtException;

	/** @modelguid {541A6649-FF31-4D74-BD37-D25134933C78} */
   public void createInteriorNode(String nodeUri) throws DmtException;

	/** @modelguid {8C7B4632-76F7-4DFF-9068-FCD2291980CA} */
   public void createInteriorNode(String nodeUri, String type) throws DmtException;

	/** @modelguid {4F98AC66-22C1-41B3-93B9-CFD67D716AF2} */
   public void createLeafNode(String nodeUri, DmtData value) throws DmtException;

	/** @modelguid {A6A00964-9F40-46F3-A86E-B2D87BA06497} */
   public void createLeafNode(String nodeUri, DmtData value, String type) throws DmtException;

	/** @modelguid {07BAD7F1-C6A0-47A8-9399-F89D972AD1FC} */
   public void clone(String nodeUri, String newNodeUri) throws DmtException;

	/** @modelguid {63B956D6-FD19-4286-9BA4-663E201FFFC3} */
   public void renameNode(String nodeUri, String newName) throws DmtException;
}
