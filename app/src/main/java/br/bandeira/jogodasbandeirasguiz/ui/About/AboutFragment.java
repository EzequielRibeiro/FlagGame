package br.bandeira.jogodasbandeirasguiz.ui.About;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.bandeira.jogodasbandeirasguiz.BuildConfig;
import br.bandeira.jogodasbandeirasguiz.R;

public class AboutFragment extends Fragment {

    private TextView textView;
    private AboutViewModel mViewModel;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_about, container, false);

        textView = v.findViewById(R.id.textViewHelpText1);
        String text = textView.getText().toString();
        text = text.replace("X.X", BuildConfig.VERSION_NAME);
        text = text.replace("appname", getText(R.string.app_name));
        textView.setText(text);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AboutViewModel.class);
        // TODO: Use the ViewModel
    }

}
