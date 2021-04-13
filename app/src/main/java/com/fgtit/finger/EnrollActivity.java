package com.fgtit.finger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.scanner.CaptureActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fgtit.app.ActivityList;
import com.fgtit.data.GlobalData;
import com.fgtit.data.UserItem;
import com.fgtit.device.Constants;
import com.fgtit.device.FPModule;
import com.fgtit.fpcore.FPMatch;
import com.fgtit.network.APIClient;
import com.fgtit.network.ApiHelper;
import com.fgtit.network.DeptsResponse;
import com.fgtit.network.ImageApi;
import com.fgtit.network.ImageRequest;
import com.fgtit.network.ImgResponse;
import com.fgtit.network.Response;
import com.fgtit.network.StatesResponse;
import com.fgtit.utils.AppPrefs;
import com.fgtit.utils.ExtApi;
import com.fgtit.utils.Threading;
import com.fgtit.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class EnrollActivity extends AppCompatActivity {

    private FPModule fpm;
    private EditText mid, dob, matric_no, first_name,
            last_name, middle_name, madien_name, card, card_serial,
            fingerprint1, fingerprint2, fingerprint3, fingerprint4, email, phone,
            address, nok_name, nok_address, nok_email, nok_phone, home_town, specialization, level, session;
    private ImageView imgPhoto, imgFinger1, imgFinger2, imgFinger3, imgFinger4;

    private byte[] jpgbytes = null;

    private byte refdata[] = new byte[Constants.TEMPLATESIZE * 2];
    private int refsize = 0;

    private byte bmpdata[] = new byte[Constants.RESBMP_SIZE];
    private int bmpsize = 0;

    private byte[] model1 = new byte[512];
    private byte[] model2 = new byte[512];

    private ImageView fpImage;
    private TextView tvFpStatus;
    private Dialog fpDialog = null;
    private int iFinger = 0;
    //Barcode
    private byte[] databuf = new byte[1024];
    private int datasize = 0;
    private int soundIda;
    private SoundPool soundPool;

    //NFC
    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;

    UserItem person = new UserItem();
    public String CardSN = "";

    private Spinner gender, marrital_status, religion, acc_session, nationality, state, lga, faculty, department;

    private boolean bIsCancel = false;
    private boolean bCapture = false;
    private boolean isenrol1;
    private boolean isenrol2;
    private boolean isenrol3;
    private boolean isenrol4;

    private TextView text1;
    private TextView text2;
    private TextView text3;

    //for date picker.
    private static final String TAG = "dd/mm/yy";
    private TextView dateTextView;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    List<DeptsResponse> deptsResponse =AppPrefs.get().getFacultyDeps();
    List<StatesResponse> statesResponses = AppPrefs.get().getStateLgas();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mid = (EditText) findViewById(R.id.editText1);
        dob = (EditText) findViewById(R.id.editText2);
        matric_no = (EditText) findViewById(R.id.matricno);
        first_name = (EditText) findViewById(R.id.firstname);
        last_name = (EditText) findViewById(R.id.lastname);
        middle_name = (EditText) findViewById(R.id.middlename);
        madien_name = (EditText) findViewById(R.id.maidenname);

        fingerprint1 = (EditText) findViewById(R.id.editText6a);
        fingerprint2 = (EditText) findViewById(R.id.editText26);
        fingerprint3 = (EditText) findViewById(R.id.editText6);
        fingerprint4 = (EditText) findViewById(R.id.editText7);

        email = (EditText) findViewById(R.id.editText11);
        phone = (EditText) findViewById(R.id.editText111);//ID
        address = (EditText) findViewById(R.id.editText11a);

        nok_name = (EditText) findViewById(R.id.editText11c);
        nok_email = (EditText) findViewById(R.id.nokemail);
        nok_address = (EditText) findViewById(R.id.nokaddress);
        nok_phone = (EditText) findViewById(R.id.nokphone);
        home_town = (EditText) findViewById(R.id.hometown);
        specialization = (EditText) findViewById(R.id.specialization);
        level = (EditText) findViewById(R.id.academiclevel);

        card = (EditText) findViewById(R.id.editText6a);
        card_serial = (EditText) findViewById(R.id.editText26);

        text1 = (TextView) findViewById(R.id.textView3);
        text2 = (TextView) findViewById(R.id.textView4);
        text3 = (TextView) findViewById(R.id.textView5);

        imgPhoto = (ImageView) findViewById(R.id.imageView1);
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bCapture = true;
                Intent intent = new Intent(EnrollActivity.this, CameraExActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", "1");
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        //date picker
        dateTextView = (EditText) findViewById(R.id.editText2);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EnrollActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        dateSetListener,
                        year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

                dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month++;
                        String datepicked = year + "/" + month + "/" + day;
                        dateTextView.setText(datepicked);
                    }
                };
            }
        });

        imgFinger1 = (ImageView) findViewById(R.id.imageView2);
        imgFinger1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                FPDialog(3);
            }
        });

        imgFinger2 = (ImageView) findViewById(R.id.imageView3);
        imgFinger2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FPDialog(4);
            }
        });

        imgFinger3 = (ImageView) findViewById(R.id.imageViewa2);
        imgFinger3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FPDialog(1);
            }
        });

        imgFinger4 = (ImageView) findViewById(R.id.imageView12);
        imgFinger4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FPDialog(2);
            }
        });


        final ImageView imgBardcode1d = (ImageView) findViewById(R.id.imageView4);
        imgBardcode1d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //	ToastUtil.showToastTop(EnrollActivity.this,"Please sweep Barcode...");
                //	BarcodeOpen();
            }
        });

        final ImageView imgBardcode2d = (ImageView) findViewById(R.id.imageView5);
        imgBardcode2d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                bCapture = true;
                Intent intent = new Intent(EnrollActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        final ImageView imgCard = (ImageView) findViewById(R.id.imageView6);
        imgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ToastUtil.showToastTop(EnrollActivity.this, "Please put the card...");
            }
        });


        gender = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array
                .us1_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter1);
        gender.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
                //	person.type=pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //nothing to do
            }
        });
        gender.setSelection(1);


        nationality = (Spinner) findViewById(R.id.nationality);
        state = (Spinner) findViewById(R.id.state);
        lga = (Spinner) findViewById(R.id.lga);
        religion = (Spinner) findViewById(R.id.religion);
        acc_session = (Spinner) findViewById(R.id.session);

        faculty = (Spinner) findViewById(R.id.faculty);
        department = (Spinner) findViewById(R.id.department);

        marrital_status = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array
                .us2_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marrital_status.setAdapter(adapter2);
        marrital_status.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
                //person.ident=pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        marrital_status.setSelection(0);


        soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
        soundIda = soundPool.load(this, R.raw.dong, 1);

        //Card
        InitReadCard();
        //FP
        fpm = new FPModule();
        fpm.InitMatch();
        fpm.SetTimeOut(Constants.TIMEOUT_LONG);
        fpm.SetLastCheckLift(true);


        ArrayList<String> faculties = new ArrayList<String>();
        ArrayList<String> departments = new ArrayList<String>();
        for(int counter=0; counter<=deptsResponse.size()-1; counter++){
            if(!faculties.contains(deptsResponse.get(counter).faculty)) {
                faculties.add(deptsResponse.get(counter).faculty);
            }
            departments.add(deptsResponse.get(counter).department);
        }
        ArrayAdapter<String> faculty_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,faculties);
        ArrayAdapter<String> department_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,departments);
        faculty.setAdapter(faculty_adapter);
        department.setAdapter(department_adapter);


        ArrayList<String> stateList = new ArrayList<String>();
        for(int counter=0; counter<=statesResponses.size()-1; counter++){
            if(!stateList.contains(statesResponses.get(counter).state)){
                stateList.add(statesResponses.get(counter).state);
            }
        }
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,stateList);
        state.setAdapter(stateAdapter);

        state.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedState = state.getSelectedItem().toString();
                ArrayList<String> lgaList = new ArrayList<String>();
                for(int counter=0; counter<=statesResponses.size()-1; counter++){
                    if(statesResponses.get(counter).state.equals(selectedState)){
                        lgaList.add(statesResponses.get(counter).lga);
                    }
                }
                ArrayAdapter<String> lgaAdapter = new ArrayAdapter<String>(EnrollActivity.this,android.R.layout.simple_spinner_item,lgaList);
                lga.setAdapter(lgaAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "New state has been selected", Toast.LENGTH_LONG).show();
            }
        });
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.FPM_DEVICE:
                    switch (msg.arg1) {
                        case Constants.DEV_OK:
                            tvFpStatus.setText("Open Device OK");
                            fpm.GenerateTemplate(2);
                            break;
                        case Constants.DEV_FAIL:
                            tvFpStatus.setText("Open Device Fail");
                            break;
                        case Constants.DEV_ATTACHED:
                            tvFpStatus.setText("USB Device Attached");
                            break;
                        case Constants.DEV_DETACHED:
                            tvFpStatus.setText("USB Device Detached");
                            break;
                        case Constants.DEV_CLOSE:
                            tvFpStatus.setText("Device Close");
                            break;
                    }
                    break;
                case Constants.FPM_PLACE:
                    tvFpStatus.setText("Place Finger");
                    break;
                case Constants.FPM_LIFT:
                    tvFpStatus.setText("Lift Finger");
                    break;
                case Constants.FPM_GENCHAR: {
                    if (msg.arg1 == 1) {
                        tvFpStatus.setText("Enrol Template OK");
                        refsize = fpm.GetTemplateByGen(refdata);
                        //TODO Insert data into the database
                        for (int i = 0; i < GlobalData.getInstance().userList.size(); i++) {
                            if (GlobalData.getInstance().userList.get(i).bytes1 != null) {
                                if (FPMatch.getInstance().MatchTemplate(refdata, GlobalData
                                        .getInstance().userList.get(i).bytes1) > 60) {
                                    tvFpStatus.setText(getString(R.string.txt_fpduplicate));
                                    return;
                                }
                            }
                            if (GlobalData.getInstance().userList.get(i).bytes2 != null) {
                                if (FPMatch.getInstance().MatchTemplate(refdata, GlobalData
                                        .getInstance().userList.get(i).bytes2) > 60) {
                                    tvFpStatus.setText(getString(R.string.txt_fpduplicate));
                                    return;
                                }
                            }
                        }

                        if (iFinger == 1) {
                            fingerprint1.setText(getString(R.string.txt_fpenrolok));
                            System.arraycopy(refdata, 0, EnrollActivity.this.model1, 0, 512);
                            isenrol1 = true;
                        }
                        if (iFinger == 2) {
                            fingerprint2.setText(getString(R.string.txt_fpenrolok));
                            System.arraycopy(refdata, 0, EnrollActivity.this.model2, 0, 512);
                            isenrol2 = true;
                        }
                        if (iFinger == 3) {
                            fingerprint3.setText(getString(R.string.txt_fpenrolok));
                            System.arraycopy(refdata, 0, EnrollActivity.this.model1, 0, 512);
                            isenrol3 = true;
                        }
                        if (iFinger == 4) {
                            fingerprint4.setText(getString(R.string.txt_fpenrolok));
                            System.arraycopy(refdata, 0, EnrollActivity.this.model1, 0, 512);
                            isenrol4 = true;
                        }

                        tvFpStatus.setText(getString(R.string.txt_fpenrolok));
                        fpDialog.cancel();
                    } else {
                        tvFpStatus.setText("Generate Template Fail");
                    }
                }
                break;
                case Constants.FPM_NEWIMAGE: {
                    bmpsize = fpm.GetBmpImage(bmpdata);
                    Bitmap bm1 = BitmapFactory.decodeByteArray(bmpdata, 0, bmpsize);
                    fpImage.setImageBitmap(bm1);
                }
                break;
                case Constants.FPM_TIMEOUT:
                    tvFpStatus.setText("Time Out");
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1: {
                bCapture = false;
                Bundle bl = data.getExtras();
                String barcode = bl.getString("barcode");
                middle_name.setText(barcode);
            }
            break;
            case 2:
                break;
            case 3: {
                bCapture = false;
                Bundle bl = data.getExtras();
                String id = bl.getString("id");
                Toast.makeText(EnrollActivity.this, "Pictures Finish", Toast.LENGTH_SHORT).show();
                byte[] photo = bl.getByteArray("photo");
                if (photo != null) {
                    try {
                        Matrix matrix = new Matrix();
                        Bitmap bm = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                        matrix.preRotate(-90);
                        Bitmap nbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                                matrix, true);

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        nbm.compress(Bitmap.CompressFormat.JPEG, 80, out);
                        jpgbytes = out.toByteArray();

                        Bitmap bitmap = BitmapFactory.decodeByteArray(jpgbytes, 0, jpgbytes.length);
                        imgPhoto.setImageBitmap(bitmap);
                    } catch (Exception e) {
                    }
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enroll, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            AlertDialog.Builder builder = new Builder(this);
            builder.setTitle("Back");
            builder.setMessage("Data not save, back?");
            //builder.setCancelable(false);
            builder.setPositiveButton("Cancel", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Back", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.create().show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save: {
                if (CheckInputData(1))
                {
                    person.id = (mid.getText().toString());
                    person.name = (first_name.getText().toString() + " " + last_name.getText().toString());
                    if (isenrol1) {
                        person.template1 = ExtApi.BytesToBase64(model1, model1.length);
                        person.bytes1 = new byte[model1.length];
                        System.arraycopy(model1, 0, person.bytes1, 0, model1.length);
                    }
                    if (isenrol2) {
                        person.template2 = ExtApi.BytesToBase64(model2, model2.length);
                        person.bytes2 = new byte[model2.length];
                        System.arraycopy(model2, 0, person.bytes2, 0, model2.length);
                    }
                    person.phone = (phone.getText().toString());
                    person.first_name = (first_name.getText().toString());
                    person.last_name = (last_name.getText().toString());
                    person.matric_no = (matric_no.getText().toString());
                    person.middle_name = (madien_name.getText().toString());
                    person.madien_name = (madien_name.getText().toString());
                    person.dob = (dob.getText().toString());
                    person.sex = (gender.getSelectedItem().toString());
                    person.marrital_status = (marrital_status.getSelectedItem().toString());
                    person.religion = (religion.getSelectedItem().toString());
                    person.card = (card.getText().toString());
                    person.card_serial = (card_serial.getText().toString());
                    person.email = (email.getText().toString());
                    person.address = (address.getText().toString());
                    person.nationality = (nationality.getSelectedItem().toString());
                    person.state = (state.getSelectedItem().toString());

                    String lgaID="0";
                    for(int j=0; j<=statesResponses.size()-1; j++){
                        if(statesResponses.get(j).state.equals(state.getSelectedItem().toString()) &&
                        statesResponses.get(j).lga.equals(lga.getSelectedItem().toString())){
                            lgaID = statesResponses.get(j).lgaId;
                        }
                    }
                    person.lga = (lgaID);

                    person.nok_name = (nok_name.getText().toString());
                    person.nok_address = (nok_address.getText().toString());
                    person.nok_email = (nok_email.getText().toString());
                    person.nok_phone = (nok_phone.getText().toString());
                    person.home_town = (home_town.getText().toString());
                    person.faculty = (faculty.getSelectedItem().toString());
                    person.department = (department.getSelectedItem().toString());

                    String deptID ="0";
                    for(int counter=0; counter<=deptsResponse.size()-1; counter++){
                        if(deptsResponse.get(counter).department.equals(department.getSelectedItem().toString()) &&
                        deptsResponse.get(counter).faculty.equals(faculty.getSelectedItem().toString())){
                            deptID = deptsResponse.get(counter).id;
                        }
                    }
                    person.depttype=deptID;

                    person.specialization = (specialization.getText().toString());
                    person.level = (level.getText().toString());
                    person.session = (acc_session.getSelectedItem().toString());
                    person.specialization = (specialization.getText().toString());


                    if (jpgbytes != null)
                        person.photo = ExtApi.BytesToBase64(jpgbytes, jpgbytes.length);
                    if (CardSN.length() > 4) person.cardsn = (CardSN);
                    else person.cardsn = ("null");

                    GlobalData.getInstance().userList.add(person);
                    GlobalData.getInstance().SaveUsersList();
                    GlobalData.getInstance().SaveUserByID(person, jpgbytes);

                    callApi();
                    
                    //uploadImgToServer("/mnt/sdcard/DCIM/Camera/IMG_20190527_101903.jpg");
                    //uploadImgToServer("/storage/sdcard0/OnePass/data/145980.jpg");

                    String base64Img = encodeImageBase64("/storage/sdcard0/OnePass/data/145980.jpg");
                    callImageApi(base64Img);

                    Toast.makeText(EnrollActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            return true;
            case R.id.action_make: {
                if (CheckInputData(0)) {
                    if (!isenrol1) {
                        Toast.makeText(EnrollActivity.this, "Please Input Template One", Toast
                                .LENGTH_SHORT).show();
                        return true;
                    }
                    if (!isenrol2) {
                        Toast.makeText(EnrollActivity.this, "Please Input Template Two", Toast
                                .LENGTH_SHORT).show();
                        return true;
                    }

                    byte[] databuf = new byte[1024];
                    int size = 1024;
                    System.arraycopy(model1, 0, databuf, 0, 512);
                    System.arraycopy(model2, 0, databuf, 512, 512);

                    //MainActivity.btReader.WriteCard(databuf,size);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDummyUserItem() {
        person.id = "6";
        person.name = random();
        person.phone = random();
        person.first_name = random();
        person.last_name = random();
        person.matric_no = random();
        person.middle_name = random();
        person.madien_name = random();
        person.dob = random();
        person.sex = "male";
        person.marrital_status = random();
        person.religion = random();
        person.card = random();
        person.card_serial = random();
        person.email = random();
        person.address = random();
        person.nationality = random();
        person.state = random();
        person.lga = random();
        person.nok_name = random();
        person.nok_address = random();
        person.nok_email = random();
        person.nok_phone = random();
        person.home_town = random();
        person.faculty = random();
        person.department = random();
        person.specialization = random();
        person.level = random();
        person.session = random();
        person.specialization = random();
    }

    //    public static String random() {
//        Random generator = new Random();
//        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(6);
//        char tempChar;
//        for (int i = 0; i < randomLength; i++) {
//            tempChar = (char) (generator.nextInt(96) + 32);
//            randomStringBuilder.append(tempChar);
//        }
//        return randomStringBuilder.toString();
//    }
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String random() {
        int randomSize = 6;
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(randomSize);
        for (int i = 0; i < randomSize; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    private void callApi() {
        Observable<Response> call = ApiHelper.getEnrollObservable(person);
        Threading.async(call, new Consumer<Response>() {
            @Override
            public void accept(Response response) throws Exception {
                person.isSyncWithBackend = true;
                GlobalData.getInstance().SaveUsersList();
                GlobalData.getInstance().SaveUserByID(person, jpgbytes);
                Log.d("", "");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("", "");
            }
        });
    }

    private String encodeImageBase64(String path){
        String encodedImage="";
        try
        {
            Bitmap bm = BitmapFactory.decodeFile(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            encodedImage= Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            Log.d("encoded_image", encodedImage);
        }
        catch (Exception e)
        {

        }
        return encodedImage;
    }


    private void callImageApi(String encodedImage) {

        final ProgressDialog progressDialog=new ProgressDialog(EnrollActivity.this);
        progressDialog.setMessage("please wait");
        progressDialog.show();

        ImageRequest imageRequest=new ImageRequest();
        imageRequest.setImage(encodedImage);
        imageRequest.setMatric_no("71103");
        imageRequest.setNevsid("1006");

        ImageApi api_interface= APIClient.getClient().create(ImageApi.class);

        Observable<ImgResponse> call=api_interface.uploadImage("application/json",imageRequest);

        Threading.async(call, new Consumer<ImgResponse>() {
            @Override
            public void accept(ImgResponse response) throws Exception {
                person.isSyncWithBackend = true;
                //GlobalData.getInstance().SaveUsersList();
                //GlobalData.getInstance().SaveUserByID(person, jpgbytes);
                //Log.d("", "");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("", "");
            }
        });
    }

    private boolean CheckInputData(int type) {
        int len = mid.getText().toString().length();
        if (len <= 0) {
            Toast.makeText(EnrollActivity.this, "Please enter the numbers", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        len = dob.getText().toString().length();
        if (len <= 0) {
            Toast.makeText(EnrollActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return false;
        }
		/*
		if(mRefSize1<=0){
			Toast.makeText(EnrollActivity.this, "Please Input Template One", Toast.LENGTH_SHORT)
			.show();
			return false;
		}
		if(mRefSize2<=0){
			Toast.makeText(EnrollActivity.this, "Please Input Template Two", Toast.LENGTH_SHORT)
			.show();
			return false;
		}
		if(!iscap){
			Toast.makeText(EnrollActivity.this, "Please Take Photo", Toast.LENGTH_SHORT).show();
			return false;
		}
		*/
        if (type == 1) {
            if (GlobalData.getInstance().IsHaveUserItem(mid.getText().toString())) {
                Toast.makeText(EnrollActivity.this, "ID Exists", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void FPDialog(int i) {
        iFinger = i;
        AlertDialog.Builder builder = new Builder(EnrollActivity.this);
        builder.setTitle("Registration fingerprint");
        final LayoutInflater inflater = LayoutInflater.from(EnrollActivity.this);
        View vl = inflater.inflate(R.layout.dialog_enrolfinger, null);
        fpImage = (ImageView) vl.findViewById(R.id.imageView1);
        tvFpStatus = (TextView) vl.findViewById(R.id.textview1);
        builder.setView(vl);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //SerialPortManager.getInstance().closeSerialPort();
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //SerialPortManager.getInstance().closeSerialPort();
                dialog.dismiss();
            }
        });

        fpDialog = builder.create();
        fpDialog.setCanceledOnTouchOutside(false);
        fpDialog.show();

        fpm.SetContextHandler(this, mHandler);
        fpm.ResumeRegister();
        fpm.OpenDevice();
    }

    public void InitReadCard() {
        if (ActivityList.getInstance().IsUseNFC) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter == null) {
                Toast.makeText(this, "Device does not support NFC!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(this, "Enable the NFC function in the system settings!", Toast
                        .LENGTH_SHORT).show();
                finish();
                return;
            }

            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            mFilters = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                    new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED), new IntentFilter
                    (NfcAdapter.ACTION_TAG_DISCOVERED)};
        }
    }
    //NFC

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        byte[] sn = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        String cardstr =/*Integer.toString(count)+":"+*/
                Integer.toHexString(sn[0] & 0xFF).toUpperCase() + Integer.toHexString(sn[1] &
                        0xFF).toUpperCase() + Integer.toHexString(sn[2] & 0xFF).toUpperCase() +
                        Integer.toHexString(sn[3] & 0xFF).toUpperCase();

        for (int i = 0; i < GlobalData.getInstance().userList.size(); i++) {
            if (GlobalData.getInstance().userList.get(i).cardsn.indexOf(cardstr) >= 0) {
                Toast.makeText(EnrollActivity.this, "Failed,Duplicate registration!", Toast
                        .LENGTH_SHORT).show();
                return;
            }
        }
        madien_name.setText(cardstr);
        CardSN = cardstr;
        //soundPool.play(soundIda, 1.0f, 0.5f, 1, 0, 1.0f);
    }

    @Override
    public void onPause() {
        if (ActivityList.getInstance().IsUseNFC) {
            if (nfcAdapter != null) nfcAdapter.disableForegroundDispatch(this);
        }
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            if (!bCapture) {
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityList.getInstance().IsUseNFC) {
            if (nfcAdapter != null)
                nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, null);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fpm.PauseUnRegister();
        fpm.CloseDevice();
    }

    private void uploadImgToServer(String filePath) {
        Retrofit retrofit = APIClient.getClient(); //NetworkClient.getRetrofitClient(this);
        File file = new File(filePath); //Create a file object using file path

        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody);


//        Observable<ImgResponse> call = APIClient.getService().uploadImage(part,person.matric_no,AppPrefs.get().getNevsid(),description);
//        Threading.async(call, new Consumer<ImgResponse>() {
//            @Override
//            public void accept(ImgResponse imgResponse ) throws Exception {
//                //person.isSyncWithBackend = true;
//                //GlobalData.getInstance().SaveUsersList();
//                //GlobalData.getInstance().SaveUserByID(person, jpgbytes);
//                Log.d("", imgResponse.msg);
//            }
//        }, new Consumer<Throwable>() {
//            @Override
//            public void accept(Throwable throwable) throws Exception {
//                Log.d("",throwable.toString());
//            }
//        });


//        Threading.async(APIClient.getService().uploadImage(part,person.matric_no,AppPrefs.get().getNevsid(),description).doOnNext(new Consumer<List<ImgResponse>>() {
//            @Override
//            public void accept(List<ImgResponse> imgResponses) throws Exception {
//                Toast.makeText(EnrollActivity.this, imgResponses.size(), Toast.LENGTH_SHORT).show();
//
//            }
//
//
//        }));
//        ImageApi upload=retrofit.create(ImageApi.class);
//        Call call = upload.uploadImage(part,person.matric_no,AppPrefs.get().getNevsid(),description);
//        call.enqueue(new Callback() {
//             @Override
//             public void onResponse(Call call, retrofit2.Response response) {
//
//             }
//
//             @Override
//             public void onFailure(Call call, Throwable t) {
//
//             }
//         });

//        Bitmap bm = BitmapFactory.decodeFile(filePath);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
//        byte[] b = baos.toByteArray();
//        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);


    }
}
