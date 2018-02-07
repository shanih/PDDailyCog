package il.ac.pddailycogresearch.pddailycog.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class InstructionFragment extends Fragment {
    Unbinder unbinder;
   /* @BindView(R.id.buttonInstructionFragmentSound)
    Button buttonInstructionFragmentSound;*/

    private OnFragmentInteractionListener mListener;
    private MediaPlayer mpori;

    public InstructionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_instruction, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    private void initViews() {
       /* if (mpori==null||!mpori.isPlaying()) {
            buttonInstructionFragmentSound.setText(R.string.sound);
        } else {
            buttonInstructionFragmentSound.setText(R.string.stop);
        }*/
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.onInstructionFragmentAttach();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onInstructionFragmentDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

  /*  @OnClick(R.id.buttonInstructionFragmentSound)
    public void onViewClicked() {
        if (mpori==null||!mpori.isPlaying()) {
            mpori = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.trial_instrc_male_sound);
            mpori.start();
            buttonInstructionFragmentSound.setText(R.string.stop);
        } else {
            mpori.stop();
            buttonInstructionFragmentSound.setText(R.string.sound);

        }

        mListener.onSoundButtonClick();
    }*/

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
        void onSoundButtonClick();

        void onInstructionFragmentAttach();

        void onInstructionFragmentDetach();
    }
}
