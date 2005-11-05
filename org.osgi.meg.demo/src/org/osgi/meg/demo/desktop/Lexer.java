package org.osgi.meg.demo.desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;

public class Lexer {
	
	private static final String VAR_BEGIN = "${";
	private static final String VAR_END   = "}$";
	
	private StreamTokenizer tok;
	
	public Lexer(InputStream stream) {
		tok = new StreamTokenizer(stream);
		tok.wordChars('a', 'z');
		tok.wordChars('A', 'Z');
		tok.wordChars('0', '9');
		tok.wordChars('_', '_');
		tok.wordChars('.', '.');
		tok.wordChars('{', '{');
		tok.wordChars('}', '}');
		tok.wordChars('/', '/');
		tok.wordChars('*', '*');
		tok.wordChars('$', '$');
		
		tok.slashStarComments(true);
		tok.slashSlashComments(true);
	}
	
	public String nextToken() throws IOException {
		tok.nextToken();
		
		if (tok.ttype == StreamTokenizer.TT_EOF)
			return null;
		
		if (tok.ttype == '\"') {
			return process(tok.sval.substring(0, tok.sval.length()));
		}
		
		if (tok.ttype != StreamTokenizer.TT_WORD)
			throw new RuntimeException("Error in line: " + tok.lineno());
		
		return tok.sval;
	}

	private String process(String string) {
		StringBuffer sb = new StringBuffer(string);
		int ind_b = sb.indexOf(VAR_BEGIN);
		while (-1 != ind_b) {
			int ind_e = sb.indexOf(VAR_END);
			if (-1 == ind_e)
				throw new RuntimeException("Unclosed property variable");
			String var = sb.substring(ind_b + VAR_BEGIN.length(), ind_e);
			String prop = System.getProperty(var);
			if (null == prop)
				throw new RuntimeException("System property '" + var + "' has not found.");
			sb.replace(ind_b, ind_e + VAR_END.length(), prop);
			
			ind_b = sb.indexOf(VAR_BEGIN);
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		InputStream is = new FileInputStream(
				new File("/users/eclipse/workspaceMEG/org.osgi.meg.demo/OSGi_MEG.policy"));
		Lexer l = new Lexer(is);
		
		String symbol = l.nextToken();
		while (null != symbol) {
			System.out.println("|" + symbol + "|");
			symbol = l.nextToken();
		}
	}
	
}
