package com.example.database;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.database.models.Post;
import com.example.database.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends BaseActivity {
	private DatabaseReference mDatabase;
	private EditText mTitleField, mBodyField, mPrice;
	private FloatingActionButton mSubmitButton;
	DatabaseReference reference;
	private static final int PICK_IMAGE = 1;
	Button choose;
	TextView alert;
	FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
	FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

	ArrayList<Uri> FileList = new ArrayList<Uri>();
	private Uri FileUri;
	private ProgressDialog progressDialog;
	private int upload_count = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_post);


		alert = findViewById(R.id.alert);
		choose = findViewById(R.id.chooser);
		progressDialog = new ProgressDialog(this);


		choose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {


				choosephoto();
			}
		});

		mTitleField = findViewById(R.id.field_title);
		mBodyField = findViewById(R.id.field_body);
		mSubmitButton = findViewById(R.id.fab_submit_post);
		mPrice = findViewById(R.id.field_price);

		mDatabase = FirebaseDatabase.getInstance().getReference();



		mSubmitButton.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("SetTextI18n")
			@Override
			public void onClick(View view) {
				submitPost();
				progressDialog.show();
				alert.setText("If Loading Takes too long please Press the button again");

				StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("FileFolder");


				for(upload_count = 0; upload_count < FileList.size(); upload_count++){


					Uri IndividualFile = FileList.get(upload_count);
					final StorageReference ImageName = ImageFolder.child("Image"+IndividualFile.getLastPathSegment());



					ImageName.putFile(IndividualFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
						@Override
						public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

							ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
								@Override
								public void onSuccess(Uri uri) {
									String url = String.valueOf(uri);
									StoreLink(url);


								}
							});







						}
					});



				}





			}
		});




	}

	private void StoreLink(String url) {


		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("UserOne");

		HashMap<String,String> hashMap = new HashMap<>();
		hashMap.put("Filelink",url);


		databaseReference.push().setValue(hashMap);

		progressDialog.dismiss();
		alert.setText("File Uploaded Successfully");
		//upload.setVisibility(View.GONE);
		FileList.clear();
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
	}


	@SuppressLint("SetTextI18n")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == PICK_IMAGE){

			if(resultCode == RESULT_OK){

				if(data.getClipData() != null){



					int countClipData = data.getClipData().getItemCount();



					int currentImageSelect = 0;

					while (currentImageSelect < countClipData){


						FileUri = data.getClipData().getItemAt(currentImageSelect).getUri();

						FileList.add(FileUri);

						currentImageSelect = currentImageSelect +1;


					}

					alert.setVisibility(View.VISIBLE);
					alert.setText("You Have Selected "+ FileList.size() +" Images");
					choose.setVisibility(View.GONE);


				}else{


					Toast.makeText(this, "Please Select Multiple File", Toast.LENGTH_SHORT).show();
				}


			}


		}


	}


	private boolean validateForm(String title, String body, String price) {
		if (TextUtils.isEmpty(title)) {
			mTitleField.setError(getString(R.string.required));
			return false;
		} else if (TextUtils.isEmpty(body)) {
			mBodyField.setError(getString(R.string.required));
			return false;
		} else if (TextUtils.isEmpty(price)) {
			mPrice.setError(getString(R.string.required));
			return false;
		} else {
			mTitleField.setError(null);
			mBodyField.setError(null);
			mPrice.setError(null);
			return true;
		}
	}

	private void choosephoto() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		startActivityForResult(intent, PICK_IMAGE);

	}

	private void submitPost() {
		final String title = mTitleField.getText().toString().trim();
		final String body = mBodyField.getText().toString().trim();
		final String userId = getUid();
		final String price = mPrice.getText().toString().trim();


		if (validateForm(title, body, price)) {
			// Disable button so there are no multi-posts
			setEditingEnabled(false);
			mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					User user = dataSnapshot.getValue(User.class);
					if (user == null) {
						Toast.makeText(NewPostActivity.this, "Error: could not fetch user.", Toast.LENGTH_LONG).show();
					} else {
						writeNewPost(userId, user.username, title, body, price);


					}
					setEditingEnabled(true);

				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					setEditingEnabled(true);
					Toast.makeText(NewPostActivity.this, "onCancelled: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	private void setEditingEnabled(boolean enabled) {
		mTitleField.setEnabled(enabled);
		mBodyField.setEnabled(enabled);
		if (enabled) {
			mSubmitButton.setVisibility(View.VISIBLE);
		} else {
			mSubmitButton.setVisibility(View.GONE);
		}
	}

	private void writeNewPost(String userId, String username, String title, String body, String price) {
		// Create new post at /user-posts/$userid/$postid
		// and at /posts/$postid simultaneously


		String key = mDatabase.child("posts").push().getKey();
		Post post = new Post(userId, username, title, body, price);
		Map<String, Object> postValues = post.toMap();
		Map<String, Object> childUpdates = new HashMap<>();
		childUpdates.put("/posts/" + key, postValues);
		childUpdates.put("/user-posts/" + userId + "/" + key, postValues);


		mDatabase.updateChildren(childUpdates);
		reference = FirebaseDatabase.getInstance().getReference();

	}


		}










