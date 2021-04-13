package com.fgtit.finger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fgtit.network.ApiHelper;
import com.fgtit.network.Response;
import com.fgtit.utils.ExtApi;
import com.fgtit.data.GlobalData;
import com.fgtit.data.UserItem;
import com.fgtit.utils.Threading;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class EmployeeActivity extends AppCompatActivity {

	private ListView listView1,listView2;
	private List<Map<String, Object>> mData1,mData2;
	
	private ImageView photoImage;
	private byte[] jpgbytes = null;
	private int 	pos=0;
	private UserItem person=null;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_employee);

		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        pos = bundle.getInt("pos");        
		//int pos=this.getIntent().getIntExtra("pos",0);
        if(pos<GlobalData.getInstance().userList.size())
        	person=GlobalData.getInstance().userList.get(pos);
		
        photoImage=(ImageView)findViewById(R.id.imageView1);
        if(person.photo.length()>1000){
        	photoImage.setImageBitmap(Bytes2Bimap(ExtApi.Base64ToBytes(person.photo)));
        }else{
        	byte[] photo=GlobalData.getInstance().LoadPhotoByID(person.id);
        	if(photo!=null)
        		photoImage.setImageBitmap(Bytes2Bimap(photo));
        	else
        		photoImage.setImageBitmap(ExtApi.LoadBitmap(getResources(),R.drawable.guest));
        }
        
		listView1=(ListView) findViewById(R.id.listView1);	
		SimpleAdapter adapter1 = new SimpleAdapter(this,getData1(),R.layout.listview_empitem,
				new String[]{"title","info"},
				new int[]{R.id.title,R.id.info});
		listView1.setAdapter(adapter1);
		
		listView2=(ListView) findViewById(R.id.listView2);	
		SimpleAdapter adapter2 = new SimpleAdapter(this,getData2(),R.layout.listview_empitem,
				new String[]{"title","info"},
				new int[]{R.id.title,R.id.info});
		listView2.setAdapter(adapter2);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.employee, menu);
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
                this.finish();
                return true;
            case R.id.action_delete:
                if (pos >= 0) {
                    GlobalData.getInstance().DeleteUserByID(person.id);
                    GlobalData.getInstance().userList.remove(pos);
                    //GlobalData.getInstance().SaveUsersList();
                    Intent resultIntent = new Intent();
                    //resultIntent.putExtra("reload", 1);
                    setResult(0, resultIntent);
                    this.finish();
                }
                //this.finish();
                return true;
            case R.id.action_update:
                //Intent intent = new Intent(EmployeesActivity.this, EnrollActivity.class);
                //startActivityForResult(intent, 0);
                return true;
			case R.id.action_sync:
				Observable<Response> call = ApiHelper.getEnrollObservable(person);
				//person.photo;
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
				Toast.makeText(EmployeeActivity.this, "Record has been successfully queued", Toast.LENGTH_SHORT).show();
        }



		return super.onOptionsItemSelected(item);
	}

	private List<Map<String, Object>> getData1() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		
		if(person!=null){			
			map = new HashMap<String, Object>();
			map.put("title", getString(R.string.txt_username)+":");
			map.put("info", person.name);
			list.add(map);
		
			map = new HashMap<String, Object>();
			map.put("title", getString(R.string.txt_userid)+":");
			map.put("info", person.id);
			list.add(map);
		}
				
		mData1=list;		
		return list;
	}
	
	private List<Map<String, Object>> getData2() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		if(person!=null){
			
			Map<String, Object> map = new HashMap<String, Object>();
			/*
			map = new HashMap<String, Object>();
			map.put("title", getString(R.string.txt_statu)+":");
			map.put("info", "Normal");
			list.add(map);
			*/
			
			map = new HashMap<String, Object>();
			map.put("title", getString(R.string.txt_usertype)+":");
			switch(person.type){
			case 0:	map.put("info", getString(R.string.txt_selact));	break;
			case 1:	map.put("info", getString(R.string.txt_selict));	break;
			}
			list.add(map);
					
			//map = new HashMap<String, Object>();
			//map.put("title", getString(R.string.txt_gender)+":");
			//switch(person.gender){
			//case 0:	map.put("info", getString(R.string.txt_male));	break;
			//case 1:	map.put("info", getString(R.string.txt_female));	break;
			//}
			//list.add(map);

			map = new HashMap<String, Object>();
			map.put("title", "Sex:");
			map.put("info", person.sex);
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("title", getString(R.string.txt_phone)+":");
			map.put("info", person.phone);
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("title", "Matric #:");
			map.put("info", person.matric_no);
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("title", "Email Address:");
			map.put("info", person.email);
			list.add(map);

			//map = new HashMap<String, Object>();
			//map.put("title", getString(R.string.txt_remark)+":");
			//map.put("info", "");
			//list.add(map);

			map = new HashMap<String, Object>();
			map.put("title", "Department:");
			map.put("info", person.department);
			list.add(map);
		}
		mData2=list;		
		return list;
	}
	
	private Bitmap Bytes2Bimap(byte[] b){  
        if(b.length!=0){  
            return BitmapFactory.decodeByteArray(b, 0, b.length);  
        }  
        else {  
            return null;  
        }  
	}
}
