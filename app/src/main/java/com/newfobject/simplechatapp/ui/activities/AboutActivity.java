package com.newfobject.simplechatapp.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.ui.fragments.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {
    @BindView(R.id.github_link)
    TextView githubLink;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setupToolBar(getString(R.string.about_activity_title));

        githubLink.setClickable(true);
        githubLink.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='https://github.com/newfobject/SimpleChatApp'> GitHub </a>";
        githubLink.setText(Html.fromHtml(text));

    }
}
