package filemanagerdemo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author fakelake
 */
public class MainSceneController implements Initializable {
    private Stage stage;
    
    @FXML
    private Pane controlPane;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnResize;
    @FXML
    private Button btnHide;
    
    @FXML
    private void handleButtonClose(ActionEvent event) {
        stage.close();
    }
    
    @FXML
    private void handleButtonHide(ActionEvent event) {
        stage.setIconified(true);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Перемещение окна  
        Coordinates dragDelta = new Coordinates();
        
        controlPane.setOnMousePressed((MouseEvent mouseEvent) -> {
            dragDelta.x = stage.getX() - mouseEvent.getScreenX();
            dragDelta.y = stage.getY() - mouseEvent.getScreenY();
        });
        controlPane.setOnMouseDragged((MouseEvent mouseEvent) -> {
            stage.setX(mouseEvent.getScreenX() + dragDelta.x);
            stage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        
        // Эффект для кнопок управления окном 
        btnClose.setEffect(new Bloom());
        btnResize.setEffect(new Bloom());
        btnHide.setEffect(new Bloom());
    }

    void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    private static class Coordinates {
        public double x;
        public double y;
    }
}
