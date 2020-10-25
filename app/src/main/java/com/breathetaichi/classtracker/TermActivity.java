package com.breathetaichi.classtracker;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TermActivity extends AppCompatActivity {

    private static DBInterface db;
    private static ArrayList<Course> allCourses = new ArrayList<>();
    private static ArrayList<Term> allTerms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);

        Button deleteTermButton = findViewById(R.id.delete_term_button);

        deleteTermButton.setSelected(true);

        db = new DBInterface(TermActivity.this);
        db.getWritableDatabase();

        allCourses = db.getCourses();
        allTerms = db.getTerms();

//        Bundle extras = getIntent().getExtras();
//        long termCalled = extras.getLong("termID");


        String termTitle = "";
        String termStartDateStr = "";
        String termEndDateStr = "";
        for (Term term : allTerms) {
            if (term.getId() == Current.term) {
                termTitle = term.getTitle();
                termStartDateStr = term.getStartDate();
                termEndDateStr = term.getEndDate();
            }
        }

        TextView termName = findViewById(R.id.term_called);
        termName.setText(termTitle);
        TextView termStartDate = findViewById(R.id.term_start_date);
        termStartDate.setText(termStartDateStr);
        TextView termEndDate = findViewById(R.id.term_end_date);
        termEndDate.setText(termEndDateStr);

        List<Course> coursesInTerm = new ArrayList<>();
        for (Course course : allCourses) {
            if(course.getTermID() == Current.term) {
                coursesInTerm.add(course);
            }
        }

        String[] titles = new String[coursesInTerm.size()];
        final long[] courseIDs = new long[coursesInTerm.size()];

        Current.numberOfCourses = 0;
        int i = 0;
        for(Course course : coursesInTerm) {
            titles[i] = course.getTitle();
            courseIDs[i] = course.getId();

            i++;
            Current.numberOfCourses++;
        }

        final android.widget.ListAdapter listAdapter = new ListAdapter(this, titles);
        final ListView lv = findViewById(R.id.course_list_view);

        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getBaseContext(), CourseActivity.class);
                Current.course = courseIDs[(int)id];
                for(Course course : allCourses) {
                    if(course.getId() == Current.course) {
                        Current.instructor = course.getInstructorID();
                    }
                }
                db.close();
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        allCourses = db.getCourses();
        allTerms = db.getTerms();

        this.recreate();
    }


    public void deleteTermButton(View v) {
        Current.numberOfCourses = 0;

        for(Course course : allCourses) {
            if(course.getTermID() == Current.term) {
                Current.numberOfCourses++;
            }
        }

        if(Current.numberOfCourses == 0) {
            long result = db.deleteTerm(Current.term);

            if(result == -1) {

                Toast.makeText(TermActivity.this, "Error, term not deleted.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TermActivity.this, "Term deleted.",
                        Toast.LENGTH_SHORT).show();
                allTerms = db.getTerms();
//                finish();


                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                db.close();
                startActivity(intent);

            }
        } else {

            Toast.makeText(TermActivity.this, "Remove all courses first.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void addCourseButton(View v) {
        Intent intent = new Intent(getBaseContext(), CourseActivity.class);
        Current.course = -1;
        Current.assessment = -1;
        Current.instructor = -1;
        db.close();
        startActivityForResult(intent, 2);

    }

    public Term getTerm(long id) {
        Term theTerm = new Term();
        for(Term term: allTerms) {
            if(term.getId() == id) {
                theTerm = term;
            }
        }
        return theTerm;
    }

    public void toMainFromTerm(View v) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }
}
