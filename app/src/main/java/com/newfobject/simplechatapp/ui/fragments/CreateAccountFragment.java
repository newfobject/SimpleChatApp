package com.newfobject.simplechatapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.ui.activities.LoginActivity;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CreateAccountFragment extends Fragment {
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;

    private FirebaseAuth auth;
    private LoginActivity loginActivity;

    public static CreateAccountFragment newInstance() {

        Bundle args = new Bundle();

        CreateAccountFragment fragment = new CreateAccountFragment();
        fragment.setArguments(args);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_account, container, false);
        ButterKnife.bind(this, rootView);

        if (loginActivity != null)
            loginActivity.setupToolBar(getString(R.string.app_name), true);

        auth = FirebaseAuth.getInstance();

        return rootView;
    }

    @OnClick(R.id.create_account_button)
    public void onCreateAccount() {
        if (isNameCorrect() && isEmailCorrect() && isPasswordCorrect()) {
            final String userName = name.getText().toString();
            final String userEmail = email.getText().toString();
            final String userPassword = password.getText().toString();
            auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            onAccountCreateResult(task, userName, userEmail);
                        }
                    });
        }
    }

    private void onAccountCreateResult(Task<AuthResult> task, String userName, String userEmail) {
        if (task.isSuccessful()) {
            Toast.makeText(getContext(), R.string.account_created, Toast.LENGTH_LONG).show();
            loginActivity.firstSigningWithEmailAndPassword(userName, userEmail);

        } else {
            String errorMessage = "";
            if (task.getException() != null) {
                errorMessage = task.getException().getLocalizedMessage();
            }

            Toast.makeText(getContext(), getString(R.string.account_creation_failed) + errorMessage,
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isPasswordCorrect() {
        String target = password.getText().toString();
        if (target.length() < 5) {
            password.setError(getString(R.string.password_short_error));
            return false;
        }
        return true;
    }

    private boolean isEmailCorrect() {
        String target = email.getText().toString();
        if (target.length() < 5) {
            email.setError(getString(R.string.email_field_is_not_valid));
            return false;
        }

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);

        if (!pattern.matcher(target).matches()) {
            email.setError(getString(R.string.email_field_is_not_valid));
            return false;
        }

        return true;
    }

    private boolean isNameCorrect() {
        String target = name.getText().toString();
        if (target.length() < 3) {
            name.setError(getString(R.string.name_is_too_short));
            return false;
        }

        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

        if (pattern.matcher(target).find()) {
            name.setError(getString(R.string.name_only_lettes_numbers_error));
            return false;
        }


        if (target.matches("[0-9]+")) {
            name.setError(getString(R.string.name_at_least_one_letter_error));
            return false;
        }

        return true;
    }

    @Override
    public void onDetach() {
        loginActivity = null;
        super.onDetach();
    }
}
