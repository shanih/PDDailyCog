package il.ac.pddailycogresearch.pddailycog.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.ImageUtils;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TakePictureFragment extends Fragment {
    private static final String IMG_ANSOLUTE_PATH_TAG = "img_absolute_path";
    private static final String IMG_URI_TAG = "img_uri";
    @BindView(R.id.imageViewTakePictureFragment)
    ImageView imageViewTakePictureFragment;
    @BindView(R.id.buttonTakePictureFragment)
    Button buttonTakePictureFragment;
    Unbinder unbinder;

    private OnFragmentInteractionListener mListener;


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String imgAbsolutePath;
    private Uri imgUri;
    private static int imageViewHeight;
    private static int imageViewWidth;


    public TakePictureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_take_picture, container, false);
        if(savedInstanceState!=null) {
            imgAbsolutePath = savedInstanceState.getString(IMG_ANSOLUTE_PATH_TAG);
            imgUri = (Uri) savedInstanceState.getParcelable(IMG_URI_TAG);
        }
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.post(new Runnable() {
            @Override
            public void run() {
                mListener.onTakePictureFragmentViewCreated(TakePictureFragment.this);
                imageViewHeight=imageViewTakePictureFragment.getHeight();
                imageViewWidth = imageViewTakePictureFragment.getWidth();
            }
        });


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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(IMG_ANSOLUTE_PATH_TAG,imgAbsolutePath);
        outState.putParcelable(IMG_URI_TAG,imgUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onTakePictureFragmentDetach();
        mListener = null;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.buttonTakePictureFragment)
    public void onViewClicked() {
        //  mListener.onTakePictureFragmentViewCreated();
        dispatchTakePictureIntent(imageViewTakePictureFragment);
    }

    public void dispatchTakePictureIntent(ImageView imageView) {
        Intent takePictureIntent = ImageUtils.createTakePictureIntent(getContext());
        imgAbsolutePath = takePictureIntent.getStringExtra(ImageUtils.IMAGE_ABSOLUTE_PATH);
        Bundle extras = takePictureIntent.getExtras();
        imgUri = (Uri) extras.get(MediaStore.EXTRA_OUTPUT);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                setImageToView(imgAbsolutePath);
                mListener.onPictureBeenTaken(imgUri.toString());
            } //else
              //  imageViewTakePictureFragment.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));

        }
    }

    //let the activity put image from its data
    public void setLastTakenImageToView() {
        if (ImageUtils.lastTakenImageAbsolutePath != null) {
            setImageToView(ImageUtils.lastTakenImageAbsolutePath);
        }
    }

    private void setImageToView(String absolutePath) {
//        DisplayMetrics metrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        int screenHeight = metrics.heightPixels;
//        int imageHeight = (int) Math.round(screenHeight * Consts.IMAGEVIEW_HEIGHT_PERCENTAGE);
//        int imageWidth = metrics.widthPixels;
     //   imageViewTakePictureFragment.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth, imageHeight));
        ImageUtils.setPic(imageViewTakePictureFragment, absolutePath, imageViewHeight, imageViewWidth);

        buttonTakePictureFragment.setText(R.string.re_take_picture);
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

        void onPictureBeenTaken(String imgUri);

        void onTakePictureFragmentViewCreated(TakePictureFragment context);

        void onTakePictureFragmentDetach();
    }
}
