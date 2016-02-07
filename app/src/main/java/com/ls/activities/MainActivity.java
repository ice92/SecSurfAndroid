package com.ls.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.ftunram.secsurf.core.pornFiltering;
import com.ftunram.secsurf.core.svmPornFiltering;
import com.ftunram.secsurf.toolkit.Asset2file;
import com.ftunram.secsurf.toolkit.FileEditor;
import com.ftunram.secsurf.toolkit.FileRWan;
import com.ftunram.secsurf.toolkit.ImageCounter;
import com.ls.directoryselector.DirectoryDialog;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class MainActivity extends ListActivity implements DirectoryDialog.Listener {

	private AppSettings settings;
	ArrayList<String> listItems=new ArrayList<String>();
	ArrayAdapter<String> adapter;


	private TextView txtDirLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		settings = AppSettings.getSettings(this);

		initViews();
		fillViews();
		adapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				listItems);
		setListAdapter(adapter);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			SettingsActivity.startThisActivity(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(sharedPrefsChangeListener);
	}

	@Override
	public void onResume() {
		super.onResume();
		settings.load();
		fillViews();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(sharedPrefsChangeListener);
	}

	//region DirectoryDialog.Listener interface
	@Override
	public void onDirectorySelected(File dir) {
		settings.setStorePath(dir.getPath());
		settings.saveDeferred();
		fillViews();
	}

	@Override
	public void onCancelled() {
	}
	//endregion

	private final SharedPreferences.OnSharedPreferenceChangeListener sharedPrefsChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			settings.load();
			fillViews();
		}
	};

	private final View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btn_change_dir) {
				DialogFragment dialog = DirectoryDialog.newInstance(settings.getStorePath());
				dialog.show(getFragmentManager(), "directoryDialog");
			}
		}
	};

	private void initViews() {
		txtDirLocation = (TextView) findViewById(R.id.txt_dir_location);
		Button btnChangeDir = (Button) findViewById(R.id.btn_change_dir);
		btnChangeDir.setOnClickListener(clickListener);
		scanres=new ArrayList<>();
	}

	ImageCounter ic=new ImageCounter();

	pornFiltering scanner=new pornFiltering();

    public void clrClick(View v){
        adapter.clear();
		new pornFiltering().scan2("blabla", this);

		scanres.clear();
    }
	public void delClick(View v){

		for(int i=0;i<scanres.size();i++)
		if(scanres.get(i)){
			boolean test=FileEditor.delete(settings.getStorePath()+"/"+files.get(i));
			Log.i("test make dir", "" + settings.getStorePath()+"/"+files.get(i));
		}
	}
	ArrayList<Boolean> scanres;
	ArrayList<String> files;
	public void scanClick(View v) {

        FileRWan write=new FileRWan();
		Intent xx=new Intent(this, LoadingScreenActivity.class);
		startActivity(xx);
		files=ic.getNumFiles(settings.getStorePath());
		Button del=(Button)findViewById(R.id.button2);
		del.setEnabled(true);
		Button clr=(Button)findViewById(R.id.button3);
		clr.setEnabled(true);
        File x=new File(settings.getStorePath()+"/protected.ini");
        try{
            x.createNewFile();
            Log.i("test make dir","sukses");
        }catch (Exception e){
            Log.i("test make dir",e.getMessage().toString());
        }

		Boolean temp=false;
        String out="";
		for(int i=0;i<files.size();i++){
			Log.i("test make dir",settings.getStorePath()+"/"+files.get(i));
			temp=myEngine.matchSVM(settings.getStorePath()+"/"+files.get(i),null);//scanner.scan(settings.getStorePath()+"/"+files.get(i));
			if(temp)
			{
				listItems.add(files.get(i) + "\nResult : " + "Negative Content!");
				scanres.add(true);
			}
			else{
				listItems.add(files.get(i) + "\nResult : " + "Good Content");
				scanres.add(false);
			}
            out+=""+temp;
		}
        Log.i("isi ini", out);
        write.write(this, out, x);
		//adapter.notifyDataSetChanged();

	}

	public CascadeClassifier faceDetector;
	Activity what=this;
	private static final String TAG = "OCVSample::Activity";
	svmPornFiltering myEngine;
	protected BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
				case LoaderCallbackInterface.SUCCESS: {
					initxml();
					String fileName="/secsurf/mySVM.xml";
					String root = Environment.getExternalStorageDirectory().toString();
					File xm= new File(root, fileName);

					myEngine=new svmPornFiltering();
					myEngine.initSVM(xm.getAbsolutePath());
					Toast.makeText(getApplicationContext(), "SVM  data loaded successfully.... ", Toast.LENGTH_LONG).show();
				}
				break;
				default: {
					super.onManagerConnected(status);
				}
				break;
			}
		}
	};
	private void initxml(){
		String dir = Environment.getExternalStorageDirectory()+File.separator+"secsurf";
		File f = new File(dir,"mySVM.xml");
		boolean init=f.exists();
		if(!init){
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				//handle case of no SDCARD present
			} else {
				AssetManager am= what.getAssets();
				try {
					File xm=new Asset2file().createFileFromInputStream(am.open("mySVM.xml"));
				} catch (IOException e) {
					Log.i("test make dir", "gagal copi file");
				}

			}
		}
	}



	private void fillViews() {
		txtDirLocation.setText(settings.getStorePath());
	}
}
