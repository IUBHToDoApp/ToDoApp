package iubh.todoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import iubh.todoapp.Application.ToDoApp;
import iubh.todoapp.SQLite.SQLiteDAO;

public class LoginActivity extends AppCompatActivity {
    private SQLiteDAO sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sqlDB = new SQLiteDAO(this);
        sqlDB.open();
        this.checkLogInState();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        sqlDB.open();
        this.checkLogInState();
    }

    @Override
    protected void onResume(){
        super.onResume();
        sqlDB.open();
        this.checkLogInState();
    }

    @Override
    protected void onPause(){
        super.onPause();
        sqlDB.close();
    }

    @Override
    protected void onStop(){
        super.onStop();
        sqlDB.close();
    }

    public void checkLogIn(View button){
        EditText username = (EditText) findViewById(R.id.login_EditTextUsername);
        EditText userPW = (EditText) findViewById(R.id.login_EditTextPassword);
        String stringUsername = username.getText().toString();
        String stringuserPW = userPW.getText().toString();

        if(sqlDB.checkUser(stringUsername, stringuserPW)){
            ((ToDoApp) this.getApplication()).setUsername(stringUsername);
            ((ToDoApp) this.getApplication()).setUserID(sqlDB.getUserIdByName(stringUsername));
            this.checkLogInState();
        }
        else{
            Toast.makeText(LoginActivity.this, "MISS", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /* noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } */

        return super.onOptionsItemSelected(item);
    }

    public void goToRegister(View button){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void goToRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void goToMainMenue(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void checkLogInState(){
        if ( ((ToDoApp) this.getApplication()).userLogged()){
            ((ToDoApp) this.getApplication()).setUserID(sqlDB.getUserIdByName(((ToDoApp) this.getApplication()).getUsername()));
            this.goToMainMenue();
        }
    }
}

