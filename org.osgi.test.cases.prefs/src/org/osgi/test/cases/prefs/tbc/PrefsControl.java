/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */

package org.osgi.test.cases.prefs.tbc;

import java.util.*;

import org.osgi.test.cases.util.*;
import org.osgi.service.prefs.*;

/**
   This is the bundle initially installed and started by the TestCase
   when started. It performs the various tests and reports back to the
   TestCase.
*/
public class PrefsControl extends DefaultTestBundleControl {

    PreferencesService prefs;
    Preferences theNode;
    String nodetype;
    String value;

    static String[] methods = new String[] {
        "testSystemRoot" ,
        "testSystemNode",
        "testUserRoot",
        "testUserNode",
        "testRemovedNode",
        "testRemovedAncestor"

        /* Left to do:
           - Check system/user leakage
           - Check bunde leakage
           - PreferenceService.getUser()
        */
    };

    public String[] getMethods() {
        return methods;
    }

    /* Overridden from base class to add type of node to the log text */
	public void log(String test, String result)  {
	    StringBuffer testString = new StringBuffer();
	    testString.append(nodetype);
	
	    if(value != null) {
	        testString.append(", ");
	        testString.append(value);
	    }
	
	    testString.append(" - ");
	    testString.append(test);
	
	    // super.log(nodetype + ", " + value + " - " + test, result);
	    super.log(testString.toString(), result);
	}

    public boolean checkPrerequisites() {
		try {
	        prefs = (PreferencesService) getService(PreferencesService.class);
			return prefs != null;
		}
		catch( Exception e ) { e.printStackTrace();}
		return false;
    }

    /*** The data driven testmethods ***/

    public void testSystemRoot() throws Exception {
        theNode = prefs.getSystemPreferences();
        nodetype = "System root node";
        runAllTests();
    }

    public void testSystemNode() throws Exception {
        theNode = prefs.getSystemPreferences().node("somenode/anothernode");
        nodetype = "System node";
        runAllTests();
    }

    public void testUserRoot() throws Exception {
        theNode = prefs.getUserPreferences("theuser");
        nodetype = "User root node";
        runAllTests();
    }

    public void testUserNode() throws Exception {
        theNode = prefs.getUserPreferences("theuser").node("somenode/anothernode");
        nodetype = "User node";
        runAllTests();
    }

    public void testRemovedNode() throws Exception {
        theNode = prefs.getUserPreferences("theuser").node("removednode");
        theNode.removeNode();
        nodetype = "Removed node";
        runRemovedTests();
    }

    public void testRemovedAncestor() throws Exception {
        theNode = prefs.getUserPreferences("theuser").node("removedancestor/child/baby");
        Preferences ancestor = prefs.getUserPreferences("theuser").node("removedancestor");
        ancestor.removeNode();
        nodetype = "Removed ancestor";
        runRemovedTests();
    }


    /*** End of data drvien test methods ***/

    private void runAllTests() throws Exception {
        /* Test flush and sync */
        testFlush();
        testSync();

        /* Test paths, names and if node exists */
        testAbsolutePath();
        testNodeExists("");
        testName();
        testUnusualNames();
        testIllegalNames();

        /* Test if properties and nodenames are case sensitive */
        testCaseSensitivity();

        /* Test get and put methods */
        clear();
        testGetMethods(methodTest);
        testPutMethods(methodTest);

        /* Test reading and writing properties from different types */
        clear();
        testPropValues(propTest);

        /* Test checking for keys, removing and clearing properties */
        clear();
        put("somekey", "somevalue");
        put("someotherkey", "someothervalue");
        testKeys();
        runAllChecks();
        testRemove("somekey");
        runAllChecks();
        testClear();
        runAllChecks();


        /* Test adding children */
        testNode("child1");
        testNode("child2");
        testNode("child3");
        testNodeExists("child1");
        testChildrenNames();
        runAllChecks();
        wipeAllChildren();

        /* Test removing the node */
        testRemoveNode();
        runAllChecks();
    }

    /* This method runs useful tests when a node is supposed to
       have been removed */
    private void runRemovedTests() throws Exception {
        /* Test flush and sync */
        testFlush();
        testSync();

        /* Test paths, names and if node exists */
        testAbsolutePath();
        testNodeExists("");
        testName();

        /* Test get and put methods */
        testGetMethods(removedTest);
        testPutMethods(removedTest);

        /* Test checking for keys, removing and clearing properties */
        testKeys();
        testRemove("somekey");
        testClear();

        /* Test adding children */
        testNode("child1");
        testNodeExists("child1");
        testChildrenNames();

        /* Test removing the node */
        testRemoveNode();
    }

    private void runAllChecks() throws Exception {
        absolutePath();
        childrenNames();
        allGets(normalTest);
        keys();
        name();
        nodeExists("");
        parent();
    }



    /* The test methods */
    /* These methods sets the requirement markers and performs tests */

    private void testAbsolutePath() throws Exception {
        setReqMarker("prefs.absolutePath");
        absolutePath();
    }

	private void testChildrenNames() throws Exception {
        setReqMarker("prefs.childrenNames");
        childrenNames();
	}

    private void testClear() throws Exception {
        setReqMarker("prefs.clear");
        clear();
    }

    private void testFlush() throws Exception {
        setReqMarker("prefs.flush");
        flush();
    }

    private void testGetMethods(KeyAndValue[][] kv) throws Exception {
        for(int i = 0; i < kv.length; i++) {
            for(int j = 0; j < kv[i].length; j++) {
                setReqMarker("prefs.get" + getTypeString(kv[i][j].clazz));
                getMethod(theNode, kv[i][j].clazz,
                          kv[i][j].key, kv[i][j].value);
            }
        }
    }

    private void testKeys() throws Exception {
        setReqMarker("prefs.keys");
        keys();
    }

    private void testName() throws Exception {
        setReqMarker("prefs.name");
        name();
    }

    private void testNode(String pathName) throws Exception {
        setReqMarker("prefs.node");
        node(pathName);
    }

    private void testNodeExists(String pathName) throws Exception {
        setReqMarker("prefs.nodeExists");
        nodeExists(pathName);
    }

    private void testParent() throws Exception {
        setReqMarker("prefs.parent");
        parent();
    }

    private void testPutMethods(KeyAndValue[][] kv) throws Exception {
        for(int i = 0; i < kv.length; i++) {
            for(int j = 0; j < kv[i].length; j++) {
                setReqMarker("prefs.put" + getTypeString(kv[i][j].clazz));
                putMethod(theNode, kv[i][j].clazz,
                          kv[i][j].key, kv[i][j].value);
            }
        }
    }

    private void testRemove(String key) throws Exception {
        setReqMarker("prefs.remove");
        remove(key);
    }

    private void testRemoveNode() throws Exception {
        setReqMarker("prefs.removeNode");
        removeNode();
    }

    private void testSync() throws Exception {
        setReqMarker("prefs.sync");
        sync();
    }

    private void testPropValues(KeyAndValue[][] kv) throws Exception {
        setReqMarker("prefs.propvalues");

        value = "default";
        allGets(kv);

        /* Test putting in a string */
        theNode.put("thekey", "astring");
        value = "aString";
        allGets(kv);

        /* Test putting in a boolean string */
        theNode.put("thekey", "true");
        value = "true";
        allGets(kv);

        /* Test putting in a boolean */
        theNode.putBoolean("thekey", true);
        value = "true";
        allGets(kv);

        /* Test putting in a string that works as a bytearray too */
        theNode.put("thekey", "sometext");
        value = "sometext";
        allGets(kv);

        /* Test putting in a double/float string */
        theNode.put("thekey", "1.0");
        value = "1.0";
        allGets(kv);

        /* Test putting in the Double.MIN_VALUE*/
        theNode.putDouble("thekey", Double.MIN_VALUE);
        value = "Double.MIN_VALUE";
        allGets(kv);

        /* Test putting in the Float.MIN_VALUE*/
        theNode.putFloat("thekey", Float.MIN_VALUE);
        value = "Float.MIN_VALUE";
        allGets(kv);

        /* Test putting in an Integer/Long string */
        theNode.put("thekey", "1");
        value = "1";
        allGets(kv);

        /* Test putting in the Integer.MIN_VALUE Integer */
        theNode.putInt("thekey", Integer.MIN_VALUE);
        value = "Integer.MIN_VALUE";
        allGets(kv);

        /* Test putting in the Long.MIN_VALUE */
        theNode.putLong("thekey", Long.MIN_VALUE);
        value = "Long.MIN_VALUE";
        allGets(kv);

        value = null;
    }

    private void testIllegalNames() throws Exception {
        setReqMarker("prefs.illegalnames");
        String[] names = {
            "/node1/",
            "/node2//subnode",
            "node3/",
            "node4//subnode"
        };

        names(names);
        wipeAllChildren();
    }

    private void testUnusualNames() throws Exception {
        setReqMarker("prefs.unusualnames");
        String[] names = {
            "/.././_?/\\",
            "/ / / / ",
            ".././_?/\\",
            " / / / "
        };

        names(names);
        wipeAllChildren();
    }

    private void testCaseSensitivity() throws Exception {
        setReqMarker("prefs.casesensitivity");

        /* Test if keys are case-sensitive */
        theNode.put("Name", "UPPERCASE");
        log("Case sensitivity in keys", theNode.get("name", "lowercase"));

        /* Test if node names are case-sensitive */
        theNode.node("NameOfChild");
        log("Case sensitivity in names", "" + theNode.nodeExists("nameofchild"));
        wipeAllChildren();
    }





    /* The helper methods */
    /* These methods performs tests that may be called
       from multiple places (while testing different requrements.
       These methods may very well perform logs, but should NOT
       set requirement markers */

    private void absolutePath() throws Exception {
        log("Absolute path", theNode.absolutePath());
    }

    private void childrenNames() throws Exception {
        String testText = "Children";
        try {
    		String[] children = theNode.childrenNames();
            Arrays.sort(children);
            log(testText, arrayToString(children));
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }
    }

    private void clear() throws Exception {
        String testText = "clear";
        try {
            theNode.clear();
            log(testText, "ok");
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }
    }

    private void flush() throws Exception {
        String testText = "flush";
        try {
            theNode.flush();
            log(testText, theNode.absolutePath());
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }
    }

    private void getMethod(Preferences theNode,
            Class clazz, String key, Object def) throws Exception{
        String testText = null;

        try {
            String result = null;

            if(clazz == String.class ) {
                testText = "get";
                result = "String: " + theNode.get(key, (String) def);
            }
            else if(clazz == Boolean.class) {
                testText = "getBoolean";
                result = "boolean: " + theNode.getBoolean(key, ((Boolean) def).booleanValue());
            }
            else if(clazz == byte[].class) {
                testText = "getByteArray";
                byte[] answer = theNode.getByteArray(key, (byte[]) def);

                String byteString = null;
                if(answer != null) {
                    byteString = Hex.toHex(answer);
                }
                result = "byte[]: " + byteString;
            }
            else if(clazz == Double.class) {
                testText = "getDouble";
                /* use bits to avoid formatting differences between VMs */
                result = "doubleToLongBits: " + Double.doubleToLongBits( theNode.getDouble(key, ((Double) def).doubleValue()) );
            }
            else if(clazz == Float.class) {
                testText = "getFloat";
                /* use bits to avoid formatting differences between VMs */
                result = "floatToIntBits: " + Float.floatToIntBits( theNode.getFloat(key, ((Float) def).floatValue()) );
            }
            else if(clazz == Integer.class) {
                testText = "getInteger";
                result = "int: " + theNode.getInt(key, ((Integer) def).intValue());
            }
            else if(clazz == Long.class) {
                testText = "getLong";
                result = "long: " + theNode.getLong(key, ((Long) def).longValue());
            }
            else {
                testText = "get?";
                result = "No matching type";
            }
            log(testText, result);
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }
        catch(NullPointerException e) {
            log(testText, NullPointerException.class, e);
        }
    }

    private void keys() throws Exception {
        String testText = "keys";
        try {
            String[] keys = theNode.keys();
            Arrays.sort(keys);
            log(testText, arrayToString(keys));
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }
    }

    private void name() throws Exception {
        log("name", theNode.name());
    }

    private void node(String pathName) throws Exception {
        String testText = "Creating node";
        try {
            Preferences theNewNode = theNode.node(pathName);
            log(testText, theNewNode.absolutePath());
        }
        catch(IllegalArgumentException e) {
            log(testText, IllegalArgumentException.class, e);
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }
    }

    private void nodeExists(String pathName) throws Exception {
        String testText = "nodeExists";

        /* Test the relative path */
        try {
            log(testText, "" + theNode.nodeExists(pathName));
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }

        /* Test the absolute path */
        try {
            String absolutePath = theNode.absolutePath();
            if(pathName != null) {
                if(!"".equals(pathName) && (theNode.parent() != null)) {
                    absolutePath = absolutePath + "/" + pathName;
                }
            }
            log(testText, "" + theNode.nodeExists(absolutePath));
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }
    }


    private void parent() throws Exception {
        String testText = "parent";
        try {
            String parentName = null;
            Preferences parentNode = theNode.parent();
            if(parentNode != null) {
                parentName = parentNode.name();
            }
            log(testText, parentName);
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }
    }

    private void put(String key, String value) throws Exception {
        putMethod(theNode, String.class, key, value);
    }

    private void putMethod(Preferences theNode,
            Class clazz, String key, Object value) throws Exception{
        String testText = null;

        try {
            String result = null;

            if(clazz == String.class ) {
                testText = "put";
                theNode.put(key, (String) value);
                result = "String: " + theNode.get(key, null);
            }
            else if(clazz == Boolean.class) {
                testText = "putBoolean";
                boolean boolValue = ((Boolean) value).booleanValue();
                theNode.putBoolean(key, boolValue);
                result = "boolean: " + theNode.getBoolean(key, !boolValue);
            }
            else if(clazz == byte[].class) {
                testText = "putByteArray";
                theNode.putByteArray(key, (byte[]) value);
                byte[] answer = theNode.getByteArray(key, null);

                String byteString = null;
                if(answer != null) {
                    byteString = Hex.toHex(answer);
                }
                result = "byte[]: " + byteString;
            }
            else if(clazz == Double.class) {
                testText = "putDouble";
                double doubleValue = ((Double) value).doubleValue();
                theNode.putDouble(key, doubleValue);
                /* use bits to avoid formatting differences between VMs */
                result = "doubleToLongBits: " + Double.doubleToLongBits( theNode.getDouble(key, doubleValue - 1.0) );
            }
            else if(clazz == Float.class) {
                testText = "putFloat";
                float floatValue = ((Float) value).floatValue();
                theNode.putFloat(key, floatValue);
                /* use bits to avoid formatting differences between VMs */
                result = "floatToIntBits: " + Float.floatToIntBits( theNode.getFloat(key, (float) (floatValue - 1.0)) );
            }
            else if(clazz == Integer.class) {
                testText = "putInteger";
                int intValue = ((Integer) value).intValue();
                theNode.putInt(key, intValue);
                result = "int: " + theNode.getInt(key, intValue - 1);
            }
            else if(clazz == Long.class) {
                testText = "putLong";
                long longValue = ((Long) value).longValue();
                theNode.putLong(key, longValue);
                result = "long: " + theNode.getLong(key, longValue - 1);
            }
            else {
                testText = "put?";
                result = "No matching type";
            }
            log(testText, result);
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }
        catch(NullPointerException e) {
            log(testText, NullPointerException.class, e);
        }
    }

    private void remove(String key) throws Exception {
        String testText = "remove";
        try {
            theNode.remove(key);
            log(testText, key + " == " + theNode.get(key, null));
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }
    }


    private void removeNode() throws Exception {
        String testText = "Removing node";
        try {
            theNode.removeNode();
            log(testText, theNode.absolutePath());
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }
        catch(RuntimeException e) {
            log(testText, RuntimeException.class, e);
        }
    }

    private void sync() throws Exception {
        String testText = "sync";
        try {
            theNode.sync();
            log(testText, theNode.absolutePath());
        }
        catch(IllegalStateException e) {
            log(testText, IllegalStateException.class, e);
        }
    }

    private void allGets(KeyAndValue[][] kv) throws Exception {
        for(int i = 0; i < kv.length; i++) {
            for(int j = 0; j < kv[i].length; j++) {
                getMethod(theNode, kv[i][j].clazz,
                          kv[i][j].key, kv[i][j].value);
            }
        }
    }

    /**
     * Tries to get/create Preferences nodes from the <code>names</code>.
     * Checks if the node was created or not afterwards.
     */
    private void names(String[] names) throws Exception {
        String testText = "names";
        for(int i = 0; i < names.length; i++) {
            try {
                Preferences newNode = theNode.node(names[i]);
                log(testText, newNode.absolutePath());
            }
            catch(IllegalArgumentException e) {
                log(testText, IllegalArgumentException.class, e);
            }
            catch(IllegalStateException e) {
                log(testText, IllegalStateException.class, e);
            }
        }
    }

    private void wipeAllChildren() {
        try {
            String[] children = theNode.childrenNames();
            for(int i = 0; i < children.length; i++) {
                Preferences child = theNode.node(children[i]);
                child.removeNode();
            }
        }
        catch(Exception e) {
            /* Do nothing */
        }

    }








    private class KeyAndValue {
        Class clazz;
        String key;
        Object value;

        KeyAndValue(Class clazz, String key, Object value) {
            this.clazz = clazz;
            this.key = key;
            this.value = value;
        }
    }

    private KeyAndValue[] normal = {
        new KeyAndValue(String.class, "stringkey", "(empty)"),
        new KeyAndValue(Boolean.class, "booleankey", new Boolean(false)),
        new KeyAndValue(byte[].class, "bytearraykey", new byte[] {}),
        new KeyAndValue(Double.class, "doublekey", new Double(0.0)),
        new KeyAndValue(Float.class, "floatkey", new Float(0.0)),
        new KeyAndValue(Integer.class, "integerkey", new Integer(0)),
        new KeyAndValue(Long.class, "longkey", new Long(0))
    };

    private KeyAndValue[] nullKeys = {
        new KeyAndValue(String.class, null, "(empty)"),
        new KeyAndValue(Boolean.class, null, new Boolean(false)),
        new KeyAndValue(byte[].class, null, new byte[] {}),
        new KeyAndValue(Double.class, null, new Double(0.0)),
        new KeyAndValue(Float.class, null, new Float(0.0)),
        new KeyAndValue(Integer.class, null, new Integer(0)),
        new KeyAndValue(Long.class, null, new Long(0))
    };

    private KeyAndValue[] nullValues = {
        new KeyAndValue(String.class, "stringkey", null),
        new KeyAndValue(byte[].class, "bytearraykey", null),
    };


    private KeyAndValue[] oneKey = {
        new KeyAndValue(String.class, "thekey", "(empty)"),
        new KeyAndValue(Boolean.class, "thekey", new Boolean(false)),
        new KeyAndValue(byte[].class, "thekey", new byte[] {}),
        new KeyAndValue(Double.class, "thekey", new Double(0.0)),
        new KeyAndValue(Float.class, "thekey", new Float(0.0)),
        new KeyAndValue(Integer.class, "thekey", new Integer(0)),
        new KeyAndValue(Long.class, "thekey", new Long(0))
    };


    KeyAndValue[][] methodTest = { normal, nullKeys, nullValues };
    KeyAndValue[][] normalTest = { normal };
    KeyAndValue[][] removedTest = { normal };
    KeyAndValue[][] propTest = { oneKey };

    private String getTypeString(Class clazz) {
        String type = null;
        if(clazz == String.class ) {
            type = "";
        }
        else if(clazz == Boolean.class) {
            type = "Boolean";
        }
        else if(clazz == byte[].class) {
            type = "ByteArray";
        }
        else if(clazz == Double.class) {
            type = "Double";
        }
        else if(clazz == Float.class) {
            type = "Float";
        }
        else if(clazz == Integer.class) {
            type = "Integer";
        }
        else if(clazz == Long.class) {
            type = "Long";
        }
        return type;
    }

}
