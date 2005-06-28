package org.osgi.test.eclipse;

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.director.Handler;
import org.osgi.test.script.Tag;
import org.osgi.test.service.*;
import org.osgi.test.shared.*;

public class TestView extends ViewPart implements IStructuredContentProvider,
		IApplet, IRun {
	Table				table;
	TableViewer			viewer;
	boolean				disposed;
	Handler				handler;
	PackageAdmin		packageAdmin;
	RemoteService		current;
	Thread				testthread;
	TestCase			testcase	= null;
	Tag					result		= null;
	IProgressMonitor	progress;
	int					lastWork	= 0;
	Label				status;
	Action				propertiesAction;
	Action				targetsAction;

	public TestView() {
		packageAdmin = (PackageAdmin) getService(PackageAdmin.class);
		handler = Activator.handler;
		try {
			handler.setApplet(this);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void makeActions() {
		propertiesAction = new Action() {
			public void run() {
				FileDialog dialog = new FileDialog(viewer.getControl().getShell(), SWT.SINGLE);
				String tp = System.getProperty(IRun.TEST_PROPERTIES_FILE);
				if (tp != null)
					dialog.setFilterPath(tp);
				String result = dialog.open();
				if (result == null)
					return;
				String file = dialog.getFileName();
				tp = dialog.getFilterPath() + File.separator + file;
				System.setProperty(IRun.TEST_PROPERTIES_FILE, tp);
				Properties properties = new Properties();
				try {
					properties.load( new FileInputStream(tp));
					showMessage("Properties " + properties);					
				}
				catch (FileNotFoundException e) {
					showMessage("Can't find file: " + tp );					
				}
				catch (IOException e) {
					showMessage("Not a properties file: " + tp );					
				}
			}
		};
		targetsAction = new Action() {
			public void run() {
				try {
					StringBuffer sb = new StringBuffer();
					String del = "";
					ServiceReference refs[] = Activator.context
							.getServiceReferences(
									RemoteService.class.getName(), null);
					for (int i = 0; refs != null && i < refs.length; i++) {
						sb.append(del);
						sb.append(refs[i].getProperty("host"));
						sb.append(":");
						sb.append(refs[i].getProperty("port"));
						del = "\n";
					}
					showMessage(sb.toString());
				}
				catch (InvalidSyntaxException e) {
					// cannot happen, filter is null
				}
			}
		};
		propertiesAction.setText("Test Properties");
		propertiesAction.setToolTipText("Select Properties file for target");

		targetsAction.setText("Show Targets");
		targetsAction.setToolTipText("List currently registered targetst");

		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(propertiesAction);
		bars.getMenuManager().add(targetsAction);
	}

	void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"OSGi Test View", message);
	}

	public void dispose() {
		disposed = true;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		table = new Table(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column = new TableColumn(table, SWT.NONE, 0);
		column.setText("Method");
		column.setWidth(100);
		column.setAlignment(SWT.LEFT);
		column = new TableColumn(table, SWT.NONE, 1);
		column.setText("Log");
		column.setWidth(1000);
		column.setAlignment(SWT.LEFT);
		viewer = new TableViewer(table);
		viewer.setLabelProvider(new LogLabelProvider());
		viewer.setContentProvider(this);
		viewer.setInput(new ArrayList());
		makeActions();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void setMessage(String message) {
	}

	public void setError(String message) {
		TestResult tr = new TestResult(message);
		add(tr);
	}

	public void setProgress(int percentage) {
		IProgressMonitor progress = this.progress;
		if (progress != null) {
			progress.worked(percentage - lastWork);
			lastWork = percentage;
		}
	}

	public void setResult(TestCase tc, int errors) {
		// add(new TestResult("r:" + tc.getName() + " Errors: " + errors));
	}

	public void setTargetProperties(Dictionary properties) {
		// TODO Auto-generated method stub
	}

	public void finished() {
	}

	public void bundlesChanged() {
	}

	public void targetsChanged() {
	}

	public void casesChanged() {
		// TODO Auto-generated method stub
	}

	public void alive(String msg) {
		// add(new TestResult("a:" + msg));
	}

	public void step(String msg) {
		TestResult tr = new TestResult(msg);
		add(tr);
	}

	void add(final TestResult result) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				viewer.add(result);
			}
		});
	}

	/**
	 * 
	 */
	public void clear() {
		viewer.setInput(new ArrayList());
		viewer.refresh();
	}

	public void test(String locations[]) {
		try {
			if (handler.getRun() != null) {
				handler.stopRun();
				return;
			}
			current = getTarget();
			if (current == null)
				return;
			if (locations == null || locations.length == 0)
				return;
			final int options = IHandler.OPTION_FOREVER
					| IHandler.OPTION_LOGGING;
			Collection existing = new Vector(handler.getTestCases().values());
			final Set bundles = new HashSet();
			for (int i = 0; i < locations.length; i++) {
				Bundle b = activate(locations[i]);
				if (b != null)
					bundles.add(b);
			}
			packageAdmin.refreshPackages(null);
			final Collection running = new Vector(handler.getTestCases()
					.values());
			running.removeAll(existing);
			if (running.size() == 0)
				error("No New Test cases installed", null);
			clear();
			Job testthread = new Job("Test Job") {
				public IStatus run(IProgressMonitor monitor) {
					progress = monitor;
					monitor.beginTask("Test run", 100);
					try {
						handler.startRun(current.getHost(), current.getPort(),
								options);
						for (Iterator i = running.iterator(); i.hasNext();) {
							if (monitor.isCanceled())
								return Status.CANCEL_STATUS;
							testcase = (TestCase) i.next();
							result = handler.getRun().doTestCase(testcase);
							if (monitor.isCanceled())
								return Status.CANCEL_STATUS;
							if (i.hasNext())
								Thread.sleep(3000);
						}
					}
					catch (InterruptedException ee) {
						error("interrupted", ee);
					}
					catch (Exception e) {
						error("Exception in testcase " + testcase.getName(), e);
					}
					handler.stopRun();
					for (Iterator i = bundles.iterator(); i.hasNext();) {
						try {
							Bundle b = (Bundle) i.next();
							b.uninstall();
						}
						catch (BundleException e) {
							// Ignore ...
						}
					}
					packageAdmin.refreshPackages(null);
					return Status.OK_STATUS;
				}
			};
			testthread.setUser(true);
			testthread.schedule();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	RemoteService getTarget() {
		final Vector targets = handler.getTargets();
		if (targets.isEmpty()) {
			error("No targets were found", null);
			return null;
		}
		current = (RemoteService) targets.elementAt(0);
		if (targets.size() > 1)
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					String[] names = new String[targets.size() + 1];
					for (int i = 0; i < targets.size(); i++) {
						RemoteService rs = (RemoteService) targets.elementAt(i);
						names[i] = rs.getHost();
					}
					names[targets.size()] = "Cancel";
					Shell shell = new Shell();
					MessageDialog md = new MessageDialog(shell,
							"Choose a target", null,
							"There are multiple targets, pick one",
							MessageDialog.INFORMATION, names, 0);
					int n = md.open();
					if (n < targets.size())
						current = (RemoteService) targets.elementAt(n);
					else
						current = null;
				}
			});
		return current;
	}

	Object getService(Class c) {
		ServiceReference ref = Activator.getContext().getServiceReference(
				c.getName());
		if (ref == null)
			return null;
		return Activator.getContext().getService(ref);
	}

	static void error(final String msg, final Throwable throwable) {
		if (throwable != null)
			Activator.log.log(new Status(Status.ERROR, "osgi.eclipse",
					Status.OK, msg, throwable));
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (throwable != null) {
					MessageDialog.openError(new Shell(),
							"OSGi Test Harness Error", msg + ": "
									+ throwable.getMessage());
					if (throwable != null)
						throwable.printStackTrace();
				}
				else {
					MessageDialog.openWarning(new Shell(),
							"OSGi Test Harness Warning", msg);
				}
			}
		});
	}

	Bundle activate(String location) {
		try {
			Bundle b = Activator.getContext().installBundle("file:" + location);
			if (b.getState() != Bundle.ACTIVE)
				b.start();
			else
				b.update();
			return b;
		}
		catch (BundleException e) {
			Throwable nested = e.getNestedException();
			if (nested != null)
				error("Bundle install failed for " + location, nested);
			else
				error("Bundle install failed for " + location, e);
		}
		return null;
	}

	/**
	 * @param inputElement
	 * @return
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return ((Collection) inputElement).toArray();
	}

	void install(final String locations[]) throws UnknownHostException,
			IOException, Exception {
		final TestView parent = this;
		Job job = new Job("Test Job") {
			public IStatus run(IProgressMonitor monitor) {
				try {
					progress = monitor;
					monitor.beginTask("Test run", 100);
					RemoteService remote = getTarget();
					if (remote != null) {
						TargetLink target = new TargetLink(parent);
						target.open(new Socket(remote.getHost(), remote
								.getPort()));
						for (int i = 0; i < locations.length; i++) {
							URL url = new URL("file:" + locations[i]);
							InputStream in = url.openStream();
							if (in != null)
								try {
									target
											.install(url.getFile() + "~keep~",
													in);
								}
								finally {
									in.close();
								}
							else
								error("No such file " + locations[i], null);
						}
						target.close();
					}
					return Status.OK_STATUS;
				}
				catch (Throwable t) {
					error("Could not install", t);
					return Status.CANCEL_STATUS;
				}
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	public void linkClosed() throws Exception {
		// TODO Auto-generated method stub
	}

	public void push(String bundle, Object msg) {
		// TODO Auto-generated method stub
	}

	public void sendLog(String bundle, Log log) throws IOException {
		// TODO Auto-generated method stub
	}

	public void stopped(String bundle) {
		// TODO Auto-generated method stub
	}
}