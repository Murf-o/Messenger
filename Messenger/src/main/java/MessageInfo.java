import java.io.Serializable;
import java.util.ArrayList;

public class MessageInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	public String message;
	public ArrayList<Integer> specClients;
	
	public MessageInfo(String message) {
		this.message = message;
		this.specClients = null;
	}
	public MessageInfo(String message, ArrayList<Integer> specClients) {
		this.message = message;
		this.specClients = specClients;
	}
	
}