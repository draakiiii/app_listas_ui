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

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.new_list.R;
import com.example.new_list.database.CategoryMethods;
import com.example.new_list.database.GlobalMethods;
import com.example.new_list.database.ItemAdapter;
import com.example.new_list.helper.DataConverter;
import com.example.new_list.model.Category;
import com.example.new_list.model.GlobalList;
import com.example.new_list.model.Item;
import com.example.new_list.model.Section;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;
import java.time.LocalDate;
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
    public static ArrayList<Category> categories, subCategories;
    private CategoryMethods categoryMethods;
    private GlobalMethods database;
    private ArrayList<Section> arrayOfArrays;
    private ArrayList<Button> arrayOfButtons;
    private ArrayList<TextView> arrayOfCounts;
    private ArrayList<ItemAdapter> arrayOfAdapters;
    private ArrayList<RecyclerView> arrayOfRecycler;
    private ItemTouchHelper itemTouchHelper;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private static AtomicInteger countButton;
    private Spinner categorySpinner, subCategorySpinner;
    private String inputDateStringStart, inputDateStringEnd;
    private Item tempItem;
    private int height, width, toPos, fromPos;
    private ArrayList<String> categoriesName, subcategoriesName;
    private ArrayAdapter<Category> adapterSpinnerCategory, adapterSpinnerSubcategory;
    private HorizontalScrollView horizontalScrollView;
    private EditText editTextTitle, editTextDescription, inputDateStart, inputDateEnd;
    private androidx.appcompat.app.AlertDialog alert;
    private final int EDIT_DIALOG = 0, INPUT_DIALOG = 1;
    private DateTimeFormatter formatter;
    private Category tempCategory;

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
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            mDrawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            linearLayout = view.findViewById(R.id.linearLayoutListPrivate);
            database = new GlobalMethods(getContext());
            categoryMethods = new CategoryMethods(getContext());
            globalList = database.findById(requireArguments().getInt("globallist"));
            arrayOfArrays = DataConverter.fromStringSection(globalList.getLists());
            arrayOfButtons = new ArrayList<>();
            arrayOfCounts = new ArrayList<>();
            arrayOfRecycler = new ArrayList<>();
            arrayOfAdapters = new ArrayList<>();
            mColumnCount = arrayOfArrays.size();
            inputDateStringEnd = "";
            inputDateStringStart = "";
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

            // Crear una lista de strings con las categorías
            categories = new ArrayList<>();
            categories.add(new Category(getContext().getString(R.string.add_category)));
            categories.add(new Category(getContext().getString(R.string.general)));
            categories.addAll(categoryMethods.getAllCategories());

            subCategories = new ArrayList<>();

            // Crear un ArrayAdapter con la lista de categorías
            adapterSpinnerCategory = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
            adapterSpinnerSubcategory = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, subCategories);
            adapterSpinnerCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapterSpinnerSubcategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            categoriesName = new ArrayList<>();
            subcategoriesName = new ArrayList<>();
            for (Category category:categories) {
                categoriesName.add(category.getName());
//                if (DataConverter.fromStringCategories(category.getArrayOfSubcategories()) != null){
//                    for (Category subcategory : DataConverter.fromStringCategories(category.getArrayOfSubcategories())) {
//                        subcategoriesName.add(subcategory.getName());
//                    }
//                }
            }

        } catch (Exception e) {
            showToastError(e);

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
            showToastError(e);
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
            showToastError(e);
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

            // Obtener el padre actual de la vista
            ViewGroup currentParent = (ViewGroup) arrayOfCounts.get(pos).getParent();

            // Si la vista ya tiene un padre, eliminarla del padre actual
            if (currentParent != null) {
                currentParent.removeView(arrayOfCounts.get(pos));
            }

            // Agregar la vista al nuevo padre
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
            showToastError(e);

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
            showToastError(e);
        }
    }

    protected void showInputDialog(ArrayList arrayIndividual, ItemAdapter adapter, int pos) {
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
            View promptView = layoutInflater.inflate(R.layout.fragment_add_item, null);
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this.getActivity(), R.style.CustomMaterialDialog).setTitle(R.string.dialogEditItem);
            alertDialogBuilder.setView(promptView);

            createDialogButtons(pos, arrayIndividual, adapter, alertDialogBuilder, getString(R.string.dialogAddItem), promptView);

//            initDialogElements(alertDialogBuilder, getString(R.string.dialogAddItem), null, null, null);
//            itemDetailDialog(alertDialogBuilder, promptView, alert, arrayIndividual, adapter, pos);
            alert.show();

        } catch (Exception e) {
            showToastError(e);

        }

    }

    protected void showEditDialog(Item item, ItemAdapter adapter, ArrayList listOfItems, int pos) {
        try {

            LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
            View promptView = layoutInflater.inflate(R.layout.fragment_add_item, null);
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this.getActivity(), R.style.CustomMaterialDialog).setTitle(R.string.dialogEditItem);
            alertDialogBuilder.setView(promptView);

            editDialogButtons(item, pos, listOfItems, adapter, alertDialogBuilder, getString(R.string.dialogEditItem), promptView);

            alert.show();
        } catch (Exception e) {
            showToastError(e);
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
                    Section section = arrayOfArrays.get(pos);
                    arrayOfArrays.remove(pos);
                    scrollView.getParent();
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

            Item itemDuplicated = new Item(item.getTitle(),item.getDescription(), item.getDateStart(), item.getDateEnd(), item.getCategory(), item.getSubcategorySelected());

            //Comprueba si la cadena contiene algún número
            if (itemDuplicated.getTitle().matches(".*\\d+")) {
                String title = itemDuplicated.getTitle().substring(0, itemDuplicated.getTitle().length()-1);
                int number = Integer.parseInt(itemDuplicated.getTitle().substring(itemDuplicated.getTitle().length()-1)) + 1;
                itemDuplicated.setTitle(title+number);
            }
            addItem(itemDuplicated, listOfItems, adapter, pos);
        } catch (Exception e) {
            showToastError(e);
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
            showToastError(e);
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
            showToastError(e);
        }
    }

    protected void addCategoryDialog(int type) { //type 0 -> category // type 1 -> subcategory
        try {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View promptView = layoutInflater.inflate(R.layout.fragment_add_global_list, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity()).setTitle(R.string.add_category);
            alertDialogBuilder.setView(promptView);
            alertDialogBuilder.setCancelable(false);
            AlertDialog alert = alertDialogBuilder.create();
            alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
            alert.setCanceledOnTouchOutside(true);
            final EditText editTextInputGlobalTitle = (EditText) promptView.findViewById(R.id.inputTitleGlobal);

            final Button buttonConfirm = (Button) promptView.findViewById(R.id.buttonConfirmGlobal);
            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editTextInputGlobalTitle.getText().toString().matches("")) {
                        Category category = new Category(editTextInputGlobalTitle.getText().toString());
                        if (type == 0) {
                            categoryMethods.insert(category);
                            categories.add(category);
                            categoriesName.add(category.getName());
                            adapterSpinnerCategory.notifyDataSetChanged();
                            categorySpinner.setSelection(categories.size()-1);
                        } else if (type == 1) {
                            subCategories.add(category);
                            ArrayList<Category> newList = new ArrayList(subCategories.subList(2, subCategories.size()));
                            tempCategory.setArrayOfSubcategories(DataConverter.fromArrayListCategories(newList));
                            adapterSpinnerSubcategory.notifyDataSetChanged();
                            subCategorySpinner.setSelection(subCategories.size()-1);
                            categoryMethods.update(tempCategory);
                        }
                        alert.dismiss();
                    } else Toast.makeText(getContext(),R.string.errorIntroduceName ,Toast.LENGTH_SHORT).show();


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
            showToastError(e);
        }
    }

    private void initDialogElements(MaterialAlertDialogBuilder builder, String title, DialogInterface.OnClickListener confirm, DialogInterface.OnClickListener delete, DialogInterface.OnClickListener duplicate) {

        builder.setTitle(title);

        builder.setPositiveButton(R.string.confirm,confirm);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        if (title.matches(getString(R.string.dialogEditItem))) {
            builder.setNegativeButton(R.string.duplicate, duplicate);
            builder.setNeutralButton(R.string.delete, delete);
//            builder.setNeutralButtonIcon(getResources().getDrawable(R.drawable.ic_baseline_delete_24));
        }
    }

    private void itemDetailDialog(MaterialAlertDialogBuilder builder, View promptView, androidx.appcompat.app.AlertDialog alert, ArrayList arrayIndividual, ItemAdapter adapter, int pos, Item item, int typeDialog) {
        try {
            Calendar mcurrentDate = Calendar.getInstance();

            final int mYear = mcurrentDate.get(Calendar.YEAR),
                    mMonth = mcurrentDate.get(Calendar.MONTH),
                    mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            editTextTitle = (EditText) promptView.findViewById(R.id.inputTitle);
            editTextDescription = (EditText) promptView.findViewById(R.id.inputDescription);
            inputDateStart = promptView.findViewById(R.id.inputDate);
            inputDateEnd = promptView.findViewById(R.id.inputDate2);

            final TextView tv_categoryText = (TextView) promptView.findViewById(R.id.tv_spinner_subcategory);

            categorySpinner = (Spinner) promptView.findViewById(R.id.categorySpinner);
            subCategorySpinner = (Spinner) promptView.findViewById(R.id.subcategorySpinner);
            categorySpinner.setAdapter(adapterSpinnerCategory);
            subCategorySpinner.setEnabled(true);
            subCategorySpinner.setAdapter(adapterSpinnerSubcategory);
            subCategorySpinner.setVisibility(View.GONE);
            tv_categoryText.setVisibility(View.GONE);
            categorySpinner.setSelection(1); // Selecciona el segundo índice, que es el general



            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i == 0) {
                        addCategoryDialog(0);
                        subCategorySpinner.setVisibility(View.GONE);
                        tv_categoryText.setVisibility(View.GONE);
                    } else if (i != 1) {
                        subCategorySpinner.setVisibility(View.VISIBLE);
                        tv_categoryText.setVisibility(View.VISIBLE);
                        tempCategory = (Category) categorySpinner.getSelectedItem();
                        initializeSubcategories();

                        adapterSpinnerSubcategory.notifyDataSetChanged();

                        if (item != null) {
                            if (item.getSubcategorySelected() == null || item.getSubcategorySelected().equals("")) subCategorySpinner.setSelection(1);
                            else if (item.getCategory().getArrayOfSubcategories() != null && item.getCategory().getArrayOfSubcategories().contains(item.getSubcategorySelected().getName())) subCategorySpinner.setSelection(item.getSubcategorySelected().getId()+2);
                            else subCategorySpinner.setSelection(1);
                        } else subCategorySpinner.setSelection(1);


                    } else {
                        subCategorySpinner.setVisibility(View.GONE);
                        tv_categoryText.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    subCategorySpinner.setVisibility(View.GONE);
                    tv_categoryText.setVisibility(View.GONE);
                }
            });

            // Se establece el evento de selección en el spinner de subcategorías
            subCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        addCategoryDialog(1);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Código a ejecutar cuando no se selecciona ninguna subcategoría
                }
            });


            if (typeDialog == EDIT_DIALOG) setEditData(item);

            inputDateStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog mDatePicker1 = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                    inputDateStart.setText(formatStringDate(selectedyear, selectedmonth+1, selectedday));
                                }
                            }, mYear, mMonth, mDay);
                    mDatePicker1.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            inputDateStart.setText("");
                            inputDateStringStart = "";
                        }
                    });
                    mDatePicker1.show();
                }
            });

            inputDateEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog mDatePicker2 = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                    inputDateEnd.setText(formatStringDate(selectedyear, selectedmonth+1, selectedday));
                                }
                            }, mYear, mMonth, mDay);
                    mDatePicker2.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            inputDateEnd.setText("");
                            inputDateStringEnd = "";
                        }
                    });
                    mDatePicker2.show();
                }
            });
        } catch (Exception e) {
            showToastError(e);
        }
    }

    public void showToastError(Exception e) {
        Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
        System.out.println("ERROR: " + e.getLocalizedMessage());
    }

    public void createDialogButtons(int pos, ArrayList listOfItems, ItemAdapter adapter, MaterialAlertDialogBuilder builder, String title, View view) {
        try {

            DialogInterface.OnClickListener acceptButton =  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (editTextTitle != null && !editTextTitle.getText().toString().matches("")) {
                        if (subCategorySpinner.getSelectedItem() == null) {
                            addItem(new Item(editTextTitle.getText().toString(), editTextDescription.getText().toString(), inputDateStart.getText().toString()  , inputDateEnd.getText().toString(), (Category) categorySpinner.getSelectedItem()), listOfItems, adapter, pos);
                        } else addItem(new Item(editTextTitle.getText().toString(), editTextDescription.getText().toString(), inputDateStart.getText().toString()  , inputDateEnd.getText().toString(), (Category) categorySpinner.getSelectedItem(), (Category) subCategorySpinner.getSelectedItem()), listOfItems, adapter, pos);
                        alert.dismiss();
                        inputDateStringStart = "";
                        inputDateStringEnd = "";
                    } else Toast.makeText(getActivity(),R.string.errorIntroduceTitle,Toast.LENGTH_SHORT).show();
                }
            };

            initDialogElements(builder, title, acceptButton, null, null);
            alert = builder.create();
            alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
            alert.setCanceledOnTouchOutside(true);
            itemDetailDialog(builder, view, alert, listOfItems, adapter, pos, null, INPUT_DIALOG);
        } catch (Exception e) {
            showToastError(e);
        }
    }

    public void editDialogButtons(Item item, int pos, ArrayList listOfItems, ItemAdapter adapter, MaterialAlertDialogBuilder builder, String title, View view) {
        try {

            DialogInterface.OnClickListener acceptButton =  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!editTextTitle.getText().toString().matches("")) {
                        item.setTitle(editTextTitle.getText().toString());
                        item.setDescription(editTextDescription.getText().toString());
                        item.setDateStart(inputDateStart.getText().toString());
                        item.setDateEnd(inputDateEnd.getText().toString());
                        item.setCategory((Category) categorySpinner.getSelectedItem());
                        if (subCategorySpinner.getSelectedItem() != null) item.setSubcategorySelected((Category) subCategorySpinner.getSelectedItem());
                        editItem(item, adapter, listOfItems, pos);
                        alert.dismiss();
                        inputDateStringStart = "";
                        inputDateStringEnd = "";

                    } else Toast.makeText(getActivity(),R.string.errorIntroduceTitle,Toast.LENGTH_SHORT).show();
                }
            };

            DialogInterface.OnClickListener duplicateButton =  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    duplicateItem(item, adapter, listOfItems, pos);
                    inputDateStringStart = "";
                    inputDateStringEnd = "";
                    alert.dismiss();
                }
            };

            DialogInterface.OnClickListener deleteButton =  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteItem(item, adapter, listOfItems, pos);
                    inputDateStringStart = "";
                    inputDateStringEnd = "";
                    alert.dismiss();
                }
            };

            initDialogElements(builder, title, acceptButton, deleteButton, duplicateButton);
            alert = builder.create();
            alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
            alert.setCanceledOnTouchOutside(true);
            itemDetailDialog(builder, view, alert, listOfItems, adapter, pos, item,EDIT_DIALOG);
        } catch (Exception e) {
            showToastError(e);
        }
    }

    public String formatStringDate(int selectedyear, int selectedmonth, int selectedday) {
        try {
            DecimalFormat twoDigitFormat = new DecimalFormat("00");
            return twoDigitFormat.format(selectedday) + "/" + twoDigitFormat.format(selectedmonth) + "/" + selectedyear;

        } catch (Exception e) {
            showToastError(e);
            return null;
        }
    }

    public void formatStringDateFromString(String date, EditText inputDateVariable, String inputDateStringVariable) {
        try {

            LocalDate localDate = LocalDate.parse(date, formatter);
            int day = localDate.getDayOfMonth();
            int month = localDate.getMonthValue();
            int year = localDate.getYear();
            inputDateStringVariable = formatStringDate(year, month, day);
            inputDateVariable.setText(inputDateStringVariable);
        } catch (Exception e) {
            showToastError(e);
        }
    }

    public void setEditData(Item item) {
        try {
            tempItem = item;

            if (!item.dateStart.matches("")) {
                formatStringDateFromString(item.getDateStart(), inputDateStart, inputDateStringStart);
            }

            if (!item.dateEnd.matches("")) {
                formatStringDateFromString(item.getDateEnd(), inputDateEnd, inputDateStringEnd);
            }

            editTextTitle.setText(item.getTitle());

            editTextDescription.setText(item.getDescription());

            if (categoriesName.contains(item.getCategory().getName())) categorySpinner.setSelection(categoriesName.indexOf(item.getCategory().getName())); // Selecciona el índice de la categoría del item
            tempCategory = item.getCategory();
            initializeSubcategories();
            if (item.getSubcategorySelected() != null && !item.getSubcategorySelected().equals("")) subCategorySpinner.setSelection(adapterSpinnerSubcategory.getPosition(item.getSubcategorySelected()));

        } catch (Exception e) {
            showToastError(e);
        }
    }

    public void initializeSubcategories() {
        subCategories.clear();
        subCategories.add(new Category(getContext().getString(R.string.add_category)));
        subCategories.add(new Category(getContext().getString(R.string.general)));
        if (tempCategory.getArrayOfSubcategories() != null && tempCategory.getArrayOfSubcategories() != (""))
            subCategories.addAll(DataConverter.fromStringCategories(tempCategory.getArrayOfSubcategories()));
    }


}