import java.rmi.Naming;
import java.rmi.RMISecurityException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import Server.MultiServer;
import ServerRMI.ServerRMI;

public class Main {
	public static void main(String[] args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		LocateRegistry.createRegistry(2004);
		ServerRMI s=  new ServerRMI();
		Naming.bind("//localhost:2004/Server", s);
		System.out.println("Ready...");
		new MultiServer();
	}

}
