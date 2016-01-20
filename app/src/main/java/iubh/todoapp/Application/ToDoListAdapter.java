package iubh.todoapp.Application;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.*;

import iubh.todoapp.LoginActivity;
import iubh.todoapp.MainActivity;
import iubh.todoapp.NewToDoActivity;
import iubh.todoapp.R;
import iubh.todoapp.SQLite.SQLiteDAO;
import iubh.todoapp.ShowToDo;

public class ToDoListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<ToDo> toDoList;
    private final SQLiteDAO sqlDB;

    // static class ViewHolder for displaying To-Do information
    static class ViewHolder {
        CheckBox done_box;
        TextView task_topic;
        TextView task_date;
        ImageView image_view;
    }

    // constructor
    public ToDoListAdapter(Context context, List toDoList, SQLiteDAO sqlDB) {
        super(context, -1, toDoList);
        this.context = context;
        this.toDoList = toDoList;
        this.sqlDB = sqlDB;
    }

    public ToDo getItemObject(int position) {
        return toDoList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ToDoListAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_todo, parent, false);
            holder = new ToDoListAdapter.ViewHolder();
            holder.task_topic = (TextView) convertView.findViewById(R.id.list_todo_topic);
            holder.task_date = (TextView) convertView.findViewById(R.id.list_todo_date);
            holder.done_box = (CheckBox) convertView.findViewById(R.id.list_checkbox);
            holder.image_view = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        }
        else {
            holder = (ToDoListAdapter.ViewHolder) convertView.getTag();
        }

        convertView.setClickable(true);
        convertView.setFocusable(true);
        convertView.setLongClickable(true);
        final ToDo toDo = getItemObject(position);

        // reacts on click on button -> set To-Do status done or not done
        holder.done_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toDoList.get(position).setStatus(isChecked, sqlDB);

                // topic of To-Do will be striked out if To-Do status is "done"
                if (isChecked) {
                    holder.task_topic.setPaintFlags(holder.task_topic.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    holder.task_topic.setPaintFlags(0);
                }
            }
        });

        // reacts on click on the image view -> set fav or not
        holder.image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toDoList.get(position).getFav() == true) {
                    // Fav is true, so set it to false
                    toDoList.get(position).setFav(false, sqlDB);
                    holder.image_view.setImageResource(R.drawable.ic_favorite_off);
                } else {
                    // Fav is false, so set it to true
                    toDoList.get(position).setFav(true, sqlDB);
                    holder.image_view.setImageResource(R.drawable.ic_favorite_on);
                }
            }
        });

        // reacts on a single click on the list item
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowToDo.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("toDoID", toDoList.get(position).getID());
                context.startActivity(intent);
            }
        });

        // set values for all other fields on listview layout: layout_todo

        // topic
        holder.task_topic.setText(toDo.getTopic());
        holder.task_topic.setTextColor(Color.BLACK);
        // due date - translate to pattern DD.MM.YYYY and save in String variable
        String date = toDo.getDate();
        String year = date.substring(0,4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        String completeDate = day + "." + month + "."+ year;
        holder.task_date.setText(completeDate);
        holder.task_date.setTextColor(Color.BLACK);
        // favorite
        if (toDo.getFav()) {  // if it is true -> means: is favorite
            holder.image_view.setImageResource(R.drawable.ic_favorite_on);
        }
        else {  // no favorite
            holder.image_view.setImageResource(R.drawable.ic_favorite_off);
        }
        // status
        holder.done_box.setChecked(toDo.getStatus());
        holder.done_box.setTextColor(Color.BLACK);

        return convertView;
    }
}
