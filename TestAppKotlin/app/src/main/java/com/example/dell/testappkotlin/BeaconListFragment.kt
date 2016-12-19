package com.example.dell.testappkotlin


import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener
import com.kontakt.sdk.android.common.profile.IEddystoneDevice
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace
import java.util.*
import android.support.v4.app.NotificationCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_beacon_list.*


class BeaconListFragment : Fragment(), EddystoneListener {

    val TAG = "Movie.TAG"
    val not_id = 124
    var mContext : Context?=null
    var notificationManager: NotificationManager? = null
    //Create object for bluetooth receiver
    val mReceiver = BluetoothReceiver()
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        notificationManager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //register bluetooth receiver
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        activity.registerReceiver(mReceiver, filter)

        mContext=context
        return inflater!!.inflate(R.layout.fragment_beacon_list, container, false)
    }

    var proximityManager: ProximityManager? = null

    val eddyStoneHM = HashMap<String?, MyBeacon>()
    val beaconList = ArrayList<MyBeacon>()
    val adapter = BeaconListAdapter(beaconList)
    fun scanBeacons() {
        initializeProximityManager(activity)
        setListener()
        if (proximityManager != null && proximityManager!!.isScanning) {
            //proximity manager already running
            //do nothing
        } else if (proximityManager != null){
            startScanning()
        }
    }

    fun initializeProximityManager(activity: Activity) {
        Log.d(TAG, "initializeProximityManager")
        proximityManager = ProximityManager(activity)
    }

    fun setListener() {
        Log.d(TAG, "setListener")
        proximityManager?.setEddystoneListener(this)
    }

    fun startScanning() {
        Log.d(TAG, "startScanning")
        proximityManager?.connect {
            proximityManager!!.startScanning()
        }
    }

    fun stopScanning() {
        proximityManager?.stopScanning()
    }

    fun disconnect() {
        proximityManager?.disconnect()
    }

    val REQUEST_ENABLE_BT = 6
    private val REQUEST_CODE_PERMISSION = 2
    internal var mPermission = Manifest.permission.ACCESS_COARSE_LOCATION

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated")
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter != null) {
            //Device supports bluetooth
            if (!mBluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }
        if (ActivityCompat.checkSelfPermission(context, mPermission) == PackageManager.PERMISSION_GRANTED && mBluetoothAdapter.isEnabled) {
            Log.d(TAG, "scanning beacons in onViewCreated")
            scanBeacons()
        }

        //set layout manager
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        BeaconList.layoutManager = layoutManager
        Log.d(TAG, "layoutManager set")
        //set adapter
        BeaconList.adapter = adapter
        Log.d(TAG, "adapter set")

        adapter.notifyDataSetChanged()
    }

    fun sendNotification(beacon: MyBeacon) {
        Log.d(TAG, "sendNotification1")
        //instantiate a builder object
        val mBuilder = NotificationCompat.Builder(mContext)
        // Creates an Intent for the Receiver
        val notifyIntent = Intent(mContext, MainActivity::class.java)
        notifyIntent.putExtra("Activity Key", 2)
        val notifyPendingIntent = PendingIntent.getBroadcast(
                mContext,
                0,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Puts the PendingIntent into the notification builder
        mBuilder.setContentIntent(notifyPendingIntent)
        mBuilder.setAutoCancel(true)
        mBuilder.setWhen(System.currentTimeMillis())
        mBuilder.setContentTitle(beacon.uniqueId)
        mBuilder.setContentText("Nearby Beacon Updated")
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)

        //build notification
        notificationManager!!.notify(not_id, mBuilder.build())
        Log.d(TAG, "sendNotification2")
    }

    override fun onEddystoneDiscovered(eddystone: IEddystoneDevice?, namespace: IEddystoneNamespace?) {
        Log.d(TAG, "onEddystoneDiscovered")
        if (eddystone != null) {
            val beacon = MyBeacon(eddystone.uniqueId, eddystone.url, eddystone.distance)
            eddyStoneHM.put(eddystone.uniqueId, beacon)
            if (beaconList.isEmpty() || eddystone.distance < beaconList[0].distance) {
                sendNotification(beacon)
            }
            updateList()
            //update database
            val values = ContentValues()
            values.put(BeaconContract.BeaconEntry.COL_TITLE, eddystone.uniqueId)
            if(eddystone.url!=null)
                values.put(BeaconContract.BeaconEntry.COL_DESCRIPTION, eddystone.url)
            else
                values.put(BeaconContract.BeaconEntry.COL_DESCRIPTION, "url")
            val inserted = mContext?.contentResolver?.insert(BeaconContract.BeaconEntry.CONTENT_URI, values)
            Log.d(TAG, inserted.toString())
        }
    }

    override fun onEddystoneLost(eddystone: IEddystoneDevice?, namespace: IEddystoneNamespace?) {
        Log.d(TAG, "onEddystoneLost")

        if (eddystone != null) {
            val beacon = MyBeacon(eddystone.uniqueId, eddystone.url, eddystone.distance)
            eddyStoneHM.remove(eddystone.uniqueId)
            if (beacon.distance == beaconList[0].distance) {
                sendNotification(beaconList[0])
            }
            updateList()
        }
    }

    override fun onEddystonesUpdated(eddystones: MutableList<IEddystoneDevice>?, namespace: IEddystoneNamespace?) {
        Log.d(TAG, "onEddystonesUpdated")
        if (eddystones == null)
            return
        var meddystone = beaconList[0]
        var b = false;
        for (eddystone: IEddystoneDevice in eddystones) {
            val beacon = MyBeacon(eddystone.uniqueId, eddystone.url, eddystone.distance)
            eddyStoneHM.put(eddystone.uniqueId, beacon)
            if (beacon.distance < meddystone.distance) {
                b = true
                meddystone = beacon
            }
        }
        if (b) {
            sendNotification(meddystone)
        }
        updateList()
    }

    fun updateList() {
        Log.d(TAG, "updateList called")
        beaconList.clear()
        for ((a, b) in eddyStoneHM)
            beaconList.add(b)
        beaconList.sortBy { it.distance }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity.unregisterReceiver(mReceiver)
    }

    //Receiver for bluetooth
    inner class BluetoothReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            Log.d(TAG, "MyReceiver")
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        //"Bluetooth off"
                        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                        if (mBluetoothAdapter != null) {
                            //Device supports bluetooth
                            if (!mBluetoothAdapter.isEnabled) {
                                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                            }
                        }
                    }
                    BluetoothAdapter.STATE_ON -> {
                        if (ActivityCompat.checkSelfPermission(context, mPermission) == PackageManager.PERMISSION_GRANTED)
                            scanBeacons()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e("Req Code", "" + requestCode)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Success Stuff here
                val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (mBluetoothAdapter != null) {
                    if (mBluetoothAdapter.isEnabled) {
                        scanBeacons()
                    }
                }
            } else {
                // Failure Stuff
                Toast.makeText(activity, "No location access", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
