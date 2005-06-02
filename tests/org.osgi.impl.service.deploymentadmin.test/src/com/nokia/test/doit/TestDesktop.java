package com.nokia.test.doit;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Hashtable;


public class TestDesktop extends Frame implements ActionListener {

    private List      li_tests = new List();
    private TextArea  ta_descr = new TextArea();
    private Method[]  tests;
    private Hashtable fields = new Hashtable();
    
    private Panel pa_left = new Panel();
    private Panel pa_right = new Panel();
    private Panel pa_right_top = new Panel();
    private Panel pa_right_bottom = new Panel();
    
    private static final String START        = "Start";
    private static final String DESELECT_ALL = "Deselect all";
    
    private Button b_start = new Button(START);
    private Button b_deselect_all = new Button(DESELECT_ALL);
    
    private DoIt doit;
    
    public TestDesktop(DoIt doit) throws Exception {
        this.doit = doit;
        
        setLayout(new GridLayout(1, 0));
        
        pa_left.setLayout(new GridLayout(0, 1));
        pa_right.setLayout(new GridLayout(2, 0));
        pa_right_top.setLayout(new GridLayout(0, 1));
        pa_right_bottom.setLayout(new GridLayout(5, 1));
        add(pa_left, BorderLayout.EAST);
        add(pa_right, BorderLayout.WEST);
        pa_right.add(pa_right_top);
        pa_right.add(pa_right_bottom);
        pa_right_bottom.add(b_start);
        b_start.setActionCommand(START);
        b_start.addActionListener(this);
        pa_right_bottom.add(b_deselect_all);
        b_deselect_all.setActionCommand(DESELECT_ALL);
        b_deselect_all.addActionListener(this);

        li_tests.setMultipleSelections(true);
        li_tests.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                String text = "";
                String sel = li_tests.getSelectedItem();
                if (null != sel) {
	                Field f = (Field) fields.get(sel);
	                if (null != f) {
		                try {
		                    text = (String) f.get(null);
		                }
		                catch (Exception ex) {
		                    ex.printStackTrace();
		                }
	                }
                }
                ta_descr.setText(null == text ? "" : text);
            }});
        Class c = Class.forName("com.nokia.test.doit.DoIt");
        tests = c.getDeclaredMethods();
        for (int i = 0; i < tests.length; i++) {
            if (tests[i].getName().startsWith("db_test") || 
                tests[i].getName().startsWith("bad_db_test"))
                	li_tests.add(tests[i].getName());
        }
        for (int i = 0; i < li_tests.getItemCount(); i++)
            li_tests.select(i);
        
        Field[] fs = c.getDeclaredFields();
        for (int i = 0; i < fs.length; i++)
            fields.put(fs[i].getName(), fs[i]);
        
        pa_left.add(li_tests);
        pa_right_top.add(ta_descr);
        
        pack();
        setSize(800, 600);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        
	        if (ae.getSource() == b_start) {
	            int passed = 0;
                int failed = 0;
	            int[] indexes = li_tests.getSelectedIndexes();
	            for (int i = 0; i < indexes.length; i++) {
	                try {
		                String mn = li_tests.getItem(indexes[i]);
		                Method m = DoIt.class.getDeclaredMethod(mn, null);
		                m.invoke(doit, null);
		                li_tests.deselect(indexes[i]);
		                ++passed;
	                }
	                catch (Exception e) {
                        ++failed;
                    }
	                setTitle("Passed / failed: " + passed + " / " + (passed + failed));
	            }
	        }
	        
	        if (ae.getSource() == b_deselect_all) {
	            for (int i = 0; i < li_tests.getItemCount(); i++)
	                li_tests.deselect(i);
	        }
    }

}