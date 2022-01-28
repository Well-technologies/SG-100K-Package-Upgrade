package org.singapore.ghru.ui.treadmill.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import org.singapore.ghru.R
import org.singapore.ghru.vo.BloodPressure
import org.singapore.ghru.vo.TreadmillBP


class TreadmillRecordAdapter(private val records: ArrayList<TreadmillBP>?) : RecyclerView.Adapter<TreadmillRecordHolder>() {

    private var listener: ((item: TreadmillBP) -> Unit)? = null

    override fun onBindViewHolder(holder: TreadmillRecordHolder, position: Int) {

        if (records != null) {
            val record = records[position]
            holder.bindRecord(record, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreadmillRecordHolder {

        val inflatedView = parent.inflate(R.layout.treadmill_record_list_item, false)
        return TreadmillRecordHolder(inflatedView, listener, records)
    }

    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    override fun getItemCount(): Int {

        if (records != null)
            return records.size
        else
            return 0
    }

    fun setOnItemClickListener(listener: (item: TreadmillBP) -> Unit) {
        this.listener = listener
    }

}