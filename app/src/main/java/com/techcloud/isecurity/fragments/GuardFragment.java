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
import com.techcloud.isecurity.adapters.MyGuardRecyclerViewAdapter;
import com.techcloud.isecurity.models.Building;
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
public class GuardFragment extends Fragment {

    public static final String TAG = GuardFragment.class.getSimpleName();

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<Guard> guardList;
    private List<Building> buildingList;
    private ApiService apiService;
    private CompositeDisposable disposable;
    private RecyclerView recyclerView;
    private MyGuardRecyclerViewAdapter adapter;

    private AlertDialog.Builder builder;
    private Dialog dialog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GuardFragment() {
    }

    public static GuardFragment newInstance() {
        GuardFragment fragment = new GuardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_guard_list, container, false);
        guardList = new ArrayList<>();
        buildingList = new ArrayList<>();
        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        disposable = new CompositeDisposable();
        recyclerView = view.findViewById(R.id.list_guard);
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
                        getGuards(view);
                    }

                    @Override
                    public void onError(Throwable e) {
                        HolderClass.showError(e, view);
                    }
                }));
    }

    private void getGuards(final View view) {
        disposable.add(apiService
                .getGuards(Guard.jwtToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<JsonArray>() {
                    @Override
                    public void onSuccess(JsonArray jsonElements) {
                        dialog.dismiss();
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

                        adapter = new MyGuardRecyclerViewAdapter(guardList, mListener, buildingList);
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
        void onListFragmentInteraction(List<Guard> guards, String btn, int position, MyGuardRecyclerViewAdapter adapter,
                                       List<Building> buildings);
    }
}
