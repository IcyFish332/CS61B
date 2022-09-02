package gitlet;

import java.io.Serializable;
import java.util.*;

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
}
