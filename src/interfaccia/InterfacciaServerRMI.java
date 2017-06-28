package interfaccia;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfacciaServerRMI extends Remote {
	public String createArchieESalvataggio(String tableName, float minSup, float minConf, String nameFile) throws RemoteException;
	public String carica(String nameFile) throws RemoteException;
	 
}

