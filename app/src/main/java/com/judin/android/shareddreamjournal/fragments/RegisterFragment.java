package com.judin.android.shareddreamjournal.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.judin.android.shareddreamjournal.QueryPreferences;
import com.judin.android.shareddreamjournal.R;
import com.judin.android.shareddreamjournal.activities.MainActivity;
import com.judin.android.shareddreamjournal.exceptions.EmptyInputException;
import com.judin.android.shareddreamjournal.exceptions.InvalidUsernameException;
import com.judin.android.shareddreamjournal.exceptions.PasswordsNotMatchingException;
import com.judin.android.shareddreamjournal.exceptions.UsernameTakenException;
import com.judin.android.shareddreamjournal.model.User;

// TODO: Loading indicator

public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    private EditText emailEdit, usernameEdit, passwordEdit, repeatPasswordEdit;
    private Button registerButton;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    public RegisterFragment() { }

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        linkUI(v);

        registerButton.setOnClickListener(view -> {
            registerButton.setEnabled(false);

            final String email = emailEdit.getText().toString();
            final String username = usernameEdit.getText().toString();
            final String password = passwordEdit.getText().toString();
            final String repeatedPassword = repeatPasswordEdit.getText().toString();
            //pack registration data in User object

            if(email.isEmpty() || username.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()){
                handleException(new EmptyInputException());
                return;
            }

            if(!password.equals(repeatedPassword)){
                handleException(new PasswordsNotMatchingException());
                return;
            }

            if(!isUserNameValid(username)){
                handleException(new InvalidUsernameException());
                return;
            }

            boolean isRegDirty = QueryPreferences.isRegDirty(getActivity());
            if (isRegDirty) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    user.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QueryPreferences.setRegDirty(getActivity(), false);
                                tryCreatingNewUser(email, username, password);
                            } else {
                                QueryPreferences.setRegDirty(getActivity(), true);
                                handleException(task.getException());
                            }
                        });
                }
            } else {
                tryCreatingNewUser(email, username, password);
            }
        });

        return v;
    }

    private void tryCreatingNewUser(final String email, final String username, final String password) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get(Source.SERVER)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    if (document.isEmpty()) {
                        setupAuthUser(email, username, password);
                    } else {
                        // Username taken
                        handleException(new UsernameTakenException());
                    }
                } else {
                    handleException(task.getException());
                }
            });
    }

    private void setupAuthUser(final String email, final String username, final String password) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    final FirebaseUser user = auth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();
                    // Update Auth display name
                    user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            writeUserToFirestore(user);
                        } else {
                            // Failed to update user info
                            QueryPreferences.setRegDirty(getActivity(), true);
                            handleException(task1.getException());
                        }
                    });
                } else {
                    handleException(task.getException());
                }
            });
    }

    private void writeUserToFirestore(FirebaseUser firebaseUser) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setUsername(firebaseUser.getDisplayName());
        user.setEmail(firebaseUser.getEmail());

        firestore.collection("users")
            .document(user.getId())
            .set(user)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // User successfully created and written to db
                    QueryPreferences.setRegDirty(getActivity(), false);
                    Toast.makeText(getActivity(), "User registered", Toast.LENGTH_LONG).show();
                    registerButton.setEnabled(true);

                    startMainActivity();
                } else {
                    // Something went wrong
                    QueryPreferences.setRegDirty(getActivity(), true);
                    handleException(task.getException());
                }
            });
    }

    private void handleException(Exception exception) {
        try {
            throw exception;
        } catch(EmptyInputException e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(getActivity(), R.string.error_required_empty, Toast.LENGTH_LONG).show();
        } catch(FirebaseNetworkException e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(getActivity(), R.string.error_network, Toast.LENGTH_LONG).show();
        } catch(FirebaseAuthUserCollisionException e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(getActivity(), R.string.error_used_email, Toast.LENGTH_LONG).show();
        } catch (FirebaseAuthInvalidCredentialsException e){
            Log.e(TAG, e.getMessage());
            String msg = e.getLocalizedMessage();
            if(e.getErrorCode().equals("ERROR_INVALID_EMAIL")){
                msg = getString(R.string.error_invalid_email);
            } else if (e.getErrorCode().equals("ERROR_WEAK_PASSWORD")) {
                msg = getString(R.string.error_invalid_password);
            }
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        } catch (InvalidUsernameException e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(getActivity(), R.string.error_invalid_username, Toast.LENGTH_LONG).show();
        } catch (UsernameTakenException e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(getActivity(), R.string.error_username_taken, Toast.LENGTH_LONG).show();
        } catch(PasswordsNotMatchingException e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(getActivity(), R.string.error_password_not_matching, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        registerButton.setEnabled(true);
    }

    /*
      Username requirements: (email and password will be verified in setupAuthUser())
      - username: min. 3 max. 36 characters
     */
    private boolean isUserNameValid(String username) {
        return username != null
                && username.length() > 2
                && username.length() < 37;
    }

    public void linkUI(View v){
        emailEdit = v.findViewById(R.id.email_edit);
        usernameEdit = v.findViewById(R.id.username_edit);
        passwordEdit = v.findViewById(R.id.password_edit);
        repeatPasswordEdit = v.findViewById(R.id.password_repeat_edit);
        registerButton = v.findViewById(R.id.register_button);
    }

    private void startMainActivity() {
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}