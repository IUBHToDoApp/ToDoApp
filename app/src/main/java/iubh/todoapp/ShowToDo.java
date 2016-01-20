package iubh.todoapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import iubh.todoapp.Application.ToDo;
import iubh.todoapp.Application.ToDoApp;
import iubh.todoapp.SQLite.SQLiteDAO;

public class ShowToDo extends AppCompatActivity {
    private SQLiteDAO sqlDB;
    private ToDo shownToDo;
    private int toDoID;
    private Calendar toDoCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toDoID = getIntent().getIntExtra("toDoID", 0);
        setContentView(R.layout.activity_show_to_do);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




        // creates and opens the database
        sqlDB = new SQLiteDAO(this);
        sqlDB.open();
        // gets ToDos from database and displays them in listview
        shownToDo = sqlDB.getToDoByID(toDoID);

        TextView shownTopic = (TextView)findViewById(R.id.show_Topic);
        shownTopic.setText(shownToDo.getTopic());
        TextView shownDesc= (TextView)findViewById(R.id.show_Description);
        shownDesc.setText(getResources().getText(R.string.todo_desc) + "\n" + shownToDo.getDesc());
        // displays the due date
        TextView shownDate= (TextView)findViewById(R.id.show_Date);
        GregorianCalendar toDoDate = shownToDo.getGregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:MM");
        String date = format.format(toDoDate.getTime());
        shownDate.setText(getResources().getText(R.string.todo_date) + " " + date);
        ImageView favIcon = (ImageView)findViewById(R.id.show_favIcon);
        if (shownToDo.getFav()) {  // if it is true -> means: is favorite
            favIcon.setImageResource(R.drawable.ic_favorite_on);
        }
        else {  // no favorite
            favIcon.setImageResource(R.drawable.ic_favorite_off);
        }
        TextView shownStatus= (TextView)findViewById(R.id.show_Status);
        TextView timeToElapse = (TextView)findViewById(R.id.show_timeToElapse);
        timeToElapse.setText("");


        if (!shownToDo.getStatus()){
            shownStatus.setText(getResources().getText(R.string.todo_status) + " " + getResources().getText(R.string.show_statusTrue));
            shownStatus.setTextColor(Color.RED);
            if (this.shownToDo.checkTimeAlreadyElapsed()) {
                timeToElapse.setText(R.string.show_alreadyElapsedMessage);
                timeToElapse.setTextColor(Color.RED);
            }
            else{
                long[] timeToElapseValues = this.shownToDo.getTimeToElapse();
                String message = getResources().getString(R.string.show_elapseMessage);

                int index = 0;

                index = message.indexOf("DD");
                message = message.substring(0, index) + timeToElapseValues[0] + message.substring(index + 2);
                index = message.indexOf("HH");
                message = message.substring(0, index) + timeToElapseValues[1] + message.substring(index + 2);
                index = message.indexOf("MM");
                message = message.substring(0, index) + timeToElapseValues[2] + message.substring(index + 2);

                timeToElapse.setText(message);
            }
        }
        else{
            shownStatus.setText(getResources().getText(R.string.todo_status) + " " + getResources().getText(R.string.show_statusFalse));
            shownStatus.setTextColor(Color.GREEN);
        }
    }
    protected void onPause(){
        super.onPause();
        //sqlDB.close();
    }

    @Override
    protected void onStop(){
        super.onStop();
        sqlDB.close();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sqlDB.open();
    }

    @Override
    protected void onResume(){
        super.onResume();
        sqlDB.open();
    }
}
