package todofy.cm.models;

import todofy.cm.DashboardActivity;
import todofy.cm.MainActivity;
import todofy.cm.R;
import todofy.cm.app.AppConfig;

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
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import android.content.ContentValues;
import android.provider.CalendarContract.Events;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        final Button favButton = (Button) convertView.findViewById(R.id.setFavorite);
        if (!course.isFavorite )
            favButton.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
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

        favButton.setTag(position);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                // Access the row position here to get the correct data item
                Course crs = getItem(position);
                updateCourses(position, crs, getContext(), view);
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

    private void updateCourses(final int position, final Course selectedCrs, final Context ctx, final View view) {
        // Tag used to cancel the request
        String tag_string_req = "req_courses";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Loading courses ...");
        showDialog(pDialog);

        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_COURSES(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog(pDialog);

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        // We get weather info (This is an array)
//                        JSONArray jArr = jObj.getJSONArray("data");
                        if (!selectedCrs.isFavorite) {
                            view.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(ctx, "Courses set as favorite", Toast.LENGTH_LONG).show();
                        } else {
                            view.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(ctx, "Courses unset as favorite", Toast.LENGTH_LONG).show();
                        }

                        DashboardActivity.listSearched.get(position).isFavorite = !selectedCrs.isFavorite;
                        DashboardActivity.listLoaded.get(position).isFavorite = !selectedCrs.isFavorite;
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(ctx,
                                "Unable to update", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog(pDialog);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", DashboardActivity.uuid);
                params.put("email", DashboardActivity.uemail);
                params.put("set", String.valueOf(selectedCrs.id));
                params.put("currentState", (selectedCrs.isFavorite) ? "set" : "unset");
                return params;
            }

        };
        queue.add(strReq);
    }

    private void showDialog(ProgressDialog pDialog) {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog(ProgressDialog pDialog) {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}