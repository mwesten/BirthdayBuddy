package com.procrastimax.birthdaybuddy.handler

import android.content.Context
import com.procrastimax.birthdaybuddy.EventDataIO
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDay
import com.procrastimax.birthdaybuddy.models.SortIdentifier
import java.text.DateFormat
import java.util.*

/**
 * EventHandler singleton object map to store all occurring eventdates (birthdays, anniversaries, etc.)
 * This is useful to compare all objects more easily, f.e. when you want to traverse all entries in event dates
 *
 * TODO:
 *  - when item is deleted or changed, and no other item needs a month divider for this month, delete month divider
 *  - rework whole month divider system
 *
 */
object EventHandler {
    private var event_map: MutableMap<Int, EventDay> = emptyMap<Int, EventDay>().toMutableMap()

    /**
     * event_list a list used for sorted viewing of the maps content
     * the data is stored in pairs of EventDay and the index of this dataset in the map as an int
     */
    var event_list: List<Pair<Int, EventDay>> = emptyList()

    /**
     * addEvent adds a EventDay type to the map and has the possibility to write it to the shared prefernces after adding it
     * this orders all events after the date automatically
     * also updates the eventday list after every adding of a new event
     * @param event: EventDay
     * @param writeAfterAdd: Boolean
     * @param isAddingMonth: Boolean, dont call changedEventList when already checked for new months
     */
    fun addEvent(event: EventDay, writeAfterAdd: Boolean = false) {
        //TODO: add event valdiation
        val last_key = getLastIndex()
        event_map[last_key] = event
        event_list = getSortedListBy()

        if (writeAfterAdd) {
            EventDataIO.writeEventToFile(last_key, event)
        }
    }

    /**
     * addMap adds a map of <Int, EventDay> to this map
     */
    fun addMap(events: Map<Int, EventDay>) {
        this.event_map.putAll(events)
        this.event_list = getSortedListBy()
    }

    /**
     * getKeyToValue searches for the key to an given event
     * This should work, because every event should be unique
     *
     * @param event : Eventday
     * @return Int
     */
    fun getKeyToValue(event: EventDay): Int {
        //TODO: this can be really slow
        val entrySet = event_map.entries
        entrySet.asIterable().forEach {
            if (it.value == event) {
                return it.key
            }
        }
        return -1
    }

    /**
     * getValueToKey returns the value with type EventDay? to a given integer key
     *
     * @param key : Int
     * @return EventDay?
     */
    fun getValueToKey(key: Int): EventDay? {
        if (event_map.contains(key)) {
            return event_map[key]!!
        }
        return null
    }

    /**
     * removeEventByEvent removes an event from the map by using a value
     * It uses the getKeyToValue function
     *
     * @param event : EventDay
     */
    fun removeEventByEvent(event: EventDay, writeChange: Boolean = false) {
        val key = getKeyToValue(event)
        event_map.remove(key)
        event_list = getSortedListBy()
        if (writeChange) {
            EventDataIO.removeEventFromFile(key)
        }
    }

    /**
     * removeEventByKey removes an event from the by using a key
     *
     * @param key : Int
     */
    fun removeEventByKey(key: Int, writeChange: Boolean = false) {
        event_map.remove(key)
        event_list = getSortedListBy()
        if (writeChange) {
            EventDataIO.removeEventFromFile(key)
        }
    }

    /**
     * clearMap deletes all entries
     */
    fun clearMap() {
        event_map.clear()
    }

    /**
     * changeEventAt assign new event at key position
     *
     * @param key : Int
     * @param event : EventDay
     */
    fun changeEventAt(key: Int, event: EventDay, context: Context, writeAfterChange: Boolean = false) {
        event_map[key] = event
        event_list = getSortedListBy()
        if (writeAfterChange) {
            EventDataIO.removeEventFromFile(key)
            EventDataIO.writeEventToFile(key, event)
        }
    }

    /**
     * containsValue checks if the given event is present in the map
     *
     * @param event: EventDay
     * @return Boolean
     */
    fun containsValue(event: EventDay): Boolean {
        return event_map.containsValue(event)
    }

    /**
     * containsKey checks if the given key is present in the map
     *
     * @param key: Int
     * @return Boolean
     */
    fun containsKey(key: Int): Boolean {
        return event_map.contains(key)
    }

    /**
     * getEvents returns all events as Map<Int, EventDay>
     *
     * @return Map<Int, EventDay>
     */
    fun getEvents(): Map<Int, EventDay> {
        return event_map
    }

    /**
     * getLastIndex returns the last used index in the map
     * The indexes of the map specify the event value, they are always incremented by one when a new value is added
     * So therefore to get the last used index, its enough to just check the map size
     *
     * It returns 0 if the map is empty
     *
     * @return Int
     */
    fun getLastIndex(): Int {
        return if (event_map.isEmpty()) {
            0
        } else event_map.size
    }

    /**
     * generateRandomEventDates does exactly what the name says
     * Only used for testing purposes!
     *
     * @param count : Int
     */
    fun generateRandomEventDates(count: Int, context: Context, writeAfterAdd: Boolean = false) {
        for (i in 1..count) {

            val day: Int = (1..30).random()
            val month: Int = (1..12).random()
            val year: Int = (0..99).random()
            val random = java.util.Random()
            val isYearGiven: Boolean = random.nextBoolean()

            val event = EventBirthday(
                EventDay.parseStringToDate(
                    "$day.$month.$year",
                    DateFormat.SHORT,
                    Locale.GERMAN
                ), EventHandler.getLastIndex().toString(), (i * i).toString(), isYearGiven
            )
            if (isYearGiven) {
                event.note = (day + month + i).toString()
            }
            addEvent(event, writeAfterAdd)
        }
    }

    /**
     * getSortValueListBy returns the map as a value list which is sorted by specific attributes given by an enum identifier
     * If the identifier is unknown, than an empty value list is returned
     *
     * @param identifier : SortIdentifier
     * @return List<EventDay>
     */
    fun getSortedListBy(identifier: SortIdentifier = EventDay.Identifier.Date): List<Pair<Int, EventDay>> {
        if (identifier == EventDay.Identifier.Date) {
            return this.event_map.toList().sortedBy { pair -> pair.second }
        } else {
            return emptyList()
        }
    }
}