package org.singapore.ghru.ui.dashboard.allparticipants

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.R
import org.singapore.ghru.databinding.AllStationsItemBinding
import org.singapore.ghru.databinding.NghruItemBinding
import org.singapore.ghru.databinding.ParticipantStationsItemBinding
import org.singapore.ghru.ui.common.DataBoundListAdapter
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.ParticipantStationItemNewNew
import org.singapore.ghru.vo.ParticipantStationsItem


class AllParticipantsAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val context: Context,
    private val callback: ((ParticipantStationItemNewNew) -> Unit)?
) : DataBoundListAdapter<ParticipantStationItemNewNew, ParticipantStationsItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<ParticipantStationItemNewNew>() {
        override fun areItemsTheSame(oldItem: ParticipantStationItemNewNew, newItem: ParticipantStationItemNewNew): Boolean {
            return oldItem.participant_id == newItem.participant_id
        }

        override fun areContentsTheSame(oldItem: ParticipantStationItemNewNew, newItem: ParticipantStationItemNewNew): Boolean {
            return oldItem.participant_id == newItem.participant_id
        }
    }
) {

    override fun createBinding(parent: ViewGroup): ParticipantStationsItemBinding {
        val binding = DataBindingUtil
            .inflate<ParticipantStationsItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.participant_stations_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.singleClick {
            binding.homeItem?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: ParticipantStationsItemBinding, item: ParticipantStationItemNewNew) {
        binding.homeItem = item

        binding.registerIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.sampleIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.heightIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.hipIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.gripIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.ecgIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.spiroIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.fundoIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.bpIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.dxaIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.selfIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.axivityIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.hlqIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.ultraIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.visualIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.tredIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.ffqIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.cogIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.chkIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.vicIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.skinIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.octaIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)

        for (stationItem in item.listRequest!!)
        {
            if (stationItem!!.station_name.equals("Registration"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.registerIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.registerIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.registerIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.registerIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.registerIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.registerIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
////                    binding.registerIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.registerIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Biological Samples"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.sampleIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.sampleIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1000)
                {
                    binding.sampleIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.sampleIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.sampleIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.sampleIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.sampleIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.sampleIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
                else if(stationItem.status_code!!.toInt() == 10000)
                {
                    binding.sampleIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.sampleIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.sampleIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.sampleIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Height and Weight"))
            {
                if(stationItem.isCancelled == 1)
                {
                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                    binding.heightIcon.setBackgroundResource(R.drawable.status_cancel)
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.heightIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.heightIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.heightIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Waist and Hip"))
            {
                if(stationItem.isCancelled == 1)
                {
                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                    binding.hipIcon.setBackgroundResource(R.drawable.status_cancel)
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.hipIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.hipIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.hipIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Grip Strength"))
            {
                if(stationItem.isCancelled == 1)
                {
                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                    binding.gripIcon.setBackgroundResource(R.drawable.status_cancel)
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.gripIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.gripIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.gripIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("ECG"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.ecgIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.ecgIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.ecgIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.ecgIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.ecgIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.ecgIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.ecgIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.ecgIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Spirometry"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.spiroIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.spiroIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.spiroIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.spiroIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.spiroIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.spiroIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.spiroIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.spiroIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Fundoscopy"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.fundoIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.fundoIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.fundoIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.fundoIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.fundoIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.fundoIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.fundoIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.fundoIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Blood Pressure"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.bpIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.bpIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.bpIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.bpIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.bpIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.bpIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.bpIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.bpIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Dual X-ray absorptometry (DXA)"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.dxaIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.intIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.dxaIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.intIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.dxaIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.intIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.dxaIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.intIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Participant HLQ"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.selfIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.paHlqIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.selfIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.paHlqIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.selfIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.paHlqIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.selfIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.paHlqIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Axivity"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.axivityIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.axiIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.axivityIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.axiIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.axivityIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.axiIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.axivityIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.axiIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Staff HLQ"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.hlqIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.hlqIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.hlqIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.hlqIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.hlqIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.hlqIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.hlqIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.hlqIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Carotid Ultrasound"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.ultraIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.ultraIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.ultraIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.ultraIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Visual Acuity"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.visualIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.visualIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.visualIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.visualIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Treadmill"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.tredIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.tredIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.tredIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.tredIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Food Frequency"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.ffqIcon.setBackgroundResource(R.drawable.status_cancel)
                }
                else if(stationItem.status_code!!.toInt() == 1 || stationItem.status_code!!.toInt() == 10)
                {
                    binding.ffqIcon.setBackgroundResource(R.drawable.status_progress)
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.ffqIcon.setBackgroundResource(R.drawable.status_complete)
                }
//                else
//                {
//                    binding.ffqIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                }
            }
            else if (stationItem!!.station_name.equals("Cognition"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.cogIcon.setBackgroundResource(R.drawable.status_cancel)
                }
                else if(stationItem.status_code!!.toInt() == 1 || stationItem.status_code!!.toInt() == 10)
                {
                    binding.cogIcon.setBackgroundResource(R.drawable.status_progress)
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.cogIcon.setBackgroundResource(R.drawable.status_complete)
                }
//                else
//                {
//                    binding.cogIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                }
            }
            else if (stationItem!!.station_name.equals("Checkout"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.chkIcon.setBackgroundResource(R.drawable.status_cancel)
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.chkIcon.setBackgroundResource(R.drawable.status_progress)
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.chkIcon.setBackgroundResource(R.drawable.status_complete)
                }
//                else
//                {
//                    binding.chkIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                }
            }
            else if (stationItem!!.station_name.equals("Vicorder"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.vicIcon.setBackgroundResource(R.drawable.status_cancel)
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.vicIcon.setBackgroundResource(R.drawable.status_progress)
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.vicIcon.setBackgroundResource(R.drawable.status_complete)
                }
            }
            else if (stationItem!!.station_name.equals("Skin"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.skinIcon.setBackgroundResource(R.drawable.status_cancel)
                }
                else if(stationItem.status_code!!.toInt() == 1 || stationItem.status_code!!.toInt() == 10)
                {
                    binding.skinIcon.setBackgroundResource(R.drawable.status_progress)
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.skinIcon.setBackgroundResource(R.drawable.status_complete)
                }
//                else
//                {
//                    binding.chkIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                }
            }
            else if (stationItem!!.station_name.equals("Octa"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.octaIcon.setBackgroundResource(R.drawable.status_cancel)
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.octaIcon.setBackgroundResource(R.drawable.status_progress)
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.octaIcon.setBackgroundResource(R.drawable.status_complete)
                }
            }
        }
    }
}
