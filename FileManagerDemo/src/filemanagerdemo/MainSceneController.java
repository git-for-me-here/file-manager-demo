package filemanagerdemo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author fakelake
 */
public class MainSceneController implements Initializable {
    private Stage stage;
    private FileSystemTree fsTree;
    private FileSystemTable fsTable;
    private FileSystemPath fsPath;
    
    @FXML
    private Pane controlPane;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnResize;
    @FXML
    private Button btnHide;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnNext;
    @FXML
    private TreeView fileSystemTree;
    @FXML
    private TableView fileSystemTable;
    @FXML
    private TableColumn iconCol;
    @FXML
    private TableColumn nameCol;
    @FXML
    private TableColumn modifiedCol;
    @FXML
    private TableColumn typeCol;
    @FXML
    private TableColumn sizeCol;
    @FXML
    private TextField pathTextField;
    
    @FXML
    private void handleButtonClose(ActionEvent event) {
        stage.close();
    }
    
    @FXML
    private void handleButtonResize(ActionEvent event) {
        stage.setFullScreen(!stage.isFullScreen());
    }
    
    @FXML
    private void handleButtonHide(ActionEvent event) {
        stage.setIconified(true);
    }
    
    @FXML
    private void handleButtonBack(ActionEvent event) {
    }
    
    @FXML
    private void handleTreePressed(MouseEvent e) {
        if (e.getTarget() instanceof TreeCell ||
                e.getTarget() instanceof Text) {
            fsTable.loadData(fsTree.getSelectedItemValue());
            fsPath.setPath(fsTree.getSelectedItemValue());
        }
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
        
        // Эффект для кнопок перемещения по просмотренным директориям
        btnBack.hoverProperty().addListener((observable, oldValue, isHover) -> {
            btnBack.setEffect(isHover ? new  Bloom() : null );
        });
        btnNext.hoverProperty().addListener((observable, oldValue, isHover) -> {
            btnNext.setEffect(isHover ? new  Bloom() : null );
        });
        
        // Построение дерева файловой системы
        fsTree = new FileSystemTree(fileSystemTree);
        
        // Таблица файловой системы
        fsTable = new FileSystemTable(fileSystemTable);
        iconCol.prefWidthProperty().bind(fileSystemTable.widthProperty().multiply(0.025));
        nameCol.prefWidthProperty().bind(fileSystemTable.widthProperty().multiply(0.34));
        modifiedCol.prefWidthProperty().bind(fileSystemTable.widthProperty().multiply(0.15));
        typeCol.prefWidthProperty().bind(fileSystemTable.widthProperty().multiply(0.15));
        sizeCol.prefWidthProperty().bind(fileSystemTable.widthProperty().multiply(0.34)); 
        
        // Отображение пути до текущей директории
        fsPath = new FileSystemPath(pathTextField);
    }
    
    void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    private static class Coordinates {
        public double x;
        public double y;
    }
}
