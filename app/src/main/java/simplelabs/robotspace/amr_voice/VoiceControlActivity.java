package simplelabs.robotspace.amr_voice;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class VoiceControlActivity extends Activity {
    private static final boolean f3D = false;
    public static final String DEVICE_NAME = "device_name";
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_WRITE = 3;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final String TAG = "BluetoothBT";
    public static final String TOAST = "toast";
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private BtService mBTService = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private String mConnectedDeviceName = null;
    private final Handler mHandler = new C00382();
    private StringBuffer mOutStringBuffer;

    public String msg;
    public String voiceData;

    class C00382 extends Handler {
        C00382() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    switch (msg.arg1) {
                        case 2:
                        case 3:
                            return;
                        default:
                            return;
                    }
                case 2:
                    //String readMessage = new String(msg.obj, 0, msg.arg1);
                    new String((byte[]) msg.obj, 0, msg.arg1);
                    return;
                case 3:
                    new String((byte[]) msg.obj);
                    // String writeMessage = new String(msg.obj);
                    return;
                case 4:
                    VoiceControlActivity.this.mConnectedDeviceName = msg.getData().getString(VoiceControlActivity.DEVICE_NAME);
                    Toast.makeText(VoiceControlActivity.this.getApplicationContext(), "Connected to " + VoiceControlActivity.this.mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    return;
                case 5:
                    Toast.makeText(VoiceControlActivity.this.getApplicationContext(), msg.getData().getString(VoiceControlActivity.TOAST), Toast.LENGTH_SHORT).show();
                    return;
                default:
                    return;
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void onStart() {
        super.onStart();
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        } else if (this.mBTService == null) {
            setupBT();
        }
        checkVoiceRecognition();
    }

    public synchronized void onResume() {
        super.onResume();
        if (this.mBTService != null && this.mBTService.getState() == 0) {
            this.mBTService.start();
        }
    }

    private void setupBT() {
        Log.d(TAG, "setupBT()");
        this.mBTService = new BtService(this, this.mHandler);
        this.mOutStringBuffer = new StringBuffer("");
    }

    public synchronized void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        this.mBluetoothAdapter.disable();
        if (this.mBTService != null) {
            this.mBTService.stop();
        }
    }

    private void sendMessage(String message) {
        if (this.mBTService.getState() == 3 && message.length() > 0) {
            this.mBTService.write(message.getBytes());
            this.mOutStringBuffer.setLength(0);
        }
    }

    public void senda(View view) {
        sendMessage("R");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == -1) {
                    connectDevice(data, true);
                    return;
                }
                return;
            case 2:
                if (resultCode == -1) {
                    connectDevice(data, f3D);
                    return;
                }
                return;
            case 3:
                if (resultCode != -1) {
                    Log.d(TAG, "BT not enabled");
                    finish();
                    break;
                }
                setupBT();
                break;
            case VOICE_RECOGNITION_REQUEST_CODE /*1001*/:
                break;
            default:
                return;
        }
        if (resultCode == -1) {
            this.voiceData = (String) data.getStringArrayListExtra("android.speech.extra.RESULTS").get(0);
            showToastMessage(this.voiceData);
            this.msg = String.format("*%s#", new Object[]{this.voiceData});
            if (this.mConnectedDeviceName != null) {
                sendMessage(this.msg);
            }
        } else if (resultCode == 5) {
            showToastMessage("Audio Error");
        } else if (resultCode == 2) {
            showToastMessage("Client Error");
        } else if (resultCode == 4) {
            showToastMessage("Network Error");
        } else if (resultCode == 1) {
            showToastMessage("No Match");
        } else if (resultCode == 3) {
            showToastMessage("Server Error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectDevice(Intent data, boolean secure) {
        this.mBTService.connect(this.mBluetoothAdapter.getRemoteDevice(data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)), secure);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_splash_screen, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Scan:
                startActivityForResult(new Intent(this, DeviceListActivity.class), 1);
                break;
            case R.id.BluetoothSettings:
                Intent intentBluetooth = new Intent();
                intentBluetooth.setAction("android.settings.BLUETOOTH_SETTINGS");
                startActivity(intentBluetooth);
                break;
            case R.id.app_info:
                startActivityForResult(new Intent(this, AppInfoActivity.class), 0);
                break;
        }
        return true;
    }

    public void checkVoiceRecognition() {
        if (getPackageManager().queryIntentActivities(new Intent("android.speech.action.RECOGNIZE_SPEECH"), 0).size() == 0) {
            Toast.makeText(this, "Voice recognizer not present", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Please Install Google Voice", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void speak(View view) {
       // if (this.mConnectedDeviceName != null) {
            Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
            intent.putExtra("calling_package", getClass().getPackage().getName());
            intent.putExtra("android.speech.extra.PROMPT", "Robot Voice Control");
            intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "web_search");
            intent.putExtra("android.speech.extra.MAX_RESULTS", 1);
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
            return;
      //  }
       // showToastMessage("First Connect a Robot!");
    }

    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
