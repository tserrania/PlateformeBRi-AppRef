package eux;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import bri.Service;

public class ServiceAnalyseXML implements Service {

	private Socket client;
	
	public ServiceAnalyseXML(Socket client) {
		this.client = client;
	}
	/**
	 * La méthode run pour la gestion des threads
	 */
	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);

			try {
				out.println("Quel fichier XML voulez-vous analyser ?");
				String filename = in.readLine();
				String msg = "Il n'y a aucune erreur dans le fichier XML '"+filename+"'.";
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

				DocumentBuilder builder = factory.newDocumentBuilder();
				try {
					builder.parse(new URL(filename).openStream());
				} catch (SAXException e) {
					msg = "Il y a des erreurs dans le fichier XML '"+filename+"'.\n"+e.toString().split("org.xml.sax.SAXParseException; ")[1];
				}
				
				out.println("A qui voulez-vous envoyer le rapport ?");
				final String to = in.readLine();
				final String from = "pwsbiblio@gmail.com";
				final String password = "@56*cdD&#asd39pthl.fd71#";

				Properties prop = new Properties();
				prop.put("mail.smtp.host", "smtp.gmail.com");
				prop.put("mail.smtp.port", "587");
				prop.put("mail.smtp.auth", "true");
				prop.put("mail.smtp.starttls.enable", "true"); //TLS

				Session session = Session.getInstance(prop,
						new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(from, password);
					}
				});

				try {

					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress(from));
					message.setRecipient(
							Message.RecipientType.TO,
							new InternetAddress(to)
							);
					message.setSubject("Bonjour");
					message.setText(msg);

					Transport.send(message);
					out.println("Rapport envoyé !");
				} catch (MessagingException e) {
					out.println("Erreur lors de l'envoi...");
					e.printStackTrace();
				}
			} catch (ParserConfigurationException e) {
				out.println("Erreur du service...");
			}
			in.readLine();
		} catch (IOException e) {

		} 
		try {
			client.close();
		} catch (IOException e) {
		}
	}

	public static String toStringue() {
		return "Service d'analyse de fichier XML";
	}
}
