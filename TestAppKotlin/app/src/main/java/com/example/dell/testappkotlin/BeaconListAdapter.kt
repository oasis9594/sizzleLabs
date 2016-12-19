package com.example.dell.testappkotlin


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.beacon_custom_row.view.*
import java.util.*

class BeaconListAdapter (beacons : ArrayList<MyBeacon>) : RecyclerView.Adapter<BeaconListAdapter.ViewHolder>() {


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
                text_uid.text = beacon.namespace
                var s= "0"
                try{
                    s=String.format("%.2f m", beacon.distance)
                }catch (e :MissingFormatWidthException)
                {
                    s=(beacon.distance*100).toString()+"cm"
                }
                text_distance.text = s
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeaconListAdapter.ViewHolder? {
        val customView = LayoutInflater.from(parent.context)
                .inflate(R.layout.beacon_custom_row, parent, false)
        val vh = ViewHolder(customView, object : ViewHolder.MyClickHandler {
            override fun showBeaconDetails(v: View, pos: Int) {
                //implement if needed
            }
        })
        return vh
    }

    override fun onBindViewHolder(holder: BeaconListAdapter.ViewHolder, position: Int) {

        holder.setUI(mBeacons.get(position))

    }

    override fun getItemCount(): Int {
        return mBeacons.size
    }

}
