package il.ac.pddailycogresearch.pddailycog.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RatingFragment extends Fragment {
    @BindView(R.id.radioGroupRatingFragment)
    RadioGroup radioGroupRatingFragment;
    Unbinder unbinder;
    private OnFragmentInteractionListener mListener;
    private int selection;

    public RatingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        unbinder = ButterKnife.bind(this, view);
        mListener.onRatingFragmentCraeteView();
        initViews();
        return view;
    }

    private void initViews() {
        View.OnClickListener radioButtonsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                int radioButtonID = radioGroupRatingFragment.getCheckedRadioButtonId();
                View radioButton = radioGroupRatingFragment.findViewById(radioButtonID);*/
                int idx = radioGroupRatingFragment.indexOfChild(v);
                mListener.onRatingChanged(idx + 1);
            }
        };

        for (int i = 1; i <= 5; i++) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(String.valueOf(i));
            rb.setOnClickListener(radioButtonsListener);
            radioGroupRatingFragment.addView(rb);
            if (i == selection)
                radioGroupRatingFragment.check(rb.getId());
        }
        radioGroupRatingFragment.setOrientation(LinearLayout.HORIZONTAL);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setRatingSelection(int resultRating) {
        selection = resultRating;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onRatingFragmentCraeteView();

        void onRatingChanged(int rating);
    }
}
