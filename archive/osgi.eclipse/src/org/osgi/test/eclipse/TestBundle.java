package org.osgi.test.eclipse;

import java.util.Iterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.*;

public class TestBundle implements IObjectActionDelegate {
	String[]		locations;

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		try {
			if ( locations== null && locations.length>0)
				TestView.error("No Bundles selected",null);
			else {
				IWorkbenchPage page = getPage();
				IViewPart part = page.showView("org.osgi.test.eclipse.TestView");
				 part = page.findView("org.osgi.test.eclipse.TestView");
				TestView view = (TestView) part;
				if ( "org.osgi.test.eclipse.sendBundle".equals(action.getId()))
					view.install(locations);
				else
					view.test(locations);
			}
		}
		catch( Exception e ) {
			TestView.error("Could not start Test View", e );
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		locations = getLocations(selection);
	}


	String[] getLocations(ISelection selection) {
		if (selection != null && (selection instanceof StructuredSelection)) {
			StructuredSelection ss = (StructuredSelection) selection;
			String[] result = new String[ss.size()];
			int n = 0;
			for (Iterator i = ss.iterator(); i.hasNext();) {
				IFile file = (IFile) i.next();
				result[n++] = file.getLocation().toOSString();
			}
			return result;
		}
		return null;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	IWorkbenchPage getPage() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		return window.getActivePage();
	}


}
