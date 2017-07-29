package filemanagerdemo;

import javafx.scene.control.TextField;

/**
 *
 * @author fakelake
 */
public class FileSystemPath {
    private TextField pathTextField;
    
    public FileSystemPath(TextField pathTextField) {
        this.pathTextField = pathTextField;
    }
    
    public void setPath(String path) {
        this.pathTextField.setText(path);
    }
}
