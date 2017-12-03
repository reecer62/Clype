package data;
import java.io.*;
/**
 * 
 * @author anthonymangiacapra
 *
 * This class holds the data for files being exchanged over Clype
 *
 */

public class FileClypeData extends ClypeData {
	private String fileName;
	private String fileContents;
	/**
	 * The constructor that sets the values of userName, fileName, and type
	 * This constructor also initializes fileContent to null
	 * @param userName
	 * @param fileName
	 * @param type
	 */
	public FileClypeData(String userName, String fileName, int type) {
		super(userName, type);
		this.fileName = fileName;
		this.fileContents = null;
	}
	/**
	 * The default constructor
	 * This constructor sets the type to 2 (send a file)
	 * This constructor also sets fileName and fileContents to null
	 */
	public FileClypeData() {
		super(2);
		this.fileName = null;
		this.fileContents = null;
	}
	/**
	 * Sets the value of fileName
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * Returns fileName
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * Returns fileContents
	 * @return
	 */
	public String getData() {
		return fileContents;
	}
	@Override
	public String getData(String key) {
		return decrypt(fileContents, key);
	}
	/**
	 * Returns nothing
	 * Reads in data from file and puts said data into the variable fileContents
	 */
	public void readFileContents() {
		String nextLine = null;
		fileContents = "";
		try {
			BufferedReader bufferedreader = new BufferedReader
					(new FileReader( fileName ));
			while((nextLine = bufferedreader.readLine()) != null) {
				try {
					fileContents += nextLine;
				} catch(NumberFormatException nfe) {
					System.err.println("'" + nextLine + "'" + " not an integer");
				}
			}
			if(bufferedreader != null) {
				bufferedreader.close();
			}
		} catch(FileNotFoundException fnfe) {
			System.err.println("file does not exist");
		} catch(IOException ioe) {
			System.err.println("IO error\n");
		}
	}
	/**
	 * Returns nothing
	 * Reads in encrypted data from file and puts decrypted data into the variable fileContents
	 * @param key
	 */
	public void readFileContents(String key) {
		String nextLine = null;
		String encryptedText = "";
		fileContents = "";
		try {
			BufferedReader bufferedreader = new BufferedReader
					(new FileReader( fileName ));
			while((nextLine = bufferedreader.readLine()) != null) {
				try {
					encryptedText += nextLine;
				} catch(NumberFormatException nfe) {
					System.err.println("'" + nextLine + "'" + " not an integer");
				}
			}
			fileContents = decrypt(encryptedText, key);
			if(bufferedreader != null) {
				bufferedreader.close();
			}
		} catch(FileNotFoundException fnfe) {
			System.err.println("file does not exist");
		} catch(IOException ioe) {
			System.err.println("IO error\n");
		}
	}
	/**
	 * Returns nothing
	 * Transfers contents from fileContents variable to a file
	 */
	public void writeFileContents() {
		try {
			BufferedWriter bufferedwriter = new BufferedWriter
					(new FileWriter( fileName));
			bufferedwriter.write(fileContents);
			if(bufferedwriter != null) {
				bufferedwriter.close();
			}
		}catch(IOException ioe) {
			System.err.println("IO error\n");
		}
		
	}
	/**
	 * Returns nothing
	 * Transfers contents from fileContents variable to a file and encrypts the contents upon transfer
	 * @param key
	 */
	public void writeFileContents(String key) {
		try {
			BufferedWriter bufferedwriter = new BufferedWriter
					(new FileWriter( fileName ));
			bufferedwriter.write(encrypt(fileContents, key));
			if(bufferedwriter != null) {
				bufferedwriter.close();
			}
		} catch(IOException ioe) {
			System.err.println("IO error\n");
		}
	}
	@Override
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hash = 7;
		hash = hash * 19 + fileName.hashCode();
		hash = hash * 37 + fileContents.hashCode();
		hash = hash * 53 + super.userName.hashCode();
		hash = hash * 71 + super.type;
		hash = hash * 89 + super.date.hashCode();
		return hash;
	}
	@Override
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		FileClypeData otherFile = (FileClypeData)other;
		return this.fileName == otherFile.fileName && this.fileContents == otherFile.fileContents && this.userName == otherFile.userName && this.type == otherFile.type && this.date == otherFile.date;
	}
	@Override
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "fileName: " + fileName + " fileContents: " + fileContents + " userName: " + super.userName + " type: " + super.type + " date: " + super.date;
	}

}
