package todofy.cm.models;

import todofy.cm.DashboardActivity;
import todofy.cm.R;
import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import java.util.ArrayList;
import java.util.Calendar;
import android.app.Activity;

import android.content.ContentValues;
import android.provider.CalendarContract.Events;

/**
 * Created by minhazv on 5/3/2017.
 */
public class CourseAdapter extends ArrayAdapter<Course>   {
    public CourseAdapter(Context context, ArrayList<Course> courses) {
        super(context, 0, courses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Course course = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_course, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        // Populate the data into the template view using the data object
        title.setText(course.title);
        description.setText(course.description);

        final Button addReminder = (Button) convertView.findViewById(R.id.setReminder);
        addReminder.setTag(position);
        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                // Access the row position here to get the correct data item
                Course crs = getItem(position);
                // Do what you want here...
                addReminder(crs.title, getContext());
            }
        });

        // Return the completed view to render on screen
        return convertView;

    }

    public void addReminder(String title, Context ctx){
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", false);
        intent.putExtra("rule", "FREQ=DAILY");
        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
        intent.putExtra("title", title);
        ctx.startActivity(intent);
    }
}