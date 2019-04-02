package com.procrastimax.birthdaybuddy.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventDate
import com.procrastimax.birthdaybuddy.models.OneTimeEvent
import kotlinx.android.synthetic.main.fragment_show_one_time_event.*
import java.text.DateFormat


class ShowOneTimeEvent : ShowEventFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_one_time_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            item_id = arguments!!.getInt(ITEM_ID_PARAM)

            val editBtn: ImageView = toolbar.findViewById<ImageView>(R.id.iv_toolbar_show_event_edit)

            editBtn.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt(
                    ITEM_ID_PARAM,
                    item_id
                )
                val ft = (context as MainActivity).supportFragmentManager.beginTransaction()

                // add arguments to fragment
                val oneTimeEventInstance = OneTimeEventInstanceFragment.newInstance()
                oneTimeEventInstance.arguments = bundle
                ft.replace(
                    R.id.fragment_placeholder,
                    oneTimeEventInstance,
                    OneTimeEventInstanceFragment.ONE_TIME_EVENT_INSTANCE_FRAGMENT_TAG
                )
                ft.addToBackStack(null)
                ft.commit()
                arguments = null
            }
            updateUI()
        } else {
            (context as MainActivity).supportFragmentManager.popBackStack()
        }
    }

    /**
     * updateUI updates all TextViews and other views to the current instance(AnnualEvent, Birthday, OneTimeEvent) data
     */
    override fun updateUI() {
        //dont update ui when wrong item id / or deleted item
        if (EventHandler.getList()[item_id] !is OneTimeEvent) {
            (context as MainActivity).supportFragmentManager.popBackStack()
        } else {
            val oneTimeEvent = EventHandler.getList()[item_id] as OneTimeEvent
            //set name of one_time event
            this.tv_show_one_time_event_name.text = oneTimeEvent.name

            val date: String = oneTimeEvent.dateToPrettyString(DateFormat.DEFAULT)

            val daysUntil = oneTimeEvent.getDaysUntil()

            this.tv_show_one_time_event_date.text = resources.getQuantityString(
                R.plurals.one_time_event_show_date,
                daysUntil,
                daysUntil,
                date
            )

            if (oneTimeEvent.getYearsUntil() > 0) {
                this.tv_show_one_time_event_years.text = resources.getQuantityString(
                    R.plurals.one_time_event_years,
                    oneTimeEvent.getYearsUntil(),
                    oneTimeEvent.getYearsUntil()
                )
            } else {
                this.tv_show_one_time_event_years.visibility = TextView.GONE
            }

            if (!oneTimeEvent.note.isNullOrBlank()) {
                this.tv_show_one_time_event_note.text =
                    context!!.resources.getString(R.string.one_time_event_note, oneTimeEvent.note)
                this.tv_show_one_time_event_note.setTextColor(ContextCompat.getColor(context!!, R.color.darkGrey))
            } else {
                this.tv_show_one_time_event_note.visibility = TextView.GONE
            }
        }
    }

    /**
     * shareEvent a function which is called after the share button has been pressed
     * It provides a simple intent to share data as plain text in other apps
     */
    override fun shareEvent() {
        if (EventHandler.getList()[item_id] is OneTimeEvent) {
            val oneTimeEvent = EventHandler.getList()[item_id] as OneTimeEvent

            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            var share_Annual_Event_Msg: String

            //annual_event name
            share_Annual_Event_Msg =
                context!!.resources.getString(R.string.share_one_time_event_name, oneTimeEvent.name)

            //annual_event next date
            share_Annual_Event_Msg += "\n" + context!!.resources.getString(
                R.string.share_one_time_event_date_next,
                EventDate.parseDateToString(EventDate.dateToCurrentTimeContext(oneTimeEvent.eventDate), DateFormat.FULL)
            )

            //annual_event days until
            share_Annual_Event_Msg += "\n" + context!!.resources.getQuantityString(
                R.plurals.share_one_time_event_days,
                oneTimeEvent.getDaysUntil(),
                oneTimeEvent.getDaysUntil()
            )

            if (oneTimeEvent.getYearsUntil() > 0) {
                share_Annual_Event_Msg += "\n" + context!!.resources.getQuantityString(
                    R.plurals.share_one_time_event_year,
                    oneTimeEvent.getYearsUntil(),
                    oneTimeEvent.getYearsUntil()
                )
            }

            intent.putExtra(Intent.EXTRA_TEXT, share_Annual_Event_Msg)
            startActivity(Intent.createChooser(intent, resources.getString(R.string.intent_share_chooser_title)))
        }
    }

    companion object {
        /**
         * SHOW_ONE_TIME_EVENT_INSTANCE_FRAGMENT_TAG is the fragments tag as a string
         */
        val SHOW_ONE_TIME_EVENT_INSTANCE_FRAGMENT_TAG = "SHOW_ONE_TIME_EVENT"

        /**
         * newInstance returns a new instance of this fragment
         */
        @JvmStatic
        fun newInstance(): ShowOneTimeEvent {
            return ShowOneTimeEvent()
        }
    }
}