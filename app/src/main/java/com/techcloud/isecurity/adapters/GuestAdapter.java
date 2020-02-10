package com.techcloud.isecurity.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.techcloud.isecurity.R;
import com.techcloud.isecurity.models.Company;
import com.techcloud.isecurity.models.Guest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<Guest> guestList;
    private List<Guest> guestListFiltered;
    private List<Company> companyList;
    private GuestAdapterListener mListener;

    public interface GuestAdapterListener {
        void onGuestSelected(List<Guest> guests, int position, String btn, GuestAdapter adapter);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final AppCompatTextView guestId;
        public final AppCompatTextView name;
        public final AppCompatTextView phoneNo;
        public final AppCompatTextView gender;
        public final AppCompatTextView reasonForVisit;
        public final AppCompatTextView timeIn;
        public final AppCompatTextView timeOut;
        public final AppCompatTextView company;
        public final AppCompatTextView floorNo;

        public final AppCompatButton signIn;
        public final AppCompatButton signOut;



        public MyViewHolder(View view) {
            super(view);

            guestId = view.findViewById(R.id.tv_guestID);
            name = view.findViewById(R.id.tv_guestName);
            phoneNo = view.findViewById(R.id.tv_guestPhoneNo);
            gender = view.findViewById(R.id.tv_guestGender);
            reasonForVisit = view.findViewById(R.id.tv_guestReasonForVisit);
            timeIn = view.findViewById(R.id.tv_guestTimeIn);
            timeOut = view.findViewById(R.id.tv_guestTimeOut);
            company = view.findViewById(R.id.tv_guestCompany);
            floorNo = view.findViewById(R.id.tv_guestFloorNo);

            signIn = view.findViewById(R.id.btn_signInGuest);
            signOut = view.findViewById(R.id.btn_signOutGuest);

            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onGuestSelected(guestListFiltered, getAdapterPosition(), "signIn", GuestAdapter.this);
                }
            });

            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onGuestSelected(guestListFiltered, getAdapterPosition(), "signOut", GuestAdapter.this);
                }
            });
        }
    }

    public GuestAdapter(List<Guest> guests, List<Company> companies, Context theContext, GuestAdapterListener listener) {
        context = theContext;
        guestList = guests;
        guestListFiltered = guests;
        mListener = listener;
        companyList = companies;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.guest_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Guest guest = guestListFiltered.get(position);
        holder.guestId.setText(Integer.toString(guest.getGuestId()));
        holder.name.setText(guest.getFull_names());
        holder.phoneNo.setText(Long.toString(guest.getPhone_no()));
        holder.gender.setText(guest.getGender());
        holder.reasonForVisit.setText(guest.getReason_for_visit());
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date timeIn = null, timeOut = null;
        try {
            timeIn = formatter1.parse(guest.getTime_in());
            timeOut = guest.getTime_out().equals("null") ? null : formatter1.parse(guest.getTime_out());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String theTimeIn = formatter2.format(timeIn);
        String theTimeOut = timeOut == null? "Waiting for sign out" : formatter2.format(timeOut);
        holder.timeIn.setText(theTimeIn);
        holder.timeOut.setText(theTimeOut);
        int companyId = guest.getCompany_id();
        for(Company c: companyList) {
            if(c.getCompany_id() == companyId) {
                holder.company.setText(c.getName());
                holder.floorNo.setText(Integer.toString(c.getFloor_number()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return guestListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    guestListFiltered = guestList;
                } else {
                    List<Guest> filteredList = new ArrayList<>();
                    for (Guest row : guestList) {

                        if (row.getFull_names().toLowerCase().contains(charString.toLowerCase()) || Integer.toString(row.getGuestId()).contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    guestListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = guestListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                guestListFiltered = (ArrayList<Guest>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
