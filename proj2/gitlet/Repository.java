package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

/** Represents a gitlet repository.
 *  All the methods representing actual operation is stored in here.
 *  does at a high level.
 *
 *  @author Siyuan Lu
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
    /** The branches directory. */
    public static final File BRANCHES_DIR = join(".gitlet", "branches");
    /** In the branches diectory, points at the cuurent branch */
    public static final File CURRENTBRANCH = join(".gitlet", "currentBranch");
    /** Points to the current commit. */
    public static final File HEAD = join(".gitlet", "HEAD");




    /**
     *  Creates a new Gitlet version-control system in the current directory.
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
        /*
         * If there is already a Gitlet version-control system
         * in the current directory, it should abort.
         */
        if (GITLET_DIR.exists() && GITLET_DIR.isDirectory()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory");
            System.exit(0);
        }

        GITLET_DIR.mkdir();
        Stage stage = new Stage();
        stage.saveStage();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        Commit initial = new Commit();
        initial.saveCommit();
        writeContents(HEAD, initial.getUID());
        File newBranch = join(BRANCHES_DIR, "master");
        writeContents(newBranch, initial.getUID());
        writeContents(CURRENTBRANCH, "master");
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area
     * (see the description of the commit command). For this reason,
     * adding a file is also called staging the file for addition.
     * Staging an already-staged file overwrites the previous entry in the staging area
     * with the new contents. The staging area should be somewhere in .gitlet.
     * If the current working version of the file is identical to the version
     * in the current commit, do not stage it to be added, and remove it
     * from the staging area if it is already there (as can happen when a file is changed,
     * added, and then changed back to it’s original version).
     * The file will no longer be staged for removal (see gitlet rm),
     * if it was at the time of the command.
     *
     * @param nFileName filename of new file added
     */
    public static void add(String nFileName) {
        /*
         * If the file does not exist, print the error message
         * and exit without changing anything.
         */
        File newFile = join(CWD, nFileName);
        if (!newFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Stage stage = readObject(STAGE, Stage.class);
        Blob newBlob = new Blob(nFileName);
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
     *
     * @param message commit message
     */
    public static void commit(String message) {
        // If no files have been staged, abort.
        Stage stage = readObject(STAGE, Stage.class);
        if (stage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        // Every commit must have a non-blank message.If it doesn’t, print the error message.
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Commit newCommit = new Commit(message, null);
        newCommit.saveCommit();
        setCommitAsHEAD(newCommit.getUID());
        stage = new Stage();
        stage.saveStage();
    }

    /** Unstage the file if it is currently staged for addition.
     *  If the file is tracked in the current commit, stage it
     *  for removal and remove the file from the working directory
     *  if the user has not already done so (do not remove it
     *  unless it is tracked in the current commit).
     *
     * @param filename filename of removing file from the stage
     */
    public static void rm(String filename) {
        /* If the file is neither staged nor tracked by the head commit, print the error message. */
        Stage stage = readObject(STAGE, Stage.class);
        Commit headCommit = getCommitFromUId(readContentsAsString(HEAD));
        if (!headCommit.getBlobs().containsKey(filename)
                && !stage.getAdded().containsKey(filename)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        if (stage.getAdded().containsKey(filename)) {
            stage.removeFile(filename);
        } else if (headCommit.getBlobs().containsKey(filename)) {
            stage.getRemoved().add(filename);
            restrictedDelete(filename);
        }
        stage.saveStage();
    }

    /** Starting at the current head commit, display information about
     *  each commit backwards along the commit tree until the initial commit,
     *  following the first parent commit links, ignoring any second parents
     *  found in merge commits. (In regular Git, this is what you get with
     *  git log --first-parent). This set of commit nodes is called the
     *  commit’s history. For every node in this history, the information it
     *  should display is the commit id, the time the commit was made,
     *  and the commit message.
     */
    public static void log() {
        Commit headCommit = getCommitFromUId(readContentsAsString(HEAD));
        System.out.println(headCommit.getCommitLog());
        while (!headCommit.getParents().isEmpty()) {
            headCommit = getCommitFromUId(headCommit.getParents().get(0));
            System.out.println(headCommit.getCommitLog());
        }
    }

    /**
     * Like log, except displays information about all commits ever made.
     * The order of the commits does not matter.
     */
    public static void globalLog() {
        StringBuilder log = new StringBuilder();
        List<String> filenames = plainFilenamesIn(COMMITS_DIR);
        for (String filename : filenames) {
            Commit c = getCommitFromUId(filename);
            log.append(c.getCommitLog()).append('\n');
        }
        System.out.print(log);
    }

    /**
     * Prints out the ids of all commits that have the given commit message, one per line.
     * If there are multiple such commits, it prints the ids out on separate lines.
     * The commit message is a single operand; to indicate a multiword message,
     * put the operand in quotation marks, as for the commit command below.
     *
     * @param message the message of commit being searched
     */
    public static void find(String message) {
        StringBuilder log = new StringBuilder();
        List<String> filenames = plainFilenamesIn(COMMITS_DIR);
        boolean isFind = false;
        for (String filename : filenames) {
            Commit c = getCommitFromUId(filename);
            if (c.getMessage().equals(message)) {
                isFind = true;
                log.append(c.getUID()).append('\n');
            }
        }
        if (isFind) {
            System.out.print(log);
        } else {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Creates a new branch with the given name, and points it at the current head commit.
     * A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node.
     * This command does NOT immediately switch to the newly created branch (just as in real Git).
     * Before ever call branch, the code should be running with a default branch called “master”.
     *
     * @param branchName the actual name of new branch
     */
    public static void branch(String branchName) {
        File newBranch = join(BRANCHES_DIR, branchName);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
        } else {
            writeContents(newBranch, readContentsAsString(HEAD));
        }
    }

    /**
     * Displays what branches currently exist, and marks the current branch with a *.
     * Also displays what files have been staged for addition or removal.
     */
    public static void status() {
        StringBuilder status = new StringBuilder();
        Stage stage = readObject(STAGE, Stage.class);
        status.append("=== Branches ===").append('\n');
        String currentBranch = readContentsAsString(CURRENTBRANCH);
        status.append('*').append(currentBranch).append('\n');
        List<String> filenames = plainFilenamesIn(BRANCHES_DIR);
        for (String branch : filenames) {
            if (!branch.equals(currentBranch)) {
                status.append(branch).append('\n');
            }
        }
        status.append('\n').append("=== Staged Files ===").append('\n');
        for (Map.Entry<String, String> staged : stage.getAdded().entrySet()) {
            status.append(staged.getKey()).append('\n');
        }
        status.append('\n').append("=== Removed Files ===").append('\n');
        for (String removed: stage.getRemoved()) {
            status.append(removed).append('\n');
        }
        status.append('\n').append("=== Modifications Not Staged For Commit ===").append('\n');

        status.append('\n').append("=== Untracked Files ===").append('\n');
        status.append('\n');
        System.out.println(status);
    }

    /**
     * Totally, "checkout" operation would deal with 3 specific cases.
     *
     * java gitlet.Main checkout -- [file name]
     * 1.Takes the version of the file as it exists in the head commit
     * and puts it in the working directory, overwriting the version of the file
     * that’s already there if there is one. The new version of the file is not staged.
     *
     * java gitlet.Main checkout [commit id] -- [file name]
     * 2.Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory, overwriting the version of the file
     * that’s already there if there is one. The new version of the file is not staged.
     *
     * java gitlet.Main checkout [branch name]
     * 3.Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory, overwriting the versions of the files
     * that are already there if they exist. Also, at the end of this command,
     * the given branch will now be considered the current branch (HEAD).
     * Any files that are tracked in the current branch but are not present
     * in the checked-out branch are deleted. The staging area is cleared,
     * unless the checked-out branch is the current branch (see Failure cases below).
     *
     * @param message used for check specific file or any other object
     */
    public static void checkout(String[] message) {
        // Case 3
        if (message.length == 2) {
            checkoutBranch(message[1]);
        // Case 2
        } else if (message[2].equals("--") && message.length == 4) {
            checkoutFileFromCommit(message[1], message[3]);
        // Case 1
        } else if (message[1].equals("--") && message.length == 3) {
            checkoutFileFromCommit(readContentsAsString(HEAD), message[2]);
        // Prints out warning and exits if the number of arguments is wrong.
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /**
     * Helps `checkout` to complete "checkout" order when
     * the UID of specific commit and the file name are provided,
     * which could be used in Case 1 and Case 2.
     *
     * @param commitUID the UID of specific commit
     * @param filename the name of file we are checking out
     */
    public static void checkoutFileFromCommit(String commitUID, String filename) {
        List<String> uids = plainFilenamesIn(COMMITS_DIR);
        if (!uids.contains(commitUID)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit headCommit = getCommitFromUId(commitUID);
        if (!headCommit.getBlobs().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        File checkedFile = join(CWD, filename);
        String blobId = headCommit.getBlobs().getOrDefault(filename, null);
        writeContents(checkedFile, getBlobFromId(blobId).getContents());
    }

    /**
     * Helps `checkout` to complete "checkout" order when the branch
     * is provided, which could be used in Case 3.
     *
     * @param branchName the name of branch we are checking out
     */
    public static void checkoutBranch(String branchName) {
        File checkedBranch = join(BRANCHES_DIR, branchName);
        if (!checkedBranch.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (branchName.equals(readContentsAsString(CURRENTBRANCH))) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        checkoutCommit(readContentsAsString(checkedBranch));
        writeContents(CURRENTBRANCH, branchName);
    }

    /**
     * "checkout" the commit through the UID of it.
     *
     * @param commitUID the UID of commit we are checking out
     */
    public static void checkoutCommit(String commitUID) {
        Commit checkedCommit = getCommitFromUId(commitUID);
        List<String> currentFilenames = plainFilenamesIn(CWD);
        for (String filename : currentFilenames) {
            File currentFile = join(CWD, filename);
            String blobId = checkedCommit.getBlobs().getOrDefault(filename, null);
            if (blobId == null) {
                restrictedDelete(currentFile);
            } else {
                writeContents(currentFile, getBlobFromId(blobId).getContents());
            }
        }
        Stage stage = new Stage();
        stage.saveStage();
        writeContents(HEAD, checkedCommit.getUID());
    }

    /**
     * Deletes the branch with the given name. This only means to delete the pointer
     * associated with the branch; it does not mean to delete all commits
     * that were created under the branch, or anything like that.
     *
     * @param branchName the name of branch that we are going to remove
     */
    public static void rmBranch(String branchName) {
        File targetBranch = join(BRANCHES_DIR, branchName);
        if (!targetBranch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branchName.equals(readContentsAsString(CURRENTBRANCH))) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        restrictedDelete(targetBranch);
    }

    /**
     * Checks out all the files tracked by the given commit. Removes tracked files that
     * are not present in that commit. Also moves the current branch’s head to that commit node.
     * See the intro for an example of what happens to the head pointer after using reset.
     * The [commit id] may be abbreviated as for checkout. The staging area is cleared.
     * The command is essentially checkout of an arbitrary commit
     * that also changes the current branch head.
     *
     * @param commitUID the UID of the commit that we are going to remove
     */
    public static void reset(String commitUID) {
        File targetCommitFile = join(COMMITS_DIR, commitUID);
        if (!targetCommitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit targetCommit = getCommitFromUId(commitUID);
        if (!checkTracked(targetCommit).isEmpty()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            System.exit(0);
        }

        checkoutCommit(commitUID);
        setCommitAsHEAD(commitUID);
    }

    /**
     * Merges files from the given branch into the current branch.
     *
     * @param givenBranch the name of branch that we are going to merge with HEAD
     */
    public static void merge(String givenBranch) {
        Stage stage = readObject(STAGE, Stage.class);
        /*
        If there are staged additions or removals present,
        print the error message and exit.
         */
        if (!stage.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        File checkedBranch = join(BRANCHES_DIR, givenBranch);
        /*
        If a branch with the given name does not exist,
        print the error message and exit.
         */
        if (!checkedBranch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        /*
        If attempting to merge a branch with itself,
        print the error message and exit.
         */
        if (readContentsAsString(CURRENTBRANCH).equals(givenBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        Commit currentCommit = getCommitFromUId(readContentsAsString(HEAD));
        File givenBranchFile = join(BRANCHES_DIR, givenBranch);
        Commit givenCommit = getCommitFromUId(readContentsAsString(givenBranchFile));
        Commit splitPoint = splitPoint(currentCommit, givenCommit);

        /*
        If the split point is the same commit as the given branch,
        then we do nothing; the merge is complete, and the operation ends
         */
        if (splitPoint.getUID().equals(givenCommit.getUID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }

        /*
        If the split point is the current branch,
        then the effect is to check out the given branch, and the operation ends
         */
        if (splitPoint.getUID().equals(currentCommit.getUID())) {
            System.out.println("Current branch fast-forwarded.");
            checkoutBranch(givenBranch);
            System.exit(0);
        }

        /*
        If an untracked file in the current commit would be overwritten
        or deleted by the merge, exit.
         */
        if (!checkTracked(currentCommit).isEmpty()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            System.exit(0);
        }

        HashMap<String, String> contentsG = givenCommit.getBlobs();
        HashMap<String, String> contentsC = currentCommit.getBlobs();
        HashMap<String, String> contentsS = splitPoint.getBlobs();

        for (String file : contentsS.keySet()) {
            if (!contentsG.containsKey(file) || !contentsC.containsKey(file)) {
                stage.removeFile(file);
            } else {
                String fileG = contentsG.getOrDefault(file, null);
                String fileC = contentsC.getOrDefault(file, null);
                String fileS = contentsS.getOrDefault(file, null);
                if (fileS.equals(fileC) && !fileS.equals(fileG)) {
                    stage.addFile(file, fileG);
                } else if (!fileS.equals(fileC) && fileS.equals(fileG)) {
                    stage.addFile(file, fileC);
                } else if (!fileS.equals(fileC) && !fileS.equals(fileG)) {
                    File conflictFile = join(CWD, file);
                    settleConflict(conflictFile, fileC, fileG);
                }
            }
            contentsG.remove(file);
            contentsC.remove(file);
            contentsS.remove(file);
        }

        for (String file : contentsG.keySet()) {
            String fileG = contentsG.getOrDefault(file, null);
            if (!contentsC.containsKey(file)) {
                stage.addFile(file, fileG);
            } else {
                String fileC = contentsG.getOrDefault(file, null);
                if (!fileG.equals(fileC)) {
                    File conflictFile = join(CWD, file);
                    settleConflict(conflictFile, fileC, fileG);
                }
            }
            contentsG.remove(file);
            contentsC.remove(file);
        }

        for (String file : contentsC.keySet()) {
            String fileC = contentsG.getOrDefault(file, null);
            if (!contentsG.containsKey(file)) {
                stage.addFile(file, fileC);
            }
        }

        String message = "Merged " + givenBranch
                + " into " + readContentsAsString(CURRENTBRANCH) + ".";
        Commit newCommit = new Commit(message, readContentsAsString(givenBranchFile));
        newCommit.saveCommit();
        setCommitAsHEAD(newCommit.getUID());
        stage = new Stage();
        stage.saveStage();
        checkoutCommit(newCommit.getUID());
    }

    public static void settleConflict(File fileToBeFixed,
                                      String currentBranchFile,
                                      String givenBranchFile) {
        File blobOfCurrent = join(BLOBS_DIR, currentBranchFile);
        File blobOfGiven = join(BLOBS_DIR, givenBranchFile);
        StringBuilder contents = new StringBuilder();
        contents.append("<<<<<<< HEAD").append('\n');
        contents.append(readContentsAsString(blobOfCurrent));
        contents.append("=======").append('\n');
        contents.append(readContentsAsString(blobOfGiven));
        contents.append(">>>>>>>").append('\n');
        writeContents(fileToBeFixed, contents);
    }
}
