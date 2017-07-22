package com.example.kjming.note7;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.jar.Manifest;

public class ItemActivity extends Activity {

    private EditText mTitleText, mContentText;
    private static final int START_CAMERA = 0;
    private static final int START_RECORD = 1;
    private static final int START_LOCATION = 2;
    private static final int mStartALarm = 3;
    private static final int START_COLOR = 4;
    private Item item;
    private String fileName;
    private ImageView picture;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 9487;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 9000;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 9777;
    private String recFileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        processViews();

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals("com.example.kjming.note7.EDIT_ITEM")) {
            Serializable serializable = intent.getSerializableExtra("com.example.kjming.note7.Item");
            if (serializable != null && serializable instanceof Item) {
                item = (Item) serializable;
                mTitleText.setText(item.getTitle());
                mContentText.setText(item.getContent());
            } else {
                Toast.makeText(this, "The note was not defined", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            item = new Item();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case START_CAMERA:
                    item.setFileName(fileName);
                    break;
                case START_COLOR:
                    int colorId = data.getIntExtra("colorId", Colors.LIGHTGREY.parseColor());
                    item.setColor(getColors(colorId));
                    break;
                case START_RECORD:
                    item.setRecFileName(recFileName);
                    break;
                case START_LOCATION :
                    double lat = data.getDoubleExtra("lag",0.0);
                    double lng = data.getDoubleExtra("lng",0.0);
                    item.setLatitude(lat);
                    item.setLongitude(lng);
                    break;
            }
        }
    }

    private void processViews() {
        mTitleText = (EditText) findViewById(R.id.title_text);
        mContentText = (EditText) findViewById(R.id.content_text);
        picture = (ImageView) findViewById(R.id.picture);
    }

    public void onSubmit(View view) {
        if (view.getId() == R.id.ok_item) {
            String titleText = mTitleText.getText().toString();
            String contentText = mContentText.getText().toString();

            item.setTitle(titleText);
            item.setContent(contentText);

            if (getIntent().getAction().equals("com.example.kjming.note7.EDIT_ITEM")) {
                item.setLastModify(new Date().getTime());
            } else {
                item.setDatetime(new Date().getTime());
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                int color = sharedPreferences.getInt("DEFAULT_COLOR",-1);
                item.setColor(getColors(color));
            }


            Intent result = getIntent();
            result.putExtra("com.example.kjming.note7.Item", item);
            setResult(Activity.RESULT_OK, result);
        }
        finish();
    }

    public void clickFunction(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.take_picture:
                requestStoragePermission();
                break;
            case R.id.record_sound:
                requestRecordPermission();
                break;
            case R.id.set_location:
                requestLocationPermission();
                break;
            case R.id.set_alarm:
                pocessSetAlarm();
                break;
            case R.id.select_color:
                startActivityForResult(new Intent(this, ColorActivity.class), START_COLOR);
                break;
        }
    }

    private void takePicture() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File pictureFile = configFileName("p", ".jpg");
        Uri uri = FileProvider.getUriForFile(this, "com.example.kjming.note7.fileprovider", pictureFile);
        // Uri uri = Uri.fromFile(pictureFile);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intentCamera, START_CAMERA);
    }

    private File configFileName(String prefix, String extension) {
        if (item.getFileName() != null && item.getFileName().length() > 0) {
            fileName = item.getFileName();
        }else

        {
            fileName = FileUtil.getUniqueFileName();
        }

        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),prefix+fileName+extension);

    }

    private void requestStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(hasPermission!= PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
                return;
            }
        }
        takePicture();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }else {
                Toast.makeText(this,R.string.write_external_storage_denied,Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode==REQUEST_RECORD_AUDIO_PERMISSION){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                processRecord();
            }else {
                Toast.makeText(this,R.string.record_audio_denied,Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode ==REQUEST_FINE_LOCATION_PERMISSION){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                processLocation();
            }else {
                Toast.makeText(this,R.string.write_external_storage_denied,Toast.LENGTH_SHORT).show();
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(item.getFileName()!=null&&item.getFileName().length()>0) {
            File file = configFileName("p",".jpg");
            if(file.exists()) {
                picture.setVisibility(View.VISIBLE);
                FileUtil.fileToImageView(file.getAbsolutePath(),picture);
            }
        }
    }

	public static Colors getColors(int color) {
        Colors result = Colors.LIGHTGREY;

        if (color == Colors.BLUE.parseColor()) {
            result = Colors.BLUE;
        } else if (color == Colors.PURPLE.parseColor()) {
            result = Colors.PURPLE;
        } else if (color == Colors.GREEN.parseColor()) {
            result = Colors.GREEN;
        } else if (color == Colors.ORANGE.parseColor()) {
            result = Colors.ORANGE;
        } else if (color == Colors.RED.parseColor()) {
            result = Colors.RED;
        }
        return result;
    }

    public void processRecord() {
        final File recordFile =configFileName("R",".3gp");
        if (recordFile.exists()) {
            AlertDialog.Builder d = new AlertDialog.Builder(this);
            d.setTitle(R.string.title_record).setCancelable(false);
            d.setPositiveButton(R.string.record_play, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent playIntent = new Intent(ItemActivity.this,PlayActivity.class);
                    playIntent.putExtra("fileName",recordFile.getAbsolutePath());
                    startActivity(playIntent);

                }
            });
            d.setNegativeButton(R.string.record_new,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent recordIntent = new Intent(ItemActivity.this,RecordActivity.class);
                    recordIntent.putExtra("mFilename",recordFile.getAbsolutePath());
                    startActivityForResult(recordIntent,START_RECORD);
                }
            });
            d.setNegativeButton(android.R.string.cancel,null);
            d.show();
        }else {
            Intent recordIntent = new Intent(this,RecordActivity.class);
            recordIntent.putExtra("mFilename",recordFile.getAbsolutePath());
            startActivityForResult(recordIntent,START_RECORD);
        }
    }

    private File configRecFileName(String prefix,String extension) {
        if (item.getRecFileName()!=null&&item.getRecFileName().length()>0) {
            recFileName = item.getRecFileName();
        }else {
            recFileName = FileUtil.getUniqueFileName();
        }
        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),prefix +recFileName+extension);
    }

    private void requestRecordPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = checkSelfPermission(android.Manifest.permission.RECORD_AUDIO);
            if(hasPermission!= PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO},REQUEST_RECORD_AUDIO_PERMISSION);
                return;
            }
        }
        processRecord();
    }

    private void processLocation() {
        Intent intentMap = new Intent(this,MapsActivity.class);

        intentMap.putExtra("lat",item.getLatitude());
        intentMap.putExtra("lng",item.getLongitude());
        intentMap.putExtra("title",item.getTitle());
        intentMap.putExtra("datetime",item.getLocaleDatetime());

        startActivityForResult(intentMap,START_LOCATION);
    }

    private void requestLocationPermission() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            int hasPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if(hasPermission!=PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
            }else {
                processLocation();
            }
        }
    }

    private void pocessSetAlarm() {
        Calendar calendar = Calendar.getInstance();
        if (item.getAlarmDatetime()!=0) {
            calendar.setTimeInMillis(item.getAlarmDatetime());
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        final Calendar alarm = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        alarm.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        alarm.set(Calendar.MINUTE,minute);
                        item.setAlarmDatetime(alarm.getTimeInMillis());
                    }
                };
        final TimePickerDialog tpd = new TimePickerDialog(this,timeSetListener,hour,minute,true);

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                alarm.set(Calendar.YEAR, year);
                alarm.set(Calendar.MONTH, month);
                alarm.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tpd.show();
            }
        };

        final DatePickerDialog dpd = new DatePickerDialog(this,dateSetListener,year,month,day);
        dpd.show();

    }

    public void clickPicture(View view) {
        Intent intent = new Intent(this,PictureActivity.class);
        intent.putExtra("pictureName",configFileName("p",".jpg").getAbsolutePath());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,picture,"picture");
            startActivity(intent,options.toBundle());
        }else {
            startActivity(intent);
        }
    }


}
