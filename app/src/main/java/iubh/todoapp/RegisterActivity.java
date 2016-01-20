package iubh.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import iubh.todoapp.SQLite.SQLiteDAO;


public class RegisterActivity extends AppCompatActivity {
    private SQLiteDAO sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sqlDB = new SQLiteDAO(this);
        sqlDB.open();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        sqlDB.open();
    }

    @Override
    protected void onResume(){
        super.onResume();
        sqlDB.open();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //sqlDB.close();
    }

    @Override
    protected void onStop(){
        super.onStop();
        sqlDB.close();
    }

    public void registerUser(View button){
        EditText username = (EditText) findViewById(R.id.register_EditTextUsername);
        EditText userPW = (EditText) findViewById(R.id.register_EditTextPassword);

        if (sqlDB.registerUser(username.getText().toString(), userPW.getText().toString())){
            Log.v("Register", "User angelegt");
            this.goToLogIn();
        }
        else{
            Log.v("Register", "User bereits vorhanden");
            Toast.makeText(RegisterActivity.this, "Benutzer bereits vorhanden", Toast.LENGTH_LONG).show();
        }

    }

    public void goToLogIn(View button){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void goToLogIn(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
