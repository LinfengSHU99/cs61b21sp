package gitlet;
import java.io.File;
import java.time.ZoneId;
import java.util.*;
import static gitlet.Utils.*;
import static gitlet.Commit.*;
import static gitlet.Tools.*;
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

    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");
    public static final File FILE_DIR = join(GITLET_DIR, "file_object");
    public static String current_branch;
    // key: sha1, value:
    public static HashMap<String, String> blobs = new HashMap<>();
    // store branch name as key and corresponding head sha1 as value.
    public static HashMap<String, String> branch_head = new HashMap<>();
    // store all commits
    public static LinkedHashSet<String> all_commits = new LinkedHashSet<>();
    // current head
    public static Commit head;
    /* TODO: fill in the rest of this class. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        else {
            GITLET_DIR.mkdir();
            COMMIT_DIR.mkdir();
            FILE_DIR.mkdir();
            current_branch = "master";

            Commit.init();
            branch_head.put("master", for_commit.log_sha1);
            for_commit.saveAsHead();
            all_commits.add(for_commit.log_sha1);
            for_commit = new Commit(for_commit);
        }
    }
    public static void add(String filename) {
        File file = new File(CWD.getPath() + "/" + filename);
        if (!file.exists()) {
            printAndExit("File does not exist.");
        }
        else {
            String sha = getSha1OfFile(filename);
            Stage.add(filename, sha);
        }
    }
    public static void commit(String message) {
        for_commit.update(message);
        branch_head.put(current_branch, for_commit.log_sha1);
        for_commit.saveCommit();
        for_commit.saveAsHead();
        all_commits.add(for_commit.log_sha1);
        for_commit = new Commit(for_commit);
    }
    public static void status() {
        System.out.println("=== Branches ===");
        List<String> branchnames = new ArrayList<>(branch_head.keySet());
        branchnames.sort(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });
        for (String name : branchnames) {
            if (current_branch.equals(name)) System.out.print("*");
            System.out.println(name);
        }
        System.out.println("");

        System.out.println("=== Staged Files ===");
        ArrayList<String> filenames = new ArrayList<>(Stage.map.keySet());
        filenames.sort(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });
        for (String filename : filenames) {
            System.out.println(filename);
        }
        System.out.println("");

        System.out.println("=== Removed Files ===");
        filenames = new ArrayList<>(Stage.mapForRm.keySet());
        filenames.sort(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });
        for (String filename : filenames) {
            System.out.println(filename);
        }
        System.out.println("");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println("=== Untracked Filed ===");
    }
    public static void rm(String filename) {
        boolean flag = true;
        if (Stage.map.containsKey(filename)) {
            Stage.map.remove(filename);
            flag = false;
        }
        head = Commit.loadHead();
        if (head.map.containsKey(filename)) {
            flag = false;
            Stage.rm(filename);
        }
        if (flag) printAndExit("No reason to remove the file.");
    }
    public static void log() {
        Commit p = head;
        List<String> parents = p.getParents();
        ZoneId zoneid = ZoneId.systemDefault();
        while (p != null) {
            System.out.println("===");
            System.out.println("commit " + p.log_sha1);
            System.out.println("Date: " + p.getDate());
            System.out.println(p.getMessage() + "\n");
            if (parents != null) {
                p = Commit.loadCommit(parents.get(0));
                parents = p.getParents();
            }
            else p = null;

        }
        //TODO need modification for branch
    }

    public static void checkoutFile(String filename) {
        String file_sha1 = head.map.getOrDefault(filename, "");
        if (file_sha1.equals("")) printAndExit("File does not exist in that commit.");
        File reference_file = new File(FILE_DIR.getPath() + "/" + file_sha1);
        File overwrite_file = new File(CWD.getPath() + "/" + filename);
        writeContents(overwrite_file, readContentsAsString(reference_file));
        Stage.map.keySet().remove(filename);
        Stage.mapForRm.keySet().remove(filename);
    }
    public static void checkoutCommitFile(String commitID, String filename) {
        if (!all_commits.contains(commitID)) printAndExit("No commit with that id exists.");
        Commit temp = head;
        head = Commit.loadCommit(commitID);
        checkoutFile(filename);
        head = temp;
    }
    public static void checkoutBranch(String name) {
        String head_sha1 = branch_head.getOrDefault(name, "");
        if (head_sha1.equals("")) printAndExit("No such branch exists.");
        if (head_sha1.equals(current_branch)) printAndExit("No need to checkout the current branch.");
        Commit temp = loadCommit(head_sha1);
        List<String> file_list = plainFilenamesIn(CWD);
        assert file_list != null;
        for (String filename : file_list) {
            if (temp.map.containsKey(filename) && !head.map.containsKey(filename))
                printAndExit("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        // delete the files which are tracked by checked-out branch but not tracked by current head.
        for (String filename : file_list) {
            if (!temp.map.containsKey(filename) && head.map.containsKey(filename))
                restrictedDelete(filename);
        }
        for (String filename : temp.map.keySet()) {
            File reference_file = new File(FILE_DIR.getPath() + "/" + temp.map.get(filename));
            File overwrite_file = new File(CWD.getPath() + "/" + filename);
            String content = readContentsAsString(reference_file);
            writeContents(overwrite_file, content);
        }
        current_branch = name;
        Stage.mapForRm.clear();
        Stage.map.clear();
    }

    public static void branch(String name) {
        if (branch_head.containsKey(name)) printAndExit("A branch with that name already exists.");
        head.addBranch(name);
        branch_head.put(name, head.log_sha1);
    }

    public static void merge(String branch_name) {
        if (!Stage.mapForRm.isEmpty() || !Stage.map.isEmpty()) printAndExit("You have uncommitted changes.");
        if (!branch_head.containsKey(branch_name)) printAndExit("A branch with that name does not exist.");
        if (current_branch.equals(branch_name)) printAndExit("Cannot merge a branch with itself.");
        Commit cur_branch_head = Commit.loadCommit(branch_head.get(current_branch));
        Commit given_branch_head = Commit.loadCommit(branch_head.get(branch_name));
        ArrayList<String> parents_of_cur_branch = new ArrayList<>();
        ArrayList<String> parents_of_given_branch = new ArrayList<>();
        Queue<String> queue = new ArrayDeque<>();
        Commit t = cur_branch_head;
        parents_of_cur_branch.add(t.log_sha1);
        while (t.getParents() != null || !queue.isEmpty()) {
            for (String s : t.getParents()) {
                if (!queue.contains(s)) {
                    queue.add(s);
                }
            }
            t = Commit.loadCommit(queue.poll());
            parents_of_cur_branch.add(t.log_sha1);
        }
        t = given_branch_head;
        parents_of_given_branch.add(t.log_sha1);
        while (t.getParents() != null || !queue.isEmpty()) {
            for (String s : t.getParents()) {
                if (!queue.contains(s)) {
                    queue.add(s);
                }
            }
            t = Commit.loadCommit(queue.poll());
            parents_of_given_branch.add(t.log_sha1);
        }
        int i = parents_of_cur_branch.size() - 1;
        int j = parents_of_given_branch.size() - 1;
        String common_ancestor = "";
        while (i > 0 || j > 0) {
            if (parents_of_cur_branch.get(i).equals(parents_of_given_branch.get((j)))){
                common_ancestor = parents_of_cur_branch.get(i);
            }
            i = (i > 0) ? i - 1 : 0;
            j = (j > 0) ? j - 1 : 0;
        }
        if (common_ancestor.equals(given_branch_head.log_sha1)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (common_ancestor.equals(cur_branch_head.log_sha1)) {
            checkoutBranch(branch_name);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        Commit ancestor = Commit.loadCommit(common_ancestor);
        Set<String> cur_file_set = cur_branch_head.map.keySet();
        for (String filename : given_branch_head.map.keySet()) {
            if (!ancestor.map.getOrDefault(filename, "").equals(given_branch_head.map.get(filename))) {
                if (!cur_branch_head.map.containsKey(filename)) printAndExit("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        boolean conflict = false;
        for (String filename : given_branch_head.map.keySet()) {
            String given_sha1 = given_branch_head.map.get(filename);
            String cur_sha1 = cur_branch_head.map.getOrDefault(filename, "");
            String anc_sha1 = ancestor.map.getOrDefault(filename, "");
            // 3
            if (!anc_sha1.equals(given_sha1)) {
                // 1 5
                if (anc_sha1.equals(cur_sha1)) {
                    checkoutCommitFile(given_branch_head.log_sha1, filename);
                    Stage.map.put(filename, given_sha1);
                }
                // 8
                else if (!anc_sha1.equals(cur_sha1)) {
                    if (!cur_sha1.equals(given_sha1)) {
                        conflict = true;
                        File overwrite_file = new File(CWD.getPath() + "/" + filename);
                        File reference_file = new File(FILE_DIR.getPath() + "/" + cur_sha1);
                        String text = "<<<<<<< HEAD\n";
                        if (!cur_sha1.equals("")) {
                            text += readContentsAsString(reference_file);
                        }
                        text += "=======\n";
                        reference_file = new File(FILE_DIR.getPath() + "/" + given_sha1);
                        text += readContentsAsString(reference_file);
                        text += ">>>>>>>\n";
                        writeContents(overwrite_file, text);
                        Stage.map.put(filename, getSha1OfFile(filename));
                    }
                }
            }
            // 2 7
            else if (anc_sha1.equals(given_sha1)) {
                if (!anc_sha1.equals(cur_sha1)) {
                }
            }
            cur_file_set.remove(given_sha1);
        }
        // file which doesn't exist in given branch.
        for (String filename : cur_file_set) {
            String anc_sha1 = ancestor.map.get(filename);
            String cur_sha1 = cur_branch_head.map.get(filename);
            // 6
            if (anc_sha1.equals(cur_sha1)) {
                Stage.mapForRm.put(filename, cur_sha1);
            }
            // 4
            else if (anc_sha1.equals("")) {

            }
        }
        String message = "Merged " + branch_name + " into " + current_branch + ".";
        if (conflict) {
            message += " Encountered a merge conflict.";
        }
        for_commit.update(message);
        branch_head.put(current_branch, for_commit.log_sha1);
        for_commit.addParents(given_branch_head.log_sha1);
        for_commit.saveCommit();
        for_commit.saveAsHead();
        all_commits.add(for_commit.log_sha1);
        for_commit = new Commit(for_commit);
        // TODO implementation for standard scenarios
    }

    public static void globalLog() {
        Commit temp;
        for (String commit_sha1 : all_commits) {
            temp = loadCommit(commit_sha1);
            System.out.println("===");
            System.out.println("commit " + temp.log_sha1);
            System.out.println("Date: " + temp.getDate());
            System.out.println(temp.getMessage() + "\n");
        }
    }

    public static void rmBranch(String branch_name) {
        if (!branch_head.containsKey(branch_name)) {
            printAndExit("A branch with that name does not exist.");
        }
        else if (branch_name.equals(current_branch)) printAndExit("Cannot remove the current branch.");
        branch_head.remove(branch_name);
    }

    public static void loadStatus() {
        File f = new File(GITLET_DIR.getPath() + "/blob_info");
        if (f.exists()) {
            blobs = readObject(f, HashMap.class);
        }
        f = new File(GITLET_DIR.getPath() + "/commit/for_commit_info");
        if (f.exists()) {
            Commit.for_commit = readObject(f, Commit.class);
        }
        f = new File(GITLET_DIR.getPath() + "/stage.map_info");
        if (f.exists()) {
            Stage.map = readObject(f, Stage.map.getClass());
        }
        f = new File(GITLET_DIR.getPath() + "/stage.mapForRm_info");
        if (f.exists()) {
            Stage.mapForRm = readObject(f, Stage.mapForRm.getClass());
        }
        f = new File(COMMIT_DIR.getPath() + "/head");
        if (f.exists()) head = readObject(f, Commit.class);
        f = new File(GITLET_DIR.getPath() + "/current_branch");
        if (f.exists()) current_branch = readContentsAsString(f);
        f = new File(GITLET_DIR.getPath() + "/branch_head");
        if (f.exists()) branch_head = readObject(f, branch_head.getClass());
        f = new File(GITLET_DIR.getPath() + "/all_commits_info");
        if (f.exists()) all_commits = readObject(f, all_commits.getClass());
    }
    public static void saveStatus() {
        File current_branch_info = new File(GITLET_DIR.getPath() + "/current_branch");
        writeContents(current_branch_info, current_branch);
        File branch_head_info = new File(GITLET_DIR.getPath() + "/branch_head");
        writeObject(branch_head_info, branch_head);
        File blob_info = new File(GITLET_DIR.getPath() + "/blob_info");
        writeObject(blob_info, blobs);
        File commit_info = new File(GITLET_DIR.getPath() + "/commit/for_commit_info");
        writeObject(commit_info, Commit.for_commit);
        File stage_info = new File(GITLET_DIR.getPath() + "/stage.map_info");
        writeObject(stage_info, Stage.map);
        File stage_info2 = new File(GITLET_DIR.getPath() + "/stage.mapForRm_info");
        writeObject(stage_info2, Stage.mapForRm);
        File all_commits_info = new File(GITLET_DIR.getPath() + "/all_commits_info");
        writeObject(all_commits_info, all_commits);
    }
}
