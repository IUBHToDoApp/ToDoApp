package iubh.todoapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import iubh.todoapp.Application.ToDo;
import iubh.todoapp.Application.ToDoApp;
import iubh.todoapp.Application.ToDoListAdapter;
import iubh.todoapp.SQLite.SQLiteDAO;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class MainActivity extends AppCompatActivity  {

    // Variables
    private SQLiteDAO sqlDB;
    List<ToDo> toDoList;
    private int userID;
    private boolean showOnlyFav = false;
    private boolean showOnlyDone = false;
    private boolean showOnlyToday = false;
    private File todoPDF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // button for creating a new To-Do
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNewToDo();
            }
        });



        // creates and opens the database
        sqlDB = new SQLiteDAO(this);
        sqlDB.open();
        // gets ToDos from database and displays them in listview
        this.userID = ((ToDoApp) this.getApplication()).getUserID();
        toDoList = sqlDB.getUserToDosByID(this.userID);
        this.displayToDoList(toDoList);
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
    protected void onRestart() {
        super.onRestart();
        sqlDB.open();
        // gets ToDos from database and displays them in list view
        this.userID =  ((ToDoApp)this.getApplication()).getUserID();
        toDoList = sqlDB.getUserToDosByID(this.userID);
        this.displayToDoList(toDoList);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sqlDB.open();
        // gets ToDos from database and displays them in list view
        this.userID =  ((ToDoApp)this.getApplication()).getUserID();
        toDoList = sqlDB.getUserToDosByID(this.userID);
        this.displayToDoList(toDoList);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // close navigation drawer if it's open
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        // otherwise: do normal BackPressed actions (from super class)
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        String userName = ((ToDoApp)this.getApplication()).getUsername();
        TextView tvUser = (TextView) findViewById(R.id.UserName);
        tvUser.setText(userName);
        return true;
    }

    @Override
    // handles action bar item clicks
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // button "Logout" has been pressed
        if (id == R.id.action_logout) {
            this.resetSession();
            this.goToLogIn();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    // intent to start activity login
    private void goToLogIn(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    // intent to start activity for creating new ToDos
    private void goToNewToDo(){
        Intent intent = new Intent(this, NewToDoActivity.class);
        // add empty ToDoValues
        intent.putExtra("Topic", "");
        intent.putExtra("Desc", "");
        intent.putExtra("Date", "");
        intent.putExtra("Fav", false);
        intent.putExtra("Status", false);
        intent.putExtra("EditToDo", false);
        intent.putExtra("ToDoID", 0);
        startActivity(intent);
    }

    // displays To-Do list without sorting or filter
    private void displayToDoList(List toDoList){
        // create new ToDoListAdapter and display list with adapter
        List<ToDo> shownList = new ArrayList<ToDo>(this.toDoList);
        String dateString = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        for (int i = 0; i < shownList.size(); i++) {
            boolean deleted = false;
            if (this.showOnlyFav && !deleted){
                if (!shownList.get(i).getFav()){
                    shownList.remove(i);
                    i--;
                    deleted = true;
                }
            }
            if (this.showOnlyDone && !deleted){
                if (shownList.get(i).getStatus()){
                    shownList.remove(i);
                    i--;
                    deleted = true;
                }
            }
            if (this.showOnlyToday && !deleted){
                String date = shownList.get(i).getDate();
                String year = date.substring(0,4);
                String month = date.substring(5, 7);
                String day = date.substring(8, 10);
                String completeDate = day + "." + month + "."+ year;


                if (!completeDate.equals(dateString)){
                    shownList.remove(i);
                    i--;
                    deleted = true;
                }
            }
        }

        ToDoListAdapter adapter = new ToDoListAdapter(getApplicationContext(), shownList, sqlDB);
        final ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);
    }

    @Override
    // creates the context menu which is displayed when long clicking on a list view item
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if (v.getId() == R.id.listView) {
            getMenuInflater().inflate(R.menu.context_menu_main, menu);
        }
    }

    @Override
    // handles context menu item clicks
    public boolean onContextItemSelected(MenuItem item) {
        // gets the item on which the context menu has been displayed
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ToDo listItem = toDoList.get(info.position);

        switch (item.getItemId()) {
            case R.id.context_edit:
                // todo: change to start intent for new activity where the item can be changed
                Intent intent = new Intent(this, NewToDoActivity.class);
                // add To-Do values
                intent.putExtra("Topic", listItem.getTopic());
                intent.putExtra("Desc", listItem.getDesc());
                intent.putExtra("Date", listItem.getDate());
                intent.putExtra("Fav", listItem.getFav());
                intent.putExtra("Status", listItem.getStatus());
                intent.putExtra("EditToDo", true);
                intent.putExtra("ToDoID", listItem.getID());
                startActivity(intent);
                return true;

            case R.id.context_delete:
                // delete To-Do item from database and list
                sqlDB.deleteToDo(listItem.getID());
                toDoList.remove(info.position);
                this.displayToDoList(toDoList);
                return true;

            case R.id.context_mail:     // create e-mail intent to send selected To-Do information in e-mail body
                StringBuffer emailText = new StringBuffer();
                emailText.append(getString(R.string.todo_topic) + listItem.getTopic() + "\n");
                emailText.append(getString(R.string.todo_desc) + listItem.getDesc() + "\n");
                emailText.append(getString(R.string.todo_date) + listItem.getDate() + "\n");
                emailText.append(getString(R.string.todo_fav) + listItem.getFav() + "\n");
                emailText.append(getString(R.string.todo_status) + listItem.getStatus() + "\n");

                Intent intent_email = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
                intent_email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject));
                intent_email.putExtra(Intent.EXTRA_TEXT, emailText.toString());

                try {
                    startActivity(intent_email);
                    Log.i("Finished sending email", "");
                }
                catch (ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "An error occurred while sending e-mail", Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    // reset session for logout
    private void resetSession(){
        ((ToDoApp)this.getApplication()).resetSession();
        this.toDoList = null;
        this.userID = -1;
    }

    // create PDF document for all To-Dos
    private File createPDF() throws FileNotFoundException, DocumentException {

        File myFile = new File(Environment.getExternalStorageDirectory(), "IUBH_ToDo/ToDos.pdf");

        try {
            if (!myFile.exists()) {
                myFile.getParentFile().mkdirs();
                myFile.createNewFile();
            }

            // create a new PDF document
            Document document = new Document(PageSize.A4, 20, 15, 15, 15);
            PdfWriter.getInstance(document, new FileOutputStream(myFile));
            document.open();

            // write all To-Dos in the PDF document
            ToDo tmpToDo;

            for (int i = 0; i < toDoList.size(); i++) {

                tmpToDo = toDoList.get(i);
                String topic = tmpToDo.getTopic();
                String desc = tmpToDo.getDesc();
                String date = tmpToDo.getDate();
                boolean fav = tmpToDo.getFav();
                boolean status = tmpToDo.getStatus();

                document.add(new Paragraph(getString(R.string.todo_topic) + " " + topic));
                document.add(new Paragraph(getString(R.string.todo_desc) + " " + desc));
                document.add(new Paragraph(getString(R.string.todo_topic) + " " + date));
                document.add(new Paragraph(getString(R.string.todo_fav) + " " + fav));
                document.add(new Paragraph(getString(R.string.todo_status) + " " + status));
                document.add( Chunk.NEWLINE );  // add blank line
            }
            document.close();
        }

        catch (Exception ex)
        {
            throw new FileNotFoundException();
        }
        return myFile.getAbsoluteFile();
    }
}
