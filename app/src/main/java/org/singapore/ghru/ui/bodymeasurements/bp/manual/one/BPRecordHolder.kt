package org.singapore.ghru.ui.bodymeasurements.bp.manual.one

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bp_record_list_item.view.*
import org.singapore.ghru.vo.BloodPressure


class BPRecordHolder(v: View, listener: ((item: BloodPressure) -> Unit)?, records: ArrayList<BloodPressure>?) : RecyclerView.ViewHolder(v), View.OnClickListener {

    private var view: View = v

    init {
        v.setOnClickListener(this)
        view.trashBP.setOnClickListener { records?.get(adapterPosition)?.let { it1 ->
            listener?.invoke(
                it1
            )
        } }
    }

    override fun onClick(v: View) {
       //L.d("RecyclerView", "CLICK!")
    }

    fun bindRecord(record: BloodPressure, index: Int) {
        view.textViewNo.text = (index + 1).toString()
        view.textViewArm.text = record.arm.value.toString()
        view.textViewSystolic.text = record.systolic.value.toString()
        view.textViewDiastolic.text = record.diastolic.value.toString()
        view.textViewPuls.text = record.pulse.value.toString()
        view.textViewTimeStamp.text = record.timestamp.value.toString()

    }
}