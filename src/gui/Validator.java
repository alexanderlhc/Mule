package gui;

import java.io.File;

/**
 * Helps views with data validation
 * 
 *
 */
public class Validator {

	/**
	 * If author input is valid (not empty)
	 * 
	 * @param author
	 * @return true if valid
	 */
	public static boolean checkAuthorIsOK(String author) {
		return (author.length() > 0) ? true : false;
	}

	/**
	 * If title input is valid (not empty)
	 * 
	 * @param title
	 * @return true if valid
	 */
	public static boolean checkTitleIsOK(String title) {
		return (title.length() > 0) ? true : false;
	}

	/**
	 * Directory is in fact a directory and is readable and writable.
	 * 
	 * @param path as string
	 * @return true if valid otherwise false
	 */
	public static boolean checkDirectoryIsOK(String path) {
		File f = new File(path);
		return (f.isDirectory() && f.canRead() && f.canWrite()) ? true : false;
	}

}
