package dev.jun0.dcalimi.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.item.QuizItem;
import dev.jun0.dcalimi.util.CustomTheme;
import dev.jun0.dcalimi.util.Post;
import dev.jun0.dcalimi.util.Quiz;

public class CreatePostActivity extends AppCompatActivity {
    public final static int CREATE_POST = 100;

    private final static int REQUEST_TAKE_PICTURE = 0;
    private final static int REQUEST_PICK_PICTURE = 1;
    private final static int PERMISSIONS_REQUEST_CAMERA = 10;

    private ImageView[] mImageViewsAttachedImage = new ImageView[2];
    private ImageButton[] mImageButtonsDetachImage = new ImageButton[2];
    private RelativeLayout[] mRelativeLayoutsAttachedImage = new RelativeLayout[2];
    private LinearLayout mLinerLayoutAttachedImages;

    private int mNumberOfAttachedImage = 0;
    private Bitmap[] mBitmapsAttachedImage = new Bitmap[2];

    private int mPostType = 1;

    private EditText mEditTextTitle;
    private EditText mEditTextUploader;
    private EditText mEditTextBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new CustomTheme(this).setThemeByPreference();

        setContentView(R.layout.activity_create_post);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            mPostType = bundle.getInt("type", Post.SUGGESTION);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if(mPostType == Post.NOTICE)
                actionBar.setTitle("공지사항 작성");
            else if(mPostType == Post.SUGGESTION)
                actionBar.setTitle("건의사항 작성");

            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mLinerLayoutAttachedImages = findViewById(R.id.linerLayoutAttachedImages);

        mImageViewsAttachedImage[0] = findViewById(R.id.imageViewAttachedImage1);
        mImageViewsAttachedImage[1] = findViewById(R.id.imageViewAttachedImage2);

        mImageButtonsDetachImage[0] = findViewById(R.id.imageButtonDetachImage1);
        mImageButtonsDetachImage[1] = findViewById(R.id.imageButtonDetachImage2);

        mRelativeLayoutsAttachedImage[0] = findViewById(R.id.relativeLayoutAttachedImage1);
        mRelativeLayoutsAttachedImage[1] = findViewById(R.id.relativeLayoutAttachedImage2);

        updateAttachedImagesVisibility();

        mImageButtonsDetachImage[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumberOfAttachedImage--;
                mBitmapsAttachedImage[0] = mBitmapsAttachedImage[1];
                mBitmapsAttachedImage[1] = null;

                mImageViewsAttachedImage[0].setImageBitmap(mBitmapsAttachedImage[0]);
                updateAttachedImagesVisibility();
            }
        });

        mImageButtonsDetachImage[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumberOfAttachedImage--;
                mBitmapsAttachedImage[1] = null;
                updateAttachedImagesVisibility();
            }
        });

        mEditTextBody = findViewById(R.id.editTextBody);
        mEditTextTitle = findViewById(R.id.editTextTitle);
        mEditTextUploader = findViewById(R.id.editTextUploader);
        if(mPostType == Post.SUGGESTION)
            mEditTextUploader.setVisibility(View.GONE);
        
        final TextView textViewCurrentNumberOfCharacter = findViewById(R.id.textViewCurrentNumberOfCharcter);
        ImageButton imageButtonAttachImageFromGallery = findViewById(R.id.imageButtonAttachImageFromGallery);
        ImageButton imageButtonAttachImageFromCamera = findViewById(R.id.imageButtonAttachImageFromCamera);

        mEditTextBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            String mStrCurrentLength = "0/2000";
            @Override
            public void afterTextChanged(Editable s) {
                mStrCurrentLength = s.length() + "/2000";
                textViewCurrentNumberOfCharacter.setText(mStrCurrentLength);
            }
        });

        imageButtonAttachImageFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNumberOfAttachedImage == 2){
                    new AlertDialog.Builder(CreatePostActivity.this)
                            .setMessage("이미지는 2개까지만 첨부할 수 있습니다.")
                            .setPositiveButton(android.R.string.ok, null)
                            .create().show();
                    return;
                }
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_PICK_PICTURE);
            }
        });

        imageButtonAttachImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNumberOfAttachedImage == 2){
                    new AlertDialog.Builder(CreatePostActivity.this)
                            .setMessage("이미지는 2개까지만 첨부할 수 있습니다.")
                            .setPositiveButton(android.R.string.ok, null)
                            .create().show();
                    return;
                }
                if (ContextCompat.checkSelfPermission(CreatePostActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreatePostActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA);
                }else {
                    takePicture();
                }
            }
        });

        if(mPostType == Post.SUGGESTION)
            new AlertDialog.Builder(this)
                    .setMessage("사용자를 식별할 수 있는 정보들이 함께 업로드 되오니 신중하게 작성하시기 바랍니다.")
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
    }

    private void updateAttachedImagesVisibility(){
        int firstImageVisibility = View.GONE;
        int secondImageVisibility = View.GONE;

        switch (mNumberOfAttachedImage){
            case 2:
                secondImageVisibility = View.VISIBLE;
            case 1:
                firstImageVisibility = View.VISIBLE;
                break;
        }

        mLinerLayoutAttachedImages.setVisibility(firstImageVisibility);

        mRelativeLayoutsAttachedImage[0].setVisibility(firstImageVisibility);
        mImageButtonsDetachImage[0].setVisibility(firstImageVisibility);
        mImageViewsAttachedImage[0].setVisibility(firstImageVisibility);

        mRelativeLayoutsAttachedImage[1].setVisibility(secondImageVisibility);
        mImageButtonsDetachImage[1].setVisibility(secondImageVisibility);
        mImageViewsAttachedImage[1].setVisibility(secondImageVisibility);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        if(Build.VERSION.SDK_INT < 28) {
           return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } else {
            ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uri);
            return ImageDecoder.decodeBitmap(source);
        }
    }

    Uri photoURI;
    private void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
                photoURI = FileProvider.getUriForFile(CreatePostActivity.this,
                        "dev.jun0.dcalimi.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    String currentPhotoPath;
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = "content:/"+image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    Toast.makeText(this, R.string.camera_permission_denied, Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(this, R.string.camera_permission_denied_permanently, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                }
            }
        }
    }

    private void getQuiz(){
        final Quiz quiz = new Quiz(getSupportFragmentManager(), this);
        quiz.get(new Quiz.OnDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(final QuizItem quizItem) {
                Log.i("CPA", "onDownloadComplete: " + quizItem.getQuestion());

                AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
                builder.setTitle(quizItem.getQuestion());
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quiz.checkAnswer(((AlertDialog)dialog).getListView().getCheckedItemPosition() + 1, new Quiz.OnAnswerCheckCompleteListener(){
                            @Override
                            public void onAnswerCorrect() {
                                Post.upload(getSupportFragmentManager(), CreatePostActivity.this,
                                        PreferenceManager.getDefaultSharedPreferences(CreatePostActivity.this).getString("enterCode" , null),
                                        mPostType, mEditTextTitle.getText().toString(), mEditTextUploader.getText().toString(), mEditTextBody.getText().toString(),
                                        mBitmapsAttachedImage[0], mBitmapsAttachedImage[1],
                                        new Post.OnPostUploadCompleteListener() {
                                    @Override
                                    public void onUploadComplete() {
                                        Toast.makeText(CreatePostActivity.this, "업로드 완료.", Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                            }

                            @Override
                            public void onAnswerIncorrect(int remainingAttempts) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
                                builder.setTitle("틀렸습니다!");
                                if(remainingAttempts > 0) {
                                    builder.setMessage(String.format(Locale.getDefault(), "남은 시도횟수: %d회", remainingAttempts));
                                    builder.setPositiveButton("다시 시도", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getQuiz();
                                        }
                                    });
                                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) { }
                                    });
                                }else {
                                    builder.setMessage("시도 횟수를 모두 사용하여 4일 간 게시글 작성이 제한됩니다.");
                                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) { }
                                    });
                                }
                                builder.show();
                            }

                            @Override
                            public void onCheckFailed() {

                            }
                        });
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setSingleChoiceItems(quizItem.getOptions(), -1, null);
                builder.show();
            }

            @Override
            public void onAttemptExceeded() {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
                builder.setTitle("알림");
                builder.setMessage("시도 횟수를 모두 사용하여 4일 간 게시글 작성이 제한된 상태입니다.");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
                builder.show();
            }

            @Override
            public void onDownloadFailed() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_PICTURE) {
                try {
                    if(data == null)
                        throw new NullPointerException();
                    Bitmap bitmapSelectedImage = getBitmapFromUri(data.getData());
                    mBitmapsAttachedImage[mNumberOfAttachedImage] = bitmapSelectedImage;
                    mImageViewsAttachedImage[mNumberOfAttachedImage].setImageBitmap(bitmapSelectedImage);
                    mNumberOfAttachedImage++;
                    updateAttachedImagesVisibility();
                } catch (NullPointerException | IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_TAKE_PICTURE) {
                try {
                    Bitmap bitmapSelectedImage = getBitmapFromUri(photoURI);
                    mBitmapsAttachedImage[mNumberOfAttachedImage] = bitmapSelectedImage;
                    mImageViewsAttachedImage[mNumberOfAttachedImage].setImageBitmap(bitmapSelectedImage);
                    mNumberOfAttachedImage++;
                    updateAttachedImagesVisibility();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }else if (item.getItemId() == R.id.send) {
            if(mEditTextTitle.getText().length() == 0)
                Toast.makeText(this, "제목을 입력하여 주십시오.", Toast.LENGTH_LONG).show();
            else if(mEditTextBody.getText().length() <= 15) {
                Toast.makeText(this, "내용이 너무 짧습니다.", Toast.LENGTH_LONG).show();
            }else{
                getQuiz();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
