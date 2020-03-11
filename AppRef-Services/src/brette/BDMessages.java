package brette;

import java.util.Hashtable;

/**
 * Classe représentant une base de données de messages et d'utilisateurs
 * @author tyefen
 *
 */
public class BDMessages {
	private Hashtable<String, Utilisateur> users;
	
	public BDMessages() {
		users = new Hashtable<>();
	}
	
	public void addUser(Utilisateur u) throws Exception {
		if (!users.containsKey(u.getPseudo())) {
			users.put(u.getPseudo(), u);
		} else {
			throw new Exception("Ce pseudo existe déjà.");
		}
	}
	public Utilisateur getUser(String pseudo, String password) {
		Utilisateur u = null;
		synchronized(users) {
			if (users.containsKey(pseudo)) {
				u = users.get(pseudo);
			}
		}
		if (u!=null) {
			if (!u.getPassword().equals(password)) {
				u = null;
			}
		}
		return u;
	}

	public boolean sendMessage(String to, Message message) {
		Utilisateur u = null;
		synchronized(users) {
			if (users.containsKey(to)) {
				u = users.get(to);
			}
		}
		if (u!=null) {
			u.addMessage(message);
		}
		return u!=null;
	}
}
