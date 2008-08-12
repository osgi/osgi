package org.osgi.meg.demo.desktop;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StatusPane extends Panel implements ActionListener {

	private TextArea text;
	private Button clear;

	public StatusPane() {
		this.text = new TextArea("", 10, 80, TextArea.SCROLLBARS_BOTH);
		this.clear = new Button("Clear log");
		
		this.setLayout(new BorderLayout());
		this.add(text,BorderLayout.CENTER);
		this.add(clear, BorderLayout.SOUTH);
		this.clear.addActionListener(this);
	}

	public void onEvent(String s) {
		this.text.append(s + "\n");
	}

	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == this.clear ) {
			this.text.setText("");
		}
	}
    
}
