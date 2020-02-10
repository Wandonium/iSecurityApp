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
import com.techcloud.isecurity.adapters.MyGuestRecyclerViewAdapter;
import com.techcloud.isecurity.models.Building;
import com.techcloud.isecurity.models.Company;
import com.techcloud.isecurity.models.Guard;
import com.techcloud.isecurity.models.Guest;
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
public class GuestFragment extends Fragment {

    public static final String TAG = GuestFragment.class.getSimpleName();

    private OnListFragmentInteractionListener mListener;
    private List<Company> companyList;
    private List<Building> buildingList;
    private List<Guest> guestList;
    private List<Guard> guardList;
    private ApiService apiService;
    private CompositeDisposable disposable;
    private RecyclerView recyclerView;
    private MyGuestRecyclerViewAdapter adapter;

    private AlertDialog.Builder builder;
    private Dialog dialog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GuestFragment() {
    }

    public static GuestFragment newInstance() {
        GuestFragment fragment = new GuestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_guest_list, container, false);

        companyList = new ArrayList<>();
        buildingList = new ArrayList<>();
        guestList = new ArrayList<>();
        guardList = new ArrayList<>();
        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        disposable = new CompositeDisposable();
        recyclerView = view.findViewById(R.id.list_guest);
        adapter = null;

        builder = new AlertDialog.Builder(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar);
        }
        dialog = builder.create();

        dialog.show();
        // get buildings
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
                    }

                    @Override
                    public void onError(Throwable e) {
                        HolderClass.showError(e, view);
                    }
                }));

        // get guards
        disposable.add(apiService
                .getGuards(Guard.jwtToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<JsonArray>() {
                    @Override
                    public void onSuccess(JsonArray jsonElements) {
                        Log.d(TAG, "Guard Response: " + jsonElements);
                        for(Object object: jsonElements) {
                            JsonObject guardResponse = (JsonObject) object;
                            int theGuardDBId = guardResponse.get("id").getAsInt();
                            //System.out.println("Guard ID: " + theGuardDBId);
                            int guardId = guardResponse.get("guardId").getAsInt();
                            String guard_name = guardResponse.get("guard_name").toString();
                            String password = guardResponse.get("password").toString();
                            long phone_no = guardResponse.get("phone_no").getAsLong();
                            String security_company = guardResponse.get("security_company").toString();
                            int building_id = guardResponse.get("building_id").getAsInt();
                            guard_name = guard_name.replaceAll("^\"|\"$", "");
                            password = password.replaceAll("^\"|\"$", "");
                            security_company = security_company.replaceAll("^\"|\"$", "");
                            Guard guard = new Guard(guardId, guard_name, phone_no, password, security_company, building_id);
                            guard.setGuard_db_id(theGuardDBId);
                            //System.out.println(guard);
                            guardList.add(guard);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        HolderClass.showError(e, view);
                    }
                }));

        // get companies
        disposable.add(apiService
                .getCompanies(Guard.jwtToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<JsonArray>() {
                    @Override
                    public void onSuccess(JsonArray jsonElements) {
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
                            //System.out.println(company);
                            companyList.add(company);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        HolderClass.showError(e, view);
                    }
                }));

        // get guests
        disposable.add(apiService
        .getGuests(Guard.jwtToken)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<JsonArray>() {
            @Override
            public void onSuccess(JsonArray jsonElements) {
                dialog.dismiss();
                Log.d(TAG, "Guest Response: " + jsonElements);
                for(Object object: jsonElements) {
                    JsonObject jsonObject = (JsonObject) object;
                    String name = jsonObject.get("full_names").getAsString();
                    String gender = jsonObject.get("gender").getAsString();
                    int guestId = jsonObject.get("guestId").getAsInt();
                    int id = jsonObject.get("id").getAsInt();
                    long phoneNo = jsonObject.get("phone_no").getAsLong();
                    String reason = jsonObject.get("reason_for_visit").getAsString();
                    String time_in = jsonObject.get("time_in").getAsString();
                    String time_out = jsonObject.get("time_out").isJsonNull() ? "null" : jsonObject.get("time_out").getAsString();
                    JsonObject guard = (JsonObject) jsonObject.getAsJsonArray("guards").get(0);
                    int guardId = guard.get("id").getAsInt();
                    JsonObject building = (JsonObject) jsonObject.getAsJsonArray("buildings").get(0);
                    int building_id = building.get("id").getAsInt();
                    JsonObject company = (JsonObject) jsonObject.getAsJsonArray("companies").get(0);
                    int company_id = company.get("id").getAsInt();

                    name = name.replaceAll("^\"|\"$", "");
                    gender = gender.replaceAll("^\"|\"$", "");
                    reason = reason.replaceAll("^\"|\"$", "");
                    time_in = time_in.replaceAll("^\"|\"$", "");
                    time_out = time_out.replaceAll("^\"|\"$", "");

                    Guest guest = new Guest(guestId, name, phoneNo, gender, reason, time_in, time_out, building_id,
                            company_id, guardId);
                    guest.setGuest_db_id(id);
                    System.out.println(guest);
                    guestList.add(guest);
                }
                adapter = new MyGuestRecyclerViewAdapter(guestList, buildingList, companyList, guardList, mListener);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(Throwable e) {
                dialog.dismiss();
                HolderClass.showError(e, view);
            }
        }));


        return view;
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
        void onListFragmentInteraction(List<Company> companies, List<Building> buildings, List<Guest> guests,
                                       String btn, int position, MyGuestRecyclerViewAdapter adapter);
    }
}
