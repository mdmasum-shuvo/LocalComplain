package com.sumdroid.localcomplaint.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sumdroid.localcomplaint.R;
import com.sumdroid.localcomplaint.adapter.ComplainListAdapter;
import com.sumdroid.localcomplaint.appConstant.AppConstants;
import com.sumdroid.localcomplaint.model.Complain;
import com.sumdroid.localcomplaint.utils.DividerItemDecoration;

import java.util.ArrayList;

public class RoadFragment extends Fragment {

    private RecyclerView recycleComplainlist;
    private ArrayList<Complain> complainList;
    ComplainListAdapter complainAdapter;
    Activity mActivity;
    Context mContext;
    FirebaseDatabase mDatabse;
    DatabaseReference mRef;
    private LinearLayout loadingView, noDataView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView=null;
        rootView = inflater.inflate(R.layout.list_layout, container, false);

        initView(rootView);
        initVariable(rootView);
        initFunctionality();
        initListener();

        return rootView;
    }

    private void getFirebaseData() {
        if (!complainList.isEmpty()){
            complainList.clear();
        }
        Log.e("call function","get firebase data");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showLoader();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String image=snapshot.child(AppConstants.IMAGE_URL_FIELD).getValue().toString();
                    String title = snapshot.child(AppConstants.TITLE_FIELD).getValue().toString();
                    String description = snapshot.child(AppConstants.DESCRIPTION_FIELD).getValue().toString();


                    complainList.add(new Complain(image,title,description));

                }
                complainAdapter.notifyDataSetChanged();
                hideLoader();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error",""+databaseError);

            }
        });


    }

    private void initView(View rootView) {

        recycleComplainlist =rootView.findViewById(R.id.recycleView_complain_list);
        recycleComplainlist.setHasFixedSize(true);
        recycleComplainlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleComplainlist.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));



    }


    private void initVariable(View rootView) {
        mActivity=getActivity();
        mContext=getActivity().getApplicationContext();
        mDatabse=FirebaseDatabase.getInstance();
        mRef=mDatabse.getReference(AppConstants.ROAD);
        complainList =new ArrayList<>();
        complainAdapter =new ComplainListAdapter(mActivity, complainList);
        recycleComplainlist.setAdapter(complainAdapter);
        initLoader(rootView);

    }

    private void initListener() {
    }

    private void initFunctionality() {

        getFirebaseData();
    }

    public void initLoader(View rootView) {
        loadingView = (LinearLayout) rootView.findViewById(R.id.loadingView);
        noDataView = (LinearLayout) rootView.findViewById(R.id.noDataView);
    }

    public void showLoader() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }

        if (noDataView != null) {
            noDataView.setVisibility(View.GONE);
        }
    }

    public void hideLoader() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (noDataView != null) {
            noDataView.setVisibility(View.GONE);
        }
    }

    public void showEmptyView() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (noDataView != null) {
            noDataView.setVisibility(View.VISIBLE);
        }
    }



}
