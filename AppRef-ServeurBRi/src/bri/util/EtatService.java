package bri.util;

/**
 * Une encapsulation d'un service avec son état (démarré ou non)
 * @author tyefen
 *
 */
public class EtatService {
	private Class<?> service;
	private boolean actif;
	public EtatService(Class<?> service) {
		this.service = service;
		this.actif = true;
	}

	public Class<?> getService() {
		return service;
	}

	public void setService(Class<?> service) {
		this.service = service;
	}
	
	public boolean estActif() {
		return actif;
	}
	public void demarrer() {
		actif = true;
	}
	public void arreter() {
		actif = false;
	}
	
}
