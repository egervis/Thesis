package com.example.quiz.Controller.ConnectionManager;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.quiz.Model.User;
import com.example.quiz.R;
import com.example.quiz.Service.ClassService;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import static com.example.quiz.Controller.StringEncryption.StringEncryption.*;

public class NearbyConnectionsManagerClassroom {
    private PayloadCallback payloadCallback;
    private EndpointDiscoveryCallback endpointDiscoveryCallback;
    private ConnectionLifecycleCallback connectionLifecycleCallback;
    private Strategy strategy = Strategy.P2P_STAR;

    private Context context;
    private String classId;
    private boolean isTeacher;
    private Callable<Void> callback;
    private String pin;

    //student
    public NearbyConnectionsManagerClassroom(Context context, Callable<Void> callback) {
        isTeacher = false;
        this.context = context;
        this.classId = "";
        this.callback = callback;
        this.pin = "";
        initializeNearby();
    }

    //teacher
    public NearbyConnectionsManagerClassroom(Context context, String classId, String pin) {
        isTeacher = true;
        this.context = context;
        this.classId = classId;
        this.callback = null;
        this.pin = pin;
        initializeNearby();
    }

    private void initializeNearby() {
        payloadCallback = new ReceiveBytesPayloadListener();
        setConnectionLifecycleCallback();
        setEndpointDiscoveryCallback();
    }



    private void startAdvertising() {
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(strategy).build();
        try {
            Nearby.getConnectionsClient(context).startAdvertising(encryptString(classId+"~"+pin), "class_join_broadcast", connectionLifecycleCallback, advertisingOptions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("Advertising started");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Advertising failed");
                }
            });
        }
        catch (Exception e) {
            System.out.println("Advertising failed");
        }
    }

    private void startDiscovery() {
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(strategy).build();
        try {
            Nearby.getConnectionsClient(context).startDiscovery("class_join_broadcast", endpointDiscoveryCallback, discoveryOptions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("Discovery started");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Discovery failed");
                }
            });
        }
        catch (Exception e) {
            System.out.println("Discovery failed");
        }
    }

    private void stopAdvertising() {
        Nearby.getConnectionsClient(context).stopAdvertising();
    }

    private void stopDiscovery() {
        Nearby.getConnectionsClient(context).stopDiscovery();
    }

    private void restartAdvertising()
    {
        stopAdvertising();
        startAdvertising();
    }

    private void restartDiscovery()
    {
        stopDiscovery();
        startDiscovery();
    }

    public void restart() {
        if(isTeacher)
            restartAdvertising();
        else
            restartDiscovery();
    }

    public void start() {
        if(isTeacher)
            startAdvertising();
        else
            startDiscovery();
    }

    public void stop() {
        if(isTeacher)
            stopAdvertising();
        else
            stopDiscovery();
    }

    private void setConnectionLifecycleCallback() {
        connectionLifecycleCallback = new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                Nearby.getConnectionsClient(context).rejectConnection(endpointId);
            }

            @Override
            public void onConnectionResult(final String endpointId, ConnectionResolution result) {
                switch (result.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        // We're connected! Can now start sending and receiving data.
                        System.out.println("Connected to: " + endpointId);
                        break;
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                        // The connection was rejected by one or both sides.
                        System.out.println("Rejected by: " + endpointId);
                        break;
                    case ConnectionsStatusCodes.STATUS_ERROR:
                        // The connection broke before it was able to be accepted.
                        System.out.println("Error while connecting to: " + endpointId);
                        break;
                    default:
                        // Unknown status code
                }
            }

            @Override
            public void onDisconnected(String endpointId) {
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.
                System.out.println("Disconnected from: " + endpointId);
            }
        };
    }

    private void setEndpointDiscoveryCallback() {
        endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(final String endpointId, final DiscoveredEndpointInfo info) {
                // An endpoint was found. We request a connection to it.
                stopDiscovery();
                try {
                    String decrypted = decryptString(info.getEndpointName());
                    final String id = decrypted.split("~")[0];
                    final String pin = decrypted.split("~")[1];
                    new AlertDialog.Builder(context).setTitle("Join a class using the following pin:")
                            .setMessage(pin).setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            joinClass(id);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startDiscovery();
                        }
                    }).setIcon(R.drawable.ic_error_outline_red_900_24dp).show();
                }
                catch (Exception e) {
                    System.out.println("Failed to encrypt/decrypt while connecting to " + endpointId);
                    e.printStackTrace();
                    restartDiscovery();
                }
            }

            @Override
            public void onEndpointLost(String endpointId) {
                // A previously discovered endpoint has gone away.
            }
        };
    }


    class ReceiveBytesPayloadListener extends PayloadCallback {
        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            // This always gets the full data of the payload. Will be null if it's not a BYTES
            // payload. You can check the payload type with payload.getType().
        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
        }
    }

    private void joinClass(String id) {
        ClassService classService = new ClassService();
        classService.addUserToClass(id, FirebaseAuth.getInstance().getCurrentUser().getUid(), "student", new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                try {
                    callback.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to join class "+ e);
            }
        });
    }
}
