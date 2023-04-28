import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ClientController implements Initializable{
	@FXML
	private BorderPane root;
	
	@FXML
	private ListView<String> list;
	
	@FXML
	private VBox vb;
	
	@FXML
	private Button sendB;
	
	@FXML
	private TextField tf1;
	
	@FXML
	private ListView<String> cList;
	
	@FXML
	private Text cLTxt;
	
	@FXML
	private Text clientSpecTxt;
	
	@FXML
	private TextField tfCspec;	//send to specific clients
	
	@FXML
	private Text mTxt;
	
	private Client clientConnection;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		root.getStylesheets().add("/CSS/client.css");
		clientConnection = new Client(data->{
			Platform.runLater(()->{list.getItems().add(data.toString());
								  });
								  
		}, dataTwo-> {Platform.runLater(()->{
			
			cList.getItems().add(dataTwo.toString());
			});
		}, dataThree->{Platform.runLater(()->{
				cList.getItems().remove("client: #" + dataThree.toString());
			});
			
		}
				);
		clientConnection.start();
	}
	
	
	//clicked sendB button, send message
	public void sendMessage(ActionEvent e) {
		if(tfCspec.getText().isEmpty())
			clientConnection.sendToAll(tf1.getText());
		else {
			//parse textfield and get clients to send info to
			String str = tfCspec.getText();
			int size = str.length();
			ArrayList<Integer> sendingToL = new ArrayList<Integer>();
			
			for(int i = 0; i < size; ++i) {
				if(Character.isDigit(str.charAt(i))) {sendingToL.add(Integer.parseInt(str.charAt(i) +""));}
			}
			//have client send data to server
			str = tf1.getText();
			clientConnection.sendToSpec(sendingToL, str);
		}
		tf1.clear();
	}
	
}