package bri.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import bri.Service;

/**
 * cette classe est un registre de services
 * partagée en concurrence par les clients et les "ajouteurs" de services,
 * un Vector pour cette gestion est pratique
 * @author tyefen
 *
 */
public class ServiceRegistry {

	private static List<EtatService> servicesClasses;

	static {
		servicesClasses = new Vector<EtatService>();
	}
	

	/**
	 * Contrôle que le service appartient au package correspondant au login
	 * @param service La classe service
	 * @param login Le login de celui qui effectue l'action sur le service
	 * @throws Exception si le service n'appartient pas au bon package
	 */
	private static void validLogin(Class<?> service, String login) throws Exception {
		if (!service.getPackage().getName().equals(login)) {
			throw new Exception("Le service n'appartient pas au bon package.");
		}
	}

	/**
	 * Contrôle de la norme BLTi
	 * @param service La classe service
	 * @param login Le login de celui qui a créée le service
	 * @throws Exception si le service n'est pas valide
	 */
	private static void validService(Class<?> service, String login) throws Exception {

		// vérifier la conformité par introspection
		// si non conforme --> exception avec message clair
		// si conforme, ajout au vector
		validLogin(service, login);
		
		boolean ok = false;
		for (Class<?> c : service.getInterfaces()) {
			if (c==Service.class) {
				ok = true;
			}
		}
		if (!ok) {
			throw new Exception("Ce n'est pas un service.");
		}
		if (Modifier.isAbstract(service.getModifiers())) {
			throw new Exception("Le service ne doit pas être abstract.");
		}
		if (!Modifier.isPublic(service.getModifiers())) {
			throw new Exception("Le service doit être public.");
		}
		Constructor<?> m;
		try {
			m = service.getConstructor(Socket.class);
		}
		catch (Exception e) {
			throw new Exception("Le service n'a pas de constructeur prenant une socket.");
		}
		try {
			m.newInstance(new Socket());
		}
		catch (Exception e) {
			throw new Exception("Le service ne peut pas s'initialiser.");
		}
		if (m.getExceptionTypes().length>0) {
			throw new Exception("Le service a un constructeur pouvant générer des exceptions.");
		}
		if (!Modifier.isPublic(m.getModifiers())) {
			throw new Exception("Le service n'a pas de constructeur public.");
		}

		ok = false;
		for(Field f : service.getDeclaredFields()) {
			if (f.getType()==Socket.class) {
				if (Modifier.isPrivate(f.getModifiers())) {
					ok = true;
				}
			}
		}

		if (!ok) {
			throw new Exception("Il n'y a pas de champ Socket private.");
		}

		Method toStringue;
		try {
			toStringue = service.getMethod("toStringue");
		}
		catch (Exception e) {
			throw new Exception("Le service n'a pas de méthode toStringue.");
		}
		if (!Modifier.isPublic(toStringue.getModifiers())) {
			throw new Exception("La méthode toStringue n'est pas public.");
		}
		if (!Modifier.isStatic(toStringue.getModifiers())) {
			throw new Exception("La méthode toStringue n'est pas static.");
		}
		if (toStringue.getReturnType()!=String.class) {
			throw new Exception("La méthode toStringue ne renvoie pas de String.");
		}
		if (toStringue.getExceptionTypes().length>0) {
			throw new Exception("La méthode toStringue peut générer des exceptions.");
		}

	}
	/**
	 * ajoute une classe de service après contrôle de la norme BLTi (ou la remplace si celle-ci existe déjà)
	 * @param service La classe service à ajouter
	 * @param login Le login de celui qui ajoute le service
	 * @throws Exception
	 */
	public static void addService(Class<?> service, String login) throws Exception {
		validService(service, login);
		EtatService sr = null;
		synchronized (servicesClasses) {
			for (EtatService s : servicesClasses) {
				if (s.getService().getName().equals(service.getName())) {
					sr = s;
					break;
				}
			}
		}
		if (sr!=null)  {
			sr.setService(service);
		}
		else {
			servicesClasses.add(new EtatService(service));
		}
	}

	/**
	 * Change l'état (démarré/arrêté) du service désiré
	 * @param numService Le numéro du service en question
	 * @param login Le login de celui qui veut effectuer l'action
	 * @return l'état du service après changement
	 * @throws Exception 
	 */
	public static boolean changeStateService(int numService, String login) throws Exception {
		EtatService s = servicesClasses.get(numService-1);
		validLogin(s.getService(), login);
		if (s.estActif()) {
			s.arreter();
		} else {
			s.demarrer();
		}
		return s.estActif();
	}

	/**
	 * Supprime le service désiré
	 * @param login Le login de celui qui veut effectuer l'action
	 * @param numService Le numéro du service en question
	 * @throws Exception 
	 */
	public static void delService(int numService, String login) throws Exception {
		EtatService s = servicesClasses.get(numService-1);
		validLogin(s.getService(), login);
		servicesClasses.remove(s);
	}
	
	/**
	 * Récupérer un service
	 * @param numService Le numéro du service
	 * @return La classe de service (numService -1)	
	 * @throws Exception 
	 */
	public static Class<?> getServiceClass(int numService) throws Exception {
		EtatService s = servicesClasses.get(numService-1);
		if (!s.estActif()) {
			throw new Exception("Le service est indisponible.");
		}
		return s.getService();
	}

	/**
	 * @return une chaine représentant la liste des services existants
	 */
	public static String toStringue() {
		String result = "Activités présentes : ##";
		int i = 0;
		for (EtatService s : servicesClasses)  {
			try {
				result += (i+1);
				if (!s.estActif()) {
					result += " [Indisponible]";
				}
				result += " - "+s.getService().getMethod("toStringue").invoke(null)+"##";
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			++i;
		}
		return result;
	}

}
