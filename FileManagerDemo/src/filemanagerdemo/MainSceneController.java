package filemanagerdemo;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author fakelake
 */
public class MainSceneController implements Initializable {
    private Stage stage;
    private FileSystemTree fsTree;
    private TreeItem selectedItem;
    
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
        // работаем только, если было выделение
        if (!fileSystemTree.getSelectionModel().isEmpty()) {
            //работаем только со значимым item
            if (!e.getTarget().toString().contains("'null'")) {
                // клик по области выделения
                if (e.getTarget() instanceof TreeCell || 
                        e.getTarget() instanceof Text) {
                    // запоминаем выбранный элемент, если он не null
                    if (fileSystemTree.getSelectionModel().getSelectedItem() != null) {
                        selectedItem = (TreeItem) fileSystemTree.getSelectionModel().getSelectedItem();
                    }
                }
                // клик по arrow
                if (e.getTarget() instanceof Group || 
                        e.getTarget() instanceof StackPane) {
                    //System.out.println(e.getTarget());
                    String receivedId = e.getPickResult().getIntersectedNode().getParent().getId() == null 
                            ? e.getPickResult().getIntersectedNode().getParent().getParent().getId()
                            : e.getPickResult().getIntersectedNode().getParent().getId();
                    // если это НЕ предок или НЕ сам выделенный элемент;
                    if (!selectedItem.getValue().toString().contains(receivedId)) {
                        // выделяем нужный элемент
                        if (fsTree.getParentOfSelectedItem(selectedItem).isExpanded()) {
                            fileSystemTree.getSelectionModel().select(selectedItem);
                        } else {
                            fileSystemTree.getSelectionModel().select(fsTree.getParentOfSelectedItem(selectedItem));
                        }
                    }
                    // иначе работаем с ЯЧЕЙКОЙ в FileSystemTree
                }
            }
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
        nameCol.prefWidthProperty().bind(fileSystemTable.widthProperty().multiply(0.35));
        modifiedCol.prefWidthProperty().bind(fileSystemTable.widthProperty().multiply(0.15));
        typeCol.prefWidthProperty().bind(fileSystemTable.widthProperty().multiply(0.15));
        sizeCol.prefWidthProperty().bind(fileSystemTable.widthProperty().multiply(0.35)); 
    }
    
    void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }
    
    /*
     * Устанавливает данные для отображения в таблице файловой системы
     */
    private void setItemsToFileSystemTable(String path) {
        File file = new File(path);
        ArrayList<File> filesList = new ArrayList<>();
        if (file.isFile()) {
            filesList.add(file);
        } else {
            File[] filesArr = file.listFiles();
            if (filesArr != null) {
                filesList = new ArrayList<>(Arrays.asList(filesArr));
            }
        }

        FileSystemTable fsTable = new FileSystemTable();
        fileSystemTable.setItems(fsTable.getFileMetaData(filesList));
    }

    private static class Coordinates {
        public double x;
        public double y;
    }
}
