package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * Ce client se connecte � un serveur dont le protocole est 
 * menu-choix-question-r�ponse client-r�ponse service
 * il n'y a qu'un �change (pas de boucle)
 * la r�ponse est saisie au clavier en String
 */
class AppliAma {
		private final static int PORT_SERVICE = 4200;
		private final static String HOST = "localhost"; 
	
	public static void main(String[] args) {
		Socket s = null;		
		try {
			s = new Socket(HOST, PORT_SERVICE);

			BufferedReader sin = new BufferedReader (new InputStreamReader(s.getInputStream ( )));
			PrintWriter sout = new PrintWriter (s.getOutputStream ( ), true);
			BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));			
		
			System.out.println("Connect� au serveur " + s.getInetAddress() + ":"+ s.getPort());
			
			String line;
		// menu et choix du service
			line = sin.readLine();
			System.out.println(line.replaceAll("##", "\n"));
		// saisie/envoie du choix
			sout.println(clavier.readLine());
			
			while (true) {
				// r�ception/affichage de la question
				String lnnext = sin.readLine();
				if (lnnext==null) {break;}
				System.out.println(lnnext.replaceAll("##", "\n"));
				// saisie clavier/envoi au service de la r�ponse
				sout.println(clavier.readLine());
			}
		}
		catch (IOException e) { System.err.println("Fin de la connexion"); }
		// Refermer dans tous les cas la socket
		try { if (s != null) s.close(); } 
		catch (IOException e2) { ; }		
	}
}
