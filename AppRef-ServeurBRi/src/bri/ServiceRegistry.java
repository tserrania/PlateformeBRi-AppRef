package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

/**
 * cette classe est un registre de services
 * partagée en concurrence par les clients et les "ajouteurs" de services,
 * un Vector pour cette gestion est pratique
 * @author tyefen
 *
 */
public class ServiceRegistry {

	static {
		servicesClasses = new Vector<Class<?>>();
	}
	private static List<Class<?>> servicesClasses;

	/**
	 * Contrôle de la norme BLTi
	 * @param service La classe service
	 * @param login Le login de celui qui a créée le service
	 * @throws Exception si le service n'est pas valique
	 */
	private static void validService(Class<?> service, String login) throws Exception {

		// vérifier la conformité par introspection
		// si non conforme --> exception avec message clair
		// si conforme, ajout au vector
		
		if (!service.getPackage().getName().equals(login)) {
			throw new Exception("Le service n'appartient pas au bon package.");
		}
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
		synchronized (servicesClasses) {
			Class<?> cr = null;
			for (Class<?> c : servicesClasses) {
				if (c.getName().equals(service.getName())) {
					cr = c;
					break;
				}
			}
			servicesClasses.remove(cr);
		}
		servicesClasses.add(service);
	}
	
	
	/**
	 * Supprime le service désiré
	 * @param numService
	 * @throws Exception
	 */
	public static void delService(int numService) throws Exception {
		servicesClasses.remove(numService-1);
	}
	
	/**
	 * Récupérer un service
	 * @param numService Le numéro du service
	 * @return La classe de service (numService -1)	
	 */
	public static Class<?> getServiceClass(int numService) {
		return servicesClasses.get(numService-1);
	}
	
	/**
	 * @return une chaine représentant la liste des services existants
	 */
	public static String toStringue() {
		String result = "Activités présentes : ##";
		int i = 0;
		for (Class<?> c : servicesClasses)  {
			try {
				result += (i+1)+" - "+c.getMethod("toStringue").invoke(null)+"##";
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			++i;
		}
		return result;
	}

}
