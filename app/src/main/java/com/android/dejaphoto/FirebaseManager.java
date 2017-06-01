package com.android.dejaphoto;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FirebaseManager {

    static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    static final DatabaseReference reference = database.getReference();

    public static String createID(String email) {
        return email.replace('.', '_')
                .replace('#', '_')
                .replace('$', '_')
                .replace('[', '_')
                .replace(']', '_');
    }

    public static void makeUser(final String user, final List<Object> friends, final Map<String, Object> photos) {
        if (user == null)
            return;

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user))
                    reference.child(user).setValue(new User(friends, photos));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void updateFriends(final String user, final List<Object> friends) {
        if (user == null)
            return;

        final List<Object> oldFriends = new ArrayList<>();
        reference.child(user).child("friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            oldFriends.add(snapshot.getValue());

                        oldFriends.addAll(friends);
                        reference.child(user)
                                .child("friends")
                                .setValue(new ArrayList<>(new HashSet<>(oldFriends)));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void updatePhotos(String user, Map<String, Object> photos) {
        if (user == null)
            return;

        reference.child(user).child("photos").updateChildren(photos);
    }

}
