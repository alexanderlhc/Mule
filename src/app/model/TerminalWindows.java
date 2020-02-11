package app.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TerminalWindows extends Terminal {

	public TerminalWindows(String workDir) throws Exception {
		super(workDir);

		if (!canRun()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error pdflatex.exe not found!");
			alert.setHeaderText("texlive directory must be in same directory as this jar file.");
			alert.setContentText("download the zip again.");

			alert.showAndWait();
		}
	}

	@Override
	public String compileToPDF() throws Exception {
		StringBuilder output = new StringBuilder();
		try {
			String pdflatexPath = new File(
					TerminalWindows.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent()
					+ File.separator + "texlive" + File.separator + "2019" + File.separator + "bin" + File.separator
					+ "win32" + File.separator + "pdflatex.exe";

			String[] compile = { "cmd.exe", "/c", pdflatexPath, "report.tex" };

			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.directory(new File(workDir));
			processBuilder.command(compile);
			Process process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			int exitCode = process.waitFor();
			output.append("\nExited with error code : " + exitCode + "\n");

		} catch (Exception e) {
			throw new Exception("Can't compile PDF.");
		}
		return output.toString();

	}

	@Override
	public boolean canRun() throws Exception {
		String pdflatexPath;
		File pdfLatex = null;
		try {
			pdflatexPath = new File(TerminalWindows.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParent() + File.separator + "texlive" + File.separator + "2019" + File.separator + "bin"
					+ File.separator + "win32" + File.separator + "pdflatex.exe";
			pdfLatex = new File(pdflatexPath);

		} catch (Exception e) {
			throw new Exception("Can't compile PDF.");
		}
		return (pdfLatex.exists()) ? true : false;
	}

}
