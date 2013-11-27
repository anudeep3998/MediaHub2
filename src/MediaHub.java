import utils.SQLite;

/**
 * Created by Phoenix on 11/27/13.
 */
public class MediaHub {
    public static void main(String[] args) {

        SQLite sqLite = new SQLite();
        sqLite.initializeDb("MediaHub");
    }
}
