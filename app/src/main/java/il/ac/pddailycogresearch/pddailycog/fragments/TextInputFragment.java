package il.ac.pddailycogresearch.pddailycog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TextInputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextInputFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.EditTextInputFragment)
    EditText EditTextInputFragment;
    Unbinder unbinder;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private int previousTextInputLength;

    public TextInputFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TextInputFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TextInputFragment newInstance(String param1, String param2) {
        TextInputFragment fragment = new TextInputFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text_input, container, false);
        unbinder = ButterKnife.bind(this, view);
        mListener.onTextInputFragmentCreateView();
        return view;
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

    @OnTextChanged(value = R.id.EditTextInputFragment,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextInput(Editable editable) {
        if (previousTextInputLength > editable.length())
            mListener.onCharacterDeleted(editable.toString());
        if (previousTextInputLength < editable.length())
           mListener.onCharacterAdded(editable.toString());
        previousTextInputLength = editable.length();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onTextInputFragmentCreateView();
        void onCharacterAdded(String inputText);
        void onCharacterDeleted(String inputText);
    }
}
