package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** Keeps track of he time of this Commit. */
    private Date timestamp;
    /** Implicates which the commit is. */
    private String UID;
    /** Every commit should record last commit, except Commit 0. */
    private List<String> parents;

    private HashMap<String, String> blobs;


    /** Constructs Commit for the "init" operation. */
    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.UID = sha1(message, timestamp.toString());
        this.parents = new ArrayList<>();
        this.blobs = new HashMap<>();
    }
    /** Constructs Commit for the rest of operations */
    public Commit(String m) {
        File file = join(COMMITS_DIR, readContentsAsString(HEAD));
        Commit lastCommit = readObject(file, Commit.class);
        HashMap<String, String> newBlobs = lastCommit.getBlobs();
        Stage stage = readObject(STAGE, Stage.class);
        for (Map.Entry<String, String> item: stage.getAdded().entrySet()) {
            newBlobs.put(item.getKey(), item.getValue());
        }
        for (String filename: stage.getRemoved()) {
            newBlobs.remove(filename);
        }
        List<String> newParents = new ArrayList<>();
        newParents.add(lastCommit.getUID());
        this.message = m;
        this.timestamp = new Date();
        this.parents = newParents;
        this.blobs = newBlobs;
        this.UID = sha1(message, timestamp.toString(), parents.toString(), blobs.toString());
    }

    public String getMessage() {
        return this.message;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getUID() {
        return UID;
    }

    public HashMap<String, String> getBlobs() {
        return blobs;
    }

    public void saveCommit() {
        File file = join(COMMITS_DIR, this.getUID());
        writeObject(file, this);
    }

}
