package iubh.todoapp.Application;

import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

import iubh.todoapp.SQLite.SQLiteDAO;

public class ToDo {

    // variables
    int toDoID;
    String toDoTopic;
    String toDoDesc;
    String toDoDate;
    boolean toDoFav;
    boolean toDoStatus;

    // constructor
    public ToDo(int ID, String topic, String desc, String date, boolean fav, boolean status){
        this.toDoID = ID;
        this.toDoTopic = topic;
        this.toDoDesc = desc;
        this.toDoDate = date;
        this.toDoFav = fav;
        this.toDoStatus = status;
    }

    // getter method for ToDoID
    public int getID() {
        return toDoID;
    }


    // getter & setter method for ToDoTopic
    public String getTopic() {
        return toDoTopic;
    }
    /*
    public void setTopic(String topic, SQLiteDAO sqlDB) {
        this.toDoTopic = topic;
        // update of DB
        sqlDB.updateTopic(topic, this.toDoID);
    } */

    // getter & setter method for ToDoDesc
    public String getDesc(){
        return toDoDesc;
    }
    /*
    public void setDesc(String desc, SQLiteDAO sqlDB) {
        this.toDoDesc = desc;
        // update of DB
        sqlDB.updateDesc(desc, this.toDoID);
    } */

    // getter & setter method for ToDoDate
    public String getDate(){
        return toDoDate;
    }
    /*
    public void setDate(String date, SQLiteDAO sqlDB) {
        this.toDoDate = date;
        // update of DB
        sqlDB.updateDate(date, this.toDoID);
    } */

    // getter & setter method for ToDoFav
    public boolean getFav() {
        return toDoFav;
    }
    public void setFav(boolean fav, SQLiteDAO sqlDB) {
        this.toDoFav = fav;
        // update of DB
        sqlDB.updateFav(fav, this.toDoID);
    }

    // getter & setter method for ToDoStatus
    public boolean getStatus() {
        return toDoStatus;
    }
    public void setStatus(boolean status, SQLiteDAO sqlDB) {
        this.toDoStatus = status;
        // update of DB
        sqlDB.updateStatus(status, this.toDoID);
    }

    public GregorianCalendar getGregorianCalendar(){
        GregorianCalendar toDoCal = new GregorianCalendar();
        String oldDate = this.getDate();

        try{
            int toDoYear = Integer.parseInt(oldDate.substring(0, 4));
            int toDoMonth = Integer.parseInt(oldDate.substring(5,7));
            int toDoDay = Integer.parseInt(oldDate.substring(8,10));
            int toDoHour = Integer.parseInt(oldDate.substring(11,13));
            int toDoMinute = Integer.parseInt(oldDate.substring(14,16));

            toDoCal.set(toDoYear, toDoMonth - 1, toDoDay, toDoHour, toDoMinute);
        } catch (NumberFormatException e){
            e.printStackTrace();
            Log.e("NumberFormatException", oldDate + "|" + oldDate.substring(11,13));
        }
        Log.d("Calendar", toDoCal.getTime().toString());
        return toDoCal;
    }

    public long[] getTimeToElapse(){
        long[] resultArray = {0,0,0};
        Calendar currentTime = new GregorianCalendar();
        Calendar toDoTime = this.getGregorianCalendar();

        long differenceTime = toDoTime.getTimeInMillis() - currentTime.getTimeInMillis();

        if (differenceTime > 86400000){
            resultArray[0] = differenceTime / 86400000;
            differenceTime = differenceTime - (resultArray[0] * 86400000);
        }
        if (differenceTime > 3600000){
            resultArray[1] = differenceTime / 3600000;
            differenceTime = differenceTime - (resultArray[1] * 3600000);
        }
        resultArray[2] = differenceTime / 60000;

        return resultArray;
    }

    public boolean checkTimeAlreadyElapsed(){
        Calendar currentTime = new GregorianCalendar();
        Calendar toDoTime = this.getGregorianCalendar();

        if (currentTime.after(this.getGregorianCalendar())){
            return true;
        }
        else{
            return false;
        }

    }

}
