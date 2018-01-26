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
 */
public class TextInputFragment extends Fragment {
    @BindView(R.id.EditTextInputFragment)
    EditText EditTextInputFragment;
    Unbinder unbinder;


    private OnFragmentInteractionListener mListener;
    private int previousTextInputLength;
    private long timeBeforeCharacter;
    private long startCurrentForegroundTime;

    public TextInputFragment() {
        // Required empty public constructor
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

    public void setTextToEditText(String text){
        EditTextInputFragment.setText(text);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        startCurrentForegroundTime = System.currentTimeMillis();
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        calcTime();
        mListener.onTextInputFragmentStop(timeBeforeCharacter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onTextInputFragmentDetach();
        mListener = null;
    }

    private void calcTime() {
        long addedTime=System.currentTimeMillis() - startCurrentForegroundTime;
        timeBeforeCharacter = timeBeforeCharacter +addedTime;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnTextChanged(value = R.id.EditTextInputFragment,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextInput(Editable editable) {
        calcTime();
        if (previousTextInputLength > editable.length())
            mListener.onCharacterDeleted(editable.toString());
        if (previousTextInputLength < editable.length())
           mListener.onCharacterAdded(editable.toString(),timeBeforeCharacter);
        previousTextInputLength = editable.length();
    }

    public interface OnFragmentInteractionListener {
        void onTextInputFragmentCreateView();
        void onCharacterAdded(String inputText, long timeBeforeCharacter);
        void onCharacterDeleted(String inputText);
        void onTextInputFragmentStop(long timeBeforeCharacter);
        void onTextInputFragmentDetach();
    }
}
