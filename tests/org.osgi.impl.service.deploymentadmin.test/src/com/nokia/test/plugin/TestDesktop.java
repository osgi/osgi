package com.nokia.test.plugin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import info.dmtree.DmtAdmin;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;


public class TestDesktop extends Frame implements ActionListener {
    
    private static final String TEST_ROOT_PROPERTY = "com.nokia.test.plugin.root"; 
    private static final String TEST_FILE_PROPERTY = "com.nokia.test.plugin.file"; 
    
    private static final String TEST_ROOT_DEFAULT = "./OSGi/Deployment";
    private static final String TEST_FILE_DEFAULT = 
        "../../org.osgi.impl.service.deploymentadmin.test/DmtPluginTest.txt";

    private Panel pa_left = new Panel();
    private Panel pa_right = new Panel();
    private Panel pa_right_top = new Panel();
    private Panel pa_right_bottom = new Panel();
    
    private static final String IS_NODE_URI          = "isNodeUri";
    private static final String IS_LEAF_NODE         = "isLeafNode";
    private static final String GET_META_NODE        = "getMetaNode";
    private static final String GET_CHILD_NODE_NAMES = "getChildNodeNames";
    private static final String CREATE_INT_NODE      = "createInteriorNode";
    private static final String CREATE_LEAF_NODE     = "createLeafNode";
    private static final String DELETE_NODE          = "deleteNode";
    private static final String SET_NODE_VALUE       = "setNodeValue";
    private static final String GET_NODE_VALUE       = "getNodeValue";
    private static final String EXECUTE              = "execute";
    private static final String REOPEN               = "reopen";
    private static final String MAKE_TEST            = "MAKE TEST";
    private static final String RUN_TESTS            = "RUN TESTS";
    
    private TextField tf_uri = new TextField(); 
    private TextField tf_result = new TextField();
    
    private Button b_isNodeUri = new Button(IS_NODE_URI);
    private Button b_isLeafNode = new Button(IS_LEAF_NODE);
    private Button b_getMetaNode = new Button(GET_META_NODE);
    private Button b_getChildNodeNames = new Button(GET_CHILD_NODE_NAMES);
    private Button b_createInteriorNode = new Button(CREATE_INT_NODE);
    private Button b_createLeafNode = new Button(CREATE_LEAF_NODE);
    private Button b_deleteNode = new Button(DELETE_NODE);
    private Button b_setNodeValue = new Button(SET_NODE_VALUE);
    private Button b_getNodeValue = new Button(GET_NODE_VALUE);
    private Button b_execute = new Button(EXECUTE);
    private Button b_reopen = new Button(REOPEN);
    private Button b_makeTest = new Button(MAKE_TEST);
    private Button b_runTests = new Button(RUN_TESTS);
    
    private Checkbox cb_negativeTest = new Checkbox("Negative test");
    
    private Label la_passed = new Label();
    
    private DmtAdmin admin;
    private DmtSession session;
    
    private String lastCommand;
    private String lastValue;
    
    private String root;
    private String testFile;
    
    public TestDesktop(DmtAdmin admin) throws Exception {
        root     = System.getProperty(TEST_ROOT_PROPERTY, TEST_ROOT_DEFAULT);
        testFile = System.getProperty(TEST_FILE_PROPERTY, TEST_FILE_DEFAULT);
        
        this.admin = admin;
        session = admin.getSession(root, DmtSession.LOCK_TYPE_ATOMIC);
        setLayout(new GridLayout(1, 0));
        addWindowListener(new WindowAdapter() {
            	public void windowClosing(WindowEvent e) {
            	    setVisible(false);
            	    dispose();
            	}
            });
        
        pa_left.setLayout(new GridLayout(8, 0));
        pa_right.setLayout(new GridLayout(2, 0));
        pa_right_top.setLayout(new GridLayout(5, 1));
        pa_right_bottom.setLayout(new GridLayout(5, 1));
        add(pa_left);
        add(pa_right);
        pa_right.add(pa_right_top);
        pa_right.add(pa_right_bottom);
        
        tf_uri.setText(root);
        pa_right_top.add(tf_uri);
        pa_right_top.add(tf_result);
        
        b_isNodeUri.setActionCommand(IS_NODE_URI);
        b_isNodeUri.addActionListener(this);
        pa_left.add(b_isNodeUri);

        b_isLeafNode.setActionCommand(IS_LEAF_NODE);
        b_isLeafNode.addActionListener(this);
        pa_left.add(b_isLeafNode);
        
        b_getMetaNode.setActionCommand(GET_META_NODE);
        b_getMetaNode.addActionListener(this);
        pa_left.add(b_getMetaNode);

        b_getChildNodeNames.setActionCommand(GET_CHILD_NODE_NAMES);
        b_getChildNodeNames.addActionListener(this);
        pa_left.add(b_getChildNodeNames);
        
        b_createInteriorNode.setActionCommand(CREATE_INT_NODE);
        b_createInteriorNode.addActionListener(this);
        pa_left.add(b_createInteriorNode);
        
        b_createLeafNode.setActionCommand(CREATE_LEAF_NODE);
        b_createLeafNode.addActionListener(this);
        pa_left.add(b_createLeafNode);
        
        b_setNodeValue.setActionCommand(SET_NODE_VALUE);
        b_setNodeValue.addActionListener(this);
        pa_left.add(b_setNodeValue);
        
        b_getNodeValue.setActionCommand(GET_NODE_VALUE);
        b_getNodeValue.addActionListener(this);
        pa_left.add(b_getNodeValue);
        
        b_deleteNode.setActionCommand(DELETE_NODE);
        b_deleteNode.addActionListener(this);
        pa_left.add(b_deleteNode);
        
        b_execute.setActionCommand(EXECUTE);
        b_execute.addActionListener(this);
        pa_left.add(b_execute);
        
        b_reopen.setActionCommand(REOPEN);
        b_reopen.addActionListener(this);
        pa_left.add(b_reopen);
        
        b_makeTest.setActionCommand(MAKE_TEST);
        b_makeTest.addActionListener(this);
        pa_left.add(b_makeTest);
        
        b_runTests.setActionCommand(RUN_TESTS);
        b_runTests.addActionListener(this);
        pa_left.add(b_runTests);
        
        pa_left.add(cb_negativeTest);
        
        pa_left.add(la_passed);
        
        pack();
        setSize(1088, 512);
        setVisible(true);
        
        runTests();
    }
    
    void destroy() throws DmtException {
        session.close();
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            action(ae.getActionCommand(), tf_uri.getText(), null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String action(String acc, String uri, String value) throws Exception {
        String res = null; 
        
        if (IS_NODE_URI.equals(acc)) {
            lastCommand = acc;
            res = Boolean.toString(session.isNodeUri(uri));
        } else if (IS_LEAF_NODE.equals(acc)) {
            lastCommand = acc;
            res = Boolean.toString(session.isLeafNode(uri));
        } else if (GET_CHILD_NODE_NAMES.equals(acc)) {
            lastCommand = acc;
            String[] sa = session.getChildNodeNames(uri);
            res = Arrays.asList(sa).toString();
        } else if (CREATE_INT_NODE.equals(acc)){
        	lastCommand = acc;
        	session.createInteriorNode(uri);
        } else if (CREATE_LEAF_NODE.equals(acc)){
        	lastCommand = acc;
            String text;
            if (null != value)
                text = value;
            else {
                text = JOptionPane.showInputDialog(this, "");
                lastValue = text;
            }
        	session.createLeafNode(uri, dmtFromString(text));
        } else if (DELETE_NODE.equals(acc)) {
            lastCommand = acc;
            session.deleteNode(uri);
        } else if (SET_NODE_VALUE.equals(acc)){
        	lastCommand = acc;
            String text;
            if (null != value)
                text = value;
            else {
                text = JOptionPane.showInputDialog(this, "");
                lastValue = text;
            }
        	session.setNodeValue(uri, dmtFromString(text));
        } else if (GET_NODE_VALUE.equals(acc)) {
            lastCommand = acc;
            res = session.getNodeValue(uri).toString();
        } else if (GET_META_NODE.equals(acc)) {
            lastCommand = acc;
            res = session.getMetaNode(uri).toString();
        } else if (EXECUTE.equals(acc)) {
                lastCommand = acc;
                session.execute(uri, "", "");
        } else if (REOPEN.equals(acc)) {
                lastCommand = acc;
                session.close();
                session = admin.getSession(root, DmtSession.LOCK_TYPE_ATOMIC);
        } else if (MAKE_TEST.equals(acc)) {
            if (null == lastCommand)
                return null;
            File f = new File(testFile);
            if (!f.exists())
                f.createNewFile();
            PrintWriter pw = new PrintWriter(new FileWriter(f, true));
            String neg = "" + cb_negativeTest.getState();
            pw.print(uri + "\t" + lastCommand + "\t" + tf_result.getText() + 
                    "\t" + neg);
            if (SET_NODE_VALUE.equals(lastCommand))
                pw.println("\t" + lastValue);
            if (CREATE_LEAF_NODE.equals(lastCommand))
                pw.println("\t" + lastValue);
            else
                pw.println();
            pw.close(); // TODO finally etc.
            lastCommand = null;
            lastValue = null;
        } else if (RUN_TESTS.equals(acc)) {
            runTests();
        }
        
        tf_result.setText(res);
        
        return res;
    }
    
    private void runTests() throws IOException {
        File f = new File(testFile);
        BufferedReader r = new BufferedReader(new FileReader(f));
        String line = r.readLine();
        int passedC = 0;
        int failedC = 0;
        ArrayList failedLines = new ArrayList();
        int lineNo = 0;
        while (null != line) {
            ++lineNo;
            if (line.trim().equals("")) {
                line = r.readLine();
                continue;
            }
                
            String[] sa = line.split("\t", 0);
            boolean neg = Boolean.valueOf(sa[3]).booleanValue();
            String res = "";
            Exception ex = null;
            try {
                String value = null;
                if (sa[1].equals(SET_NODE_VALUE))
                    value = sa[4];
                if (sa[1].equals(CREATE_LEAF_NODE))
                    value = sa[4];
                res = action(sa[1], sa[0], value);
                res = (null == res ? "" : res);
            } catch (Exception e) {
                ex = e;
            }
            
            //System.out.println(line);
            //System.out.print(" " + res + " " + ex + " --> ");
            boolean passed;
            if (!neg) {
                passed = (ex == null && res.equals(sa[2]));
            } else {
                passed = (ex != null);
            }
            //System.out.println(passed);
            if (passed)
                ++passedC;
            else {
                ++failedC;
                failedLines.add(new Long(lineNo));
            }
            la_passed.setText("RESULT (passed/failed): " + passedC + " / " + failedC);
            line = r.readLine();
        }
        r.close();
        System.out.println("FAILED LINES: " + failedLines);
    }

    // copied from org.osgi.meg.demo.remote.CommandProcessor, keep in sync.
    private DmtData dmtFromString(String typedata) {
        // TODO add new types (here and in remote gui)
        // expected format eg "int:23"
        int pos = typedata.indexOf(":");
        String type = typedata.substring(0, pos);
        String data = typedata.substring(pos + 1);
        DmtData value = null;
        if (type.equals("int")) {
            value = new DmtData(Integer.parseInt(data));
        }
        else if (type.equals("bool")) {
            value = new DmtData((new Boolean(data)).booleanValue());
        }
        else if (type.equals("chr")) {
            value = new DmtData(data);
        }
        else if (type.equals("xml")) {
            value = new DmtData(data, DmtData.FORMAT_XML);
        }
        else if (type.equals("bin")) {
            value = new DmtData(data.getBytes());
        }
        else if (type.equals("b64")) {
            value = new DmtData(data.getBytes(), true);
        }
        else if (type.equals("date")) {
            value = new DmtData(data, DmtData.FORMAT_DATE);
        }
        else if (type.equals("time")) {
            value = new DmtData(data, DmtData.FORMAT_TIME);
        }
        else if (type.equals("float")) {
            value = new DmtData(Float.parseFloat(data));
        }
        else if (type.equals("null")) {
            value = DmtData.NULL_VALUE;
        }
        return value;
    }
}