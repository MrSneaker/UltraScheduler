package com.mrsneaker.ultrascheduler.ui.event;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrsneaker.ultrascheduler.database.AppDatabase;
import com.mrsneaker.ultrascheduler.databinding.FragmentEventFormBinding;
import com.mrsneaker.ultrascheduler.injection.ViewModelFactory;
import com.mrsneaker.ultrascheduler.model.dao.DetailedEventDao;
import com.mrsneaker.ultrascheduler.model.event.DetailedEvent;
import com.mrsneaker.ultrascheduler.viewmodel.EventViewModel;

import java.util.Calendar;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFormFragment extends Fragment {

    public static final String YEAR = "YEAR";
    public static final String MONTH = "MONTH";
    public static final String DAY_OF_MONTH = "DAY_OF_MONTH";
    public static final String HOUR_OF_DAY = "HOUR_OF_DAY";
    public static final String MINUTE = "MINUTE";
    public static final String SECOND = "SECOND";
    private FragmentEventFormBinding binding;
    private Calendar startCal;
    private Calendar endCal;
    private EventViewModel evm;

    public EventFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment EventFragment.
     */
    public static EventFormFragment newInstance(Calendar currentDay) {
        EventFormFragment fragment = new EventFormFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, currentDay.get(Calendar.YEAR));
        args.putInt(MONTH, currentDay.get(Calendar.MONTH));
        args.putInt(DAY_OF_MONTH, currentDay.get(Calendar.DAY_OF_MONTH));
        args.putInt(HOUR_OF_DAY, currentDay.get(Calendar.HOUR_OF_DAY));
        args.putInt(MINUTE, currentDay.get(Calendar.MINUTE));
        args.putInt(SECOND, currentDay.get(Calendar.SECOND));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        evm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(EventViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventFormBinding.inflate(inflater, container, false);

        TextView dateStart = binding.dateStart;
        TextView dateEnd = binding.dateEnd;
        TextView hourStart = binding.hourStart;
        TextView hourEnd = binding.hourEnd;

        startCal = Calendar.getInstance();
        endCal = setStartCalendar();

        dateStart.setText(String.format("%d/%d/%d", startCal.get(Calendar.DAY_OF_MONTH),  startCal.get(Calendar.MONTH) + 1, startCal.get(Calendar.YEAR)));
        dateEnd.setText(String.format("%d/%d/%d", endCal.get(Calendar.DAY_OF_MONTH),  endCal.get(Calendar.MONTH) + 1, endCal.get(Calendar.YEAR)));
        hourStart.setText(String.format("%d:%d", startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE)));
        hourEnd.setText(String.format("%d:%d", endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE)));
        initSaveButton();
        return binding.getRoot();
    }

    private void initSaveButton() {
        binding.saveEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: make the difference between DetailEvent & TaskEvent
                DetailedEvent detailedEvent = new DetailedEvent("New event", startCal,endCal,"a desc", isAllDay());
                evm.insertDetailedEvent(detailedEvent);
            }
        });
    }

    private boolean isAllDay()  {
        return binding.isAllDaySwitch.isActivated();
    }

    private Calendar setStartCalendar() {
        if(getArguments() != null) {
            startCal.set(Calendar.YEAR, (Integer) getArguments().get(YEAR));
            startCal.set(Calendar.DAY_OF_MONTH, (Integer) getArguments().get(DAY_OF_MONTH));
            startCal.set(Calendar.MONTH, (Integer) getArguments().get(MONTH));
        }
        int actualMin = startCal.get(Calendar.MINUTE);
        if(actualMin >= 30) {
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            startCal.add(Calendar.HOUR_OF_DAY, 1);
        } else {
            startCal.set(Calendar.MINUTE, 30);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
        }

        Calendar endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.HOUR_OF_DAY, 1);
        return endCal;
    }
}