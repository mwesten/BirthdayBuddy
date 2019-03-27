package com.procrastimax.birthdaybuddy.views

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.fragments.*
import com.procrastimax.birthdaybuddy.handler.DrawableHandler
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.AnnualEvent
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.MonthDivider
import kotlinx.android.synthetic.main.annual_event_item_view.view.*
import kotlinx.android.synthetic.main.birthday_event_item_view.view.*
import kotlinx.android.synthetic.main.event_month_view_divider.view.*


class EventAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isClickable: Boolean = true

    class BirthdayEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class EventMonthDividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class EventAnniversaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**
     * getItemViewType overrides the standard function
     * it defines the different viewholder types used for the recycler view
     * 0 - month description divider
     * 1 - birthday event viewholder
     * 2 - anniversary event viewholder
     *
     * @param position: Int
     * @return Int
     */
    override fun getItemViewType(position: Int): Int {
        when (EventHandler.getList()[position].second) {
            is MonthDivider -> {
                if (position < EventHandler.getList().size - 1) {
                    if (EventHandler.getList()[position + 1].second !is MonthDivider) {
                        return 0
                    }
                }
                return -1
            }
            is EventBirthday -> {
                return 1
            }
            is AnnualEvent -> {
                return 2
            }
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // create a new view
        when (viewType) {
            0 -> {
                val item_view =
                    LayoutInflater.from(parent.context).inflate(R.layout.event_month_view_divider, parent, false)
                return EventMonthDividerViewHolder(item_view)
            }
            1 -> {
                val item_view =
                    LayoutInflater.from(parent.context).inflate(R.layout.birthday_event_item_view, parent, false)
                return BirthdayEventViewHolder(item_view)
            }
            2 -> {
                val item_view =
                    LayoutInflater.from(parent.context).inflate(R.layout.annual_event_item_view, parent, false)
                return EventAnniversaryViewHolder(item_view)
            }
            else -> {
                //Default is birthday event
                val item_view = View(context)
                return EventMonthDividerViewHolder(item_view)
            }
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // - get element from dataset at this position
        // - replace the contents of the view with that element

        when (holder.itemViewType) {

            //EventMonthDividerViewHolder
            0 -> {
                holder.itemView.tv_divider_description_month.text =
                    (EventHandler.getList()[position].second as MonthDivider).month_name
            }

            //BirthdayEventViewHolder
            1 -> {
                //check if is birthday event and if the year is given
                if (EventHandler.getList()[position].second is EventBirthday) {

                    //set on click listener for item
                    holder.itemView.setOnClickListener {
                        if (isClickable) {
                            val bundle = Bundle()
                            //do this in more adaptable way
                            bundle.putInt(
                                ITEM_ID_PARAM,
                                position
                            )
                            val ft = (context as MainActivity).supportFragmentManager.beginTransaction()
                            // add arguments to fragment
                            val newBirthdayFragment = ShowBirthdayEvent.newInstance()
                            newBirthdayFragment.arguments = bundle
                            ft.replace(
                                R.id.fragment_placeholder,
                                newBirthdayFragment
                            )
                            ft.addToBackStack(null)
                            ft.commit()
                        }
                    }

                    holder.itemView.setOnLongClickListener {
                        if (isClickable) {
                            val bundle = Bundle()
                            //do this in more adaptable way
                            bundle.putInt(
                                ITEM_ID_PARAM,
                                position
                            )

                            val ft = (context as MainActivity).supportFragmentManager.beginTransaction()

                            // add arguments to fragment
                            val newBirthdayFragment = BirthdayInstanceFragment.newInstance()
                            newBirthdayFragment.arguments = bundle
                            ft.replace(
                                R.id.fragment_placeholder,
                                newBirthdayFragment
                            )
                            ft.addToBackStack(null)
                            ft.commit()
                        }
                        true
                    }

                    //set date
                    holder.itemView.tv_birthday_event_item_date_value.text =
                        (EventHandler.getList()[position].second as EventBirthday).getPrettyShortStringWithoutYear()

                    //set days until
                    holder.itemView.tv_birthday_event_item_days_until_value.text =
                        EventHandler.getList()[position].second.getDaysUntil().toString()

                    //set years since, if specified
                    if ((EventHandler.getList()[position].second as EventBirthday).isYearGiven) {
                        holder.itemView.tv_birthday_event_item_years_since_value.text =
                            EventHandler.getList()[position].second.getYearsSince().toString()
                    } else {
                        holder.itemView.tv_birthday_event_item_years_since_value.text =
                            "-"
                    }

                    //if a birthday has a nickname, only show nickname
                    if ((EventHandler.getList()[position].second as EventBirthday).nickname != null) {

                        //set forename and surname invisible
                        holder.itemView.tv_birthday_event_item_forename.visibility = TextView.GONE
                        holder.itemView.tv_birthday_event_item_surname.visibility = TextView.GONE

                        //set nickname textview visible
                        holder.itemView.tv_birthday_event_item_nickname.visibility = TextView.VISIBLE

                        //set nickname textview text
                        holder.itemView.tv_birthday_event_item_nickname.text =
                            (EventHandler.getList()[position].second as EventBirthday).nickname

                    } else {

                        //set forename and surname invisible
                        holder.itemView.tv_birthday_event_item_forename.visibility = TextView.VISIBLE
                        holder.itemView.tv_birthday_event_item_surname.visibility = TextView.VISIBLE

                        //set nickname textview visible
                        holder.itemView.tv_birthday_event_item_nickname.visibility = TextView.GONE

                        holder.itemView.tv_birthday_event_item_forename.text =
                            (EventHandler.getList()[position].second as EventBirthday).forename

                        //set surname
                        holder.itemView.tv_birthday_event_item_surname.text =
                            (EventHandler.getList()[position].second as EventBirthday).surname
                    }

                    val avatarUri = (EventHandler.getList()[position].second as EventBirthday).avatarImageUri

                    if (avatarUri != null && !(context as MainActivity).isLoading) {
                        holder.itemView.iv_birthday_event_item_image.setImageDrawable(
                            DrawableHandler.getDrawableAt(
                                (EventHandler.getList()[position].first)
                            )
                        )
                    } else {
                        holder.itemView.iv_birthday_event_item_image.setImageResource(R.drawable.ic_birthday_person)
                    }
                }
            }

            //anniversary item view holder
            2 -> {
                //check if is birthday event and if the year is given
                if (EventHandler.getList()[position].second is AnnualEvent) {

                    //set on click listener for item
                    holder.itemView.setOnClickListener {
                        if (isClickable) {
                            val bundle = Bundle()
                            //do this in more adaptable way
                            bundle.putInt(
                                ITEM_ID_PARAM,
                                position
                            )

                            val ft = (context as MainActivity).supportFragmentManager.beginTransaction()
                            // add arguments to fragment
                            val newAnniversaryFragment = ShowAnnualEvent.newInstance()
                            newAnniversaryFragment.arguments = bundle
                            ft.replace(
                                R.id.fragment_placeholder,
                                newAnniversaryFragment
                            )
                            ft.addToBackStack(null)
                            ft.commit()
                        }
                    }

                    holder.itemView.setOnLongClickListener {
                        if (isClickable) {
                            val bundle = Bundle()
                            //do this in more adaptable way
                            bundle.putInt(
                                ITEM_ID_PARAM,
                                position
                            )

                            val ft = (context as MainActivity).supportFragmentManager.beginTransaction()

                            // add arguments to fragment
                            val newAnniversaryFragment = AnnualEventInstanceFragment.newInstance()
                            newAnniversaryFragment.arguments = bundle
                            ft.replace(
                                R.id.fragment_placeholder,
                                newAnniversaryFragment
                            )
                            ft.addToBackStack(null)
                            ft.commit()
                        }
                        true
                    }

                    //set date
                    holder.itemView.tv_anniversary_item_date_value.text =
                        (EventHandler.getList()[position].second as AnnualEvent).getPrettyShortStringWithoutYear()

                    //set days until
                    holder.itemView.tv_days_until_anniversary_value.text =
                        EventHandler.getList()[position].second.getDaysUntil().toString()

                    //set years since, if specified
                    if ((EventHandler.getList()[position].second as AnnualEvent).hasStartYear) {
                        holder.itemView.tv_years_since_anniversary_value.text =
                            EventHandler.getList()[position].second.getYearsSince().toString()
                    } else {
                        holder.itemView.tv_years_since_anniversary_value.text =
                            "-"
                    }

                    //set forename
                    holder.itemView.tv_anniversary_item_name.text =
                        (EventHandler.getList()[position].second as AnnualEvent).name
                }
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return if (EventHandler.getList().isEmpty()) {
            0
        } else {
            EventHandler.getLastIndex()
        }
    }
}