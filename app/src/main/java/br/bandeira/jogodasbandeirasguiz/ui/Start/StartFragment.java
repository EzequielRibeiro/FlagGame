package br.bandeira.jogodasbandeirasguiz.ui.Start;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import br.bandeira.jogodasbandeirasguiz.R;
import br.bandeira.jogodasbandeirasguiz.ui.Home.HomeFragment;


public class StartFragment extends Fragment {

    private StartViewModel startViewModel;
    private View root;
    private NavController navController ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        startViewModel =
                ViewModelProviders.of(this).get(StartViewModel.class);
        root = inflater.inflate(R.layout.fragment_start, container, false);
        final TextView textView = root.findViewById(R.id.text_points_value);
        startViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
              //  textView.setText(s);
            }
        });

        StartViewModel.CHRONOMETER = root.findViewById(R.id.chronometer);
        StartViewModel.CHRONOMETER.setTextColor(root.getResources().getColor(R.color.colorGreen));

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                StartViewModel.pauseChronometer();
                closeWindows();

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        return root;
    }



   AlertDialog alert;
   private void closeWindows(){

       AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
       builder.setMessage("Are you sure you want to exit?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                   public void onClick(DialogInterface dialog, int id) {

                       getActivity().onBackPressed();

                       startViewModel.saveScore(getContext());
                       StartViewModel.resetChronometer();
                       navController.popBackStack();
                       alert.dismiss();

                   }
               })
               .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                       StartViewModel.startChronometer();
                   }
               });
       alert = builder.create();
       alert.show();

   }


    private void replaceFragment() {
        HomeFragment homeFragment = new HomeFragment();
        String backStateName = homeFragment.getClass().getName();
        FragmentManager manager = getFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.nav_host_fragment,homeFragment);
            ft.addToBackStack(backStateName);
            ft.commit();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.menu_home));

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("score",StartViewModel.SCORE);
        savedInstanceState.putInt("hit",StartViewModel.HIT);
        savedInstanceState.putInt("error",StartViewModel.ERROR);
        savedInstanceState.putInt("positioncurrent",StartViewModel.POSITIONCURRENT);
        Log.e("acti: ","onsave");
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            if(!savedInstanceState.containsKey("score")){
                savedInstanceState.putInt("score",StartViewModel.SCORE);}
            else if(!savedInstanceState.containsKey("hit")){
                savedInstanceState.putInt("hit",StartViewModel.HIT);}
            else if(!savedInstanceState.containsKey("error")){
                savedInstanceState.putInt("error",StartViewModel.ERROR);}
            else if(!savedInstanceState.containsKey("positioncurrent")){
                savedInstanceState.putInt("positioncurrent", StartViewModel.POSITIONCURRENT);
            }

            Log.e("score: ", Integer.toString(savedInstanceState.getInt("score")));
            StartViewModel.SCORE = savedInstanceState.getInt("score");
            StartViewModel.HIT   = savedInstanceState.getInt("hit");
            StartViewModel.ERROR = savedInstanceState.getInt("error");
            StartViewModel.POSITIONCURRENT = savedInstanceState.getInt("positioncurrent");
        }

        startViewModel.StartGame(getResources(),root,getFragmentManager());
    }

    public void onStop(){
        super.onStop();
        StartViewModel.pauseChronometer();
    }

    public void onResume(){
        super.onResume();
        StartViewModel.startChronometer();

        if(alert != null){
            alert.dismiss();
        }
    }

    public void onStart(){
        super.onStart();
    }

    public void onDestroy(){
        super.onDestroy();
        StartViewModel.saveScore(getContext());

    }

}
