package macro.noteorganizer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.List;

import macro.noteorganizer.models.nosql.Notes2DO;

import static android.support.v4.content.ContextCompat.startActivity;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Notes2DO> notes;

    RecyclerViewAdapter(List<Notes2DO> n) {
        notes = n;
    }
    DynamoDBMapper dbMapper;

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View notesView = inflater.inflate(R.layout.item_note, viewGroup, false);

        return new ViewHolder(notesView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int i) {
        Notes2DO note = notes.get(i);

        TextView textTitle = viewHolder.title;
        textTitle.setText(note.getTitle());

        TextView textDate = viewHolder.date;
        textDate.setText(note.getCreationDate());

        TextView textContent = viewHolder.content;
        textContent.setText(note.getContent());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        protected TextView title;
        protected TextView content;
        protected TextView date;

        ViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.textView_title);
            date = (TextView) v.findViewById(R.id.textView_date);
            content = (TextView) v.findViewById(R.id.textView_content);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            /* Pass data of selected RV item to Retrieve Note activity */
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(view.getContext(), RetrieveNote.class);
                intent.putExtra("TITLE", title.getText());
                intent.putExtra("CONTENT", content.getText());

                view.getContext().startActivity(intent);
            }
        }

        /*
            Context menu when press and hold an item
         */
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem item = contextMenu.add(0, view.getId(), 0, "Remove");
            item.setOnMenuItemClickListener(this);
        }

        /*
            Remove RecyclerView item and item in DB
         */
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            initDB();
            final Notes2DO notes2DO = new Notes2DO();
            notes2DO.setUserId(IdentityManager.getDefaultIdentityManager().getCachedUserID());
            notes2DO.setNoteId(title.getText().toString());

            new Thread(new Runnable() {
                @Override
                public void run() { dbMapper.delete(notes2DO); }
            }).start();

            int position = getAdapterPosition();
            notes.remove(position);
            notifyDataSetChanged();
            return true;
        }

        private void initDB() {
            AWSCredentialsProvider credentialsProvider = IdentityManager.getDefaultIdentityManager().getCredentialsProvider();
            AWSConfiguration configuration = IdentityManager.getDefaultIdentityManager().getConfiguration();

            AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentialsProvider);

            dbMapper = DynamoDBMapper.builder()
                    .awsConfiguration(configuration)
                    .dynamoDBClient(dbClient)
                    .build();
        }
    }
}
