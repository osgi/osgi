package com.nokia.test.exampleresourceprocessor.db;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.deploymentadmin.Logger;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;

import com.nokia.test.db.Db;
import com.nokia.test.db.FieldDef;

public class DbResourceProcessor implements ResourceProcessor, BundleActivator, Serializable {
    
    // refrence to the database service
    private transient Db				db;
    
    // current Deployment Package and operation
    private transient DeploymentPackage actDp;
    private transient int 				actOp;
    
    // It contains the following hierarchy (dp is the key).
    // A side effect is a created table.
    //
    // dp_1 ----+---- res_1 ----+---- sideEff_1
    //          |               +---- sideEff_2
    //          |               +---- sideEff_3
    //          |
    //          +---- res_2 ----+---- sideEff_1
    //                          +---- sideEff_4
    //
    // dp_2 ----+---- res_1 ----+---- sideEff_1
    //          |               +---- sideEff_8
    //          |
    //          +---- res_3 ----+---- sideEff_1
    //                          +---- sideEff_9
    private Hashtable		  	dps = new Hashtable();
    
    private transient Object                dbSession;
    private transient ByteArrayOutputStream copy;
    private transient String 				id;
    
    /*
     * Side effect means table creation in case of this Resource Processor
     */
    private void putSideEffect(String resName, String tableName) {
        Hashtable ht = (Hashtable) dps.get(actDp);
        if (null == ht) {
            ht = new Hashtable();
            dps.put(actDp, ht);
        }
        
        Set s = (Set) ht.get(resName);
        if (null == s) { 
            s = new HashSet();
            ht.put(resName, s);
        }
        s.add(tableName);
    }
    
    public void begin(DeploymentPackage dp, int operation) {
        this.actDp = dp;
        this.actOp = operation;
        dbSession = db.begin();
        
        copy = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(copy);
            oos.writeObject(dps);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void complete(boolean commit) {
        if (commit) {
            db.commit(dbSession);
        } else {
            db.rollback(dbSession);
            
            try {
                ObjectInputStream ois = new ObjectInputStream(
                        new ByteArrayInputStream(copy.toByteArray()));
                dps = (Hashtable) ois.readObject();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void process(String resName, InputStream stream) throws Exception {
        deleteTables(resName);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line = br.readLine();
        while (null != line) {
            if (line.startsWith("TABLE")) {
                String[] parts = Splitter.split(line, ' ', 0);
                String tableName = parts[1]; 
                FieldDef[] fieldDefs = getFieldDefs(parts);
                db.createTable(dbSession, tableName, fieldDefs);
                putSideEffect(resName, tableName);
            } else if (line.startsWith("INSERT")) {
                String[] parts = Splitter.split(line, ' ', 0);
                String tableName = parts[1];
                Object[] row = getRow(db.getFieldDefs(dbSession, tableName), parts[2]);
                db.insertRow(dbSession, tableName, row);
            } else if (line.startsWith("DELETE")) {
                String[] parts = Splitter.split(line, ' ', 0);
                String tableName = parts[1];
                FieldDef[] fieldDefs = db.getFieldDefs(dbSession, tableName);
                int keyInd = FieldDef.indexOfKeyField(fieldDefs);
                switch (fieldDefs[keyInd].type.intValue()) {
                    case FieldDef.INTEGER :
                        db.deleteRow(dbSession, tableName, new Integer(parts[2]));
                        break;
                    case FieldDef.STRING :
                        db.deleteRow(dbSession, tableName, new String(parts[2]));
                        break;
                    default :
                        break;
                }
            } else if (line.startsWith("SLEEP")) {
                String[] parts = Splitter.split(line, ' ', 0);
                long time = Integer.parseInt(parts[1]);
                try {
                    System.out.println("SLEEP " + time + "ms");
                    Thread.sleep(time);
                }
                catch (InterruptedException e) {
                }
            }
            line = br.readLine();
        }
    }

    private void deleteTables(String resName) {
        Hashtable sideEffs = (Hashtable) dps.get(actDp);
        if (null == sideEffs)
            return;
        Set sideEffsForRes = (Set) sideEffs.get(resName);
        if (null == sideEffsForRes)
            return;
        for (Iterator iter = sideEffsForRes.iterator(); iter.hasNext();) {
            String tableName = (String) iter.next();
            db.dropTable(dbSession, tableName);
            iter.remove();
        }
    }

    private Object[] getRow(FieldDef[] fieldDefs, String line) {
        String[] parts = Splitter.split(line, '/', 0);
        Vector ret = new Vector();
        for (int i = 0; i < parts.length; i++) {
            switch (fieldDefs[i].type.intValue()) {
                case FieldDef.INTEGER :
                    ret.add(new Integer(parts[i]));
                    break;
                case FieldDef.STRING :
                    ret.add(new String(parts[i]));
                    break;
                default :
                    break;
            }
        }
        return ret.toArray();
    }

    private FieldDef[] getFieldDefs(String[] parts) {
        Vector ret = new Vector();
        int keyInd = Integer.parseInt(parts[parts.length - 1]);
        for (int i = 2; i < parts.length - 2; i += 2) {
            String n = i == 2 ? parts[i].substring(1) : parts[i];
            String t = parts[i + 1];
            if (t.startsWith("Integer")) {
                ret.add(new FieldDef(FieldDef.INTEGER, n, i - 2 == keyInd));
            } else if (t.startsWith("String")) {
                ret.add(new FieldDef(FieldDef.STRING, n, i - 2 == keyInd));
            }
        }
        return (FieldDef[]) ret.toArray(new FieldDef[] {});
    }

    public void dropped(String resName) throws Exception {
        Hashtable ht = (Hashtable) dps.get(actDp);
        Set effects = (Set) ht.get(resName);
        for (Iterator iter = effects.iterator(); iter.hasNext();) {
            String tableName = (String) iter.next();
            db.dropTable(dbSession, tableName);
        }
        ht.remove(resName);
    }

    public void dropped() {
        // TODO 
    }

    public void start(BundleContext context) throws Exception {
        String s = (String) context.getBundle().getHeaders().get("id");
        id = null == s ? "default_id" : s;
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        db = (Db) context.getService(ref);
        Dictionary	d = new Hashtable();
		d.put("type", "db");
		d.put("id", id);
        context.registerService(ResourceProcessor.class.getName(), this, d);
        System.out.println("DbResourceProcessor started. Id: " + id);
    }

    public void stop(BundleContext context) throws Exception {
        System.out.println("DbResourceProcessor stopped. Id: " + id);
    }
    
    // FOR TEST ONLY
    public Set getResources(DeploymentPackage dp, String resName) {
        Hashtable ht = (Hashtable) dps.get(dp);
        if (null == ht)
            return null;
        Set s = (Set) ht.get(resName);
        return s;
    }
    
    // FOR TEST ONLY
    /*private static Db db = new Db();
    public static void main(String[] args) throws Exception {
        ResourceProcessor rp = new DbResourceProcessor();
        
        String s = 
            "TABLE score (no Integer, player Integer, game Integer, score Integer) KEY 0\n" +
            "INSERT score 1/1/1/1050\n" +
            "\n" + 
            "# comment\n" + 
            "INSERT score 2/2/1/2000\n" +
            "INSERT score 3/3/1/5000\n" +
            "INSERT score 4/3/2/200\n" +
            "DELETE score 3";

        InputStream input = new ByteArrayInputStream(s.getBytes());
        rp.begin(null, 0);
        rp.process("sg.dbscript", input);
        rp.complete(true);
        
        db.printTableHeader(null, "score", System.out);
        db.printTableContent(null, "score", System.out);
    }*/

}
