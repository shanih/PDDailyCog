package il.ac.pddailycogresearch.pddailycog.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

/**
 * Created by שני on 08/01/2018.
 */

public final class ImageUtils {
    private static final String TAG = "ImageUtils";
    public static final String IMAGE_ABSOLUTE_PATH = "image_absolute_path";

    public static String lastTakenImageAbsolutePath; //TODO ask Tal for better sulotion

    private ImageUtils() {
        // This utility class is not publicly instantiable
    }
    private static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = CommonUtils.getTimeStamp();
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public static Intent createTakePictureIntent(Context context) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            //TODO error handling
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "il.ac.pddailycogresearch.pddailycog.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                lastTakenImageAbsolutePath=photoFile.getAbsolutePath();
                //so the activity can know the absolute path
                takePictureIntent.putExtra(IMAGE_ABSOLUTE_PATH, photoFile.getAbsolutePath());
               return takePictureIntent;
            }
        }
        return null;
    }

    public static void setPic(ImageView mImageView, String mCurrentPhotoPath, int targetH, int targetW) {

       // targetH = mImageView.getMeasuredHeight();//.getHeight();
       // targetW = mImageView.getMeasuredWidth();//.getWidth();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    public static void deleteFiles(Context context) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }
    }
}
