package app.model;

import java.util.ArrayList;
import java.util.Arrays; 

public enum Language {
	JAVA("java", new ArrayList<String>(Arrays.asList("java"))),
	CSHARP("[Sharp]C", new ArrayList<String>(Arrays.asList("cs"))),
	C("C", new ArrayList<String>(Arrays.asList("c", "o", "h"))),
	SQL("SQL", new ArrayList<String>(Arrays.asList("sql")));

	private Language(String latexString, ArrayList<String> filetypes){
		this.latexString = latexString;
		this.filetypes.addAll(filetypes);
	}
	
	private final String latexString;
	private final ArrayList<String> filetypes = new ArrayList<String>();

	
	public String getLatexString() {
		return latexString;
	}
	
	public ArrayList<String> getFiletypes() {
		return filetypes;
	}

}
