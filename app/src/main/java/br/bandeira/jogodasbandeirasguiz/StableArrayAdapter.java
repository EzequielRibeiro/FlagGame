package br.bandeira.jogodasbandeirasguiz;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class StableArrayAdapter extends ArrayAdapter <Score> {


    Context context;

    public StableArrayAdapter(Context context, int textViewResourceId,
                              List<Score> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;

    }

    @SuppressLint("ResourceType")
    public View getView(int position, View view, ViewGroup parent) {

        Score sc = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.score_custom, parent, false);
        }
        // Lookup view for data population
        TextView time = view.findViewById(R.id.textViewTime);
        time.setText(sc.getTime());
        TextView error = view.findViewById(R.id.textViewError);
        error.setText(sc.getError());
        TextView hit = view.findViewById(R.id.textViewHit);
        hit.setText(sc.getHit());
        TextView score = view.findViewById(R.id.textViewScore);
         score.setText(sc.getScore());

        return view;

    };


}
