// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import com.google.sps.TimeRange;
import java.util.Comparator;
import com.google.sps.Event;

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<String> attendees = request.getAttendees(); 
        Collection<String> optional_attendees = request.getOptionalAttendees(); 
        long duration = request.getDuration(); 
        
        return getSlotsWithOptional(attendees, optional_attendees, duration, events);
    }

    private Collection<TimeRange> getSlotsWithOptional(Collection<String> attendees, Collection<String> optional_attendees, 
    long duration, Collection<Event> events) {
        if (attendees.isEmpty()) return getSlots(optional_attendees, duration, events); 

        Collection<String> combined_attendees = new HashSet<>();
        combined_attendees.addAll(attendees); 
        combined_attendees.addAll(optional_attendees); 
        Collection<TimeRange> combined_slots = getSlots(combined_attendees, duration, events); 

        if (!combined_slots.isEmpty()) return combined_slots; 
        else return getSlots(attendees, duration, events);
    }

    private Collection<TimeRange> getSlots(Collection<String> attendees, long duration, Collection<Event> events) {
        List<Event> list_events = new ArrayList<Event> (events);
        TimeRange beginning = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.START_OF_DAY, false);
        Event last_event = new Event("Beginning", beginning, Collections.emptySet());
        Collections.sort(list_events, ORDER_BY_START);
        Collection<TimeRange> slots = new ArrayList<TimeRange>();

        for (Event curr_event : list_events) {
            last_event = addEvent(curr_event, last_event, slots, attendees, duration); 
        }
        int last_end = last_event.getWhen().end();
        int end_of_day = TimeRange.END_OF_DAY + 1;
        if (duration <= (end_of_day - last_end)) {
            TimeRange final_slot = TimeRange.fromStartEnd(last_end, end_of_day, false); 
            slots.add(final_slot); 
        }
        return slots; 
    }

    private Event addEvent(Event curr_event, Event last_event, Collection<TimeRange> slots, Collection<String> 
    attendees, long duration) {
        if (!eventHasAttendee(attendees, curr_event)) return last_event; 
        TimeRange curr_range = curr_event.getWhen(); 
        TimeRange last_range = last_event.getWhen(); 
        if (curr_range.overlaps(last_range)) return getLaterTime(curr_event, last_event); 
        else{
            int curr_start = curr_range.start(); 
            int last_end = last_range.end();
            if (duration <= (curr_start - last_end)) {
                TimeRange slot = TimeRange.fromStartEnd(last_end, curr_start, false); 
                slots.add(slot);  
            }
            return curr_event; 
        }
    }

    private Event getLaterTime(Event curr_event, Event last_event) {
        TimeRange curr_range = curr_event.getWhen(); 
        TimeRange last_range = last_event.getWhen(); 
        if (curr_range.end() < last_range.end()) return last_event; 
        else return curr_event; 
    }

    private boolean eventHasAttendee(Collection<String> attendees, Event event) {
        Set<String> event_attendees = event.getAttendees(); 
        for (String attendee : attendees) { 
            if (event_attendees.contains(attendee)) {
                return true; 
            }
        }
        return false; 
    }

    private static final Comparator<Event> ORDER_BY_START = new Comparator<Event>() {
        @Override
        public int compare(Event first, Event next) {
            int first_start = first.getWhen().start();
            int next_start = next.getWhen().start();
            return Long.compare(first_start, next_start);
        }
    };
}
