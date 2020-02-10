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
import com.techcloud.isecurity.models.Employee;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<Employee> employeeList;
    private List<Employee> employeeListFiltered;
    private List<Company> companyList;
    private EmployeeAdapterListener mListener;

    public interface EmployeeAdapterListener {
        void onEmployeeSelected(List<Employee> employees, int position, String btn, EmployeeAdapter adapter);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final AppCompatTextView empId;
        public final AppCompatTextView name;
        public final AppCompatTextView phoneNo;
        public final AppCompatTextView timeIn;
        public final AppCompatTextView timeOut;
        public final AppCompatTextView company;
        public final AppCompatTextView floorNo;

        public final AppCompatButton signIn;
        public final AppCompatButton signOut;



        public MyViewHolder(View view) {
            super(view);

            empId = view.findViewById(R.id.tv_empID);
            name = view.findViewById(R.id.tv_empName);
            phoneNo = view.findViewById(R.id.tv_empPhoneNo);
            timeIn = view.findViewById(R.id.tv_empTimeIn);
            timeOut = view.findViewById(R.id.tv_empTimeOut);
            company = view.findViewById(R.id.tv_empCompany);
            floorNo = view.findViewById(R.id.tv_empFloorNo);

            signIn = view.findViewById(R.id.btn_signInEmp);
            signOut = view.findViewById(R.id.btn_signOutEmp);

            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onEmployeeSelected(employeeListFiltered, getAdapterPosition(), "signIn", EmployeeAdapter.this);
                }
            });

            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onEmployeeSelected(employeeListFiltered, getAdapterPosition(), "signOut", EmployeeAdapter.this);
                }
            });
        }
    }

    public EmployeeAdapter(List<Employee> employees, List<Company> companies, Context theContext, EmployeeAdapterListener listener) {
        context = theContext;
        employeeList = employees;
        employeeListFiltered = employees;
        mListener = listener;
        companyList = companies;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.emp_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Employee employee = employeeListFiltered.get(position);
        holder.empId.setText(Integer.toString(employee.getEmployeeId()));
        holder.name.setText(employee.getName());
        holder.phoneNo.setText(Long.toString(employee.getPhone_no()));
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date timeIn = null, timeOut = null;
        try {
            timeIn = formatter1.parse(employee.getTime_in());
            timeOut = employee.getTime_out().equals("null") ? null : formatter1.parse(employee.getTime_out());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String theTimeIn = formatter2.format(timeIn);
        String theTimeOut = timeOut == null? "Waiting for sign out" : formatter2.format(timeOut);
        holder.timeIn.setText(theTimeIn);
        holder.timeOut.setText(theTimeOut);
        int companyId = employee.getCompany_id();
        for(Company c: companyList) {
            if(c.getCompany_id() == companyId) {
                holder.company.setText(c.getName());
                holder.floorNo.setText(Integer.toString(c.getFloor_number()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return employeeListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    employeeListFiltered = employeeList;
                } else {
                    List<Employee> filteredList = new ArrayList<>();
                    for (Employee row : employeeList) {

                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || Integer.toString(row.getEmployeeId()).contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    employeeListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = employeeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                employeeListFiltered = (ArrayList<Employee>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
