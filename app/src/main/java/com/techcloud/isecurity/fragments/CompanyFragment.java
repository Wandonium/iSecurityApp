package com.techcloud.isecurity.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techcloud.isecurity.helpers.HolderClass;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.adapters.MyCompanyRecyclerViewAdapter;
import com.techcloud.isecurity.models.Building;
import com.techcloud.isecurity.models.Company;
import com.techcloud.isecurity.models.Guard;
import com.techcloud.isecurity.server.ApiClient;
import com.techcloud.isecurity.server.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CompanyFragment extends Fragment {

    public static final String TAG = CompanyFragment.class.getSimpleName();

    private OnListFragmentInteractionListener mListener;
    private List<Company> companyList;
    private List<Building> buildingList;
    private ApiService apiService;
    private CompositeDisposable disposable;
    private RecyclerView recyclerView;
    private MyCompanyRecyclerViewAdapter adapter;

    private AlertDialog.Builder builder;
    private Dialog dialog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CompanyFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CompanyFragment newInstance() {
        CompanyFragment fragment = new CompanyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_company_list, container, false);

        companyList = new ArrayList<>();
        buildingList = new ArrayList<>();
        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        disposable = new CompositeDisposable();
        recyclerView = view.findViewById(R.id.listCompany);
        adapter = null;

        builder = new AlertDialog.Builder(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar);
        }
        dialog = builder.create();

        dialog.show();
        getBuildings(view);
        return view;
    }

    private void getBuildings(final View view) {
        disposable.add(apiService
                .getBuildings(Guard.jwtToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<JsonArray>() {
                    @Override
                    public void onSuccess(JsonArray jsonElements) {
                        Log.d(TAG, "Buildings Response: " + jsonElements);
                        for(Object object: jsonElements) {
                            JsonObject jsonObject = (JsonObject) object;
                            String city = jsonObject.get("city").toString();
                            int id = jsonObject.get("id").getAsInt();
                            float latitude = jsonObject.get("latitude").getAsFloat();
                            float longitude = jsonObject.get("longitude").getAsFloat();
                            String name = jsonObject.get("name").toString();
                            int no_of_floors = jsonObject.get("no_of_floors").getAsInt();
                            String street = jsonObject.get("street").getAsString();

                            city = city.replaceAll("^\"|\"$", "");
                            name = name.replaceAll("^\"|\"$", "");
                            street = street.replaceAll("^\"|\"$", "");

                            Building building = new Building(name, street, city, no_of_floors, longitude, latitude);
                            building.setBuilding_id(id);
                            buildingList.add(building);
                        }
                        getCompanies(view);
                    }

                    @Override
                    public void onError(Throwable e) {
                        HolderClass.showError(e, view);
                    }
                }));
    }

    private void getCompanies(final View view) {
        disposable.add(apiService
                .getCompanies(Guard.jwtToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<JsonArray>() {
                    @Override
                    public void onSuccess(JsonArray jsonElements) {
                        dialog.dismiss();
                        Log.d(TAG, "Response: " + jsonElements);
                        for(Object object: jsonElements) {
                            JsonObject jsonObject = (JsonObject) object;
                            int building_id = jsonObject.get("building_id").getAsInt();
                            String doorOrRoom = jsonObject.get("door_or_room").getAsString();
                            String email = jsonObject.get("email").toString();
                            int floorNo = jsonObject.get("floor_number").getAsInt();
                            long phone_no = jsonObject.get("phone_no").getAsLong();
                            String name = jsonObject.get("name").toString();
                            int id = jsonObject.get("id").getAsInt();
                            doorOrRoom = doorOrRoom.replaceAll("^\"|\"$", "");
                            email = email.replaceAll("^\"|\"$", "");
                            name = name.replaceAll("^\"|\"$", "");
                            Company company = new Company(name, email, doorOrRoom,floorNo, phone_no, building_id);
                            company.setCompany_id(id);
                            System.out.println(company);
                            companyList.add(company);
                        }

                        adapter = new MyCompanyRecyclerViewAdapter(companyList, buildingList, mListener);
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        HolderClass.showError(e, view);
                    }
                }));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(List<Company> companies, String btn, int position,
                                       MyCompanyRecyclerViewAdapter adapter, List<Building> buildings);
    }
}
