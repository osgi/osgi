package com.nokia.test.exampleresourceprocessor.db;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;

import com.nokia.test.db.Db;
import com.nokia.test.db.FieldDef;

public class DbResourceProcessor implements ResourceProcessor, BundleActivator {
    
    private transient Db				db;
    
    private transient DeploymentPackage actDp;
    
    // It contains the following hierarchy (dp is the key).
    // A side effect is a line of the .dbscript resource.
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
    private transient Hashtable actSideEffs;
    private Object 			    dbSession;
    
    private void putSideEffect(String resName, String sideEff) {
        Vector v = (Vector) actSideEffs.get(resName);
        if (null == v) { 
            v = new Vector();
            actSideEffs.put(resName, v);
        }
        v.add(sideEff);
    }

    public void begin(DeploymentPackage dp, int operation) {
        this.actDp = dp;
        actSideEffs = new Hashtable();
        dbSession = db.begin();
    }

    public void complete(boolean commit) {
        if (commit) {
            db.commit(dbSession);
            if (null != actDp)
                dps.put(actDp, actSideEffs);
        }
        else 
            db.rollback(dbSession);
    }

    public void process(String name, InputStream stream) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line = br.readLine();
        while (null != line) {
            if (line.startsWith("TABLE")) {
                String[] parts = Splitter.split(line, ' ', 0);
                String tableName = parts[1]; 
                FieldDef[] fieldDefs = getFieldDefs(parts);
                db.createTable(dbSession, tableName, fieldDefs);
                putSideEffect(name, line);
            } else if (line.startsWith("INSERT")) {
                String[] parts = Splitter.split(line, ' ', 0);
                String tableName = parts[1];
                Object[] row = getRow(db.getFieldDefs(dbSession, tableName), parts[2]);
                db.insertRow(dbSession, tableName, row);
                putSideEffect(name, line);
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
                putSideEffect(name, line);
            }
            line = br.readLine();
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

    public void dropped(String name) throws Exception {
        Hashtable ht = (Hashtable) dps.get(actDp);
        Vector effects = (Vector) ht.get(name);
        // TODO delete effects
    }

    public void dropped() {
        // TODO 
    }

    public void start(BundleContext context) throws Exception {
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        db = (Db) context.getService(ref);
        
        Dictionary	d = new Hashtable();
		d.put("type", "db");        
        context.registerService(ResourceProcessor.class.getName(), this, d);
    }

    public void stop(BundleContext context) throws Exception {
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
