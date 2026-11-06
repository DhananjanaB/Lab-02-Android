
package com.example.uidesignlab;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HeaderFragment extends Fragment {

    private static final String TAG = "LifecycleTracker";

    // Interface
    public interface OnHeaderInteractionListener {
        void onButtonClicked(String data);
    }

    // Interface reference
    private OnHeaderInteractionListener mListener;

    public HeaderFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d(TAG, "Header onAttach");

        // Check whether Activity implements the interface
        if (context instanceof OnHeaderInteractionListener) {
            mListener = (OnHeaderInteractionListener) context;
        } else {
            throw new RuntimeException(
                    context.toString()
                            + " must implement OnHeaderInteractionListener"
            );
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "Header onCreateView");

        View view = inflater.inflate(
                R.layout.fragment_header,
                container,
                false
        );

        // Find Button
        Button btnHeader = view.findViewById(R.id.btnHeader);

        // Button click event
        btnHeader.setOnClickListener(v -> {

            if (mListener != null) {
                mListener.onButtonClicked("Hello from Header!");
            }

        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "Header onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }
}
