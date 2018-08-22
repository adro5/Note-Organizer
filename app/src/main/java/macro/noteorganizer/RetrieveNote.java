package macro.noteorganizer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class RetrieveNote extends AppCompatActivity {

    DynamoDBMapper dbMapper;
    EditText title;
    EditText content;
    String passTitle;
    String passContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Coming Soon!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        title = (EditText) findViewById(R.id.editTitle);
        content = (EditText) findViewById(R.id.editContent);

        Button update = (Button) findViewById(R.id.button_Update);

        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentialsProvider);

        dbMapper = DynamoDBMapper.builder()
                .dynamoDBClient(client)
                .awsConfiguration(configuration)
                .build();

        // Retrieving note data passed from Adapter object
        passTitle = getIntent().getStringExtra("TITLE");
        passContent = getIntent().getStringExtra("CONTENT");

        UpdateContent();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { Save(); }
        });
    }

    public void UpdateContent() {
        title.setText(passTitle);
        content.setText(passContent);
    }

    public void Save() {
        final Notes2DO note = new Notes2DO();
        note.setUserId(IdentityManager.getDefaultIdentityManager().getCachedUserID());
        note.setTitle(title.getText().toString());
        note.setContent(content.getText().toString());
        note.setNoteId(title.getText().toString());
        note.setCreationDate(Calendar.getInstance().getTime().toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                dbMapper.save(note);
            }
        }).start();
    }

}
