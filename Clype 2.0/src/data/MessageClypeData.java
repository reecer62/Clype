package data;
/**
 * 
 * @author anthonymangiacapra
 *
 * This class holds the data for messages sent over Clype
 *
 */
public class MessageClypeData extends ClypeData {
	private String message;
	/**
	 * The constructor that sets the values of userName, message, key, and type
	 * @param userName
	 * @param message
	 * @param key 
	 * @param type
	 */
	public MessageClypeData(String userName, String message, String key, int type) {
		super(userName, type);
		this.message = super.encrypt(message, key);
	}
	/**
	 * The constructor that sets the values of userName, message, and type
	 * @param userName
	 * @param message
	 * @param type
	 */
	public MessageClypeData(String userName, String message, int type) {
		super(userName, type);
		this.message = message;
	}
	/**
	 * The default constructor
	 * This constructor sets type to 3 (send a message)
	 */
	public MessageClypeData() {
		super(3);
	}
	/**
	 * The default constructor
	 */
	public String getData() {
		return message;
	}
	@Override
	public String getData(String key) {
		return decrypt(message, key);
	}
	@Override
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hash = 7;
		hash = hash * 19 + message.hashCode();
		hash = hash * 37 + super.userName.hashCode();
		hash = hash * 53 + super.type;
		hash = hash * 71 + super.date.hashCode();
		return hash;
	}
	@Override
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		MessageClypeData otherMessage = (MessageClypeData)other;
		return this.message == otherMessage.message && this.userName == otherMessage.userName && this.type == otherMessage.type && this.date == otherMessage.date;
	}
	@Override
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "message: " + message + " userName: " + super.userName + " type: " + super.type + " date: " + super.date;
	}

}
