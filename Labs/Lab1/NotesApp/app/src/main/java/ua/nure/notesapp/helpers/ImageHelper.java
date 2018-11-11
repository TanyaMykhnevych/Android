package ua.nure.notesapp.helpers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import java.io.InputStream;

import ua.nure.notesapp.R;

public class ImageHelper {
    public static void DrawImage(String currentImagePath, Activity context, ImageView vImageDisplay) {
        try {
            final Uri imageUri = Uri.parse(currentImagePath);

            context.grantUriPermission(context.getPackageName(), imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            final InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            vImageDisplay.setImageBitmap(selectedImage);
        } catch (Exception e) {
            vImageDisplay.setImageResource(R.drawable.note);
        }
    }
}
