package org.osgi.service.dmt;

/** @modelguid {92C02305-6E6D-4A28-AB2B-9506DC0FBB74} */
public class DmtData {
	/** @modelguid {05CD0F17-6725-43A6-BD61-DF1C927BED12} */
	String mimeType;


	

	/** @modelguid {859226BD-AD91-4813-9D09-066EC5E7CAE9} */
	Object value;

	/** @modelguid {88AD2F3D-8DA4-49E5-ABD8-195FAF900053} */
	public DmtData() {
	}
	/** @modelguid {3770C89D-FC04-404F-8B20-5419FA9B9969} */
	public DmtData(String str) {
		value = str;
	}

	/** @modelguid {AD50C4B0-2AA2-49FB-8A96-5EBD78811957} */
	public DmtData(int integer) {
		value = new Integer(integer);
	}

	/** @modelguid {846F9B60-83EE-451E-9B92-FA4EAE1EEF14} */
	public DmtData(boolean bool) {
		value = new Boolean(bool);
	}

	/** @modelguid {E64774DA-FAD1-4869-A9BD-518E8B54F634} */
	public DmtData(byte[] bin) {
		value = bin;
	}

	// TODO: duplicated constructor
	//	public DmtData(String mimeType) {
	//		this.mimeType = mimeType;
	//	}

	/** @modelguid {0194878E-5F6A-4DBD-B625-8D5E0FDCF073} */
	public DmtData(String str, String mimeType) {
	}
	/** @modelguid {038A2573-5ED0-4CC8-AB2A-EF07292EC212} */
	public DmtData(int integer, String mimeType) {
	}
	/** @modelguid {AE1A7428-05E1-479F-B27E-FDCF7D3B2859} */
	public DmtData(boolean bool, String mimeType) {
	}
	/** @modelguid {775EECF0-E313-47CD-A9D9-77126DC4E66E} */
	public DmtData(byte[] bin, String mimeType) {
	}
	/** @modelguid {D9CFDDD4-22B0-4450-A83F-C71C8BD87D13} */
	public String getString() {
		return "" + value;
	}
	/** @modelguid {485CEA14-2465-4548-A018-1A550C771F6D} */
	public boolean getBoolean() throws DmtException {
		return ((Boolean) value).booleanValue();
	}
	/** @modelguid {EBC8DAA7-E054-4727-BE79-AB359DE33274} */
	public int getInt() throws DmtException {
		return ((Integer) value).intValue();
	}

	/** @modelguid {A5654327-0DB4-4382-85E4-6A61E2D789AF} */
	public byte[] getBinary() {
		return (byte[]) value;
	}

	/** @modelguid {CED559B0-7879-4BFD-82CD-B2C1A1711713} */
	public int getFormat() {
		if (value == null)
			return -1;
		else if (value instanceof String)
			return DmtDataType.STRING;
		else if (value instanceof Integer)
			return DmtDataType.INTEGER;
		else if (value instanceof Boolean)
			return DmtDataType.BOOLEAN;
		else if (value instanceof byte[])
			return DmtDataType.BINARY;
		else
			return -1;
	}
	
	/** @modelguid {2212745D-7603-42AD-B553-789F5879A4C6} */
	public String getMimeType() {
		return mimeType;
	}

}
