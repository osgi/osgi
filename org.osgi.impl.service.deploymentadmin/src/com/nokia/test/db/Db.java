package com.nokia.test.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Db implements BundleActivator {
    
    public static class Table {
        private FieldDef[] fieldDefs;
        //private Integer	   indexOfKeyField;
        private Vector     rows = new Vector();
        
        public Table(FieldDef[] fieldDefs) throws Exception {
            this.fieldDefs = fieldDefs;
        }
        
        public FieldDef[] getFieldDefs() {
            return fieldDefs;
        }
        
        public void insert(Object[] row) throws Exception {
            // TODO check
            rows.add(row);
        }
        
        public void delete(Object key) {
            for (Iterator iter = rows.iterator(); iter.hasNext();) {
                Object[] row = (Object[]) iter.next();
                if (key.equals(row[FieldDef.indexOfKeyField(fieldDefs)]))
                    iter.remove();
            }
        }
        
        public Object[] find(Object key) {
            for (Iterator i = rows.iterator(); i.hasNext();) {
                Object[] row = (Object[]) i.next();
                if (row[FieldDef.indexOfKeyField(fieldDefs)].equals(key))
                    return row;
            }
            return null;
        }
    }

    // these contains Table class instances
    private Hashtable                       database = new Hashtable();
    private transient ByteArrayOutputStream copyOfDatabase;
    private transient ServiceRegistration   reg; 

    public void begin() {
        copyOfDatabase = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(copyOfDatabase);
            oos.writeObject(database);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void commit() {
        // do nothing
    }
    
    public void rollback() {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(copyOfDatabase.toByteArray()));
            database = (Hashtable) ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createTable(String tableName, FieldDef[] fieldDefs) throws Exception 
    {
        database.put(tableName, new Table(fieldDefs));
    }
    
    public void dropTable(String tableName) {
        database.remove(tableName);
    }

    public Iterator tables() {
        return database.keySet().iterator();
    }
    
    public void printTable(String tableName, PrintStream ps) {
        Table table = (Table) database.get(tableName);
        for (Iterator iter = table.rows.iterator(); iter.hasNext();) {
            Object[] row = (Object[]) iter.next();
            for (int i = 0; i < row.length; i++)
                ps.print(row[i] + " ");
            ps.println();
        }
    }

    public FieldDef[] getFieldDefs(String tableName) {
        return ((Table) database.get(tableName)).getFieldDefs();
    }

    public void insertRow(String tableName, Object[] row) throws Exception {
        ((Table) database.get(tableName)).insert(row);
    }
    
    public void deleteRow(String tableName, Object key) {
        Table table = (Table) database.get(tableName);
        table.delete(key);
    }
    
    public Object[] findRow(String tableName, Object key) {
        return ((Table) database.get(tableName)).find(key);
    }

    public void start(BundleContext context) throws Exception {
        reg = context.registerService(Db.class.getName(), this, null);
    }

    public void stop(BundleContext context) throws Exception {
        reg.unregister();
    }

}
