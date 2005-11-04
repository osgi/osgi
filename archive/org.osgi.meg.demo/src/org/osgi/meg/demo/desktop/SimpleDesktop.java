package org.osgi.meg.demo.desktop;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Enumeration;


public class SimpleDesktop extends Frame implements ActionListener {
    
    private static final String INSTALL_URL   = "Install url";
    private static final String INSTALL_LOCAL = "Install local";
    private static final String UNINSTALL     = "Uninstall";
    private static final String LAUNCH        = "Launch";
    private static final String STOP          = "Stop";
    private static final String PERMS         = "Permissions";
    
    // in GetPane class
    static final String OK          = "OK";
    static final String CANCEL      = "Cancel";
    
    // in PermPane class
    public static final String CLOSE_PERMS  = "Close";
	public static final String RELOAD_PERMS = "Reload";
    
    private GetPane    pSouthGet = new GetPane(this);
    private StatusPane pSouthStatus = new StatusPane();
    private PermPane   pSouthPerms;
    private Button     bInstallURL;
    private Button     bInstallLocal;
    private Button     bUninstall;
    private Button     bLaunch;
    private Button     bStop;
    private Button     bPerm;
    private List       lInstPackages;
    private List       lInstApp;
    private List       lRunningApp;

    private Activator     controller;
    private Panel         actPane = pSouthStatus;
    
    public SimpleDesktop(Activator controller) {
        super("Desktop (OSGi MEG RI)");
        this.controller = controller;

        setLayout(new BorderLayout());
        setSize(400, 500);
        pSouthPerms = new PermPane(this, controller);
        
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
        
        Panel pWest = new Panel();
        pWest.setLayout(new GridLayout(3, 0));
        add(pWest, BorderLayout.WEST);
            bInstallURL = new Button(INSTALL_URL);
            bInstallURL.setActionCommand(INSTALL_URL);
            bInstallURL.addActionListener(this);
            pWest.add(bInstallURL);

            bInstallLocal = new Button(INSTALL_LOCAL);
            bInstallLocal.setActionCommand(INSTALL_LOCAL);
            bInstallLocal.addActionListener(this);
            pWest.add(bInstallLocal);

            bPerm = new Button(PERMS);
            bPerm.setActionCommand(PERMS);
            bPerm.addActionListener(this);
            pWest.add(bPerm);
            
        Panel pCenter = new Panel(); 
        pCenter.setLayout(new BorderLayout());
        add(pCenter, BorderLayout.CENTER);
        
        Panel pCenterCenter = new Panel(); 
        pCenterCenter.setLayout(new GridLayout(0, 3));
        pCenter.add(pCenterCenter, BorderLayout.CENTER);
       		lInstPackages = new List();
            pCenterCenter.add(lInstPackages);

       		lInstApp = new List();
            pCenterCenter.add(lInstApp);
            
            lRunningApp = new List();
            pCenterCenter.add(lRunningApp);

        Panel pCenterNorth = new Panel(); 
        pCenterNorth.setLayout(new GridLayout(0, 3));
        pCenter.add(pCenterNorth, BorderLayout.NORTH);
            bUninstall = new Button(UNINSTALL);
            bUninstall.setActionCommand(UNINSTALL);
            bUninstall.addActionListener(this);
            pCenterNorth.add(bUninstall);
        
            bLaunch = new Button(LAUNCH);
            bLaunch.setActionCommand(LAUNCH);
            bLaunch.addActionListener(this);
            pCenterNorth.add(bLaunch);
            
            bStop = new Button(STOP);
            bStop.setActionCommand(STOP);
            bStop.addActionListener(this);
            pCenterNorth.add(bStop);
            
            pCenterNorth.add(new Label("Packages"));
            pCenterNorth.add(new Label("Applications"));
            pCenterNorth.add(new Label("Application instances"));

        setActPane(pSouthStatus);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            if (CLOSE_PERMS.equals(command)) {
                setActPane(pSouthStatus);
            } else if (RELOAD_PERMS.equals(command)) {
            	controller.reloadPolicy();
            	pSouthPerms.refresh();
            } else if (PERMS.equals(command)) {
                pSouthPerms.refresh();
                setActPane(pSouthPerms);
            } else if (OK.equals(command)) {
                String url = pSouthGet.getText();
                if (null != url) {
                    try {
                        if (url.endsWith(".dp")) {
                            controller.installDp(url);
                        } else if (url.endsWith(".jar")) {
                            controller.installBundle(url);
                        }
                    } catch (Exception ex) {
                        // TODO
                        ex.printStackTrace();
                    }
                }
                setActPane(pSouthStatus);
            } else if (CANCEL.equals(command)) {
                setActPane(pSouthStatus);
            } else if (INSTALL_URL.equals(command)) {
                setActPane(pSouthGet);
            } else if (INSTALL_LOCAL.equals(command)) {
                FileDialog fd = new FileDialog(new Frame(),  "", FileDialog.LOAD);
                fd.setVisible(true);
                if (fd.getFile() != null) {
                  File f = new File(fd.getDirectory(), fd.getFile());
                  if (f.getName().endsWith(".dp")) {
                      controller.installDp(f);
                  } else if (f.getName().endsWith(".jar")) {
                      controller.installBundle(f);
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

    private void setActPane(Panel panel) {
        if (panel == pSouthPerms)
            pSouthPerms.refresh();
        
        remove(actPane);
        add(panel, BorderLayout.SOUTH);
        actPane = panel;
        validate();
    }

    public void onAppInstalled(String pid) {
        lInstApp.add(pid);
    }

    public void onDpInstalled(String symbName) {
        lInstPackages.add(symbName);
    }

    public void onBundleInstalled(String location) {
        lInstPackages.add(location);
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

class ImportKeyStoreDialog extends Dialog implements ActionListener {
    
    static final int OK     = 0;
    static final int CANCEL = 1;

    private int state = CANCEL;
    
    private TextField tType = new TextField("JKS");
    private TextField tPwd = new TextField();
    private Button bOK = new Button("OK");
    private Button bCancel = new Button("Cancel");

    public ImportKeyStoreDialog(Frame owner) {
        super(owner);
        
        setLayout(new GridLayout(4, 0));
        
        add(tType);
        
        add(tPwd);
        
        bOK.addActionListener(this);
        add(bOK);
        
        bCancel.addActionListener(this);
        add(bCancel);
        
        pack();
        setModal(true);
        setVisible(true);
    }
    
    String getType() {
        return tType.getText();
    }

    String getPwd() {
        return tPwd.getText();
    }
    
    int getState() {
        return state;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bOK) {
            state = OK;
            dispose();
        }
        if (e.getSource() == bCancel) {
            state = CANCEL;
            dispose();
        }
    }


}