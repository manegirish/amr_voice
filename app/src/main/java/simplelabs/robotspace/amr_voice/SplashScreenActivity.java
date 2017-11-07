package simplelabs.robotspace.amr_voice;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.widget.Toast;

public class SplashScreenActivity extends Activity {
    final SplashScreenActivity SplashScreen = this;
    private Thread SplashThread;
    BluetoothAdapter bluetoothAdapter;

    class C00361 extends Thread {
        C00361() {
        }

        public void run() {
            while (!SplashScreenActivity.this.bluetoothAdapter.isEnabled()) {
                try {
                    synchronized (this) {
                        wait(5000);
                    }
                } catch (InterruptedException e) {
                } finally {
                    SplashScreenActivity.this.finish();
                    Intent i = new Intent();
                    i.setClass(SplashScreenActivity.this.SplashScreen, VoiceControlActivity.class);
                    SplashScreenActivity.this.startActivity(i);
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
        if (!this.bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "ENABLING BLUETOOTH !", Toast.LENGTH_LONG).show();
            this.bluetoothAdapter.enable();
        }
        this.SplashThread = new C00361();
        this.SplashThread.start();
    }

    public void onBackPressed() {
        this.bluetoothAdapter.disable();
        Toast.makeText(this, "DISABLING BLUETOOTH !", Toast.LENGTH_LONG).show();
        Process.killProcess(Process.myPid());
        finish();
    }
}
