package data;

import java.util.Date;
import java.io.*;

/**
 * 
 * @author Reece Emero
 * 
 * This class is a superclass that represents the data sent between the client and the server.
 * ClypeData is serializable, meaning it can be interpreted as byte data.
 * 
 */
public abstract class ClypeData implements Serializable {
	protected String userName;
	protected final int type;
	protected static Date date = new Date();
	protected final int LIST_ALL_USERS = 0;
	protected final int LOG_OUT = 1;
	protected final int SEND_FILE = 2;
	protected final int SEND_MESSAGE = 3;
	
	/**
	 * Implements Vignère cipher to perform encryption with a key
	 * 
	 * I make the key lower case
	 * I turn the input string into an array of characters
	 * I iterate through the array of characters with a for loop
	 * For each character in the array i get its ASCII value and I set a boolean called 'isUpperCase' to false
	 * If the character's ASCII value is the ASCII equivalent of A-->Z, then 'isUpperCase' is set to true and turned into a lowercase character
	 * I create an offset that calculates what value is being added to the inputStringAsciiValue
	 * Add the offset to inputStringAsciiValue
	 * If that inputStringAsciiValue goes past 'z' then rotate through the alphabet again
	 * If the input string character is upper case, subtract the value of 'a' and add 'A' to get to capital alphabet and then subtract that from inputStringAsciiValue to get the encrypted letter
	 * Otherwise, just replace the character in inputStringCharacter array with the encrypted character
	 * Turn the array into a new String object and return that string
	 * 
	 * @param inputStringToEncrypt input string that is going to be encrypted
	 * @param key the key used to encrypt the input string
	 * 
	 * @return the encrypted input string
	 */
	protected String encrypt(String inputStringToEncrypt, String key) { 
		key = key.toLowerCase();
		char[] inputStringCharacters = inputStringToEncrypt.toCharArray();
		for(int i = 0; i < inputStringToEncrypt.length(); i++) {
			int inputStringAsciiValue = (int)inputStringCharacters[i];
			if ((inputStringAsciiValue >= 65 && inputStringAsciiValue <= 90) || (inputStringAsciiValue >= 97 && inputStringAsciiValue <= 122)) { //if it's alphabetical then encrypt
				boolean isUpperCase=false;
				if (inputStringAsciiValue >= 'A' && inputStringAsciiValue <= 'Z') {
					isUpperCase=true;
					inputStringAsciiValue+='a'-'A';
				}
				//Possible to change the encryption to fit around spaces
				int offset = key.charAt(i%key.length()) - 'a'; //charAt(iterates through the key modularly) and gets the distance between 'a' and the key's character
				inputStringAsciiValue += offset;
				if(inputStringAsciiValue > 'z') {
					inputStringAsciiValue -= 26;
				}
				inputStringCharacters[i] = (char) (isUpperCase ? inputStringAsciiValue - 'a' + 'A' : inputStringAsciiValue);
			} else {
				inputStringCharacters[i] = (char) inputStringAsciiValue;
			}
		}
		return new String(inputStringCharacters);
	}
	
	/**
	 * Implements the backwards decryption of the Vignère cipher using the key provided as input
	 * 
	 * I make the key lower case
	 * I turn the input string into an array of characters
	 * I iterate through the array of characters with a for loop
	 * For each character in the array i get its ASCII value and I set a boolean called 'isUpperCase' to false
	 * If the character's ASCII value is the ASCII equivalent of A-->Z, then 'isUpperCase' is set to true and turned into a lowercase character
	 * I create an offset that calculates what value is being added to the inputStringAsciiValue
	 * Add the offset to inputStringAsciiValue
	 * If that inputStringAsciiValue goes before 'a' then rotate back through the alphabet again
	 * If the input string character is upper case, subtract the value of 'a' and add 'A' to get to capital alphabet and then subtract that from inputStringAsciiValue to get the encrypted letter
	 * Otherwise, just replace the character in inputStringCharacter array with the decrypted character
	 * Turn the array into a new String object and return that string
	 * 
	 * @param inputStringToDecrypt
	 * @param key the key used to decrypt the input string
	 * 
	 * @return the decrypted input string
	 */
	protected String decrypt(String inputStringToDecrypt, String key) {
		key = key.toLowerCase();
		char[] inputStringCharacters = inputStringToDecrypt.toCharArray();
		for(int i = 0; i < inputStringToDecrypt.length(); i++) {
			int inputStringAsciiValue = (int)inputStringCharacters[i];
			if ((inputStringAsciiValue >= 65 && inputStringAsciiValue <= 90) || (inputStringAsciiValue >= 97 && inputStringAsciiValue <= 122)) { //if it's alphabetical then decrypt
				boolean isUpperCase=false;
				if (inputStringAsciiValue >= 'A' && inputStringAsciiValue <= 'Z') {
					isUpperCase=true;
					inputStringAsciiValue+='a'-'A';
				}
				int offset = key.charAt(i%key.length()) - 'a'; //charAt(iterates through the key modularly) and gets the distance between 'a' and the key's character
				inputStringAsciiValue -= offset;
				if(inputStringAsciiValue < 'a') {
					inputStringAsciiValue += 26;
				}
				inputStringCharacters[i] = (char) (isUpperCase ? inputStringAsciiValue - 'a' + 'A' : inputStringAsciiValue);
			} else {
				inputStringCharacters[i] = (char) inputStringAsciiValue;
			}
		}
		return new String(inputStringCharacters);
	}
	
	/**
	 * Constructor used to set up 'userName' and 'type', 'date' is automatically created here.
	 * 
	 * @param userName The user name of the client user
	 * @param type The kind of data exchanged between the client and the server
	 */
	public ClypeData(String userName, int type) {
		this.userName = userName;
		/*if(type == LIST_ALL_USERS || type == SEND_MESSAGE || type == LOG_OUT) {
			this.type = type; //MessageClypeData
		} else if(type == SEND_FILE) {
			this.type = type; //FileClypeData
		}*/
		this.type = type;
	}
	
	/**
	 * Constructor used to create an anonymous user
	 * 
	 * @param type The kind of data exchanged between the client and the server
	 */
	public ClypeData(int type) {
		this("Anon", type);
	}
	
	/**
	 * Default constructor
	 */
	public ClypeData() {
		this(3);
	}
	
	/**
	 * This accessor returns the type
	 * 
	 * @return the current type value (0-3)
	 */
	public int getType() {
		return this.type;
	}
	
	/**
	 * This accessor returns the user name of the client user
	 * 
	 * @return the current user name of the client user
	 */
	public String getUserName() {
		return this.userName;
	}
	
	/**
	 * This accessor returns the date
	 * 
	 * @return the current date
	 */
	public Date getDate() {
		return this.date;
	}
	
	//Abstract method
	public abstract String getData();
	public abstract String getData(String key);

	
}