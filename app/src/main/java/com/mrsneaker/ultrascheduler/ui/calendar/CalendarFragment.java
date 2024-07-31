package com.mrsneaker.ultrascheduler.ui.calendar;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.mrsneaker.ultrascheduler.utils.DateUtils.monthYearFromDate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mrsneaker.ultrascheduler.R;
import com.mrsneaker.ultrascheduler.databinding.FragmentCalendarBinding;
import com.mrsneaker.ultrascheduler.injection.ViewModelFactory;
import com.mrsneaker.ultrascheduler.model.event.GenericEvent;
import com.mrsneaker.ultrascheduler.ui.event.EventFormFragment;
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
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        DateUtils.setSelectedDate(currentDate);

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
        initNewEventBtn();

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

    private void initNewEventBtn() {
        Button newEvent = binding.newEventBtnWeek;
        newEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        requireContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.event_type_popup_selector, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = false;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                Button okButton = popupView.findViewById(R.id.okBtnPopUpEvent);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioGroup radioGroup = popupView.findViewById(R.id.popupEventRadioGrp);

                        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

                        RadioButton selectedRadioButton = popupView.findViewById(checkedRadioButtonId);

                        if (selectedRadioButton != null) {
                            String selectedText = selectedRadioButton.getText().toString();
                            if (selectedText.equals(getString(R.string.event))) {
                                goToEventForm(DateUtils.getSelectedDate(), "dEvent");
                            } else if (selectedText.equals(getString(R.string.task))) {
                                goToEventForm(DateUtils.getSelectedDate(), "tEvent");
                            }
                        }
                        popupWindow.dismiss();
                    }
                });

            }
        });
    }

    private void goToEventForm(Calendar displayedDay, String eventType) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        EventFormFragment eventFormFragment = EventFormFragment.newInstance(displayedDay, eventType);
        fragmentTransaction.add(R.id.fragment_container_view, eventFormFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
                if(newEvents != null) {
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