package app.model;

import java.io.File;

public abstract class Terminal {
//	protected final static String WORKING_DIR = "src"+File.separator+"resources"+File.separator+"tex"+File.separator;
	protected final static String COMPILED_PDF_NAME = "report.pdf";
	protected String workDir;

	public Terminal(String workDir) {
		this.workDir = workDir;
	}

	/**
	 * Compiles the TeX files to the final report
	 * 
	 * @return the path for the report
	 * @throws Exception to catch in GUI
	 */
	public abstract String compileToPDF() throws Exception;

	/**
	 * Checks wether LaTeX can be run on local machine TODO: disallow "RUN" calls if
	 * false, also throw error and then show alert from superclass
	 * 
	 * @return true or false
	 * @throws Exception
	 */
	public abstract boolean canRun() throws Exception;

	/**
	 * 
	 * @param path (including filename!): location and name for final file.
	 */
	public void movePdfToDestination(String path) {
		// TODO: validate compile tex before parsing/moving?
		File fSource = new File(workDir + COMPILED_PDF_NAME);
		File fDestination = new File(path);
		// TODO: remove eventually!
		System.out.println(path);
		fSource.renameTo(fDestination);
	}

	private void deleteDirRecursive(String path) {
		File d = new File(path);

		if (d.isDirectory() == false) {
			return;
		}
		File[] listFiles = d.listFiles();
		for (File f : listFiles) {
			f.delete();
		}
		d.delete();
	}

	public String compileAndMove(String path) throws Exception {
		String compiledLog = compileToPDF();
		movePdfToDestination(path);
		deleteDirRecursive(workDir);

		return compiledLog + "\n\n Finished!";
	}
}
