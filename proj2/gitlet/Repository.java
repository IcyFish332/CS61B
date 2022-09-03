package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import gitlet.Stage.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The stage directory. */
    public static final File STAGE = join(".gitlet", "stage");
    /** The blobs directory. */
    public static final File BLOBS_DIR = join(".gitlet", "blobs");
    /** The commits directory. */
    public static final File COMMITS_DIR = join(".gitlet", "commits");
    /** Points to the current commit. */
    public static final File HEAD = join(".gitlet", "HEAD");




    /** Creates a new Gitlet version-control system in the current directory.
     *  This system will automatically start with one commit: a commit that
     *  contains no files and has the commit message initial commit (just
     *  like that, with no punctuation). It will have a single branch: master,
     *  which initially points to this initial commit, and master will be the
     *  current branch. The timestamp for this initial commit will be 00:00:00
     *  UTC, Thursday, 1 January 1970 in whatever format you choose for dates
     *  (this is called “The (Unix) Epoch”, represented internally by the time 0.)
     *  Since the initial commit in all repositories created by Gitlet will
     *  have exactly the same content, it follows that all repositories will
     *  automatically share this commit (they will all have the same UID) and
     *  all commits in all repositories will trace back to it.
     */
    public static void init() {
        /** If there is already a Gitlet version-control system
         *  in the current directory, it should abort.
         */
        if (GITLET_DIR.exists() && GITLET_DIR.isDirectory()) {
            System.out.println("A Gitlet version-control system already exists in the current directory");
            System.exit(0);
        }

        GITLET_DIR.mkdir();
        Stage stage = new Stage();
        stage.saveStage();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        Commit initial = new Commit();
        initial.writeCommitToFile();
        writeContents(HEAD, initial.getUID());
    }

    public static void add(String nFileName) {
        /** If the file does not exist, print the error message
         *  and exit without changing anything.
         */
        File newFile = join(CWD,nFileName);
        if (!newFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Stage stage = readObject(STAGE, Stage.class);
        Blob newBlob = new Blob(CWD, nFileName);
        stage.addFile(nFileName, newBlob.getId());
        stage.saveStage();
    }

    /** Saves a snapshot of tracked files in the current commit and staging area
     *  so they can be restored at a later time, creating a new commit.
     *  The commit is said to be tracking the saved files. By default,
     *  each commit’s snapshot of files will be exactly the same as
     *  its parent commit’s snapshot of files; it will keep versions of files
     *  exactly as they are, and not update them. A commit will only update
     *  the contents of files it is tracking that have been staged for addition
     *  at the time of commit, in which case the commit will now include
     *  the version of the file that was staged instead of the version it got
     *  from its parent. A commit will save and start tracking any files that
     *  were staged for addition but weren’t tracked by its parent. Finally,
     *  files tracked in the current commit may be untracked in the new commit
     *  as a result being staged for removal by the rm command (below).
     */
    public static void commit(String message) {
        /** If no files have been staged, abort.*/
        Stage stage = readObject(STAGE, Stage.class);
        if (stage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        /** Every commit must have a non-blank message.
         *  If it doesn’t, print the error message.
         */
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Commit newCommit = new Commit(message);
        newCommit.writeCommitToFile();
        writeContents(HEAD, newCommit.getUID());
        stage = new Stage();
        stage.saveStage();
    }

    /** Unstage the file if it is currently staged for addition.
     *  If the file is tracked in the current commit, stage it
     *  for removal and remove the file from the working directory
     *  if the user has not already done so (do not remove it
     *  unless it is tracked in the current commit).
     */
    public static void rm(String filename) {

    }
}