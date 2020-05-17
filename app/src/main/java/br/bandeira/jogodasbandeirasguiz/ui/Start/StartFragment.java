package br.bandeira.jogodasbandeirasguiz.ui.Start;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import br.bandeira.jogodasbandeirasguiz.MainActivity;
import br.bandeira.jogodasbandeirasguiz.R;
import br.bandeira.jogodasbandeirasguiz.ScoreDbHelper;

public class StartFragment extends Fragment {

    private StartViewModel startViewModel;


    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        startViewModel =
                ViewModelProviders.of(this).get(StartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_start, container, false);
        final TextView textView = root.findViewById(R.id.text_points_value);
        startViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
              //  textView.setText(s);
            }
        });

        SharedPreferences.Editor editor = root.getContext().getSharedPreferences("score", Context.MODE_PRIVATE).edit();
        editor.putInt("value",StartViewModel.SCORE).commit();

        startViewModel.StartGame(getResources(),root);
        return root;
    }

    public void onStop(){
        super.onStop();
       startViewModel.pauseChronometer();

    }

    public void onStart(){
        super.onStart();
        startViewModel.startChronometer();
    }

    public void onDestroy(){
        super.onDestroy();

        ScoreDbHelper dbHelper = new ScoreDbHelper(getContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ScoreDbHelper.ScoreEntry.COLUMN_NAME_DATE, "00/00/000");
        values.put(ScoreDbHelper.ScoreEntry.COLUMN_NAME_TIME, startViewModel.getTime());
        values.put(ScoreDbHelper.ScoreEntry.COLUMN_NAME_ERROR, StartViewModel.ERROR);
        values.put(ScoreDbHelper.ScoreEntry.COLUMN_NAME_HIT, StartViewModel.HIT);
        values.put(ScoreDbHelper.ScoreEntry.COLUMN_NAME_SCORE, StartViewModel.SCORE);

        int sum = StartViewModel.ERROR + StartViewModel.HIT;

        if(sum > 0) {
            long newRowId = db.insert(ScoreDbHelper.ScoreEntry.TABLE_NAME, null, values);

            if (newRowId > 0) {
                Log.e(MainActivity.TAG, "salvo no banco");
                StartViewModel.ERROR = 0;
                StartViewModel.HIT = 0;
                StartViewModel.SCORE = 0;
            } else {
                Log.e(MainActivity.TAG, "não foi salvo no banco");
            }

        }
    }

}
