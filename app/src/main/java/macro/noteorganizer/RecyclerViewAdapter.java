package macro.noteorganizer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import macro.noteorganizer.models.nosql.Notes2DO;

import static android.support.v4.content.ContextCompat.startActivity;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Notes2DO> notes;

    RecyclerViewAdapter(List<Notes2DO> n) {
        notes = n;
    }

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


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView title;
        protected TextView content;
        protected TextView date;

        ViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.textView_title);
            date = (TextView) v.findViewById(R.id.textView_date);
            content = (TextView) v.findViewById(R.id.textView_content);

            itemView.setOnClickListener(this);
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
    }
}
