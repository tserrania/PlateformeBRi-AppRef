package bri;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

import login.ProgList;

/*
 * Arguments à ajouter à la JVM
 * -XX:+TraceClassLoading
 * -XX:+TraceClassUnloading
 */

/**
 * Le service BRi côté client programmeur
 * @author tyefen
 */
public class ServiceBRiProg implements ServiceBRi {

	private Socket client;

	public ServiceBRiProg(Socket socket) {
		client = socket;
	}

	/**
	 * La méthode run pour la gestion des threads
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

				String msg = "Bienvenue dans votre gestionnaire dynamique d'activité BRi##"
						+ "Pour ajouter une activité, celle-ci doit être présente sur votre serveur ftp ("+url+")##"
						+ "Les clients se connectent au serveur 4200 pour lancer une activité##";
				while (true){
					msg += "Que voulez-vous faire ?##"
							+ "1 - Ajouter / Mettre à jour un service##"
							+ "2 - Changer de serveur ftp##"
							+ "3 - Démarrer / Arrêter un service##"
							+ "4 - Supprimer un service##"
							+ "5 - Quitter";
					out.println(msg);
					String choix = in.readLine();
					try {
						if (choix.equals("1")) {
							msg = "Quelle classe voulez-vous charger ?";
							out.println(msg);
							msg = "";
							// charger la classe et la déclarer au ServiceRegistry
							URL[] tabURL={new URL(url)};
							URLClassLoader urlcl = new URLClassLoader(tabURL);
							Class<? extends Runnable> classeChargée;
							classeChargée = (Class<? extends Service>) urlcl.loadClass(in.readLine());
							msg += classeChargée.getName()+"##";
							ServiceRegistry.addService(classeChargée, login);
							msg += "Classe chargée.##";
							urlcl.close();
							System.gc(); //Eventuellement pour décharger des classes
						}
						else if (choix.equals("2")) {
							msg = "Nouvelle URL :";
							out.println(msg);
							url = in.readLine();
							ProgList.changeURL(login, url);
							msg = "URL changée !##";
						}
						else if (choix.equals("3")) {
							msg = ServiceRegistry.toStringue()+"##Tapez le numéro de service désiré :";
							out.println(msg);
							msg = "";
							int choix_service = Integer.parseInt(in.readLine());
							
							boolean etat = ServiceRegistry.changeStateService(choix_service, login);
							if (etat) {
								msg = "Service démarré !##";
							}
							else {
								msg = "Service arrêté !##";
							}
							
						} 
						else if (choix.equals("4")) {
							msg = ServiceRegistry.toStringue()+"##Tapez le numéro de service désiré :";
							out.println(msg);
							int choix_service = Integer.parseInt(in.readLine());
							msg = "";
							ServiceRegistry.delService(choix_service, login);
							msg = "Service supprimé !##";
							System.gc(); //Pour décharger des classes
						} 
						else if (choix.equals("5")) {
							break;
						} 
						else {
							msg = "Choix invalide !##";
						}

					} catch (Exception e) {
						msg += e.toString().replace("\n", "##")+"##";
						System.gc();
					}
				}
			} else {
				out.println("Login ou mot de passe incorrect.");
			}
			client.close();

		} catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Fermer la socket quand le service est détruit
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
