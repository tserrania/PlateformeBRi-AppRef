package bri;

/**
 * Une encapsulation d'un service avec son état (démarré ou non)
 * @author tyefen
 *
 */
public class ServiceEtat {
	private Class<?> service;
	private boolean actif;
	public ServiceEtat(Class<?> service) {
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
