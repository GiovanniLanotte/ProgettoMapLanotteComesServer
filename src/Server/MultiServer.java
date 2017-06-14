package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MultiServer {
	public static final int PORT = 8080;
	private ServerSocket s;
public MultiServer()   {
	try {
		s= new ServerSocket(PORT);
	
	while(true){
		Socket socket= s.accept();
		ServerOneClient client= new ServerOneClient(socket);
	}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public static void main(String[] args){
	new MultiServer();
}
}
