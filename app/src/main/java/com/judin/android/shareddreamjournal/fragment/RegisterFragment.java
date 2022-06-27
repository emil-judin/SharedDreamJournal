package com.judin.android.shareddreamjournal.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.judin.android.shareddreamjournal.QueryPreferences;
import com.judin.android.shareddreamjournal.R;
import com.judin.android.shareddreamjournal.activity.MainActivity;
import com.judin.android.shareddreamjournal.model.User;

import javax.annotation.Nullable;

public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    private EditText mEmailEdit, mUsernameEdit, mPasswordEdit, mRepeatPasswordEdit;
    private Button mRegisterButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public RegisterFragment() { }

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        linkUI(v);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: disable buttons with loading indicator
                mRegisterButton.setEnabled(false);

                final String email = mEmailEdit.getText().toString();
                final String username = mUsernameEdit.getText().toString();
                final String password = mPasswordEdit.getText().toString();
                final String repeatedPassword = mRepeatPasswordEdit.getText().toString();
                //pack registration data in User object

                if(!password.equals(repeatedPassword)){
                    mRegisterButton.setEnabled(true);
                    Toast.makeText(getActivity(), R.string.error_password_not_matching, Toast.LENGTH_LONG).show();
                    return;
                }

                if(!isUserDataValid(username, password)){
                    mRegisterButton.setEnabled(true);
                    Toast.makeText(getActivity(), R.string.error_invalid_user_data, Toast.LENGTH_LONG).show();
                    return;
                }

                boolean isRegDirty = QueryPreferences.isRegDirty(getActivity());
                if (isRegDirty) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        QueryPreferences.setRegDirty(getActivity(), false);
                                        tryCreatingNewUser(email, username, password);
                                    } else {
                                        //something went wrong internet check
                                        QueryPreferences.setRegDirty(getActivity(), true);
                                        handleException(task.getException());
                                    }
                                }
                            });
                    }
                } else {
                    tryCreatingNewUser(email, username, password);
                }
            }
        });

        return v;
    }

    private void tryCreatingNewUser(final String email, final String username, final String password) {
        mFirestore.collection("users")
                .whereEqualTo("username", username)
                .get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document.isEmpty()) {
                                setupAuthUser(email, username, password);
                            } else {
                                //username taken
                                Toast.makeText(getActivity(), R.string.error_username_taken, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            handleException(task.getException());
                        }
                    }
                });
    }

    private void setupAuthUser(final String email, final String username, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            //update Display name
                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        writeUserToFirestore(user);
                                    } else {
                                        //failed to update user info
                                        QueryPreferences.setRegDirty(getActivity(), true);
                                        handleException(task.getException());
                                    }
                                }
                            });
                        } else {
                            handleException(task.getException());
                        }
                    }
                });
    }

    private void writeUserToFirestore(FirebaseUser firebaseUser) {
//        User user = new User(FirebaseUser.getUid(),
//                FirebaseUser.getDisplayName(),
//                FirebaseUser.getEmail());
        User user = new User();
        user.setUid(firebaseUser.getUid());
        user.setUsername(firebaseUser.getDisplayName());
        user.setEmail(firebaseUser.getEmail());

        mFirestore.collection("users")
                .document(user.getUid())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //user successfully created and written to db
                            QueryPreferences.setRegDirty(getActivity(), false);
                            Toast.makeText(getActivity(), "User registered", Toast.LENGTH_LONG).show();
                            mRegisterButton.setEnabled(true);

                            startMainActivity();
                        } else {
                            //something went wrong
                            QueryPreferences.setRegDirty(getActivity(), true);
                            handleException(task.getException());
                        }
                    }
                });
    }

    private void handleException(Exception e) {
        //TODO: ERRORHANDLER
        mRegisterButton.setEnabled(true);
    }

    /*
      User data requirements: (email will be verified in setupAuthUser())
      - username: min. 3 max. 36 characters
      - password: min. 6 characters
     */
    private boolean isUserDataValid(String username, String password) {
        boolean validUsername = username != null
                && username.length() > 2
                && username.length() < 37;
        boolean validPassword = password != null
                && password.length() > 5;

        return validUsername && validPassword;
    }

    public void linkUI(View v){
        mEmailEdit = v.findViewById(R.id.email_edit);
        mUsernameEdit = v.findViewById(R.id.username_edit);
        mPasswordEdit = v.findViewById(R.id.password_edit);
        mRepeatPasswordEdit = v.findViewById(R.id.password_repeat_edit);
        mRegisterButton = v.findViewById(R.id.register_button);
    }

    private void startMainActivity() {
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}