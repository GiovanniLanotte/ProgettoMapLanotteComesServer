package ServerRMI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;

import data.Data;
import data.EmptySetException;
import database.DatabaseConnectionException;
import database.NoValueException;
import interfaccia.InterfacciaServerRMI;
import mining.AssociationRule;
import mining.AssociationRuleArchive;
import mining.AssociationRuleMiner;
import mining.FrequentPattern;
import mining.FrequentPatternMiner;
import mining.OneLevelPatternException;

public class ServerRMI extends UnicastRemoteObject implements InterfacciaServerRMI {
	
	public ServerRMI() throws RemoteException {
		
	}

	public String createArchieESalvataggio(String tableName, float minSup, float minConf, String nomeFile) throws RemoteException{	
		AssociationRuleArchive archive;
		try{
		archive = new AssociationRuleArchive();
		Data trainingSet=new Data(tableName);
		archive=new AssociationRuleArchive();
		LinkedList<FrequentPattern> outputFP=FrequentPatternMiner.frequentPatternDiscovery(trainingSet,minSup);
		Iterator<FrequentPattern> it=outputFP.iterator();
		while(it.hasNext()){
			FrequentPattern FP=it.next();
			archive.put(FP);
			LinkedList<AssociationRule> outputAR=null;
			try{
				outputAR = AssociationRuleMiner.confidentAssociationRuleDiscovery(trainingSet,FP,minConf);
				Iterator<AssociationRule> itRule=outputAR.iterator();
				while(itRule.hasNext()){
					archive.put(FP,itRule.next());
				}
			}catch(OneLevelPatternException e){}
		}
		}catch (EmptySetException e) {
			return "Errore durante il calcolo della frequenza";
		}
		catch (DatabaseConnectionException | SQLException | NoValueException e1) {
		return "Errore connessione al Database, cambiare il nome della tabella";
	    }
		try {
			archive.salva(nomeFile);
		} catch (IOException e) {
			return "Errore durante il salvataggio";
		}
		return archive.toString();
	}
	
	public String carica(String nomeFile) throws RemoteException{
		AssociationRuleArchive archive;
		try {
			archive= AssociationRuleArchive.carica(nomeFile);
			return archive.toString();
		} catch (ClassNotFoundException | IOException e) {
		return "Errore durante il caricamento del file";
	    }
		
	}

}
