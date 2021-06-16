//package com.example.Conekto.Activities;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.example.Conekto.R;
//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
//
//import java.io.IOException;
//
//public class ImageviewerActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.image_viewer);
//
//        final String url = getIntent().getStringExtra("imageUrl");
//
//        final ImageView image = findViewById(R.id.a_fullscreen_image);
//        final TextView message = findViewById(R.id.a_fullscreen_message);
//
//        message.setText("Loading Picture...");
//        message.setVisibility(View.VISIBLE);
//
//        Glide.with(getApplicationContext())
//                .load(url)
//                .into(image, new Callback() {
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//                        Glide.with(getApplicationContext())
//                                .load(url)
//                                .into((image), new Callback() {
//                                    @Override
//                                    public void onFailure(Request request, IOException e) {
//                                        message.setVisibility(View.VISIBLE);
//                                        message.setText("Error: Could not load picture.");
//                                    }
//
//                                    @Override
//                                    public void onResponse(Response response) throws IOException {
//                                        message.setVisibility(View.GONE);
//
//                                    }
//                                });
//                    }
//
//                    @Override
//                    public void onResponse(Response response) throws IOException {
//                        message.setVisibility(View.GONE);
//
//                    }
//                });
//
//    }
//
//}