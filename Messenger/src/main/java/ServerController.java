import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class ServerController implements Initializable{
	@FXML
	private BorderPane root; 
	
	@FXML
	private Text txt1;
	
	@FXML
	private ListView<String> list;
	
	
	private Server serverConnection;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		root.getStylesheets().add("/CSS/server.css");
		
		
		serverConnection = new Server(data -> {
			Platform.runLater(()->{
				list.getItems().add(data.toString());
			});
		});
	}
	
	
	
	
	
}