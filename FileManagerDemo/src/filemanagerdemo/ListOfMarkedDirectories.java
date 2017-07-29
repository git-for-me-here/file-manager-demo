package filemanagerdemo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author fakelake
 */
public class ListOfMarkedDirectories {
    private ObservableList<String> listMarkDir = FXCollections.observableArrayList();
    private IntegerProperty index = new SimpleIntegerProperty(-1);
    
    public ObservableList<String> getList() {
        return this.listMarkDir;
    }
    
    public void addUnique(String path) {
        if (listMarkDir.contains(path)) {
            index.set(listMarkDir.indexOf(path));
        } else {
            listMarkDir.add(path);
            index.set(listMarkDir.size() - 1);
        }
    }
    
    public void clear() {
        listMarkDir.clear();
    }
    
    public int size() {
        return listMarkDir.size();
    }
    
    public int getCurrentIndex() {
        return index.get();
    }
    
    public String getPrevious() {
        if (index.get()-1 < 0) return "";
       
        return listMarkDir.get(index.get()-1);
    }
    
    public String getNext() {
        if (index.get()+1 >= listMarkDir.size()) return "";
       
        return listMarkDir.get(index.get()+1);
    }
    
    public IntegerProperty indexProperty() {
        return index;
    }
    
    public void indexDecrement() {
        index.set(index.get()-1);
    }
    
    public void indexIncrement() {
        index.set(index.get()+1);
    }
}
