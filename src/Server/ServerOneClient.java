package Server;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;

import data.Data;
import database.DatabaseConnectionException;
import database.NoValueException;
import mining.AssociationRule;
import mining.AssociationRuleArchive;
import mining.AssociationRuleMiner;
import mining.FrequentPattern;
import mining.FrequentPatternMiner;
import mining.OneLevelPatternException;

public class ServerOneClient extends Thread {
	private Socket socie;// Terminale lato server del canale tramite cui avviene lo scambio di oggetti client-server
	private ObjectInputStream in; //flusso di oggetti in input al server.
	private ObjectOutputStream out; //flusso di oggetti in output dal server al client.
	private AssociationRuleArchive archive=null;
public ServerOneClient(Socket s) throws IOException {
	socie=s;
	in= new ObjectInputStream(socie.getInputStream());
	out= new ObjectOutputStream(socie.getOutputStream());
	start();
}
@Override
public void run() {
	System.out.println("Nuovo client connesso");
	
	try{
		while (true) {
			int command=0;
			command = ((Integer)(in.readObject())).intValue();
			switch(command)
			{
			case 1: // Learning a new archive from DB
				try{
					archive=new AssociationRuleArchive();
					
					String tableName=(String)in.readObject();
					Float minSup=(Float)in.readObject();
					Float minConf=(Float)in.readObject();
					Data trainingSet=new Data(tableName);
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
						}
						catch(OneLevelPatternException e){}
					}
				out.writeObject("CON SUCCESSO");
				} 
				catch (DatabaseConnectionException e1) {
					e1.printStackTrace();
				} 
				catch (SQLException e1) {
					out.writeObject("Nome tabella inesistente");
					e1.printStackTrace();
				} 
				catch (NoValueException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				finally{
					out.writeObject(archive.toString());
					break;
				}
			case 2:{ // SAVING ON File
				// TO DO
				String nomeFile= in.readObject().toString();
				try{
				archive.salva(nomeFile);
				out.writeObject("CON SUCCESSO");
				}catch(IOException e){
					out.writeObject("SENZA SUCCESSO");
				}
				break;
				}
			case 3: {// STORING PATTERNS and RULES stored from a FILE
				//TO DO
				try{
				String nomeFile= in.readObject().toString();
				archive= AssociationRuleArchive.carica(nomeFile);
				out.writeObject(archive.toString());
				}catch(IOException e){
					out.writeObject("Nome del file inesistente");
					System.err.println("Nome del file inesistente");
				}
				break;
			}
			default:
				System.out.println("COMANDO INESISTENTE");
			try {
				out.writeObject("COMANDO INESISTENTE");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			}// END SWITCH
		}
	}
	catch(SocketException e){
		return;
	 } 
	catch (IOException e) {
			e.printStackTrace();
	} 
	 catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
		 e.printStackTrace();
			
		}
	 
	finally {
		try {
			socie.close();
		} catch (IOException e) {
			System.out.println("Socket non chiuso!");
		}
	}

		
}



}

