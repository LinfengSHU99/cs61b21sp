package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static gitlet.Tools.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private String date;
    String log_sha1;
    // store file name and its sha1
    HashMap<String, String> map;
    private List<String> parents;
    public static Commit for_commit;
    private List<String> branch;

    // after a commit, execute this function to set an empty status, call update to commit.
    // args here are the parents of the new commit.
    public Commit(Commit ... parent_arr)  {
        map = new HashMap<>();
        branch = new ArrayList<>();
//        date = LocalDateTime.of(LocalDate.of(1970, 1,1), LocalTime.of(0,0,0)).toString();
        Date date1 = new Date();
        date1.setTime(0);
        String pattern = "EEE MMM d HH:mm:ss YYYY Z";

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        date = sdf.format(date1);
        if (parent_arr.length > 0) {
            parents = new ArrayList<>();
            for (Commit p : parent_arr) {
                parents.add(p.log_sha1);
            }
        }
        if (parents == null) return;
        for (String sha1 : parents) {
            Commit p = loadCommit(sha1);
            for (String filename : p.map.keySet()) {
                map.put(filename, p.map.get(filename));
            }
        }

    }

    public static void init() {
        for_commit = new Commit();
        for_commit.addBranch(current_branch);
        for_commit.message = "initial commit";
        for_commit.log_sha1 = getSha1OfCommit(for_commit);
        File f = new File(COMMIT_DIR.getPath() + "/" + for_commit.log_sha1);
        writeObject(f, for_commit);
    }

    public void update(String message) {
        if (!clearStage()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
            return;
        }
        addBranch(current_branch);
        this.message = message;
//        date = LocalDateTime.of(LocalDate.now(), LocalTime.now()).toString();
        String pattern = "EEE MMM d HH:mm:ss YYYY Z";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        date = sdf.format(new Date());
        log_sha1 = getSha1OfCommit(this);

        //TODO
    }

    // return false no changes have been made.
    private boolean clearStage() {
        if (Stage.map.isEmpty() && Stage.mapForRm.isEmpty()) return false;
        for (String filename : Stage.map.keySet()) {
            if (blobs.getOrDefault(Stage.map.get(filename), " ").equals(" ")) {
                blobs.put(Stage.map.get(filename), FILE_DIR.getPath() + "/" + Stage.map.get(filename));
                File file = new File(CWD.getPath() + "/" +filename);
                assert(file.exists());
                File f = new File(FILE_DIR.getPath() + "/" + Stage.map.get(filename));
                writeContents(f, readContentsAsString(file));
            }
            map.put(filename, Stage.map.get(filename));
        }
        for (String filename : Stage.mapForRm.keySet()) {
            map.remove(filename);
        }
        Stage.map.clear();
        Stage.mapForRm.clear();
        return true;
    }

    public void saveCommit() {
        File f = new File(COMMIT_DIR.getPath() + "/" + log_sha1);
        writeObject(f, this);
    }
    public void saveAsHead() {
        File f = new File(COMMIT_DIR.getPath() + "/" + "head");
//        writeObject(f, this);
        writeContents(f, this.log_sha1);
    }

    public void addBranch(String name) {
        branch.add(name);
    }

    public static Commit loadCommit(String sha1) {
        File f = new File(COMMIT_DIR.getPath() + "/" + sha1);
        return readObject(f, Commit.class);
    }
    public static Commit loadHead() {
        File f = new File(COMMIT_DIR.getPath() + "/head");
//        return readObject(f, Commit.class);
        return loadCommit(readContentsAsString(f));
    }


    public String commitInfo() {
        String t = (this.parents == null) ? "null" : this.parents.toString();
        return map.toString() + t + message + date;
    }
    public List<String> getParents() {
        return parents;
    }
    public String getDate() {
        return date;
    }
    public String getMessage() {
        return message;
    }
    public void addParents(String parent) {
        parents.add(parent);
    }
    /* TODO: fill in the rest of this class. */
}
