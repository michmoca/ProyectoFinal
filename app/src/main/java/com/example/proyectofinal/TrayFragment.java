package com.example.proyectofinal;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrayFragment extends Fragment {


    public TrayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tray, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = getActivity().findViewById(R.id.tray_list);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return LayoutInflater.from(getActivity()).inflate(R.layout.list_item_tray, null);
            }
        });

        /*// Initialise DB
        db = AppDatabase.getAppDatabase(getContext());
        listTray();

        trayList = new ArrayList<Tray>();
        adapter = new TrayAdapter(this.getActivity(), trayList);

        ListView listView = (ListView) getActivity().findViewById(R.id.tray_list);
        listView.setAdapter(adapter);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.tray_map);
        mapFragment.getMapAsync(this);

        // Address EditText
        address = (EditText) getActivity().findViewById(R.id.tray_address);

        // Handle Map Address
        handleMapAddress();

        // Handle Add Payment Button Click event
        handleAddPayment();*/
    }

}
