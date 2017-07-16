package filemanagerdemo;

import java.io.File;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 *
 * @author fakelake
 */
public class FileSystemTree {
    ObjectProperty selectedItem = new SimpleObjectProperty();
    ObjectProperty previousSelectedItem = new SimpleObjectProperty();
    
    Callback<TreeView<File>, TreeCell<File>> getCellFactory = new Callback<TreeView<File>, TreeCell<File>>() {
        @Override
        public TreeCell<File> call(TreeView<File> treeView) {
            TreeCell<File> cell = new TreeCell<>();
            
            // различные иконки для айтемов дерева
            cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    cell.setText(null);
                    cell.setGraphic(null);
                    cell.setOnMouseEntered(e -> {
                        cell.setCursor(Cursor.DEFAULT);
                    }); 
                } else {
                    cell.setText(newItem.toString());
                    cell.setGraphic(cell.getTreeItem().getValue().isFile()
                        ? new ImageView(new Image("icons/file-icon.png"))
                        : cell.getTreeItem().isExpanded()
                            ? new ImageView(new Image("icons/open-folder.png"))
                            : new ImageView(new Image("icons/close-folder.png")));
                    // эффект при наведении мыши
                    cell.setOnMouseEntered(e -> {
                        if (cell.getTreeItem() != null) {
                            if (!cell.getStyleClass().contains("cellClicked")) {
                                cell.getStyleClass().add("cellHover");
                            }
                            cell.setCursor(Cursor.HAND);
                        }
                    });
                }
            });

            cell.setOnMouseExited(e -> {
                if (cell.getTreeItem() != null) {
                    cell.getStyleClass().remove("cellHover");
                }
            });

            // эффект нажатия мыши
            cell.setOnMouseClicked(e -> {
                if (cell.getTreeItem() != null) {
                    selectedItem.set(cell.getTreeItem());

                    if (cell.getTreeItem().isExpanded()) {
                        selectedItem.set(previousSelectedItem.get());
                        treeView.getSelectionModel().select((TreeItem)selectedItem.get());
                    } else {
                       selectedItem.set(cell.getTreeItem());
                    }

                    previousSelectedItem.set(selectedItem.get());

                    cell.getStyleClass().remove("cellHover");
                }
            });
            
            return cell;
        }
    };
    
    protected TreeItem<File> createNode(final File f) {
        return new TreeItem<File>(f) {   
            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;
            
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
