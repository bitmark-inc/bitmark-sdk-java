package com.bitmark.androidsdksample.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bitmark.androidsdksample.R;
import com.bitmark.androidsdksample.utils.Global;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.features.Account;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bitmark.androidsdksample.sdksamples.AccountSample.getAccountFromRecoveryPhrase;
import static com.bitmark.androidsdksample.sdksamples.BitmarkTransferSample.acceptTransferOffer;
import static com.bitmark.androidsdksample.sdksamples.BitmarkTransferSample.cancelTransferOffer;
import static com.bitmark.androidsdksample.sdksamples.BitmarkTransferSample.sendTransferOffer;
import static com.bitmark.androidsdksample.sdksamples.BitmarkTransferSample.transferOneSignature;
import static com.bitmark.androidsdksample.utils.Global.hasCurrentAccount;

public class TransferFragment extends Fragment {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private EditText edtTransferBitmark;
    private EditText edtReceiverAccountNumber;
    private EditText edtReceiverRecoveryPhrase;
    private TextView tvTransferMessage;
    private TextView tvTransfer2SignaturesMessage;
    private Button btnCancelTransferOffer;
    private LinearLayout receiverContainer;
    private ProgressBar progressBar;

    public static TransferFragment newInstance() {
        return new TransferFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button btnTransferOneSignature = view.findViewById(R.id.btnTransferOneSignature);
        btnTransferOneSignature.setOnClickListener(onClickListener);

        Button btnSendTransferOffer = view.findViewById(R.id.btnSendTransferOffer);
        btnSendTransferOffer.setOnClickListener(onClickListener);

        btnCancelTransferOffer = view.findViewById(R.id.btnCancelTransferOffer);
        btnCancelTransferOffer.setOnClickListener(onClickListener);

        Button btnAcceptTransferOffer = view.findViewById(R.id.btnAcceptTransferOffer);
        btnAcceptTransferOffer.setOnClickListener(onClickListener);

        edtTransferBitmark = view.findViewById(R.id.edtTransferBitmarkId);
        edtReceiverAccountNumber = view.findViewById(R.id.edtReceiverAccountNumber);
        tvTransferMessage = view.findViewById(R.id.tvTransferMessage);
        receiverContainer = view.findViewById(R.id.receiverContainer);
        edtReceiverRecoveryPhrase = view.findViewById(R.id.edtReceiverRecoveryPhrase);
        tvTransfer2SignaturesMessage = view.findViewById(R.id.tvTransfer2SignaturesMessage);

        progressBar = view.findViewById(R.id.progressBar);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.btnTransferOneSignature:
                transferOneSignatureHandler();
                break;
            case R.id.btnSendTransferOffer:
                sendTransferOfferHandler();
                break;
            case R.id.btnCancelTransferOffer:
                cancelTransferOfferHandler();
                break;
            case R.id.btnAcceptTransferOffer:
                acceptTransferOfferHandler();
                break;
        }
    };

    private void transferOneSignatureHandler() {
        if (!hasCurrentAccount(getContext())) return;

        String bitmarkId = edtTransferBitmark.getText().toString();
        String receiverAccountNumber = edtReceiverAccountNumber.getText().toString();

        if (TextUtils.isEmpty(bitmarkId)) {
            Toast.makeText(getContext(), "Please input Your Bitmark Id", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(receiverAccountNumber)) {
            Toast.makeText(getContext(), "Please input Receiver Account Number", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvTransferMessage.setVisibility(View.GONE);
        Single<String> single = transferOneSignature(Global.currentAccount, bitmarkId, receiverAccountNumber);
        Disposable disposable = single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> progressBar.setVisibility(View.INVISIBLE))
                .subscribe(txId -> {
                    Toast.makeText(getContext(), "Transfer successfully!", Toast.LENGTH_LONG).show();
                    tvTransferMessage.setVisibility(View.VISIBLE);
                }, error -> {
                    Toast.makeText(getContext(), "Can not transfer bitmark", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                });

        compositeDisposable.add(disposable);
    }

    private void sendTransferOfferHandler() {
        if (!hasCurrentAccount(getContext())) return;

        String bitmarkId = edtTransferBitmark.getText().toString();
        String receiverAccountNumber = edtReceiverAccountNumber.getText().toString();

        if (TextUtils.isEmpty(bitmarkId)) {
            Toast.makeText(getContext(), "Please input Your Bitmark Id", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(receiverAccountNumber)) {
            Toast.makeText(getContext(), "Please input Receiver Account Number", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Single<String> single = sendTransferOffer(Global.currentAccount, bitmarkId, receiverAccountNumber);
        Disposable disposable = single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> progressBar.setVisibility(View.INVISIBLE))
                .subscribe(txId -> {
                    Toast.makeText(getContext(), "Sent successfully!", Toast.LENGTH_LONG).show();
                    btnCancelTransferOffer.setVisibility(View.VISIBLE);
                    receiverContainer.setVisibility(View.VISIBLE);
                }, error -> {
                    Toast.makeText(getContext(), "Can not send transfer offer", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                });

        compositeDisposable.add(disposable);
    }

    private void cancelTransferOfferHandler() {
        if (!hasCurrentAccount(getContext())) return;

        String bitmarkId = edtTransferBitmark.getText().toString();

        if (TextUtils.isEmpty(bitmarkId)) {
            Toast.makeText(getContext(), "Please input Your Bitmark Id", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Single<String> single = cancelTransferOffer(Global.currentAccount, bitmarkId);
        Disposable disposable = single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> progressBar.setVisibility(View.INVISIBLE))
                .subscribe(txId -> {
                    Toast.makeText(getContext(), "Canceled successfully!", Toast.LENGTH_LONG).show();
                    btnCancelTransferOffer.setVisibility(View.GONE);
                    receiverContainer.setVisibility(View.GONE);
                }, error -> {
                    Toast.makeText(getContext(), "Can not cancel transfer offer", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                });

        compositeDisposable.add(disposable);
    }

    private void acceptTransferOfferHandler() {
        String bitmarkId = edtTransferBitmark.getText().toString();
        String receiverRecoveryPhrase = edtReceiverRecoveryPhrase.getText().toString();

        if (TextUtils.isEmpty(bitmarkId)) {
            Toast.makeText(getContext(), "Please input Your Bitmark Id", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(receiverRecoveryPhrase)) {
            Toast.makeText(getContext(), "Please input Recovery Phrase", Toast.LENGTH_LONG).show();
            return;
        }

        Account receiverAccount;
        try {
            receiverAccount = getAccountFromRecoveryPhrase(receiverRecoveryPhrase);
        } catch (ValidateException ex) {
            Toast.makeText(getContext(), "Can not get account from Recovery Phrase", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvTransfer2SignaturesMessage.setVisibility(View.GONE);
        Single<String> single = acceptTransferOffer(receiverAccount, bitmarkId);
        Disposable disposable = single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> progressBar.setVisibility(View.INVISIBLE))
                .subscribe(txId -> {
                    Toast.makeText(getContext(), "Accepted successfully!", Toast.LENGTH_LONG).show();
                    tvTransfer2SignaturesMessage.setVisibility(View.VISIBLE);
                    btnCancelTransferOffer.setVisibility(View.GONE);
                }, error -> {
                    Toast.makeText(getContext(), "Can not accept transfer offer", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                });

        compositeDisposable.add(disposable);
    }
}