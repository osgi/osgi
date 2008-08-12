package org.osgi.meg.demo.desktop;

import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

public class GetPane extends Panel {

    private Label     label = new Label("URL:");
    private TextField text = new TextField();
    private Button    bOK = new Button("OK");
    private Button    bCancel = new Button("Cancel");
    
    public GetPane(SimpleDesktop desktop) {
        setLayout(new GridLayout(0, 4));

        bOK.addActionListener(desktop);
        bOK.setActionCommand(SimpleDesktop.OK);

        bCancel.addActionListener(desktop);
        bCancel.setActionCommand(SimpleDesktop.CANCEL);

        add(label);
        add(text);
        add(bOK);
        add(bCancel);
    }

    public String getText() {
        return text.getText();
    }
    
}
