package eux;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

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

import bri.Service;

public class ServiceAnalyseXML implements Service {

	private static int TAB_LENGTH = 4;

	private Socket client;

	public List<String> searchForErrors(InputStreamReader xmlFile) {
		Scanner sc = new Scanner(xmlFile);
		StringBuffer docBuf = new StringBuffer();
		while (sc.hasNextLine()) {
			docBuf.append(sc.nextLine());
			docBuf.append('\n');
		}
		String doc = docBuf.toString();
		System.out.println(doc);
		sc.close();
		List<String> e = new ArrayList<>();
		int line = 1;
		int column = 1;
		boolean inTag = false;
		boolean inTagName = false;
		boolean tagInfo = false;
		boolean tagClose = false;
		boolean tagOrphean = false;
		String tagName = "";
		List<String> tags = new ArrayList<>();
		for (int i=0; i<doc.length(); ++i) {
			char c = doc.charAt(i);
			if (c=='<'){
				if (inTag) {
					e.add("'<' dans la déclaration d'une balise. ligne "+line+", colonne "+column);
				} else {
					inTag = true;
					inTagName = true;
				}
			} else if (c=='>'){
				if (!inTag) {
					e.add("'>' sans ouverture de balise. ligne "+line+", colonne "+column);
				} else {
					if (tagInfo) {
						if (doc.charAt(i-1)!='?') {
							e.add("La balise info ne se ferme pas correctement. "+line+", colonne "+column);
						}
					} else if (tagClose) {
						if (!tags.get(tags.size()-1).equals(tagName)) {
							e.add("Nom de balise fermante différent de la balise ouvrante. ligne "+line+", colonne "+column);
						}
						tags.remove(tags.size()-1);
					} else if (inTagName && !tagOrphean) {
						tags.add(tagName);
					}
					inTag = false;
					tagInfo = false;
					tagClose = false;
					inTagName = false;
					tagOrphean = false;
					tagName = "";
				}
			} else if (c=='/'){
				if (inTag && !tagInfo) {
					try {
						if (doc.charAt(i-1)=='<') {
							tagClose = true;
						} else if (doc.charAt(i+1)=='>') {
							if (tagClose) {
								e.add("Une balise ne peut être orpheline et fermante. ligne "+line+", colonne "+column);
							} else {
								tagOrphean = true;
							}
						} else {
							e.add("Caractère '/' invalide dans la déclaration d'une balise. ligne "+line+", colonne "+column);
						}
					} catch (Exception e2) {
						e.add("Caractère '/' invalide dans la déclaration d'une balise. ligne "+line+", colonne "+column);
					}
				}
			} else if (c=='?'){
				if (inTag) {
					try {
						if (doc.charAt(i-1)=='<') {
							tagInfo = true;
							inTagName = false;
						} 
					} catch (Exception e2) {
						
					}
				}
			}
			else if (c==' ' || c=='\n' || c=='\t'){
				if (inTag) {
					inTagName = false;
				}
			} else {
				if (inTag) {
					if (inTagName) {
						tagName += c;
					}
				}
			}
			if (c=='\n'){
				++line;
				column = 1;
			} else if (c=='\t') {
				column += TAB_LENGTH;
			}else {
				++column;
			}
		}
		return e;
	}

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
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();

				out.println("Quel fichier XML voulez-vous analyser ?");
				InputStreamReader xmlFile = new InputStreamReader(new URL(in.readLine()).openStream());
				List<String> listE = searchForErrors(xmlFile);
				String msg = "Il y a "+listE.size()+" erreur(s)\n";
				for (String e : listE) {
					msg+= e.toString()+'\n';
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

				} catch (MessagingException e) {
					e.printStackTrace();
				}
				out.println("Rapport envoyé !");
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
