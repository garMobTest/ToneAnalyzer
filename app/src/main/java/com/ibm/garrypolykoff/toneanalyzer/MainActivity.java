package com.ibm.garrypolykoff.toneanalyzer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;


import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private EditText messageToAnalyze;
    private TextView analsisTextView;
    private Button analysisButton;
    private Map valuesMap = new HashMap();


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        messageToAnalyze = (EditText) findViewById(R.id.messageEditText);


        analysisButton = (Button) findViewById(R.id.sendAnalysisButton);
        analsisTextView = (TextView) findViewById(R.id.analysisTextView);

        analysisButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



                    InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);



                if (!messageToAnalyze.getText().toString().isEmpty()) {

                    String message = messageToAnalyze.getText().toString();
                    new ToneAnalyzerCall().execute(message, null, null);

                }


            }

        });


    }


    private class ToneAnalyzerCall extends AsyncTask<String, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        ToneAnalysis tone = null;
        boolean success = false;


        @Override
        protected Void doInBackground(String... params) {

            ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
            service.setUsernameAndPassword("260ba98a-1ec2-405b-8795-55b861930b54", "6Lth0ZwxtJE6");
            //  service.setEndPoint("https://gateway.watsonplatform.net/tone-analyzer/api");
            // EditText text = (EditText) findViewById(R.id.analyzeEditText);
            Log.e("Param", params[0]);
            String value = params[0];

            // Call the service and get the tone
            tone = service.getTone(value, null).execute();
            success = true;


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            if (success) {
                pdLoading.dismiss();
                valuesMap.clear();
                for (ToneCategory tc : tone.getDocumentTone().getTones())
                    for (ToneScore ts : tc.getTones()) {

                        valuesMap.put(ts.getName(), ts.getScore());

                    }

                String toneScore = "";

                Iterator entries = valuesMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry thisEntry = (Map.Entry) entries.next();
                    Log.e("Entry", (String) thisEntry.getKey());
                    toneScore += thisEntry.getKey() + ": " + thisEntry.getValue() + "\n";

                }


                analsisTextView.setText(toneScore, TextView.BufferType.SPANNABLE);

            }
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class MyFocusChangeListener implements  View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus){

            if(v.getId() == R.id.messageEditText && !hasFocus) {

                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        }
    }


}