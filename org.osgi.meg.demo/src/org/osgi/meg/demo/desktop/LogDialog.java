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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Dialog to show recent logs.
 */
public class LogDialog extends javax.swing.JDialog implements ActionListener {
	private JButton				jButton1;
	// action commands
	private static final String	CLOSE	= "Close";
	private JTextArea			jTextArea1;
	private JScrollPane			jScrollPane1;

	public LogDialog(JFrame frame) {
		super(frame, true);
		initGUI();
	}

	private void initGUI() {
		try {
			GridBagLayout thisLayout = new GridBagLayout();
			this.setEnabled(false);
			this.getContentPane().setLayout(thisLayout);
			this.setSize(800, 400);
			this.setTitle("Log viewer");
			{
				jScrollPane1 = new JScrollPane();
				this.getContentPane().add(
						jScrollPane1,
						new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
								GridBagConstraints.CENTER,
								GridBagConstraints.BOTH,
								new Insets(0, 0, 0, 0), 0, 0));
				{
					jTextArea1 = new JTextArea();
					jScrollPane1.setViewportView(jTextArea1);
				}
			}
			{
				jButton1 = new JButton();
				this.getContentPane().add(
						jButton1,
						new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
								GridBagConstraints.CENTER,
								GridBagConstraints.NONE, new Insets(10, 0, 10,
										5), 0, 0));
				jButton1.setText("Close");
				jButton1.setActionCommand(CLOSE);
				jButton1.addActionListener(this);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		if (CLOSE == actionCommand) {
			this.hide();
		}
	}

	public void setLogs(String logs) {
		jTextArea1.setText(logs);
	}
}
