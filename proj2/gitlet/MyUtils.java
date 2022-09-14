package gitlet;

import java.io.File;
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
}
