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
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import org.osgi.service.application.*;

/**
 * The user interface of the OSGi MEG reference implementation. (View part of
 * the MVC pattern)
 */
public class Desktop extends javax.swing.JFrame implements ActionListener {
	// action commands
	private static final String	INSTALL			= "Install";
	private static final String	UNINSTALL		= "Uninstall";
	private static final String	START			= "Start";
	private static final String	STOP			= "Stop";
	private static final String	SUSPEND			= "Suspend";
	private static final String	RESUME			= "Resume";
	private static final String	LOGS			= "Log viewer";
	private static final String	SCHEDULEEVENT	= "Schedule Event";
	private static final String	SCHEDULEDATE	= "Schedule Date";
	// non swing elements
	private Activator			controller;
	private Model				model;
	private Hashtable			instApps;
	private Hashtable			runApps;
	// swing elements
	private JToolBar			jToolBarLeft;
	private JToolBar			jToolBarRight;
	private JPanel				jPanelLeftApps;
	private JPanel				jPanelRightApps;
	private JButton				jButtonInstall;
	private JButton				jButtonUninstall;
	private JButton				jButtonStart;
	private JButton				jButtonStop;
	private JButton				jButtonSuspend;
	private JButton				jButtonResume;
	private JButton				jButtonLogs;
	private JButton				jButtonScheduleEvent;
	private JButton				jButtonScheduleDate;
	private ButtonGroup			instAppGroup;
	private JLabel				jLabel2;
	private JLabel				jLabel1;
	private ButtonGroup			runAppGroup;
	// icons for the buttons representing installed and running applications
	private ImageIcon			iconSuspended;						// suspended applications
	private ImageIcon			iconRunning;						// running applications
	private ImageIcon			iconNoIcon;						// installed applications have no icon

	public Desktop(Activator controller) {
		super();
		this.controller = controller;
		instApps = new Hashtable();
		runApps = new Hashtable();
		instAppGroup = new ButtonGroup();
		runAppGroup = new ButtonGroup();
		this.setTitle("Desktop (OSGi MEG RI Phase-1)");
		initGUI();
	}

	void setModel(Model model) {
		this.model = model;
	}

	void initGUI() {
		try {
			iconSuspended = new ImageIcon(getClass().getResource(
					"suspended.gif"));
			iconRunning = new ImageIcon(getClass().getResource("running.gif"));
			iconNoIcon = new ImageIcon(getClass().getResource("noicon.gif"));
			GridBagLayout flayout = new GridBagLayout();
			this.setSize(800, 500);
			this.getContentPane().setLayout(flayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			{
				jToolBarLeft = new JToolBar();
				this.getContentPane().add(
						jToolBarLeft,
						new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
								GridBagConstraints.WEST,
								GridBagConstraints.BOTH,
								new Insets(0, 0, 0, 0), 0, 0));
				BoxLayout jToolBarLeftLayout = new BoxLayout(jToolBarLeft,
						javax.swing.BoxLayout.X_AXIS);
				jToolBarLeft.setBorder(BorderFactory.createBevelBorder(
						BevelBorder.LOWERED, null, null, null, null));
				jToolBarLeft.setLayout(jToolBarLeftLayout);
				{
					jButtonInstall = new JButton();
					jToolBarLeft.add(jButtonInstall);
					jButtonInstall.setText(INSTALL);
					jButtonInstall.addActionListener(this);
					jButtonInstall.setActionCommand(INSTALL);
				}
				{
					jButtonUninstall = new JButton();
					jToolBarLeft.add(jButtonUninstall);
					jButtonUninstall.setText(UNINSTALL);
					jButtonUninstall.addActionListener(this);
					jButtonUninstall.setActionCommand(UNINSTALL);
				}
				{
					jButtonStart = new JButton();
					jToolBarLeft.add(jButtonStart);
					jButtonStart.setText(START);
					jButtonStart.addActionListener(this);
					jButtonStart.setActionCommand(START);
				}
				{
					jButtonLogs = new JButton();
					jToolBarLeft.add(jButtonLogs);
					jButtonLogs.setText(LOGS);
					jButtonLogs.addActionListener(this);
					jButtonLogs.setActionCommand(LOGS);
				}
				{
					jButtonScheduleEvent = new JButton();
					jToolBarLeft.add(jButtonScheduleEvent);
					jButtonScheduleEvent.setText(SCHEDULEEVENT);
					jButtonScheduleEvent.addActionListener(this);
					jButtonScheduleEvent.setActionCommand(SCHEDULEEVENT);
				}
				{
					jButtonScheduleDate = new JButton();
					jToolBarLeft.add(jButtonScheduleDate);
					jButtonScheduleDate.setText(SCHEDULEDATE);
					jButtonScheduleDate.addActionListener(this);
					jButtonScheduleDate.setActionCommand(SCHEDULEDATE);
				}
				jToolBarRight = new JToolBar();
				this.getContentPane().add(
						jToolBarRight,
						new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
								GridBagConstraints.WEST,
								GridBagConstraints.BOTH,
								new Insets(0, 0, 0, 0), 0, 0));
				BoxLayout jToolBarRightLayout = new BoxLayout(jToolBarRight,
						javax.swing.BoxLayout.X_AXIS);
				jToolBarRight.setBorder(BorderFactory.createBevelBorder(
						BevelBorder.LOWERED, null, null, null, null));
				jToolBarRight.setLayout(jToolBarRightLayout);
				{
					jButtonStop = new JButton();
					jToolBarRight.add(jButtonStop);
					jButtonStop.setText(STOP);
					jButtonStop.addActionListener(this);
					jButtonStop.setActionCommand(STOP);
				}
				{
					jButtonSuspend = new JButton();
					jToolBarRight.add(jButtonSuspend);
					jButtonSuspend.setText(SUSPEND);
					jButtonSuspend.addActionListener(this);
					jButtonSuspend.setActionCommand(SUSPEND);
				}
				{
					jButtonResume = new JButton();
					jToolBarRight.add(jButtonResume);
					jButtonResume.setText(RESUME);
					jButtonResume.addActionListener(this);
					jButtonResume.setActionCommand(RESUME);
				}
				jPanelLeftApps = new JPanel();
				GridLayout jPanelLeftAppsLayout = new GridLayout(8, 1);
				jPanelLeftAppsLayout.setRows(10);
				this.getContentPane().add(
						jPanelLeftApps,
						new GridBagConstraints(0, 1, 1, 1, 0.5, 1.0,
								GridBagConstraints.CENTER,
								GridBagConstraints.BOTH,
								new Insets(0, 0, 0, 0), 0, 0));
				jPanelLeftApps.setLayout(jPanelLeftAppsLayout);
				jPanelLeftApps.setBorder(new SoftBevelBorder(
						BevelBorder.LOWERED, null, null, null, null));
				{
					jLabel1 = new JLabel();
					jPanelLeftApps.add(jLabel1);
					jLabel1.setText("Installed applications");
					jLabel1.setForeground(new java.awt.Color(255, 255, 255));
					jLabel1.setBackground(new java.awt.Color(64, 128, 128));
					jLabel1.setOpaque(true);
				}
				jPanelRightApps = new JPanel();
				this.getContentPane().add(
						jPanelRightApps,
						new GridBagConstraints(1, 1, 1, 1, 0.5, 1.0,
								GridBagConstraints.CENTER,
								GridBagConstraints.BOTH,
								new Insets(0, 0, 0, 0), 0, 0));
				GridLayout jPanelRightAppsLayout = new GridLayout(10, 1);
				jPanelRightAppsLayout.setRows(10);
				jPanelRightApps.setLayout(jPanelRightAppsLayout);
				jPanelRightApps.setBorder(new SoftBevelBorder(
						BevelBorder.LOWERED, null, null, null, null));
				{
					jLabel2 = new JLabel();
					jPanelRightApps.add(jLabel2);
					jLabel2.setText("Launched applications");
					jLabel2.setForeground(new java.awt.Color(255, 255, 255));
					jLabel2.setBackground(new java.awt.Color(64, 128, 128));
					jLabel2.setOpaque(true);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO eliminate this in the final release
	public void refreshRunningApps(Hashtable table) {
		for (Enumeration en = runApps.keys(); en.hasMoreElements();) {
			ApplicationHandle handle = (ApplicationHandle) en.nextElement();
			JToggleButton button = (JToggleButton) runApps.get(handle);
			Integer state = (Integer) table.get(handle);
			if (null == state)
				continue;
			if (ApplicationHandle.RUNNING == state.intValue())
				button.setIcon(iconRunning);
			else
				if (ApplicationHandle.SUSPENDED == state.intValue())
					button.setIcon(iconSuspended);
				else
					button.setIcon(null);
			jPanelRightApps.validate();
			jPanelRightApps.repaint();
		}
	}

	public void addInstApp(ApplicationDescriptor descr, String appName,
			byte[] imageData) {
		//JToggleButton button = new JToggleButton(appName + " (" + 
		//    descr.getContainerID() + ")");
		ImageIcon icon = null;
		if (null == imageData)
			icon = iconNoIcon;
		else {
			icon = new ImageIcon(imageData);
		}
		//button.setIcon(icon);
		//String toolTipText = "category: " + descr.getCategory() + " \n" +
		//                     "container id: " + descr.getContainerID() + " \n" +
		//                     "unique id: " + descr.getUniqueID() + " \n" +
		//                     "version: " + descr.getVersion();
		//button.setToolTipText(toolTipText);
		//instAppGroup.add(button);
		//jPanelLeftApps.add(button);
		/*----------------------------------------------------*/
		AppPanel appPanel = new AppPanel(appName, icon);
		JToggleButton button = appPanel.getButtonApp();
		instAppGroup.add(button);
		jPanelLeftApps.add(appPanel);
		instApps.put(descr, appPanel);
		/*----------------------------------------------------*/
		jPanelLeftApps.validate();
		//instApps.put(descr, button);
	}

	void refreshInstApps() {
		for (Enumeration en = instApps.keys(); en.hasMoreElements();) {
			ApplicationDescriptor key = (ApplicationDescriptor) en
					.nextElement();
			AppPanel appPanel = (AppPanel) instApps.get(key);
			appPanel.refreshSchedule();
		}
	}

	public void removeInstApp(ApplicationDescriptor descr) {
		AppPanel appPanel = (AppPanel) instApps.get(descr);
		jPanelLeftApps.remove(appPanel);
		jPanelLeftApps.validate();
		jPanelLeftApps.repaint();
		instApps.remove(descr);
	}

	public void addLaunchApp(ApplicationHandle handle) {
		JToggleButton jButton = new JToggleButton(handle.getAppDescriptor()
				.getName());
		jButton.addActionListener(this);
		jButton.setIcon(iconRunning);
		jPanelRightApps.add(jButton);
		jButton.setSize(10, 10);
		runAppGroup.add(jButton);
		jPanelRightApps.validate();
		jPanelLeftApps.repaint();
		runApps.put(handle, jButton);
	}

	public void removeRunApp(ApplicationHandle handle) {
		JToggleButton button = (JToggleButton) runApps.get(handle);
		jPanelRightApps.remove(button);
		jPanelRightApps.validate();
		jPanelRightApps.repaint();
		runApps.remove(handle);
	}

	private ApplicationDescriptor getAppDescr() {
		for (Enumeration en = instApps.keys(); en.hasMoreElements();) {
			ApplicationDescriptor key = (ApplicationDescriptor) en
					.nextElement();
			AppPanel appPanel = (AppPanel) instApps.get(key);
			if (appPanel.getButtonApp().isSelected())
				return key;
		}
		return null;
	}

	private ApplicationHandle getAppHandle() {
		for (Enumeration en = runApps.keys(); en.hasMoreElements();) {
			ApplicationHandle key = (ApplicationHandle) en.nextElement();
			JToggleButton button = (JToggleButton) runApps.get(key);
			if (button.isSelected())
				return key;
		}
		return null;
	}

	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		try {
			// install new application
			if (INSTALL.equals(actionCommand)) {
				String url = JOptionPane.showInputDialog(this, "URL");
				if (null != url) {
					controller.installApp(url);
				}
				return;
			}
			// uninstall application
			if (UNINSTALL.equals(actionCommand)) {
				ApplicationDescriptor descr = getAppDescr();
				if (null != descr)
					controller.uninstallApp(descr);
			}
			// start new application
			if (START.equals(actionCommand)) {
				ApplicationDescriptor descr = getAppDescr();
				if (null != descr)
					controller.launchApp(descr);
			}
			// stop an application
			if (STOP.equals(actionCommand)) {
				ApplicationHandle handle = getAppHandle();
				if (null != handle)
					controller.stopApp(handle);
			}
			// suspend an application
			if (SUSPEND.equals(actionCommand)) {
				ApplicationHandle handle = getAppHandle();
				if (null != handle)
					controller.suspendApp(handle);
			}
			// resume an application
			if (RESUME.equals(actionCommand)) {
				ApplicationHandle handle = getAppHandle();
				if (null != handle)
					controller.resumeApp(handle);
			}
			// view logs
			if (LOGS.equals(actionCommand)) {
				LogDialog ld = new LogDialog(this);
				ld.setLogs(model.getLogs());
				ld.setVisible(true);
				return;
			}
			// schedule app on an event
			if (SCHEDULEEVENT.equals(actionCommand)) {
				ApplicationDescriptor descr = getAppDescr();
				if (null == descr)
					return;
				EventBind ebd = new EventBind(this);
				ebd.setVisible(true);
				if ("".equals(ebd.getTopic()))
					return;
				controller.scheduleOnEvent(ebd.getTopic(), ebd.getProps(),
						descr);
				return;
			}
			// schedule app on an date
			if (SCHEDULEDATE.equals(actionCommand)) {
				ApplicationDescriptor descr = getAppDescr();
				if (null == descr)
					return;
				DateBind dbd = new DateBind(this);
				dbd.setVisible(true);
				if (null == dbd.getDate())
					return;
				controller.scheduleOnDate(dbd.getDate(), descr);
				((AppPanel) instApps.get(descr)).addScheduleDate(dbd.getDate());
				return;
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}