package br.bandeira.jogodasbandeirasguiz.ui.Home;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.provider.BaseColumns;
import java.util.ArrayList;
import java.util.List;
import br.bandeira.jogodasbandeirasguiz.MainActivity;
import br.bandeira.jogodasbandeirasguiz.R;
import br.bandeira.jogodasbandeirasguiz.Score;
import br.bandeira.jogodasbandeirasguiz.ScoreDbHelper;
import br.bandeira.jogodasbandeirasguiz.StableArrayAdapter;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ListView listView;
    private StableArrayAdapter adapter;
    private static List<Score> itemScore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        textView.setText("SCORE");
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
               // textView.setText(s);
            }
        });

        listView = root.findViewById(R.id.listView);

        return root;
    }

    public void onResume(){
        super.onResume();
    }

    public void onStart(){
        super.onStart();
        adapter = new StableArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1,getScoreRecord());
        listView.setAdapter(adapter);
        Log.e(MainActivity.TAG,"onstart");

    }

    private List<Score> getScoreRecord(){
        itemScore = new ArrayList<>();
        ScoreDbHelper dbHelper = new ScoreDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                ScoreDbHelper.ScoreEntry.COLUMN_NAME_DATE,
                ScoreDbHelper.ScoreEntry.COLUMN_NAME_TIME,
                ScoreDbHelper.ScoreEntry.COLUMN_NAME_ERROR,
                ScoreDbHelper.ScoreEntry.COLUMN_NAME_HIT,
                ScoreDbHelper.ScoreEntry.COLUMN_NAME_SCORE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = ScoreDbHelper.ScoreEntry.COLUMN_NAME_SCORE + " = ?";
        String[] selectionArgs = { "My Title" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ScoreDbHelper.ScoreEntry.COLUMN_NAME_SCORE + " DESC";

        Cursor cursor = db.query(
                ScoreDbHelper.ScoreEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );


        Score score = new Score("ERRO","HIT","TIME","SCORE");
        itemScore.add(score);

        while(cursor.moveToNext()) {
            score = new Score();
            score.setId(cursor.getLong(
                    cursor.getColumnIndexOrThrow(ScoreDbHelper.ScoreEntry._ID)));
            score.setDate(cursor.getString(
                    cursor.getColumnIndexOrThrow(ScoreDbHelper.ScoreEntry.COLUMN_NAME_DATE)));
            score.setTime(cursor.getString(
                    cursor.getColumnIndexOrThrow(ScoreDbHelper.ScoreEntry.COLUMN_NAME_TIME)));
            score.setError(cursor.getString(
                    cursor.getColumnIndexOrThrow(ScoreDbHelper.ScoreEntry.COLUMN_NAME_ERROR)));
            score.setHit(cursor.getString(
                    cursor.getColumnIndexOrThrow(ScoreDbHelper.ScoreEntry.COLUMN_NAME_HIT)));
            score.setScore(cursor.getString(
                    cursor.getColumnIndexOrThrow(ScoreDbHelper.ScoreEntry.COLUMN_NAME_SCORE)));

            itemScore.add(score);
           // Log.e(MainActivity.TAG," " + score);
        }
        cursor.close();
        return itemScore;

    }



}
