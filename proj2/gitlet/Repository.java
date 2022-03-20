package gitlet;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import static gitlet.Utils.*;
import static gitlet.Commit.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    // key: sha1, value:
    public static HashMap<String, Boolean> blobs = new HashMap<>();
    /* TODO: fill in the rest of this class. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        else {

            Commit.init();
            GITLET_DIR.mkdir();
        }
    }
    public static void add(String filename) {
        File file = new File(CWD.getPath() + "/" + filename);
        if (!file.exists()) {
            printAndExit("File does not exist.");
        }
        else {
            String sha = sha1(filename, readContents(file));
            Stage.add(filename, sha);
        }
    }
    public static void commit(String message) {
        for_commit.update(message);
        head = for_commit;
        for_commit = new Commit(head);
    }
    public static void status() {
        System.out.println("=== Branches ===");
        //TODO
        System.out.println("");

        System.out.println("=== Staged Files ===");
        String[] filenames = new String[Stage.map.size()];
        int cnt = 0;
        for (String filename : Stage.map.keySet()) {
            filenames[cnt++] = filename;
        }
        Arrays.sort(filenames);
        for (String filename : filenames) {
            System.out.println(filename);
        }
        System.out.println("");

        System.out.println("=== Removed Files ===");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println("=== Untracked Filed ===");
    }
    public static void rm(String filename) {
        if (Stage.map.containsKey(filename)) {
            Stage.map.remove(filename);
        }
        if (head.map.containsKey(filename)) {

        }
    }
    public static void loadStatus() {
        File f = new File(GITLET_DIR.getPath() + "/blob_info");
        if (f.exists()) {
            blobs = readObject(f, HashMap.class);
        }
        f = new File(GITLET_DIR.getPath() + "/commit_info");
        if (f.exists()) {
            Commit.for_commit = readObject(f, Commit.class);
        }
        f = new File(GITLET_DIR.getPath() + "/stage_info");
        if (f.exists()) {
            Stage.map = readObject(f, Stage.map.getClass());
        }
    }
    public static void saveStatus() {
        File blob_info = new File(GITLET_DIR.getPath() + "/blob_info");
        writeObject(blob_info, blobs);
        File commit_info = new File(GITLET_DIR.getPath() + "/commit_info");
        writeObject(commit_info, Commit.for_commit);
        File stage_info = new File(GITLET_DIR.getPath() + "/stage_info");
        writeObject(stage_info, Stage.map);
    }
}
