package org.osgi.service.dmt;

/** @modelguid {F4F2E3F6-718D-4300-A997-557695CA36F9} */
public interface DmtDataPlugin extends Dmt {
	/** @modelguid {3459C046-BD35-4F95-8939-FFE6C0484B2F} */
   public void open(int lockMode) throws DmtException;   
	/** @modelguid {CEDBE0F7-8F07-486A-BC9B-8831D898C32A} */
   public void open(String subtreeUri, int lockMode) throws DmtException;
	/** @modelguid {E8EAE059-D845-47D5-AF3A-8D5101C388C9} */
   public DmtMetaNode getMetaNode(DmtMetaNode generic);
}
