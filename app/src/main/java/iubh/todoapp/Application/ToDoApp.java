package iubh.todoapp.Application;

import android.app.Application;

public class ToDoApp extends Application {
    private String username = null;
    private int userID;
    private boolean debugMode = true;

    // getter method for UserName
    public String getUsername() {
        return this.username;
    }

    // setter method for UserName
    public void setUsername(String username) {
        this.username = username;
    }

    // getter method for UserID
    public int getUserID() {
        return this.userID;
    }

    // setter method for UserID
    public void setUserID(int userID) {
        this.userID = userID;
    }

    // getter method for DebugMode (Test)
    public boolean getDebugMode() {
        return this.debugMode;
    }

    // setter method for DebugMode (Test)
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    // check if a user is logged in or if application is in debug mode
    public boolean userLogged(){
        if (this.debugMode){
            this.initDebugMode();
        }
        if (this.username == null){
            return false;
        }
        else {
            return true;
        }
    }

    public void resetSession(){
        this.username = null;
        this.userID = -1;
    }

    // set username to DEBUGMODE for testing application
    private void initDebugMode(){
        this.username = "DEBUGMODE";
    }
}
