package login;

import java.util.List;
import java.util.Vector;

/**
 * Classe gérant la liste des programmeurs
 * @author tyefen
 *
 */
public class ProgList {
	private static List<Prog> progs = new Vector<>(); //La liste des programmeurs
	static {
		progs.add(new Prog("moi", "1234", "ftp://localhost/classes/"));
		progs.add(new Prog("lui", "5678", "ftp://localhost/classes/"));
		progs.add(new Prog("eux", "9101112", "ftp://localhost/classes/"));
		progs.add(new Prog("brette", "abcd", "ftp://localhost/classes/"));
	}
	
	/**
	 * 
	 * @param login Le login du programmeur
	 * @param pass Le mot de passe du programmeur
	 * @return l'url du serveur ftp du programmeur (ou null si la connexion a échouée)
	 */
	public static String verifLogin(String login, String pass) {
		for (Prog p : progs) {
			if (login.equals(p.getLogin()) && pass.equals(p.getPass())) {
				return p.getFtpurl();
			}
		}
		return null;
	}

	public static void changeURL(String login, String url) {
		for (Prog p : progs) {
			if (login.equals(p.getLogin())) {
				p.setFtpurl(url);
			}
		}
	}
}
