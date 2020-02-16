package app.model;

import java.io.File;

public class TerminalWindows extends Terminal {

	public TerminalWindows(File tmpDir) throws Exception {
		super(tmpDir);
	}

	/**
	 * On Windows existing files aren't overwritten,
	 * therefore manually deleting works around that.
	 */
	@Override
	public void movePdfToDestination(String path) throws Exception {
		File fDestination = new File(path);

		if (fDestination.exists())
			fDestination.delete();

		super.movePdfToDestination(path);
	}

	@Override
	public String getPdflatexPath(String tinyTexPath) {
		return tinyTexPath + "win32" + File.separator + "latexmk.exe";
	}

	@Override
	public String[] getCommand() {
		String[] command = { "cmd.exe", "/c", pdflatexPath.getAbsolutePath(), "-pdf", "report.tex" };
		return command;
	}

}
