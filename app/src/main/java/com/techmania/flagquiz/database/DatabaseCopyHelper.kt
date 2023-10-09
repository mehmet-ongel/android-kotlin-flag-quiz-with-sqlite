package com.techmania.flagquizwithsqlitedemo

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class DatabaseCopyHelper(context: Context) : SQLiteOpenHelper(context,DB_NAME,null,1) {

    var DB_PATH : String? = null
    var myContext : Context? = null
    lateinit var myDataBase : SQLiteDatabase

    companion object{
        var DB_NAME : String = "countries.db"
    }

    init {
        DB_PATH = "/data/data/" + context.packageName + "/" + "databases/"
        myContext = context
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private fun checkDataBase(): Boolean {
        var checkDB: SQLiteDatabase? = null
        try {
            val myPath: String = DB_PATH + DB_NAME
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)
        } catch (e: SQLiteException) {

            //database doesn't exist yet.
        }
        checkDB?.close()
        return checkDB != null
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    @Throws(IOException::class)
    private fun copyDataBase() {

        //Open your local db as the input stream
        val myInput = myContext?.assets?.open(DB_NAME)

        // Path to the just created empty db
        val outFileName: String = DB_PATH + DB_NAME

        //Open the empty db as the output stream
        val myOutput: OutputStream = FileOutputStream(outFileName)

        //transfer bytes from the inputfile to the outputfile
        val buffer = ByteArray(1024)
        var length: Int
        if (myInput != null) {
            while (myInput.read(buffer).also { length = it } > 0) {
                myOutput.write(buffer, 0, length)
            }
			
			//Toast.makeText(myContext?.applicationContext,"Database is copied",Toast.LENGTH_SHORT).show()
        }

        //Close the streams
        myOutput.flush()
        myOutput.close()
        myInput?.close()
    }
	
	/**
     * Creates an empty database on the system and rewrites it with your own database.
     */
    @Throws(IOException::class)
    fun createDataBase() {
        val dbExist = checkDataBase()
        if (dbExist) {
            //do nothing - database already exist
            //Toast.makeText(myContext?.applicationContext,"Database is already exist",Toast.LENGTH_SHORT).show()
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.readableDatabase
            try {
                copyDataBase()
            } catch (e: IOException) {
                throw Error("Error copying database")
            }
        }
    }

    @Throws(SQLException::class)
    fun openDataBase() {

        //Open the database
        val myPath: String = DB_PATH + DB_NAME
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)
        //Toast.makeText(myContext?.applicationContext,"Database is opened",Toast.LENGTH_SHORT).show()
    }

    @Synchronized
    override fun close() {
        myDataBase.close()
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE IF NOT EXISTS flags (flag_id INTEGER, country_name TEXT, flag_name TEXT)")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        db?.execSQL("DROP TABLE IF EXISTS flags")
        onCreate(db)
    }
}