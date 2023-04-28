import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainController implements Initializable{
	@FXML
	private BorderPane root;
	
	@FXML
	private Button serverB;
	
	@FXML
	private Button clientB;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		root.getStylesheets().add("/CSS/main.css");
		
	}
	
	public void createServer(ActionEvent e) throws IOException{
		Stage origStage = (Stage)((Node)e.getSource()).getScene().getWindow();
		origStage.setTitle("This is the server");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/serverMessages.fxml"));
		Parent rootServer;
		rootServer = loader.load();
		root.getScene().setRoot(rootServer);
		
		//System.out.println("chckpt 1");
		//loader.setController(new ServerController());
		//System.out.println("chckpt 2");
		
		//Scene serverScene = new Scene(rootServer, 700, 700);
		
		//origStage.setScene(serverScene);
		//origStage.show();
	}
	
	public void createClient(ActionEvent e) throws IOException{
		Stage origStage = (Stage)((Node)e.getSource()).getScene().getWindow();
		origStage.setTitle("This is a client");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clientMessages.fxml"));
		Parent rootServer;
		rootServer = loader.load();
		root.getScene().setRoot(rootServer);
		
	}
}