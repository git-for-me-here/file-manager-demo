package filemanagerdemo;

import javafx.scene.control.TextField;

/**
 *
 * @author fakelake
 */
public class FileSystemPath {
    private TextField pathTextField;
    
    public FileSystemPath(TextField pathTextField) {
        pathTextField.setFocusTraversable(false);
        
        this.pathTextField = pathTextField;
        
    }
    
    public String getPath() {
        return pathTextField.getText();
    }
    
    public void setPath(String path) {
        this.pathTextField.setText(path);
    }
}
