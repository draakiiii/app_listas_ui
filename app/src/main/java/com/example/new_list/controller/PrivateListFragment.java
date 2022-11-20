package com.example.new_list.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.new_list.R;
import com.example.new_list.database.GlobalMethods;
import com.example.new_list.database.ItemAdapter;
import com.example.new_list.helper.DataConverter;
import com.example.new_list.model.GlobalList;
import com.example.new_list.model.Item;
import com.example.new_list.model.Section;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A fragment representing a list of Items.
 */
public class PrivateListFragment extends Fragment{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount;
    private DrawerLayout mDrawer;
    private Button addButton;
    private LinearLayout linearLayout;
    private GlobalList globalList;
    private GlobalMethods database;
    private ArrayList<Section> arrayOfArrays;
    private ArrayList<Button> arrayOfButtons;
    private ArrayList<TextView> arrayOfCounts;
    private ArrayList<ItemAdapter> arrayOfAdapters;
    private ArrayList<RecyclerView> arrayOfRecycler;
    private ItemTouchHelper itemTouchHelper;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private static AtomicInteger countButton;
    private String inputDateString;
    private String inputDateString2;
    private int height;
    private int width;
    private int toPos;
    private int fromPos;
    private HorizontalScrollView horizontalScrollView;

    public PrivateListFragment() {
    }

    public static PrivateListFragment newInstance(int columnCount) {
        PrivateListFragment fragment = new PrivateListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_private_list_list, container, false);
        try {
            toPos = -1;
            fromPos = -1;
            countButton = new AtomicInteger(0);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;
            width = (width / 100) * 90;
            mDrawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            linearLayout = view.findViewById(R.id.linearLayoutListPrivate);
            database = new GlobalMethods(getContext());
            globalList = database.findById(requireArguments().getInt("globallist"));
            arrayOfArrays = DataConverter.fromStringSection(globalList.getLists());
            arrayOfButtons = new ArrayList<>();
            arrayOfCounts = new ArrayList<>();
            arrayOfRecycler = new ArrayList<>();
            arrayOfAdapters = new ArrayList<>();
            mColumnCount = arrayOfArrays.size();
            inputDateString2 = "";
            inputDateString = "";
            System.out.println("Número de listas: " + mColumnCount);

            addButton = view.findViewById(R.id.addPrivateList);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogCreateSection();
                }
            });

            //Si hay columnas, empieza a generarlas
            if (mColumnCount > 0) {
                System.out.println("--------- LISTAS ----------");
                for (int i = 0; i < mColumnCount; i++) {
                    ArrayList<Item> arrayOfItemsPrivate = DataConverter.changeItemType(arrayOfArrays.get(i).getListOfItems());
                    generateGlobalView(i, arrayOfItemsPrivate, arrayOfArrays.get(i).getTitle());
                }
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    // Actualiza los datos de la lista global en la base de datos
    public void updateGlobalList(int pos, ArrayList arrayIndividual) {

        try {
            arrayOfArrays.get(pos).setListOfItems(arrayIndividual);
            globalList.setLists(DataConverter.fromArrayListSection(arrayOfArrays));
            database.updateItem(globalList);
        } catch (Exception e) {
            Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
        }

    }

    // Método que genera una lista desde cero
    public void generateNewListView(String title) {
        try {
            arrayOfArrays.add(new Section(title, new ArrayList<>()));
            int pos = arrayOfArrays.size() - 1;
            ArrayList<Item> arrayOfItemsPrivate = DataConverter.changeItemType(arrayOfArrays.get(pos).getListOfItems());
            Toast.makeText(getActivity(),R.string.list_created,Toast.LENGTH_SHORT).show();
            generateGlobalView(pos, arrayOfItemsPrivate, arrayOfArrays.get(pos).getTitle());
        } catch (Exception e) {
            Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
        }
    }

    public void generateGlobalView(int pos, ArrayList<Item> arrayIndividual, String title) {
        try {
            LinearLayout linearTitle = new LinearLayout(getActivity(), null, R.style.LinearLayoutMargin);
            linearTitle.setPadding(15,0,0,0);
            linearTitle.setOrientation(LinearLayout.HORIZONTAL);
            ScrollView scrollView = new ScrollView(getActivity());
            Button tv_private_title = new Button(getActivity());
            tv_private_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEditDialogPrivateName(pos, arrayIndividual, scrollView, tv_private_title);
                }
            });
            tv_private_title.setText(title);
            tv_private_title.setBackgroundResource(R.drawable.button_title);
            tv_private_title.setTextSize(20);
            LinearLayout linearLayoutPrivate = new LinearLayout(getActivity(), null, R.style.LinearLayoutMargin);
            linearLayoutPrivate.setMinimumWidth(width);
            arrayOfRecycler.add(new RecyclerView(getActivity()));

            arrayOfButtons.add(new Button(getActivity()));
            arrayOfCounts.add(new TextView(getActivity()));
            int lastButton = arrayOfButtons.size()-1;

            RelativeLayout.LayoutParams lpButtons = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpButtons.setMargins(10,0,20,0);
            arrayOfButtons.get(lastButton).setId(countButton.incrementAndGet());
            arrayOfButtons.get(lastButton).setLayoutParams(lpButtons);
            arrayOfButtons.get(lastButton).setText(R.string.addItem);
            arrayOfButtons.get(lastButton).setMinHeight(60);
            arrayOfButtons.get(lastButton).setPadding(0,0,250,0);
            arrayOfButtons.get(lastButton).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_add_24,0,0,0);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            arrayOfRecycler.get(lastButton).setLayoutManager(layoutManager);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            arrayOfRecycler.get(lastButton).setLayoutParams(lp);

            linearLayout.addView(scrollView);
            linearLayoutPrivate.addView(linearTitle);
            linearTitle.addView(tv_private_title);
            linearTitle.addView(arrayOfCounts.get(pos));
            scrollView.addView(linearLayoutPrivate);
            linearLayoutPrivate.addView(arrayOfRecycler.get(lastButton));
            linearLayoutPrivate.addView(arrayOfButtons.get(lastButton));
            linearLayoutPrivate.setOrientation(LinearLayout.VERTICAL);

            arrayOfCounts.get(pos).setText(String.valueOf(arrayIndividual.size()));
            arrayOfCounts.get(pos).setTextSize(15);

            arrayOfAdapters.add(new ItemAdapter(arrayIndividual, new ItemAdapter.OnItemClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onItemClick(Item item) {
                    showEditDialog(item, arrayOfAdapters.get(pos), arrayIndividual, pos);
                }
            }));

            arrayOfRecycler.get(lastButton).setAdapter(arrayOfAdapters.get(lastButton));

            arrayOfButtons.get(lastButton).setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    showInputDialog(arrayIndividual, arrayOfAdapters.get(pos), pos);
                }
            });

            simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT , 0) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    int toPos = target.getBindingAdapterPosition();
                    int fromPos = viewHolder.getBindingAdapterPosition();
                    Collections.swap(arrayIndividual, fromPos, toPos);
                    arrayOfAdapters.get(lastButton).notifyItemMoved(fromPos,toPos);
                    updateGlobalList(pos, arrayIndividual);
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                }
            };
            itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(arrayOfRecycler.get(lastButton));
            updateGlobalList(pos, arrayIndividual);
        } catch (Exception e) {
            Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
            deleteItem(arrayIndividual.get(pos), arrayOfAdapters.get(pos), arrayIndividual, pos);
        }

    }

    public void countItemsToTitle(int pos, ArrayList arrayIndividual) {
        arrayOfCounts.get(pos).setText(String.valueOf(arrayIndividual.size()));
    }

    public void addItem(Item item, ArrayList arrayIndividual, ItemAdapter adapter, int pos) {
        try {
            arrayIndividual.add(item);
            adapter.notifyItemInserted(adapter.getItemCount());
            updateGlobalList(pos, arrayIndividual);
            countItemsToTitle(pos, arrayIndividual);
        } catch (Exception e) {
            Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
        }
    }

    protected void showInputDialog(ArrayList arrayIndividual, ItemAdapter adapter, int pos) {
        try {

            LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
            View promptView = layoutInflater.inflate(R.layout.fragment_add_global_item, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity()).setTitle(R.string.dialogAddItem);
            alertDialogBuilder.setView(promptView);
            alertDialogBuilder.setCancelable(false);
            AlertDialog alert = alertDialogBuilder.create();
            alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
            alert.setCanceledOnTouchOutside(true);
            Calendar mcurrentDate = Calendar.getInstance();
            final int mYear = mcurrentDate.get(Calendar.YEAR);
            final int mMonth = mcurrentDate.get(Calendar.MONTH);
            final int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
            final EditText editTextTitle = (EditText) promptView.findViewById(R.id.inputTitle);
            final EditText editTextDescription = (EditText) promptView.findViewById(R.id.inputDescription);
            final EditText inputDate = promptView.findViewById(R.id.inputDate);
            inputDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                    if (selectedday > 9 && selectedmonth > 9) {
                                        inputDateString = (selectedday + "/" + selectedmonth + "/" + selectedyear);
                                        selectedmonth++;
                                        inputDate.setText(selectedday + "/" + selectedmonth +"/" + selectedyear);
                                    } else if (selectedday < 10 && selectedmonth > 9) {
                                        inputDateString = ("0" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                                        selectedmonth++;
                                        inputDate.setText("0" + selectedday + "/" + selectedmonth +"/" + selectedyear);
                                    } else if (selectedday < 10 && selectedmonth < 10) {
                                        inputDateString = ("0" + selectedday + "/" + "0" + selectedmonth + "/" + selectedyear);
                                        selectedmonth++;
                                        inputDate.setText("0" + selectedday + "/" + "0" + selectedmonth +"/" + selectedyear);
                                    }
                                }
                            }, mYear, mMonth, mDay);
                    mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            inputDate.setText("");
                            inputDateString = "";
                        }
                    });
                    mDatePicker.show();
                }
            });
            final EditText inputDate2 = promptView.findViewById(R.id.inputDate2);
            inputDate2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                    if (selectedday > 9 && selectedmonth > 9) {
                                        inputDateString2 = (selectedday + "/" + selectedmonth + "/" + selectedyear);
                                        selectedmonth++;
                                        inputDate2.setText(selectedday + "/" + selectedmonth +"/" + selectedyear);
                                    } else if (selectedday < 10 && selectedmonth > 9) {
                                        inputDateString2 = ("0" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                                        selectedmonth++;
                                        inputDate2.setText("0" + selectedday + "/" + selectedmonth +"/" + selectedyear);
                                    } else if (selectedday < 10 && selectedmonth < 10) {
                                        inputDateString2 = ("0" + selectedday + "/" + "0" + selectedmonth + "/" + selectedyear);
                                        selectedmonth++;
                                        inputDate2.setText("0" + selectedday + "/" + "0" + selectedmonth +"/" + selectedyear);
                                    }
                                }
                            }, mYear, mMonth, mDay);
                    mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            inputDate2.setText("");
                            inputDateString2 = "";
                        }
                    });
                    mDatePicker.show();
                }
            });
            final Button buttonConfirm = (Button) promptView.findViewById(R.id.buttonConfirmAddItem);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editTextTitle.getText().toString().matches("")) {
                        addItem(new Item(editTextTitle.getText().toString(), editTextDescription.getText().toString(),inputDateString  , inputDateString2), arrayIndividual, adapter, pos);
                        alert.dismiss();
                        inputDateString = "";
                        inputDateString2 = "";
                    } else Toast.makeText(getActivity(),R.string.errorIntroduceTitle,Toast.LENGTH_SHORT).show();

                }
            });
            final Button buttonCancel = (Button) promptView.findViewById(R.id.buttonCancelAddItem);
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            alert.show();
        } catch (Exception e) {
            Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
        }

    }


    protected void showEditDialog(Item item, ItemAdapter adapter, ArrayList listOfItems, int pos) {
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
            View promptView = layoutInflater.inflate(R.layout.fragment_edit_global_item, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity()).setTitle(R.string.dialogEditItem);
            alertDialogBuilder.setView(promptView);
            alertDialogBuilder.setCancelable(false);
            AlertDialog alert = alertDialogBuilder.create();
            alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
            alert.setCanceledOnTouchOutside(true);
            EditText editTextTitle = (EditText) promptView.findViewById(R.id.inputTitle);
            EditText editTextDescription = (EditText) promptView.findViewById(R.id.inputDescription);
            Button buttonConfirm = (Button) promptView.findViewById(R.id.buttonConfirmAddItem);
            EditText inputDate = promptView.findViewById(R.id.inputDate);
            EditText inputDate2 = promptView.findViewById(R.id.inputDate2);


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if (!item.dateStart.matches("")) {
                LocalDate tempDate1 = LocalDate.parse(item.dateStart, formatter);
                Month date1Month = tempDate1.getMonth().plus(1);
                int date1Day = tempDate1.getDayOfMonth();
                int date1Year = tempDate1.getYear();
                LocalDate date1Format = LocalDate.of(date1Year,date1Month,date1Day);
                inputDate.setText(date1Format.format(formatter));

                date1Month = date1Month.minus(1);
                if (date1Day > 9 && date1Month.getValue() > 9) {
                    inputDateString = (date1Day + "/" + date1Month.getValue() + "/" + date1Year);
                } else if (date1Day < 10 && date1Month.getValue() > 9) {
                    inputDateString = ("0" + date1Day + "/" + date1Month.getValue() + "/" + date1Year);
                } else if (date1Day < 10 && date1Month.getValue() < 10) {
                    inputDateString = ("0" + date1Day + "/" + "0" + date1Month.getValue() + "/" + date1Year);
                }
            }

            if (!item.dateEnd.matches("")) {
                LocalDate tempDate2 = LocalDate.parse(item.dateEnd, formatter);
                Month date2Month = tempDate2.getMonth().plus(1);
                int date2Day = tempDate2.getDayOfMonth();
                int date2Year = tempDate2.getYear();
                LocalDate date2Format = LocalDate.of(date2Year,date2Month,date2Day);
                inputDate2.setText(date2Format.format(formatter));

                date2Month = date2Month.minus(1);
                if (date2Day > 9 && date2Month.getValue() > 9) {
                    inputDateString2 = (date2Day + "/" + date2Month.getValue() + "/" + date2Year);
                } else if (date2Day < 10 && date2Month.getValue() > 9) {
                    inputDateString2 = ("0" + date2Day + "/" + date2Month.getValue() + "/" + date2Year);
                } else if (date2Day < 10 && date2Month.getValue() < 10) {
                    inputDateString2 = ("0" + date2Day + "/" + "0" + date2Month.getValue() + "/" + date2Year);
                }
            }

            LocalDate date1 = LocalDate.now();
            LocalDate date2 = LocalDate.now();

            if (!item.dateStart.matches("")) date1 = LocalDate.parse(item.dateStart, formatter);
            if (!item.dateEnd.matches("")) date2 = LocalDate.parse(item.dateEnd, formatter);

            LocalDate dateStart = date1;
            LocalDate dateEnd = date2;

            inputDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                    if (selectedday > 9 && selectedmonth > 9) {
                                        inputDateString = (selectedday + "/" + selectedmonth + "/" + selectedyear);
                                        selectedmonth++;
                                        inputDate.setText(selectedday + "/" + selectedmonth +"/" + selectedyear);
                                    } else if (selectedday < 10 && selectedmonth > 9) {
                                        inputDateString = ("0" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                                        selectedmonth++;
                                        inputDate.setText("0" + selectedday + "/" + selectedmonth +"/" + selectedyear);
                                    } else if (selectedday < 10 && selectedmonth < 10) {
                                        inputDateString = ("0" + selectedday + "/" + "0" + selectedmonth + "/" + selectedyear);
                                        selectedmonth++;
                                        inputDate.setText("0" + selectedday + "/" + "0" + selectedmonth +"/" + selectedyear);
                                    }
                                }
                            }, dateStart.getYear(), dateStart.getMonthValue(), dateStart.getDayOfMonth());
                    mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            inputDateString = "";
                            inputDate.setText("");

                        }
                    });
                    mDatePicker.show();
                }
            });

            inputDate2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                    if (selectedday > 9) {
                                        inputDateString2 = (selectedday + "/" + selectedmonth + "/" + selectedyear);
                                        selectedmonth++;
                                        inputDate2.setText(selectedday + "/" + selectedmonth +"/" + selectedyear);
                                    } else if (selectedday < 10 && selectedmonth > 9) {
                                        inputDateString2 = ("0" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                                        selectedmonth++;
                                        inputDate2.setText("0" + selectedday + "/" + selectedmonth +"/" + selectedyear);
                                    } else if (selectedday < 10 && selectedmonth < 10) {
                                        inputDateString2 = ("0" + selectedday + "/" + "0" + selectedmonth + "/" + selectedyear);
                                        selectedmonth++;
                                        inputDate2.setText("0" + selectedday + "/" + "0" + selectedmonth +"/" + selectedyear);
                                    }
                                }
                            }, dateEnd.getYear(), dateEnd.getMonthValue(), dateEnd.getDayOfMonth());
                    mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            inputDateString2 = "";
                            inputDate2.setText("");
                        }
                    });
                    mDatePicker.show();
                }
            });

            editTextTitle.setText(item.getTitle());
            editTextDescription.setText(item.getDescription());
            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!editTextTitle.getText().toString().matches("")) {
                            item.setTitle(editTextTitle.getText().toString());
                            item.setDescription(editTextDescription.getText().toString());
                            item.setDateStart(inputDateString);
                            item.setDateEnd(inputDateString2);
                            editItem(item, adapter, listOfItems, pos);
                            alert.dismiss();
                            inputDateString = "";
                            inputDateString2 = "";

                        } else Toast.makeText(getActivity(),R.string.errorIntroduceTitle,Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
                    }

                }
            });
            final Button buttonDelete = (Button) promptView.findViewById(R.id.buttonDeleteAddItem);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(item, adapter, listOfItems, pos);
                    inputDateString = "";
                    inputDateString2 = "";
                    alert.dismiss();
                }
            });
            final Button buttonDuplicate = (Button) promptView.findViewById(R.id.buttonDuplicateAddItem);
            buttonDuplicate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    duplicateItem(item, adapter, listOfItems, pos);
                    inputDateString = "";
                    inputDateString2 = "";
                    alert.dismiss();
                }
            });
            alert.show();
        } catch (Exception e) {
            Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
        }
    }

    protected void showEditDialogPrivateName(int pos, ArrayList list, ScrollView scrollView, Button tv_private_title) {
        try {

            LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
            View promptView = layoutInflater.inflate(R.layout.fragment_edit_section, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity()).setTitle(R.string.edit_section);
            alertDialogBuilder.setView(promptView);
            alertDialogBuilder.setCancelable(false);
            AlertDialog alert = alertDialogBuilder.create();
            alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
            alert.setCanceledOnTouchOutside(true);
            EditText editTextTitle = (EditText) promptView.findViewById(R.id.inputTitle_section);
            editTextTitle.setText(arrayOfArrays.get(pos).getTitle());
            Button buttonConfirm = (Button) promptView.findViewById(R.id.buttonConfirmSection);
            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editTextTitle.getText().toString().matches("") && !editTextTitle.getText().toString().equals(arrayOfArrays.get(pos).getTitle())) {
                        arrayOfArrays.get(pos).setTitle(editTextTitle.getText().toString());
                        tv_private_title.setText(editTextTitle.getText().toString());
                        updateGlobalList(pos, list);
                        Toast.makeText(getActivity(),R.string.section_name_changed,Toast.LENGTH_SHORT).show();
                        alert.dismiss();
                    } else Toast.makeText(getActivity(),R.string.errorIntroduceTitle,Toast.LENGTH_SHORT).show();

                }
            });
            final Button buttonDelete = (Button) promptView.findViewById(R.id.buttonDeleteSection);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayOfArrays.remove(pos);
                    globalList.setLists(DataConverter.fromArrayListSection(arrayOfArrays));
                    database.updateItem(globalList);
                    scrollView.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),R.string.list_deleted,Toast.LENGTH_SHORT).show();
                    alert.dismiss();
                }
            });
//        final Button buttonDuplicate = (Button) promptView.findViewById(R.id.buttonDuplicateSection);
//        buttonDuplicate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                duplicateItem(item, adapter, );
////                Toast.makeText(getActivity(),R.string.section_duplicated,Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(),R.string.unavailable,Toast.LENGTH_SHORT);
//                alert.dismiss();
//            }
//        });

            alert.show();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void duplicateItem(Item item, ItemAdapter adapter, ArrayList listOfItems, int pos) {
        try {

            Item itemDuplicated = new Item(item.getTitle(),item.getDescription(), item.getDateStart(), item.getDateEnd());

            //Comprueba si la cadena contiene algún número
            if (itemDuplicated.getTitle().matches(".*\\d+")) {
                String title = itemDuplicated.getTitle().substring(0, itemDuplicated.getTitle().length()-1);
                int number = Integer.parseInt(itemDuplicated.getTitle().substring(itemDuplicated.getTitle().length()-1)) + 1;
                itemDuplicated.setTitle(title+number);
            }
            addItem(itemDuplicated, listOfItems, adapter, pos);
        } catch (Exception e) {
            Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteItem(Item item, ItemAdapter adapter, ArrayList list, int pos) {
        try {
            adapter.notifyItemRemoved(list.indexOf(item));
            list.remove(item);
            updateGlobalList(pos, list);
            System.out.println("Removed item -> " + item);
            countItemsToTitle(pos, list);
        } catch (Exception e) {
            Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
        }
    }

    public void editItem(Item item, ItemAdapter adapter, ArrayList list, int pos) {
        try {

            adapter.notifyItemChanged(list.indexOf(item));
            list.set(list.indexOf(item),item);
            updateGlobalList(pos, list);
            System.out.println("Updated item -> " + item);
        } catch (Exception e) {
            Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
        }
    }

    protected void showDialogCreateSection() {
        try {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View promptView = layoutInflater.inflate(R.layout.fragment_add_global_list, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity()).setTitle(R.string.dialogAddItem);
            alertDialogBuilder.setView(promptView);
            alertDialogBuilder.setCancelable(false);
            AlertDialog alert = alertDialogBuilder.create();
            alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
            alert.setCanceledOnTouchOutside(true);
            final EditText editTextTitle = (EditText) promptView.findViewById(R.id.inputTitleGlobal);
            final Button buttonConfirm = (Button) promptView.findViewById(R.id.buttonConfirmGlobal);
            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editTextTitle.getText().toString().matches("")) {
                        generateNewListView(editTextTitle.getText().toString());
                        alert.dismiss();
                    } else Toast.makeText(getActivity(),R.string.errorIntroduceTitle,Toast.LENGTH_SHORT).show();

                }
            });
            final Button buttonCancel = (Button) promptView.findViewById(R.id.buttonCancelGlobal);
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });

            alert.show();
        } catch (Exception e) {
            Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
        }
    }


}