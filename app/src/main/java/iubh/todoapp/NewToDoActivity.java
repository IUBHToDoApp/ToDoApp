package iubh.todoapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.support.v7.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import iubh.todoapp.Application.ToDoApp;
import iubh.todoapp.SQLite.SQLiteDAO;

public class NewToDoActivity extends AppCompatActivity implements OnClickListener{

    private SQLiteDAO sqlDB;
    private boolean EditToDo;
    private int ToDoID;

    private EditText DueDate;
    private EditText DueTime;
    private EditText Topic;
    private EditText Desc;
    private ImageButton Fav;

    private DatePickerDialog DatePickerDialog;
    private TimePickerDialog TimePickerDialog;

    private String ToDoTopic = "";
    private String ToDoDesc = "";
    private String ToDoDate = "";
    private boolean ToDoFav = false;
    private boolean ToDoStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_to_do);

        // open SQLite Database
        sqlDB = new SQLiteDAO(this);
        sqlDB.open();

        // find views on activity
        Topic = (EditText) findViewById(R.id.editTopic);
        Desc = (EditText) findViewById(R.id.editDesc);
        DueDate = (EditText) findViewById(R.id.editDate);
        DueDate.setInputType(InputType.TYPE_NULL);
        DueDate.requestFocus();
        DueTime = (EditText) findViewById(R.id.editTime);
        DueTime.setInputType(InputType.TYPE_NULL);
        DueTime.requestFocus();
        Fav = (ImageButton) findViewById(R.id.editFav);

        // write values from intent to To-Do variables
        ToDoTopic = getIntent().getStringExtra("Topic");
        ToDoDesc = getIntent().getStringExtra("Desc");
        ToDoDate = getIntent().getStringExtra("Date");
        ToDoFav = getIntent().getExtras().getBoolean("Fav");
        ToDoStatus = getIntent().getExtras().getBoolean("Status");
        EditToDo = getIntent().getExtras().getBoolean("EditToDo");
        ToDoID = getIntent().getExtras().getInt("ToDoID");

        // write values from intent to activity views, if Topic contains a value
        if (ToDoTopic.length() != 0) {
            Topic.setText(ToDoTopic);
            Desc.setText(ToDoDesc);
            DueDate.setText(ToDoDate.substring(8, 10) + "." + ToDoDate.substring(5, 7) + "." + ToDoDate.substring(0, 4));
            DueTime.setText(ToDoDate.substring(11, 13) + ":" + ToDoDate.substring(14, 16));
            if (ToDoFav == false) {
                Fav.setImageResource(R.drawable.ic_favorite_off);
            } else {
                Fav.setImageResource(R.drawable.ic_favorite_on);
            }
        }

        setDateTimeField();
        setTimeField();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } */

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view == DueDate) {
            DatePickerDialog.show();
        } else if (view == DueTime) {
            TimePickerDialog.show();
        }
    }

    private void setDateTimeField() {

        DueDate.setOnClickListener(this);
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

        final Calendar newCalendar = Calendar.getInstance();
        newCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        DatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                DueDate.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setTimeField() {

        DueTime.setOnClickListener(this);
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.GERMANY);

        final Calendar newCalendar = Calendar.getInstance();
        TimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                newDate.set(Calendar.MINUTE, minute);
                DueTime.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);
    }

    // button click on "Cancel" -> go back to MainActivity
    public void goToMain(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // image button is clicked -> change the fav status
    public void changeFav(View v) {
        if (ToDoFav == false)
        {
            ToDoFav = true;
            Fav.setImageResource(R.drawable.ic_favorite_on);
        } else {
            ToDoFav = false;
            Fav.setImageResource(R.drawable.ic_favorite_off);
        }
    }

    public void saveToDo(View v) {
        ToDoTopic = Topic.getText().toString();
        ToDoDesc = Desc.getText().toString();

        StringBuffer DateBuffer = new StringBuffer();
        DateBuffer.append(DueDate.getText().toString().substring(6,10));
        DateBuffer.append("-");
        DateBuffer.append(DueDate.getText().toString().substring(3,5));
        DateBuffer.append("-");
        DateBuffer.append(DueDate.getText().toString().substring(0,2));
        DateBuffer.append(" ");
        DateBuffer.append(DueTime.getText().toString().substring(0,2));
        DateBuffer.append(":");
        DateBuffer.append(DueTime.getText().toString().substring(3,5));
        DateBuffer.append(":");
        DateBuffer.append("00");
        ToDoDate = DateBuffer.toString();

        int userID = ((ToDoApp) this.getApplication()).getUserID();

        // insert To-Do into database
        if (EditToDo == false) {
            sqlDB.insertToDo(userID, ToDoTopic, ToDoDesc, ToDoDate, ToDoFav, ToDoStatus);
            Toast.makeText(NewToDoActivity.this, R.string.insertSuccess, Toast.LENGTH_SHORT).show();
        } else {
        // update To-Do in database
            sqlDB.updateToDo(ToDoTopic, ToDoDesc, ToDoDate, ToDoFav, ToDoID);
            Toast.makeText(NewToDoActivity.this, R.string.editSuccess, Toast.LENGTH_SHORT).show();
        }
        // go back to Main Activity (displaying all To-Dos)
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
