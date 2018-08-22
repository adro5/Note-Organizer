package macro.noteorganizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.Calendar;

import macro.noteorganizer.models.nosql.Notes2DO;

public class AddNewNote extends AppCompatActivity {

    EditText textTitle;
    EditText textContent;
    DynamoDBMapper dbMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);
        textTitle = (EditText) findViewById(R.id.editText_title);
        textContent = (EditText) findViewById(R.id.editText_content);
        Button btnAdd = (Button) findViewById(R.id.button_add);

        dbInit();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnClick();
            }
        });
        
    }

    private void btnClick() {
        final Notes2DO notes = new Notes2DO();
        notes.setUserId(IdentityManager.getDefaultIdentityManager().getCachedUserID());
        notes.setTitle(textTitle.getText().toString());
        notes.setContent(textContent.getText().toString());
        notes.setNoteId(textTitle.getText().toString());
        notes.setCreationDate(Calendar.getInstance().getTime().toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                dbMapper.save(notes);
            }
        }).start();
        finish();
    }

    private void dbInit() {
        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();
        AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentialsProvider);

        dbMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dbClient)
                .awsConfiguration(configuration)
                .build();
    }
}
