package com.techcloud.isecurity.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.techcloud.isecurity.helpers.HolderClass;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.activities.ScanActivity;
import com.techcloud.isecurity.activities.CardActivity;


public class ShowImageFragment extends DialogFragment {

    private ImageView showImage;
    private ImageButton cancel;
    private ImageButton ok;

    public ShowImageFragment() {

    }

    public static ShowImageFragment newInstance(String title) {
        ShowImageFragment frag = new ShowImageFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.show_image, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showImage = view.findViewById(R.id.image_post_view);
        cancel = view.findViewById(R.id.btn_cancel);
        ok = view.findViewById(R.id.btn_ok);

        String title = getArguments().getString("title", "Photo Taken");
        getDialog().setTitle(title);
        showImage.setImageBitmap(HolderClass.theBitmap);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CardActivity.class);
                startActivity(intent);
                getDialog().dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                ((ScanActivity)getActivity()).startCam();
            }
        });
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }
}
