package macro.noteorganizer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.core.SignInStateChangeListener;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import macro.noteorganizer.models.nosql.NotesDO;

public class Notes extends AppCompatActivity {

    private ViewPager mViewPager;

    DynamoDBMapper dynamoDBMapper;
    RecyclerView recyclerView;

    List<NotesDO> notesDOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notesDOS = Collections.synchronizedList(new ArrayList<NotesDO>());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Notes.this, AddNewNote.class));
            }
        });

        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        // Initialize DB Client

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);

        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();

        RetrieveDBInfo();

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(new RecyclerViewAdapter(notesDOS));
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


    public void RetrieveDBInfo() {
        DBThread dbThread = new DBThread();
        dbThread.start();
        /*new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();*/
        synchronized (dbThread) {
            try {
                Log.d("Waiting...", "Waiting for completion");
                dbThread.wait();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        /*synchronized (this) {
            PaginatedList<NotesDO> result = dynamoDBMapper.query(NotesDO.class, queryExpression);
            if (!result.isEmpty()) {
                notesDOS.addAll(result);

            }
        }*/

        Log.d("RetrieveDB", String.format("%d",notesDOS.size()));
    }

    public void UpdateRecyclerList() {

    }

    public void SignOut() {
        IdentityManager.getDefaultIdentityManager().signOut();
    }

    private class DBThread extends Thread {
        final NotesDO notesDO = new NotesDO();


        DynamoDBQueryExpression queryExpression;
        @Override
        public void run() {
            synchronized (this) {
                init();
                PaginatedList<NotesDO> result = dynamoDBMapper.query(NotesDO.class, queryExpression);
                if (!result.isEmpty()) {
                    notesDOS.addAll(result);
                }
                notify();
            }
        }

        public void init() {
            queryExpression = new DynamoDBQueryExpression().withHashKeyValues(notesDO)
                    .withConsistentRead(false);
            notesDO.setUserId(IdentityManager.getDefaultIdentityManager().getCachedUserID());
        }
    }
}
