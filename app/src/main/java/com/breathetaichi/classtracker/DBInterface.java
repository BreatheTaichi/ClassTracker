package com.breathetaichi.classtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBInterface extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ClassTracker.db";
    public static final int DATABASE_VERSION = 1;

    public DBInterface(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertTerm(String title, String startDate, String endDate) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("title", title);
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        long r = db.insert("term_table", null, values);
        db.close();
        return r;

    }

    public ArrayList<Term> getTerms() {

        ArrayList<Term> allTerms = new ArrayList<>();
        long id;
        String title;
        String startDate;
        String endDate;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM term_table", null);

        while(cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex("id"));
            title = cursor.getString(cursor.getColumnIndex("title"));
            startDate = cursor.getString(cursor.getColumnIndex("startDate"));
            endDate = cursor.getString(cursor.getColumnIndex("endDate"));

            allTerms.add(new Term(id, title, startDate, endDate));
        }

        db.close();
        return allTerms;
    }

    public long deleteTerm(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String idStr = Long.toString(id);
        String[] whereArgs = {idStr};

        long r = db.delete("term_table", "id = ?", whereArgs);
        db.close();
        return r;
    }

    public long insertCourse(long termID, long instructorID, String title, String startDate,
                             String endDate, String status, String note) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("termID", termID);
        values.put("instructorID", instructorID);
        values.put("title", title);
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        values.put("status", status);
        values.put("note", note);

        long r = db.insert("course_table", null, values);
        db.close();

        return r;
    }

    public ArrayList<Course> getCourses() {

        ArrayList<Course> allCourses = new ArrayList<>();

        long id;
        long termID;
        long instructorID;
        String title;
        String startDate;
        String endDate;
        String status;
        String note;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM course_table", null);

        while(cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex("id"));
            termID = cursor.getInt(cursor.getColumnIndex("termID"));
            instructorID = cursor.getInt(cursor.getColumnIndex("instructorID"));
            title = cursor.getString(cursor.getColumnIndex("title"));
            startDate = cursor.getString(cursor.getColumnIndex("startDate"));
            endDate = cursor.getString(cursor.getColumnIndex("endDate"));
            status = cursor.getString(cursor.getColumnIndex("status"));
            note = cursor.getString(cursor.getColumnIndex("note"));

            allCourses.add(new Course(id, termID, instructorID, title, startDate,
                    endDate, status, note));
        }

        db.close();
        return allCourses;
    }

    public long updateCourse(long id, long termID, long instructorID, String title, String startDate,
                             String endDate, String status, String note) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("termID", termID);
        cv.put("instructorID", instructorID);
        cv.put("title", title);
        cv.put("startDate", startDate);
        cv.put("endDate", endDate);
        cv.put("status", status);
        cv.put("note", note);

        String[] whereArgs = {Long.toString(id)};

        long r = db.update("course_table", cv, "id = ?", whereArgs);
        db.close();
        return r;
    }

    public long deleteCourse(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String idStr = Long.toString(id);
        String[] whereArgs = {idStr};

        long r = db.delete("course_table", "id = ?", whereArgs);
        db.close();

        return r;
    }

    public long insertInstructor(String name, String phone, String email) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("phone", phone);
        values.put("email", email);

        long r = db.insert("instructor_table", null, values);
        db.close();
        return r;
    }

    public long updateInstructor(long id, String name, String phone, String email) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("phone", phone);
        values.put("email", email);

        String[] whereArgs = {Long.toString(id)};

        long r = db.update("instructor_table", values, "id = ?", whereArgs);
        db.close();
        return r;
    }

    public ArrayList<Instructor> getInstructors() {

        ArrayList<Instructor> allInstructors = new ArrayList<>();
        long id;
        String name;
        String phone;
        String email;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM instructor_table", null);

        while(cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex("id"));
            name = cursor.getString(cursor.getColumnIndex("name"));
            phone = cursor.getString(cursor.getColumnIndex("phone"));
            email = cursor.getString(cursor.getColumnIndex("email"));

            allInstructors.add(new Instructor(id, name, phone, email));
        }

        db.close();
        return allInstructors;
    }

    public long deleteInstructor(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String idStr = Long.toString(id);
        String[] whereArgs = {idStr};

        long r = db.delete("instructor_table", "id = ?", whereArgs);
        db.close();
        return r;
    }

    public long insertAssessment(long courseID, String title, String type, String dueDate, String dueTime) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("title", title);
        values.put("courseID", courseID);
        values.put("type", type);
        values.put("dueDate", dueDate);
        values.put("dueTime", dueTime);

        long r = db.insert("assessment_table", null, values);
        db.close();
        return r;
    }

    public ArrayList<Assessment> getAssessments() {

        ArrayList<Assessment> allAssessments = new ArrayList<>();
        long id;
        long courseID;
        String title;
        String type;
        String dueDate;
        String dueTime;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM assessment_table", null);

        while(cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex("id"));
            courseID = cursor.getInt(cursor.getColumnIndex("courseID"));
            title = cursor.getString(cursor.getColumnIndex("title"));
            type = cursor.getString(cursor.getColumnIndex("type"));
            dueDate = cursor.getString(cursor.getColumnIndex("dueDate"));
            dueTime = cursor.getString(cursor.getColumnIndex("dueTime"));

            allAssessments.add(new Assessment(id, courseID, title, type, dueDate, dueTime));
        }

        db.close();
        return allAssessments;
    }

    public long deleteAssessment(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String idStr = Long.toString(id);
        String[] whereArgs = {idStr};
System.out.println("Attempting to delete assessment with id: " + id);

        long r = db.delete("assessment_table", "id = ?", whereArgs);
        db.close();
        return r;
    }

    public long updateAssessment(long id, long courseID, String name,
                                 String type, String dueDate, String dueTime) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("courseID", courseID);
        values.put("title", name);
        values.put("type", type);
        values.put("dueDate", dueDate);
        values.put("dueTime", dueTime);

        String[] whereArgs = {Long.toString(id)};

        long r = db.update("assessment_table", values, "id = ?", whereArgs);
        db.close();
        return r;
    }



    public void deleteTables() {
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS instructor_table");
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS course_table");
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS term_table");
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS assessment_table");
    }

    public void createTables() {
        this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS term_table" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, startDate DATE, endDate DATE)");

        this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS course_table" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, termID INT, instructorID INT, " +
                "title TEXT, startDate DATE, endDate DATE, status TEXT, note TEXT)");

        this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS instructor_table" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, " +
                "phone TEXT, email TEXT)");

        this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS assessment_table" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, courseID INT, title TEXT, type TEXT, " +
                "dueDate DATE, dueTime TIME)");
    }
}
