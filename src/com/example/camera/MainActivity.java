package com.example.camera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity {
	// Activity request codes
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	@SuppressWarnings("unused")
	private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

	private Uri fileUri; // file url to store image/video

	private ImageView imgPreview;
	private VideoView videoPreview;
	private Button btnCapturePicture, btnRecordVideo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imgPreview = (ImageView) findViewById(R.id.imgPreview);
		videoPreview = (VideoView) findViewById(R.id.videoPreview);
		btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);
		btnRecordVideo = (Button) findViewById(R.id.btnRecordVideo);
		btnCapturePicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				captureImage();

			}

		});
		btnRecordVideo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				recordVideo();

			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

	}

	private void recordVideo() {
		try {
			// hide image preview
			imgPreview.setVisibility(View.GONE);

			videoPreview.setVisibility(View.VISIBLE);
			videoPreview.setVideoPath(fileUri.getPath());
			// start playing
			videoPreview.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Uri getOutputMediaFileUri(int type) {

		return Uri.fromFile(getOutputMediaFile(type));
	}

	private File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}

		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (requestCode == RESULT_OK) {
				previewCaptureImage();

			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(),
						"Sorry! User canceled The Camera ...",
						Toast.LENGTH_SHORT).show();

			}

		} else {
			Toast.makeText(getApplicationContext(),
					"Sorry! Failed To capture Image ...", Toast.LENGTH_SHORT)
					.show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void previewCaptureImage() {
		try {
			// hide video preview
			videoPreview.setVisibility(View.GONE);

			imgPreview.setVisibility(View.VISIBLE);

			// bimatp factory
			BitmapFactory.Options options = new BitmapFactory.Options();

			// downsizing image as it throws OutOfMemory Exception for larger
			// images
			options.inSampleSize = 8;

			final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
					options);

			imgPreview.setImageBitmap(bitmap);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		outState.putParcelable("file_uri", fileUri);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
		fileUri = savedInstanceState.getParcelable("file_uri");
	}

}
