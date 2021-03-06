package com.example.rtponestream;

import java.net.InetAddress;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;

public class RTPMainActivity extends ActionBarActivity {

	private String TAG = "RTP_SAURABH";
	private boolean isRunning = false;

	private static byte[] ip1 = { (byte) 172,(byte)16,(byte)7,(byte)107 };
	private static byte[] ip2 = { (byte) 172,(byte)16,(byte)7,(byte)123 };
	private static AudioManager manager;
	private static AudioGroup audioGroup = new AudioGroup();
	private static AudioStream Stream;
	private Thread mThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rtpmain);

		((Button)findViewById(R.id.StartButton)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.StopButton)).setOnClickListener(btnClick);

		enableButton(R.id.StartButton,true);
		enableButton(R.id.StopButton,false);

		if (null == (manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE)))
			Log.d(TAG, "======== getSystemService(Context.AUDIO_SERVICE) : FAILED\n\n ==========");

		manager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		audioGroup.setMode(AudioGroup.MODE_NORMAL);
		manager.setSpeakerphoneOn(true);
	}

	private void run_threads(final boolean flag)
	{
		Log.d(TAG, "========== Thread Started ===========");
		mThread = new Thread(new Runnable() {
			public void run() {
				doRTP(flag);
			}
		});
		mThread.start();
		return;
	}

	private void doRTP (final boolean flag) 
	{
		try {
			Stream = new AudioStream(InetAddress.getByAddress(ip1));
			Log.d(TAG, "\n\n======== Local Port :" + Stream.getLocalPort() + "==========");
			Log.d(TAG, "======== Local Address :" + Stream.getLocalAddress() + "==========\n\n\n");
			Stream.setMode(RtpStream.MODE_NORMAL);
			Stream.setCodec(AudioCodec.PCMU);
			Stream.associate(InetAddress.getByAddress(ip2), 38362 );
			Log.d(TAG, "\n\n======== Remote Port :" + Stream.getRemotePort() + "==========");
			Log.d(TAG, "======== Remote Address :" + Stream.getRemoteAddress() + "==========\n\n\n");
		}
		catch (Exception e) {
			Log.e("sendRTP -----------", e.toString());
			e.printStackTrace();
		}
		if (isRunning == false)
			Stream.join(null);
		else {
			Stream.join(audioGroup);
			Log.d(TAG, "\n\n After Join\n======== Remote Port :" + Stream.getRemotePort() + "==========");
			Log.d(TAG, "======== Remote Address :" + Stream.getRemoteAddress() + "==========\n\n\n");
		}
		return;
	}

	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.StartButton:
			{
				Log.d(TAG, "======== Start Button Pressed ==========");
				isRunning = true;
				Log.d(TAG, "========== isRunning = true =============");
				run_threads(isRunning);
				enableButton(R.id.StartButton,false);
				enableButton(R.id.StopButton,true);
				break;
			}
			case R.id.StopButton:
			{
				Log.d(TAG, "======== Stop Button Pressed ==========");
				isRunning = false;
				Log.d(TAG, "========== isRunning = false =============");
				run_threads(isRunning);
				enableButton(R.id.StopButton,false);
				enableButton(R.id.StartButton,true);
				break;
			}
			}
		}
	};

	/* Function to Enable/Disable Buttons */
	private void enableButton(int id,boolean isEnable){
		((Button)findViewById(id)).setEnabled(isEnable);
	}

	/*		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rtpmain, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_rtpmain,
					container, false);
			return rootView;
		}
	}

}
