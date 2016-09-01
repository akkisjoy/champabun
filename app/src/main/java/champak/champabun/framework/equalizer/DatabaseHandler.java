
package champak.champabun.framework.equalizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context) {
        super(context, "equalizer_test5", null, 1);
    }

    public void onCreate(SQLiteDatabase sqlitedatabase) {
        sqlitedatabase.execSQL("CREATE TABLE oldpathdemo(id INTEGER PRIMARY KEY,Y1 INTEGER,Y2 INTEGER,Y3 INTEGER,Y4 INTEGER,Y5 INTEGER)");
        sqlitedatabase.execSQL("CREATE TABLE button_values(ids INTEGER PRIMARY KEY,bass INTEGER,volume INTEGER,virtualizer INTEGER,B1 INTEGER,B2 INTEGER,B3 INTEGER)");
        sqlitedatabase.execSQL("CREATE TABLE preset_values_demo(PID integer primary key, presetname text not null,PY1 integer,PY2 integer,PY3 integer,PY4 integer,PY5 integer)");
    }

    public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j) {
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS oldpathdemo");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS button_values");
        sqlitedatabase.execSQL("DROP TABLE IF EXISTS preset_values_demo");
        onCreate(sqlitedatabase);
    }

    void addPath(Pathvalues pathvalues) {
        SQLiteDatabase sqlitedatabase = getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("Y1", pathvalues.getY1());
        contentvalues.put("Y2", pathvalues.getY2());
        contentvalues.put("Y3", pathvalues.getY3());
        contentvalues.put("Y4", pathvalues.getY4());
        contentvalues.put("Y5", pathvalues.getY5());
        sqlitedatabase.insert("oldpathdemo", null, contentvalues);
        sqlitedatabase.close();
    }

    void addPreset(Presetvalues presetvalues) {
        SQLiteDatabase sqlitedatabase = getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("presetname", presetvalues.getPRESETNAME());
        contentvalues.put("PY1", presetvalues.getPY1());
        contentvalues.put("PY2", presetvalues.getPY2());
        contentvalues.put("PY3", presetvalues.getPY3());
        contentvalues.put("PY4", presetvalues.getPY4());
        contentvalues.put("PY5", presetvalues.getPY5());
        sqlitedatabase.insert("preset_values_demo", null, contentvalues);
        sqlitedatabase.close();
    }

    void addvalue(Buttonvalues buttonvalues) {
        SQLiteDatabase sqlitedatabase = getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("bass", buttonvalues.getBass());
        contentvalues.put("volume", buttonvalues.getVol());
        contentvalues.put("virtualizer", buttonvalues.getVirtualizer());
        contentvalues.put("B1", buttonvalues.getB1());
        contentvalues.put("B2", buttonvalues.getB2());
        contentvalues.put("B3", buttonvalues.getB3());
        sqlitedatabase.insert("button_values", null, contentvalues);
        sqlitedatabase.close();
    }

    public List<Pathvalues> getAllContacts() {
        ArrayList<Pathvalues> arraylist = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("SELECT  * FROM oldpathdemo", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Pathvalues pathvalues = new Pathvalues();
                pathvalues.setID(Integer.parseInt(cursor.getString(0)));
                pathvalues.setY1(Integer.parseInt(cursor.getString(1)));
                pathvalues.setY2(Integer.parseInt(cursor.getString(2)));
                pathvalues.setY3(Integer.parseInt(cursor.getString(3)));
                pathvalues.setY4(Integer.parseInt(cursor.getString(4)));
                pathvalues.setY5(Integer.parseInt(cursor.getString(5)));
                arraylist.add(pathvalues);
            } while (cursor.moveToNext());
        }
        return arraylist;
    }

    public List<Buttonvalues> getAllValues() {
        ArrayList<Buttonvalues> arraylist = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("SELECT  * FROM button_values", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Buttonvalues buttonvalues = new Buttonvalues();
                buttonvalues.setIDS(Integer.parseInt(cursor.getString(0)));
                buttonvalues.setBass(Integer.parseInt(cursor.getString(1)));
                buttonvalues.setVol(Integer.parseInt(cursor.getString(2)));
                buttonvalues.setVirtualizer(Integer.parseInt(cursor.getString(3)));
                buttonvalues.setB1(Integer.parseInt(cursor.getString(4)));
                buttonvalues.setB2(Integer.parseInt(cursor.getString(5)));
                buttonvalues.setB3(Integer.parseInt(cursor.getString(6)));
                arraylist.add(buttonvalues);
            } while (cursor.moveToNext());
        }
        return arraylist;
    }

    public int getContactsCount() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT  * FROM oldpathdemo", null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }

    public int getpresetCount() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM preset_values_demo", null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }

    public List<Presetvalues> getpresetbyid(int i) {
        ArrayList<Presetvalues> arraylist = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query("preset_values_demo", new String[]{
                "PID", "presetname", "PY1", "PY2", "PY3", "PY4", "PY5"
        }, "PID=?", new String[]{
                String.valueOf(i)
        }, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Presetvalues presetvalues = new Presetvalues();
                presetvalues.setPRESETNAME(cursor.getString(1));
                presetvalues.setPY1(Integer.parseInt(cursor.getString(2)));
                presetvalues.setPY2(Integer.parseInt(cursor.getString(3)));
                presetvalues.setPY3(Integer.parseInt(cursor.getString(4)));
                presetvalues.setPY4(Integer.parseInt(cursor.getString(5)));
                presetvalues.setPY5(Integer.parseInt(cursor.getString(6)));
                arraylist.add(presetvalues);
            } while (cursor.moveToNext());
        }
        return arraylist;
    }

    public int getrowCount() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT  * FROM button_values", null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }


    public int updateButtonValue(Buttonvalues buttonvalues) {
        SQLiteDatabase sqlitedatabase = getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("bass", buttonvalues.getBass());
        contentvalues.put("volume", buttonvalues.getVol());
        contentvalues.put("virtualizer", buttonvalues.getVirtualizer());
        contentvalues.put("B1", buttonvalues.getB1());
        contentvalues.put("B2", buttonvalues.getB2());
        contentvalues.put("B3", buttonvalues.getB3());
        return sqlitedatabase.update("button_values", contentvalues, "ids = ?", new String[]{
                String.valueOf(buttonvalues.getIDS())
        });
    }

    public int updateContact(Pathvalues pathvalues) {
        SQLiteDatabase sqlitedatabase = getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("Y1", pathvalues.getY1());
        contentvalues.put("Y2", pathvalues.getY2());
        contentvalues.put("Y3", pathvalues.getY3());
        contentvalues.put("Y4", pathvalues.getY4());
        contentvalues.put("Y5", pathvalues.getY5());
        return sqlitedatabase.update("oldpathdemo", contentvalues, "id = ?", new String[]{
                String.valueOf(pathvalues.getID())
        });
    }
}
