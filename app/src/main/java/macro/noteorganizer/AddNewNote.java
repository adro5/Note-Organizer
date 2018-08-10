package macro.noteorganizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddNewNote extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.line_layout);
        TextView title = (TextView) findViewById(R.id.editText_Title);
        TextView content = (TextView) findViewById(R.id.editText_Content);
        
    }
}
