package lui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bri.Service;

public class ServiceRandom implements Service {

	private Socket client;
	
	public ServiceRandom(Socket client) {
		this.client = client;
	}
	/**
	 * La méthode run pour la gestion des threads
	 */
	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			out.println("Tapez la chaine à mélanger :");
			String line = in.readLine();
			List<Character> caracteres = new ArrayList<>();
			for (int i = 0; i<line.length(); ++ i) {
				caracteres.add(line.charAt(i));
			}
			
			Collections.shuffle(caracteres);
			line = "";
			for (Character c : caracteres) {
				line += c;
			}
			
			out.println(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String toStringue() {
		return "Service de mélange des caractères d'une chaîne";
	}
}
