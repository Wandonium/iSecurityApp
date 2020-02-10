package com.techcloud.isecurity.adapters;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techcloud.isecurity.fragments.GuestFragment.OnListFragmentInteractionListener;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.models.Building;
import com.techcloud.isecurity.models.Company;
import com.techcloud.isecurity.models.Guard;
import com.techcloud.isecurity.models.Guest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.techcloud.isecurity.models.Guest} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyGuestRecyclerViewAdapter extends RecyclerView.Adapter<MyGuestRecyclerViewAdapter.ViewHolder> {

    private final List<Guest> guestList;
    private final List<Company> companyList;
    private final List<Building> buildingList;
    private final List<Guard> guardList;
    private final OnListFragmentInteractionListener mListener;

    public MyGuestRecyclerViewAdapter(List<Guest> guests,
            List<Building> buildings,
            List<Company> companies,
            List<Guard> guards, OnListFragmentInteractionListener listener) {
        guestList = guests;
        buildingList = buildings;
        companyList = companies;
        guardList = guards;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_guest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.guest = guestList.get(position);
        holder.guestId.setText(Integer.toString(holder.guest.getGuestId()));
        holder.name.setText(holder.guest.getFull_names());
        holder.phoneNo.setText(Long.toString(holder.guest.getPhone_no()));
        holder.gender.setText(holder.guest.getGender());
        holder.reasonForVisit.setText(holder.guest.getReason_for_visit());
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date timeIn = null, timeOut = null;
        try {
            timeIn = formatter1.parse(holder.guest.getTime_in());
            timeOut = holder.guest.getTime_out().equals("null") ? null : formatter1.parse(holder.guest.getTime_out());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String theTimeIn = formatter2.format(timeIn);
        String theTimeOut = timeOut == null? "Waiting for sign out" : formatter2.format(timeOut);
        holder.timeIn.setText(theTimeIn);
        holder.timeOut.setText(theTimeOut);
        int buildingId = holder.guest.getBuilding_id();
        for(Building building: buildingList) {
            if(building.getBuilding_id() == buildingId)
                holder.building.setText(building.getName());
        }
        int companyId = holder.guest.getCompany_id();
        for(Company c: companyList) {
            if(c.getCompany_id() == companyId)
                holder.company.setText(c.getName());
        }
        int guardId = holder.guest.getGuard_id();
        for(Guard guard: guardList) {
            if(guard.getGuard_db_id() == guardId)
                holder.guard.setText(guard.getGuard_name());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(companyList, buildingList, guestList, "card", position, MyGuestRecyclerViewAdapter.this);
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(companyList, buildingList, guestList, "delete", position, MyGuestRecyclerViewAdapter.this);
                }
            }
        });

        holder.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(companyList, buildingList, guestList, "signIn", position, MyGuestRecyclerViewAdapter.this);
                }
            }
        });

        holder.signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(companyList, buildingList, guestList, "signOut", position, MyGuestRecyclerViewAdapter.this);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return guestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final AppCompatTextView guestId;
        public final AppCompatTextView name;
        public final AppCompatTextView phoneNo;
        public final AppCompatTextView gender;
        public final AppCompatTextView reasonForVisit;
        public final AppCompatTextView timeIn;
        public final AppCompatTextView timeOut;
        public final AppCompatTextView company;
        public final AppCompatTextView building;
        public final AppCompatTextView guard;

        public final AppCompatButton delete;
        public final AppCompatButton signIn;
        public final AppCompatButton signOut;
        public Guest guest;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            guestId = view.findViewById(R.id.tv_guestID);
            name = view.findViewById(R.id.tv_guestName);
            phoneNo = view.findViewById(R.id.tv_guestPhoneNo);
            gender = view.findViewById(R.id.tv_guestGender);
            reasonForVisit = view.findViewById(R.id.tv_guestReasonForVisit);
            timeIn = view.findViewById(R.id.tv_guestTimeIn);
            timeOut = view.findViewById(R.id.tv_guestTimeOut);
            company = view.findViewById(R.id.tv_guestCompany);
            building = view.findViewById(R.id.tv_guestBuilding);
            guard = view.findViewById(R.id.tv_guestGuard);

            delete = view.findViewById(R.id.btn_deleteGuest);
            signIn = view.findViewById(R.id.btn_signInGuest);
            signOut = view.findViewById(R.id.btn_signOutGuest);
        }

    }
}
