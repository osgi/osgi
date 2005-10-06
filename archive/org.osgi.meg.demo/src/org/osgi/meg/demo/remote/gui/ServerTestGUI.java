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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.osgi.meg.demo.remote.CommanderException;
import org.osgi.meg.demo.remote.RMServer;


public class ServerTestGUI extends javax.swing.JFrame implements ActionListener, TreeSelectionListener {
    
    private static final String ADMIN_PRINCIPAL = "admin";
    private RMServer 		 rms; 
    private Commander        commander;
    private TreeNodeImpl     rootNode;
    private ScriptRunner 	 runner; 
    private boolean          opened = false;
    
    private JTabbedPane      jTabbedPane;
    private JTree            jTree;
    private DefaultTreeModel treeModel;
    private JPanel           jPanelTree;
    private JPanel           jPanelAlert;
    private JPanel           jPanelCommandLog;
    private JScrollPane      jScrollPaneAlert;
    private JScrollPane 	 jScrollPaneTree; 
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
    private JComboBox 	 	 jComboBoxScripts;
    private JLabel 			 jLabelScripts; 
    private JProgressBar 	 jProgressBarWaiting = new JProgressBar();
	
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
        
        int sto = 5000;
        if (args.length > 2)
        	sto = Integer.parseInt(args[2]);
        
		new ServerTestGUI(port, root, sto);
	}
	
    public ServerTestGUI(int port, String root, int socketTimeout) {
        super();

        rms = new RMServer(port, socketTimeout);
        commander = new Commander(rms, this);
        rms.setReceiver(commander);
        try {
			runner = new ScriptRunner(commander);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		rootNode = new TreeNodeImpl(root, null, commander);

        initGUI();
        refreshToolBar();
        setVisible(true);
        
        showProgressBar();
    }
    
    /*private void waitForClient() {
    	final Timer timer = new Timer();
    	final JFrame fr = ServerTestGUI.this;
    	
    	timer.schedule(new TimerTask() {

			public void run() {
				JProgressBar bar = new JProgressBar();
				bar.setIndeterminate(true);
				bar.setMinimum(0);
				bar.setMaximum(5);
				bar.setStringPainted(true);
				bar.setString("WAITING FOR CLIENT");
				fr.getContentPane().add(bar, BorderLayout.SOUTH);
				fr.validate();
				
				int value = 0;
		    	while (!rms.isConnected()) {
		    		try {Thread.sleep(1000);} catch (InterruptedException e) {}
		    		//bar.setValue((++value) % (bar.getMaximum() + 1));
		    	}
		    	try {
		    		fr.getContentPane().remove(bar);
		    		fr.validate();
		    		bar = null;
					open();
					refreshToolBar();
					timer.cancel();
				}
				catch (CommanderException e) {
					e.printStackTrace();
		            destroy();
		            System.exit(-1);
				}
			}
			
		}, 0);
    }*/
    
    private void destroy() {
    	try {
			commander.command("close");
		}
		catch (CommanderException e) {
            System.err.println(e.getString());
			e.printStackTrace();
		}
        rms.setRunning(false);
    }
    
    private void open() throws CommanderException {
        commander.command("open " + rootNode.uri() + " " + ADMIN_PRINCIPAL);
		
        treeModel = new DefaultTreeModel(rootNode);
        jTree = new JTree(treeModel);
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree.setShowsRootHandles(true);
        jTree.addTreeSelectionListener(this);
        
        jScrollPaneTree = new JScrollPane(jTree);
        this.jPanelTree.add(jScrollPaneTree, BorderLayout.CENTER);
        
        opened = true;
    }
    
    private void close() {
    	this.jPanelTree.remove(jScrollPaneTree);
		
        treeModel = null;
        jTree = null;
        jScrollPaneTree = null;
    	opened = false;
    	
    	refreshToolBar();
    }
	
	private void initGUI() {
		try {
			setSize(1000, 800);
            setTitle("Remote DMT manager");
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

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
                    {
                    	jLabelScripts = new JLabel("  Scripts: ");
                        jComboBoxScripts = new JComboBox(runner.getscriptFiles());
                        jComboBoxScripts.addActionListener(this);
                        jToolBar.add(jLabelScripts);
                        jToolBar.add(jComboBoxScripts);
                    }
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
		if (jComboBoxScripts.equals(e.getSource())) {
			JComboBox cb = (JComboBox)e.getSource();
	        String s = (String) cb.getSelectedItem();
	        try {
	        	jTextAreaCommandLog.append("Runs script: " + s + "-------------------\n");
	        	
	        	
				String result = runner.runScript(s);
				
				
				jTextAreaCommandLog.append(result);
				jTextAreaCommandLog.append("-------------------\n");
			}
			catch (CommanderException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, ex.getString(), 
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
        if (OPEN.equals(e.getActionCommand())) {
            // TODO close
            command("open " + rootNode.uri() + " " + ADMIN_PRINCIPAL);
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
                    "type:data_as_string (type int/chr/bool/date/time/float/" +
                    "xml/bin/b64/null):");
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
                    "type:data_as_string (type int/chr/bool/date/time/float/" +
            "xml/bin/b64/null):");
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
            if(nodeAcl.startsWith("<unset>"))
                nodeAcl = tn.getEffectiveNodeAcl() +
                    " <font color=\"#009900\">(inherited)</font>";
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
            jProgressBarWaiting.setVisible(true);
        } else {
            jButtonOpen.setEnabled(true);
            jButtonClose.setEnabled(false);
            jButtonRefresh.setEnabled(false);
            jProgressBarWaiting.setVisible(false);
        }
        if (!opened || null == jTree.getLastSelectedPathComponent()) {
        	jButtonSetACL.setEnabled(false);
            jButtonExec.setEnabled(false);
            jButtonCreateInter.setEnabled(false);
            jButtonCreateLeaf.setEnabled(false);
            jButtonDelete.setEnabled(false);
            jButtonSetValue.setEnabled(false);            
        } else {
            jButtonSetACL.setEnabled(true);
            jButtonExec.setEnabled(true);
            jButtonCreateInter.setEnabled(true);
            jButtonCreateLeaf.setEnabled(true);
            jButtonDelete.setEnabled(true);
            jButtonSetValue.setEnabled(true);
        }
    }
    
    private void command(String cmd) {
        try {
            jTextAreaCommandLog.append(cmd + "\n");
            String result = commander.command(cmd);
            jTextAreaCommandLog.append(result + "\n");
		} catch (CommanderException e) {
            JOptionPane.showMessageDialog(this, e.getCode(), "Error", JOptionPane.ERROR_MESSAGE);
            jTextAreaCommandLog.append(e.getString());
		}
    }

	public void setConnected(boolean b) {
		if (b) {
			new Thread(new Runnable() {
				public void run() {
					try {
						open();
						refreshToolBar();
						hideProgressBar();
						refreshToolBar();
					} catch (CommanderException e) {
                        System.err.println(e.getString());
						e.printStackTrace();
					}
				}}).start();
		} else {
			close();
			showProgressBar();
			refreshToolBar();
		}
	}
	
	private void showProgressBar() {
		jProgressBarWaiting = new JProgressBar();
		jProgressBarWaiting.setIndeterminate(true);
		jProgressBarWaiting.setMinimum(0);
		jProgressBarWaiting.setMaximum(5);
		jProgressBarWaiting.setStringPainted(true);
		jProgressBarWaiting.setString("WAITING FOR CLIENT");
		getContentPane().add(jProgressBarWaiting, BorderLayout.SOUTH);
		validate();		
	}
	
	private void hideProgressBar() {
		getContentPane().remove(jProgressBarWaiting);
		validate();
	}

}
