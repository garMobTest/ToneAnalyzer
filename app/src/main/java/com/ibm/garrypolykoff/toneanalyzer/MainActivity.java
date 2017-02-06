package com.ibm.garrypolykoff.toneanalyzer;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.SentenceTone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;


import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private EditText messageToAnalyze;
    private TextView analsisTextView;
    private Button analysisButton;
    private Map valuesMap = new HashMap();

    private final String  LIKELY = "Likely";
    private final  String VERYLIKELY = "Very Likely";
    private final  String UNLIKELY = "Unlikely";
    private GridLayout gridView;

    private final int ColorUnlikely = Color.GRAY;
    private final int ColorLikely = Color.YELLOW;
    private final int ColorVeryLikely = Color.RED;


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
     //   analsisTextView = (TextView) findViewById(R.id.analysisTextView);
        gridView = (GridLayout) findViewById(R.id.gridView);
        gridView.setRowCount(5);
        gridView.setColumnCount(2);


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

          //  tone.addSentencesTone(sentenceTone);

            // Call the service and get the tone
            tone = service.getTone(value,null ).execute();




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

            TextView viewEmotions;
            TextView viewScore;
            gridView.removeAllViews();
            int probColor;

            //this method will be running on UI thread
            if (success) {
                pdLoading.dismiss();
                valuesMap.clear();
                for (ToneCategory tc : tone.getDocumentTone().getTones())

                    if (tc.getId().equals("emotion_tone")) {
                        for (ToneScore ts : tc.getTones()) {


                            valuesMap.put(ts.getName(), ts.getScore());


                        }

                    }

                String toneScore = "";




                Iterator entries = valuesMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry thisEntry = (Map.Entry) entries.next();
                    Log.e("Entry", (String) thisEntry.getKey());

                    String probability;

                    if ((double)thisEntry.getValue() < 0.3) {

                        probability = UNLIKELY;

                        probColor = ColorUnlikely;



                    } else if ((double)thisEntry.getValue() > 0.3 && (double)thisEntry.getValue() < 0.6) {

                        probability = LIKELY;
                        probColor = ColorLikely;




                    } else {

                        probability = VERYLIKELY;
                        probColor = ColorVeryLikely;
                    }

                    viewEmotions = new TextView(getApplicationContext());
                    viewScore = new TextView(getApplicationContext());


                    viewEmotions.setText((CharSequence) thisEntry.getKey() + ": ");
                    viewEmotions.setTextColor(Color.BLACK);
                    viewEmotions.setTextSize(20f);


                    viewScore.setText((CharSequence)probability);
                    viewScore.setTextColor(probColor);
                    viewScore.setTextSize(20f);



                   // toneScore += thisEntry.getKey() + ": " + probability + "\n";



                    gridView.addView(viewEmotions);

                    GridLayout.LayoutParams param =new GridLayout.LayoutParams();

                    param.setGravity(Gravity.LEFT);

                    viewEmotions.setLayoutParams (param);



                    gridView.addView(viewScore);

                    GridLayout.LayoutParams param1 =new GridLayout.LayoutParams();

                    param1.setGravity(Gravity.CENTER);
                    viewScore.setLayoutParams(param1);

                }


              //  analsisTextView.setText(toneScore, TextView.BufferType.SPANNABLE);

            }
        }

    }

    /**
    GridLayout.LayoutParams param =new GridLayout.LayoutParams();
    param.height = LayoutParams.WRAP_CONTENT;
    param.width = LayoutParams.WRAP_CONTENT;
    param.rightMargin = 5;
    param.topMargin = 5;
    param.setGravity(Gravity.CENTER);
    param.columnSpec = GridLayout.spec(c);
    param.rowSpec = GridLayout.spec(r);
    titleText.setLayoutParams (param);

     **/


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