package brette;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import bri.Service;

public class ServiceMessagerie implements Service {

	private String DEL_MSG = "Je souhaite supprimer tous mes messages.";
	private static BDMessages dbmessages;
	private Socket client;
	
	static {
		try {
			Class.forName("brette.Utilisateur");
			Class.forName("brette.Message");
			Class.forName("brette.BDMessages");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbmessages = new BDMessages();
	}

	public ServiceMessagerie(Socket client) {
		this.client = client;
	}
	/**
	 * La méthode run pour la gestion des threads
	 */
	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			Boolean newUser = null;
			String msg = "Bienvenue sur le service de messagerie";
			while (newUser==null) {
				out.println(msg + "##Que souhaitez-vous faire ?##1 - Se connecter##2 - S'enregistrer");
				String choix = in.readLine();
				if (choix.equals("1")) {
					newUser = false;
				} else if (choix.equals("2")) {
					newUser = true;
				} else {
					msg = "Choix invalide !";
				}
			}
			out.println("Pseudo :");
			String pseudo = in.readLine();
			out.println("Mot de passe :");
			String password = in.readLine();
			boolean success = true;
			Utilisateur u = null;
			if (!newUser) {
				u = dbmessages.getUser(pseudo, password);
				if (u==null) {
					msg = "Pseudo ou mot de passe incorrect !";
					success = false;
				} else {
					msg = "";
				}
			} else {
				u = new Utilisateur(pseudo, password);
				try {
					dbmessages.addUser(u);
					msg = "Utilisateur créé !";
				} catch (Exception e) {
					success = false;
					msg = e.getMessage();
				}
			}

			if (!success) {
				out.println(msg);
			} else {
				msg += "##Bonjour, "+u.getPseudo()+".";
				while (true) {
					msg += "##Que souhaitez-vous faire ?##"+
							"1 - Consulter mes messages##2 - Envoyer un message##3 - Effacer mes messages##4 - Quitter";
					out.println(msg);
					String choix = in.readLine();
					if (choix.equals("1")) {
						Vector<Message> messages = u.readMessages();
						msg = "Vous avez "+messages.size()+" message(s).##";
						for (Message m : messages) {
							msg += ">>>>>>>>>>>>>>>>>>>>>>##De : "+m.getFromUser()+"##";
							msg += m.getMessage()+"####";
						}
					} else if (choix.equals("2")) {
						out.println("A qui ?");
						String to = in.readLine();
						out.println("Quel message ?");
						String message = in.readLine();
						if (dbmessages.sendMessage(to, new Message(u, message))) {
							msg = "Message envoyé !";
						} else {
							msg = "Destinataire inconnu !";
						}
					} else if (choix.equals("3")) {
						msg = "Attention ! Cela supprimera définitivement TOUS vos messages !##"
								+"Voulez-vous continuer ?##"
								+"(Tapez '"+DEL_MSG+"' pour valider.)";
						out.println(msg);
						choix = in.readLine();
						if (choix.equals(DEL_MSG)) {
							u.delMessages();
							msg = "Messages supprimés !";
						} else {
							msg = "Annulation de la suppression...";
						}
					} else if (choix.equals("4")) {
						break;
					} else {
						msg = "Choix invalide !";
					}
				}
			}

		} catch (IOException e) {
		}
		try {
			client.close();
		} catch (IOException e) {
		}
	}

	public static String toStringue() {
		return "Service de messagerie";
	}
}
