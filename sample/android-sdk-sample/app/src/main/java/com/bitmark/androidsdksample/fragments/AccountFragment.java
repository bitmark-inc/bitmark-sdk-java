package com.bitmark.androidsdksample.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bitmark.androidsdksample.R;
import com.bitmark.androidsdksample.utils.Global;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.features.Account;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.bitmark.androidsdksample.samples.AccountSample.createNewAccount;
import static com.bitmark.androidsdksample.samples.AccountSample.getAccountFromRecoveryPhrase;
import static com.bitmark.androidsdksample.samples.AccountSample.getRecoveryPhraseFromAccount;
import static com.bitmark.androidsdksample.samples.StoreKeySample.getAccountFromKeyStore;
import static com.bitmark.androidsdksample.samples.StoreKeySample.storeAccountToKeystore;
import static com.bitmark.androidsdksample.utils.CommonUtil.setClipboard;
import static com.bitmark.androidsdksample.utils.KeyUtil.getAccountNumber;

public class AccountFragment extends Fragment {
    private TextView tvAccountNumber;
    private TextView tvRecoveryPhrase;
    private EditText edtRecoveryPhrase;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button createNewAccountBtn = view.findViewById(R.id.createNewAccountBtn);
        createNewAccountBtn.setOnClickListener(onClickListener);

        Button recoverAccountBtn = view.findViewById(R.id.recoverAccountBtn);
        recoverAccountBtn.setOnClickListener(onClickListener);

        tvAccountNumber = view.findViewById(R.id.accountNumber);
        tvAccountNumber.setOnClickListener(onClickListener);

        tvRecoveryPhrase = view.findViewById(R.id.recoveryPhrase);
        tvRecoveryPhrase.setOnClickListener(onClickListener);

        edtRecoveryPhrase = view.findViewById(R.id.recoveryPhraseInput);

        loadAccountIfAny(getActivity());
    }

    private View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.createNewAccountBtn:
                createNewAccountHandler();
                break;
            case R.id.recoverAccountBtn:
                recoverAccountHandler();
                break;
            case R.id.accountNumber:
                setClipboard(v.getContext(), tvAccountNumber.getText().toString());
                break;
            case R.id.recoveryPhrase:
                setClipboard(v.getContext(), tvRecoveryPhrase.getText().toString());
                break;
        }
    };

    private void createNewAccountHandler() {
        Global.currentAccount = createNewAccount();
        renderAccountInfo(Global.currentAccount);
        storeAccountToKeystore(Global.currentAccount, getContext(), getActivity());
    }

    private void renderAccountInfo(Account account) {
        tvAccountNumber.setText(account.getAccountNumber());
        tvRecoveryPhrase.setText(getRecoveryPhraseFromAccount(account));
    }

    private void recoverAccountHandler() {
        String recoveryPhrase = edtRecoveryPhrase.getText().toString();

        if (TextUtils.isEmpty(recoveryPhrase)) {
            Toast.makeText(getContext(), "Please input Recovery Phrase", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Global.currentAccount = getAccountFromRecoveryPhrase(recoveryPhrase);
            renderAccountInfo(Global.currentAccount);
            storeAccountToKeystore(Global.currentAccount, getContext(), getActivity());
            Toast.makeText(getContext(), "Recovered successfully!", Toast.LENGTH_LONG).show();
        } catch (ValidateException ex) {
            Toast.makeText(getContext(), "Can not recover account", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void loadAccountIfAny(Activity activity) {
        String accountNumber = getAccountNumber(activity.getApplicationContext());

        getAccountFromKeyStore(accountNumber, activity.getApplicationContext(), activity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(account -> {
                    if (account != null) {
                        Global.currentAccount = account;
                        renderAccountInfo(Global.currentAccount);
                    }
                }, error -> {
                    Toast.makeText(getContext(), "Can not get account from Key Store", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                });
    }
}