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
        StringBuilder stringBuilder=new StringBuilder();
        long time = System.currentTimeMillis();
        for (int i = 5; i < 25; i++) {
            stringBuilder.setLength(0);
            stringBuilder.append("GoUnited!!!").append(i);
            //System.out.println(String.valueOf(stringBuilder));
            sqLite.addWatchedDirectory("GoUnited", String.valueOf(stringBuilder));
        }
        System.out.println("Hashing ended in :" + (System.currentTimeMillis() - time));
    }
}
