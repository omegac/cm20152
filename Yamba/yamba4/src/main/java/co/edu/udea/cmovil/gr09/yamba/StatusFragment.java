package co.edu.udea.cmovil.gr09.yamba;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thenewcircle.yamba.client.YambaClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    private static final String TAG = StatusActivity.class.getSimpleName();
    private EditText mTextStatus;
    private Button mButtonTweet;
    private TextView mTextCount;
    private int mDefaultColor;

    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status, container, false);
        mButtonTweet = (Button) v.findViewById(R.id.status_button_tweet);
        mTextStatus = (EditText) v.findViewById(R.id.status_text);
        mTextCount = (TextView) v.findViewById(R.id.status_text_count);
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
                mTextCount.setTextColor(Color.GREEN);
                if (count < 10)
                    mTextCount.setTextColor(Color.RED);
                else
                    mTextCount.setTextColor(mDefaultColor);
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

        mButtonTweet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String status = mTextStatus.getText().toString();
                PostTask postTask = new PostTask();
                postTask.execute(status);
                mTextStatus.clearFocus();
                //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(new View(this).getWindowToken(), 0);
                Log.d(TAG, "onClicked");


            }
        });

        Log.d(TAG, "onCreated");
        return v;
    }

    class PostTask extends AsyncTask<String, Void, String> {

        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getActivity(), "Posting",
                    "Please wait...");
            progress.setCancelable(true);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String username = prefs.getString("username", "");
                String password = prefs.getString("password", "");

                // Check that username and password are not empty
                // If empty, Toast a message to set login info and bounce to
                // SettingActivity
                // Hint: TextUtils.
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    getActivity().startActivity(
                            new Intent(getActivity(), SettingsActivity.class));
                    return "Please update your username and password";
                }
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
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG)
                        .show();

        }
    }

}
