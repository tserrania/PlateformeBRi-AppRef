package bri.services;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bri.Service;
import bri.ServiceBRi;
import bri.util.ServiceRegistry;

/**
 * Le service BRi côté client amateur
 * @author tyefen
 *
 */
public class ServiceBRiAma implements ServiceBRi {

	private Socket client;

	public ServiceBRiAma(Socket socket) {
		client = socket;
	}

	/**
	 * La méthode run pour la gestion des threads
	 */
	public void run() {
		BufferedReader in;
		PrintWriter out;
		try {
			in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			out = new PrintWriter (client.getOutputStream ( ), true);

			try {
				out.println(ServiceRegistry.toStringue()+"##Tapez le numéro de service désiré :");
				int choix = Integer.parseInt(in.readLine());

				// instancier le service numéro "choix" en lui passant la socket "client"
				// invoquer run() pour cette instance ou la lancer dans un thread à part 
				((Service) ServiceRegistry.getServiceClass(choix).getConstructor(Socket.class).newInstance(client)).run();
			}
			catch (Exception e) {
				out.println(e.getMessage());
			}
			System.gc(); //Eventuellement pour décharger des classes
		} catch (IOException e) {
		}

		try {client.close();} catch (IOException e2) {}
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
