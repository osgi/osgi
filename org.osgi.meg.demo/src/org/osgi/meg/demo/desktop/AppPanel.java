package org.osgi.meg.demo.desktop;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;
import java.util.Date;
import javax.swing.JToggleButton;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

/**
 * This code was generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * *************************************
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED
 * for this machine, so Jigloo or this code cannot be used legally
 * for any corporate or commercial purpose.
 * *************************************
 */
public class AppPanel extends javax.swing.JPanel {
	private JToggleButton	buttonApp;
	private JPanel			panelSouth;

	/*public static void main(String[] args) {
	 JFrame frame = new JFrame();
	 frame.getContentPane().add(new AppPanel(null, null));
	 frame.pack();
	 frame.show();
	 }*/
	public AppPanel(String name, ImageIcon icon) {
		super();
		initGUI();
		buttonApp.setText(name);
		buttonApp.setIcon(icon);
	}

	public JToggleButton getButtonApp() {
		return buttonApp;
	}

	private Date			scheduleDate;
	private JProgressBar	scheduleBar;

	public void addScheduleDate(Date date) {
		if (null != scheduleDate)
			return;
		scheduleDate = date;
		scheduleBar = new JProgressBar();
		long start = System.currentTimeMillis();
		long end = scheduleDate.getTime();
		long lv = end - start;
		if (end <= start)
			return;
		if (lv > Integer.MAX_VALUE)
			return;
		int l = (int) (lv / 1000);
		scheduleBar.setMaximum(0);
		scheduleBar.setMaximum((int) l);
		scheduleBar.setValue((int) l);
		panelSouth.add(scheduleBar);
		scheduleBar.setStringPainted(true);
		scheduleBar.setString("Scheduled date: " + scheduleDate.toString());
		validate();
	}

	void refreshSchedule() {
		if (null == scheduleDate)
			return;
		long start = System.currentTimeMillis();
		long end = scheduleDate.getTime();
		long lv = end - start;
		if (end <= start)
			return;
		if (lv > Integer.MAX_VALUE)
			return;
		int l = (int) (lv / 1000);
		scheduleBar.setValue((int) l);
		//scheduleBar.setString("Scheduled date: " + scheduleDate.toString());
		if (scheduleBar.getValue() <= 0) {
			panelSouth.remove(scheduleBar);
			scheduleDate = null;
			scheduleBar = null;
			validate();
		}
	}

	private void initGUI() {
		try {
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(398, 77));
			this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
					null, null, null, null));
			{
				buttonApp = new JToggleButton();
				this.add(buttonApp, BorderLayout.CENTER);
			}
			{
				panelSouth = new JPanel();
				BoxLayout panelSouthLayout = new BoxLayout(panelSouth,
						javax.swing.BoxLayout.Y_AXIS);
				panelSouth.setLayout(panelSouthLayout);
				this.add(panelSouth, BorderLayout.SOUTH);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}