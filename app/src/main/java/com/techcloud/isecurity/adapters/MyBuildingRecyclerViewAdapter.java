package com.techcloud.isecurity.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techcloud.isecurity.fragments.BuildingFragment.OnListFragmentInteractionListener;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.models.Building;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Building} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyBuildingRecyclerViewAdapter extends RecyclerView.Adapter<MyBuildingRecyclerViewAdapter.ViewHolder> {

    private final List<Building> mBuildings;
    private final OnListFragmentInteractionListener mListener;

    public MyBuildingRecyclerViewAdapter(List<Building> buildings, OnListFragmentInteractionListener listener) {
        mBuildings = buildings;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_building, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mBuilding = mBuildings.get(position);
        holder.name.setText(mBuildings.get(position).getName());
        holder.street.setText(mBuildings.get(position).getStreet());
        holder.city.setText(mBuildings.get(position).getCity());
        holder.noOfFloors.setText(Integer.toString(mBuildings.get(position).getNo_of_floors()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(mBuildings, "card", position, MyBuildingRecyclerViewAdapter.this);
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mListener) {
                    mListener.onListFragmentInteraction(mBuildings, "delete", position, MyBuildingRecyclerViewAdapter.this);
                }
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mBuildings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final AppCompatTextView name;
        public final AppCompatTextView street;
        public final AppCompatTextView city;
        public final AppCompatTextView noOfFloors;
        public final AppCompatButton delete;
        public Building mBuilding;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.tv_buildingName);
            street = view.findViewById(R.id.tv_buildingStreet);
            city = view.findViewById(R.id.tv_buildingCity);
            noOfFloors = view.findViewById(R.id.tv_buildingNoOfFloors);
            delete = view.findViewById(R.id.btn_deleteBuilding);
        }

        @Override
        public String toString() {
            return super.toString() + "Name: " + name.getText() + "\nStreet: " + street.getText() +
                    "\nCity: " + city.getText() + "\nNo. of floors: " + noOfFloors.getText();
        }
    }
}
