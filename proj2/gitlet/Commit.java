package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  The new Commit is generated whenever the 'commit' is valid.
 *  does at a high level.
 *
 *  @author Siyuan Lu
 */
public class Commit implements Serializable {
    /** The message of this Commit. */
    private String message;
    /** Keeps track of the time of this Commit. */
    private Date timestamp;
    /** Implicates which the commit is. */
    private String UID;
    /** Last Commit of this. */
    private List<String> parents;
    /** All the blobs this commit includes. */
    private HashMap<String, String> blobs;

    /** Constructs Commit for the "init" operation. */
    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.UID = sha1(message, timestamp.toString());
        this.parents = new ArrayList<>();
        this.blobs = new HashMap<>();
    }

    /**
     * Constructs Commit for the rest of operations.
     *
     * @param m The message of the commit
     */
    public Commit(String m, String parentUID) {
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
        if (parentUID != null) {
            newParents.add(parentUID);
        }
        this.message = m;
        this.timestamp = new Date();
        this.parents = newParents;
        this.blobs = newBlobs;
        this.UID = sha1(message, timestamp.toString(), parents.toString(), blobs.toString());
    }

    public List<String> getParents() {
        return this.parents;
    }
    public String getMessage() {
        return this.message;
    }

    public String getTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(this.timestamp);
    }

    public String getUID() {
        return UID;
    }

    public HashMap<String, String> getBlobs() {
        return blobs;
    }

    /** Save this commit as a file, whose file name is its SHA-1 hash code. */
    public void saveCommit() {
        File file = join(COMMITS_DIR, this.getUID());
        writeObject(file, this);
    }

    /** Returns all the messages included in the commit with a specific format. */
    public String getCommitLog() {
        StringBuilder logPrinter = new StringBuilder();
        logPrinter.append("===").append('\n');
        logPrinter.append("commit ").append(this.UID).append('\n');
        if (parents.size() > 1) {
            logPrinter.append("Merge:");
            for (String parent : parents) {
                logPrinter.append(" ").append(parent, 0, 7);
            }
            logPrinter.append("\n");
        }
        logPrinter.append("Date: ").append(this.getTimestamp()).append('\n');
        logPrinter.append(message).append('\n');
        return logPrinter.toString();
    }
}
