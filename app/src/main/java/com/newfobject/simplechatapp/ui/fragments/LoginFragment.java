package com.newfobject.simplechatapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.ui.activities.LoginActivity;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginFragment extends Fragment {
    private LoginActivity loginActivity;
    @BindView(R.id.email)
    EditText emailEditText;
    @BindView(R.id.password)
    EditText passwordEditText;


    public static LoginFragment newInstance() {

//        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof LoginActivity) {
            loginActivity = (LoginActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);

        if (loginActivity != null)
            loginActivity.setupToolBar(getString(R.string.app_name), false);

        return rootView;
    }

    @OnClick(R.id.create_account)
    public void onCreateAccountClick() {
        if (loginActivity != null)
            loginActivity.addCreateAccountFragment();
    }

    @OnClick(R.id.email_sign_in_button)
    public void signInWithEmailAndPassword() {
        if (isEmailCorrect() && isPasswordCorrect())
            signInWithEmailAndPassword(emailEditText.getText().toString(),
                    passwordEditText.getText().toString());
    }

    private boolean isPasswordCorrect() {
        String target = passwordEditText.getText().toString();
        if (target.length() < 5) {
            passwordEditText.setError(getString(R.string.password_short_error));
            return false;
        }
        return true;
    }

    private boolean isEmailCorrect() {
        String target = emailEditText.getText().toString();
        if (target.length() < 5) {
            emailEditText.setError(getString(R.string.email_field_is_not_valid));
            emailEditText.requestFocus();
            return false;
        }

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);

        if (!pattern.matcher(target).matches()) {
            emailEditText.setError(getString(R.string.email_field_is_not_valid));
            emailEditText.requestFocus();
            return false;
        }

        return true;
    }

    public void signInWithEmailAndPassword(String email, String password) {
        if (loginActivity != null)
            loginActivity.getAuth()
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                onLoginFailed(task.getException());
                            }
                        }
                    });
    }

    @OnClick(R.id.google_sign_in)
    public void googleSignIn() {
        if (loginActivity != null) {
            loginActivity.googleSignIn();
        }
    }

    private void onLoginFailed(Exception exception) {
        if (exception == null) {
            Toast.makeText(getContext(), getString(R.string.sign_in_failed),
                    Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(getContext(), getString(R.string.sign_in_failed)
                + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDetach() {
        loginActivity = null;
        super.onDetach();
    }
}
