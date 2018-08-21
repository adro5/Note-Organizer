package macro.noteorganizer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import macro.noteorganizer.models.nosql.Notes2DO;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public List<Notes2DO> notes;

    public RecyclerViewAdapter(List<Notes2DO> n) {
        notes = n;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View notesView = inflater.inflate(R.layout.item_note, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(notesView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder viewHolder, int i) {
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public TextView date;

        public ViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.textView_title);
            date = (TextView) v.findViewById(R.id.textView_date);
            content = (TextView) v.findViewById(R.id.textView_content);
        }
    }
}
