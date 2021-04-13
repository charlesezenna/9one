package com.fgtit.finger;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fgtit.app.ActivityList;
import com.fgtit.data.GlobalData;
import com.fgtit.data.ImageSimpleAdapter;
import com.fgtit.data.RecordItem;
import com.fgtit.data.UserItem;
import com.fgtit.device.Constants;
import com.fgtit.device.FPModule;
import com.fgtit.fpcore.FPMatch;
import com.fgtit.utils.ExtApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SignOnActivity extends AppCompatActivity {

    private FPModule fpm;
    private byte bmpdata[] = new byte[Constants.RESBMP_SIZE];
    private int bmpsize = 0;
    private byte matdata[] = new byte[Constants.TEMPLATESIZE * 2];
    private int matsize = 0;
    private ListView listView1;
    private ArrayList<HashMap<String, Object>> mData1;
    private SimpleAdapter adapter1;

    private TextView tvFpStatus;
    private ImageView fpImage;

    //NFC
    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_local);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView1 = (ListView) findViewById(R.id.listView1);
        mData1 = new ArrayList<HashMap<String, Object>>();
        adapter1 = new ImageSimpleAdapter(this, mData1, R.layout.listview_signitem, new
                String[]{"title", "info", "dts", "img"}, new int[]{R.id.title, R.id.info, R.id
                .dts, R.id.img});
        listView1.setAdapter(adapter1);

        tvFpStatus = (TextView) findViewById(R.id.textView1);
        tvFpStatus.setText("");
        fpImage = (ImageView) findViewById(R.id.imageView1);
        fpImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        //Card
        InitReadCard();

        //Fingerprint
        fpm = new FPModule();
        fpm.SetTimeOut(Constants.TIMEOUT_LONG);
        fpm.SetLastCheckLift(true);
        fpm.InitMatch();
        fpm.SetContextHandler(SignOnActivity.this, mHandler);
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
                            fpm.GenerateTemplate(1);
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
                    tvFpStatus.setText("Please place finger");
                    break;
                case Constants.FPM_LIFT:
                    tvFpStatus.setText("Please lift finger");
                    break;
                case Constants.FPM_GENCHAR: {
                    if (msg.arg1 == 1) {
                        matsize = fpm.GetTemplateByGen(matdata);
                        for (int i = 0; i < GlobalData.getInstance().userList.size(); i++) {
                            byte[] tmp = new byte[256];
                            if (GlobalData.getInstance().userList.get(i).bytes1 != null) {
                                System.arraycopy(GlobalData.getInstance().userList.get(i).bytes1,
                                        0, tmp, 0, 256);
                                if (fpm.MatchTemplate(tmp, tmp.length, matdata, matsize, 80)) {
                                    AddPersonItem(GlobalData.getInstance().userList.get(i));
                                    tvFpStatus.setText(getString(R.string.txt_fpmatchok));
                                    break;
                                }
                                System.arraycopy(GlobalData.getInstance().userList.get(i).bytes1,
                                        256, tmp, 0, 256);
                                if (fpm.MatchTemplate(tmp, tmp.length, matdata, matsize, 80)) {
                                    AddPersonItem(GlobalData.getInstance().userList.get(i));
                                    tvFpStatus.setText(getString(R.string.txt_fpmatchok));
                                    break;
                                }
                            }
                            if (GlobalData.getInstance().userList.get(i).bytes2 != null) {
                                System.arraycopy(GlobalData.getInstance().userList.get(i).bytes2,
                                        0, tmp, 0, 256);
                                if (fpm.MatchTemplate(tmp, tmp.length, matdata, matsize, 80)) {
                                    AddPersonItem(GlobalData.getInstance().userList.get(i));
                                    tvFpStatus.setText(getString(R.string.txt_fpmatchok));
                                    break;
                                }
                                System.arraycopy(GlobalData.getInstance().userList.get(i).bytes2,
                                        256, tmp, 0, 256);
                                if (fpm.MatchTemplate(tmp, tmp.length, matdata, matsize, 80)) {
                                    AddPersonItem(GlobalData.getInstance().userList.get(i));
                                    tvFpStatus.setText(getString(R.string.txt_fpmatchok));
                                    break;
                                }
                            }
                        }
                        try {
                            Thread.sleep(200);
                            fpm.GenerateTemplate(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        tvFpStatus.setText("Generate Template Fail");
                        try {
                            Thread.sleep(200);
                            fpm.GenerateTemplate(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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

    private void AddPersonItem(UserItem person) {
        RecordItem rs = new RecordItem();
        rs.id = person.id;
        rs.name = person.name;
        rs.datetime = ExtApi.getStringDate();
        if (GlobalData.getInstance().glocal) {
            rs.lat = String.valueOf(GlobalData.getInstance().glat);
            rs.lng = String.valueOf(GlobalData.getInstance().glng);
        }
        rs.type = "1";
        rs.worktype = person.worktype;
        rs.linetype = person.linetype;
        rs.depttype = person.depttype;
        GlobalData.getInstance().AppendRecord(rs);
        GlobalData.getInstance().recordList.add(rs);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("title", rs.name);
        map.put("info", rs.id);
        map.put("dts", rs.datetime);
        if (person.photo.length() > 1000) {
            map.put("img", ExtApi.Bytes2Bimap(ExtApi.Base64ToBytes(person.photo)));
        } else {
            byte[] photo = GlobalData.getInstance().LoadPhotoByID(person.id);
            if (photo != null) map.put("img", ExtApi.Bytes2Bimap(photo));
            else map.put("img", ExtApi.LoadBitmap(getResources(), R.drawable.guest));
        }
        mData1.add(map);
        adapter1.notifyDataSetChanged();

        String smsText =  person.name + " sign in at"+ ExtApi.getStringDate1() + ".";
        Log.d("SignOnActivity", smsText);
        if (person.phone.length() > 0) {
            SmsManager.getDefault().sendTextMessage(person.phone, null, smsText, null, null);
        }
        ScrollListViewToBottom();
    }

    private void ScrollListViewToBottom() {
        listView1.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView1.setSelection(adapter1.getCount() - 1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_off, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Toast.makeText(SignOnActivity.this, "Cancel...", Toast.LENGTH_SHORT).show();
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();
                return true;
            case R.id.action_screen:
                mData1.clear();
                adapter1.notifyDataSetChanged();
                break;
            case R.id.action_sign:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Toast.makeText(SignOnActivity.this, "Cancel...", Toast.LENGTH_SHORT).show();
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finish();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //Card
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
        String cardsn = Integer.toHexString(sn[0] & 0xFF).toUpperCase() + Integer.toHexString
                (sn[1] & 0xFF).toUpperCase() + Integer.toHexString(sn[2] & 0xFF).toUpperCase() +
                Integer.toHexString(sn[3] & 0xFF).toUpperCase();

        for (int i = 0; i < GlobalData.getInstance().userList.size(); i++) {
            if (GlobalData.getInstance().userList.get(i).cardsn.indexOf(cardsn) >= 0) {
                AddPersonItem(GlobalData.getInstance().userList.get(i));
            }
        }
    }

    @Override
    public void onPause() {
        if (ActivityList.getInstance().IsUseNFC) {
            if (nfcAdapter != null) nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        fpm.ResumeRegister();
        fpm.OpenDevice();
        if (ActivityList.getInstance().IsUseNFC) {
            if (nfcAdapter != null)
                nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, null);
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        fpm.PauseUnRegister();
        fpm.CloseDevice();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        fpm.Cancle();
        super.onDestroy();
    }
}
