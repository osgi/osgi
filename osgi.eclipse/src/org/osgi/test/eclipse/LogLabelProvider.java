package org.osgi.test.eclipse;

import org.eclipse.jface.viewers.*;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class LogLabelProvider extends LabelProvider implements ITableLabelProvider {
	public String getColumnText(Object element, int columnIndex) {
		TestResult testResult = (TestResult) element;
		switch (columnIndex) {
			case 0 :
				if ( testResult.map.containsKey("begin"))
					return testResult.map.get("begin").toString();
				return "";
			case 1 :
				return testResult.map.get("_").toString();
		}
		return null;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}
}
