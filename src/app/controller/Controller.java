package app.controller;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;

import app.model.Language;
import app.model.LatexProcessor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller implements Initializable {
	@FXML
	private TextField txfAuthor = new TextField();
	@FXML
	private TextField txfTitle;
	@FXML
	private TextField txfCodeDir;
	@FXML
	private TextField txfTargetFile;
	@FXML
	private ListView<String> lwSourceFiles;
	@FXML
	private VBox vbLanguages;
	@FXML
	private CheckBox cbLangJava;
	@FXML
	private CheckBox cbLangCsharp;
	@FXML
	private CheckBox cbLangSql;
	@FXML
	private TextArea txaLog;


	public Controller() throws URISyntaxException {


	}

	/**
	 * Initializes the listeners used for live validation
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		txfAuthor.textProperty().addListener((obs, oldText, newText) -> {
		    checkAuthorIsOK();
		});
		
		txfTitle.textProperty().addListener((obs, oldText, newText) -> {
		    checkTitleIsOK();
		});
		txfTargetFile.textProperty().addListener((obs, oldText, newText) -> {
			checkTargetIsOK();
		});
		txfCodeDir.textProperty().addListener((obs, oldText, newText) -> {
			checkCodeDirIsOK();
		});
		
		// CheckBoxes (Language selection)
		for (Node n : vbLanguages.getChildren()) {
			if (n instanceof CheckBox) {
				((CheckBox) n).selectedProperty().addListener(new ChangeListener<Boolean>() {
			           public void changed(ObservableValue<? extends Boolean> ov,
			                   Boolean old_val, Boolean new_val) {
			                   checkLanguagesIsOK();		
			                   setLwSourceFiles(txfCodeDir.getText());
			                }
              });
			}
		}
	}
		
	
	@FXML
	private void createReport() {
		if (canGenerateReport()) {
			dialogConfirmCompile();
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Something prevents me from running");
			alert.setContentText("aaand, I don't know why?!\n"
					+ "Try again.");

			alert.showAndWait();
		}
	}

	@FXML
	private void chooseDirectory() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		String path = "";

		try {
			path = directoryChooser.showDialog(new Stage()).getAbsolutePath();
			if (!checkDirectoryIsOK(path))
				throw new Exception("Invalid directory");

			setLwSourceFiles(path);

		} catch (Exception e) {
			path = "";
			lwSourceFiles.setVisible(false);
		}
		txfCodeDir.setText(path);
	}

	@FXML
	private void chooseSave() {
		String path = "";
		try {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF", "*.pdf");
			fileChooser.getExtensionFilters().add(extFilter);
			path = fileChooser.showSaveDialog(new Stage()).getAbsolutePath();

			if (path.equals(""))
				throw new Exception("No file chosen");

		} catch (Exception e) {
		}

		txfTargetFile.setText(path);
	}

	private void dialogConfirmCompile() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Ready to compile?");
		alert.setHeaderText("Want to compile? Application might FREEZE!");
		alert.setContentText("This is normal. Be patient with me.\n");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){   
//			// TODO: why read vars twice and set this again?
			ArrayList<String> files = removeUnwantedFiles(getSrcFilesPaths(txfCodeDir.getText()), getFiletypes());
			LatexProcessor lp = new LatexProcessor(txfTitle.getText(), txfAuthor.getText(), files, languagesSelected());
			String exportFile = txfTargetFile.getText();
			if (getFileExtension(exportFile).equals("")) {
				exportFile = exportFile + ".pdf";
			}
			txaLog.setText(lp.compile(exportFile));
			
		} 
	}
	// -------------------- Helpers -----------------

	private ArrayList<Language> languagesSelected(){
		ArrayList<Language> languages = new ArrayList<>();
		
		if (cbLangJava.isSelected()) {
			languages.add(Language.JAVA);
		} 
		if (cbLangCsharp.isSelected()) {
			languages.add(Language.CSHARP);
		}
		if (cbLangSql.isSelected()) {
			languages.add(Language.SQL);
		}
		return languages;
	}
	/**
	 * Precondition: path is a valid directory Sets the ListView with all files with
	 * selected filetypes within path
	 */
	private void setLwSourceFiles(String path) {
		lwSourceFiles.setVisible(true);
		ArrayList<String> files = removeUnwantedFiles(getSrcFilesPaths(path), getFiletypes());
		lwSourceFiles.getItems().setAll(files);
	}

	/**
	 * Gets filetypes selected from checkboxes
	 * 
	 * @return list of filetypes or empty if non
	 */
	private ArrayList<String> getFiletypes() {
		ArrayList<String> filetypes = new ArrayList<String>();

		if (cbLangJava.isSelected()) {
			filetypes.add(".java");
		}
		if (cbLangCsharp.isSelected()) {
			filetypes.add(".cs");
		}
		if (cbLangSql.isSelected()) {
			filetypes.add(".sql");
		}
		return filetypes;
	}

	/**
	 * Precondition: root is valid path This method is (overloaded) for prettier
	 * calls. See other method for documentation!
	 */
	private ArrayList<String> getSrcFilesPaths(String root) {
		return getSrcFilesPaths(root, new ArrayList<String>());
	}

	/**
	 * Precondition: root is valid path
	 * 
	 * @param root (requested) directory for source files
	 * @return list of ALL files within root
	 */
	private ArrayList<String> getSrcFilesPaths(String root, ArrayList<String> srcFilesPaths) {
		File f = new File(root);
		if (f.isDirectory()) {
			String absPath = f.getAbsolutePath();
			for (String subpath : f.list()) {
				getSrcFilesPaths(absPath + File.separatorChar + subpath, srcFilesPaths);
			}

		} else {
			srcFilesPaths.add(f.getAbsolutePath());
		}

		return srcFilesPaths;
	}

	/**
	 * Gets file extension from filepath.
	 * 
	 * @param path to file
	 * @return file extension (with prepending dot)
	 */
	public static String getFileExtension(String path) {
		int indexExtension = path.lastIndexOf(".");
		if (indexExtension == -1) {
			return "";
		}
		return path.substring(indexExtension).toLowerCase();
	}

	/**
	 * Remove files from list that doesn't have accepted filetype(s).
	 * 
	 * @param files     list of files
	 * @param filetypes list of accepted filetypes (prepended with dot!)
	 * @return same list without unwanted files.
	 */
	private ArrayList<String> removeUnwantedFiles(ArrayList<String> files, ArrayList<String> filetypes) {
		Iterator<String> i = files.iterator();
		while (i.hasNext()) {
			String f = i.next();
			if (!isFileAccepted(f, filetypes))
				i.remove();
		}
		return files;
	}

	/**
	 * Checks if file has acceptable filetype
	 * 
	 * @param file      to check
	 * @param filetypes that gives acceptance
	 * @return true if file has valid file extension, otherwise false
	 */
	private boolean isFileAccepted(String file, ArrayList<String> filetypes) {
		return filetypes.contains(getFileExtension(file));
	}
	// -------------------- Checks -----------------

	/**
	 * Whether all options are satisfyingly filled the check to rule (contain) them
	 * all!
	 * 
	 * @return true or false can create the report
	 */
	private boolean canGenerateReport() {
		boolean hasError = false;

		if (!checkAuthorIsOK() && !checkTitleIsOK() && !checkCodeDirIsOK() && !checkTargetIsOK() && !checkLanguagesIsOK()) {
			hasError = true;
		}
		return !hasError;
	}


	private boolean checkAuthorIsOK() {
		boolean valid = false;

		if (checkString(txfAuthor.getText())) {
			valid = true;
			setStyleClass(txfAuthor, "success");
		} else {
			setStyleClass(txfAuthor, "error");
		}
		return valid;
	}
	

	private boolean checkTitleIsOK() {
		boolean valid = false;

		if (checkString(txfTitle.getText())) {
			valid = true;
			setStyleClass(txfTitle, "success");
		} else {
			setStyleClass(txfTitle, "error");
		}
		return valid;
	}
	
	private boolean checkCodeDirIsOK() {
		boolean isValid = false;		
		if (checkDirectoryIsOK(txfCodeDir.getText())) {
			isValid = true;
			setStyleClass(txfCodeDir, "success");
		} else {
			setStyleClass(txfCodeDir, "error");
		}
		return isValid;
	}

	private boolean checkTargetIsOK() {
		boolean isValid = false;
		String filePath = txfTargetFile.getText();
		if (!filePath.equals("") && checkDirectoryIsOK(new File(filePath).getParentFile().getAbsolutePath())) {
			isValid = true;
			setStyleClass(txfTargetFile, "success");
		} else {
			setStyleClass(txfTargetFile, "error");
		}	
		return isValid;
	}
	
	/**
	 * Check if at least one language checkbox is checked.
	 * 
	 * @return true if valid otherwise false
	 */
	private boolean checkLanguagesIsOK() {
		// TODO: intended to recall getFileTypes multiple times
		// maybe read fieldvariable.
		boolean isValid = false;
		if (getFiletypes().size() > 0) {
			isValid = true;
			setStyleClass(vbLanguages, "success");
		} else {
			setStyleClass(vbLanguages, "error");
		}
		return isValid;
	}
	
	private void setStyleClass(Node n, String newStyle) {
		n.getStyleClass().clear();
		n.getStyleClass().add(newStyle);
	}

	/**
	 * Given string is not empty nor contains illegal characters
	 * 
	 * @param s the string to check
	 * @return true if valid otherwise false
	 */
	private boolean checkString(String s) {
		// TODO: Doesn't support UNICODE (æøå etc)
		return (s.matches(".*\\W+.*") || s.length() == 0) ? false : true;
	}

	/**
	 * Directory is in fact a directory and is readable and writable.
	 * 
	 * @param path as string
	 * @return true if valid otherwise false
	 */
	private boolean checkDirectoryIsOK(String path) {
		File f = new File(path);
		return (f.isDirectory() && f.canRead() && f.canWrite()) ? true : false;
	}






}
