package com.judin.android.shareddreamjournal.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.judin.android.shareddreamjournal.R;
import com.judin.android.shareddreamjournal.exceptions.EmptyInputException;

public class ForgotPasswordFragment extends Fragment {
    private static final String TAG = "ForgotPasswordFragment";
    private EditText mEmailEdit;
    private Button mResetPasswordButton;
    private FirebaseAuth mAuth;

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        linkUI(v);


        mResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEdit.getText().toString();
                if(email.isEmpty()){
                    handleException(new EmptyInputException());
                    return;
                }

                mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(), R.string.check_email_prompt, Toast.LENGTH_LONG).show();
                            } else {
                                handleException(task.getException());
                            }
                        }
                    });
            }
        });
        return v;
    }

    private void handleException(Exception exception){
        try {
            throw exception;
        } catch (EmptyInputException e){
            Toast.makeText(getActivity(), R.string.error_required_empty, Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void linkUI(View v){
        mEmailEdit = v.findViewById(R.id.email_edit);
        mResetPasswordButton = v.findViewById(R.id.reset_password_button);
    }
}