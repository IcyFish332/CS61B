package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

class MyUtils {
    static Commit getCommitFromUId(String UID) {
        File file = join(COMMITS_DIR, UID);
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

        for (String file : filesInCWD) {
            if (!commitFiles.containsKey(file)) {
                untrackedFiles.add(file);
            }
        }
        return untrackedFiles;
    }
}
