package br.bandeira.jogodasbandeirasguiz.ui.Help;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

import br.bandeira.jogodasbandeirasguiz.R;

public class HelpFragment extends Fragment {

    private HelpViewModel mViewModel;

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_help, container, false);
        TextView textView = root.findViewById(R.id.textViewHelpText1);
        String symbol[] = root.getResources().getStringArray(R.array.symbol);
        String flags[]  = root.getResources().getStringArray(R.array.flags);

        String txt = root.getResources().getString(R.string.the_game_has);
        txt = txt.replace("x1",Integer.toString(flags.length));
        txt = txt.replace("x2",Integer.toString(symbol.length));
        textView.setText(txt);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HelpViewModel.class);




    }

}
