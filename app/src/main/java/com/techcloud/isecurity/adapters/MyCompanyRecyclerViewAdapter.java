package com.techcloud.isecurity.adapters;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techcloud.isecurity.fragments.CompanyFragment.OnListFragmentInteractionListener;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.models.Building;
import com.techcloud.isecurity.models.Company;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Company} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyCompanyRecyclerViewAdapter extends RecyclerView.Adapter<MyCompanyRecyclerViewAdapter.ViewHolder> {

    private final List<Company> companyList;
    private final List<Building> buildingList;
    private final OnListFragmentInteractionListener mListener;

    public MyCompanyRecyclerViewAdapter(List<Company> companies, List<Building> buildings, OnListFragmentInteractionListener listener) {
        companyList = companies;
        buildingList = buildings;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_company, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.company = companyList.get(position);
        holder.name.setText(holder.company.getName());
        holder.email.setText(holder.company.getEmail());
        holder.phoneNo.setText(Long.toString(holder.company.getPhone_no()));
        holder.doorOrRoom.setText(holder.company.getDoor_or_room());
        holder.floorNo.setText(Integer.toString(holder.company.getFloor_number()));
        int buildingId = holder.company.getBuilding_id();
        for(Building building: buildingList) {
            if(building.getBuilding_id() == buildingId)
                holder.building.setText(building.getName());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(companyList, "card", position, MyCompanyRecyclerViewAdapter.this, buildingList);
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(companyList, "delete", position, MyCompanyRecyclerViewAdapter.this, buildingList);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final AppCompatTextView name;
        public final AppCompatTextView email;
        public final AppCompatTextView phoneNo;
        public final AppCompatTextView doorOrRoom;
        public final AppCompatTextView floorNo;
        public final AppCompatTextView building;
        public final AppCompatButton delete;
        public Company company;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            name = view.findViewById(R.id.tv_companyName);
            email = view.findViewById(R.id.tv_companyEmail);
            phoneNo = view.findViewById(R.id.tv_companyPhoneNo);
            doorOrRoom = view.findViewById(R.id.tv_companyDoorOrRoom);
            floorNo = view.findViewById(R.id.tv_companyFloorNo);
            building = view.findViewById(R.id.tv_companyBuilding);

            delete = view.findViewById(R.id.btn_deleteCompany);
        }
    }
}
