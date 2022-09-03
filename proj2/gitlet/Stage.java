package gitlet;

import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.STAGE;
import static gitlet.Utils.writeObject;

public class Stage implements Serializable {
    private HashMap<String, String> added;

    private HashSet<String> removed;

    public Stage() {
        added = new HashMap<>();
        removed = new HashSet<>();
    }

    public void addFile(String filename, String blobId) {
        added.put(filename, blobId);
        removed.remove(filename);
    }

    public void removeFile(String filename) {
        added.remove(filename);
        removed.add(filename);
    }

    public boolean isEmpty() {
        return added.isEmpty() && removed.isEmpty();
    }

//    public clearStage() {
//        this.added = new HashMap<>();
//        this.removed = new HashSet<>();
//    }

    public HashMap<String, String> getAdded() {
        return added;
    }

    public HashSet<String> getRemoved() {
        return removed;
    }

    public void saveStage() {
        writeObject(STAGE, this);
    }
}
