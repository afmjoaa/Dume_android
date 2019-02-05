package io.dume.dume.teacher.homepage.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.dume.dume.R;
import io.dume.dume.teacher.adapters.PayAdapter;
import io.dume.dume.teacher.homepage.TeacherActivtiy;
import io.dume.dume.teacher.homepage.TeacherContract;
import io.dume.dume.teacher.homepage.TeacherDataStore;
import io.dume.dume.teacher.pojo.Pay;
import io.dume.dume.util.DumeUtils;
import io.dume.dume.util.GridSpacingItemDecoration;

public class PayFragment extends Fragment {
    @BindView(R.id.payRV)
    RecyclerView payRv;
    private PayViewModel mViewModel;
    private TeacherActivtiy activtiy;
    private static PayFragment payFragment = null;
    private int itemWidth;
    private TeacherDataStore teacherDataStore;

    public static PayFragment getInstance() {
        if (payFragment == null) {
            payFragment = new PayFragment();
        }
        return payFragment;
    }

    public static PayFragment newInstance() {
        return new PayFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pay_fragment, container, false);
        ButterKnife.bind(this, root);
        mViewModel = new PayViewModel();

        assert container != null;
        int[] wh = DumeUtils.getScreenSize(container.getContext());
        float mDensity = getResources().getDisplayMetrics().density;
        int availableWidth = (int) (wh[0] - (93 * mDensity));

        payRv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        payRv.addItemDecoration(new GridSpacingItemDecoration(2, (int) (10 * mDensity), true));
        itemWidth = (int) ((availableWidth - (30 * mDensity)) / 2);


        teacherDataStore = activtiy != null ? activtiy.teacherDataStore : null;
        if (teacherDataStore != null) {
            if (teacherDataStore.getDocumentSnapshot() == null) {
                activtiy.presenter.loadProfile(new TeacherContract.Model.Listener<Void>() {
                    @Override
                    public void onSuccess(Void list) {
                        loadPaymentData();
                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                loadPaymentData();
            }
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void loadPaymentData() {
        Map<String, Object> payments = (Map<String, Object>) activtiy.teacherDataStore.getDocumentSnapshot().get("payments");
        ArrayList<Pay> payArrayList = new ArrayList<>();
        payArrayList.add(new Pay(payments != null ? Integer.parseInt(payments.get("obligation_amount").toString()) : 0, "Due", 0, false, 0.0f));
        payArrayList.add(new Pay(payments != null ? Integer.parseInt(payments.get("total_paid").toString()) : 0, "Total Paid", 0, false, 0.0f));
        payRv.setAdapter(new PayAdapter(getContext(), itemWidth, payArrayList));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activtiy = (TeacherActivtiy) context;
    }
}
