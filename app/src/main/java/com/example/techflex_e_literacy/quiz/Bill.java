package com.example.techflex_e_literacy.quiz;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.mainActivity.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Bill extends AppCompatActivity {
    private static final String LIST_VIEW_STATE = "list_view";
    public Button add, remove, save;
    TextView num_of_course;
    int coures_picked = 0;
    int price;
    Spinner semester_select;
    private EditText course_code;
    private  ListView dataListView;
    private ArrayList<String> list;
    ArrayAdapter<String> arrayAdapter;
    FirebaseAuth auth;
    DatabaseReference databaseCourseReg;
    DatabaseReference databaseSub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_activity);

        add = findViewById(R.id.add_course);
        num_of_course = findViewById(R.id.num_of_course);
        remove = findViewById(R.id.remove);
        dataListView = findViewById(R.id.listview);
        course_code = findViewById(R.id.course_code);
        save = findViewById(R.id.save_course_button);
        semester_select = findViewById(R.id.semester_select);
        databaseCourseReg = FirebaseDatabase.getInstance().getReference("courseRegDb");
        databaseSub = FirebaseDatabase.getInstance().getReference("Total_Subscribers");
        list = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String course = course_code.getText().toString();
                if (course.isEmpty()){
                    Toast.makeText(Bill.this,"Please type a course code",Toast.LENGTH_SHORT).show();
                }else if(list.contains(course)){
                    Toast.makeText(Bill.this,"course already  exits",Toast.LENGTH_SHORT).show();

                }else if(course.trim().length()!= 6 && course.trim().length()> 6){
                    Toast.makeText(Bill.this,"Invalid Course Code",Toast.LENGTH_SHORT).show();

                }else {
                    list.add(course);
                    coures_picked++;
                    price = price+500;
                    num_of_course.setText("Courses Picked: "+coures_picked+"@ "+"#"+price);
                    dataListView.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.isEmpty()){
                    Toast.makeText(Bill.this,"Sorry,No Item to  Remove",Toast.LENGTH_SHORT).show();
                }else {
                    list.remove(0);
                    coures_picked--;
                    price = price -500;
                    num_of_course.setText("Courses Picked: "+coures_picked+"@ "+"#"+price);
                    arrayAdapter.notifyDataSetChanged();
                }

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (list.isEmpty()){
                   showPopUp2();
               }else {
                   if (isConnected(Bill.this)){
                       showPopUp();
                       //addCourse();
                       //Toast.makeText(Bill.this,"CourseAdded Successfully",Toast.LENGTH_SHORT).show();
                   }else {
                       Toast.makeText(Bill.this,"Please Turn  on Data / Internet To Subscribe",Toast.LENGTH_SHORT).show();
                   }

               }
            }
        });
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(LIST_VIEW_STATE,coures_picked);
        outState.putSerializable("d",list);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        savedInstanceState.getInt(LIST_VIEW_STATE);
        super.onRestoreInstanceState(savedInstanceState);
    }

    void showPopUp() {
        new AlertDialog.Builder(Bill.this)
        .setTitle("Attention!")
        .setMessage("Are you  Sure you want to Proceed?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    addCourse();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Bill.this, SubscriptionCodeActivity.class);
                startActivity(intent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        })
                .setCancelable(false).create()
                .show();
    }
    void showPopUp2() {
        new AlertDialog.Builder(Bill.this)
                .setTitle("!Attention")
                .setMessage("to subscribe please add a course")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();

    }
    public void addCourse() throws ParseException {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //  users subscription start date using system date

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");// HH:mm:ss");
        String reg_date = df.format(c.getTime());
        Log.d("TAG2", "Start Date: " + reg_date);

        c.add(Calendar.DATE, 90);  // number of days to add
        String end_date = df.format(c.getTime());
        Log.d("TAG2", "Start Date: " + end_date);

        /*Date date  = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        String strDate = formatter.format(date);
        Log.d("TAG2", "Start Date: " + strDate);

        //getting users subscription end-date
        Date enddate  = new Date();
        SimpleDateFormat formatter1 = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        String endDate = formatter.format(enddate);
        Calendar calendar1 = Calendar.getInstance();

        try {
            calendar1.setTime(formatter1.parse(endDate));
            calendar1.add(Calendar.DATE, 2); // number of days for subscription = 2days
            endDate = formatter1.format(calendar1.getTime());
            Log.d("TAG2", "End Date: " +endDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }*/


        String semester_count = semester_select.getSelectedItem().toString().trim();
        String[] list = new String[dataListView.getAdapter().getCount()];
        String email = null;
        int count = 0;
        if (user != null) {
            email = user.getEmail();
        }
        for(int i = 0; i<list.length; i++){
            list[i] = dataListView.getAdapter().getItem(i).toString();
            count++;
           //String[] course_picked = dataListView.getAdapter().getItem(i).toString();
        }
        String id = databaseCourseReg.push().getKey();
        CourseReg coureseReg = new CourseReg(id,Arrays.toString(list),semester_count,email,reg_date,end_date,count);
        databaseCourseReg.child(id).setValue(coureseReg);
        Toast.makeText(this,"Course Added Successfully",Toast.LENGTH_SHORT).show();

    }
    public Boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobile != null && mobile.isConnected()) || (wifi != null && wifi.isConnected()))
                return true;
            else return false;
        } else
            return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Bill.this, UserActivity.class));
    }
}
