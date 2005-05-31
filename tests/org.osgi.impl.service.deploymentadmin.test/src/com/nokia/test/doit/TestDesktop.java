package com.nokia.test.doit;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;


public class TestDesktop extends Frame {

    private List      li_tests = new List();
    private TextArea  ta_descr = new TextArea();
    private Method[]  tests;
    private Hashtable fields = new Hashtable();
    
    private Panel pa_left  = new Panel();
    private Panel pa_right = new Panel();
    
    public TestDesktop() throws Exception {
        setLayout(new GridLayout(0, 2));
        pa_left.setLayout(new GridLayout(0, 1));
        pa_right.setLayout(new GridLayout(0, 1));
        add(pa_left);
        add(pa_right);

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
            if (tests[i].getName().startsWith("db_test"))
                li_tests.add(tests[i].getName());
        }
        
        Field[] fs = c.getDeclaredFields();
        for (int i = 0; i < fs.length; i++)
            fields.put(fs[i].getName(), fs[i]);
        
        pa_left.add(li_tests);
        pa_right.add(ta_descr);
        
        pack();
        setSize(200, 200);
        setVisible(true);
    }

}
