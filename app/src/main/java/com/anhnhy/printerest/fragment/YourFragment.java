package com.anhnhy.printerest.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anhnhy.printerest.R;
import com.anhnhy.printerest.adapter.YourImageAdapter;
import com.anhnhy.printerest.model.Image;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class YourFragment extends Fragment implements YourImageAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private YourImageAdapter adapter;
    private ProgressBar progressCircle;
    private FirebaseStorage fbStorage;
    private DatabaseReference dbRef;
    private DatabaseReference userRef;
    private ValueEventListener valueEventListener;
    private List<String> imageIds;
    private List<Image> images;
    private String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public YourFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_your, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        progressCircle = view.findViewById(R.id.progress_circle);

        images = new ArrayList<>();
        imageIds = new ArrayList<>();
        adapter = new YourImageAdapter(getContext(), images);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        fbStorage = FirebaseStorage.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("images");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(UID);
        DatabaseReference imageIdsRef = userRef.child("imageIds");
        valueEventListener = imageIdsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageIds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String imageId = dataSnapshot.getValue().toString();
                    imageIds.add(imageId);
                }
                images.clear();
                for (String imageId : imageIds) {
                    DatabaseReference imageRef = dbRef.child(imageId);
                    valueEventListener = imageRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String name = snapshot.child("name").getValue().toString();
                                String imageUrl = snapshot.child("imageUrl").getValue().toString();
                                String senderId = snapshot.child("senderId").getValue().toString();
                                String tag = snapshot.child("tag").getValue().toString();
                                Image image = new Image(name, imageUrl, senderId, tag);
                                image.setKey(imageId);
                                images.add(image);
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getContext(), "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(getContext(), "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        Image selectedItem = images.get(position);
        final String selectedKey = selectedItem.getKey();
        images.remove(position);
        imageIds.remove(selectedKey);
        images.clear();
        imageIds.clear();
        StorageReference imageRef = fbStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dbRef.child(selectedKey).removeValue();
                userRef.child("imageIds").child(selectedKey).removeValue();
                userRef.child("likeIds").child(selectedKey).removeValue();
                Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDownload(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Download");
        request.setDescription("Downloading...");

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, String.valueOf(System.currentTimeMillis()));
        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
    }
}
