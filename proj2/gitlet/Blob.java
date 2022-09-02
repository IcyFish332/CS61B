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
        File newBlob = join(fileFoder, nFilename);
        this.filename = nFilename;
        if (newBlob.length() != 0) {
            this.contents = readContents(newBlob);
            this.id = sha1(filename, contents);
        } else {
            this.contents = null;
            this.id = sha1(filename);
        }
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
