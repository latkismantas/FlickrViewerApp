package uk.ac.kent.ml555.flickrviewer;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Random;

public class DetailsActivity extends AppCompatActivity {


    private int photoPosition;
    private  ImageInfo photo;
    private TextView descriptionText;

    private TextView imageTitle;
    private ImageView mainImage;
    private TextView nameText;

    FloatingActionButton moreBtn;
    FloatingActionButton downloadBtn;
    FloatingActionButton shareBtn;
    FloatingActionButton favBtn;

    boolean areButtonsVisible = false;



    DatabaseHelper dbHelper;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        moreBtn = (FloatingActionButton) findViewById(R.id.moreBtn);
        downloadBtn = (FloatingActionButton) findViewById(R.id.downloadBtn);
        shareBtn = (FloatingActionButton) findViewById(R.id.shareBtn);
        favBtn = (FloatingActionButton) findViewById(R.id.favBtn);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        Intent intent = getIntent();
        photoPosition = intent.getIntExtra("PHOTO_POSITION",0 );
        photo = NetworkMgr.getInstance(this).imageList.get(photoPosition);

        mainImage = (ImageView) findViewById(R.id.mainImage);

        descriptionText = (TextView) findViewById(R.id.descriptionText) ;
        descriptionText.setText(photo.getDescription());

        nameText = (TextView) findViewById(R.id.nameText);
        nameText.setText(photo.owner);

        imageTitle = (TextView) findViewById(R.id.imageTitle);
        imageTitle.setText(photo.getTitle());

        NetworkMgr netMgr = NetworkMgr.getInstance(this);
        netMgr.imageLoader.get(photo.getLargeImage(), imageListener);




        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        mainImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                Intent intent = new Intent (DetailsActivity.this, FullscreenActivity.class);
                intent.putExtra("PHOTO_POSITION", photoPosition);
                startActivity(intent);
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!areButtonsVisible){
//                    downloadBtn.setVisibility(View.VISIBLE);
//                    shareBtn.setVisibility(View.VISIBLE);
//                    favBtn.setVisibility(View.VISIBLE);
                    areButtonsVisible = true;

                    moreBtn.setImageResource(R.drawable.ic_close_white_48dp);

                    showAnimation();
                }
                else{
                    goneAnimation();
                    areButtonsVisible = false;
                    moreBtn.setImageResource(R.drawable.ic_more_horiz_white_48dp);
                }

            }
        });




        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(DetailsActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(DetailsActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);


                }

                Bitmap bitmap =  getBitmapFromURL(photo.getLargeImage());
                saveImage(bitmap);


            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTextUrl();
            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dbHelper = new DatabaseHelper(DetailsActivity.this);

                dbHelper.insert(photo.getLargeImage());

                Toast.makeText(DetailsActivity.this, "Added to your Favorites", Toast.LENGTH_SHORT).show();


            }
        });

    }




    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Share Image");
        share.putExtra(Intent.EXTRA_TEXT, photo.getLargeImage());

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    private ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
        if (response.getBitmap() != null)
            mainImage.setImageBitmap (response.getBitmap());
        }

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };




    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }





    private void saveImage(Bitmap finalBitmap) {


        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        File myDir = new File(root );
        myDir.mkdirs();
        String title = photo.getTitle().replaceAll("\\s+","_");
        String fname = title +".jpg";
        File file = new File (myDir, fname);

        try {
            FileOutputStream out = new FileOutputStream(file);
            System.out.println(file.getPath());
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            Context context = getApplicationContext();
            CharSequence text = "Image Downloaded!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showAnimation() {
        downloadBtn.animate()
                .translationY(-225)
                .alpha(100)
                .setInterpolator(new BounceInterpolator())
                .setDuration(500)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        downloadBtn.setVisibility(View.VISIBLE);
                    }
                });

        shareBtn.animate()
                .translationY(225)
                .alpha(100)
                .setInterpolator(new BounceInterpolator())
                .setDuration(500)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        shareBtn.setVisibility(View.VISIBLE);
                    }
                });


        favBtn.animate()
                .translationX(-225)
                .alpha(100)
                .setInterpolator(new BounceInterpolator())
                .setDuration(500)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        favBtn.setVisibility(View.VISIBLE);
                    }
                });

    }

    protected void goneAnimation() {
        downloadBtn.animate()

                .translationY(0)
                .alpha(0)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(300)
                .withEndAction(new Runnable() {
            @Override
            public void run() {
                downloadBtn.setVisibility(View.GONE);
            }
        });

        shareBtn.animate()
                .translationY(0)
                .alpha(0)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(300)
                .withEndAction(new Runnable() {
            @Override
            public void run() {
                shareBtn.setVisibility(View.GONE);
            }
        });


        favBtn.animate()
                .translationX(0)
                .alpha(0)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(300)
                .withEndAction(new Runnable() {
            @Override
            public void run() {
                favBtn.setVisibility(View.GONE);
            }
        });

    }











    private View.OnClickListener closeListener = new View.OnClickListener(){

        @Override
        public void onClick(View view){
            finish();

        }
    };
}
