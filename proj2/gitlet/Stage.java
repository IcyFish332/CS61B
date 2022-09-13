package gitlet;

import java.io.Serializable;
import java.util.*;
import static gitlet.Repository.STAGE;
import static gitlet.Utils.writeObject;

/**
 * Represents a gitlet stage object.
 * Temporarily stores files operated by orders.
 *
 * @author Siyuan Lu
 */
public class Stage implements Serializable {
    /** Records all the "add" operation before the next commit and after the last commit. */
    private HashMap<String, String> added;

    /** Records all the "rm" operation before the next commit and after the last commit. */
    private HashSet<String> removed;

    /** Constructs the default stage area. */
    public Stage() {
        added = new HashMap<>();
        removed = new HashSet<>();
    }

    /**
     * Temporarily stores the blobs(files) operated by 'add' order.
     *
     * @param filename filename of new added blob (as the key)
     * @param blobId id of it (as the value)
     */
    public void addFile(String filename, String blobId) {
        added.put(filename, blobId);
        removed.remove(filename);
    }

    /**
     * Temporarily stores the blobs(files) operated by 'rm' order.
     *
     * @param filename filename of new removed blob
     */
    public void removeFile(String filename) {
        added.remove(filename);
        removed.add(filename);
    }

    public boolean isEmpty() {
        return added.isEmpty() && removed.isEmpty();
    }

    public HashMap<String, String> getAdded() {
        return added;
    }

    public HashSet<String> getRemoved() {
        return removed;
    }

    /** Saves the stage object in files. */
    public void saveStage() {
        writeObject(STAGE, this);
    }
}
