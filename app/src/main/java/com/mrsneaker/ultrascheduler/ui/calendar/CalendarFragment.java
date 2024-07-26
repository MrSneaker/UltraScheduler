package com.mrsneaker.ultrascheduler.ui.calendar;

import static com.mrsneaker.ultrascheduler.utils.DateUtils.monthYearFromDate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mrsneaker.ultrascheduler.databinding.FragmentCalendarBinding;
import com.mrsneaker.ultrascheduler.injection.ViewModelFactory;
import com.mrsneaker.ultrascheduler.model.event.GenericEvent;
import com.mrsneaker.ultrascheduler.utils.DateUtils;
import com.mrsneaker.ultrascheduler.viewmodel.EventViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private EventViewModel evm;
    private List<GenericEvent> events;
    private CalendarEventAdapter eventAdapter;


    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalendarFragment.
     */
    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        evm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(EventViewModel.class);
        events = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        Calendar currentDate = DateUtils.getSelectedDate();

        binding.monthYearTV.setText(monthYearFromDate(currentDate));

        RecyclerView recyclerView = binding.calendarRecyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 7));
        recyclerView.setAdapter(new CalendarAdapter(DateUtils.daysInWeekArray(currentDate), getParentFragmentManager()));

        ListView eventList = binding.eventWeekListView;
        eventAdapter = new CalendarEventAdapter(getContext(), events, getParentFragmentManager());
        eventList.setAdapter(eventAdapter);

        refreshData();

        initNextWeekBtn();
        initLastWeekBtn();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CalendarFragment", "onResumeCalendar");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("CalendarFragment", "Fragment is in onPause state");
    }

    private void initNextWeekBtn() {
        Button nextWeek = binding.nextWeekBtn;
        nextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView recyclerView = binding.calendarRecyclerView;
                Calendar currentDate = (Calendar) DateUtils.getNextWeek().clone();
                recyclerView.setAdapter(new CalendarAdapter(DateUtils.daysInWeekArray(currentDate), getParentFragmentManager()));
                updateMonthYearTitle();
                evm.loadAllEvents();
            }
        });
    }

    private void initLastWeekBtn() {
        Button lastWeek = binding.previousWeekBtn;
        lastWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView recyclerView = binding.calendarRecyclerView;
                Calendar currentDate = (Calendar) DateUtils.getLastWeek().clone();
                recyclerView.setAdapter(new CalendarAdapter(DateUtils.daysInWeekArray(currentDate), getParentFragmentManager()));
                updateMonthYearTitle();
                evm.loadAllEvents();
            }
        });
    }

    private void updateMonthYearTitle() {
        TextView monthYearTitle = binding.monthYearTV;
        String prevMonthYString = (String) monthYearTitle.getText();
        String newMonthYString = monthYearFromDate(DateUtils.getSelectedDate());
        if(!prevMonthYString.equals(newMonthYString)) {
            monthYearTitle.setText(newMonthYString);
        }
    }

    private void refreshData() {
        evm.getAllEventList().observe(getViewLifecycleOwner(), new Observer<List<GenericEvent>>() {
            @Override
            public void onChanged(List<GenericEvent> newEvents) {
                if(!newEvents.isEmpty()) {
                    List<GenericEvent> filteredEvents = new ArrayList<>();

                    for (GenericEvent event : newEvents) {
                        if (DateUtils.isInCurrentWeek(event.getStartTime())) {
                            filteredEvents.add(event);
                        }
                    }
                    filteredEvents.sort(new Comparator<GenericEvent>() {
                        @Override
                        public int compare(GenericEvent e1, GenericEvent e2) {
                            return e1.getStartTime().compareTo(e2.getStartTime());
                        }
                    });
                    events.clear();
                    events.addAll(filteredEvents);
                    eventAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}