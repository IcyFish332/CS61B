package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/**
 *
 * @author Siyuan Lu
 */
class MyUtils {
    static Commit getCommitFromUId(String uid) {
        File file = join(COMMITS_DIR, uid);
        if (!file.exists()) {
            return null;
        }
        return readObject(file, Commit.class);
    }
    static Blob getBlobFromId(String id) {
        File file = join(BLOBS_DIR, id);
        if (!file.exists()) {
            return null;
        }
        return readObject(file, Blob.class);
    }

    static void setCommitAsHEAD(String commmitUID) {
        writeContents(HEAD, commmitUID);
        File currentBranch = join(BRANCHES_DIR, readContentsAsString(CURRENTBRANCH));
        writeContents(currentBranch, commmitUID);
    }

    static List<String> modificationsNotStaged(Commit currentCommit) {
        List<String> filesInCWD = plainFilenamesIn(CWD);
        HashMap<String, String> commitFiles = currentCommit.getBlobs();
        Stage stage = readObject(STAGE, Stage.class);
        List<String> targetFiles = new ArrayList<>();

        for (Map.Entry<String, String> trackedfile : commitFiles.entrySet()) {
            String filename = trackedfile.getKey();
            if (filesInCWD.contains(filename)) {
                Blob blobInCWD = new Blob(filename);
                if (blobInCWD.getId() != null && !stage.getAdded().containsKey(filename)) {
                    targetFiles.add(filename + "modified");
                }
            } else if (!filesInCWD.contains(filename)
                    && !stage.getRemoved().contains(filename)) {
                targetFiles.add(filename + "deleted");
            }
        }

        for (Map.Entry<String, String> addedfile : stage.getAdded().entrySet()) {
            String filename = addedfile.getKey();
            Blob blobInCWD = new Blob(filename);
            if (blobInCWD.getId() != null) {
                targetFiles.add(filename + "modified");
            } else if (!filesInCWD.contains(filename)) {
                targetFiles.add(filename + "deleted");
            }
        }

        return targetFiles;
    }

    static String getCompleteCommitUid(String commitUid) {
        if (commitUid.length() == 40) {
            return commitUid;
        }

        List<String> commitfiles = plainFilenamesIn(COMMITS_DIR);
        for (String filename : commitfiles) {
            if (filename.startsWith(commitUid)) {
                return filename;
            }
        }
        return null;
    }

    static List<String> checkTracked(Commit currentCommit) {
        List<String> untrackedFiles = new ArrayList<>();
        HashMap<String, String> commitFiles = currentCommit.getBlobs();
        List<String> filesInCWD = plainFilenamesIn(CWD);
        Stage stage = readObject(STAGE, Stage.class);

        for (String file : filesInCWD) {
            if (!commitFiles.containsKey(file)) {
                if (stage.getRemoved().contains(file)) {
                    untrackedFiles.add(file);
                }
            } else {
                if (!stage.getAdded().containsKey(file)) {
                    untrackedFiles.add(file);
                }
            }
        }
        return untrackedFiles;
    }

    static void validUntrackedFile(Commit targetCommit) {
        Commit currentCommit = getCommitFromUId(readContentsAsString(HEAD));
        List<String> untrackedFiles = checkTracked(currentCommit);
        if (untrackedFiles.isEmpty()) {
            return;
        }

        HashMap<String, String> targetCommitFiles = targetCommit.getBlobs();
        for (String filename : untrackedFiles) {
            Blob newBlob = new Blob(filename);
            String blobIdInCWD = newBlob.getId();
            String targetBlobId = targetCommitFiles.getOrDefault(filename, "");
            if (blobIdInCWD == null) {
                blobIdInCWD  = currentCommit.getBlobs().getOrDefault(filename, "");
            }
            if (!blobIdInCWD.equals(targetBlobId)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    static Commit splitPoint(Commit currentCommit, Commit givenCommit) {
        Set<String> ancestorsOfC = bfsFromCommit(currentCommit);
        Queue<Commit> queue = new LinkedList<>();

        queue.add(givenCommit);
        while (!queue.isEmpty()) {
            Commit commit = queue.poll();
            if (ancestorsOfC.contains(commit.getUID())) {
                return commit;
            }
            if (!commit.getParents().isEmpty()) {
                for (String uid : commit.getParents()) {
                    queue.add(getCommitFromUId(uid));
                }
            }
        }
        return new Commit();

    }

    static Set<String> bfsFromCommit(Commit currentCommit) {
        Set<String> ancestors = new HashSet<>();
        Queue<Commit> queue = new LinkedList<>();

        queue.add(currentCommit);
        while (!queue.isEmpty()) {
            Commit commit = queue.poll();
            if (!ancestors.contains(commit.getUID()) && !commit.getParents().isEmpty()) {
                for (String uid : commit.getParents()) {
                    queue.add(getCommitFromUId(uid));
                }
            }
            ancestors.add(commit.getUID());
        }
        return ancestors;
    }
}
