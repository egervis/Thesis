package com.example.quiz.Controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.example.quiz.Controller.ConnectionManager.NearbyConnectionsManagerStar;
import com.example.quiz.Model.Classroom;
import com.example.quiz.Model.QuizSession;
import com.example.quiz.Model.QuizSessionStudent;
import com.example.quiz.Model.User;
import com.example.quiz.Service.ClassService;
import com.example.quiz.Service.QuizService;
import com.example.quiz.Service.UserService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

import static com.example.quiz.Controller.StringEncryption.StringEncryption.decryptString;

public class ClassViewStudentActivity extends AppCompatActivity {
    private boolean BLUETOOTH_CHECK_ON = false;//toggle off (false) to disable bluetooth check
    private boolean WIFI_CHECK_ON = false;//toggle off (false) to disable wifi direct check
    private boolean NEARBY_CHECK_ON = true;//toggle off (false) to disable wifi direct check

    private String userId;
    private String classId;
    private String className;

    private NearbyConnectionsManagerStar nearby;

//    private BroadcastReceiver receiverBluetooth;
//
//
//    private IntentFilter intentFilter;
//    private WifiP2pManager.Channel channel;
//    private WifiP2pManager manager;
//    private BroadcastReceiver receiverWiFi;
//    private List<WifiP2pDevice> peers;
//    private WifiP2pManager.PeerListListener myPeerListListener;
//    private ConnectionManager connectionManager;
//
//
//    private PayloadCallback payloadCallback;
//    private EndpointDiscoveryCallback endpointDiscoveryCallback;
//    private ConnectionLifecycleCallback connectionLifecycleCallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_view_student);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //userId = getIntent().getExtras().getString("id");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        classId = getIntent().getExtras().getString("classId");
        className = getIntent().getExtras().getString("className");

        getSupportActionBar().setTitle(className);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setOnClicks();
        if(NEARBY_CHECK_ON) {
            askWiFiPermissions();
            setNearby(classId);
        }
        checkCache();
//        if(WIFI_CHECK_ON)
//            initializeWiFiDirect();
    }

    private void setOnClicks() {
        Button quizzes = findViewById(R.id.quizSessionMenuButtonStudent);
        quizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService userService = new UserService();
                userService.getUser(userId, new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        Intent intent = new Intent(getApplicationContext(), StudentQuizSessionListActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("userName", user.getFirstName());
                        intent.putExtra("classId", classId);
                        startActivity(intent);
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to get user "+ e);
                    }
                });
            }
        });
        Button authenticate = findViewById(R.id.authenticateStudentButton);
        authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = findViewById(R.id.authenticatedTextViewStudent);
                textView.setText("Searching for professor's device...");
                TextView warning = findViewById(R.id.warningTextViewStudent);
                warning.setVisibility(View.GONE);
                nearby.restart();
            }
        });

    }
    private void checkCache() {
        File cacheFile = new File(getApplicationContext().getCacheDir(), "class_code.tmp");
        if(cacheFile.exists())
        {
            setClasswork();
        }
    }
    private void setClasswork() {
        TextView textView = findViewById(R.id.authenticatedTextViewStudent);
        textView.setText("Authenticated. Token will last 3 hours");
        TextView warning = findViewById(R.id.warningTextViewStudent);
        warning.setVisibility(View.VISIBLE);
        Button takeQuiz = findViewById(R.id.takeQuizButton);
        takeQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //check cache file
                File cacheFile = new File(getApplicationContext().getCacheDir(), "class_code.tmp");
                if(cacheFile.exists())
                {
                    try {
                        FileInputStream fis = new FileInputStream(cacheFile);
                        InputStreamReader inputStreamReader = new InputStreamReader(fis);
                        try{
                            BufferedReader reader = new BufferedReader(inputStreamReader);
                            final String code = reader.readLine();
                            //check code against db
                            ClassService classService = new ClassService();
                            classService.getClassCode(classId, new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    String e = s;
                                    //String e = e.split("~")[0];
                                    //String timestampServer = e.split("~")[1];
                                    String pass = code.split("~")[0];
                                    String timestampLocal = code.split("~")[1];
                                    System.out.println(code);

                                    try{
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.setTimeInMillis(Long.valueOf(timestampLocal));
                                        Date local = calendar.getTime();
                                        calendar = Calendar.getInstance();
                                        Date current = calendar.getTime();//SHOULD BE OBTAINED FROM FIREBASE WITH CLOUD FUNCTIONS
                                        long diff = current.getTime() - local.getTime();
                                        long maxDiff = 3/*hours*/ * 3600 * 1000;System.out.println(diff);
                                        if(diff<maxDiff)
                                        {
                                            String decrypted = decryptString(e);System.out.println(decrypted);
                                            if(decrypted.equals(pass))
                                                startQuiz();
                                            else {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Invalid Token", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                        }
                                        else {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Outdated Token", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }

                                    }
                                    catch(Exception exception)
                                    {
                                        //error
                                        exception.printStackTrace();
                                    }

                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //failed to get code
                                }
                            });
                        } catch (IOException e) {
                            // Error occurred when opening raw file for reading.
                        }
                    } catch (Exception e) {
                        //file not found
                    }
                }
                else
                {
                    //file not found
                }
            }
        });
        takeQuiz.setClickable(true);
        Drawable drawable = takeQuiz.getBackground();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable,Color.parseColor("#ffffff"));
        takeQuiz.setBackground(drawable);
    }

    private void startQuiz() {
        ClassService classService = new ClassService();
        classService.getClass(classId, new OnSuccessListener<Classroom>() {
            @Override
            public void onSuccess(Classroom classroom) {
                if(classroom.getLastQuizSessionId()!=null) {
                    final QuizService quizService = new QuizService();
                    quizService.getQuizSession(classroom.getLastQuizSessionId(), new OnSuccessListener<QuizSession>() {
                        @Override
                        public void onSuccess(final QuizSession quizSession) {
                            Calendar calendar = Calendar.getInstance();
                            if(calendar.getTime().getTime()<quizSession.getEndTime().getTime())
                            {
                                quizService.getStudentQuizSessions(userId, classId, new OnSuccessListener<ArrayList<QuizSessionStudent>>() {
                                    @Override
                                    public void onSuccess(ArrayList<QuizSessionStudent> quizSessions) {
                                        boolean quizTaken = false;
                                        for(QuizSessionStudent q:quizSessions)
                                        {
                                            if(q.getQuizSessionId().equals(quizSession.getId()))
                                                quizTaken = true;
                                        }
                                        if(quizSessions.size()==0 || !quizTaken) {
                                            long duration = quizSession.getEndTime().getTime() - quizSession.getStartTime().getTime();

                                            Intent intent = new Intent(ClassViewStudentActivity.this, TakeQuizActivity.class);
                                            intent.putExtra("id", userId);
                                            intent.putExtra("quizSessionId", quizSession.getId());
                                            intent.putExtra("quizId", quizSession.getQuizId());
                                            intent.putExtra("duration", duration);
                                            startActivity(intent);
                                        }
                                        else {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Quiz already taken.", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                }, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("Failed to get quiz sessions "+ e);
                                    }
                                });
                            }
                            else {
                                Toast toast = Toast.makeText(getApplicationContext(), "No available quizzes.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Failed to get quiz session "+ e);
                        }
                    });
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "No available quizzes.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get class "+ e);
            }
        });
    }

    private void askWiFiPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            //    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    private void setNearby(String serviceId) {
        this.nearby = new NearbyConnectionsManagerStar(getApplicationContext(), userId, serviceId, new Callable<Void>() {
            @Override
            public Void call() {
                setClasswork();
                return null;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /*

    //bluetooth code
    private void checkBluetooth(){
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(hasBluetooth(bluetoothAdapter))
        {
            askPermissions(bluetoothAdapter);
            getTeacherMacAddresses(bluetoothAdapter);
        }
    }

    private boolean hasBluetooth(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    private void askPermissions(BluetoothAdapter bluetoothAdapter) {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    private void startBluetoothDiscovery(final BluetoothAdapter bluetoothAdapter, final BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        bluetoothAdapter.startDiscovery();
        final Timer mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                bluetoothAdapter.cancelDiscovery();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Bluetooth Search Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                mTimer.cancel();
            }
        };
        mTimer.schedule(mTimerTask, 10000, 1);
    }

    private BroadcastReceiver createBluetoothReceiver(final ArrayList<String> macs) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();
                    Log.v("bluetooth test", "Device with mac address " + deviceHardwareAddress + " found.");

                    boolean valid = macAddressCheck(deviceHardwareAddress, macs);
                    if(valid)
                    {
                        startQuiz();
                    }
                }
            }
        };
        return receiver;
    }

    private void getTeacherMacAddresses(final BluetoothAdapter bluetoothAdapter) {
        ClassService classService = new ClassService();
        classService.getClass(classId, new OnSuccessListener<Classroom>() {
            @Override
            public void onSuccess(Classroom classroom) {
                ArrayList<String> ids = new ArrayList<>();
                for(User u:classroom.getTeachers())
                    ids.add(u.getId());
                BluetoothService bluetoothService = new BluetoothService();
                bluetoothService.getUsersMacAddresses(ids, new OnSuccessListener<ArrayList<String>>() {
                    @Override
                    public void onSuccess(ArrayList<String> strings) {
                        receiverBluetooth = createBluetoothReceiver(strings);
                        Toast.makeText(getApplicationContext(), "Bluetooth Search Started", Toast.LENGTH_SHORT).show();
                        startBluetoothDiscovery(bluetoothAdapter, receiverBluetooth);
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to user macs "+ e);
                    }
                });
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get class "+ e);
            }
        });
    }

    private boolean macAddressCheck(String mac, ArrayList<String> macs) {
        for(String s:macs)
            if(s.equals(mac))
                return true;
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Log.v("bluetooth","location granted");
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), "location denied, bluetooth inactive", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }



    //wifi direct code
    private void initializeWiFiDirect()
    {
        askWiFiPermissions();

        intentFilter = new IntentFilter();
        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        peers = new ArrayList<>();
        setPeerListener();
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiverWiFi = new WiFiDirectBroadcastReceiver(manager, channel, this);
        discoverPeers();
        checkConnections();
    }

    private void setPeerListener() {
        myPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                if(!peerList.getDeviceList().equals(peers))
                {
                    peers.clear();
                    peers.addAll(peerList.getDeviceList());
                    printPeers();
                }
                if(peers.size()==0)
                {
                    System.out.println("No devices found");
                    return;
                }
            }
        };
    }

    private void discoverPeers() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("discovery started");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("discovery failed "+reason);
            }
        });
    }

    private void connectToPeer(ArrayList<String> deviceAddresses) {
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = deviceAddresses.get(0);
            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    System.out.println("success");
                }

                @Override
                public void onFailure(int reason) {
                    System.out.println("connection failed " + reason);
                }
            });
    }

    private void connectToPeers(final ArrayList<String> deviceAddresses) {
        final int[] index = {deviceAddresses.size() - 1};System.out.println("index =>" + index[0]);
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //final int[] tries = {1};
                //while (tries[0] >0) {
                    WifiP2pConfig config = new WifiP2pConfig();
                    final String address = deviceAddresses.get(index[0]);
                    config.deviceAddress = address;
                    config.groupOwnerIntent = 15;
                    manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            System.out.println("success =>" + address);
                            //tries[0] = 0;
                        }

                        @Override
                        public void onFailure(int reason) {
                            System.out.println("connection failed " + reason);
                            //tries[0] = tries[0]-1;
                        }
                    });
                //}
                if(index[0]>0)
                    index[0]--;
                else
                    timer.cancel();
            }
        };
        timer.schedule(timerTask, 10000, 30000);
    }

    private void checkConnections() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        if(group!=null) {
                            WifiP2pDevice[] arr = new WifiP2pDevice[group.getClientList().size()];
                            group.getClientList().toArray(arr);
                            for (WifiP2pDevice device : arr)
                                System.out.println("Connected Device: " + device.deviceName);
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 20000, 10000);
    }

    private void initiateConnections(ArrayList<String> deviceAddresses)
    {
        connectionManager = new ConnectionManager(deviceAddresses);
        connectionManager.start();
    }

    class ConnectionManager extends Thread {
        ArrayList<String> deviceAddresses;
        ConnectionManager(ArrayList<String> deviceAddresses) {
            this.deviceAddresses = deviceAddresses;
        }
        public void run() {System.out.println("size =>" + deviceAddresses.size());
            for(final String address:deviceAddresses)
            {
                final int[] tries = {100};
                while (tries[0] >0) {
                    WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = address;
                    final boolean[] connecting = {true};
                    manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            System.out.println("success =>" + address);
                            connecting[0] = false;
                            tries[0] = 0;
                        }

                        @Override
                        public void onFailure(int reason) {
                            System.out.println("connection failed " + reason);
                            connecting[0] = false;
                            tries[0] = tries[0]-1;
                        }
                    });
                    while (connecting[0]) ;
                    System.out.println("here");
                }
            }
            return;
        }
    }

    private void printPeers() {
        for (WifiP2pDevice device:peers)
        {
            System.out.println(device.deviceName);
            System.out.println(device.deviceAddress);
        }
    }

    public WifiP2pManager.PeerListListener getPeerListener() {
        return this.myPeerListListener;
    }

    public void checkWifi() {
        ArrayList<String> testMacs = new ArrayList<>();
        testMacs.add("c2:ee:fb:ef:9d:24");
        testMacs.add("c2:ee:fb:f5:4d:00");
        testMacs.add("36:be:00:6b:66:29");
        testMacs.add("8a:bd:6e:29:15:40");
        connectToPeers(testMacs);
        initiateConnections(testMacs);
    }




    //nearby code
    public void checkNearby() {
        Nearby.getConnectionsClient(getApplicationContext()).stopAdvertising();
        startAdvertising();
        Nearby.getConnectionsClient(getApplicationContext()).stopDiscovery();
        startDiscovery();
    }

    private void initializeNearby() {
        payloadCallback = new ReceiveBytesPayloadListener();
        setConnectionLifecycleCallback();
        setEndpointDiscoveryCallback();
        askWiFiPermissions();
    }

    private String getUserNickname() {
        //return "3T-E";
        return "3T-M";
        //return "5";
        //return "7Pro";
        //return "tablet";
    }


    private void startAdvertising() {
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build();
        Nearby.getConnectionsClient(getApplicationContext()).startAdvertising(getUserNickname(), "com.example.quiz", connectionLifecycleCallback, advertisingOptions)
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

    private void startDiscovery() {
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build();
        Nearby.getConnectionsClient(getApplicationContext()).startDiscovery("com.example.quiz", endpointDiscoveryCallback, discoveryOptions)
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

    private void setConnectionLifecycleCallback() {
        connectionLifecycleCallback = new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                // Automatically accept the connection on both sides.
                Nearby.getConnectionsClient(getApplicationContext()).acceptConnection(endpointId, payloadCallback);
            }

            @Override
            public void onConnectionResult(String endpointId, ConnectionResolution result) {
                switch (result.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        // We're connected! Can now start sending and receiving data.
                        System.out.println("Connected to: " + endpointId);
                        //sendPayload("payload", endpointId);
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
                System.out.println("Discovered endpoint: " + info.getEndpointName() + " with id " + endpointId);
                Nearby.getConnectionsClient(getApplicationContext())
                        .requestConnection(getUserNickname(), endpointId, connectionLifecycleCallback)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                System.out.println("Connecting to: " + info.getEndpointName() + " with id " + endpointId);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to connected to " + endpointId);
                    }
                });
            }

            @Override
            public void onEndpointLost(String endpointId) {
                // A previously discovered endpoint has gone away.
            }
        };
    }


    static class ReceiveBytesPayloadListener extends PayloadCallback {

        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            // This always gets the full data of the payload. Will be null if it's not a BYTES
            // payload. You can check the payload type with payload.getType().
            byte[] receivedBytes = payload.asBytes();
            String text = new String(receivedBytes);
            System.out.println("Received from " + endpointId + ": " + text);
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
        Nearby.getConnectionsClient(getApplicationContext()).sendPayload(endpoint, bytesPayload);
    }




    // register the broadcast receiver with the intent values to be matched
    @Override
    protected void onResume() {
        super.onResume();
        if(receiverWiFi != null)
            registerReceiver(receiverWiFi, intentFilter);
    }
    // unregister the broadcast receiver
    @Override
    protected void onPause() {
        super.onPause();
        if(receiverWiFi != null)
            unregisterReceiver(receiverWiFi);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiverBluetooth !=null)
            unregisterReceiver(receiverBluetooth);
    }
*/
}
