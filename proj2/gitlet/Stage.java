package gitlet;

import java.util.HashMap;
import static gitlet.Utils.*;
import static gitlet.Tools.*;

public class Stage {
    static public HashMap<String, String> map = new HashMap<>();
    static public HashMap<String, String> mapForRm = new HashMap<>();
//    public Stage() {
//        set = new HashSet<>();
//    }
    public static void add(String name, String sha) {
        if (!map.getOrDefault(name, "").equals(sha) && !Commit.for_commit.map.getOrDefault(name, "").equals(sha)) {
            map.put(name, sha);
            //TODO some information
        }
    }
    public static void rm(String filename) {
        mapForRm.put(filename, getSha1OfFile(filename));
        restrictedDelete(filename);
    }
}
