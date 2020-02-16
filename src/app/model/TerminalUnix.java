package app.model;

import java.io.File;

public class TerminalUnix extends Terminal {

	public TerminalUnix(File tmpDir) throws Exception {
		super(tmpDir);
	}

	@Override
	public String[] getCommand() {
		String[] command = { pdflatexPath.getAbsolutePath(), "-pdf", "report.tex" };
		return command;
	}

	@Override
	public String getPdflatexPath(String tinyTexPath) {
		return tinyTexPath + "x86_64-linux" + File.separator + "latexmk";
	}

}