package filemanagerdemo;

import java.io.File;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 *
 * @author fakelake
 */
public class FileSystemTree {
    private TreeView treeView;
    private TreeItem selectedItem;
    private ArrayList<TreeDataBinding> treeNodes = new ArrayList();
    
    public FileSystemTree(TreeView treeView) {
        TreeItem<File> root = new TreeItem<>(new File(""));
        root.setExpanded(true);
        
        for (File cat : File.listRoots()) {
            TreeItem<File> item = this.createNode(cat);
            root.getChildren().add(item);
        }

        treeView.setRoot(root);
        treeView.setShowRoot(false);
        treeView.setCellFactory(this.getCellFactory);
        
        treeView.setOnMouseClicked((MouseEvent e) -> {
            // работаем только, если было выделение
            if (!treeView.getSelectionModel().isEmpty()) {
                //работаем только со значимым item
                if (!e.getTarget().toString().contains("'null'")) {
                    // клик по области выделения
                    if (e.getTarget() instanceof TreeCell || 
                            e.getTarget() instanceof Text) {
                        // запоминаем выбранный элемент, если он не null
                        if (treeView.getSelectionModel().getSelectedItem() != null) {
                            this.selectedItem = (TreeItem) treeView.getSelectionModel().getSelectedItem();
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
                            if (getParentOfSelectedItem(selectedItem).isExpanded()) {
                                treeView.getSelectionModel().select(selectedItem);
                            } else {
                                treeView.getSelectionModel().select(getParentOfSelectedItem(selectedItem));
                            }
                        }
                        // иначе работаем с TreeCell
                    }
                }
            }
        });
        
        this.treeView = treeView;
    }
    
    private Callback<TreeView<File>, TreeCell<File>> getCellFactory = new Callback<TreeView<File>, TreeCell<File>>() {
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
                            cell.getStyleClass().add("cellHover");
                            cell.setCursor(Cursor.HAND);
                        }
                    });
                    cell.setId(cell.getText());
                    
                    if (!containsObjectWithId(cell.getId())) {
                        treeNodes.add(new TreeDataBinding(
                            treeView,
                            cell.getTreeItem(),
                            cell,
                            cell.getId()
                        ));                  
                    }
                }
            });
            
            cell.setOnMouseExited(e -> {
                if (cell.getTreeItem() != null) {
                    cell.getStyleClass().remove("cellHover"); 
                }
            });

            cell.setOnMouseClicked(e -> {
                // работаем только со значимой ячейкой
                if (cell.getTreeItem() != null) {
                    // клик по ячейки
                    if (e.getTarget() instanceof TreeCell || 
                            e.getTarget() instanceof Text) {
                        // запоминаем выбранный item
                        selectedItem = cell.getTreeItem();
                    } else {
                        if (cell.getTreeItem().isExpanded()) {
                            treeView.getSelectionModel().select(selectedItem);
                        }
                    }
                }
            });           
            
            return cell;
        }
    };

    private TreeItem<File> createNode(final File f) {
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
    
    private boolean containsObjectWithId(String id) {
        for (TreeDataBinding obj : treeNodes) {
            if (obj.getId().equals(id)) return true;
        }
        
        return false;
    }
    
    private TreeItem<File> getTreeItemById(String id) {
        return treeNodes.get(treeNodes.indexOf(getTreeDataBindingObjectById(id))).getTreeItem();
    }
    
    private TreeDataBinding getTreeDataBindingObjectById(String id) {
        for (TreeDataBinding obj : treeNodes) {
            if (obj.getId().equals(id)) return obj;
        }
        
        return null;
    }
    
    private TreeItem getParentOfSelectedItem(TreeItem selectedItem) {
        TreeItem parent = selectedItem;

        for (int i=1; i<this.treeView.getTreeItemLevel(selectedItem); i++) {
            parent = parent.getParent();    
        }

        return parent;
    }
    
    public String getSelectedItemValue() {
        TreeItem selectedItem = treeView.getTreeItem(treeView.getSelectionModel().getSelectedIndex());

        return  selectedItem.getValue().toString();
    }
    
    public class TreeDataBinding  {
        private TreeView treeView;
        private TreeItem treeItem;
        private TreeCell treeCell;
        private String id;
        private boolean expandedStatus;
        
        public TreeDataBinding() {}
        
        public TreeDataBinding(TreeView treeView, TreeItem treeItem, TreeCell treeCell, String id) {
            this.treeView = treeView;
            this.treeItem = treeItem;
            this.treeCell = treeCell;
            this.id = id;
            this.expandedStatus = false;
        }
        
        public TreeView getTreeView() {
            return this.treeView;
        }
        
        public void setTreeView(TreeView treeView) {
            this.treeView = treeView;
        }
        
        public TreeItem getTreeItem() {
            return this.treeItem;
        }
        
        public void setTreeItem(TreeItem treeItem) {
            this.treeItem = treeItem;
        }
        
        public TreeCell getTreeCell() {
            return this.treeCell;
        }
        
        public void setTreeCell(TreeCell treeCell) {
            this.treeCell = treeCell;
        }
        
        public String getId() {
            return this.id;
        }
        
        public void setSize(String id) {
            this.id = id;
        }
        
        public boolean getExpandedStatus() {
            return this.expandedStatus;
        }
        
        public void setExpandedStatus(boolean expandedStatus) {
            this.expandedStatus = expandedStatus;
        }
    }
}
