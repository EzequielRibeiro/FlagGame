package br.bandeira.jogodasbandeirasguiz;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import br.bandeira.jogodasbandeirasguiz.ui.Start.StartViewModel;

public class DialogFragmentShowResult extends DialogFragment {


    private int score = 0, hit = 0, flags = 0;
    private boolean win = false;

    public DialogFragmentShowResult(){

    }

    public static DialogFragmentShowResult newInstance(int score,int flags,int hit,boolean win) {
        DialogFragmentShowResult frag = new DialogFragmentShowResult();
        Bundle args = new Bundle();
        args.putInt("score",score);
        args.putInt("flags",flags);
        args.putInt("hit",hit);
        args.putBoolean("win",win);
        frag.setArguments(args);

        return frag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        score = getArguments().getInt("score");
        flags = getArguments().getInt("flags");
        hit   = getArguments().getInt("hit");
        win   = getArguments().getBoolean("win");

        View v = inflater.inflate(R.layout.dialog_show_result, null);

        TextView textDialogScore = (TextView)  v.findViewById(R.id.textDialogScore);
        textDialogScore.setText(Integer.toString(score));

        TextView textDialogFlags = (TextView)  v.findViewById(R.id.textDialogFlags);
        textDialogFlags.setText(Integer.toString(flags));

        TextView textDialogHit = (TextView)  v.findViewById(R.id.textDialogHits);
        textDialogHit.setText(Integer.toString(hit));

        ImageView imageView = (ImageView) v.findViewById(R.id.imageDialogResultWin);

        if(win){
            imageView.setVisibility(View.VISIBLE);
        }else{
            imageView.setVisibility(View.GONE);
        }

        builder.setView(v)
                // Add action buttons
                .setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        StartViewModel.resetChronometer();
                        StartViewModel.startChronometer();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                        navController.popBackStack();
                        //Navigation.findNavController(view).navigate(R.id.nav_home);
                    }
                });
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }



}
