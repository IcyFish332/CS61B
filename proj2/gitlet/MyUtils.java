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

    static List<String> checkTracked(Commit currentCommit) {
        List<String> untrackedFiles = new ArrayList<>();
        HashMap<String, String> commitFiles = currentCommit.getBlobs();
        List<String> filesInCWD = plainFilenamesIn(CWD);
        Stage stage = readObject(STAGE, Stage.class);

        for (String file : filesInCWD) {
            if (!commitFiles.containsKey(file) || stage.getRemoved().contains(file)) {
                untrackedFiles.add(file);
            }
        }
        return untrackedFiles;
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
