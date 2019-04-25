package minidb.client;
	
import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


public class ClientDialog extends Application {
	
	private Stage primaryStage;
	private Scene scene;
    private VBox rootLayout;
    
    //private Stage 
	
	@Override
	public void start(Stage primaryStage) {	
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("MiniDB Client Dialog");
		this.primaryStage.setWidth(950);
		this.primaryStage.setHeight(625);
		this.primaryStage.setResizable(false);
		initRootLayout();
	}
	
	/**
     * Initializes the root layout.
     */
    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientDialog.class.getResource("MainScene.fxml"));
            rootLayout = (VBox) loader.load();
            rootLayout.setAlignment(Pos.CENTER);
            
            // Show the scene containing the root layout.
            this.scene = new Scene(rootLayout, 950, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            SceneController contentController = loader.getController();
            contentController.setScene(scene);
            contentController.setStage(primaryStage);
            contentController.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	public static void main(String[] args) {
		launch(args);
	}
	
}
