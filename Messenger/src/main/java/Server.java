import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;



public class Server{

	int count = 1;	
	Map<Integer, ClientThread> clients = new HashMap<Integer, ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	
	
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
		  
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				
				synchronized(callback){	//synchronize callback accept method
				callback.accept("client has connected to server: " + "client #" + count);
				}
				synchronized(clients) {	//synchronize list of clients, other threads may be trying to read/write from/to it
				clients.put(count, c);	//updating clients'
				}
				c.start();
				
				
				count++;
				
			    }
			}//end of try
				catch(Exception e) {
					synchronized(callback) {//synchronize callback accept method
					callback.accept("Server socket did not launch");
					}
				}
			}//end of while
		}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			//update ALL clients
			public void updateClients(String message) {
				synchronized(clients) {	//synchronize clients list, don't want it being updated while we're reading from it
				for(Integer key: clients.keySet()) {
					ClientThread t = clients.get(key);
					try {
					 t.out.writeObject(message);
					}
					catch(Exception e) {return;}	
					}
				}
			}
			
			//get list of clients in the form of an ArrayList, used to send list of clients
			//to new client connected
			public ArrayList<Integer> getCList(){
				ArrayList<Integer> l = new ArrayList<Integer>();
				synchronized(clients) {
				for(Integer key: clients.keySet()) {
					ClientThread c = clients.get(key);
					l.add(c.count);
				}
				}
				return l;
			}
			public void sendToAll(String data) {
				synchronized(callback) {//synchronize callback accept method
	    		callback.accept("client: " + count + " sent: " + data);
	    		}
		    	updateClients("client #"+count+" said: "+data);
			}
			
			//update only specific clients defined in specList
			public void updateSpecClients(String message, ArrayList<Integer> specList) {
				synchronized(clients) {
				for(Integer i: specList) {
					
					try {
						if(clients.containsKey(i)) {
							ClientThread t = clients.get(i);
							t.out.writeObject(message);
						} 
					}catch(Exception e) {System.out.println(e.getMessage());e.printStackTrace();}
				}
				}
			}
			
			//parses message to send to specific client
			public void sendToSpec(String data, ArrayList<Integer> specList) {
				String cls = "";
				for(Integer i: specList) {
					if(i == count || !clients.containsKey(i)) {continue;}	//i == to the client sending the message, or client does not exist
					cls += i + ", ";
				}
				if(cls.length() == 0) {callback.accept("Failed Whisper from client: #" + count);updateSpecClients("From Server: could not find specific clients to whisper to\n(Cannot whisper to yourself!)", new ArrayList<Integer>(Arrays.asList(count)));return;}	//no clients being sent anything
				cls = cls.substring(0, cls.length()-2);
				synchronized(callback) {//synchronize callback accept method
	    		callback.accept("client: " + count + " whispered to(Client #(s) " + cls + "): " + data);
	    		}
				updateSpecClients("client #"+count+" whispered to (Client #(s) " + cls +"): " + data, specList);
			}
			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
					
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				
				try {
					//give list of clients to client
					ArrayList<Integer> cList = getCList();
					out.writeObject(cList);
				} catch (IOException e1) {
					System.out.println("couldn't send cList to client");
				}
				
				updateClients("new client on server: client #"+count);
					
				 while(true) {
					    try {
					    	MessageInfo mInfo = (MessageInfo) in.readObject();
					    	String data = mInfo.message;
					    	
					    	if(mInfo.specClients == null) {sendToAll(data);}	//send to all
					    	else {mInfo.specClients.add(count);sendToSpec(data, mInfo.specClients);}	//show message to client sending, and send to specific clients
					    	
					    	}
					    catch(Exception e) {
					    	synchronized(clients) {	//removing element from clients, don't want other threads adding/removing/reading from it, or writing to an object we are removing
					    		synchronized(callback) {//synchronize callback accept method
					    		callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    		}
					    	clients.remove(count);	//updating 'clients', don't want other threads trying to access this element while it's being removed
					    	}	//moved clients.remove(this) above updateClients.
					    	updateClients("Client #"+count+" has left the server!");
					    	
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	
