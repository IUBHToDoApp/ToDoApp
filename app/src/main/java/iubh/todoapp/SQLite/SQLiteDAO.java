package iubh.todoapp.SQLite;

/**
 * Created by Tina on 29.12.2015.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Exchanger;

import iubh.todoapp.Application.ToDo;

public class SQLiteDAO {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    // constructor
    public SQLiteDAO(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    // opens the database
    public void open(){
        try {
            database = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Log.v("SQLite", "cant open Database");
        }
    }

    // closes the database
    public void close() {
        dbHelper.close();
    }

    // inserts a new user into the database
    public void insertUser(String userName, String userPW){
        database.execSQL("INSERT INTO user('userName', 'userPW') VALUES ('" + userName + "', '" + userPW + "')");
    }

    // checks if user exists and passwort is correct -> for login
    public boolean checkUser(String username, String userPW){
        Cursor result = database.rawQuery("SELECT * FROM user", null);
        while (result.moveToNext()){
            if (username.equals(result.getString(1)) && userPW.equals(result.getString(2))){
                return true;
            }
            else{
                Log.v("DB-Inhalt", result.getString(1) + " : " + result.getString(2));
            }
        }
        Log.v("DB-Inhalt", "Kein Treffer");
        return false;
    }

    // checks if the user name already exists in database, otherwise creates new user
    public boolean registerUser(String username, String userPW){
        Cursor result = database.rawQuery("SELECT userName FROM user", null);
        while (result.moveToNext()){
            if (username.equals(result.getString(0))){
                Log.v("DB-Inhalt", "User bereits vorhanden");
                return false;
            }
        }
        this.insertUser(username, userPW);
        return true;
    }

    // gets the UserID of currently logged in user
    public int getUserIdByName(String username){
        Cursor result = database.rawQuery("SELECT userID FROM user WHERE userName = '"+username+"';" , null);
        while (result.moveToNext()){
            return result.getInt(0);
        }
        return 0;
    }

    // gets all To-Dos of user
    public List<ToDo> getUserToDosByID(int userID) {
        List<ToDo> toDoList = new ArrayList<>();

        Cursor result = database.rawQuery("SELECT todoID, todoTopic, todoDesc, todoDate, todoFav, todoStatus FROM todo WHERE userID = '"+userID+"';" , null);

        if (result != null) {

            while (result.moveToNext()) {
                boolean fav_help = Boolean.parseBoolean(result.getString(4));
                boolean status_help = Boolean.parseBoolean(result.getString(5));
                ToDo nextToDo = new ToDo(result.getInt(0), result.getString(1), result.getString(2), result.getString(3), fav_help, status_help);
                toDoList.add(nextToDo);
            }
        }
        return toDoList;
    }

    public ToDo getToDoByID(int toDoID){
        Cursor result = database.rawQuery("SELECT todoID, todoTopic, todoDesc, todoDate, todoFav, todoStatus FROM todo WHERE todoID = '"+toDoID+"';" , null);
        ToDo resultToDo = null;
        if (result != null) {

            while (result.moveToNext()) {
                boolean fav_help = Boolean.parseBoolean(result.getString(4));
                boolean status_help = Boolean.parseBoolean(result.getString(5));
                resultToDo = new ToDo(result.getInt(0), result.getString(1), result.getString(2), result.getString(3), fav_help, status_help);
            }
        }
        return resultToDo;
    }

    public List<ToDo> getUserToDosByIDDateSorted(int userID, boolean order){
        List<ToDo> toDoList = new ArrayList<>();

        Cursor result = database.rawQuery("SELECT todoID, todoTopic, todoDesc, todoDate, todoFav, todoStatus FROM todo WHERE userID = '"+userID+"' ORDER BY todoDate DESC;" , null);

        if (result != null) {

            while (result.moveToNext()) {
                boolean fav_help = Boolean.parseBoolean(result.getString(4));
                boolean status_help = Boolean.parseBoolean(result.getString(5));
                ToDo nextToDo = new ToDo(result.getInt(0), result.getString(1), result.getString(2), result.getString(3), fav_help, status_help);
                toDoList.add(nextToDo);
            }
        }
        return toDoList;
    }

    // updates To-Do (Edit Mode)
    public void updateToDo(String topic, String desc, String date, boolean fav, int toDoID){
        database.execSQL("UPDATE todo SET todoTopic = '"+topic+"', todoDesc = '"+desc+"', todoDate = '"+date+"', todoFav = '"+fav+"' WHERE todoID = '"+toDoID+"';");
    }

    // updates the favorite (on Main Activity)
    public void updateFav(boolean fav, int toDoID){
        database.execSQL("UPDATE todo SET todoFav = '"+fav+"' WHERE todoID = '"+toDoID+"';");
    }

    // updates the status (on Main Activity)
    public void updateStatus(boolean status, int toDoID){
        database.execSQL("UPDATE todo SET todoStatus = '"+status+"' WHERE todoID = '"+toDoID+"';");
    }

    // insert a new To-Do into database
    public  void insertToDo (int userID, String topic, String desc, String date, boolean fav, boolean status) {
        database.execSQL("INSERT INTO todo('userID', 'todoTopic', 'todoDesc', 'todoDate', 'todoFav', 'todoStatus') VALUES ('"+userID+"', '"+topic+"', '"+desc+"', '"+date+"', '"+fav+"', '"+status+"');");
    }

    // delete a To-Do from database
    public void  deleteToDo (int toDoID) {
        database.execSQL("DELETE FROM todo WHERE todoID = '"+toDoID+"';");
    }
}

