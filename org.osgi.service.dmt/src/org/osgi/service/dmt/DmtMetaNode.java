package org.osgi.service.dmt;

/** @modelguid {4DC28E2F-824C-49B3-9832-918CE735B4BB} */
public interface DmtMetaNode {
	/** @modelguid {57F15304-68A1-4886-8134-9638779F9F2F} */
   public boolean canDelete();
	/** @modelguid {A0104526-0C7D-47A2-9FC5-0C6984A0DF37} */
   public boolean canAdd();
	/** @modelguid {883A8643-235B-4E10-8677-E84A72F31464} */
   public boolean canGet();
	/** @modelguid {A8153BFD-B0A6-4BA9-A2A3-EBC771B11B45} */
   public boolean canReplace();
	/** @modelguid {B5798E9D-15B0-45EC-AE2A-E95D9598A357} */
   public boolean canExecute();
	/** @modelguid {F7D5C173-E06D-49FC-A0AF-C55294C3A609} */
   public boolean isLeaf();
	/** @modelguid {FF2E8346-3DAE-4E17-BBFC-6A77E634CE81} */
   public boolean isPermanent();
	/** @modelguid {F6000F31-10C2-43CF-9CE3-DAB94138FEEE} */
   public String getDescription();
	/** @modelguid {9CF8329D-90BD-46FB-AC43-0B23788CC9B7} */
   public int getMaxOccurrence();
	/** @modelguid {097B081F-7CDA-4FBC-BD91-77E25855BD60} */
   public boolean isZeroOccurrenceAllowed();
	/** @modelguid {D0084872-6831-41FF-A0C6-67D7EFF24786} */
   public DmtData getDefault();
	/** @modelguid {7691B841-742B-4B6C-89ED-4B66B2331FD7} */
   public boolean hasMax();
	/** @modelguid {E557F102-24A7-477B-86E8-39974D629843} */
   public boolean hasMin();
	/** @modelguid {9BB43C9E-7EFB-4EA5-85E3-47E72069A595} */
   public int getMax();
	/** @modelguid {5132AE43-8A7D-475D-A8F5-69E67002829A} */
   public int getMin();
	/** @modelguid {479D11AD-E00B-4515-AA92-4B8BD2C09CA6} */
   public DmtData [] getValidValues();
	/** @modelguid {7E952258-98D9-4C92-9C2C-997C7C243A8B} */
   public int getType();
	/** @modelguid {088EEA26-CE9C-4E69-A51F-B6D703E502A9} */
   public String getRegExp();
	/** @modelguid {2A50E589-E3E8-4ACD-B6B2-3603B6EEB1ED} */
   public String [] getMimeTypes();
	/** @modelguid {AFA3BB7D-2D12-498E-9D79-801081625CE3} */
   public String getReferredURI();
	/** @modelguid {6ED4571B-3D70-445C-8823-3D2A5D24BA2E} */
   public String [] getDependentURIs();
	/** @modelguid {366E56CD-E846-4D8E-8BB6-258339C9AC4C} */
   public String [] getChildURIs();
}
