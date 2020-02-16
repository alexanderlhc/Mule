package app.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import gui.Popup;
import javafx.scene.control.Alert.AlertType;

public abstract class Terminal {
	protected File pdflatexPath;
	protected final static String COMPILED_PDF_NAME = "report.pdf";
	protected File tmpDir;

	public Terminal(File tmpDir) throws Exception {
		this.tmpDir = tmpDir;
		setPdflatexPath();

		if (!pdflatexPath.canExecute()) {
			new Popup("Error latexmk not found!", "TinyTex directory must be in same directory as this jar file.",
					"download the zip again.", AlertType.ERROR).showAndWait();
		}
	}

	public abstract String getPdflatexPath(String tinyTexPath);

	public abstract String[] getCommand();

	/**
	 * Compiles the TeX files to the final report
	 * 
	 * @return the path for the report
	 * @throws Exception to catch in GUI
	 */
	public String compileToPDF() throws Exception {
		StringBuilder output = new StringBuilder();

		try {
			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.directory(tmpDir);
			processBuilder.command(getCommand());
			Process process = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

			int exitCode = process.waitFor();
			output.append("\nExited with error code : " + exitCode + "\n");

		} catch (Exception e) {
			throw new Exception(
					"Can't compile PDF, am unable to properly call latexmk.\n\n System error: \n" + e.getMessage());
		}
		return output.toString();

	}

	/**
	 * Checks whether LaTeX can be run on local machine
	 * false, also throw error and then show alert from superclass
	 * 
	 * @return true or false
	 * @throws Exception
	 */
	private void setPdflatexPath() throws Exception {
		try {
			String tinyTexPath = new File(Terminal.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParent() + File.separator + "TinyTeX" + File.separator + "bin" + File.separator;

			pdflatexPath = new File(getPdflatexPath(tinyTexPath));
		} catch (Exception e) {
			throw new Exception("Can't compile PDF. Am unable to find TinyTeX.\n\n System error: \n" + e.getMessage());
		}
	}

	/**
	 * 
	 * @param path (including filename!): location and name for final file.
	 * @throws Exception
	 */
	public void movePdfToDestination(String path) throws Exception {
		File fSource = new File(tmpDir + File.separator + COMPILED_PDF_NAME);
		File fDestination = new File(path);
		if (!fSource.renameTo(fDestination))
			throw new Exception("Can't move the report to destination");
	}

	/**
	 * Deletes directory
	 * 
	 * @param directory
	 */
	private void deleteDirectory(File directory) {
		File[] listFiles = directory.listFiles();
		for (File f : listFiles) {
			f.delete();
		}
		directory.delete();
	}

	public String compileAndMove(String path) throws Exception {
		String compiledLog = compileToPDF();
		movePdfToDestination(path);
		deleteDirectory(tmpDir);

		return compiledLog + "\n\n Finished!";
	}
}
