package filemanagerdemo;

import java.io.File;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author fakelake
 */
public class FileSystemTree {  
    protected TreeItem<File> createNode(final File f) {
        return new TreeItem<File>(f) {   
            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;
            
            @Override
            public <E extends Event> void addEventHandler(EventType<E> eventType,
                    EventHandler<E> eventHandler) {
                super.setGraphic(super.getValue().isFile()
                        ? new ImageView(new Image("icons/file-icon.png"))
                        : new ImageView(new Image("icons/close-folder.png")));
                super.expandedProperty().addListener(
                        (ObservableValue<? extends Boolean> ov,
                                Boolean t, Boolean t1) -> {
                    super.setGraphic(t1 
                            ? new ImageView(new Image("icons/open-folder.png"))
                            : new ImageView(new Image("icons/close-folder.png")));
                });          
            }
            
            @Override
            public ObservableList<TreeItem<File>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }
            
            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    File f = (File) getValue();
                    isLeaf = f.isFile();
                }
                return isLeaf;
            }

            private ObservableList<TreeItem<File>> buildChildren(
                    TreeItem<File> TreeItem) {                        
                File f = TreeItem.getValue();
                if (f == null) {
                    return FXCollections.emptyObservableList();
                }
                if (f.isFile()) {
                    return FXCollections.emptyObservableList();
                }
                File[] files = f.listFiles();
                if (files != null) {  
                    ObservableList<TreeItem<File>> children = FXCollections
                        .observableArrayList();
                    for (File childFile : files) {
                        children.add(createNode(childFile));
                    }
                    return children;
                }
                return FXCollections.emptyObservableList();
            }
        };
    }
}
