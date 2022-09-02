package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

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
    /** Every commit should record last commit, except Commit 0. */
    private Commit parent;
    /** Implicates which the commit is. */
    private String id;

    /** Constructs Commit for the "init" operation. */
    public Commit () {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.id = sha1(message, timestamp.toString());
        this.parent = null;
    }
    /** Constructs Commit for the rest of operations */
    public Commit (String m, Commit parent) {
        this.message = m;
        this.timestamp = new Date();
        this.parent = null;
    }

    public String getMessage () {
        return this.message;
    }

    public Date getTimestamp () {
        return this.timestamp;
    }

    public Commit getParent () {
        return this.parent;
    }

    public String getId() {
        return id;
    }

    public void writeCommitToFile() {
        File file = join(COMMITS_DIR, this.getId());
        writeObject(file, this);
    }
}
