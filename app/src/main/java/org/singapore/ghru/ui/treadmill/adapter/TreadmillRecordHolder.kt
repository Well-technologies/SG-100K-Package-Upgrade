package org.singapore.ghru.ui.treadmill.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bp_record_list_item.view.*
import kotlinx.android.synthetic.main.bp_record_list_item.view.textViewDiastolic
import kotlinx.android.synthetic.main.bp_record_list_item.view.textViewPuls
import kotlinx.android.synthetic.main.bp_record_list_item.view.textViewSystolic
import kotlinx.android.synthetic.main.treadmill_record_list_item.view.*
import org.singapore.ghru.vo.BloodPressure
import org.singapore.ghru.vo.TreadmillBP


class TreadmillRecordHolder(v: View, listener: ((item: TreadmillBP) -> Unit)?, records: ArrayList<TreadmillBP>?) : RecyclerView.ViewHolder(v), View.OnClickListener {

    private var view: View = v

    init {
        v.setOnClickListener{ records?.get(adapterPosition)?.let { it1 ->
            listener?.invoke(
                it1
            )
        } }
    }

    override fun onClick(v: View) {
       //L.d("RecyclerView", "CLICK!")
    }

    fun bindRecord(record: TreadmillBP, index: Int) {
        view.textViewStage.text = record.stage.value.toString()
        view.textViewSystolic.text = record.systolic.value.toString()
        view.textViewDiastolic.text = record.diastolic.value.toString()
        view.textViewPuls.text = record.pulse.value.toString()

    }
}