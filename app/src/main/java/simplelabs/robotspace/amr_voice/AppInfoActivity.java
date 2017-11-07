package simplelabs.robotspace.amr_voice;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AppInfoActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void simplelabs(View view) {
        Intent i = new Intent("android.intent.action.VIEW");
        i.setData(Uri.parse("http://www.simplelabs.co.in"));
        startActivity(i);
    }

    public void robotspace(View view) {
        Intent i = new Intent("android.intent.action.VIEW");
        i.setData(Uri.parse("http://www.robotspace.in"));
        startActivity(i);
    }
}
