package macro.noteorganizer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.amazonaws.ResponseMetadata;
import com.amazonaws.auth.AWSCognitoIdentityProvider;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import macro.noteorganizer.models.nosql.Notes2DO;

public class Notes extends AppCompatActivity implements View.OnCreateContextMenuListener{

    private ViewPager mViewPager;

    DynamoDBMapper dynamoDBMapper;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    List<Notes2DO> notesDOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notesDOS = Collections.synchronizedList(new ArrayList<Notes2DO>());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create new note activity...
                startActivity(new Intent(Notes.this, AddNewNote.class));
            }
        });

        // Precursor to getting access to the authorized AWS resources
        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        // Initialize DB Client
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);

        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();

        RetrieveDBInfo();

        /*
            Captures RecyclerView
            Adds dividers
            Sets custom adapter and layout manager
        */
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(false);

        adapter = new RecyclerViewAdapter(notesDOS);
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);
    }

    /*
        When restarting Notes activity check for any changes to data set
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        RetrieveDBInfo();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes, menu);
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
            SignOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SignOut() {
        IdentityManager.getDefaultIdentityManager().signOut();
    }

    /*
        DynamoDB querying is an asynchronous task.
        Following code:
        1) Queries database for all notes relating to the userId
        2) Updates list of notes and unlocks the main thread to continue init
     */
    public void RetrieveDBInfo() {
        DBThread dbThread = new DBThread();
        dbThread.start();

        synchronized (dbThread) {
            try {
                Log.d("Waiting...", "Waiting for completion");
                dbThread.wait();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class DBThread extends Thread {
        final Notes2DO notesDO = new Notes2DO();
        DynamoDBQueryExpression queryExpression;

        @Override
        public void run() {
            synchronized (this) {
                    init();
                    PaginatedList<Notes2DO> result = dynamoDBMapper.query(Notes2DO.class, queryExpression);
                if (notesDOS.isEmpty()) {
                    if (!result.isEmpty()) {
                        notesDOS.addAll(result);
                    }
                }
                else {
                    notesDOS.clear();
                    notesDOS.addAll(result);
                }
                notify();
            }
        }

        void init() {
            queryExpression = new DynamoDBQueryExpression().withHashKeyValues(notesDO)
                    .withConsistentRead(false);
            notesDO.setUserId(IdentityManager.getDefaultIdentityManager().getCachedUserID());
        }
    }
}
