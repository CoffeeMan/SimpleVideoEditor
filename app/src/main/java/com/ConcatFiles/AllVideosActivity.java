package com.ConcatFiles;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ConcatFiles.data.CroppedVideoContract;
import com.ConcatFiles.data.MergedVideosContract;
import com.ConcatFiles.data.VideoDbHelper;

import java.util.ArrayList;
import java.util.List;


public class AllVideosActivity extends AppCompatActivity {

    VideoDbHelper vDbHelper;
    List<String> mergedData = new ArrayList<>();
    List<String> trimData = new ArrayList<>();
    ListView listViewTrim;
    ListView listViewMerge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_videos);

        listViewTrim = findViewById(R.id.listTrim);
        listViewMerge = findViewById(R.id.listMerge);

        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                R.layout.list_item, R.id.textViewItem, trimData);
        listViewTrim.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter(this,
                R.layout.list_item, R.id.textViewItem, mergedData);
        listViewMerge.setAdapter(adapter1);

        listViewTrim.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> text, View v,
                                           int pos, long id) {
                String selectedFromList = (String) (listViewTrim.getItemAtPosition(pos));
                showPopupMenu(v, selectedFromList);
                return true;
            }
        });

        listViewMerge.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> text, View v,
                                           int pos, long id) {
                String selectedFromList = (String) (listViewMerge.getItemAtPosition(pos));
                showPopupMenu(v, selectedFromList);
                return true;
            }
        });

    }

    private void showPopupMenu(View v, String text) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        SQLiteDatabase db = vDbHelper.getWritableDatabase();
        String[] arguments = text.split("\n");
        popupMenu.inflate(R.menu.menu_popup);
        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_record:
                                if (arguments.length == 4) {
                                    db.delete(CroppedVideoContract.CropperdVideosEntry.TABLE_NAME, CroppedVideoContract.CropperdVideosEntry._ID + " = " + arguments[0].substring(3), null);
                                }
                                else if (arguments.length == 5) {
                                    db.delete(MergedVideosContract.mergedVideosEntry.TABLE_NAME, MergedVideosContract.mergedVideosEntry._ID + " = " + arguments[0].substring(3), null);
                                }
                                Toast.makeText(getApplicationContext(), "Success, refresh page", Toast.LENGTH_LONG);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
        popupMenu.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        vDbHelper = new VideoDbHelper(this);
        getDbData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_drop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.drop){
            final AlertDialog.Builder alert = new AlertDialog.Builder(AllVideosActivity.this);
            LinearLayout linearLayout = new LinearLayout(AllVideosActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            alert.setMessage("All entries will be deleted");
            alert.setTitle("Are you sure?");
            alert.setView(linearLayout);
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AllVideosActivity.this.deleteDatabase(vDbHelper.getDatabaseName());
                    finish();
                    dialogInterface.dismiss();

                }
            });
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }




    private void getDbData() {
        SQLiteDatabase db = vDbHelper.getReadableDatabase();

        String[] projectionMerge = {
                MergedVideosContract.mergedVideosEntry._ID,
                MergedVideosContract.mergedVideosEntry.COLUMN_OWN_PATH,
                MergedVideosContract.mergedVideosEntry.COLUMN_BASE_PATH_FIRST,
                MergedVideosContract.mergedVideosEntry.COLUMN_BASE_PATH_SECOND,
                MergedVideosContract.mergedVideosEntry.COLUMN_DURATION
        };

        String[] projectionTrim = {
                CroppedVideoContract.CropperdVideosEntry._ID,
                CroppedVideoContract.CropperdVideosEntry.COLUMN_OWN_PATH,
                CroppedVideoContract.CropperdVideosEntry.COLUMN_BASE_PATH,
                CroppedVideoContract.CropperdVideosEntry.COLUMN_DURATION
        };

        Cursor cursor1 = db.query(
                MergedVideosContract.mergedVideosEntry.TABLE_NAME,
                projectionMerge,
                null,
                null,
                null,
                null,
                null);

        Cursor cursor2 = db.query(
                CroppedVideoContract.CropperdVideosEntry.TABLE_NAME,
                projectionTrim,
                null,
                null,
                null,
                null,
                null);
        try {
            int idColumnIndex = cursor2.getColumnIndex(CroppedVideoContract.CropperdVideosEntry._ID);
            int columnOwnPathIndex = cursor2.getColumnIndex(CroppedVideoContract.CropperdVideosEntry.COLUMN_OWN_PATH);
            int columnBasePathIndex = cursor2.getColumnIndex(CroppedVideoContract.CropperdVideosEntry.COLUMN_BASE_PATH);
            int columnDurationIndex = cursor2.getColumnIndex(CroppedVideoContract.CropperdVideosEntry.COLUMN_DURATION);
            while (cursor2.moveToNext()) {
                int currentID = cursor2.getInt(idColumnIndex);
                String currentOwnPath = cursor2.getString(columnOwnPathIndex);
                String currentBasePath = cursor2.getString(columnBasePathIndex);
                int currentDuration = cursor2.getInt(columnDurationIndex);
                String[] tmpData = {"id: " + String.valueOf(currentID), "Path: " + currentOwnPath, "Base path: " + currentBasePath, "Duration: " + String.valueOf(currentDuration)};
                String res = "";
                for (String tmp : tmpData)
                    res += tmp + "\n";
                res = res.substring(0, res.length() - 1);
                trimData.add(res);
            }

            int newIdColumnIndex = cursor1.getColumnIndex(MergedVideosContract.mergedVideosEntry._ID);
            int newColumnOwnPathIndex = cursor1.getColumnIndex(MergedVideosContract.mergedVideosEntry.COLUMN_OWN_PATH);
            int columnFirstBasePathIndex = cursor1.getColumnIndex(MergedVideosContract.mergedVideosEntry.COLUMN_BASE_PATH_FIRST);
            int columnSecondBasePathIndex = cursor1.getColumnIndex(MergedVideosContract.mergedVideosEntry.COLUMN_BASE_PATH_SECOND);
            int newColumnDurationIndex = cursor1.getColumnIndex(MergedVideosContract.mergedVideosEntry.COLUMN_DURATION);
            while (cursor1.moveToNext()) {
                int currentID = cursor1.getInt(newIdColumnIndex);
                String currentOwnPath = cursor1.getString(newColumnOwnPathIndex);
                String currentBasePath1 = cursor1.getString(columnFirstBasePathIndex);
                String currentBasePath2 = cursor1.getString(columnSecondBasePathIndex);
                int currentDuration = cursor1.getInt(newColumnDurationIndex);
                String[] tmpData = {"id: " + String.valueOf(currentID), "Path: " + currentOwnPath, "Base path1: " + currentBasePath1, "Base path2: " + currentBasePath2, "Duration: " + String.valueOf(currentDuration)};
                String res = "";
                for (String tmp : tmpData)
                    res += tmp + "\n";
                res = res.substring(0, res.length() - 1);
                mergedData.add(res);
            }
        } finally {
            cursor1.close();
            cursor2.close();
        }
    }


}