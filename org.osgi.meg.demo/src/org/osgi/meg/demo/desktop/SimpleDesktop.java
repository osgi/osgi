package org.osgi.meg.demo.desktop;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;


public class SimpleDesktop extends Frame implements ActionListener {
    
    private static final String INSTALL_URL   = "install_url";
    private static final String INSTALL_LOCAL = "install_local";
    private static final String UNINSTALL     = "uninstall";
    private static final String LAUNCH        = "launch";
    private static final String STOP          = "stop";
    
    // in GetPane class
    static final String OK          = "OK";
    static final String CANCEL      = "Cancel";
    
    //private Panel      pSouth = new Panel();
    private GetPane    pSouthGet = new GetPane(this);
    private StatusPane pSouthStatus = new StatusPane();
    private Button     bInstallURL;
    private Button     bInstallLocal;
    private Button     bUninstall;
    private Button     bLaunch;
    private Button     bStop;
    private List       lInstPackages;
    private List       lInstApp;
    private List       lRunningApp;

    private Activator     controller;
    
    public SimpleDesktop(Activator controller) {
        super("Desktop (OSGi MEG RI)");
        this.controller = controller;

        setLayout(new BorderLayout());
        setSize(400, 500);
        
        // it doesn't exist on CDC (Erin 9500)
        //setExtendedState(MAXIMIZED_BOTH);
        
        addWindowListener(new WindowAdapter() {
            	public void windowClosing(WindowEvent e) {
            	    setVisible(false);
            	    dispose();
            	}
            });
        
        Panel pNorth = new Panel();
        pNorth.setLayout(new GridLayout(0, 3));
        add(pNorth, BorderLayout.NORTH);
        
        Panel pNorth1 = new Panel();
        pNorth.add(pNorth1);
        pNorth1.setLayout(new GridLayout(4, 0));
            bInstallURL = new Button(INSTALL_URL);
            bInstallURL.setActionCommand(INSTALL_URL);
            bInstallURL.addActionListener(this);
            pNorth1.add(bInstallURL);

            bInstallLocal = new Button(INSTALL_LOCAL);
            bInstallLocal.setActionCommand(INSTALL_LOCAL);
            bInstallLocal.addActionListener(this);
            pNorth1.add(bInstallLocal);

            bUninstall = new Button(UNINSTALL);
            bUninstall.setActionCommand(UNINSTALL);
            bUninstall.addActionListener(this);
            pNorth1.add(bUninstall);
            
            pNorth1.add(new Label("Packages"));

        Panel pNorth2 = new Panel();
        pNorth.add(pNorth2);
        pNorth2.setLayout(new GridLayout(4, 0));
            pNorth2.add(new Panel());
            
            pNorth2.add(new Panel());
        
            bLaunch = new Button(LAUNCH);
            bLaunch.setActionCommand(LAUNCH);
            bLaunch.addActionListener(this);
            pNorth2.add(bLaunch);
            
            pNorth2.add(new Label("Applications"));

        Panel pNorth3 = new Panel();
        pNorth.add(pNorth3);
        pNorth3.setLayout(new GridLayout(4, 0));
            pNorth3.add(new Panel());
            
            pNorth3.add(new Panel());
        
            bStop = new Button(STOP);
            bStop.setActionCommand(STOP);
            bStop.addActionListener(this);
            pNorth3.add(bStop);
            
            pNorth3.add(new Label("Application instances"));

        add(pSouthStatus, BorderLayout.SOUTH);

        Panel pCenter = new Panel(); 
        pCenter.setLayout(new GridLayout(0, 3));
        
        add(pCenter, BorderLayout.CENTER);
       		lInstPackages = new List();
           	pCenter.add(lInstPackages);

       		lInstApp = new List();
       		pCenter.add(lInstApp);
            
            lRunningApp = new List();
            pCenter.add(lRunningApp);
            
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            if (OK.equals(command)) {
                String url = pSouthGet.getText();
                if (null != url) {
                    try {
                        if (url.endsWith(".dp")) {
                            String symbName = 
                                controller.installDp(url);
                            lInstPackages.add(symbName);
                        } else if (url.endsWith(".jar")) {
                            String location = 
                                controller.installBundle(url);
                            lInstPackages.add(location);
                        }
                    } catch (Exception ex) {
                        // TODO
                        ex.printStackTrace();
                    }
                }
                remove(pSouthGet);
                add(pSouthStatus, BorderLayout.SOUTH);
                validate();
            } else if (CANCEL.equals(command)) {
                remove(pSouthGet);
                add(pSouthStatus, BorderLayout.SOUTH);
                validate();
            } else if (INSTALL_URL.equals(command)) {
                remove(pSouthStatus);
                add(pSouthGet, BorderLayout.SOUTH);
                validate();
            } else if (INSTALL_LOCAL.equals(command)) {
                FileDialog fd = new FileDialog(new Frame(),  "", FileDialog.LOAD);
                fd.setVisible(true);
                if (fd.getFile() != null) {
                  File f = new File(fd.getDirectory(), fd.getFile());
                  if (f.getName().endsWith(".dp")) {
                      String symbName = 
                          controller.installDp(f);
                      lInstPackages.add(symbName);
                  } else if (f.getName().endsWith(".jar")) {
                      String location = 
                          controller.installBundle(f);
                      lInstPackages.add(location);
                  }
                }
	        } else if (UNINSTALL.equals(command)) {
	            String s = lInstPackages.getSelectedItem();
	            if (null == s)
                    return;
                boolean existsDp = controller.existsDp(s);
                if (existsDp)
                    controller.uninstallDp(s);
                else
                    controller.uninstallBundle(s);
                lInstPackages.remove(s);
	        } else if (LAUNCH.equals(command)) {
                String pid = lInstApp.getSelectedItem();
                if (null == pid)
                    return;
                controller.launchApp(pid);
	        } else if (STOP.equals(command)) {
                String pid = lRunningApp.getSelectedItem();
                if (null == pid)
                    return;
                controller.stopApp(pid);
	        }
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }
    }

    public void onAppInstalled(String pid) {
        lInstApp.add(pid);
    }

    public void onAppUninstalled(String pid) {
        lInstApp.remove(pid);
    }

    public void onAppLaunched(String pid) {
        lRunningApp.add(pid);
    }

    public void onAppStopped(String pid) {
        lRunningApp.remove(pid);
    }

    public void onEvent(String string) {
        pSouthStatus.onEvent(string);
    }

}
