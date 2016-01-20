package iubh.todoapp.SQLite;

/**
 * Created by Tina on 29.12.2015.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/*import de.tasmedia.iubh.iubh_todoapp.Application.IUBH_ToDoApp;*/

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "IUBH_ToDoApp.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE_USER_TABLE = "CREATE TABLE user (userID INTEGER PRIMARY KEY AUTOINCREMENT, userName TEXT NOT NULL UNIQUE, userPW TEXT);";
    private static final String DATABASE_CREATE_USER_TODOS = "CREATE TABLE todo (todoID INTEGER PRIMARY KEY AUTOINCREMENT, userID INTEGER NOT NULL, todoTopic TEXT, todoDesc TEXT, todoDate DATETIME, todoFav BOOLEAN, todoStatus BOOLEAN, FOREIGN KEY(userID) REFERENCES user(userID));";

    // constructor
    public SQLiteHelper(Context context){
        // creates the database
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creating tables in database
    public void onCreate(SQLiteDatabase db){
        db.execSQL(DATABASE_CREATE_USER_TABLE);
        db.execSQL((DATABASE_CREATE_USER_TODOS));

        // fügt Beispielsdaten für einen Test hinzu todo: muss bei richtigem Betrieb entfernt werden
        if (true){
            db.execSQL("INSERT INTO user('userName', 'userPW') VALUES ('DEBUGMODE', '')");
            for (int i = 0; i < 6; i++){
                String fav = "false";
                if (i % 3 == 0) {
                    fav = "true";
                }
                db.execSQL("INSERT INTO todo('userID', 'todoTopic', 'todoDesc', 'todoDate', 'todoFav', 'todoStatus') VALUES ('1', 'ToDo Number: "+i+"', '"+i+"disidfj ciscisc djsdssc scs csicihsc', '2016-01-"+(i+14)+" 10:25:00', '"+fav+"', '"+fav+"');");
            }
        }
    }



    // upgrading the database (includes deleting all old data!)
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.w(SQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS todo");
        onCreate(db);
    }


}
