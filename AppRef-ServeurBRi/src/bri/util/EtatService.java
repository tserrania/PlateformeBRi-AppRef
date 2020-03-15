package bri.util;

/**
 * Une encapsulation d'un service avec son �tat (d�marr� ou non)
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
