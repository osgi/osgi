/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.meg.demo.remote.gui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.osgi.meg.demo.remote.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ServerTestGUI extends javax.swing.JFrame implements ActionListener, TreeSelectionListener {
    
    private static final int SOCKET_TIMEOUT = 5000;
    
    private Commander        commander;
    private TreeNodeImpl     rootNode;
    private boolean          opened = true;
    
    private JTabbedPane      jTabbedPane;
    private JTree            jTree;
    private DefaultTreeModel treeModel;
    private JPanel           jPanelTree;
    private JPanel           jPanelAlert;
    private JPanel           jPanelCommandLog;
    private JScrollPane      jScrollPaneAlert;
    private JButton          jButtonOpen;
    private JButton          jButtonClose;
    private JButton          jButtonSetACL;
    private JButton          jButtonRefresh;
    private JButton          jButtonExec;
    private JButton          jButtonCreateInter;
    private JButton          jButtonCreateLeaf;
    private JButton          jButtonDelete;
    private JButton          jButtonSetValue;
    private JToolBar         jToolBar;
    private JLabel           jLabelProps;
    private JTextArea        jTextAreaAlert;
    private JTextArea        jTextAreaCommandLog;
	
    // action commands
    private String OPEN            = "Open";
    private String CLOSE           = "Close";
    private String SETACL          = "Set ACL";
    private String REFRESH         = "Refresh";
    private String EXECUTE         = "Execute";
    private String CREATEINTERIOR  = "Create Interior";
    private String CREATELEAF      = "Create Leaf";
    private String DELETE          = "Delete";
    private String SETVALUE        = "Set Value";

	public static void main(String[] args) {
        int port = 7777;
        if (args.length > 0)
        	port = Integer.parseInt(args[0]);
        String root = ".";
        if (args.length > 1)
            root = args[1];
		ServerTestGUI gui = new ServerTestGUI(port, root);
		gui.setVisible(true);
	}
	
    public ServerTestGUI(int port, String root) {
        super();

        RMServer rms = new RMServer(port, SOCKET_TIMEOUT);
        commander = new Commander(rms, this);
        rms.setReceiver(commander);
        rootNode = new TreeNodeImpl(root, null, commander);
        try {
            commander.command("open " + rootNode.uri() + " server");
		} catch (CommanderException e) {
			e.printStackTrace();
            destroy();
            rms.stop();
            System.exit(-1);
		}
        initGUI();
        refreshToolBar();
    }
    
    private void destroy() {
        commander.destroy();
    }
	
	private void initGUI() {
		try {
			setSize(1000, 800);
            setTitle("Remote DMT manager");
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent e) {
                        destroy();
                    }
                });
			{
                jTabbedPane = new JTabbedPane(); 
                this.getContentPane().add(jTabbedPane, BorderLayout.CENTER);
                
                jPanelTree = new JPanel();
                BorderLayout jPanel1Layout = new BorderLayout();
                jPanelTree.setLayout(jPanel1Layout);
                jTabbedPane.addTab("Tree", jPanelTree);
                {
                    jToolBar = new JToolBar();
                    this.getContentPane().add(jToolBar, BorderLayout.NORTH);
                    {
                        jButtonOpen = new JButton();
                        jToolBar.add(jButtonOpen);
                        jButtonOpen.setText(OPEN);
                        jButtonOpen.setActionCommand(OPEN);
                        jButtonOpen.addActionListener(this);
                    }
                    {
                        jButtonClose = new JButton();
                        jToolBar.add(jButtonClose);
                        jButtonClose.setText(CLOSE);
                        jButtonClose.setActionCommand(CLOSE);
                        jButtonClose.addActionListener(this);
                    }
                    {
                        jButtonSetACL = new JButton();
                        jToolBar.add(jButtonSetACL);
                        jButtonSetACL.setText(SETACL);
                        jButtonSetACL.setActionCommand(SETACL);
                        jButtonSetACL.addActionListener(this);
                    }
                    {
                        jButtonRefresh = new JButton();
                        jToolBar.add(jButtonRefresh);
                        jButtonRefresh.setText(REFRESH);
                        jButtonRefresh.setActionCommand(REFRESH);
                        jButtonRefresh.addActionListener(this);
                    }
                    {
                        jButtonExec = new JButton();
                        jToolBar.add(jButtonExec);
                        jButtonExec.setText(EXECUTE);
                        jButtonExec.setActionCommand(EXECUTE);
                        jButtonExec.addActionListener(this);
                    }
                    {
                        jButtonCreateInter = new JButton();
                        jToolBar.add(jButtonCreateInter);
                        jButtonCreateInter.setText(CREATEINTERIOR);
                        jButtonCreateInter.setActionCommand(CREATEINTERIOR);
                        jButtonCreateInter.addActionListener(this);
                    }
                    {
                        jButtonCreateLeaf = new JButton();
                        jToolBar.add(jButtonCreateLeaf);
                        jButtonCreateLeaf.setText(CREATELEAF);
                        jButtonCreateLeaf.setActionCommand(CREATELEAF);
                        jButtonCreateLeaf.addActionListener(this);
                    }
                    {
                        jButtonDelete = new JButton();
                        jToolBar.add(jButtonDelete);
                        jButtonDelete.setText(DELETE);
                        jButtonDelete.setActionCommand(DELETE);
                        jButtonDelete.addActionListener(this);
                    }
                    {
                        jButtonSetValue = new JButton();
                        jToolBar.add(jButtonSetValue);
                        jButtonSetValue.setText(SETVALUE);
                        jButtonSetValue.setActionCommand(SETVALUE);
                        jButtonSetValue.addActionListener(this);
                    }
                }
                {
                    treeModel = new DefaultTreeModel(rootNode);
                    jTree = new JTree(treeModel);
                    jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                    jTree.setShowsRootHandles(true);
                    jTree.addTreeSelectionListener(this);
                    
                    JScrollPane jScrollPaneTree = new JScrollPane(jTree);
                    this.jPanelTree.add(jScrollPaneTree, BorderLayout.CENTER);
                }
                {
                	jLabelProps = new JLabel("<html><b><font color=\"#bb000f\">Properties:</b></html>");
                    this.jPanelTree.add(jLabelProps, BorderLayout.SOUTH);
                }
                
                jPanelAlert = new JPanel();
                jPanelAlert.setLayout(new BorderLayout());
                jTabbedPane.addTab("Alert", jPanelAlert);
                {
                	jTextAreaAlert = new JTextArea();
                    jTextAreaAlert.setText("");
                    jTextAreaAlert.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(jTextAreaAlert);
                    this.jPanelAlert.add(scrollPane);
                }
                
                jPanelCommandLog = new JPanel();
                jPanelCommandLog.setLayout(new BorderLayout());
                jTabbedPane.addTab("Command log", jPanelCommandLog);
                {
                    jTextAreaCommandLog = new JTextArea();
                    jTextAreaCommandLog.setText("");
                    jTextAreaCommandLog.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(jTextAreaCommandLog);                     
                    this.jPanelCommandLog.add(scrollPane, BorderLayout.CENTER);
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
        if (OPEN.equals(e.getActionCommand())) {
            // TODO close
            command("open " + rootNode.uri() + " server");
            treeModel.reload();
            jTree.setVisible(true);
            opened = true;
            refreshToolBar();
            return;
        }
        
        if (CLOSE.equals(e.getActionCommand())) {
            jTree.setVisible(false);
            command("close");
            opened = false;
            refreshToolBar();
            return;
        }
        
        if (SETACL.equals(e.getActionCommand())) {
            TreeNodeImpl tn = (TreeNodeImpl) jTree.getLastSelectedPathComponent();
            if (null == tn)
                return;
            String acl = JOptionPane.showInputDialog(this, "ACL: ", "Add=*&Exec=*&Get=*&Delete=*&Replace=*");
            if (null == acl)
                return;
            command("setNodeAcl " + tn.uri() + " " + acl);
            setPropsText(tn);
            return;
        }
        
        if (REFRESH.equals(e.getActionCommand())) {
            TreeNodeImpl tn = (TreeNodeImpl) jTree.getLastSelectedPathComponent();
            if (null == tn)
                return;
            treeModel.reload(tn);
            return;
        }
        
        if (EXECUTE.equals(e.getActionCommand())) {
            TreeNodeImpl tn = (TreeNodeImpl) jTree.getLastSelectedPathComponent();
            if (null == tn)
                return;
            String param = JOptionPane.showInputDialog(this, "Exec parameter string:", "\"\"");
            if (null == param)
                return;
            if (0 == param.trim().length())
                return;
            command("x " + tn.uri() + " " + param);
            //treeModel.reload();
            return;
        }
        
        if (CREATEINTERIOR.equals(e.getActionCommand())) {
            TreeNodeImpl tn = (TreeNodeImpl) jTree.getLastSelectedPathComponent();
            if (null == tn)
                return;
            String name = JOptionPane.showInputDialog(this, "Node name:");
            if (null == name)
                return;
            command("ci " + tn.uri() + "/" + name);
            treeModel.reload(tn);
            return;
        }
        
        if (CREATELEAF.equals(e.getActionCommand())) {
            TreeNodeImpl tn = (TreeNodeImpl) jTree.getLastSelectedPathComponent();
            if (null == tn)
                return;
            String name = JOptionPane.showInputDialog(this, "Node name:");
            if (null == name)
                return;
            String value = JOptionPane.showInputDialog(this, "Node value " +
                "(type can be 'int', 'chr', 'boolean'):", "type:data_as_string");
            if (null == value)
                return;
            command("cl " + tn.uri() + "/" + name + " " + value);
            treeModel.reload(tn);
            return;
        }
        
        if (DELETE.equals(e.getActionCommand())) {
            TreeNodeImpl tn = (TreeNodeImpl) jTree.getLastSelectedPathComponent();
            TreeNodeImpl par = (TreeNodeImpl) tn.getParent();
            command("deleteNode " + tn.uri());
            if (null == par)
                treeModel.reload();
            else
            	treeModel.reload(par);
            return;
        }

        if (SETVALUE.equals(e.getActionCommand())) {
            TreeNodeImpl tn = (TreeNodeImpl) jTree.getLastSelectedPathComponent();
            if (null == tn)
                return;
            String value = JOptionPane.showInputDialog(this, "Node value " +
                "(type can be 'int', 'chr', 'boolean'):", "type:data_as_string");
            if (null == value)
                return;
            command("setNodeValue " + tn.uri() + " " + value);
            setPropsText(tn);
            return;
        }
	}

	public void valueChanged(TreeSelectionEvent e) {
        TreePath tp = e.getNewLeadSelectionPath();
        if (null == tp)
            return;
        TreeNodeImpl tn = (TreeNodeImpl) tp.getLastPathComponent();
        setPropsText(tn);
        refreshToolBar();
	}

	private void setPropsText(TreeNodeImpl tn) {
        StringBuffer sb = new StringBuffer(); 
        String nodeValue;
        String nodeAcl;
        String metaNode; 
		try {
			nodeValue = tn.getNodeValue();
		} catch (CommanderException e) {
            nodeValue = "Error: " + e.getCode();
		}
        try {
        	nodeAcl = tn.getNodeAcl();
        } catch (CommanderException e) {
            nodeAcl = "Error: " + e.getCode();
        }
        try {
        	metaNode = tn.getMetaNode();
        } catch (CommanderException e) {
            metaNode = "Error: " + e.getCode();
        }

        sb.append("<html>");
        sb.append("<font color=\"#990000\">Properties of: </font>");
        sb.append("<font color=\"#990000\">" + tn.uri() + "</font><p>");
        sb.append("Node value: <font color=\"#000099\">" + nodeValue + "</font><p>");
        sb.append("ACL: <font color=\"#000099\">" + nodeAcl + "</font><p>");
        sb.append("Meta node: <font color=\"#000099\">" + metaNode + "</font><p>");
        jLabelProps.setText(sb.toString());
	}

	public void alert(String alert) {
        jTextAreaAlert.append("ALERT:\n" + alert);
        jTextAreaAlert.setVisible(true);
	}
    
    private void refreshToolBar() {
        if (opened) {
            jButtonOpen.setEnabled(false);
            jButtonClose.setEnabled(true);
            jButtonRefresh.setEnabled(true);
        } else {
            jButtonOpen.setEnabled(true);
            jButtonClose.setEnabled(false);
            jButtonRefresh.setEnabled(false);
        }
        if (null != jTree.getLastSelectedPathComponent()) {
            jButtonSetACL.setEnabled(true);
            jButtonExec.setEnabled(true);
            jButtonCreateInter.setEnabled(true);
            jButtonCreateLeaf.setEnabled(true);
            jButtonDelete.setEnabled(true);
            jButtonSetValue.setEnabled(true);
        } else {
            jButtonSetACL.setEnabled(false);
            jButtonExec.setEnabled(false);
            jButtonCreateInter.setEnabled(false);
            jButtonCreateLeaf.setEnabled(false);
            jButtonDelete.setEnabled(false);
            jButtonSetValue.setEnabled(false);
        }
    }
    
    private void command(String cmd) {
        try {
            jTextAreaCommandLog.append("> " + cmd + "\n");
            String result = commander.command(cmd);
            jTextAreaCommandLog.append("< " + result + "\n");
		} catch (CommanderException e) {
            JOptionPane.showMessageDialog(this, e.getCode(), "Error", JOptionPane.ERROR_MESSAGE);
            jTextAreaCommandLog.append("< EXCEPTION code: " + e.getCode() + " trace:\n" + e.getTrace());
		}
    }

}
