package brette;

/**
 * Classe repr�sentant un message avec son exp�diteur
 * @author tyefen
 *
 */
public class Message {
	private Utilisateur fromUser;
	private String message;
	
	public Message(Utilisateur fromUser, String message) {
		super();
		this.fromUser = fromUser;
		this.message = message;
	}

	public Utilisateur getFromUser() {
		return fromUser;
	}

	public String getMessage() {
		return message;
	}
	
}
