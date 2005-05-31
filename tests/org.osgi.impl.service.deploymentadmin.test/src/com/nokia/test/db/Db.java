package com.nokia.test.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * A simple non persistent database implementation that supports 
 * the most common database operations (create table, delete table, 
 * insert/delete row, find row by primary key etc. 
 */
public class Db implements BundleActivator {

    /*
     * Table class that represent a database table. Stores 
     * structural information about the table and actual content
     * (rows) of the table.  
     */
    private static class Table implements Serializable {
        private FieldDef[] fieldDefs;
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
    
    /*
     * The databse doesn't supports concurrent database access and 
     * concurrent transactions. This behaviour is ensure throw this 
     * semaphore implementation. 
     */
    private static class Semaphore {
        private Object session;
        
        public synchronized Object capture(Object session) {
            if (this.session == session)
                return session;
                
            while (this.session != null) {
                try {
                    wait();
                } catch (InterruptedException e) {}
            }
            return this.session = new Object();
        }
        
        public synchronized void release(Object session) {
            if (this.session == session) {
                session = null;
                notify();
            }
        }
    }

    // these contains Table class instances
    private Hashtable                       database = new Hashtable();
    private transient ByteArrayOutputStream copyOfDatabase;
    private Semaphore						semaphore = new Semaphore(); 	
    
    private transient ServiceRegistration   reg; 

    public synchronized Object begin() {
        Object session = semaphore.capture(null);
        
        copyOfDatabase = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(copyOfDatabase);
            oos.writeObject(database);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return session;
    }
    
    public synchronized void commit(Object session) {
        semaphore.release(session);
    }
    
    public synchronized void rollback(Object session) {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(copyOfDatabase.toByteArray()));
            database = (Hashtable) ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        semaphore.release(session);
    }
    
    public synchronized void reset(Object session) {
        semaphore.capture(session);
        database = new Hashtable(); 
        semaphore.release(session);
    }
    
    public synchronized void createTable(Object session, String tableName, FieldDef[] fieldDefs) throws Exception {
        semaphore.capture(session);
        database.put(tableName, new Table(fieldDefs));
        semaphore.release(session);
    }
    
    public synchronized void dropTable(Object session, String tableName) {
        semaphore.capture(session);
        database.remove(tableName);
        semaphore.release(session);
    }

    public synchronized String[] tableNames(Object session) {
        semaphore.capture(session);
        String[] ret = (String[]) database.keySet().toArray(new String[] {});
        semaphore.release(session);
        return ret;
    }
    
    public synchronized void printTableHeader(Object session, String tableName, PrintStream ps) {
        semaphore.capture(session); 
        Table table = (Table) database.get(tableName);
        for (int i = 0; i < table.fieldDefs.length; i++)
            ps.print(table.fieldDefs[i].name + " ");
        ps.println();
        semaphore.release(session);
    }
    
    public synchronized void printTableContent(Object session, String tableName, PrintStream ps) {
        semaphore.capture(session);
        Table table = (Table) database.get(tableName);
        for (Iterator iter = table.rows.iterator(); iter.hasNext();) {
            Object[] row = (Object[]) iter.next();
            for (int i = 0; i < row.length; i++)
                ps.print(row[i] + " ");
            ps.println();
        }
        semaphore.release(session);
    }

    public synchronized FieldDef[] getFieldDefs(Object session, String tableName) {
        semaphore.capture(session);
        FieldDef[] ret = ((Table) database.get(tableName)).getFieldDefs();
        semaphore.release(session);
        return ret;
    }

    public synchronized void insertRow(Object session, String tableName, Object[] row) throws Exception {
        semaphore.capture(session);
        ((Table) database.get(tableName)).insert(row);
        semaphore.release(session);
    }
    
    public synchronized void deleteRow(Object session, String tableName, Object key) {
        semaphore.capture(session);
        Table table = (Table) database.get(tableName);
        table.delete(key);
        semaphore.release(session);
    }
    
    public synchronized Object[] findRow(Object session, String tableName, Object key) {
        semaphore.capture(session);
        Object[] ret = ((Table) database.get(tableName)).find(key);
        semaphore.release(session);
        return ret;
    }

    public void start(BundleContext context) throws Exception {
        reg = context.registerService(Db.class.getName(), this, null);
        System.out.println("Simple database started.");
    }

    public void stop(BundleContext context) throws Exception {
        reg.unregister();
        System.out.println("Simple database stopped.");
    }

}
