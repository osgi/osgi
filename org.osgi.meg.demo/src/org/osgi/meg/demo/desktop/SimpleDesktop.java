package org.osgi.meg.demo.desktop;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationContainer;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.application.ApplicationManager;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class SimpleDesktop extends Frame implements BundleActivator, ActionListener {
    
    private static final String INSTALL   = "install";
    private static final String UNINSTALL = "uninstall";
    private static final String LAUNCH    = "launch";
    private static final String STOP      = "stop";
    
    private Panel  pNorth;
    private Panel  pSouth;
    private Button bInstall;
    private Button bUninstall;
    private Button bLaunch;
    private Button bStop;
    private List   lInstApp;
    private List   lRunApp;

    private BundleContext context;
    private DmtSession session;
    private Hashtable instApp = new Hashtable();
    private Hashtable runApp = new Hashtable();
    private ServiceTracker trackAppDescr;
    private ServiceTracker trackAppHandle;
    private ServiceTracker trackAppManager;
    
    public SimpleDesktop() throws DmtException {
        super("Desktop (OSGi MEG RI)");
        setLayout(new BorderLayout());
        setSize(300, 200);
        
        // it doesn't exist on CDC (Erin 9500)
        //setExtendedState(MAXIMIZED_BOTH);
        
        pNorth = new Panel();
        add(pNorth, BorderLayout.NORTH);
        	bInstall = new Button(INSTALL);
        	bInstall.setActionCommand(INSTALL);
        	bInstall.addActionListener(this);
        	pNorth.add(bInstall);

        	bUninstall = new Button(UNINSTALL);
        	bUninstall.setActionCommand(UNINSTALL);
        	bUninstall.addActionListener(this);
        	pNorth.add(bUninstall);

        	bLaunch = new Button(LAUNCH);
        	bLaunch.setActionCommand(LAUNCH);
        	bLaunch.addActionListener(this);
        	pNorth.add(bLaunch);

        	bStop = new Button(STOP);
        	bStop.setActionCommand(STOP);
        	bStop.addActionListener(this);
        	pNorth.add(bStop);

        pSouth = new Panel();
        pSouth.setLayout(new GridLayout(1, 4));
        add(pSouth, BorderLayout.SOUTH);
        
        Panel pCenter = new Panel();
        pCenter.setLayout(new GridLayout(1,2));
        add(pCenter, BorderLayout.CENTER);
       		lInstApp = new List();
           	pCenter.add(lInstApp);

       		lRunApp = new List();
       		pCenter.add(lRunApp);
    }
    
    private void init() throws DmtException {
        initDmtAdmin();
        initTracker();

        setVisible(true);
        validate();
    }

    private void initTracker() {
		final BundleContext finalContext = context;
		
		trackAppDescr = new ServiceTracker(context, ApplicationDescriptor.class
				.getName(), new ServiceTrackerCustomizer() {
			public Object addingService(ServiceReference reference) {
				ApplicationDescriptor descr = (ApplicationDescriptor) finalContext
						.getService(reference);
				String uid = (String) reference.getProperty("unique_id");
				String appName = (String) reference
						.getProperty("localized_name");
				
				lInstApp.add(uid + "(" + appName + ")");
				instApp.put(uid + "(" + appName + ")", descr);
				
				validate();
				return descr;
			}

			public void modifiedService(ServiceReference reference,
					Object service) {
			}

			public void removedService(ServiceReference reference,
					Object service) {
				String uid = (String) reference.getProperty("unique_id");
				String appName = (String) reference
						.getProperty("localized_name");
			
				lInstApp.remove(uid + "(" + appName + ")");
				instApp.remove(uid + "(" + appName + ")");
				
				finalContext.ungetService(reference);
				validate();
			}
		});
		trackAppDescr.open();
		
		trackAppHandle = new ServiceTracker(context, ApplicationHandle.class
				.getName(), new ServiceTrackerCustomizer() {
			public Object addingService(ServiceReference reference) {
				ApplicationHandle handle = (ApplicationHandle) finalContext.
						getService(reference);
				lRunApp.add(handle.getAppDescriptor().getName());
				runApp.put(handle.getAppDescriptor().getName(), handle);

				lRunApp.validate();
				validate();
				return handle;
			}

			public void modifiedService(ServiceReference reference,
					Object service) {
			}

			public void removedService(ServiceReference reference,
					Object service) {
			    ApplicationHandle handle = (ApplicationHandle) finalContext.
						getService(reference);
			    lRunApp.remove(handle.getAppDescriptor().getName());
			    runApp.remove(handle.getAppDescriptor().getName());
				finalContext.ungetService(reference);
				
				lRunApp.validate();
				validate();
			}
		});
		trackAppHandle.open();

		trackAppManager = new ServiceTracker(context, ApplicationManager.class
				.getName(), new ServiceTrackerCustomizer() {
			public Object addingService(ServiceReference reference) {
			    ApplicationManager man = (ApplicationManager) finalContext.
						getService(reference);
				return man;
			}

			public void modifiedService(ServiceReference reference,
					Object service) {
			}

			public void removedService(ServiceReference reference,
					Object service) {
			}
		});
		trackAppManager.open();
	}

    private void initDmtAdmin() throws DmtException {
        ServiceReference ref = context.getServiceReference(DmtAdmin.class.getName());
        DmtAdmin factory = (DmtAdmin) context.getService(ref);
        session = factory.getSession(".", DmtSession.LOCK_TYPE_ATOMIC);
    }

    /////////////////////////////////////////////////////////////////
    // ActionListener

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
	        if (INSTALL.equals(command)) {
	            final Label l = new Label("URL:");
	            l.setAlignment(Label.RIGHT);
	            l.setBounds(0, 0, 20, 20);
	            pSouth.add(l, BorderLayout.SOUTH);
	            
	            final TextField tUrl = new TextField();
	            tUrl.setText("file:");
	            tUrl.setSize(200, 20);
	            pSouth.add(tUrl, BorderLayout.SOUTH);
	
	            final Button bOK = new Button("OK");
	            final Button bCancel = new Button("Cancel");
	            
	            ActionListener listener = new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                    if (bOK == e.getSource()) {
		    				if (null != l.getText()) {
		    					try {
	                                session.execute("./OSGi/deploy/install",
	                                        tUrl.getText());
	                            }
	                            catch (Exception ex) {
	                                // TODO
	                                ex.printStackTrace();
	                            }
		    				}
	                    }
	                    pSouth.removeAll();
	                }};
	
	            bOK.setBounds(230, 0, 100, 20);	            
	            pSouth.add(bOK, BorderLayout.SOUTH);
	            bOK.addActionListener(listener);
	            
	            bCancel.setBounds(335, 0, 100, 20);
	            pSouth.add(bCancel, BorderLayout.SOUTH);
	            bCancel.addActionListener(listener);
	            
	            validate();
	        } else if (UNINSTALL.equals(command)) {
	            String s = lInstApp.getSelectedItem();
	            ApplicationDescriptor descr = (ApplicationDescriptor) instApp.get(s);
	    		String type = descr.getContainerID();
	    		ServiceReference[] refs = context.getServiceReferences(
	    				ApplicationContainer.class.getName(), "(application_type=MEG)");
	    		ApplicationContainer cont = (ApplicationContainer) context.
	    				getService(refs[0]);
	    		cont.uninstallApplication(descr, false);
	    		context.ungetService(refs[0]);
	        } else if (LAUNCH.equals(command)) {
	            ApplicationManager man = (ApplicationManager) trackAppManager.getService();
	            String s = lInstApp.getSelectedItem();
	            ApplicationDescriptor descr = (ApplicationDescriptor) instApp.get(s);
	            man.launchApplication(descr, null);
	        } else if (STOP.equals(command)) {
	            String s = lRunApp.getSelectedItem();
	            ApplicationHandle handle = (ApplicationHandle) runApp.get(s);
	            handle.destroyApplication();
	        }
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////////////
    // BundleActivator
    
    public void start(BundleContext context) throws Exception {
        this.context = context;
        init();
    }

    public void stop(BundleContext context) throws Exception {
        // TODO Auto-generated method stub
        
    }

}
