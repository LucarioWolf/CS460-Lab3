package com.example.poster1019;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class ContactFragment extends Fragment {

    //Added object variables
    Button changeButton;
    TextView descriptionTextView;


    public ContactFragment(){
        super(R.layout.fragment_contact);
    }

    /**
     * Added the button and textview objects as well as adding a click listener to the change description button
     * @param view The View returned by {link onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Find objects by id
        changeButton = view.findViewById(R.id.descriptionButton);
        descriptionTextView = view.findViewById(R.id.descriptionText);

        view.findViewById(R.id.layoutEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmailApp(getString(R.string.contact_email));
            }
        });

        //Added a click listener to the change description button
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDescription();
            }
        });

    }

    private void openEmailApp(String emailAddress){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+emailAddress));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(),R.string.no_email_app,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Created a function that changed the description text view.
     */
    private void changeDescription(){
        descriptionTextView.setText(R.string.alan_description);
    }
}
