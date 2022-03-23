package capers;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = new File(".capers"); // TODO Hint: look at the `join`
                                            //      function in Utils

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        // TODO
        CAPERS_FOLDER.mkdir();

        File story = new File("./.capers/story.txt");
        try {
            story.createNewFile();
        } catch (IOException e){

        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        // TODO
        try {
            File story = new File("./.capers/story.txt");
            FileOutputStream out = new FileOutputStream(story, true);
            out.write((text + "\n").getBytes(StandardCharsets.UTF_8));
            FileInputStream in = new FileInputStream(story);
            String t = new String(in.readAllBytes(), StandardCharsets.UTF_8);
//            StringBuilder sb = new StringBuilder();
//            int n = 0;
//            while ((n = in.read()) != -1) {
//                sb.append((char)n);
//            }
            System.out.println(t);
            out.close();
            in.close();
        } catch (IOException e) {

        }
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        // TODO
        Dog dog = new Dog(name, breed, age);
        try {
            File dg = new File("./.capers/" + name);
            if (!dg.exists()) {
                dg.createNewFile();
            }
            FileOutputStream file_out = new FileOutputStream(dg);
            ObjectOutputStream d = new ObjectOutputStream(file_out);
            d.writeObject(dog);
            d.close();
            file_out.close();
        } catch (IOException e) {

        }
        System.out.println("" + dog);
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        // TODO
        try {
            File dg = new File("./.capers/" + name);
            FileInputStream file_in = new FileInputStream(dg);
            ObjectInputStream in = new ObjectInputStream(file_in);
            Dog dog = (Dog) in.readObject();
            dog.haveBirthday();
            FileOutputStream file_out = new FileOutputStream(dg);
            ObjectOutputStream out = new ObjectOutputStream(file_out);
            out.writeObject(dog);
            out.close();
            in.close();
            file_out.close();
            file_in.close();
        } catch (IOException | ClassNotFoundException c) {
        }
    }
}
