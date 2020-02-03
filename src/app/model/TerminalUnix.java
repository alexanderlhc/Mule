package app.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TerminalUnix extends Terminal{

	public TerminalUnix(String workDir) {
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
	public String compileToPDF() {
		StringBuilder output = new StringBuilder();
		try {
			String[] compile = { "latexmk", "-pdf", "report.tex" };
			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.directory(new File(workDir));
			processBuilder.command(compile);
			Process process = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line + "\n");
				output.append(line);
			}
			int exitCode = process.waitFor();
			output.append("\nExited with error code : " + exitCode + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(output.toString());
		return output.toString();
	}

	@Override
	public boolean canRun() {
		boolean canRun = false;
		StringBuilder output = new StringBuilder();
		try {
			String[] compile = { "which", "latexmk" };
			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.command(compile);
			Process process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line + "\n");
				output.append(line);
			}
	
			if(!output.toString().contains("not found")) {
				canRun = true;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return canRun;
	}


}