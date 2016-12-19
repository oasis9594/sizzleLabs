package com.example.dell.testappkotlin

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.all_beacon_row.view.*
import java.util.*

class AllBeaconsAdapter (beacons : ArrayList<MyBeacon>) : RecyclerView.Adapter<AllBeaconsAdapter.ViewHolder>() {


    val mBeacons= beacons
    class ViewHolder(itemView: View, var handler: ViewHolder.MyClickHandler) : RecyclerView.ViewHolder(itemView), View.OnClickListener {


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            handler.showBeaconDetails(v, adapterPosition)
        }

        interface MyClickHandler {
            fun showBeaconDetails(v: View, pos: Int)
        }
        fun setUI(beacon : MyBeacon)
        {
            with(itemView)
            {
                text_title.text = beacon.uniqueId
                text_url.text = beacon.namespace
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllBeaconsAdapter.ViewHolder? {
        val customView = LayoutInflater.from(parent.context)
                .inflate(R.layout.all_beacon_row, parent, false)
        val vh = ViewHolder(customView, object : ViewHolder.MyClickHandler {
            override fun showBeaconDetails(v: View, pos: Int) {
                //implement if needed
            }
        })
        return vh
    }

    override fun onBindViewHolder(holder: AllBeaconsAdapter.ViewHolder, position: Int) {

        holder.setUI(mBeacons.get(position))

    }

    override fun getItemCount(): Int {
        return mBeacons.size
    }

}
