package app.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TerminalUnix extends Terminal {
	// private String pdflatexPath;

	public TerminalUnix(String workDir) throws Exception {
		super(workDir);

		if (!canRun()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error LaTeX not installed!");
			alert.setHeaderText("Can't find 'latexmk', are you sure it's installed correctly?");
			alert.setContentText("which means I can't compile.");

			alert.showAndWait();
		}
	}

	@Override
	public String compileToPDF() throws Exception {
		StringBuilder output = new StringBuilder();
		try {
			String[] compile = { pdflatexPath, "-pdf", "report.tex" };
			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.directory(new File(workDir));
			processBuilder.command(compile);
			Process process = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line);
			}
			int exitCode = process.waitFor();
			output.append("\nExited with error code : " + exitCode + "\n");
		} catch (Exception e) {
			throw new Exception("Can't compile PDF.");
		}

		return output.toString();
	}

	@Override
	public void setPdflatexPath() {
		pdflatexPath = tinyTexPath + "x86_64-linux" + File.separator + "latexmk";
	}

}