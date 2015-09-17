package co.edu.udea.cmovil.gr09.yamba;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thenewcircle.yamba.client.YambaClient;

public class StatusActivity extends Activity implements View.OnClickListener {
    private static final String TAG = StatusActivity.class.getSimpleName();
    private EditText mTextStatus;
    private Button mButtonTweet;
    private TextView mTextCount;
    private int mDefaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mButtonTweet = (Button) findViewById(R.id.status_button_tweet);
        mButtonTweet.setOnClickListener(this);
        mButtonTweet.setClickable(false);
        mTextStatus = (EditText) findViewById(R.id.status_text);
        mTextCount = (TextView) findViewById(R.id.status_text_count);

        mTextCount.setText(Integer.toString(140));
        mDefaultColor = mTextCount.getTextColors().getDefaultColor();
        mTextStatus.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (mTextStatus.getText().toString().equals("")) {
                    mButtonTweet.setClickable(false);
                } else {
                    mButtonTweet.setClickable(true);
                }
                int count = 140 - s.length();
                mTextCount.setText(Integer.toString(count));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (mTextStatus.getText().toString().equals("")) {
                    mButtonTweet.setClickable(false);
                } else {
                    mButtonTweet.setClickable(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (mTextStatus.getText().toString().equals("")) {
                    mButtonTweet.setClickable(false);
                } else {
                    mButtonTweet.setClickable(true);
                }
            }

        });

        Log.d(TAG, "onCreated");
    }

    @Override
    public void onClick(View v) {
        String status = mTextStatus.getText().toString();
        PostTask postTask = new PostTask();
        mTextStatus.setText("");
        mButtonTweet.setClickable(false);
        mTextStatus.clearFocus();
        postTask.execute(status);
        Log.d(TAG, "onClicked");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
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

    class PostTask extends AsyncTask<String, Void, String> {

        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(StatusActivity.this, "Posting",
                    "Please wait...");
            progress.setCancelable(true);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                YambaClient cloud = new YambaClient("student", "password");
                cloud.postStatus(params[0]);

                Log.d(TAG, "Successfully posted to the cloud" + params[0]);
                return "Successfully posted";
            } catch (Exception e) {
                Log.e(TAG, "Failed to post to the cloud", e);
                e.printStackTrace();
                return "Failed to post";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            if (this != null && result != null)
                Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG)
                        .show();
        }
    }
}
