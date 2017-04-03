package practicaltest01.eim.systems.cs.pub.ro.practicaltest01;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest01MainActivity extends AppCompatActivity {

    private MessageBroadcastReceiver messageBroadcastReceiver = new MessageBroadcastReceiver();
    private IntentFilter intentFilter = new IntentFilter();

    private static final int SECONDARY_ACTIVITY_REQUEST_CODE = 1;
    private static final int NUMBER_OF_CLICKS_THRESHOLD = 10;
    private static final int SERVICE_STOPPED = -1;
    private static final int SERVICE_STARTED = 1;
    EditText textView_right;
    EditText textView_left;
    int leftNumberOfClicks;
    int rightNumberOfClicks;
    int serviceStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test01_main);

        intentFilter.addAction("TODAY");
        intentFilter.addAction("TOMORROW");
        intentFilter.addAction("YESTERDAY");

        textView_right = (EditText) findViewById(R.id.right_edit_text);
        textView_left = (EditText) findViewById(R.id.left_edit_text);
        textView_left.setText(String.valueOf(0));
        textView_right.setText(String.valueOf(0));

        Button left_button = (Button) findViewById(R.id.left_button);
        left_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textView_left.getText().toString();
                if (!text.isEmpty()) {
                    int left_text = Integer.parseInt(text);
                    textView_left.setText(String.valueOf(left_text + 1));
                }
                leftNumberOfClicks = Integer.parseInt(text) + 1;
                if (leftNumberOfClicks + rightNumberOfClicks > NUMBER_OF_CLICKS_THRESHOLD
                        && serviceStatus == SERVICE_STOPPED) {
                    Intent intent = new Intent(getApplicationContext(), PracticalTest01Service.class);
                    intent.putExtra("firstNumber", leftNumberOfClicks);
                    intent.putExtra("secondNumber", rightNumberOfClicks);
                    getApplicationContext().startService(intent);
                    serviceStatus = SERVICE_STARTED;
                }
            }
        });

        Button right_button = (Button) findViewById(R.id.right_button);
        right_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textView_right.getText().toString();
                if (!text.isEmpty()) {
                    int right_text = Integer.parseInt(text);
                    textView_right.setText(String.valueOf(right_text + 1));
                }
                rightNumberOfClicks = Integer.parseInt(text) + 1;
                if (leftNumberOfClicks + rightNumberOfClicks > NUMBER_OF_CLICKS_THRESHOLD
                        && serviceStatus == SERVICE_STOPPED) {
                    Intent intent = new Intent(getApplicationContext(), PracticalTest01Service.class);
                    intent.putExtra("firstNumber", leftNumberOfClicks);
                    intent.putExtra("secondNumber", rightNumberOfClicks);
                    getApplicationContext().startService(intent);
                    serviceStatus = SERVICE_STARTED;
                }
            }
        });

        Button navigate_button = (Button) findViewById(R.id.navigate_to_secondary_activity_button);
        navigate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PracticalTest01SecondaryActivity.class);
                int numberOfClicks = Integer.parseInt(textView_left.getText().toString()) +
                        Integer.parseInt(textView_right.getText().toString());
                intent.putExtra("numberOfClicks", numberOfClicks);
                startActivityForResult(intent, SECONDARY_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("leftCount", textView_left.getText().toString());
        outState.putString("rightCount", textView_right.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("leftCount")) {
            textView_left.setText(savedInstanceState.getString("leftCount"));
        } else {
            textView_left.setText(String.valueOf(0));
        }
        if (savedInstanceState.containsKey("rightCount")) {
            textView_right.setText(savedInstanceState.getString("rightCount"));
        } else {
            textView_right.setText(String.valueOf(0));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECONDARY_ACTIVITY_REQUEST_CODE) {
            String message;
            if (resultCode == 0)
                message = "Cancel";
            else
                message = "OK";
            Toast.makeText(this, "The activity returned with result " + message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, PracticalTest01Service.class);
        stopService(intent);
        serviceStatus = SERVICE_STOPPED;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(messageBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(messageBroadcastReceiver, intentFilter);
    }
}

    class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("[Message]", intent.getStringExtra("message"));
        }
    }
