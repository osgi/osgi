package org.osgi.test.applet;

import java.util.StringTokenizer;
import netscape.application.*;

public class TabListItem extends ListItem {
	int	tabs[];

	public void setTabs(int tabs[]) {
		this.tabs = tabs;
	}

	public int[] tabs() {
		return tabs;
	}

	protected void drawStringInRect(Graphics g, String title, Font titleFont,
			Rect textBounds, int justification) {
		if (listView().isEnabled() && isEnabled()) {
			g.setColor(textColor());
		}
		else {
			g.setColor(Color.gray);
		}
		g.setFont(titleFont);
		StringTokenizer st = new StringTokenizer(title(), "\t");
		int index = 0;
		int offset = 0;
		int width;
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (tabs == null || index >= tabs.length) {
				width = titleFont.fontMetrics().stringWidth(s);
			}
			else {
				width = tabs[index];
			}
			g.drawStringInRect(s, textBounds.x + offset, textBounds.y,
					width - 2, textBounds.height, justification);
			offset += width;
			index++;
		}
	}
}
