package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    private byte[] contents;
    private String id;
    private String filename;

    public Blob(File fileFoder, String nFilename) {
        File newFile = join(fileFoder, nFilename);
        this.filename = nFilename;
        if (newFile.length() != 0) {
            this.contents = readContents(newFile);
            this.id = sha1(filename, contents);
        } else {
            this.contents = null;
            this.id = sha1(filename);
        }
        File newBlob = join(BLOBS_DIR, this.id);
        writeObject(newBlob, this);
    }

    public byte[] getContents() {
        return contents;
    }

    public String getFilename() {
        return filename;
    }

    public String getId() {
        return id;
    }
}
