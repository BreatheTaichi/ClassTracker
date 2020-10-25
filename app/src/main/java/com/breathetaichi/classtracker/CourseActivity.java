package com.breathetaichi.classtracker;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    private static DBInterface db;
    private static ArrayList<Course> allCourses = new ArrayList<>();
    private static ArrayList<Assessment> allAssessments = new ArrayList<>();
    private static ArrayList<Instructor> allInstructors = new ArrayList<>();
    private static TextView startDateText;
    private static TextView endDateText;
    private static TextView assessmentDueTime;
    private static TextView assessmentDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        startDateText = findViewById(R.id.edit_date_label);
        endDateText = findViewById(R.id.end_edit_date_label);
        assessmentDueDate = findViewById(R.id.assessment_edit_date_label);
        assessmentDueTime = findViewById(R.id.assessment_edit_time_label);

        db = new DBInterface(CourseActivity.this);
        db.getWritableDatabase();

        allCourses = db.getCourses();
        allAssessments = db.getAssessments();
        allInstructors = db.getInstructors();

        String courseTitle = "";
        String courseStartDateStr = "";
        String courseEndDateStr = "";
        String courseStatusStr = "";
        String courseNoteStr = "";
        for(Course course : allCourses) {
            if(course.getId() == Current.course) {
                courseTitle = course.getTitle();
                Current.term = course.getTermID();
                Current.instructor = course.getInstructorID();
                courseStartDateStr = course.getStartDate();
                courseEndDateStr = course.getEndDate();
                courseStatusStr = course.getStatus();
                courseNoteStr = course.getNote();
            }
        }

        EditText courseName = findViewById(R.id.course_title);
        courseName.setText(courseTitle);
        TextView courseStartDate = findViewById(R.id.edit_date_label);
        courseStartDate.setText(courseStartDateStr);
        TextView courseEndDate = findViewById(R.id.end_edit_date_label);
        courseEndDate.setText(courseEndDateStr);
        EditText courseStatus = findViewById(R.id.course_status);
        courseStatus.setText(courseStatusStr);
        EditText termEndDate = findViewById(R.id.course_note);
        termEndDate.setText(courseNoteStr);

        String instructorNameStr = "";
        String instructorPhoneStr = "";
        String instructorEmailStr = "";

        for (Instructor instructor : allInstructors) {
            if(instructor.getId() == Current.instructor) {
                instructorNameStr = instructor.getName();
                instructorPhoneStr = instructor.getPhone();
                instructorEmailStr = instructor.getEmail();
            }
        }

        EditText instructorName = findViewById(R.id.instructor_name);
        instructorName.setText(instructorNameStr);
        EditText instructorPhone = findViewById(R.id.instructor_phone);
        instructorPhone.setText(instructorPhoneStr);
        EditText instructorEmail = findViewById(R.id.instructor_email);
        instructorEmail.setText(instructorEmailStr);

        List<Assessment> assessmentsInCourse = new ArrayList<>();
        for (Assessment assessment : allAssessments) {
            if(assessment.getCourseID() == Current.course) {
                assessmentsInCourse.add(assessment);
            }
        }

        String[] titles = new String[assessmentsInCourse.size()];
        final long[] assessmentIDs = new long[assessmentsInCourse.size()];

        int i = 0;
        for(Assessment assessment : assessmentsInCourse) {
            titles[i] = assessment.getTitle();
            assessmentIDs[i] = assessment.getId();
            i++;
        }

        final android.widget.ListAdapter adapter = new ListAdapter(this, titles);
        final ListView lv = findViewById(R.id.assessment_list_view);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

//                long currentAssessment = assessmentIDs[(int)id];
                Current.assessment = assessmentIDs[(int)id];

                Assessment assessment = getAssessment(Current.assessment);

                String assessmentNameStr = assessment.getTitle();
                String assessmentTypeStr = assessment.getType();
                String assessmentDueStr = assessment.getDueDate();
                String assessmentTimeStr = assessment.getDueTime();

                EditText assessmentName = findViewById(R.id.assessment_title);
                assessmentName.setText(assessmentNameStr);
                EditText assessmentType = findViewById(R.id.assessment_type);
                assessmentType.setText(assessmentTypeStr);
                TextView assessmentStartDate = findViewById(R.id.assessment_edit_date_label);
                assessmentStartDate.setText(assessmentDueStr);
                TextView assessmentEndDate = findViewById(R.id.assessment_edit_time_label);
                assessmentEndDate.setText(assessmentTimeStr);
            }
        });
    }

    public void deleteCourseButton(View v) {

        if(Current.course <= 0) {

            Intent intent = new Intent(getBaseContext(), TermActivity.class);
            Current.course = -1;
            Current.instructor = -1;
            Current.assessment = -1;
            db.close();
            startActivity(intent);

        } else {
            for (Assessment assessment : allAssessments) {
                if(assessment.getCourseID() == Current.course) {
                    db.deleteAssessment(assessment.getCourseID());
                }
            }

            for (Course course : allCourses) {
                if(course.getId() == Current.course) {
                    db.deleteInstructor(course.getInstructorID());
                }
            }

            db.deleteCourse(Current.course);
            Current.course = -1;
            Current.instructor = -1;
            Current.assessment = -1;

            Intent intent = new Intent(getBaseContext(), TermActivity.class);
            db.close();
            startActivity(intent);
        }
    }

    public void saveCourseButton(View v) {

        String courseTitle;
        String courseStartDateStr;
        String courseEndDateStr;
        String courseStatusStr;
        String courseNoteStr;

        EditText courseName = findViewById(R.id.course_title);
        courseTitle = courseName.getText().toString().trim();
        TextView courseStartDate = findViewById(R.id.edit_date_label);
        courseStartDateStr = courseStartDate.getText().toString().trim();
        TextView courseEndDate = findViewById(R.id.end_edit_date_label);
        courseEndDateStr = courseEndDate.getText().toString().trim();
        EditText courseStatus = findViewById(R.id.course_status);
        courseStatusStr = courseStatus.getText().toString().trim();
        EditText courseNote = findViewById(R.id.course_note);
        courseNoteStr = courseNote.getText().toString().trim();

        String instructorNameStr;
        String instructorPhoneStr;
        String instructorEmailStr;

        EditText instructorName = findViewById(R.id.instructor_name);
        instructorNameStr = instructorName.getText().toString().trim();
        EditText instructorPhone = findViewById(R.id.instructor_phone);
        instructorPhoneStr = instructorPhone.getText().toString().trim();
        EditText instructorEmail = findViewById(R.id.instructor_email);
        instructorEmailStr = instructorEmail.getText().toString().trim();

        if(courseTitle.isEmpty()
                || courseStartDateStr.isEmpty()
                || courseEndDateStr.isEmpty()
                || courseStatusStr.isEmpty()) {

            Toast.makeText(CourseActivity.this, "Please fill course fields.",
                    Toast.LENGTH_SHORT).show();

        } else if(instructorNameStr.isEmpty()
                || instructorPhoneStr.isEmpty()
                || instructorEmailStr.isEmpty()) {

            Toast.makeText(CourseActivity.this, "Please fill instructor fields.",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Check if new course Current.course is -1 or if not update with id in Current.course
            if(Current.course <= 0) {

                // Insert new instructor and get their ID back
                Current.instructor = (int) db.insertInstructor(instructorNameStr,
                        instructorPhoneStr, instructorEmailStr);

                Current.course = (int) db.insertCourse(Current.term,
                        Current.instructor, courseTitle, courseStartDateStr,
                        courseEndDateStr, courseStatusStr, courseNoteStr);

                allCourses = db.getCourses();

                Toast.makeText(CourseActivity.this, "New course saved.",
                        Toast.LENGTH_SHORT).show();

            } else {
                // Insert new instructor and get their ID back
                Current.instructor = (int) db.insertInstructor(instructorNameStr,
                        instructorPhoneStr, instructorEmailStr);

                db.updateCourse(Current.course, Current.term, Current.instructor, courseTitle,
                        courseStartDateStr, courseEndDateStr, courseStatusStr, courseNoteStr);

                allCourses = db.getCourses();

                Toast.makeText(CourseActivity.this, "Course updated.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void saveAssessmentButton(View v) {

        String assessmentNameStr;
        String assessmentTypeStr;
        String assessmentDueTimeStr;
        String assessmentDueDateStr;

        EditText assessmentName = findViewById(R.id.assessment_title);
        assessmentNameStr = assessmentName.getText().toString().trim();
        EditText assessmentType = findViewById(R.id.assessment_type);
        assessmentTypeStr = assessmentType.getText().toString().trim();
        assessmentDueTimeStr = assessmentDueTime.getText().toString().trim();
        assessmentDueDateStr = assessmentDueDate.getText().toString().trim();

        if(assessmentDueTimeStr.isEmpty()
                || assessmentDueDateStr.isEmpty()
                || assessmentNameStr.isEmpty()
                || assessmentTypeStr.isEmpty()) {
            Toast.makeText(CourseActivity.this, "Please fill all fields.",
                    Toast.LENGTH_SHORT).show();
        } else {
            if(Current.course <= 0) {

                Toast.makeText(CourseActivity.this, "Not added, create course first.",
                        Toast.LENGTH_SHORT).show();

            } else {
                if (Current.assessment <= 0) {

                    Current.assessment = db.insertAssessment(Current.course, assessmentNameStr,
                            assessmentTypeStr, assessmentDueDateStr, assessmentDueTimeStr);

                    allAssessments = db.getAssessments();

                    Toast.makeText(CourseActivity.this, "Assessment added.",
                            Toast.LENGTH_SHORT).show();
                    this.recreate();
                } else {

                    Current.assessment = db.updateAssessment(Current.assessment,
                            Current.course, assessmentNameStr,
                            assessmentTypeStr, assessmentDueDateStr, assessmentDueTimeStr);

                    allAssessments = db.getAssessments();

                    Toast.makeText(CourseActivity.this, "Assessment updated.",
                            Toast.LENGTH_SHORT).show();

                    this.recreate();
                }
            }
        }
    }

    public void newAssessmentButton(View v) {

        clearAssessmentFields();

        Current.assessment = -1;
    }

    public void deleteAssessmentButton(View v) {
        if(Current.assessment <= 0) {
            clearAssessmentFields();

            Toast.makeText(CourseActivity.this, "No assessment chosen.",
                    Toast.LENGTH_SHORT).show();
        } else {
            long result = db.deleteAssessment(Current.assessment);

            if(result <= 0) {
                Toast.makeText(CourseActivity.this, "Assessment NOT deleted.",
                        Toast.LENGTH_SHORT).show();

            } else {
                db.deleteAssessment(Current.assessment);
                clearAssessmentFields();
                Toast.makeText(CourseActivity.this, "Assessment deleted.  result = " + result,
                        Toast.LENGTH_SHORT).show();

                allAssessments = db.getAssessments();

                this.recreate();
            }
        }
    }

    private void clearAssessmentFields() {

        EditText assessmentName = findViewById(R.id.assessment_title);
        assessmentName.setText("");
        EditText assessmentType = findViewById(R.id.assessment_type);
        assessmentType.setText("");
        assessmentDueDate.setText("");
        assessmentDueTime.setText("");
    }

    public void backButton(View v) {
        Intent intent = new Intent(getBaseContext(), TermActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        allAssessments = db.getAssessments();
        allCourses = db.getCourses();
        allInstructors = db.getInstructors();

    }

    public Assessment getAssessment(long id) {
        Assessment assessment = new Assessment();

        for(Assessment asmt : allAssessments) {
            if(asmt.getId() == id) {
                assessment = asmt;
            }
        }
        return assessment;
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String time = hourOfDay + ":" + minute;
            assessmentDueTime.setText(time);
        }
    }

    public void ShowTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class AssessmentDatePickerFragment extends DialogFragment
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
            assessmentDueDate.setText(date);
        }
    }

    public void AssessmentDatePickerDialog(View v) {
        DialogFragment newFragment = new AssessmentDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class EndCourseDatePickerFragment extends DialogFragment
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
            endDateText.setText(date);
        }
    }

    public void showCourseEndDatePickerDialog(View v) {
        DialogFragment newFragment = new EndCourseDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class StartCourseDatePickerFragment extends DialogFragment
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
            startDateText.setText(date);
        }
    }

    public void showCourseStartDatePickerDialog(View v) {
        DialogFragment newFragment = new StartCourseDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void sendEmail(View v) {
        if(Current.course <= 0) {

            Toast.makeText(CourseActivity.this,
                    "Please save course first.", Toast.LENGTH_SHORT).show();
            return;
        }

        Course theCourse = new Course();

        for(Course course : allCourses) {
            if(Current.course == course.getId()) {
                theCourse = course;
            }
        }

        if(theCourse.getNote().isEmpty()) {

            Toast.makeText(CourseActivity.this,
                    "No note to send.", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailSubject = "Note from " + theCourse.getTitle();

        EditText emailAddress = findViewById(R.id.email_address);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress.getText().toString()});
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        intent.putExtra(Intent.EXTRA_TEXT, theCourse.getNote());

        try {
            startActivity(Intent.createChooser(intent, "Send Mail"));
            Toast.makeText(CourseActivity.this,
                    "Email sent.", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(CourseActivity.this,
                    "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setStartAlert(View v) {

        if (Current.course <= 0) {
            Toast.makeText(CourseActivity.this, "Please save course first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Course theCourse = new Course();

        for(Course course : allCourses) {
            if(Current.course == course.getId()) {
                theCourse = course;
            }
        }

        long milliDate = new Date(theCourse.getStartDate()).getTime();
        milliDate = milliDate - (24 * 60 * 60 * 1000);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String cal = formatter.format(new Date(milliDate));
        Toast.makeText(CourseActivity.this, "Alert set for " + cal,
                Toast.LENGTH_SHORT).show();

        MyReceiver.notificationID = 1;
        MyReceiver.channelID = "courseStart";
        MyReceiver.name = "Course Start";
        MyReceiver.content = "Course is starting on " + theCourse.getStartDate();
        MyReceiver.title = theCourse.getTitle() + " Alarm";

        Intent intent = new Intent(CourseActivity.this, MyReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(CourseActivity.this,
                0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, milliDate, sender);
    }

    public void setEndAlert(View v) {

        if (Current.course <= 0) {
            Toast.makeText(CourseActivity.this, "Please save course first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Course theCourse = new Course();

        for(Course course : allCourses) {
            if(Current.course == course.getId()) {
                theCourse = course;
            }
        }

        long milliDate = new Date(theCourse.getEndDate()).getTime();
        milliDate = milliDate - (24 * 60 * 60 * 1000);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String cal = formatter.format(new Date(milliDate));
        Toast.makeText(CourseActivity.this, "Alert set for " + cal,
                Toast.LENGTH_SHORT).show();

        MyReceiver.notificationID = 2;
        MyReceiver.channelID = "courseEnd";
        MyReceiver.name = "Course End";
        MyReceiver.content = "Course is ending on " + theCourse.getEndDate();
        MyReceiver.title = theCourse.getTitle() + " Alarm";

        Intent intent = new Intent(CourseActivity.this, MyReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(CourseActivity.this,
                0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, milliDate, sender);

    }

    public void setAssessmentAlert(View v) {

        if (Current.assessment <= 0) {
            Toast.makeText(CourseActivity.this, "Please choose an assessment",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Assessment theAsmt = new Assessment();
        for (Assessment assessment : allAssessments) {
            if(Current.assessment == assessment.getId()) {
                theAsmt = assessment;
            }
        }
        String dateTime = theAsmt.getDueDate() + " " + theAsmt.getDueTime();

        long milliDate = new Date(dateTime).getTime();
        milliDate = milliDate - (15 * 60 * 1000);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String cal = formatter.format(new Date(milliDate));
        Toast.makeText(CourseActivity.this, "Alert set for " + cal,
                Toast.LENGTH_SHORT).show();

        MyReceiver.notificationID = 2;
        MyReceiver.channelID = "assessmentEnd";
        MyReceiver.name = "Assessment End";
        MyReceiver.content = "Assessment begins at " + theAsmt.getDueTime();
        MyReceiver.title = "Assessment " + theAsmt.getTitle() + " Alarm";

        Intent intent = new Intent(CourseActivity.this, MyReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(CourseActivity.this,
                0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, milliDate, sender);
    }
}
