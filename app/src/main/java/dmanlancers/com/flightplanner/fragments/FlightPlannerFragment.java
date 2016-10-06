package dmanlancers.com.flightplanner.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import dmanlancers.com.flightplanner.R;
import dmanlancers.com.flightplanner.activities.FlightPlanActivity;
import dmanlancers.com.flightplanner.listeners.AutoCompleteTextViewClickListener;
import dmanlancers.com.flightplanner.managers.RealmManager;
import dmanlancers.com.flightplanner.model.AirportCode;
import dmanlancers.com.flightplanner.model.MessageType;
import dmanlancers.com.flightplanner.utils.Utils;
import io.realm.RealmResults;

public class FlightPlannerFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener, AdapterView.OnItemClickListener {

    private RealmManager realmManager;
    private AppCompatTextView mCurrentDate;
    private AppCompatTextView mCurrentTime;
    private AppCompatSpinner mMessageType;
    private AppCompatAutoCompleteTextView mOriginAirport;
    private AppCompatAutoCompleteTextView mDestinationAirport;
    private FlightPlanActivity mActivity;
    private String mMessageTypeSelectedValue;
    private String mOriginAirportValue;
    private String mDestinationAirportValue;
    private AppCompatEditText mFlightCode;
    private String mDestinationEmail;
    private String mSubjectEmail;
    private LinearLayout mFlightPlanLayout;

    public FlightPlannerFragment() {
        realmManager = new RealmManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (FlightPlanActivity) getActivity();
        return inflater.inflate(R.layout.fragment_flight_planner, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentDate = (AppCompatTextView) view.findViewById(R.id.date);
        mCurrentTime = (AppCompatTextView) view.findViewById(R.id.time);
        mMessageType = (AppCompatSpinner) view.findViewById(R.id.spinner_message_type);
        mOriginAirport = (AppCompatAutoCompleteTextView) view.findViewById(R.id.origin_airport_code);
        mDestinationAirport = (AppCompatAutoCompleteTextView) view.findViewById(R.id.destination_airport_code);
        mFlightCode = (AppCompatEditText) view.findViewById(R.id.flight_code);
        AppCompatButton mSendEmail = (AppCompatButton) view.findViewById(R.id.send_email);
        mFlightPlanLayout = (LinearLayout) view.findViewById(R.id.flight_plan_layout);
        mMessageType.setOnItemSelectedListener(this);
        mOriginAirport.setOnItemClickListener(new AutoCompleteTextViewClickListener(mOriginAirport, this));
        mDestinationAirport.setOnItemClickListener(new AutoCompleteTextViewClickListener(mDestinationAirport, this));
        mSendEmail.setOnClickListener(this);
        populateAirportCodeSpinner();
        populateMessageTypeSpinner();
        populateDateAndTime();
    }

    private void populateDateAndTime() {
        mCurrentDate.setText(Utils.getCurrentDate());
        mCurrentTime.setText(Utils.getCurrentTime());
    }

    private void populateMessageTypeSpinner() {
        RealmResults<MessageType> results = realmManager.getAllMessageType();
        List<String> messageType = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            messageType.add(results.get(i).getMessageType());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mActivity,
                android.R.layout.simple_spinner_item, messageType);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMessageType.setAdapter(arrayAdapter);
    }

    private void populateAirportCodeSpinner() {
        RealmResults<AirportCode> results = realmManager.getAllAirportCode();
        List<String> airportCode = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            airportCode.add(results.get(i).getAirportCode());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mActivity,
                android.R.layout.simple_dropdown_item_1line, airportCode);

        mOriginAirport.setAdapter(arrayAdapter);
        mDestinationAirport.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

        switch (adapterView.getId()) {
            case R.id.spinner_message_type:
                mMessageTypeSelectedValue = adapterView.getItemAtPosition(pos).toString();
                selectDestinationEmail(pos);
                emailSubject(pos);
                mFlightCode.requestFocus();
                break;
        }
    }

    private void emailSubject(int pos) {
        switch (pos) {
            case 0:
                mSubjectEmail = getString(R.string.message_delay);
                break;
            case 1:
                mSubjectEmail = getString(R.string.message_change);
                break;
            case 2:
                mSubjectEmail = getString(R.string.message_request);
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onClick(View view) {

        if (Utils.matchFlightCodePattern(mFlightCode)) {
            if (!Utils.validateAirportCode(mOriginAirport, mDestinationAirport)) {
                Utils.showDialog(mActivity,
                        String.format(getResources().getString(R.string.email_template),
                                mMessageTypeSelectedValue, mFlightCode.getText().toString().toUpperCase()
                                , mOriginAirportValue + mCurrentTime.getText().toString(), mDestinationAirportValue
                                , mCurrentDate.getText().toString()), new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialogInterface.dismiss();
                                        break;
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Utils.sendEmail(getActivity(), mDestinationEmail, mSubjectEmail,
                                                String.format(getResources().getString(R.string.email_template),
                                                        mMessageTypeSelectedValue, mFlightCode.getText().toString().toUpperCase()
                                                        , mOriginAirportValue + mCurrentTime.getText().toString(), mDestinationAirportValue
                                                        , mCurrentDate.getText().toString()));
                                        break;
                                }
                            }
                        });
            } else {
                Snackbar.make(mFlightPlanLayout, R.string.airport_code_error_message, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(mFlightPlanLayout, R.string.flight_code_error_format, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void selectDestinationEmail(int pos) {
        switch (pos) {
            case 0:
                mDestinationEmail = "fjvvasco@hotmail.com";
                break;
            case 1:
                mDestinationEmail = "celsorodrigues@sapo.pt";
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        switch (view.getId()) {
            case R.id.origin_airport_code:
                mOriginAirportValue = adapterView.getItemAtPosition(pos).toString();
                break;

            case R.id.destination_airport_code:
                mDestinationAirportValue = adapterView.getItemAtPosition(pos).toString();
                break;
        }
    }
}
