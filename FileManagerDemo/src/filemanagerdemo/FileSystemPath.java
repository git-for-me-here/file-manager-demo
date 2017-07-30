package filemanagerdemo;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author fakelake
 */
public class FileSystemPath {
    private TextField pathTextField;
    private String currentPath = null;
    private boolean flag = false;
    
    public FileSystemPath(TextField pathTextField) {
        pathTextField.setFocusTraversable(false);
        
        pathTextField.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (isFocused) {
                currentPath = pathTextField.getText();
                pathTextField.selectAll();
                flag = false;
            } else {
                pathTextField.setText(currentPath);
                flag = true;
            }
        }); 
        
        pathTextField.setOnMouseClicked((MouseEvent event) -> {
            if (!pathTextField.getText().isEmpty()) {
                flag = !flag;
                if (flag) {
                    pathTextField.selectAll();
                } else {
                    pathTextField.deselect();
                }
            }
        });
        
        this.pathTextField = pathTextField;
    }
    
    public String getPath() {
        return pathTextField.getText();
    }
    
    public void setPath(String path) {
        this.pathTextField.setText(path);
    }
}
