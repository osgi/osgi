package org.osgi.meg.demo.desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.tracker.ServiceTracker;

public class Policy {

	public static final String POLICY_FILE = "org.osgi.meg.demo.policy";

	private ServiceTracker cpaTr;
	
	private Lexer          lex;
	private String         symbol;
	
	private Vector         cpInfos;
	
	private CondPermInfo   actCondPermInfo;
	private CondInfo       actCondInfo;
	private PermInfo       actPermInfo;
	
	///////////////////////////////////////////////////////////////////////////
	
	public Policy(ServiceTracker cpaTr) {
		this.cpaTr = cpaTr;
	}

	public void clear() {
		ConditionalPermissionAdmin cpa = (ConditionalPermissionAdmin) cpaTr.getService();
        if (null == cpa)
            return;

        for (Enumeration en = cpa.getConditionalPermissionInfos(); en.hasMoreElements();) {
        	ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) en.nextElement();
        	cpi.delete();
        }
	}
	
	public void load() throws IOException {
		cpInfos = new Vector();
		
		String fn = System.getProperty(POLICY_FILE);
		if (null == fn) {
			// throw new IllegalArgumentException(POLICY_FILE + " property not set!");
			System.out.println(POLICY_FILE + " property is not set!");
			return;
		}
		
		File f = new File(fn);
		if (!f.exists()) {
		    throw new FileNotFoundException(fn);
        }

		loadPolicy(f);
	}

	private void loadPolicy(File f) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			lex = new Lexer(fis);
			
			nextSymbol();
			parseCpis();
		} finally {
			if (null != fis)
				fis.close();
		}
		
		createCondPermissions(cpInfos);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	private void nextSymbol() throws IOException {
		symbol = lex.nextToken();
	}

	private void accept(String expected) throws IOException {
		if (expected.equalsIgnoreCase(symbol))
			nextSymbol();
		else 
			throw new RuntimeException("Parsing error. " + expected + 
					" was expected but " + symbol + " was got.");
	}

	private String get() throws IOException {
		String ret = symbol;
		nextSymbol();
		return ret;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	private void parseCpis() throws IOException {
		parseCpi();
		parseCpiCont();
	}

	private void parseCpiCont() throws IOException {
		if (null != symbol)
			parseCpis();
	}

	private void parseCpi() throws IOException {
		actCondPermInfo = new CondPermInfo();
		accept("CPI");
		actCondPermInfo.id = get();
		accept("{");
		parseConds();
		parsePerms();
		accept("}");
		cpInfos.add(actCondPermInfo);
	}

	private void parseConds() throws IOException {
		parseCond();
		parseCondCont();
	}

	private void parseCond() throws IOException {
		actCondInfo = new CondInfo();
		accept("CONDITION");
		actCondInfo.type = get();
		accept("{");
		parseParams();
		accept("}");
		actCondPermInfo.add(actCondInfo);
	}

	private void parseCondCont() throws IOException {
		if ("CONDITION".equalsIgnoreCase(symbol))
			parseConds();
	}
	
	private void parseParams() throws IOException {
		actCondInfo.add(get());
		parseParamsCont();
	}
	
	private void parseParamsCont() throws IOException {
		if (!"}".equalsIgnoreCase(symbol))
			parseParams();
	}

	private void parsePerms() throws IOException {
		parsePerm();
		parsePermCont();
	}

	private void parsePerm() throws IOException {
		actPermInfo = new PermInfo();
		accept("PERMISSION");	
		actPermInfo.type = get();
		accept("{");
		actPermInfo.name = get();
		actPermInfo.actions = get();
		accept("}");
		actCondPermInfo.add(actPermInfo);
	}
	
	private void parsePermCont() throws IOException {
		if ("PERMISSION".equalsIgnoreCase(symbol))
			parsePerms();
	}

	///////////////////////////////////////////////////////////////////////////
	
	private void createCondPermissions(Vector infos) {
		for (Iterator iter = infos.iterator(); iter.hasNext();) {
			CondPermInfo cpi = (CondPermInfo) iter.next();
			addPermission(cpi);
		}
	}
	
    public void addPermission(CondPermInfo cpi) {
        ConditionalPermissionAdmin cpa = (ConditionalPermissionAdmin) cpaTr.getService();
        if (null == cpa)
            return;

        cpa.setConditionalPermissionInfo(cpi.id, cpi.getConditions(), cpi.getPermissions());
    }

	private class CondPermInfo {

		private String id;
		private Vector conds = new Vector();      
		private Vector perms = new Vector();
		
		private void add(CondInfo cond) {
			conds.add(cond.getConditionInfo());
		}
		
		private void add(PermInfo perm) {
			perms.add(perm.getPermissionInfo());
		}
		
		private ConditionInfo[] getConditions() {
			return (ConditionInfo[]) conds.toArray(new ConditionInfo[] {});
		}
		
		private PermissionInfo[] getPermissions() {
			return (PermissionInfo[]) perms.toArray(new PermissionInfo[] {});
		}

	}
	
	private class CondInfo {

		private String type;
		private Vector params = new Vector();      
				
		private void add(String param) {
			params.add(param);
		}
		
		private ConditionInfo getConditionInfo() {
			return new ConditionInfo(type, 
					(String[]) params.toArray(new String[] {}));
		}
		
	}

	private class PermInfo {
		
		private String type;
		private String name;
		private String actions;
		
		private PermissionInfo getPermissionInfo() {
			return new PermissionInfo(type, name, actions);
		}
		
	}

}
