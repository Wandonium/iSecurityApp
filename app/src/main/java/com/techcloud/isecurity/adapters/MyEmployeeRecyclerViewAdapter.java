package com.techcloud.isecurity.adapters;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techcloud.isecurity.fragments.EmployeeFragment.OnListFragmentInteractionListener;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.models.Building;
import com.techcloud.isecurity.models.Company;
import com.techcloud.isecurity.models.Employee;
import com.techcloud.isecurity.models.Guard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.techcloud.isecurity.models.Employee} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyEmployeeRecyclerViewAdapter extends RecyclerView.Adapter<MyEmployeeRecyclerViewAdapter.ViewHolder> {

    private final List<Employee> employeeList;
    private final List<Guard> guardList;
    private final List<Company> companyList;
    private final List<Building> buildingList;
    private final OnListFragmentInteractionListener mListener;

    public MyEmployeeRecyclerViewAdapter(List<Employee> employees,
            List<Guard> guards,
            List<Company> companies,
            List<Building> buildings, OnListFragmentInteractionListener listener) {
        employeeList = employees;
        guardList = guards;
        companyList = companies;
        buildingList = buildings;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.employee = employeeList.get(position);
        holder.empId.setText(Integer.toString(holder.employee.getEmployeeId()));
        holder.name.setText(holder.employee.getName());
        holder.phoneNo.setText(Long.toString(holder.employee.getPhone_no()));
        holder.role.setText(holder.employee.getRole());
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date timeIn = null, timeOut = null;
        try {
            timeIn = formatter1.parse(holder.employee.getTime_in());
            timeOut = holder.employee.getTime_out().equals("null") ? null : formatter1.parse(holder.employee.getTime_out());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String theTimeIn = formatter2.format(timeIn);
        String theTimeOut = timeOut == null? "Waiting for sign out" : formatter2.format(timeOut);
        holder.timeIn.setText(theTimeIn);
        holder.timeOut.setText(theTimeOut);
        int buildingId = holder.employee.getBuilding_id();
        for(Building building: buildingList) {
            if(building.getBuilding_id() == buildingId)
                holder.building.setText(building.getName());
        }
        int companyId = holder.employee.getCompany_id();
        for(Company c: companyList) {
            if(c.getCompany_id() == companyId)
                holder.company.setText(c.getName());
        }
        int guardId = holder.employee.getGuard_id();
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
                    mListener.onListFragmentInteraction(companyList, guardList, buildingList,
                            employeeList, MyEmployeeRecyclerViewAdapter.this, "card", position);
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(companyList, guardList, buildingList,
                            employeeList, MyEmployeeRecyclerViewAdapter.this, "delete", position);
                }
            }
        });

        holder.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(companyList, guardList, buildingList,
                            employeeList, MyEmployeeRecyclerViewAdapter.this, "signIn", position);
                }
            }
        });

        holder.signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(companyList, guardList, buildingList,
                            employeeList, MyEmployeeRecyclerViewAdapter.this, "signOut", position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final AppCompatTextView empId;
        public final AppCompatTextView name;
        public final AppCompatTextView phoneNo;
        public final AppCompatTextView role;
        public final AppCompatTextView timeIn;
        public final AppCompatTextView timeOut;
        public final AppCompatTextView company;
        public final AppCompatTextView building;
        public final AppCompatTextView guard;

        public final AppCompatButton delete;
        public final AppCompatButton signIn;
        public final AppCompatButton signOut;
        public Employee employee;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            empId = view.findViewById(R.id.tv_empID);
            name = view.findViewById(R.id.tv_empName);
            phoneNo = view.findViewById(R.id.tv_empPhoneNo);
            role = view.findViewById(R.id.tv_empRole);
            timeIn = view.findViewById(R.id.tv_empTimeIn);
            timeOut = view.findViewById(R.id.tv_empTimeOut);
            company = view.findViewById(R.id.tv_empCompany);
            building = view.findViewById(R.id.tv_empBuilding);
            guard = view.findViewById(R.id.tv_empGuard);

            delete = view.findViewById(R.id.btn_deleteEmp);
            signIn = view.findViewById(R.id.btn_signInEmp);
            signOut = view.findViewById(R.id.btn_signOutEmp);

        }
    }
}
