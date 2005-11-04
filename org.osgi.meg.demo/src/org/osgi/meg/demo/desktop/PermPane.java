package org.osgi.meg.demo.desktop;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class PermPane extends Panel implements ItemListener {

    private Activator controller;

    private List     lCondPerms = new List();
    private TextArea taInfo = new TextArea();
    private Button   bClose = new Button(SimpleDesktop.CLOSE_PERMS);
    private Button   bReload = new Button(SimpleDesktop.RELOAD_PERMS);

    public PermPane(SimpleDesktop desktop, Activator controller) {
        this.controller = controller;
    
        setLayout(new BorderLayout());

        Panel pCenter = new Panel();
        pCenter.setLayout(new GridLayout(2, 0));
        add(pCenter, BorderLayout.CENTER);
            lCondPerms.addItemListener(this);
            pCenter.add(lCondPerms, BorderLayout.CENTER);
            
            pCenter.add(taInfo, BorderLayout.SOUTH);
        
        Panel pEast = new Panel();
        pEast.setLayout(new GridLayout(2, 0));
        add(pEast, BorderLayout.EAST);
            bReload.setActionCommand(SimpleDesktop.RELOAD_PERMS);
            bReload.addActionListener(desktop);
            pEast.add(bReload, BorderLayout.EAST);
            bClose.setActionCommand(SimpleDesktop.CLOSE_PERMS);
            bClose.addActionListener(desktop);
            pEast.add(bClose, BorderLayout.EAST);
    }
    
    void refresh() {
        lCondPerms.removeAll();
        taInfo.setText("");
        String[] cps = controller.getCondPerms();
        for (int i = 0; i < cps.length; i++) {
            lCondPerms.add(cps[i]);
        }
    }
    
    String getPermName() {
        return lCondPerms.getSelectedItem();
    }

    public void itemStateChanged(ItemEvent ie) {
        taInfo.setText("");
        
        String cpiName = lCondPerms.getSelectedItem();
        Object[] info = controller.getInfo(cpiName);
        if (null == info)
            return;
        
        taInfo.append("INFO FOR: " + cpiName + "\n");
        
        String[] conds = (String[]) info[0]; 
        String[] perms = (String[]) info[1];
        
        taInfo.append("CONDITIONS:\n");
        for (int i = 0; i < conds.length; i++) {
            taInfo.append(conds[i] + "\n");
        }
        taInfo.append("PERMISSIONS:\n");
        for (int i = 0; i < perms.length; i++) {
            taInfo.append(perms[i] + "\n");
        }
    }
    
}
