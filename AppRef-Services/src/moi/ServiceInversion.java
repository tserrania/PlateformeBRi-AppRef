package moi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bri.Service;

public class ServiceInversion implements Service {

	private Socket client;
	
	public ServiceInversion(Socket client) {
		this.client = client;
	}
	/**
	 * La méthode run pour la gestion des threads
	 */
	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			out.println("Tapez la chaîne à inverser :");
			String line = in.readLine();
			StringBuilder sb = new StringBuilder(line);
			sb = sb.reverse();
			out.println(sb);
			in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			client.close();
		} catch (IOException e) {
		}
	}
	
	public static String toStringue() {
		return "Service d'inversion de chaîne";
	}
}
