package com.sandeep.socialmediaintegration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import is.arontibo.library.ElasticDownloadView;

public class NextFB extends AppCompatActivity {

    ElasticDownloadView elasticDownloadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_fb);
        elasticDownloadView = findViewById(R.id.progress_bar);

        getBtn().setOnClickListener(view -> {
            getBtn().setVisibility(View.GONE);
            elasticDownloadView.setVisibility(View.VISIBLE);
            elasticDownloadView.startIntro();
            elasticDownloadView.setProgress(100);
//        elasticDownloadView.fail();
            elasticDownloadView.success();
        });
    }

    private Button getBtn() {
        return findViewById(R.id.done);
    }
}
