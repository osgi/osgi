/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.meg.demo.desktop;

import java.awt.event.*;
import javax.swing.*;

public class EventBind extends javax.swing.JDialog implements ActionListener {
	private JTextField	jTextFieldTopic;
	private JButton		jButtonCancel;
	private JButton		jButtonOK;
	private JTextField	jTextFieldProps;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		EventBind inst = new EventBind(frame);
		inst.setVisible(true);
	}

	public EventBind(JFrame frame) {
		super(frame);
		initGUI();
	}

	private void initGUI() {
		try {
			this.setTitle("Schedule an application on an event");
			this.setSize(388, 111);
			this.getContentPane().setLayout(null);
			this.setModal(true);
			{
				jTextFieldTopic = new JTextField();
				this.getContentPane().add(jTextFieldTopic);
				jTextFieldTopic.setText("Bundle Event");
				jTextFieldTopic.setBounds(4, 4, 370, 20);
				jTextFieldTopic.setEnabled(false);
			}
			{
				jTextFieldProps = new JTextField();
				this.getContentPane().add(jTextFieldProps);
				jTextFieldProps.setText("TYPE BUNDLE ID HERE");
				jTextFieldProps.setBounds(4, 30, 370, 20);
			}
			{
				jButtonOK = new JButton();
				this.getContentPane().add(jButtonOK);
				jButtonOK.setText("OK");
				jButtonOK.setBounds(5, 55, 51, 26);
				jButtonOK.addActionListener(this);
			}
			{
				jButtonCancel = new JButton();
				this.getContentPane().add(jButtonCancel);
				jButtonCancel.setText("Cancel");
				jButtonCancel.setBounds(60, 55, 73, 26);
				jButtonCancel.addActionListener(this);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getTopic() {
		return jTextFieldTopic.getText();
	}

	public String getProps() {
		return jTextFieldProps.getText();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jButtonOK) {
			setVisible(false);
		}
		else
			if (e.getSource() == jButtonCancel) {
				// TODO
				jTextFieldTopic.setText("");
				jTextFieldProps.setText("");
				setVisible(false);
			}
	}
}
