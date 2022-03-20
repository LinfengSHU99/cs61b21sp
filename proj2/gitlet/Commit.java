package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static gitlet.Repository.blobs;
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
    private String log_sha1;
    HashMap<String, String> map;
    private List<Commit> parents;
    public static Commit for_commit;
    public static Commit head;
    private String branch;

    // after a commit, execute this function to set an empty status, call update to commit.
    public Commit(Commit ... parent_arr)  {
        map = new HashMap<>();
        date = LocalDateTime.of(LocalDate.of(1970, 1,1), LocalTime.of(0,0,0)).toString();
        if (parent_arr.length > 0) {
            parents = new ArrayList<>();
            parents.addAll(Arrays.asList(parent_arr));
        }
        if (parents == null) return;
        for (Commit parent : parents) {
            for (String filename : parent.map.keySet()) {
                map.put(filename, parent.map.get(filename));
            }
        }
        branch = parents.get(0).branch;
    }
//    public Commit(String message) {
//        this.message = message;
//        date = LocalDate.now().toString();
//
//    }
    public static void init() {
        for_commit = new Commit();
        for_commit.branch = "master";
        for_commit.message = "initial commit";
        for_commit.log_sha1 = sha1(for_commit.map.toString(), "null", for_commit.message.toString(), for_commit.date.toString());
    }

    public void update(String message) {
        if (!clearStage()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
            return;
        }
        this.message = message;
        date = LocalDateTime.of(LocalDate.now(), LocalTime.now()).toString();
        String parents_string = (parents == null) ? "null" : parents.toString();
        log_sha1 = sha1(map.toString(), parents_string, message, date);
        //TODO
    }

    // return false no changes have been made.
    private boolean clearStage() {
        if (Stage.map.isEmpty()) return false;
        for (String filename : Stage.map.keySet()) {
            if (!blobs.getOrDefault(Stage.map.get(filename), false)) {
                blobs.put(Stage.map.get(filename), true);
            }
            map.put(filename, Stage.map.get(filename));
        }
        Stage.map.clear();
        return true;
    }

    public void addParent(Commit parent) {
        for_commit.parents = new ArrayList<>();
        for_commit.parents.add(parent);
    }
    /* TODO: fill in the rest of this class. */
}
