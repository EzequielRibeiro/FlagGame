package br.bandeira.jogodasbandeirasguiz.ui.Privacy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PrivacyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PrivacyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is privacy fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}