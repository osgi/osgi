package org.osgi.meg.demo.desktop;

import java.awt.BorderLayout;
import java.awt.Button;
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


public class SimpleDesktop extends Frame implements ActionListener {
    
    private static final String INSTALL   = "install";
    private static final String UNINSTALL = "uninstall";
    private static final String LAUNCH    = "launch";
    private static final String STOP      = "stop";
    
    private Panel  pSouth = new Panel();
    private Button bInstall;
    private Button bUninstall;
    private Button bLaunch;
    private Button bStop;
    private List   lInstPackages;
    private List   lInstApp;
    private List   lRunningApp;

    private Activator     controller;
    //private DmtSession session;
    //private Hashtable instApp = new Hashtable();
    //private Hashtable runApp = new Hashtable();
    //private ServiceTracker trackAppDescr;
    //private ServiceTracker trackAppHandle;
    //private ServiceTracker trackAppManager;
    
    public SimpleDesktop(Activator controller) {
        super("Desktop (OSGi MEG RI)");
        this.controller = controller;
        initTracker();

        setLayout(new BorderLayout());
        setSize(300, 200);
        
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
        pNorth1.setLayout(new GridLayout(3, 0));
            bInstall = new Button(INSTALL);
            bInstall.setActionCommand(INSTALL);
            bInstall.addActionListener(this);
            pNorth1.add(bInstall);
    
            bUninstall = new Button(UNINSTALL);
            bUninstall.setActionCommand(UNINSTALL);
            bUninstall.addActionListener(this);
            pNorth1.add(bUninstall);
            
            pNorth1.add(new Label("Packages"));

        Panel pNorth2 = new Panel();
        pNorth.add(pNorth2);
        pNorth2.setLayout(new GridLayout(3, 0));
            bLaunch = new Button(LAUNCH);
            bLaunch.setActionCommand(LAUNCH);
            bLaunch.addActionListener(this);
            pNorth2.add(bLaunch);
            
            pNorth2.add(new Panel());
            
            pNorth2.add(new Label("Applications"));

        Panel pNorth3 = new Panel();
        pNorth.add(pNorth3);
        pNorth3.setLayout(new GridLayout(3, 0));
            bStop = new Button(STOP);
            bStop.setActionCommand(STOP);
            bStop.addActionListener(this);
            pNorth3.add(bStop);
            
            pNorth3.add(new Panel());
            
            pNorth3.add(new Label("Application instances"));

        pSouth.setLayout(new GridLayout(0, 4));
        add(pSouth, BorderLayout.SOUTH);

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

    private void initTracker() {
		//final BundleContext finalContext = context;
		
//		trackAppDescr = new ServiceTracker(context, ApplicationDescriptor.class
//				.getName(), new ServiceTrackerCustomizer() {
//			public Object addingService(ServiceReference reference) {
//				ApplicationDescriptor descr = (ApplicationDescriptor) finalContext
//						.getService(reference);
//				String uid = (String) reference.getProperty("unique_id");
//				String appName = (String) reference
//						.getProperty("localized_name");
//				
//				lInstApp.add(uid + "(" + appName + ")");
//				instApp.put(uid + "(" + appName + ")", descr);
//				
//				validate();
//				return descr;
//			}
//
//			public void modifiedService(ServiceReference reference,
//					Object service) {
//			}
//
//			public void removedService(ServiceReference reference,
//					Object service) {
//				String uid = (String) reference.getProperty("unique_id");
//				String appName = (String) reference
//						.getProperty("localized_name");
//			
//				lInstApp.remove(uid + "(" + appName + ")");
//				instApp.remove(uid + "(" + appName + ")");
//				
//				finalContext.ungetService(reference);
//				validate();
//			}
//		});
//		trackAppDescr.open();
		
//		trackAppHandle = new ServiceTracker(context, ApplicationHandle.class
//				.getName(), new ServiceTrackerCustomizer() {
//			public Object addingService(ServiceReference reference) {
//				ApplicationHandle handle = (ApplicationHandle) finalContext.
//						getService(reference);
//				lRunApp.add(handle.getAppDescriptor().getName());
//				runApp.put(handle.getAppDescriptor().getName(), handle);
//
//				lRunApp.validate();
//				validate();
//				return handle;
//			}
//
//			public void modifiedService(ServiceReference reference,
//					Object service) {
//			}
//
//			public void removedService(ServiceReference reference,
//					Object service) {
//			    ApplicationHandle handle = (ApplicationHandle) finalContext.
//						getService(reference);
//			    lRunApp.remove(handle.getAppDescriptor().getName());
//			    runApp.remove(handle.getAppDescriptor().getName());
//				finalContext.ungetService(reference);
//				
//				lRunApp.validate();
//				validate();
//			}
//		});
//		trackAppHandle.open();

//		trackAppManager = new ServiceTracker(context, ApplicationManager.class
//				.getName(), new ServiceTrackerCustomizer() {
//			public Object addingService(ServiceReference reference) {
//			    ApplicationManager man = (ApplicationManager) finalContext.
//						getService(reference);
//				return man;
//			}
//
//			public void modifiedService(ServiceReference reference,
//					Object service) {
//			}
//
//			public void removedService(ServiceReference reference,
//					Object service) {
//			}
//		});
//		trackAppManager.open();
	}

//    private void initDmtAdmin() throws DmtException {
//        ServiceReference ref = context.getServiceReference(DmtAdmin.class.getName());
//        DmtAdmin factory = (DmtAdmin) context.getService(ref);
//        session = factory.getSession(".", DmtSession.LOCK_TYPE_ATOMIC);
//    }

    /////////////////////////////////////////////////////////////////
    // ActionListener

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
	        if (INSTALL.equals(command)) {
	            if (0 != pSouth.getComponentCount())
	                return;
	            
	            final Label l = new Label("URL:");
	            l.setAlignment(Label.RIGHT);
	            l.setBounds(0, 0, 20, 20);
	            pSouth.add(l, BorderLayout.SOUTH);
	            
	            final TextField tUrl = new TextField();
	            tUrl.setSize(200, 20);
	            pSouth.add(tUrl, BorderLayout.SOUTH);
	
	            final Button bOK = new Button("OK");
	            final Button bCancel = new Button("Cancel");
	            
	            ActionListener listener = new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                    if (bOK == e.getSource()) {
                            String url = tUrl.getText();
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
	                    }
	                    pSouth.removeAll();
	                    validate();
	                }};
	
	            bOK.setBounds(230, 0, 100, 20);	            
	            pSouth.add(bOK, BorderLayout.SOUTH);
	            bOK.addActionListener(listener);
	            
	            bCancel.setBounds(335, 0, 100, 20);
	            pSouth.add(bCancel, BorderLayout.SOUTH);
	            bCancel.addActionListener(listener);
	            
	            validate();
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

}
