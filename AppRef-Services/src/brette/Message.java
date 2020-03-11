package brette;

/**
 * Classe représentant un message avec son expéditeur
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
