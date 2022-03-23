package gitlet;
import java.io.File;
import static gitlet.Repository.*;
import static gitlet.Utils.*;
public class Tools {
    static String getSha1OfFile(String filename) {
        File f = new File(CWD.getPath() + "/" + filename);
        return sha1(filename, readContents(f));
    }
    static String getSha1OfCommit(Commit t) {
        return sha1(t.commitInfo());
    }
}
