package com.ConcatFiles;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedList;
import java.util.List;

public class MergeVideos extends AppCompatActivity {
    ImageView img1, img2;
    Button button1, button2;
    Uri firstUri, secondUri;
    int durationFirstVideo, durationSecondVideo;
    String[] command;
    String filePrefix, pathVideo1, pathVideo2;
    File dest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_videos);
        img1 = (ImageView) findViewById(R.id.imageView1);
        img2 = (ImageView) findViewById(R.id.imageView2);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);


    }

    public void button1Listener(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/*");
        startActivityForResult(i, 100);
    }

    public void button2Listener(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/*");
        startActivityForResult(i, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            button1.setText("Select another video");
            firstUri = data.getData();
            img1.setImageResource(R.drawable.ic_ok);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, firstUri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            durationFirstVideo = (int) Long.parseLong(time);
            retriever.release();

        } else if (requestCode == 200 && resultCode == RESULT_OK){
            button2.setText("Select another video");
            secondUri = data.getData();
            img2.setImageResource(R.drawable.ic_ok);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, secondUri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            durationSecondVideo = (int) Long.parseLong(time);
            retriever.release();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.merge){
            final AlertDialog.Builder alert = new AlertDialog.Builder(MergeVideos.this);
            LinearLayout linearLayout = new LinearLayout(MergeVideos.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(50, 0, 50, 100);
            final EditText input = new EditText(MergeVideos.this);
            input.setLayoutParams(lp);
            input.setGravity(Gravity.TOP|Gravity.START);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            linearLayout.addView(input, lp);
            alert.setMessage("Set video name");
            alert.setTitle("Change video name");
            alert.setView(linearLayout);
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    filePrefix = input.getText().toString();
                    mergeVideos(filePrefix);
                    finish();
                    dialogInterface.dismiss();

                }
            });
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void mergeVideos(String filNam) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/MergeVideos");
        if (!folder.exists()){
            folder.mkdir();
        }
        //TODO
        filePrefix = filNam;
        String fileExt = ".mp4";
        pathVideo1 = getRealPathFromUri(getApplicationContext(), firstUri);
        pathVideo2 = getRealPathFromUri(getApplicationContext(), secondUri);

        try {
            Movie[] movies = new Movie[]{
                MovieCreator.build(pathVideo1),
                    MovieCreator.build(pathVideo2)
            };
            List<Track> videoTracks = new LinkedList<Track>();
            List<Track> audioTracks = new LinkedList<Track>();
            for (Movie m : movies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            }
            Movie result = new Movie();
            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }
            BasicContainer out =  (BasicContainer) new DefaultMp4Builder().build(result);
            WritableByteChannel fc = new RandomAccessFile(
                    String.format(Environment.getExternalStorageDirectory()+ "/MergeVideos/"+filNam + ".mp4"), "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
            Toast.makeText(this, "Videos merge successful", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_merge, menu);
        return true;
    }
}