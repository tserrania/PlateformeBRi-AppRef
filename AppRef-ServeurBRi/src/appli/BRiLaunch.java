package appli;

import bri.ServeurBRi;
import bri.ServiceBRiProg;
import bri.ServiceBRiAma;

public class BRiLaunch {
	private final static int PORT_PROG = 8686;
	private final static int PORT_AMA = 4200;
	
	public static void main(String[] args) throws Exception{
		new Thread(new ServeurBRi(PORT_PROG, ServiceBRiProg.class)).start();
		new Thread(new ServeurBRi(PORT_AMA, ServiceBRiAma.class)).start();
	}
}
