package filemanagerdemo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author fakelake
 */
public class FileManagerDemo extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainScene.fxml"));
        Parent root = (Parent)loader.load();
        MainSceneController controller = (MainSceneController)loader.getController();
        controller.setStage(primaryStage); 
        
        Scene scene = new Scene(root, Color.TRANSPARENT);
        
        // Application icons
        Image image = new Image("/icons/appIcon.png");
        primaryStage.getIcons().addAll(image);
        
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
