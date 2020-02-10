package com.techcloud.isecurity.adapters;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techcloud.isecurity.fragments.GuardFragment.OnListFragmentInteractionListener;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.models.Building;
import com.techcloud.isecurity.models.Guard;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Guard} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyGuardRecyclerViewAdapter extends RecyclerView.Adapter<MyGuardRecyclerViewAdapter.ViewHolder> {

    private final List<Guard> guardList;
    private final OnListFragmentInteractionListener mListener;
    private final List<Building> buildings;

    public MyGuardRecyclerViewAdapter(List<Guard> guards, OnListFragmentInteractionListener listener,
                                      List<Building> buildingList) {
        guardList = guards;
        mListener = listener;
        buildings = buildingList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_guard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mGuard = guardList.get(position);
        holder.name.setText(holder.mGuard.getGuard_name());
        holder.guardId.setText(Integer.toString(holder.mGuard.getGuardId()));
        holder.phoneNo.setText(Long.toString(holder.mGuard.getPhone_no()));
        holder.securityCompany.setText(holder.mGuard.getSecurity_company());
        int buildingId = holder.mGuard.getBuilding_id();
        for(Building building: buildings) {
            if(building.getBuilding_id() == buildingId)
                holder.building.setText(building.getName());
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(guardList, "card", position, MyGuardRecyclerViewAdapter.this, buildings);
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListFragmentInteraction(guardList, "delete", position, MyGuardRecyclerViewAdapter.this, buildings);
            }
        });
    }

    @Override
    public int getItemCount() {
        return guardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final AppCompatTextView name;
        public final AppCompatTextView guardId;
        public final AppCompatTextView phoneNo;
        public final AppCompatTextView building;
        public final AppCompatTextView securityCompany;
        public final AppCompatButton delete;
        public Guard mGuard;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            name = view.findViewById(R.id.tv_guardName);
            guardId = view.findViewById(R.id.tv_guardID);
            phoneNo = view.findViewById(R.id.tv_guardPhoneNo);
            building = view.findViewById(R.id.tv_guardBuilding);
            securityCompany = view.findViewById(R.id.tv_guardSecurityCompany);

            delete = view.findViewById(R.id.btn_deleteGuard);
        }

    }
}
