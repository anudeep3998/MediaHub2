package utils.constants;

/**
 * Created by Phoenix on 11/28/13.
 */
public enum MyQueries {
    INSERT_WATCHED_DIRECTORY,
    INSERT_MEDIA_ITEM,
    CREATE_WATCHED_DIRECTORY_TABLE,
    CREATE_MEDIA_ITEM_TABLE,
    SEARCH_MEDIA_ITEM;

    private static final int size = MyQueries.values().length;  //create a size variable to get the size easily

    public static int getSize(){
        return size;
    }

}
