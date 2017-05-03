package todofy.cm;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.ContentValues;
import android.provider.CalendarContract.Events;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import todofy.cm.app.AppConfig;
import todofy.cm.app.Courses;
import todofy.cm.helper.SessionManager;
import todofy.cm.helper.SQLiteHandler;
import todofy.cm.models.Course;
import todofy.cm.models.CourseAdapter;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = DashboardActivity.class.getSimpleName();

    private TextView txtName;
    private TextView txtEmail, txtSubtitle;
    private Button btnLogout, btnSetReminder, btnProfile;
    private LinearLayout setReminderView;
    private ListView listCourses;

    private SQLiteHandler db;
    private SessionManager session;

    private ArrayList<Course> listSearched, listLoaded;
    private boolean courseLoaded = false;
    private ProgressDialog pDialog;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        txtName = (TextView) findViewById(R.id.name);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnProfile = (Button) findViewById(R.id.btnProfile);
        listCourses = (ListView) findViewById(R.id.listCourses);

        listLoaded = new ArrayList<Course>();
        listSearched = new ArrayList<Course>();
        loadCourses();

        // Create the adapter to convert the array to views
        CourseAdapter adapter = new CourseAdapter(this, listSearched);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.listCourses);
        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        // Profile button click event
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        ProfileActivity.class);
                startActivity(i);
                finish();
            }
        });

//        final Spinner dropdown = (Spinner) findViewById(R.id.SelectCourse);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Courses.items);
//        dropdown.setAdapter(adapter);

//        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                setReminderView.setVisibility(View.VISIBLE);
//                txtSubtitle.setText("Set Reminder for " +Courses.items[i] +" in your calendar.");
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                setReminderView.setVisibility(View.INVISIBLE);
//            }
//        });

//        btnSetReminder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),
//                        "Setting course reminder", Toast.LENGTH_LONG)
//                        .show();
//
//                addReminder(Courses.items[(int)(dropdown.getSelectedItemId())]);
//            }
//        });

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        Intent i = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(i);
        finish();
    }

    private void loadCourses() {
        // Tag used to cancel the request
        String tag_string_req = "req_courses";

        pDialog.setMessage("Loading courses ...");
        showDialog();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_COURSES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        courseLoaded = true;
                        // We get weather info (This is an array)
                        JSONArray jArr = jObj.getJSONArray("data");
                        for (int i = 0; i < jArr.length(); i++) {
                            JSONObject _jobj = jArr.getJSONObject(i);
                            listSearched.add(new Course(_jobj.getString("title"), _jobj.getString("description")));
                            listLoaded.add(new Course(_jobj.getString("title"), _jobj.getString("description")));
                        }

                        Toast.makeText(getApplicationContext(), "Courses loaded", Toast.LENGTH_LONG).show();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                "Unable to load courses", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Course Load Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };
        queue.add(strReq);
    }

    public void addReminder(String title){
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", false);
        intent.putExtra("rule", "FREQ=DAILY");
        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
        intent.putExtra("title", getString( R.string.prefix_reminder_title) +" " + title);
        startActivity(intent);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
