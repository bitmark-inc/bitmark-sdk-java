package com.bitmark.androidsdksample.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bitmark.androidsdksample.R;
import com.bitmark.androidsdksample.utils.CommonUtil;
import com.bitmark.androidsdksample.utils.Global;
import com.bitmark.apiservice.response.RegistrationResponse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bitmark.androidsdksample.samples.AssetRegistrationSample.registerAsset;
import static com.bitmark.androidsdksample.samples.BitmarkIssuanceSample.issueBitmarks;
import static com.bitmark.androidsdksample.utils.Global.hasCurrentAccount;

public class RegistrationIssuanceFragment extends Fragment {
    private static final String FILE_NAME = "example.txt";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private TextView tvFileContent;
    private TextView tvFilePath;
    private EditText edtAssetId;
    private EditText edtQuantity;
    private ListView lvBitmarks;
    private ProgressBar progressBar;

    private String outputFilePath;

    public static RegistrationIssuanceFragment newInstance() {
        return new RegistrationIssuanceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration_issuance, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button generateRandomFileBtn = view.findViewById(R.id.generateRandomFileBtn);
        generateRandomFileBtn.setOnClickListener(onClickListener);

        Button registerAssetBtn = view.findViewById(R.id.registerAssetBtn);
        registerAssetBtn.setOnClickListener(onClickListener);

        Button issueBitmarksBtn = view.findViewById(R.id.issueBitmarksBtn);
        issueBitmarksBtn.setOnClickListener(onClickListener);

        tvFileContent = view.findViewById(R.id.fileContent);
        tvFilePath = view.findViewById(R.id.filePath);
        edtAssetId = view.findViewById(R.id.assetId);
        edtQuantity = view.findViewById(R.id.quantity);
        lvBitmarks = view.findViewById(R.id.bitmarksListView);
        View header = view.inflate(getContext(), R.layout.listview_header, null);
        lvBitmarks.addHeaderView(header);
        progressBar = view.findViewById(R.id.progressBar);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.generateRandomFileBtn:
                generateRandomFileHandler();
                break;
            case R.id.registerAssetBtn:
                registerAssetHandler();
                break;
            case R.id.issueBitmarksBtn:
                issueBitmarksHandler();
                break;
        }
    };

    private void generateRandomFileHandler() {
        String randomText = CommonUtil.randomAlphaNumeric(20);
        FileOutputStream fos;

        try {
            Context context = getContext();
            fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(randomText.getBytes());
            fos.close();

            tvFileContent.setText("File Content: " + randomText);
            outputFilePath = context.getFilesDir() + "/" + FILE_NAME;
            tvFilePath.setText("File Path: " + outputFilePath);

        } catch (IOException e) {
            Toast.makeText(getContext(), "Can not generate random file", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void registerAssetHandler() {
        if (!hasCurrentAccount(getContext())) return;

        if (outputFilePath != null) {
            String assetName = "YOUR_ASSET_NAME"; // Asset length must be less than or equal 64 characters
            String assetFilePath = outputFilePath;

            HashMap metadata = new HashMap<String, String>() {{
                put("key1", "value1");
                put("key2", "value2");
            }}; // Metadata length must be less than or equal 2048 characters

            progressBar.setVisibility(View.VISIBLE);
            Single<List<RegistrationResponse.Asset>> single = registerAsset(Global.currentAccount, assetName, assetFilePath, metadata);
            Disposable disposable = single.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(() -> progressBar.setVisibility(View.INVISIBLE))
                    .subscribe(assets -> {
                        edtAssetId.setText(assets.get(0).getId());
                    }, error -> {
                        Toast.makeText(getContext(), "Can not register asset", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    });

            compositeDisposable.add(disposable);
        } else {
            Toast.makeText(getContext(), "Please generate file first", Toast.LENGTH_LONG).show();
        }
    }

    private void issueBitmarksHandler() {
        if (!hasCurrentAccount(getContext())) return;

        String assetId = edtAssetId.getText().toString();
        String quantityText = edtQuantity.getText().toString();

        if (TextUtils.isEmpty(assetId)) {
            Toast.makeText(getContext(), "Please register asset first", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(quantityText)) {
            Toast.makeText(getContext(), "Please input quantity", Toast.LENGTH_LONG).show();
            return;
        }

        int quantity = Integer.parseInt(quantityText);  // quantity must be less than or equal 100.

        progressBar.setVisibility(View.VISIBLE);
        Single<List<String>> single = issueBitmarks(Global.currentAccount, assetId, quantity);
        Disposable disposable = single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> progressBar.setVisibility(View.INVISIBLE))
                .subscribe(bitmarkIds -> {
                    renderBitmarksList(getContext(), bitmarkIds);
                }, error -> {
                    Toast.makeText(getContext(), "Can not issue bitmarks", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                });

        compositeDisposable.add(disposable);
    }

    private void renderBitmarksList(Context context, List<String> bitmarkIds) {
        final ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, bitmarkIds);
        lvBitmarks.setAdapter(adapter);
    }
}