package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    /** The contents of this Blob which is stored as bytes. */
    private byte[] contents;
    /**  The unique id of each Blob. */
    private String id;
    /** The filename of the Blob. */
    private String filename;

    /** Constructs new bolb as well as saves it to the file. */
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
