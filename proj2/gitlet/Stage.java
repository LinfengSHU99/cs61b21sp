package gitlet;

import java.util.HashMap;

public class Stage {
    static public HashMap<String, String> map = new HashMap<>();
//    public Stage() {
//        set = new HashSet<>();
//    }
    public static void add(String name, String sha) {
        if (!map.getOrDefault(name, "").equals(sha) && !Commit.for_commit.map.getOrDefault(name, "").equals(sha)) {
            map.put(name, sha);
            //TODO some information
        }
    }
}
