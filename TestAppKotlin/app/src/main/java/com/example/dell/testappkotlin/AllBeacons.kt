package com.example.dell.testappkotlin


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.fragment_all_beacons.*


class AllBeacons : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_all_beacons, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mProjection = arrayOf(
                BeaconContract.BeaconEntry.COL_TITLE,
                BeaconContract.BeaconEntry.COL_DESCRIPTION
        )

// Defines a string to contain the selection clause
        val mSelectionClause: String? = null

// Initializes an array to contain selection arguments
        val mSelectionArgs = arrayOf("")

        val allBeaconList= ArrayList<MyBeacon>()

        val cursor = context.contentResolver.query(BeaconContract.BeaconEntry.CONTENT_URI, mProjection, mSelectionClause, null, null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    val title=cursor.getString(cursor.getColumnIndex(BeaconContract.BeaconEntry.COL_TITLE))
                    val url = cursor.getString(cursor.getColumnIndex(BeaconContract.BeaconEntry.COL_DESCRIPTION))
                    allBeaconList.add(MyBeacon(title, url, 0.0))
                }while (cursor.moveToFirst())
            }
        } catch (e: Exception) {
            Log.d("Movie.TAG", "Error while trying to get beacons from database");
        } finally {
            if (!cursor.isClosed) {
                cursor.close()
            }
        }

        //set layout manager
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        AllBeaconList.layoutManager= layoutManager

        //set adapter
        val adapter = AllBeaconsAdapter(allBeaconList)
        AllBeaconList.adapter=adapter

        adapter.notifyDataSetChanged()
    }

}// Required empty public constructor
