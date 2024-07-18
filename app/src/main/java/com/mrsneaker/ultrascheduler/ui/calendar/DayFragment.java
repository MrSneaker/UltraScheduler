package com.mrsneaker.ultrascheduler.ui.calendar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mrsneaker.ultrascheduler.R;
import com.mrsneaker.ultrascheduler.databinding.FragmentDayBinding;
import com.mrsneaker.ultrascheduler.model.event.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayFragment extends Fragment {

    private FragmentDayBinding binding;
    private static final String ACTUAL_DAY = "actualDay";

    public DayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param actualDay
     * @return A new instance of fragment DayFragment.
     */
    public static DayFragment newInstance(String actualDay) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putString(ACTUAL_DAY, actualDay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDayBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            String date = getArguments().getString(ACTUAL_DAY);
            binding.actualDayTV.setText(date);
        } else {
            binding.actualDayTV.setText("ERROR");
        }

        RecyclerView recyclerView = binding.hourRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Event> events = new ArrayList<>();
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.HOUR_OF_DAY, 13);
        Calendar endDate = (Calendar) startDate.clone();
        endDate.add(Calendar.HOUR_OF_DAY, 2);
        events.add(new Event("test", startDate, endDate));
        recyclerView.setAdapter(new DayAdapter(getHourOfTheDay(), getParentFragmentManager()));
        addEventsToContainer(events, binding.eventsContainer);

        return binding.getRoot();
    }

    private List<String> getHourOfTheDay() {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < 24; ++i) {
            String hour = i + ":00";
            res.add(hour);
        }
        return res;
    }

    private void addEventsToContainer(List<Event> events, FrameLayout eventsContainer) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (Event event : events) {
            CardView eventCard = (CardView) inflater.inflate(R.layout.item_event, eventsContainer, false);
            TextView eventTextView = eventCard.findViewById(R.id.eventTextView);
            eventTextView.setText(event.getSubject());

            int startHour = event.getStartTime().get(Calendar.HOUR_OF_DAY);
            int startMinute = event.getStartTime().get(Calendar.MINUTE);
            int endHour = event.getEndTime().get(Calendar.HOUR_OF_DAY);
            endHour = (endHour == 0) ? 24  : endHour;
            int endMinute = event.getEndTime().get(Calendar.MINUTE);

            int durationInMinutes = (endHour * 60 + endMinute) - (startHour * 60 + startMinute);

            int hourHeight = getResources().getDimensionPixelSize(R.dimen.hour_height);
            int eventHeight = (durationInMinutes * hourHeight) / 60;
            int topMargin = (startHour * hourHeight) + (startMinute * hourHeight) / 60;

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    eventHeight // Adjust height based on duration
            );
            params.topMargin = topMargin;

            eventCard.setLayoutParams(params);
            initCardAction(eventCard);
            eventsContainer.addView(eventCard);
        }
    }

    private void initCardAction(CardView eventCard) {
        eventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: DISPLAY EVENT INFO  AND ALLOW MODIFICATION
                Log.d("EventCardClick", "CLICK");
            }
        });
    }

}