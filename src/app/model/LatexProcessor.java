package app.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.*;

import app.controller.Controller;
import com.sun.org.apache.xpath.internal.operations.Bool;

public class LatexProcessor {
	private String title;
	private String author;
	private List<String> files;
	private ArrayList<Language> languages;
	private File tmpDir;
	private boolean addChapters;
	private TreeMap<String, Boolean> extraFiles; // path, append or prepend

	public LatexProcessor(String title, String author, List<String> files, ArrayList<Language> languages, TreeMap<String, Boolean> extraFiles, boolean addChapters)
			throws URISyntaxException {
		this.title = title;
		this.author = author;
		this.files = files;
		this.languages = languages;
		this.addChapters = addChapters;
		this.extraFiles = extraFiles;

		tmpDir = new File(
				new File(LatexProcessor.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent()
						+ File.separator + "tmp" + File.separator);
		tmpDir.mkdir();

	}

	public File getTmpDir() {
		return tmpDir;
	}

	/**
	 * Builds a chapter for each selected language
	 * 
	 * @return the string (LaTeX compatible) containing all code.
	 */
	private String latexForAllLanguages() {
		StringBuilder sb = new StringBuilder();
		for (Language language : languages) {
			sb.append(latexFromCodeSection(language, addChapters));
		}
		return sb.toString();
	}

	/**
	 * Returns string with all files separated on individual pages formatted for
	 * LaTeX to compile.
	 * 
	 * @param files    to be included in the section
	 * @param language that will be highlighted
	 * @return string formatted for LaTeX
	 */
	private String latexFromCodeSection(Language language, boolean isChapter) {
		StringBuilder sb = new StringBuilder();
		if(isChapter)
			sb.append(String.format("\\chapter{%s}", language.getLatexString()));
		sb.append("\\newpage\n");

		for (String path : files) {
			File f = new File(path);
			String filetype = Controller.getFileExtension(f.getName());
			if (language.getFiletypes().contains(filetype)) {
				sb.append(String.format("\\section{%s}", f.getName()));
				path = path.replaceAll("\\\\", "/");
				sb.append(String.format("\\lstinputlisting[language={%s}]{\"%s\"}", language.getLatexString(), path));
				sb.append("\\newpage\n");
			}
		}

		return sb.toString();
	}

	/**
	 * Returns string with title and author formatted for LaTeX to compile.
	 * 
	 * @param title     of the document
	 * @param author(s) of the document
	 * @return string formatted for LaTeX
	 */
	private String latexFromReportProperties() {
		return String.format("\\title{%s}", title) +
				String.format("\\author{%s}", author);
	}

	/**
	 * Writes the temporary TeX files from templates, are target for compiler and
	 * thus turned into PDF these files should be deleted since placed in tmp dir.
	 * 
	 * @throws FileNotFoundException
	 */
	public void writeTexFiles() throws Exception {
		// Template
		try (PrintWriter writer = new PrintWriter(tmpDir + File.separator + "report.tex")) {
			BufferedReader txtReader = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream("/resources/template.tex")));
			String line;
			while ((line = txtReader.readLine()) != null) {
				writer.println(line);
			}
		} catch (IOException e) {
			throw new Exception("Can't write to the temporary report.tex file.");
		}

		// For each file wanted prepended
			try (PrintWriter writerPrepends = new PrintWriter(tmpDir + File.separator + "prepends.tex"); PrintWriter writerAppends = new PrintWriter(tmpDir + File.separator + "appends.tex");) {
				for (Map.Entry<String, Boolean> file : extraFiles.entrySet()) {

					String path = file.getKey().replaceAll("\\\\", "/");
					String extraFile = String.format("\\includepdf[pages=-]{%s}%n\\newpage", path);
					if (file.getValue()) {
						writerPrepends.write(extraFile);
					} else {
						writerAppends.write(extraFile);
					}
				}
			} catch (IOException e) {
				throw new Exception("Can't write to the temporary report.tex file.");
			}


		// Document properties
		try (PrintWriter writer = new PrintWriter(tmpDir + File.separator + "properties.tex")) {
			writer.write(latexFromReportProperties());
			writer.write("\\newpage");
		} catch (IOException e) {
			throw new Exception("Can't write to the temporary report.tex file.");
		}


		// Code
		try (PrintWriter writer = new PrintWriter(tmpDir + File.separator + "code.tex")) {
			writer.write(latexForAllLanguages());
		} catch (Exception e) {
			throw new Exception("Can't write to the temporary code.tex file.");
		}
	}

}
