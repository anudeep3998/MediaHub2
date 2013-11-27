import utils.Hash;
import utils.SQLite;

/**
 * Created by Phoenix on 11/27/13.
 * This file houses the main method, and will be the entry point for the application, at least for now
 */
public class MediaHub {
    public static void main(String[] args) {
        Hash myHasher = new Hash();
        SQLite sqLite = new SQLite();
        sqLite.initializeDb("MediaHub");
        long time = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            System.out.println(myHasher.getHash("GoUnited!" + i));
        }
        System.out.println("Hashing ended in :" + (System.currentTimeMillis() - time));
    }
}
