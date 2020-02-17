package bri;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

import login.ProgList;

/**
 * Le service BRi c�t� client programmeur
 * @author tyefen
 *
 */
public class ServiceBRiProg implements ServiceBRi {

	private Socket client;

	public ServiceBRiProg(Socket socket) {
		client = socket;
	}

	/**
	 * La m�thode run pour la gestion des threads
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			out.println("Login : ");
			String login = in.readLine();
			out.println("Mot de passe : ");
			String pass = in.readLine();

			String url = ProgList.verifLogin(login, pass);

			if (url!=null) {
				// URLClassLoader sur ftp

				String msg = "Bienvenue dans votre gestionnaire dynamique d'activit� BRi##"
						+ "Pour ajouter une activit�, celle-ci doit �tre pr�sente sur votre serveur ftp ("+url+")##"
						+ "Les clients se connectent au serveur 4200 pour lancer une activit�##";
				while (true){
					msg += "Que voulez-vous faire ?##"
							+ "1 - Ajouter / Mettre � jour un service##"
							+ "2 - Changer de serveur ftp";
					out.println(msg);
					String choix = in.readLine();
					try {
						if (choix.equals("1")) {
							msg = "Quelle classe voulez-vous charger ?";
							out.println(msg);
							msg = "";
							// charger la classe et la d�clarer au ServiceRegistry
							URL[] tabURL={new URL(url)};
							URLClassLoader urlcl = new URLClassLoader(tabURL);
							Class<? extends Runnable> classeCharg�e;
							classeCharg�e = (Class<? extends Service>) urlcl.loadClass(in.readLine());
							msg += classeCharg�e.getName()+"##";
							ServiceRegistry.addService(classeCharg�e, login);
							msg += "Classe charg�e.##";
						}
						else if (choix.equals("2")) {
							msg = "Nouvelle URL :";
							out.println(msg);
							url = in.readLine();
							ProgList.changeURL(login, url);
							msg = "URL chang�e !##";
						} else {
							msg = "Choix invalide !##";
						}

					} catch (Exception e) {
						msg += e.toString().replace("\n", "##")+"##";
					}
				}
			} else {
				out.println("Login ou mot de passe incorrect.");
				client.close();
			}

		} catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Fermer la socket quand le service est d�truit
	 */
	protected void finalize() throws Throwable {
		client.close(); 
	}

	/**
	 * Lancer le service
	 */
	public void start() {
		(new Thread(this)).start();		
	}

}
