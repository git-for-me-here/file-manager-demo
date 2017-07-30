package filemanagerdemo;

import filemanagerdemo.FileSystemTable.FileMetaData;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.effect.Bloom;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author fakelake
 */
public class MainSceneController implements Initializable {
    private Stage stage;
    private FileSystemTree fsTree;
    private FileSystemTable fsTable;
    private FileSystemPath fsPath;
    private ListOfMarkedDirectories listMarkDir;
    
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
        String path = listMarkDir.getPrevious();
        
        //fsTree.setSelectedItem(path);
        fsTable.loadData(path);
        fsPath.setPath(path);
        
        listMarkDir.indexDecrement();
    }
    
    @FXML
    private void handleButtonNext(ActionEvent event) {
        String path = listMarkDir.getNext();
        
        //fsTree.setSelectedItem(path);
        fsTable.loadData(path);
        fsPath.setPath(path);
        
        listMarkDir.indexIncrement();
    }
    
    @FXML
    private void handleTreeMousePressed(MouseEvent e) {
        if ((e.getTarget() instanceof TreeCell ||
                e.getTarget() instanceof Text) &&
                !e.getTarget().toString().contains("'null'")) {
            if (listMarkDir.getCurrentIndex() < 0) {
                listMarkDir.clear();
            }
            
            fsTable.loadData(fsTree.getSelectedItemValue());
            fsPath.setPath(fsTree.getSelectedItemValue());
            listMarkDir.addUnique(fsTree.getSelectedItemValue());
        }
    }
    
    @FXML
    private void handleTfKeyPressed(KeyEvent e) throws IOException {
        if (e.getCode() == KeyCode.ENTER) {
            String path = fsPath.getPath();
            File file = new File(path);
            
            if (file.exists()) {
                fsTable.loadData(path);
                fileSystemTable.requestFocus();
            } else {
                String error = "Не удается найти \"" + path +  "\" . Проверьте, "
                   + " правильно ли указан путь до файла или директории.";
                showError(error);
            }
            
            fsPath.setPath(path);
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
        
        fileSystemTable.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 2) {
                if (fileSystemTable.getSelectionModel().getSelectedItem() != null) {
                    fileSystemTree.getSelectionModel().clearSelection();
                    
                    int selectedRow = fileSystemTable.getSelectionModel().getSelectedIndex();
                    FileMetaData selectedItem = (FileMetaData)fileSystemTable.getSelectionModel().getSelectedItem();
                    String path = selectedItem.getPath();
                    File file = new File(path);
                    
                    if (file.isDirectory()) {
                        fsPath.setPath(path);
                        fsTable.loadData(path);
                        listMarkDir.addUnique(path);
                    } else {
                        try {
                            Desktop.getDesktop().open(file);
                        } catch (IOException ex) {
                            String error = "Не могу открыть файл \"" + selectedItem.getName() + "\"";
                            try {
                                showError(error);
                            } catch (IOException ex1) { }
                        }
                    }
                }
            }
        });
        
        // Отображение пути до текущей директории
        fsPath = new FileSystemPath(pathTextField);
        
        pathTextField.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (isFocused) {
                fileSystemTree.getSelectionModel().clearSelection();
            } 
        }); 
        
        // Список куда попадают пути до просмотренных директорий
        listMarkDir = new ListOfMarkedDirectories();
        
        // Слушатель для активации кнопок перемещения по просмотренным директориям
        listMarkDir.indexProperty().addListener((observable, oldValue, newValue) -> {
            btnBack.setDisable(newValue.intValue() < 0);
            btnNext.setDisable(newValue.intValue() >= listMarkDir.size()-1);
        });
    }
    
    void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }
    
    private void showError(String error) throws IOException {
        Stage eStage = new Stage();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("errorDialogScene.fxml"));
        Parent root = (Parent)loader.load();

        ErrorDialogSceneController controller = (ErrorDialogSceneController)loader.getController();
        controller.setStage(eStage);
        controller.setError(error);
        
        Scene scene = new Scene(root, Color.TRANSPARENT);
        scene.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                eStage.close();
            }
        });
        
        eStage.setScene(scene);
        eStage.initStyle(StageStyle.TRANSPARENT);
        eStage.initModality(Modality.WINDOW_MODAL);
        eStage.initOwner(stage);
        eStage.show();
    }

    public static class Coordinates {
        public double x;
        public double y;
    }
}
