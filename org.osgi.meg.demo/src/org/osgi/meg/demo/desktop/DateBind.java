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
import java.text.*;
import java.util.Date;
import javax.swing.*;

public class DateBind extends javax.swing.JDialog implements ActionListener {
	private JButton		jButtonCancel;
	private JButton		jButtonOK;
	private JTextField	jTextFieldDate;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		DateBind inst = new DateBind(frame);
		inst.setVisible(true);
	}

	public DateBind(JFrame frame) {
		super(frame);
		initGUI();
	}

	private void initGUI() {
		try {
			this.setTitle("Schedule an application on a date");
			this.setSize(206, 86);
			this.getContentPane().setLayout(null);
			this.setModal(true);
			{
				jTextFieldDate = new JTextField();
				this.getContentPane().add(jTextFieldDate);
				DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
				jTextFieldDate.setText(df.format(new Date()));
				jTextFieldDate.setBounds(4, 4, 191, 20);
			}
			{
				jButtonOK = new JButton();
				this.getContentPane().add(jButtonOK);
				jButtonOK.setText("OK");
				jButtonOK.setBounds(5, 30, 51, 26);
				jButtonOK.addActionListener(this);
			}
			{
				jButtonCancel = new JButton();
				this.getContentPane().add(jButtonCancel);
				jButtonCancel.setText("Cancel");
				jButtonCancel.setBounds(60, 30, 73, 26);
				jButtonCancel.addActionListener(this);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Date getDate() {
		DateFormat d = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		try {
			return d.parse(jTextFieldDate.getText());
		}
		catch (ParseException e) {
			return null;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jButtonOK) {
			setVisible(false);
		}
		else
			if (e.getSource() == jButtonCancel) {
				// TODO
				jTextFieldDate.setText("");
				jTextFieldDate.setText("");
				setVisible(false);
			}
	}
}
