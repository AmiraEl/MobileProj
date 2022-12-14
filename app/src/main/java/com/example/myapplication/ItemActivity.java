package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ItemActivity extends AppCompatActivity implements View.OnClickListener {
    Computers item = new Computers();
    private Button buyButton;
    private TextView TextViewname;
    private TextView TextViewgpu;
    private TextView TextViewcpu;
    private TextView TextViewram;
    private TextView TextViewcase;
    private TextView TextViewmother;
    private TextView TextViewpsu;
    private TextView TextViewhdd;
    private TextView TextViewssd;
    private TextView TextViewSeller;
    private TextView TextViewPrice;
    private FirebaseFirestore db;
    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        int pos = getIntent().getIntExtra("position", -1);
        item = MainActivity.ItemsList.get(pos);

        TextViewname = findViewById(R.id.textViewName);
        TextViewgpu = findViewById(R.id.TextViewGPU);
        TextViewcpu = findViewById(R.id.TextViewCPU);
        TextViewram = findViewById(R.id.textViewRAM);
        TextViewcase = findViewById(R.id.textViewCase);
        TextViewmother = findViewById(R.id.TextViewboard);
        TextViewpsu = findViewById(R.id.TextViewPower);
        TextViewhdd = findViewById(R.id.TextViewHdd);
        TextViewssd = findViewById(R.id.TextViewSSD);
        TextViewSeller = findViewById(R.id.TextViewSeller);
        TextViewPrice = findViewById(R.id.TextViewPrice);
        this.db = MainActivity.db;

        Intent intent = getIntent();
        num = intent.getIntExtra("number", 0);
        buyButton = findViewById(R.id.buttonSAVE);

        if (num == 2) {
            //NO BUTTON
            buyButton.setVisibility(View.INVISIBLE);
        }

        if (LoginActivity.profile.getUID().equals(item.getSellerID())) {
            buyButton.setText("Delete");
        }

        buyButton.setOnClickListener(this);

//        if()


        db.collection("profiles").whereEqualTo("UID", item.getSellerID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Profiles temp = new Profiles();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        temp = document.toObject(Profiles.class);
                        break;
                    }
                    TextViewname.setText(item.getName());
                    TextViewgpu.setText(item.getGpu());
                    TextViewcpu.setText(item.getCpu());
                    TextViewram.setText(item.getRam());
                    TextViewcase.setText(item.getPcase());
                    TextViewmother.setText(item.getMotherboard());
                    TextViewpsu.setText(item.getPowersupply());
                    TextViewhdd.setText(item.getHdd());
                    TextViewssd.setText(item.getSsd());
                    TextViewPrice.setText(item.getPrice());
                    TextViewSeller.setText(temp.getEmail());

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSAVE) {
            if (LoginActivity.profile.getUID().equals(item.getSellerID())) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ItemActivity.this);


                builder.setTitle("Delete"); // title bar string
                builder.setMessage("Do you want to delete your listing?");

                builder.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                db.collection("computers").document(item.getPcID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {


                                        Intent notificationIntent = new Intent(getApplicationContext(), ItemActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                        // create the pending intent
                                        int flags = PendingIntent.FLAG_IMMUTABLE;
                                        PendingIntent pendingIntent =
                                                PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, flags);//<--------

                                        // create the variables for the notification
                                        CharSequence contentTitle = "Computify";

                                        CharSequence tickerText = "New computers available";
                                        CharSequence contentText = "Your listing has been removed";

                                        NotificationChannel notificationChannel =
                                                new NotificationChannel("Channel_ID", "My Notifications", NotificationManager.IMPORTANCE_HIGH);

                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        manager.createNotificationChannel(notificationChannel);


                                        // create the notification and set its data
                                        Notification notification = new NotificationCompat
                                                .Builder(getApplicationContext(), "Channel_ID")
                                                .setSmallIcon(R.drawable.computer)
                                                .setTicker(tickerText)
                                                .setContentTitle(contentTitle)
                                                .setContentText(contentText)
                                                .setContentIntent(pendingIntent)
                                                .setAutoCancel(true)
                                                .setChannelId("Channel_ID")
                                                .build();

                                        final int NOTIFICATION_ID = 4; //cannot be 0
                                        manager.notify(NOTIFICATION_ID, notification);


                                        Toast.makeText(getApplicationContext(), "Listing deleted", Toast.LENGTH_LONG).show();
                                        Intent deleteintent = new Intent(ItemActivity.this, MainActivity.class);
                                        deleteintent.putExtra("number", 0);
                                        startActivity(deleteintent);

                                    }
                                });

                            }
                        }
                );
                builder.setNegativeButton("cancel", null);
                builder.show();

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(ItemActivity.this);
                builder.setTitle("Purchase"); // title bar string
                builder.setMessage("Confirm your purchase?");

                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                item.setSellerID(LoginActivity.profile.getUID());

                                db.collection("computers").document(item.getPcID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        db.collection("purchases").document(item.getPcID()).set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Intent notificationIntent = new Intent(getApplicationContext(), ItemActivity.class)
                                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                // create the pending intent
                                                int flags = PendingIntent.FLAG_IMMUTABLE;
                                                PendingIntent pendingIntent =
                                                        PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, flags);//<--------

                                                // create the variables for the notification
                                                CharSequence contentTitle = "Computify";

                                                CharSequence tickerText = "New computers available";
                                                CharSequence contentText = "Your order has been placed, congratulations!";

                                                NotificationChannel notificationChannel =
                                                        new NotificationChannel("Channel_ID", "My Notifications", NotificationManager.IMPORTANCE_HIGH);

                                                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                manager.createNotificationChannel(notificationChannel);


                                                // create the notification and set its data
                                                Notification notification = new NotificationCompat
                                                        .Builder(getApplicationContext(), "Channel_ID")
                                                        .setSmallIcon(R.drawable.computer)
                                                        .setTicker(tickerText)
                                                        .setContentTitle(contentTitle)
                                                        .setContentText(contentText)
                                                        .setContentIntent(pendingIntent)
                                                        .setAutoCancel(true)
                                                        .setChannelId("Channel_ID")
                                                        .build();

                                                final int NOTIFICATION_ID = 4; //cannot be 0
                                                manager.notify(NOTIFICATION_ID, notification);


                                                Toast.makeText(getApplicationContext(), "Listing deleted", Toast.LENGTH_LONG).show();
                                                Intent buyIntent = new Intent(ItemActivity.this, MainActivity.class);
                                                buyIntent.putExtra("number", 2);
                                                startActivity(buyIntent);
                                            }
                                        });
                                    }
                                });


                            }
                        }
                );
                builder.setNegativeButton("Cancel", null);
                builder.show();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
//        menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            Intent HomeIntent = new Intent(ItemActivity.this, MainActivity.class);
            startActivity(HomeIntent);
        }
        if (item.getItemId() == R.id.addlisting) {
            Intent AddIntent = new Intent(ItemActivity.this, AddEditActivity.class);
            startActivity(AddIntent);
        }
        if (item.getItemId() == R.id.viewlisting) {
            Intent ViewIntent = new Intent(ItemActivity.this, MainActivity.class);
            ViewIntent.putExtra("number", 1);
            startActivity(ViewIntent);
        }
        if (item.getItemId() == R.id.purchases) {
            Intent ViewIntent = new Intent(ItemActivity.this, MainActivity.class);
            ViewIntent.putExtra("number", 2);
            startActivity(ViewIntent);
        }
        if (item.getItemId() == R.id.profile) {
            //TBA WHEN THE PROFILE ACTIVITY IS CREATED
            Intent ProfileIntent = new Intent(ItemActivity.this, ProfileActivity.class);
            startActivity(ProfileIntent);
        }
        if (item.getItemId() == R.id.about) {
            //TBA WHEN ABOUT ACTIVITY IS CREATED
            Intent HomeIntent = new Intent(ItemActivity.this, AboutActivity.class);
            startActivity(HomeIntent);
        }
        if (item.getItemId() == R.id.logout) {

            FirebaseAuth.getInstance().signOut();
            Intent HomeIntent = new Intent(ItemActivity.this, LoginActivity.class);
            startActivity(HomeIntent);
        }
        return super.onOptionsItemSelected(item);
    }

}