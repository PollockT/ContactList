package com.example.contactlist.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.contactlist.models.Contact;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "contacts.db";
    private static final String TABLE_NAME = "contacts_table";
    public static final String COL_0 = "ID";
    public static final String COL_1 = "NAME";
    public static final String COL_2 = "PHONE_NUMBER";
    public static final String COL_3 = "DEVICE";
    public static final String COL_4 = "EMAIL";
    public static final String COL_5 = "PROFILE_PHOTO";


    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use for locating paths to the the database
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null,1);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlite =  "CREATE TABLE " + TABLE_NAME + " ( " +
                COL_0 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COL_1 + " TEXT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT ) ";
        db.execSQL(sqlite);

    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate(db);
    }

    /**
     * Insert a new contact into the database
     * @param contact
     * @return
     */
    public boolean addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, contact.getName());
        contentValues.put(COL_2, contact.getPhoneNumber());
        contentValues.put(COL_3, contact.getDevice());
        contentValues.put(COL_4, contact.getEmail());
        contentValues.put(COL_5, contact.getProfileImage());

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Retrieve all contacts from database
     * @return
     */
    public Cursor getAllContacts(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    /**
     * Update a Contact using id col
     * @param contact
     * @param id
     * @return
     */
    public boolean updateContact(Contact contact, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, contact.getName());
        contentValues.put(COL_2, contact.getPhoneNumber());
        contentValues.put(COL_3, contact.getDevice());
        contentValues.put(COL_4, contact.getEmail());
        contentValues.put(COL_5, contact.getProfileImage());


        int update = db.update(TABLE_NAME,contentValues,COL_0+" = ? ",new String[] {String.valueOf(id)});

        if(update != 1){
            return false;
        }else{
            return true;
        }
    }

    /**
     * grab the contact id
     * @param contact
     * @return
     */
    public Cursor getContactID(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = '" + contact.getName() + "'"+
                " AND " + COL_2 + " = '" + contact.getPhoneNumber()+"'";

        return db.rawQuery(sql, null);
    }

    public Integer deleteContact(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {String.valueOf(id)});
    }


}

