import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;



public class Client extends Thread{

	
	Socket socketClient;
	boolean firstRec = true;	//first message to receive from server, false after we rec message
	
	ObjectOutputStream out;
	ObjectInputStream in;
	
	private Consumer<Serializable> callback;
	private Consumer<Serializable> callbackCL;
	private Consumer<Serializable> callListRemove;
	
	Client(Consumer<Serializable> call, Consumer<Serializable> callList, Consumer<Serializable> callListRemove){
	
		this.callback = call;
		this.callbackCL = callList;
		this.callListRemove = callListRemove;
	}
	
	public void run() {
		
		try {
		socketClient= new Socket("127.0.0.1",5555);
	    out = new ObjectOutputStream(socketClient.getOutputStream());
	    in = new ObjectInputStream(socketClient.getInputStream());
	    socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {callback.accept("Could not connect to server");return;}
		
		//get list of clients, as of right now, from server
		ArrayList<Integer> cList = null;
		try{cList = (ArrayList<Integer>) in.readObject();}
		catch(Exception k) {System.out.println("Failed to receive client List");}
		
		int size = cList.size();
		callbackCL.accept("client: #" + cList.get(size-1) + " (You)");
		for(int i = 0; i < size-1;++i) {
			callbackCL.accept("client: #" + cList.get(i));
		}
		while(true) {
			 
			try {
			String message = in.readObject().toString();
			//parse message
			//if new client connected, update list of clients
			if(message.length() >= 30 && message.substring(0, 30).equals("new client on server: client #")) {
				if(firstRec) {firstRec = false;callback.accept(message); continue;}
				callbackCL.accept("client: #" + message.substring(30));
			}
			//else if client disconnected
			else if(message.length() >= 20 && message.substring(message.length()-20, message.length()).equals("has left the server!")) {
				//parse and get number of client that left
				//"Client #"+count+" has left the server!"
				int num = Integer.parseInt(message.substring(8, message.length()-21));
				callListRemove.accept(num);
			}
			
			callback.accept(message);
			}
			catch(Exception e) {System.out.println(e.getMessage());break;}
		}
    }
	
	public void sendToAll(String data) {
		MessageInfo mInfo = new MessageInfo(data);
		try {
			out.writeObject(mInfo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendToSpec(ArrayList<Integer> specList, String data) {
		MessageInfo mInfo = new MessageInfo(data, specList);
		try {
			out.writeObject(mInfo);
		}
		catch(IOException e) {System.out.println(e.getMessage());e.printStackTrace();}
	}


}
