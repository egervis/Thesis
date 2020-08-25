package com.example.quiz.Controller.ConnectionManager;

import android.content.Context;
import android.util.Base64;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.quiz.Model.User;
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

import java.io.File;
import java.io.FileOutputStream;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static com.example.quiz.Controller.StringEncryption.StringEncryption.decryptString;
import static com.example.quiz.Controller.StringEncryption.StringEncryption.encryptString;

public class NearbyConnectionsManagerCluster2 {
    private PayloadCallback payloadCallback;
    private EndpointDiscoveryCallback endpointDiscoveryCallback;
    private ConnectionLifecycleCallback connectionLifecycleCallback;
    private Strategy strategy = Strategy.P2P_STAR;

    private Context context;
    private String nickname;
    private boolean isTeacher;
    private boolean connected;
    private ArrayList<String> userIds;
    private HashMap<String, String> endpoints;
    private HashMap<String, User> map;
    private String password;
    private Callable<Void> callback;
    private String serviceId;
    private TextView teacherText;

    private final String SECRET_KEY = "ThisIsASecretKey";//Replace with a more secure key

    //student
    public NearbyConnectionsManagerCluster2(Context context, String nickname, String serviceId, Callable<Void> callback) {
        this.isTeacher = false;
        this.connected = false;
        this.context = context;
        this.nickname = nickname;
        this.callback = callback;
        this.serviceId = serviceId;
        this.password = "";
        this.userIds = new ArrayList<>();
        initializeNearby();
    }

    //teacher
    public NearbyConnectionsManagerCluster2(Context context, String nickname, String password, String serviceId, ArrayList<String> userIds, HashMap<String, User> map, TextView teacherText) {
        this.isTeacher = true;
        this.connected = false;
        this.context = context;
        this.nickname = nickname;
        this.password = password;
        this.userIds = userIds;
        this.serviceId = serviceId;
        this.teacherText = teacherText;
        this.map = map;
        callback = null;
        initializeNearby();
    }

    private void initializeNearby() {
        endpoints = new HashMap<>();
        payloadCallback = new ReceiveBytesPayloadListener();
        setConnectionLifecycleCallback();
        setEndpointDiscoveryCallback();
//        try {
//            System.out.println("e:" + encryptString(serviceId));
//            System.out.println("e:" + decryptString(encryptString(serviceId)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void startAdvertising() {
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(strategy).build();
        try {
            Nearby.getConnectionsClient(context).startAdvertising(encryptString(nickname), encryptString(serviceId), connectionLifecycleCallback, advertisingOptions)
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
            Nearby.getConnectionsClient(context).startDiscovery(encryptString(serviceId), endpointDiscoveryCallback, discoveryOptions)
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
                if(isTeacher)
                {
                    try {
                        teacherText.setText("");
                        if (userIds.contains(decryptString(connectionInfo.getEndpointName()))) {
                            endpoints.put(endpointId, decryptString(connectionInfo.getEndpointName()));
                            // Automatically accept the connection.
                            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback);
                        }
                        else
                        {
                            Nearby.getConnectionsClient(context).rejectConnection(endpointId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    // Automatically accept the connection on both sides.
                    Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback);
                }
            }

            @Override
            public void onConnectionResult(final String endpointId, ConnectionResolution result) {
                switch (result.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        // We're connected! Can now start sending and receiving data.
                        System.out.println("Connected to: " + endpointId);
                        if(isTeacher) {
                            final String endpointNickname = endpoints.get(endpointId);
                            teacherText.setText("Authenticating " + map.get(endpointNickname).getFirstName() + " " + map.get(endpointNickname).getLastName());
                            sendPayload(password, endpointId);
                            Nearby.getConnectionsClient(context).disconnectFromEndpoint(endpointId);
                        }
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
                if(!isTeacher && !connected)
                {
                    restartDiscovery();
                }
            }
        };
    }

    private void setEndpointDiscoveryCallback() {
        endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(final String endpointId, final DiscoveredEndpointInfo info) {
                // An endpoint was found. We request a connection to it.
                try {
                    final String endpointNickname = decryptString(info.getEndpointName());
                    System.out.println("Discovered endpoint: " + endpointNickname + " with id " + endpointId);
                    Nearby.getConnectionsClient(context)
                            .requestConnection(encryptString(nickname), endpointId, connectionLifecycleCallback)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    System.out.println("Connecting to: " + endpointNickname + " with id " + endpointId);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Failed to connected to " + endpointId);
                            restartDiscovery();
                        }
                    });
                }
                catch (Exception e) {
                    System.out.println("Failed to encrypt/decrypt while connecting to " + endpointId);
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
            byte[] receivedBytes = payload.asBytes();
            String text = new String(receivedBytes);
            System.out.println("Received from " + endpointId + ": " + text);
            Nearby.getConnectionsClient(context).disconnectFromEndpoint(endpointId);
            if(!isTeacher) {
                stopDiscovery();
                connected = true;
                checkPass(text);
            }
        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
        }
    }

    private void sendPayload(String text, String endpoint) {
        byte[] bytes = text.getBytes();
        Payload bytesPayload = Payload.fromBytes(bytes);
        Nearby.getConnectionsClient(context).sendPayload(endpoint, bytesPayload);
    }

    private void checkPass(String pass) {
        try {
            File cacheFile = new File(context.getCacheDir(), "class_code.tmp");
            System.out.println(cacheFile.delete());
            File.createTempFile("class_code", null, context.getCacheDir());
            cacheFile = new File(context.getCacheDir(), "class_code.tmp");
            FileOutputStream fos = new FileOutputStream(cacheFile);
            fos.write(pass.getBytes());
            callback.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
