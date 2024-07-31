package com.mrsneaker.ultrascheduler.ui.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;
import com.mrsneaker.ultrascheduler.databinding.FragmentEventFormBinding;
import com.mrsneaker.ultrascheduler.injection.ViewModelFactory;
import com.mrsneaker.ultrascheduler.model.event.DetailedEvent;
import com.mrsneaker.ultrascheduler.model.event.GenericEvent;
import com.mrsneaker.ultrascheduler.model.event.TaskEvent;
import com.mrsneaker.ultrascheduler.viewmodel.EventViewModel;

import java.util.Calendar;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFormFragment extends Fragment {

    private static final String YEAR = "YEAR";
    private static final String MONTH = "MONTH";
    private static final String DAY_OF_MONTH = "DAY_OF_MONTH";
    private static final String HOUR_OF_DAY = "HOUR_OF_DAY";
    private static final String MINUTE = "MINUTE";
    private static final String SECOND = "SECOND";
    private static final String EVENT_ID = "EVENT_ID";
    private static final String EVENT_TYPE = "EVENT_TYPE";
    private FragmentEventFormBinding binding;
    private Calendar startCal;
    private Calendar endCal;
    private EventViewModel evm;
    private long eventId;
    private GenericEvent currentEvent;
    private String eventType;

    public EventFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment EventFragment.
     */
    public static EventFormFragment newInstance(Calendar currentDay, String eventType) {
        EventFormFragment fragment = new EventFormFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, currentDay.get(Calendar.YEAR));
        args.putInt(MONTH, currentDay.get(Calendar.MONTH));
        args.putInt(DAY_OF_MONTH, currentDay.get(Calendar.DAY_OF_MONTH));
        args.putInt(HOUR_OF_DAY, currentDay.get(Calendar.HOUR_OF_DAY));
        args.putInt(MINUTE, currentDay.get(Calendar.MINUTE));
        args.putInt(SECOND, currentDay.get(Calendar.SECOND));
        args.putLong(EVENT_ID, -1);
        args.putString(EVENT_TYPE, eventType);
        fragment.setArguments(args);
        return fragment;
    }

    public static EventFormFragment newInstance(long eventId) {
        EventFormFragment fragment = new EventFormFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putLong(EVENT_ID, eventId);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        evm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(EventViewModel.class);
        currentEvent = new GenericEvent();
        if(getArguments() != null) {
            eventId = getArguments().getLong(EVENT_ID);
            eventType = getArguments().getString(EVENT_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventFormBinding.inflate(inflater, container, false);

        if(eventId < 0) {
            initEventFormDateOnCreation();
            initSaveButtonOnCreation();
        } else {
            initEventFormOnUpdate();
            initSaveButtonOnUpdate();
        }

        initDateClick();
        initHourClick();

        return binding.getRoot();
    }

    private void initEventFormDateOnCreation() {
        TextView dateStart = binding.dateStart;
        TextView dateEnd = binding.dateEnd;
        TextView hourStart = binding.hourStart;
        TextView hourEnd = binding.hourEnd;

        startCal = Calendar.getInstance();
        endCal = setStartCalendar();
        setDateStringView(dateStart, dateEnd, hourStart, hourEnd);

    }

    private void initEventFormOnUpdate() {
        LiveData<GenericEvent> eventLiveData = evm.getEventById(eventId);
        initEventObservation(eventLiveData);
    }

    private void initSaveButtonOnCreation() {
        binding.saveEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText subject = binding.eventSubject;
                TextInputEditText desc = binding.eventDescriptionForm;


                switch (eventType) {
                    case "dEvent":
                        DetailedEvent detailedEvent = new DetailedEvent(subject.getText().toString(), startCal, endCal, Objects.requireNonNull(desc.getText()).toString(), isAllDay());
                        detailedEvent.initNotificationSendEvent(requireContext());
                        evm.insertDetailedEvent(detailedEvent);
                        break;
                    case "tEvent":
                        TaskEvent taskEvent = new TaskEvent(subject.getText().toString(), startCal, Objects.requireNonNull(desc.getText()).toString(), isAllDay());
                        taskEvent.initNotificationSendEvent(requireContext());
                        evm.insertTaskEvent(taskEvent);
                        break;
                    default:
                        break;
                }

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });
    }

    private void initSaveButtonOnUpdate() {
        binding.saveEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText subject = binding.eventSubject;
                TextInputEditText desc = binding.eventDescriptionForm;
                currentEvent.setSubject(subject.getText().toString());
                currentEvent.setDescription(desc.getText().toString());
                currentEvent.setAllDay(isAllDay());

                if(currentEvent  instanceof DetailedEvent) {
                    DetailedEvent detailedEvent = (DetailedEvent) currentEvent;
                    evm.updateDetailedEvent(detailedEvent);
                } else {
                    TaskEvent taskEvent = (TaskEvent) currentEvent;
                    evm.updateTaskEvent(taskEvent);
                }
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });
    }

    private void initEventObservation(LiveData<GenericEvent>  eventLiveData) {
        eventLiveData.observe(getViewLifecycleOwner(), new Observer<GenericEvent>() {
            @Override
            public void onChanged(GenericEvent event) {
                if(event != null) {
                    currentEvent = event;
                }
                TextView dateStart = binding.dateStart;
                TextView dateEnd = binding.dateEnd;
                TextView hourStart = binding.hourStart;
                TextView hourEnd = binding.hourEnd;
                EditText sub = binding.eventSubject;
                TextInputEditText desc = binding.eventDescriptionForm;

                startCal = currentEvent.getStartTime();
                endCal = currentEvent.getEndTime();
                setDateStringView(dateStart, dateEnd, hourStart, hourEnd);

                sub.setText(currentEvent.getSubject());
                desc.setText(currentEvent.getDescription());
                binding.isAllDaySwitch.setChecked(currentEvent.isAllDay());
            }
        });
    }

    private boolean isAllDay()  {
        return binding.isAllDaySwitch.isChecked();
    }

    private void setDateStringView(TextView dateStart, TextView dateEnd, TextView hourStart, TextView hourEnd) {
        dateStart.setText(String.format("%02d/%02d/%04d", startCal.get(Calendar.DAY_OF_MONTH),  startCal.get(Calendar.MONTH) + 1, startCal.get(Calendar.YEAR)));
        dateEnd.setText(String.format("%02d/%02d/%04d", endCal.get(Calendar.DAY_OF_MONTH),  endCal.get(Calendar.MONTH) + 1, endCal.get(Calendar.YEAR)));
        hourStart.setText(String.format("%02d:%02d", startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE)));
        hourEnd.setText(String.format("%02d:%02d", endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE)));
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

    private void initDateClick() {
        TextView dateStart = binding.dateStart;
        TextView dateEnd = binding.dateEnd;

        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(dateStart, startCal);
            }
        });
        dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(dateEnd, endCal);
            }
        });
    }

    private void initHourClick() {
        TextView hourStart = binding.hourStart;
        TextView hourEnd = binding.hourEnd;

        hourStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(hourStart, startCal);
            }
        });
        hourEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(hourEnd, endCal);
            }
        });
    }

    private void showDatePickerDialog(final TextView dateTextView, Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                        cal.set(Calendar.YEAR, selectedYear);
                        cal.set(Calendar.MONTH, selectedMonth);
                        cal.set(Calendar.DAY_OF_MONTH, selectedDay);

                        String selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        dateTextView.setText(selectedDate);

                        dateAutoCorrect(dateTextView.getId());
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void showTimePickerDialog(final TextView hourTextView, Calendar cal) {
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                        cal.set(Calendar.HOUR_OF_DAY, selectedHour);
                        cal.set(Calendar.MINUTE, selectedMinute);

                        String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                        hourTextView.setText(selectedTime);

                        dateAutoCorrect(hourTextView.getId());
                    }
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    private void dateAutoCorrect(int idSelected) {
        TextView hourStart = binding.hourStart;
        TextView hourEnd = binding.hourEnd;
        TextView dateStart = binding.dateStart;
        TextView dateEnd = binding.dateEnd;

        int comparison = startCal.compareTo(endCal);

        if (comparison > 0) {
            if(idSelected == binding.dateEnd.getId() || idSelected == binding.hourEnd.getId()) {
                startCal = (Calendar) endCal.clone();
                startCal.add(Calendar.HOUR_OF_DAY, -1);

                updateDateTimeTextViews(hourStart, dateStart, startCal);
            } else {
                endCal = (Calendar) startCal.clone();
                endCal.add(Calendar.HOUR_OF_DAY, 1);

                updateDateTimeTextViews(hourEnd, dateEnd, endCal);
            }
        }
    }


    private void updateDateTimeTextViews(TextView hourTextView, TextView dateTextView, Calendar calendar) {
        String selectedTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        hourTextView.setText(selectedTime);

        String selectedDate = String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        dateTextView.setText(selectedDate);
    }
}