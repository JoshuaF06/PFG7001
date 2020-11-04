package com.android.pfg7001.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.pfg7001.R;

public class FindPeersFragment extends Fragment {

    private Button btnDiscover;
    private ListView listView;
    private IFindPeersFragment myInterface;
    String[] deviceNameArray;

    public interface IFindPeersFragment {
        void btnDiscover();

        void onListClick(int position);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof IFindPeersFragment) {
            this.myInterface = (IFindPeersFragment) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_peers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnDiscover = view.findViewById(R.id.discover);
        listView = view.findViewById(R.id.peerListView);

        btnDiscover.setOnClickListener(v -> myInterface.btnDiscover());

        listView.setOnItemClickListener((parent, view1, position, id) -> myInterface.onListClick(position));
    }

    public void updateList(String[] deviceNameArray) {
        this.deviceNameArray = deviceNameArray;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, deviceNameArray);
        listView.setAdapter(adapter);
    }
}
