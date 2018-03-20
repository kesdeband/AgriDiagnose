package com.agrinett.agridiagnose.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
//import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
//import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.agrinett.agridiagnose.R;
import com.agrinett.agridiagnose.broadcasts.INetworkListener;
import com.agrinett.agridiagnose.broadcasts.NetworkBroadcastReceiver;
import com.agrinett.agridiagnose.data.IRepository;
import com.agrinett.agridiagnose.data.Repository;
import com.agrinett.agridiagnose.rest.JsonClient;
import com.agrinett.agridiagnose.rest.VolleyRequestQueue;
import com.agrinett.agridiagnose.services.SyncIntentService;

public class CoreActivity extends AppCompatActivity implements INetworkListener {

    private FragmentManager fragmentManager;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private MaterialDialog materialDialog;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Broadcast receiver to capture internet connectivity events
        fragmentManager = getFragmentManager();
        networkBroadcastReceiver = new NetworkBroadcastReceiver(CoreActivity.this);
        handler = new Handler();

        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(this)
                .title("Syncing...")
                .content("Please wait")
                .progress(true, 0)
                .icon(ContextCompat.getDrawable(this, R.drawable.sync))
//                .icon(ResourcesCompat.getDrawable(getResources(), R.drawable.sync, null))
                .progressIndeterminateStyle(true);
        materialDialog = dialogBuilder.build();

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                syncWithMiddleware();
////                materialDialog.show();
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyRequestQueue.getInstance(CoreActivity.this).cancelPendingRequests(JsonClient.REQUEST_TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_core, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sync) {
            IRepository repository = new Repository(CoreActivity.this);
            if(repository.GetNetworkInfo() == null) {
                Snackbar.make(findViewById(R.id.coreLayout), "No internet connection.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
            else {
                syncWithMiddleware();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNetworkStatusChanged(int status) {
//        Log.d("Status", String.valueOf(status));
        setView(status);
    }

    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        //internetFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkBroadcastReceiver, internetFilter);
    }

    private void setView(int status) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
        switch (status) {
            case TYPE_NOT_CONNECTED:
                //toggleSyncBtn(View.INVISIBLE);
                fragment = new NoInternetFragment();
                break;
            case TYPE_NOT_SYNC:
                //toggleSyncBtn(View.VISIBLE);
                fragment = new NotSyncFragment();
                break;
            default:
                //toggleSyncBtn(View.INVISIBLE);
                fragment = new QuestionFragment();
                break;
        }
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, null);
        fragmentTransaction.commit();
    }

    private void syncWithMiddleware() {
        materialDialog.show();
        Intent intent = new Intent(CoreActivity.this, SyncIntentService.class);
        SyncResultReceiver syncResultReceiver = new SyncResultReceiver(null);
        intent.putExtra("result", syncResultReceiver);
        startService(intent);
    }

    private class SyncResultReceiver extends ResultReceiver {

        private SyncResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if(resultCode == 18 && resultData != null) {
                final boolean result = resultData.getBoolean("result");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        materialDialog.dismiss();
                        if(result) {
                            Toast.makeText(CoreActivity.this, "Sync Completed...", Toast.LENGTH_SHORT).show();
                            setView(TYPE_CONNECTED);
                        }
                        else {
                            Toast.makeText(CoreActivity.this, "Sync failed. Please try again later...", Toast.LENGTH_SHORT).show();
                            setView(TYPE_NOT_SYNC);
                        }
                    }
                });
            }
        }
    }
}
