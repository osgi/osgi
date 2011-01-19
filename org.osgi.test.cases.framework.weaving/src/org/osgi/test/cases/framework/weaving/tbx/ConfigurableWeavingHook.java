package org.osgi.test.cases.framework.weaving.tbx;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;

public class ConfigurableWeavingHook implements WeavingHook {

	private String expected = "DEFAULT";
	
	private String changeTo = "WOVEN";
	
	private List dynamicImports = new ArrayList();
	
	public void setExpected(String expected) {
		this.expected = expected;
	}

	public void setChangeTo(String changeTo) {
		this.changeTo = changeTo;
	}
	
	public void addImport(String importString) {
		dynamicImports.add(importString);
	}

	public void clearImports() {
		dynamicImports.clear();
	}

	public void weave(WovenClass wovenClass) {
		
		if(wovenClass.getClassName().startsWith(TestConstants.TESTCLASSES_PACKAGE)) {
		    
			ClassReader reader = new ClassReader(wovenClass.getBytes());
		
			ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS) {

				public MethodVisitor visitMethod(int arg0, String arg1,
						String arg2, String arg3, String[] arg4) {
					if("toString".equals(arg1))
						return new ToStringChanger(super.visitMethod(arg0, arg1, arg2, arg3, arg4),
								expected, changeTo);
				    else
				    	return super.visitMethod(arg0, arg1, arg2, arg3, arg4);
				}
			};
			
			reader.accept(writer, 0);
			for(int i = 0; i < dynamicImports.size(); i++) {
				wovenClass.getDynamicImports().add(dynamicImports.get(i));
			}
			wovenClass.setBytes(writer.toByteArray());
		}

	}

	public ServiceRegistration register(BundleContext ctx) {
		return register(ctx, 0);
	}
	
	public ServiceRegistration register(BundleContext ctx, int rank) {
		Hashtable table = new Hashtable();
		table.put(Constants.SERVICE_RANKING, new Integer(rank));
		return ctx.registerService(WeavingHook.class.getName(), this, table);
	}
}
