package com.bitmark.androidsdksample.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bitmark.androidsdksample.R;
import com.bitmark.androidsdksample.utils.Global;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.response.GetBitmarksResponse;
import com.bitmark.apiservice.utils.record.BitmarkRecord;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bitmark.androidsdksample.samples.QuerySample.queryBitmarks;
import static com.bitmark.androidsdksample.utils.CommonUtil.setClipboard;

public class QueryFragment extends Fragment {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private EditText edtAccountNumber;
    private CheckBox cbPending;
    private ListView lvBitmarks;
    private ProgressBar progressBar;

    public static QueryFragment newInstance() {
        return new QueryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_query, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button btnQueryBitmarks = view.findViewById(R.id.btnQueryBitmarks);
        btnQueryBitmarks.setOnClickListener(onClickListener);

        edtAccountNumber = view.findViewById(R.id.tvAccountNumber);
        cbPending = view.findViewById(R.id.cbPending);
        lvBitmarks = view.findViewById(R.id.lvBitmarks);
        View header = view.inflate(getContext(), R.layout.listview_header, null);
        lvBitmarks.addHeaderView(header);
        lvBitmarks.setOnItemLongClickListener(onItemLongClickListener);

        progressBar = view.findViewById(R.id.progressBar);

        if (Global.currentAccount != null) {
            edtAccountNumber.setText(Global.currentAccount.getAccountNumber());
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.btnQueryBitmarks:
                queryBitmarksHandler();
                break;
        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = (adapterView, view, position, id) -> {
        String bitmarkId = (String) adapterView.getItemAtPosition(position);
        setClipboard(getContext(), bitmarkId);
        return true;
    };

    private void queryBitmarksHandler() {
        String accountNumber = edtAccountNumber.getText().toString();

        if (TextUtils.isEmpty(accountNumber)) {
            Toast.makeText(getContext(), "Please input account number", Toast.LENGTH_LONG).show();
            return;
        }

        BitmarkQueryBuilder bitmarkQueryBuilder = new BitmarkQueryBuilder().ownedBy(accountNumber).pending(cbPending.isChecked());

        progressBar.setVisibility(View.VISIBLE);
        Single<GetBitmarksResponse> single = queryBitmarks(bitmarkQueryBuilder);

        Disposable disposable = single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> progressBar.setVisibility(View.INVISIBLE))
                .subscribe(response -> {
                    renderBitmarksList(getContext(), response);
                }, error -> {
                    Toast.makeText(getContext(), "Can not issue bitmarks", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                });

        compositeDisposable.add(disposable);
    }

    private void renderBitmarksList(Context context, GetBitmarksResponse response) {
        List<String> bitmarkIds = new ArrayList<>();
        List<BitmarkRecord> bitmarkRecords = response.getBitmarks();
        for (BitmarkRecord bitmark : bitmarkRecords) {
            bitmarkIds.add(bitmark.getId());
        }

        final ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, bitmarkIds);
        lvBitmarks.setAdapter(adapter);
    }
}