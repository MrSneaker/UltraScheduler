package com.mrsneaker.ultrascheduler.ui.calendar;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mrsneaker.ultrascheduler.R;
import com.mrsneaker.ultrascheduler.databinding.FragmentDayBinding;
import com.mrsneaker.ultrascheduler.injection.ViewModelFactory;
import com.mrsneaker.ultrascheduler.model.event.GenericEvent;
import com.mrsneaker.ultrascheduler.ui.event.EventFormFragment;
import com.mrsneaker.ultrascheduler.utils.DateUtils;
import com.mrsneaker.ultrascheduler.viewmodel.EventViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayFragment extends Fragment {

    public static final String YEAR = "YEAR";
    public static final String MONTH = "MONTH";
    public static final String DAY_OF_MONTH = "DAY_OF_MONTH";
    public static final String HOUR_OF_DAY = "HOUR_OF_DAY";
    public static final String MINUTE = "MINUTE";
    public static final String SECOND = "SECOND";
    private static final String ACTUAL_DAY = "actualDay";
    private FragmentDayBinding binding;
    private EventViewModel evm;
    private Calendar displayedDay;


    public DayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param currentDay the day
     * @return A new instance of fragment DayFragment.
     */
    public static DayFragment newInstance(Calendar currentDay) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        String actualDayString = String.format(DateUtils.getDayOfWeekName(currentDay) + " %d " + DateUtils.getMonthName(currentDay), currentDay.get(Calendar.DAY_OF_MONTH));
        args.putString(ACTUAL_DAY, actualDayString);
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
        displayedDay = Calendar.getInstance();
        evm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(EventViewModel.class);
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
        List<GenericEvent> events = evm.getAllEventList().getValue();
        recyclerView.setAdapter(new DayAdapter(getHourOfTheDay(), getParentFragmentManager()));
        addEventsToContainer(events, binding.eventsContainer);

        initAddEventButton();
        initEventObserver();

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

    private void addEventsToContainer(List<GenericEvent> events, FrameLayout eventsContainer) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if(events != null) {
            for (GenericEvent event : events) {
                if(getArguments() != null) {
                    int EventDayOfMonth = event.getStartTime().get(Calendar.DAY_OF_MONTH);
                    if(EventDayOfMonth != (Integer) getArguments().get(DAY_OF_MONTH))  {
                        continue;
                    }
                }
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
        } else {
            Log.d("TEST", "ALOOO");
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

    private void initAddEventButton() {
        Button addEventBtn = binding.addEventDay;
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
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
                            setDisplayedDay();
                            if (selectedText.equals(getString(R.string.event))) {
                                FragmentManager fragmentManager = getParentFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                EventFormFragment eventFormFragment = EventFormFragment.newInstance(displayedDay);
                                fragmentTransaction.add(R.id.fragment_container_view, eventFormFragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            } else if (selectedText.equals(getString(R.string.task))) {
                                FragmentManager fragmentManager = getParentFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                EventFormFragment eventFormFragment = EventFormFragment.newInstance(displayedDay);
                                fragmentTransaction.add(R.id.fragment_container_view, eventFormFragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                        }
                        popupWindow.dismiss();
                    }
                });

            }
        });
    }

    private void setDisplayedDay() {
        if(getArguments() != null) {
            displayedDay.set(Calendar.YEAR, (Integer) getArguments().get(YEAR));
            displayedDay.set(Calendar.MONTH, (Integer) getArguments().get(MONTH));
            displayedDay.set(Calendar.DAY_OF_MONTH, (Integer) getArguments().get(DAY_OF_MONTH));
            displayedDay.set(Calendar.HOUR_OF_DAY, (Integer) getArguments().get(HOUR_OF_DAY));
            displayedDay.set(Calendar.MINUTE, (Integer) getArguments().get(MINUTE));
            displayedDay.set(Calendar.SECOND, (Integer) getArguments().get(SECOND));
        }
    }

    private void initEventObserver() {
        evm.getAllEventList().observe(getViewLifecycleOwner(), new Observer<List<GenericEvent>>() {
            @Override
            public void onChanged(List<GenericEvent> events) {
                Log.d("DayFragment", "CHANGE");
                binding.eventsContainer.removeAllViews();
                addEventsToContainer(events, binding.eventsContainer);
            }
        });
    }

}