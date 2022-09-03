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
    private String parent;

    private HashMap<String, String> blobs;


    /** Constructs Commit for the "init" operation. */
    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.UID = sha1(message, timestamp.toString());
        this.parent = null;
        this.blobs = null;
    }
    /** Constructs Commit for the rest of operations */
    public Commit(String m) {
        this.message = m;
        this.timestamp = new Date();
        this.parent = readContentsAsString(HEAD);
        this.blobs = new HashMap<>();
        Commit lastCommit = readObject(HEAD, Commit.class);
        for (Map.Entry<String, String> item: lastCommit.getBlobs().entrySet()) {
            blobs.put(item.getKey(), item.getValue());
        }
        Stage stage = readObject(STAGE, Stage.class);
        for (Map.Entry<String, String> item: stage.getAdded().entrySet()) {
            blobs.put(item.getKey(), item.getValue());
        }
        for (String filename: stage.getRemoved()) {
            blobs.remove(filename);
        }
        this.UID = sha1(message, timestamp.toString(), parent, blobs.toString());
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

    public void writeCommitToFile() {
        File file = join(COMMITS_DIR, this.getUID());
        writeObject(file, this);
    }
}
