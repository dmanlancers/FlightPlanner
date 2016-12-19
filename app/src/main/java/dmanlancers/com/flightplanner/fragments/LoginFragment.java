package dmanlancers.com.flightplanner.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import dmanlancers.com.flightplanner.R;
import dmanlancers.com.flightplanner.activities.FlightPlanActivity;
import dmanlancers.com.flightplanner.activities.LoginActivity;
import dmanlancers.com.flightplanner.managers.RealmManager;
import dmanlancers.com.flightplanner.model.Login;
import dmanlancers.com.flightplanner.utils.Utils;
import io.realm.RealmResults;

public class LoginFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener {
    private final RealmManager realmManager;
    private LoginActivity mActivity;
    private AppCompatEditText inputEmail, inputPassword;
    private TextInputLayout usernameWrapper, passwordWrapper;
    private LinearLayout loginLayout;
    private Toolbar mToolbar;

    public LoginFragment() {
        realmManager = new RealmManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, parentViewGroup, false);
        mActivity = (LoginActivity) getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usernameWrapper = (TextInputLayout) view.findViewById(R.id.usernameWrapper);
        passwordWrapper = (TextInputLayout) view.findViewById(R.id.passwordWrapper);
        inputEmail = (AppCompatEditText) view.findViewById(R.id.username);
        inputPassword = (AppCompatEditText) view.findViewById(R.id.password);
        loginLayout = (LinearLayout) view.findViewById(R.id.login);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        usernameWrapper.setHint(getString(R.string.hint_email));
        passwordWrapper.setHint(getString(R.string.hint_password));
        AppCompatButton mBtnlogin = (AppCompatButton) view.findViewById(R.id.btn_login);
        LinearLayout mLinearLayout = (LinearLayout) view.findViewById(R.id.login);
        mLinearLayout.setOnTouchListener(this);
        mBtnlogin.setOnClickListener(this);
        setToolbar();
    }

    private boolean submitForm() {
        return validateEmail() && validatePassword();
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty()) {
            usernameWrapper.setError(getString(R.string.err_msg_email));
            return false;
        } else if(!Utils.isValidEmail(email)) {
            usernameWrapper.setError(getString(R.string.err_invalid_format_email));
            return false;
        }else{
            usernameWrapper.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            passwordWrapper.setError(getString(R.string.err_msg_password));
            return false;
        } else {
            passwordWrapper.setErrorEnabled(false);
        }

        return true;
    }

    @Override
    public void onClick(View view) {

        @SuppressWarnings("ConstantConditions")
        String user = usernameWrapper.getEditText().getText().toString().trim();
        @SuppressWarnings("ConstantConditions")
        String password = passwordWrapper.getEditText().getText().toString().trim();

        Utils.hideKeyboard(mActivity);

        if (submitForm()) {

            RealmResults<Login> validateUser = realmManager.getAllUsers();

            for (Login c : validateUser) {

                if (c.getEmail().equals(user) && c.getPassword().equals(password)) {

                    Utils.putSharedPrefs(mActivity, "username", user);

                    Intent i = new Intent(mActivity, FlightPlanActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }
        } else {

            Snackbar.make(loginLayout, getString(R.string.msg_error_login_access), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void setToolbar() {
        mActivity.setSupportActionBar(mToolbar);
        if (mActivity.getSupportActionBar() != null) {
            mActivity.getSupportActionBar().setTitle(R.string.login);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Utils.hideKeyboard(mActivity);
        return false;
    }
}

