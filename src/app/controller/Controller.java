package app.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckComboBox;

import app.model.Language;
import app.model.LatexProcessor;
import app.model.Terminal;
import app.model.TerminalUnix;
import app.model.TerminalWindows;
import gui.Popup;
import gui.Validator;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller implements Initializable {
	@FXML
	private TextField txfAuthor;
	@FXML
	private TextField txfTitle;
	@FXML
	private TextField txfCodeDir;
	@FXML
	private TextField txfResultFile;
	@FXML
	private ListView<String> lwSourceFiles;
	@FXML
	private CheckComboBox<Language> ccbLanguages;
	@FXML
	private TextArea txaLog;
	@FXML
	private Button btnRun;
	@FXML
	private HBox hbLogArea;

	/**
	 * Initializes the listeners used for live validation
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lwSourceFiles.setManaged(false);

		txfAuthor.textProperty().addListener((obs, oldText, newText) -> {
			checkAuthorIsOK();
		});

		txfTitle.textProperty().addListener((obs, oldText, newText) -> {
			checkTitleIsOK();
		});
		txfResultFile.textProperty().addListener((obs, oldText, newText) -> {
			checkTargetIsOK();
		});
		txfCodeDir.textProperty().addListener((obs, oldText, newText) -> {
			checkCodeDirIsOK();
			lwSourceFiles.setManaged(true); // pane adopts to its size
			lwSourceFiles.setVisible(true);
		});

		// Populate languages dropdown
		for (Language l : Language.values()) {
			ccbLanguages.getItems().add(l);
		}

		ccbLanguages.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Language>() {
			public void onChanged(ListChangeListener.Change<? extends Language> l) {
				checkLanguagesIsOK();
				setLwSourceFiles(txfCodeDir.getText()); // update source files list
			}
		});

		hbLogArea.managedProperty().bind(hbLogArea.visibleProperty()); // controls size when visibility toggled
	}

	/**
	 * Creates report from users input: - if all fields are valid - or shows an
	 * error message
	 */
	@FXML
	private void createReport() {
		if (canGenerateReport()) {
			if (dialogConfirmCompile()) { // user accepts
				compileToPdf();
				btnRun.setDisable(true);
				new Popup("Document is ready", "Document has been compiled!", "Find it at " + txfResultFile.getText(),
						AlertType.INFORMATION).showAndWait();
			}
		} else {
			new Popup("Error", "Something prevents me from running", "Are all input fields green?\n"
					+ "If yes, try to close and open again.\n" + "I sometimes behave weirdly.", AlertType.ERROR)
							.showAndWait();
		}
	}

	/**
	 * Chooses directory where source code is located
	 */
	@FXML
	private void chooseDirectory() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		String path = "";

		try {
			path = directoryChooser.showDialog(new Stage()).getAbsolutePath();
			if (!Validator.checkDirectoryIsOK(path))
				throw new Exception("Invalid directory");

			setLwSourceFiles(path);

		} catch (Exception e) {
			path = "";
			lwSourceFiles.setVisible(false);
		}
		txfCodeDir.setText(path);
	}

	/**
	 * Choose location and name for the resulting report (PDF).
	 */
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

		txfResultFile.setText(path);
	}

	@FXML
	private void logAreaToggle() {
		hbLogArea.setVisible(!hbLogArea.isVisible());
	}

	/**
	 * Confirmation dialog asking user to start compiling report or not
	 * 
	 * @return users answer, ok = true.
	 */
	private boolean dialogConfirmCompile() {
		boolean confirmation = false;

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Ready to compile?");
		alert.setHeaderText("Want to compile? Application might FREEZE!");
		alert.setContentText("This is normal. Be patient with me.\n");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			confirmation = true;
		}
		return confirmation;
	}

	// -------------------- Helpers -----------------

	/**
	 * Starts the chain of calls, thus beginning the compilation of the code. Also
	 * updates the log area with "useful" information about the process.
	 * precondition: source files must be sanitized first
	 */
	private void compileToPdf() {
		String log;
		// Document data preparation
		String title = Validator.sanitizeString(txfTitle.getText());
		String author = Validator.sanitizeString(txfAuthor.getText());
		List<String> files = new ArrayList<String>(lwSourceFiles.getItems());
		// if no PDF file extension, then add
		String resultFile = (getFileExtension(txfResultFile.getText()).equals("")) ? txfResultFile.getText() + ".pdf"
				: txfResultFile.getText();

		// Start process
		// 1) write the TeX
		LatexProcessor lp = null;
		try {
			lp = new LatexProcessor(title, author, files, languagesSelected());
			lp.writeTexFiles();
		} catch (Exception e) {
			new Popup("Error", "Something went wrong with the filesystem", e.toString(), AlertType.ERROR).showAndWait();
		}
		// 2) compile TeX to PDF
		Terminal term = null;
		try {
			if (System.getProperty("os.name").contains("Windows")) {
				term = new TerminalWindows(lp.getTmpDir());
			} else {
				term = new TerminalUnix(lp.getTmpDir());
			}

			log = term.compileAndMove(resultFile);
			txaLog.setText(log);
		} catch (Exception e) {
			new Popup("Error", "Something went wrong. Can't compile: \n", e.toString(), AlertType.ERROR).showAndWait();
		} finally {
			term.deleteTmpDirectory();
		}
	}

	/**
	 * Gets a list of (selected) programming languages.
	 * 
	 * @return list of (enum) languages.
	 */
	private ArrayList<Language> languagesSelected() {
		ArrayList<Language> languages = new ArrayList<>();

		for (Language language : ccbLanguages.getCheckModel().getCheckedItems()) {
			languages.add(language);
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
	private ArrayList<Language> getFiletypes() {
		ArrayList<Language> filetypes = new ArrayList<Language>();

		for (int i : ccbLanguages.getCheckModel().getCheckedIndices()) {
			filetypes.add(ccbLanguages.getItems().get(i));
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
	 * @return file extension
	 */
	public static String getFileExtension(String path) {
		int indexExtension = path.lastIndexOf(".");
		if (indexExtension == -1) {
			return "";
		}
		return path.substring(indexExtension + 1).toLowerCase();
	}

	/**
	 * Remove files from list that doesn't have accepted filetype(s).
	 * 
	 * @param files     list of files
	 * @param filetypes list of accepted filetypes (prepended with dot!)
	 * @return same list without unwanted files.
	 */
	private ArrayList<String> removeUnwantedFiles(ArrayList<String> files, ArrayList<Language> filetypes) {
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
	private boolean isFileAccepted(String file, ArrayList<Language> filetypes) {
		boolean isAccepted = false;
		int i = 0;
		while (filetypes.size() > i && isAccepted == false) {
			if (filetypes.get(i).getFiletypes().contains(getFileExtension(file)))
				isAccepted = true;
			i++;
		}
		return isAccepted;
	}

	/**
	 * Clears style for Node and applying new. Useful for clearing status
	 * (error,success) and setting new
	 * 
	 * @param node to update
	 * @param css  class style
	 */
	private void setSuccessState(Node n, String newStyle) {
		n.getStyleClass().removeAll("success", "error");
		n.getStyleClass().add(newStyle);
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

		if (!checkAuthorIsOK() || !checkTitleIsOK() || !checkCodeDirIsOK() || !checkTargetIsOK()
				|| !checkLanguagesIsOK()) {
			hasError = true;
		}
		return !hasError;
	}

	private boolean checkAuthorIsOK() {
		boolean valid = false;

		if (Validator.checkAuthorIsOK(txfAuthor.getText())) {
			valid = true;
			setSuccessState(txfAuthor, "success");
		} else {
			setSuccessState(txfAuthor, "error");
		}
		return valid;
	}

	private boolean checkTitleIsOK() {
		boolean valid = false;

		if (Validator.checkTitleIsOK(txfTitle.getText())) {
			valid = true;
			setSuccessState(txfTitle, "success");
		} else {
			setSuccessState(txfTitle, "error");
		}
		return valid;
	}

	private boolean checkCodeDirIsOK() {
		boolean isValid = false;
		if (Validator.checkDirectoryIsOK(txfCodeDir.getText())) {
			isValid = true;
			setSuccessState(txfCodeDir, "success");
		} else {
			setSuccessState(txfCodeDir, "error");
		}
		return isValid;
	}

	private boolean checkTargetIsOK() {
		boolean isValid = false;
		String filePath = new File(txfResultFile.getText()).getParentFile().getAbsolutePath();
		if (Validator.checkDirectoryIsOK(filePath)) {
			isValid = true;
			setSuccessState(txfResultFile, "success");
		} else {
			setSuccessState(txfResultFile, "error");
		}
		return isValid;
	}

	/**
	 * Check if at least one language checkbox is checked.
	 * 
	 * @return true if valid otherwise false
	 */
	private boolean checkLanguagesIsOK() {
		boolean isValid = false;
		if (getFiletypes().size() > 0) {
			isValid = true;
			setSuccessState(ccbLanguages, "success");
		} else {
			setSuccessState(ccbLanguages, "error");
		}
		return isValid;
	}

}
