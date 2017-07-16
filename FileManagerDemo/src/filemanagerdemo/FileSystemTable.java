package filemanagerdemo;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author fakelake
 */
public class FileSystemTable {
    protected ObservableList getFileMetaData(ArrayList<File> filesList) {
        ObservableList<FileMetaData> list = FXCollections.observableArrayList();
        for (File file : filesList) {
            list.add(new FileMetaData(
                    file.getName(),
                    getFormattedDate(file.lastModified()),
                    getFileType(file),
                    getFileSize(file)
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
    
    public class FileMetaData  {
        private String name;
        private String modified;
        private String type;
        private String size;
        
        public FileMetaData() {}
        
        public FileMetaData(String name, String modified, String type, String size) {
            this.name = name;
            this.modified = modified;
            this.type = type;
            this.size = size;
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
        
        public Comparator<FileMetaData> FileTypeComparator = new Comparator<FileMetaData>() {
            @Override
            public int compare(FileMetaData f1, FileMetaData f2) {
                return f1.type.compareTo(f2.type);
            }
        };
    }
}
