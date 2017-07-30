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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author fakelake
 */
public class FileSystemTable {
    private TableView tableView;
    
    public FileSystemTable(TableView tableView) {
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
