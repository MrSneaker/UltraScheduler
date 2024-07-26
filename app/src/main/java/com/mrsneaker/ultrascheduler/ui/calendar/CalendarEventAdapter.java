package com.mrsneaker.ultrascheduler.ui.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mrsneaker.ultrascheduler.R;
import com.mrsneaker.ultrascheduler.model.event.DetailedEvent;
import com.mrsneaker.ultrascheduler.model.event.GenericEvent;
import com.mrsneaker.ultrascheduler.model.event.TaskEvent;
import com.mrsneaker.ultrascheduler.ui.event.EventCardFragment;
import com.mrsneaker.ultrascheduler.utils.DateUtils;
import com.mrsneaker.ultrascheduler.utils.MetricConverter;

import java.util.Calendar;
import java.util.List;

public class CalendarEventAdapter extends ArrayAdapter<GenericEvent> {
    private Context context;
    private List<GenericEvent> events;
    private final FragmentManager  fragmentManager;

    public CalendarEventAdapter(Context context, List<GenericEvent> events, FragmentManager  fragmentManager) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        convertView.setLayoutParams(params);

        GenericEvent event = events.get(position);

        TextView eventSubjectTextView = convertView.findViewById(R.id.eventSubject);
        TextView eventDateTextView = convertView.findViewById(R.id.eventDate);

        if (event instanceof DetailedEvent) {
            convertView.setTag(((DetailedEvent) event).getId());
        } else if (event instanceof TaskEvent) {
            convertView.setTag(((TaskEvent) event).getId());
        }

        eventSubjectTextView.setText(event.getSubject());
        eventDateTextView.setText(getEventDateString(event));

        initEventClick((CardView) convertView);

        return convertView;
    }

    private String getEventDateString(GenericEvent event) {
        Calendar startDate = event.getStartTime();
        Calendar endDate = event.getEndTime();

        int startDayOfMonth = startDate.get(Calendar.DAY_OF_MONTH);
        String startMonth = DateUtils.getMonthName(startDate);
        int startYear = startDate.get(Calendar.YEAR);
        int startHour = startDate.get(Calendar.HOUR_OF_DAY);
        int startMin = startDate.get(Calendar.MINUTE);

        int endDayOfMonth = endDate.get(Calendar.DAY_OF_MONTH);
        String endMonth = DateUtils.getMonthName(endDate);
        int endYear = endDate.get(Calendar.YEAR);
        int endHour = endDate.get(Calendar.HOUR_OF_DAY);
        int endMin = endDate.get(Calendar.MINUTE);

        return String.format("%d %s %d %02d:%02d - %d %s %d %02d:%02d",
                startDayOfMonth, startMonth, startYear, startHour, startMin,
                endDayOfMonth, endMonth, endYear, endHour, endMin);
    }

    private void initEventClick(CardView convertView) {
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long eventId = (long) v.getTag();
                ColorStateList cardColor = convertView.getBackgroundTintList();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                EventCardFragment eventCardFragment = EventCardFragment.newInstance(cardColor, eventId);
                fragmentTransaction.add(R.id.fragment_container_view, eventCardFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}
