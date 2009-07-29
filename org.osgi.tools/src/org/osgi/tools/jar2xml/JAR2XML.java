package org.osgi.tools.jar2xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.apache.bcel.classfile.AccessFlags;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.FieldOrMethod;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.osgi.tools.tag.Tag;

public class JAR2XML {
	Tag					top;
	File				file;
	
	public static void main( String args[] ) throws Exception {
		String				tagged=null;
		PrintWriter			out = new PrintWriter( new OutputStreamWriter(System.out,"ISO-8859-1"));
		out.println( "<?xml version='1.0' encoding='ISO-8859-1'?>" );
		Tag		top = new Tag("jars");
		top.addAttribute( "now", new Date() );
		
		for ( int i=0; i<args.length; i++ ) {
			if ( args[i].equals("-tagged") )
				tagged = args[++i];
			else {
				JAR2XML	j2x = new JAR2XML( args[i] );
				Tag	jar = j2x.process();
				if ( tagged != null )
					jar.addAttribute( "tagged", tagged );
				top.addContent( jar );
			}
		}
		top.print( 0, out );
		out.flush();
	}
	
	
	JAR2XML( String file ) {
		this.file = new File( file );
	}
	
	Tag process() throws IOException {
		JarFile		jar = new JarFile( file );
		top = new Tag("jar");
		top.addAttribute( "file", file.getAbsolutePath() );
		top.addAttribute( "date", new Date() );
		Manifest manifest = jar.getManifest();
		if ( manifest != null )
			top.addContent( doManifest( manifest ) );
		for ( Enumeration e= jar.entries(); e.hasMoreElements(); ) {
			ZipEntry 		zentry = (ZipEntry) e.nextElement();
			JarEntry		entry = jar.getJarEntry( zentry.getName() );
			Tag te = doEntry( entry );
			if ( entry.getName().endsWith(".class") ) {
				InputStream in = jar.getInputStream( entry );
				ClassParser		cp = new ClassParser( in, normalize( entry.getName() ) );
				JavaClass clazz = cp.parse();
				te.addContent( doClass( clazz ) );
			}
			top.addContent( te );
		}
		return top;
	}
	
	
	Tag doManifest( Manifest manifest ) {
		Tag			m = new Tag( "manifest" );
		Attributes attributes = manifest.getMainAttributes();
		for ( Iterator i = attributes.keySet().iterator(); i.hasNext();  ) {
			Attributes.Name name = (Attributes.Name)i.next();
			m.addContent( doAttribute( name, attributes.getValue( name ) ) );
		}
		return m;
	}
	
	Tag doAttribute( Attributes.Name name, Object value ) {
		Tag a = new Tag( "attribute", value.toString() );
		a.addAttribute( "name", name.toString() );
		return a;
	}
	
	Tag doEntry( JarEntry entry ) throws IOException {
		Tag e = new Tag( "entry" );
		e.addAttribute( "name", entry.getName() );
		Attributes attributes = entry.getAttributes();
		if ( attributes != null )
			for ( Iterator i = attributes.keySet().iterator(); i.hasNext();  ) {
				Attributes.Name name = (Attributes.Name)i.next();
				e.addContent( doAttribute( name, attributes.getValue( name ) ) );
			}
		Certificate[] certificates= entry.getCertificates();
		for ( int i=0; certificates!=null && i < certificates.length; i++ ) {
			e.addContent( doCertificate( certificates[i] ) );
		}
		return e;
	}
	
	Tag doClass( JavaClass clazz ) {
		Tag		c = new Tag("class");
	
		c.addAttribute( "className", clazz.getClassName().replace('$','.') );
		c.addAttribute( "short", mkShort(clazz.getClassName(),clazz).replace('$','.') );
		c.addAttribute( "fileName", clazz.getFileName() );
		c.addAttribute( "sourceFileName", clazz.getSourceFileName() );
		c.addAttribute( "major", clazz.getMajor()+"" );
		c.addAttribute( "minor", clazz.getMinor()+"" );
		c.addAttribute( "package", clazz.getPackageName() );
		c.addAttribute( "extends", mkShort(clazz.getSuperclassName(),clazz).replace('$','.') );

		doAccess( c, clazz );
		Field[] fields = clazz.getFields();
		for ( int i=0; i<fields.length; i++ )
			c.addContent( doField( clazz, fields[i] ) );
		Method[] methods = clazz.getMethods();
		for ( int i=0; i<methods.length; i++ )
			c.addContent( doMethod(  clazz, methods[i] ) );
		String[] interfaces = clazz.getInterfaceNames();
		for ( int i=0; i<interfaces.length; i++ )
			c.addContent( new Tag("implements", mkShort(interfaces[i],clazz) ) );
		return c;
		
	}	
	void doAccess( Tag a, AccessFlags access ) {		
		StringBuffer sb = new StringBuffer();
		if ( access.isPublic() ) {
			a.addAttribute( "public", "true" );
			sb.append( "public " );
		}
		if ( access.isFinal() ) {
			sb.append( "final " );
			a.addAttribute( "final", "true" );
		}
		if ( access.isAbstract() ) {
			a.addAttribute( "abstract", "true" );
			if ( ! access.isInterface() )
				sb.append( "abstract " );
		}
		if ( access.isProtected() ) {
			a.addAttribute( "protected", "true" );
			sb.append( "protected " );
		}
		if ( access.isPrivate() ) {
			a.addAttribute( "private", "true" );
			sb.append( "private " );
		}
		if ( access.isStatic() ) {
			sb.append( "static " );
			a.addAttribute( "static", "true" );
		}
		if ( access.isNative() ) {
			a.addAttribute( "native", "true" );
			sb.append( "native " );
		}
		if ( access.isVolatile() ) {
			a.addAttribute( "volatile", "true" );
			sb.append( "volatile " );
		}
		if ( access.isTransient() ) {
			a.addAttribute( "transient", "true" );
			sb.append( "transient " );
		}
		if ( access.isStrictfp() ) {
			a.addAttribute( "strictfp", "true" );
			sb.append( "strictfp " );
		}
		//if ( access.isSynchronized() ) {
		//	a.addAttribute( "synchronized", "true" );
		//	sb.append( "synchronized " );
		//}	
		if ( access.isInterface() ) {
			a.addAttribute( "interface", "true" );
		}	
		a.addAttribute( "access", sb.toString().trim() );
	}
	
	Tag doField(  JavaClass clazz, Field field  ) {
		Tag	f = new Tag("field");
		doFieldOrMethod( f, clazz, field );
		return f;
	}
	
	Tag doMethod( JavaClass clazz, Method method ) {
		Tag m = new Tag("method");
		doFieldOrMethod( m, clazz, method );
		ExceptionTable	et = method.getExceptionTable();
		if ( et != null ) {
			String[] names = et.getExceptionNames();
			for ( int i=0; i<names.length; i++ )
				m.addContent( new Tag("throws", mkShort(names[i],clazz)) );
		}
		//if ( method.getCode() != null )
		//	m.addContent( doCode( method.getCode() ) );
		return m;
	}
	
	void doFieldOrMethod(  Tag fm, JavaClass clazz, FieldOrMethod form ) {
		doAccess( fm, form );
		if ( form.getName().equals("<init>") ) {
			fm.addAttribute( "name", mkShort(clazz.getClassName(),clazz).replace('$','.') );
			fm.addAttribute( "constructor", "true" );
		}
		else
			fm.addAttribute( "name", form.getName() );
		fm.addAttribute( "signature", form.getSignature() );
		fm.addContent( doSignature( form.getSignature(),clazz ) );
	}		
	
	Tag doCode( Code code ) {
		Tag	c = new Tag("code");
		c.addAttribute("maxLocals", code.getMaxLocals()+"" );
		c.addAttribute("maxStack", code.getMaxStack()+"" );
		return c;
	}
	
	String normalize( String path ) {
		return path.replace('/','.').substring(0,path.length()-5);
	}
	
	Tag doCertificate( Certificate certificate ) {
		Tag	c = new Tag("certificate");
		c.addAttribute("type", certificate.getType() );
		c.addAttribute("algorithm", certificate.getPublicKey().getAlgorithm() );
		if ( certificate instanceof X509Certificate ) {
			X509Certificate x509 = (X509Certificate) certificate;
			c.addAttribute( "dn", x509.getIssuerDN().toString() );
			c.addAttribute( "notAfter", x509.getNotAfter()+"");
			c.addAttribute( "notBefore", x509.getNotBefore()+"");
			c.addAttribute( "serial", x509.getSerialNumber()+"");
			c.addAttribute( "version", x509.getVersion()+"");
		}
		return c;
	}
	
	Tag doSignature( String signature, JavaClass clazz ) {
		Tag			s = new Tag("signature");
		boolean		arguments = false;
		int			dimension = 0;
		
		for ( int i=0; i<signature.length(); i++ ) {
			char c = signature.charAt(i);
			switch(c) {
			case '[':
				dimension++;
				break;
			
			case '(':
				arguments=true;
				s.addAttribute("function","true");
				break;
				
			case ')':
				arguments=false;
				break;

			case 'L':
				int end = signature.indexOf( ';', i );
				String type = signature.substring( i+1, end );
				doType(s,clazz,type,arguments,dimension);
				dimension=0;
				i = end;
				break;
				
			case 'B':
				doType(s,clazz,"byte",arguments,dimension);
				dimension=0;
				break;
				
			case 'C':
				doType(s,clazz,"char",arguments,dimension);
				dimension=0;
				break;
				
			case 'D':
				doType(s,clazz,"double",arguments,dimension);
				dimension=0;
				break;
				
			case 'F':
				doType(s,clazz,"float",arguments,dimension);
				dimension=0;
				break;
				
			case 'I':
				doType(s,clazz,"int",arguments,dimension);
				dimension=0;
				break;
				
			case 'J':
				doType(s,clazz,"long",arguments,dimension);
				dimension=0;
				break;
				
			case 'S':
				doType(s,clazz,"short",arguments,dimension);
				dimension=0;
				break;
				
			case 'V':
				doType(s,clazz,"void",arguments,dimension);
				dimension=0;
				break;
				
			case 'Z':
				doType(s,clazz,"boolean",arguments,dimension);
				dimension=0;
				break;
				
			default: 
				throw new IllegalArgumentException( "Unknown type: " + c + " in '" + signature + "' " + s + "  "  + signature.substring(i-1));
			}
		}
		return s;
	}

	void doType( Tag s, JavaClass clazz, String type, boolean arguments, int dim ) {
		Tag result = null;
		if ( arguments )
			result = new Tag("argument");
		else
			result = new Tag("returns");
		
		type = type.replace('/','.');
		result.addAttribute( "type", mkShort(type,clazz).replace('$','.') );

		String shrt = type;
		String pack = "";
		int n = shrt.lastIndexOf('.');
		if ( n > 0 ) {
			pack = shrt.substring(0,n);
			shrt = shrt.substring(n+1);
		}
		result.addAttribute( "short", shrt.replace('$','.') );
		if (pack.length() > 0 )
			result.addAttribute( "package", pack );
		result.addAttribute( "dim", dim+"" );
		StringBuffer sb = new StringBuffer();
		sb.append( mkShort(type,clazz) );
		for ( int i=0; i<dim; i++ )
			sb.append("[]");
		result.addAttribute( "toString", sb.toString().replace('/','.').replace('$','.') );
		
		s.addContent( result );
	}
	
	String mkShort( String name, JavaClass clazz  ) {
		name = name.replace('/','.');
		int n = name.lastIndexOf( '.' );
		if ( (name.startsWith("java.lang.") )
		|| name.startsWith( clazz.getPackageName()  ) ) {
			return name.substring( n+1 );
		}
		else
			return name;
	}
}



