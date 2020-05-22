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
        TextView timeText = view.findViewById(R.id.textViewTime);
        timeText.setText(sc.getTime());
        TextView errorText = view.findViewById(R.id.textViewFlags);
        errorText.setText(sc.getFlags());
        TextView hitTex = view.findViewById(R.id.textViewHit);
        hitTex.setText(sc.getHit());
        TextView scoreText = view.findViewById(R.id.textViewScore);
         scoreText.setText(sc.getScore());

        return view;

    };
 /*
    private boolean isNumber(String number){

        try {
            int i = Integer.parseInt(number);
        } catch (NumberFormatException n) {
            return false;
        }

        return true;

    }

  */


}
