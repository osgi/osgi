package com.nokia.test.plugin;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

import org.apache.xpath.operations.Bool;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtSession;


public class TestDesktop extends Frame implements ActionListener {
    
    private String ROOT = "./OSGi/Deployment/Inventory/Deployed";
    private String TEST_FILE = "../../org.osgi.impl.service.deploymentadmin.test/" +
    		"DmtPluginTest.txt";

    private Panel pa_left = new Panel();
    private Panel pa_right = new Panel();
    private Panel pa_right_top = new Panel();
    private Panel pa_right_bottom = new Panel();
    
    private static final String IS_NODE_URI          = "isNodeUri";
    private static final String IS_LEAF_NODE         = "isLeafNode";
    private static final String GET_CHILD_NODE_NAMES = "getChildNodeNames";
    private static final String GET_NODE_VALUE       = "getNodeValue";
    private static final String MAKE_TEST            = "MAKE TEST";
    
    private TextField tf_uri = new TextField(); 
    private TextField tf_result = new TextField();
    
    private Button b_isNodeUri = new Button(IS_NODE_URI);
    private Button b_isLeafNode = new Button(IS_LEAF_NODE);
    private Button b_getChildNodeNames = new Button(GET_CHILD_NODE_NAMES);
    private Button b_getNodeValue = new Button(GET_NODE_VALUE);
    private Button b_makeTest = new Button(MAKE_TEST);
    
    private Checkbox cb_negativeTest = new Checkbox("Negative test");
    
    private DmtAdmin admin;
    private DmtSession session;
    private String lastCommand;
    
    public TestDesktop(DmtAdmin admin) throws Exception {
        this.admin = admin;
        this.session = admin.getSession(ROOT);
        setLayout(new GridLayout(1, 0));
        addWindowListener(new WindowAdapter() {
            	public void windowClosing(WindowEvent e) {
            	    setVisible(false);
            	    dispose();
            	}
            });
        
        pa_left.setLayout(new GridLayout(10, 0));
        pa_right.setLayout(new GridLayout(2, 0));
        pa_right_top.setLayout(new GridLayout(5, 1));
        pa_right_bottom.setLayout(new GridLayout(5, 1));
        add(pa_left);
        add(pa_right);
        pa_right.add(pa_right_top);
        pa_right.add(pa_right_bottom);
        
        tf_uri.setText(ROOT);
        pa_right_top.add(tf_uri);
        
        pa_right_bottom.add(tf_result);
        
        b_isNodeUri.addActionListener(this);
        pa_left.add(b_isNodeUri);

        b_isLeafNode.addActionListener(this);
        pa_left.add(b_isLeafNode);

        b_getChildNodeNames.addActionListener(this);
        pa_left.add(b_getChildNodeNames);

        b_getNodeValue.addActionListener(this);
        pa_left.add(b_getNodeValue);
        
        b_makeTest.addActionListener(this);
        pa_left.add(b_makeTest);
        
        pa_left.add(cb_negativeTest);
        
        pack();
        setSize(800, 600);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        String acc = ae.getActionCommand();
        String uri = tf_uri.getText();
        
        try {
            if (IS_NODE_URI.equals(acc)) {
                tf_result.setText(Boolean.toString(session.isNodeUri(uri)));
            } else if (IS_LEAF_NODE.equals(acc)) {
                tf_result.setText(Boolean.toString(session.isLeafNode(uri)));
            } else if (GET_CHILD_NODE_NAMES.equals(acc)) {
                String[] sa = session.getChildNodeNames(uri);
                tf_result.setText(Arrays.asList(sa).toString());
            } else if (GET_NODE_VALUE.equals(acc)) {
                tf_result.setText(session.getNodeValue(uri).toString());
            } else if (MAKE_TEST.equals(acc)) {
                File f = new File(TEST_FILE);
                if (!f.exists())
                    f.createNewFile();
                PrintWriter pw = new PrintWriter(new FileWriter(f, true));
                String neg = "" + cb_negativeTest.getState();
                pw.println(uri + " " + lastCommand + " " + tf_result.getText() + 
                        " " + neg);
                pw.close(); // TODO finally etc.
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        lastCommand = acc;
    }

}