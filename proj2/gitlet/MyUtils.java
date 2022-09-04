package gitlet;

import java.io.File;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

class MyUtils {
    static Commit getCommitFromUId(String UID) {
        File file = join(COMMITS_DIR, UID);
        if (UID.equals("null") || !file.exists()) {
            return null;
        }
        return readObject(file, Commit.class);
    }
}
