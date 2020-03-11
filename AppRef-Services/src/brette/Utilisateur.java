package brette;

import java.util.Vector;

/**
 * Classe représentant un utilisateur et ses messages
 * @author tyefen
 *
 */
public class Utilisateur {
	private String pseudo;
	private String password;
	private Vector<Message> messages;
	
	public Utilisateur(String pseudo, String password) {
		this.pseudo = pseudo;
		this.password = password;
		this.messages = new Vector<>();
	}

	public String getPseudo() {
		return pseudo;
	}

	public String getPassword() {
		return password;
	}
	
	public void addMessage(Message m) {
		messages.add(m);
	}
	public void delMessages() {
		messages = new Vector<>();
	}
	public Vector<Message> readMessages() {
		return new Vector<>(messages);
	}
	
	public String toString() {
		return getPseudo();
	}
}
