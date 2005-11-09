package com.nokia.test.doit;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class TestDesktop extends Frame implements ActionListener, ItemListener {

    private List      li_tests = new List();
    private TextArea  ta_descr = new TextArea();
    private TextArea  ta_asserts = new TextArea();
    
    private Panel pa_left = new Panel();
    private Panel pa_right = new Panel();
    private Panel pa_right_top = new Panel();
    private Panel pa_right_bottom = new Panel();
    
    private static final String LOAD         = "Load";
    private static final String START        = "Start";
    private static final String DESELECT_ALL = "Deselect all";
    private static final String SELECT_ALL   = "Select all";
    private static final String COMMAND      = "Command";
    
    private Button b_load = new Button(LOAD);
    private Button b_start = new Button(START);
    private Button b_deselect_all = new Button(DESELECT_ALL);
    private Button b_select_all = new Button(SELECT_ALL);
    private Button b_command = new Button(COMMAND);
    
    private DoIt doIt;
    
    public TestDesktop(DoIt doit) {
        this.doIt = doit;
        
        setLayout(new GridLayout(1, 0));
        addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        	    setVisible(false);
        	    dispose();
        	}
        });
        
        pa_left.setLayout(new GridLayout(0, 1));
        pa_right.setLayout(new GridLayout(2, 0));
        pa_right_top.setLayout(new GridLayout(2, 0));
        pa_right_bottom.setLayout(new GridLayout(5, 0));
        add(pa_left, BorderLayout.EAST);
        add(pa_right, BorderLayout.WEST);
        pa_right.add(pa_right_top);
        pa_right.add(pa_right_bottom);
        pa_right_bottom.add(b_load);
        b_load.setActionCommand(LOAD);
        b_load.addActionListener(this);
        pa_right_bottom.add(b_start);
        b_start.setActionCommand(START);
        b_start.addActionListener(this);
        pa_right_bottom.add(b_select_all);
        b_select_all.setActionCommand(SELECT_ALL);
        b_select_all.addActionListener(this);
        pa_right_bottom.add(b_deselect_all);
        b_deselect_all.setActionCommand(DESELECT_ALL);
        b_deselect_all.addActionListener(this);
        pa_right_bottom.add(b_command);
        b_command.setActionCommand(DESELECT_ALL);
        b_command.addActionListener(this);
        li_tests.setMultipleSelections(true);
        li_tests.addItemListener(this);

        pa_left.add(li_tests);
        pa_right_top.add(ta_descr);
        pa_right_top.add(ta_asserts);
        
        pack();
        setSize(800, 600);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals(LOAD)) {
            refreshTests();
            return;
        }
        
        if (ae.getActionCommand().equals(START)) {
            int passed = 0;
            int failed = 0;
            int[] indexes = li_tests.getSelectedIndexes();
            for (int i = 0; i < indexes.length; i++) {
                try {
	                String testId = li_tests.getItem(indexes[i]);
                    doIt.runTest(testId);
	                li_tests.deselect(indexes[i]);
	                ++passed;
                }
                catch (Exception e) {
                    ++failed;
                    e.printStackTrace();
                }
                setTitle("Passed / all: " + passed + " / " + (passed + failed));
            }
        }
        
        if (ae.getSource() == b_deselect_all) {
            for (int i = 0; i < li_tests.getItemCount(); i++)
                li_tests.deselect(i);
        }
        
        if (ae.getSource() == b_select_all) {
            for (int i = 0; i < li_tests.getItemCount(); i++)
                li_tests.select(i);
        }
        
        if (ae.getSource() == b_command) {
            try {
                doIt.command();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshTests() {
        li_tests.removeAll();
        ArrayList tcs = new ArrayList(
        		Arrays.asList(doIt.getTestIds()));
        Collections.sort(tcs);
        int i = 0;
        for (Iterator iter = tcs.iterator(); iter.hasNext();) {
			String tc = (String) iter.next();
            li_tests.add(tc);
            li_tests.select(i);
            ++i;
        }
    }

    public void itemStateChanged(ItemEvent ie) {
        if (li_tests != ie.getSource())
            return;
        
        String testId = li_tests.getSelectedItem();
        if (null == testId)
            return;
        
        ta_descr.setText(doIt.getDescription(testId));
        StringBuffer sb = new StringBuffer("");
        String[] asserts = doIt.getAsserts(testId);
        if (null == asserts)
            return;
        for (int i = 0; i < asserts.length; i++) {
            sb.append(asserts[i] + "\n");
        }
        ta_asserts.setText(sb.toString());
    }

}