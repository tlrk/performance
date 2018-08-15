package com.example.momo.performance;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.performance_android.appluanch.AppLaunchMonitor;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppLaunchMonitor.getInstance().markLifeCycleMethodExecute(AppLaunchMonitor.APP_LAUNCH_START_MAIN_ACT_CREATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, DemoFragment.newInstance())
                .commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTipDialog();
            }
        });
    }

    private void showTipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tip");
        builder.setMessage(getResources().getString(R.string.hello_world));
        builder.setNegativeButton("ok", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLaunchMonitor.getInstance().markLifeCycleMethodExecute(AppLaunchMonitor.APP_LAUNCH_VISIBLE_TO_USER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
