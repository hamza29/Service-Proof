package com.glowingsoft.serviceproof;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.glowingsoft.serviceproof.MainActivity.PREFERENCE;
import static com.glowingsoft.serviceproof.MainActivity.PREF_CONCERN;
import static com.glowingsoft.serviceproof.MainActivity.PREF_NAME;
import static com.glowingsoft.serviceproof.MainActivity.PREF_SIZE;
import static com.glowingsoft.serviceproof.MainActivity.PREF_STATUS;
import static com.glowingsoft.serviceproof.MainActivity.PREF_TYPE;

public class CameraActivity extends AppCompatActivity {
    Button btnImageTake, btnnext, btndownl;
    ImageView imageView;
    ContentValues values;
    Uri imageUri;
    TextView textView, textView1, textView2, textView3, textView4;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    SharedPreferences mSharedPreferences;
    LinearLayout linearLayout, linearLayout2, linearLayout1;
    RelativeLayout contentLayout;
    private NotificationManager notifManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mSharedPreferences = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);

        contentLayout = findViewById(R.id.content);
        btnImageTake = findViewById(R.id.picture);
        linearLayout1 = findViewById(R.id.largeLabel1);
        linearLayout = findViewById(R.id.labeled);
        linearLayout2 = findViewById(R.id.largeLabel);
        textView = findViewById(R.id.txtname);
        textView1 = findViewById(R.id.txttype);
        textView2 = findViewById(R.id.txtstatus);
        textView3 = findViewById(R.id.txtsize);
        textView4 = findViewById(R.id.cncrn);
        btndownl = findViewById(R.id.download);
        btndownl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bm = screenShot(contentLayout);
                final int random = new Random().nextInt(61) + 20;
                saveBitmap(bm, "screen" + random + ".png");
//                Toast.makeText(CameraActivity.this, "Screenshot saved successfully", Toast.LENGTH_SHORT).show();
                createNotification("Screen Shot Saved", CameraActivity.this);
            }
        });
        textView.setText("" + mSharedPreferences.getString(PREF_NAME, ""));
        textView1.setText("" + mSharedPreferences.getString(PREF_TYPE, ""));
        textView2.setText("" + mSharedPreferences.getString(PREF_STATUS, ""));
        textView3.setText("" + mSharedPreferences.getString(PREF_SIZE, ""));
        textView4.setText("" + mSharedPreferences.getString(PREF_CONCERN, ""));
        btnnext = findViewById(R.id.next);
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnnext.setVisibility(View.GONE);
                btndownl.setVisibility(View.VISIBLE);
                btnImageTake.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.VISIBLE);
            }
        });
        imageView = findViewById(R.id.image);
        btnImageTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnableRuntimePermission();
            }
        });


    }

    File filePath;

    public void EnableRuntimePermission() {

        Dexter.withActivity(CameraActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                isStoragePermissionGranted();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }

    public void isStoragePermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            EasyImage.openCamera(CameraActivity.this, 0012);
//            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent, 2);

        } else { //permission is automatically granted on sdk<23 upon installation
            values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your cameraImage");
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, 13);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, CameraActivity.this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
                filePath = imageFile;
                imageView.setImageBitmap(bitmap);
                imageView.setRotation(270);
                imageView.setVisibility(View.VISIBLE);
            }
        });
    }

    private static File saveBitmap(Bitmap bm, String fileName) {
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ServiceProof";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dir, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        Log.d("bitmap", bitmap.toString() + " ");
        return bitmap;
    }

    public void createNotification(String aMessage, Context context) {
        final int NOTIFY_ID = 0; // ID of notification
        String id = "12"; // default_channel_id
        String title = "CHANEL_"; // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
              intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "content://media/internal/images/media"));
//            startActivity(intent);


            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "content://media/internal/images/media"));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }

}
