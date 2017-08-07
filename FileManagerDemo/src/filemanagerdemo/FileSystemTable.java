package filemanagerdemo;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.WindowEvent;

/**
 *
 * @author fakelake
 */
public class FileSystemTable {
    private TableView tableView;
    
    public FileSystemTable(TableView<FileMetaData> tableView) {
        tableView.setPlaceholder(new Label(""));   
        tableView.setRowFactory((TableView<FileMetaData> tv) -> {
            TableRow<FileMetaData> row = new TableRow<>();
            
            ContextMenu contextMenu = new ContextMenu();
            
            Menu createMenu = new Menu("Создать");
            Menu folderMenu = new Menu("Папку");
            
            MenuItem createHereMenuItem = new MenuItem("В текущей директории");
            createHereMenuItem.setOnAction((ActionEvent event) -> {
                String path = tv.getItems().get(0).getPath();
                path = path.substring(0, path.lastIndexOf(File.separatorChar));
                
                createDir(path, "current");
            });
            MenuItem createInFolderMenuItem = new MenuItem();
            createInFolderMenuItem.setOnAction((ActionEvent event) -> {
                String path = row.getItem().getPath();
                
                createDir(path, "internal");
            });
            MenuItem deleteItem = new MenuItem("Удалить");
            deleteItem.setOnAction((ActionEvent event) -> {
                String path = row.getItem().getPath();
                File file = new File(path);
                
                deleteFile(file); 
            });
            
            folderMenu.getItems().add(createHereMenuItem);
            createMenu.getItems().add(folderMenu);
            
            contextMenu.getItems().add(createMenu);
            
            contextMenu.setOnShowing((WindowEvent event) -> {
                if (!row.isEmpty()) {
                    if (!row.getItem().getType().equals("Файл")) {
                        createInFolderMenuItem.setText("В \"" +
                                row.getItem().getName() + "\"");

                        if (!folderMenu.getItems().contains(createInFolderMenuItem)) {
                            folderMenu.getItems().add(createInFolderMenuItem);
                        }
                    }
                    
                    if (!contextMenu.getItems().contains(deleteItem)) {
                        contextMenu.getItems().add(deleteItem);
                    }
                }
            });
            
            row.contextMenuProperty().set(contextMenu);
            
            return row;  
        });
        
        this.tableView = tableView; 
        
        TableColumn<FileMetaData, ImageView> iconCol = 
                (TableColumn<FileMetaData, ImageView>)tableView.getColumns().get(0);
        iconCol.setCellValueFactory(new PropertyValueFactory<>("icon"));
    }
    
    private ObservableList getFileMetaData(ArrayList<File> filesList) {
        ObservableList<FileMetaData> list = FXCollections.observableArrayList();
        for (File file : filesList) {
            ImageView icon = file.isFile() 
                    ? new ImageView(new Image("icons/file-icon.png"))
                    : new ImageView(new Image("icons/folder-icon.png"));
            
            list.add(new FileMetaData(
                    icon,
                    file.getName(),
                    getFormattedDate(file.lastModified()),
                    getFileType(file),
                    getFileSize(file),
                    file.getAbsolutePath()
            ));
        }
        
        FileMetaData fmData = new FileMetaData();
        FXCollections.sort(list, fmData.FileTypeComparator);
        
        return list;
    }
    
    private String getFormattedDate(long timestamp) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = new Date(timestamp);
        
        return df.format(date);
    }
    
    private String getFileType(File file) {
        if (file.isFile()) return "Файл";
        return "Папка с файлами";
    }
    
    private String getFileSize(File file) {
        if (file.isDirectory()) return "";
        long bytes = file.length();
        int unit = 1024;
        if (bytes == 0) return "0 КБ";
        if (bytes < unit) return "1 КБ";
        int result = (int)Math.ceil(bytes / (double)unit);
        return String.format("%,d", result) + " КБ";
    }
    
    private void deleteFile(File file) {
        // если это директория
        if(file.isDirectory()) {
            // если папка ничего не содержит удаляем ее
            if(file.list().length == 0) {
               file.delete();
            } else {
                // получаем список содержимого директории
                String files[] = file.list();

                for (String temp : files) {
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    deleteFile(fileDelete);
                }

                // проверяем директоию снова, если она пуста, удаляем ее
                if(file.list().length == 0) {
                    file.delete();
                }
            }
    	} else{
            // если это файл, удаляем его
            file.delete();
    	}
        
        FileManagerDemo.FILE_SYSTEM_CHANGED.set("FILE_DELETED::" + file.getPath());
    }
    
    private void createDir(String path, String folder) {
        File currentFolder = new File(path);
        File newFolder = new File(path + File.separator + "Новая папка");

        if (!newFolder.exists()) {
            newFolder.mkdir();
        } else {
            String[] list = currentFolder.list((File dir, String name) -> 
                    name.matches("Новая папка( (\\(\\d+\\)))?"));
            int i = list.length + 1;

            newFolder = new File(path + File.separator + "Новая папка (" + i + ")");
            newFolder.mkdir(); 
        } 
        
        switch (folder) {
            case "current":
                FileManagerDemo.FILE_SYSTEM_CHANGED.set("FILE_CREATED::" + newFolder);
                break;
                
            case "internal":
                FileManagerDemo.FILE_SYSTEM_CHANGED.set("FILE_CREATED_IN_FOLDER::" + newFolder);
                break;
        }
    }
    
    protected void loadData(String path) {
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

        this.tableView.setItems(getFileMetaData(filesList));
    }
    
    public class FileMetaData  {
        private ImageView icon;
        private String name;
        private String modified;
        private String type;
        private String size;
        private String path;
        
        public FileMetaData() {}
        
        public FileMetaData(ImageView icon, String name, String modified,
                String type, String size, String path) {
            this.icon = icon;
            this.name = name;
            this.modified = modified;
            this.type = type;
            this.size = size;
            this.path = path;
        }
        
        public ImageView getIcon() {
            return this.icon;
        }
        
        public void setName(ImageView icon) {
            this.icon = icon;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getModified() {
            return this.modified;
        }
        
        public void setModified(String modified) {
            this.modified = modified;
        }
        
        public String getType() {
            return this.type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getSize() {
            return this.size;
        }
        
        public void setSize(String size) {
            this.size = size;
        }
        
        public String getPath() {
            return this.path;
        }
        
        public void setPath(String path) {
            this.path = path;
        }
        
        public Comparator<FileMetaData> FileTypeComparator = new Comparator<FileMetaData>() {
            @Override
            public int compare(FileMetaData f1, FileMetaData f2) {
                return f1.type.compareTo(f2.type);
            }
        };
    }
}
