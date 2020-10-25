package com.breathetaichi.classtracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DBInterface db;

    private static ArrayList<Term> allTerms = new ArrayList<>();
    private static TextView getTermStart;
    private static TextView getTermEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getTermStart = findViewById(R.id.term_start);
        getTermEnd = findViewById(R.id.term_end);
        Button addTermButton = findViewById(R.id.add_term_button);

        addTermButton.setSelected(true);

        db = new DBInterface(MainActivity.this);
        db.createTables();
        db.getWritableDatabase();

        allTerms = db.getTerms();
        String[] titles = new String[allTerms.size()];
        long[] termIDs = new long[allTerms.size()];

        int i = 0;
        for(Term term : allTerms) {
            titles[i] = term.getTitle();
            termIDs[i] = term.getId();

            i++;
        }

        final android.widget.ListAdapter listAdapter = new ListAdapter(this, titles);
        final ListView lv = findViewById(R.id.term_list_view);

        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String termPicked = "You selected " +
                        (adapterView.getItemAtPosition(position)) + " id: " + id;
                long termID = allTerms.get((int)id).getId();

                Toast.makeText(MainActivity.this, termPicked, Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(getBaseContext(), TermActivity.class);

                Current.term = termID;

                db.close();
                startActivityForResult(intent, 1);
            }
        });


        addTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText getTitle = findViewById(R.id.term_title);

                String title = getTitle.getText().toString().trim();
                String termStart = getTermStart.getText().toString().trim();
                String termEnd = getTermEnd.getText().toString().trim();

                if(title.isEmpty()
                        || termStart.isEmpty()
                        || termEnd.isEmpty()) {

                    Toast.makeText(MainActivity.this, "Error, record not inserted.",
                            Toast.LENGTH_SHORT).show();
                } else {

                    long result = db.insertTerm(title, termStart, termEnd);

                    if (result == -1) {
                        Toast.makeText(MainActivity.this, "Error, term not inserted.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Term added.",
                                Toast.LENGTH_SHORT).show();
                        allTerms = db.getTerms();
                        finish();
                        db.close();
                        startActivity(getIntent());
                    }
                }
            }
        });

    }

    public static class EndTermDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month++;
            String date = year + "/" + month  + "/" + day;
            getTermEnd.setText(date);
        }
    }

    public void termEndDatePickerDialog(View v) {
        DialogFragment newFragment = new EndTermDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class StartTermDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month++;
            String date = year + "/" + month  + "/" + day;
            getTermStart.setText(date);
        }
    }

    public void termStartDatePickerDialog(View v) {
        DialogFragment newFragment = new StartTermDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onPause() {
        super.onPause();
        allTerms = db.getTerms();
        this.recreate();
    }
}