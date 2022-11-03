package com.example.new_list.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.new_list.R;
import com.example.new_list.database.GlobalMethods;
import com.example.new_list.database.ItemAdapter;
import com.example.new_list.helper.DataConverter;
import com.example.new_list.model.GlobalList;
import com.example.new_list.model.Item;
import com.example.new_list.model.Section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A fragment representing a list of Items.
 */
public class PrivateListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount;
    private DrawerLayout mDrawer;
    private Button addButton;
    private LinearLayout linearLayout;
    private GlobalList globalList;
    private GlobalMethods database;;
    private ArrayList<ArrayList> arrayOfArrays;
    private ArrayList<Button> arrayOfButtons;
    private ArrayList<Item> arrayOfItems;
    private ArrayList<ItemAdapter> arrayOfAdapters;
    private ArrayList<RecyclerView> arrayOfRecycler;
    private ItemTouchHelper itemTouchHelper;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private Button tv_private_title;
    private static AtomicInteger countButton;
    private int toPos;
    private int fromPos;

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
        toPos = -1;
        fromPos = -1;
        countButton = new AtomicInteger(0);
        mDrawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        linearLayout = view.findViewById(R.id.linearLayoutListPrivate);
        database = new GlobalMethods(getContext());
        globalList = database.findById(requireArguments().getInt("globallist"));
        arrayOfArrays = DataConverter.fromString(globalList.getLists());
        arrayOfButtons = new ArrayList<>();
        arrayOfRecycler = new ArrayList<>();
        arrayOfAdapters = new ArrayList<>();
        mColumnCount = arrayOfArrays.size();
        System.out.println("Número de listas: " + mColumnCount);
        addButton = view.findViewById(R.id.addPrivateList);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateNewListView();
            }
        });

        //Si hay columnas, empieza a generarlas
        if (mColumnCount > 0) {
            System.out.println("--------- LISTAS ----------");
            for (int i = 0; i < mColumnCount; i++) {
                ArrayList<Section> individualArrayOfSection = DataConverter.changeSectionType(arrayOfArrays.get(i));
                if (individualArrayOfSection.size() == 0) individualArrayOfSection.add(new Section("TEST", new ArrayList<>()));

                    ArrayList<Item> individualArrayOfItem = DataConverter.changeItemType(individualArrayOfSection.get(i).getListOfItems());
                    Section section = individualArrayOfSection.get(i);
                    section.setListOfItems(individualArrayOfItem);
                    generateListView(section, i);
            }
        }

        return view;
    }

    // Actualiza los datos de la lista global en la base de datos
    public void updateGlobalList() {
        globalList.setLists(DataConverter.fromArrayList(arrayOfArrays));
        database.updateItem(globalList);
    }

    // Método que genera una lista desde cero
    public void generateNewListView() {
        arrayOfArrays.add(new ArrayList());
        int positionNew = arrayOfArrays.size() - 1;
        ArrayList<Section> individualArrayOfSections = DataConverter.changeSectionType(arrayOfArrays.get(positionNew));
        individualArrayOfSections.add(new Section("TEST", new ArrayList<>()));
        int positionSection = individualArrayOfSections.size() - 1;
        Section section = individualArrayOfSections.get(positionSection);
        Toast.makeText(getActivity(),R.string.list_created,Toast.LENGTH_SHORT).show();
        updateGlobalList();
        generateGlobalView(positionSection, section);
    }

    // Método que genera las listas ya creadas
    public void generateListView(Section section , int pos) {
        generateGlobalView(pos, section);
    }

    public void generateGlobalView(int pos, Section section) {
        arrayOfItems = DataConverter.changeItemType(section.getListOfItems());
        ScrollView scrollView = new ScrollView(getActivity());
        tv_private_title = new Button(getActivity());
        tv_private_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialogPrivateName(pos);
            }
        });
        tv_private_title.setText("TEST");
        tv_private_title.setBackgroundResource(R.drawable.button_title);
        LinearLayout linearLayoutPrivate = new LinearLayout(getActivity(), null, R.style.Theme_App_listas);
        arrayOfRecycler.add(new RecyclerView(getActivity()));

        arrayOfButtons.add(new Button(getActivity()));
        int lastButton = arrayOfButtons.size()-1;

        RelativeLayout.LayoutParams lpButtons = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpButtons.setMargins(10,0,20,0);
        arrayOfButtons.get(lastButton).setId(countButton.incrementAndGet());
        arrayOfButtons.get(lastButton).setLayoutParams(lpButtons);
        arrayOfButtons.get(lastButton).setMinimumWidth(250);
        arrayOfButtons.get(lastButton).setText(R.string.addItem);
        arrayOfButtons.get(lastButton).setMinHeight(60);
        arrayOfButtons.get(lastButton).setPadding(0,0,250,0);
        arrayOfButtons.get(lastButton).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_add_24,0,0,0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        arrayOfRecycler.get(pos).setLayoutManager(layoutManager);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        arrayOfRecycler.get(pos).setLayoutParams(lp);

        linearLayout.addView(scrollView);
        scrollView.addView(linearLayoutPrivate);
        linearLayoutPrivate.addView(tv_private_title);
        linearLayoutPrivate.addView(arrayOfRecycler.get(pos));
        linearLayoutPrivate.addView(arrayOfButtons.get(lastButton));
        linearLayoutPrivate.setOrientation(LinearLayout.VERTICAL);
        arrayOfAdapters.add(new ItemAdapter(arrayOfItems, new ItemAdapter.OnItemClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemClick(Item item) {
                showEditDialog(item, arrayOfAdapters.get(pos));
            }
        }));

        arrayOfRecycler.get(pos).setAdapter(arrayOfAdapters.get(pos));

        arrayOfButtons.get(lastButton).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                showInputDialog(arrayOfArrays.get(arrayOfButtons.get(pos).getId()-1), arrayOfAdapters.get(pos));
            }
        });

        simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN , 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int toPos = target.getBindingAdapterPosition();
                int fromPos = viewHolder.getBindingAdapterPosition();
                Collections.swap(arrayOfArrays.get(pos), fromPos, toPos);
                arrayOfAdapters.get(pos).notifyItemMoved(fromPos,toPos);
                updateGlobalList();
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(arrayOfRecycler.get(pos));

        updateGlobalList();
    }

    public void addItem(Item item, ItemAdapter adapter) {
        arrayOfItems.add(item);
        adapter.notifyItemInserted(adapter.getItemCount());
        updateGlobalList();
    }

    protected void showInputDialog(ArrayList arrayIndividual, ItemAdapter adapter) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.fragment_add_global_item, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity()).setTitle(R.string.dialogAddItem);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);
        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
        alert.setCanceledOnTouchOutside(true);
        final EditText editTextTitle = (EditText) promptView.findViewById(R.id.inputTitle);
        final EditText editTextDescription = (EditText) promptView.findViewById(R.id.inputDescription);
        final Button buttonConfirm = (Button) promptView.findViewById(R.id.buttonConfirmAddItem);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextTitle.getText().toString().matches("")) {
                    addItem(new Item(editTextTitle.getText().toString(), editTextDescription.getText().toString()),adapter);
                    alert.dismiss();
                } else Toast.makeText(getActivity(),R.string.errorIntroduceTitle,Toast.LENGTH_LONG).show();

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
    }

    protected void showEditDialog(Item item, ItemAdapter adapter) {
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
        editTextTitle.setText(item.getTitle());
        editTextDescription.setText(item.getDescription());
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextTitle.getText().toString().matches("")) {
                    item.setTitle(editTextTitle.getText().toString());
                    item.setDescription(editTextDescription.getText().toString());
                    editItem(item, adapter);
                    alert.dismiss();
                } else Toast.makeText(getActivity(),R.string.errorIntroduceTitle,Toast.LENGTH_LONG).show();

            }
        });
        final Button buttonDelete = (Button) promptView.findViewById(R.id.buttonDeleteAddItem);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(item, adapter);
                alert.dismiss();
            }
        });
        final Button buttonDuplicate = (Button) promptView.findViewById(R.id.buttonDuplicateAddItem);
        buttonDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duplicateItem(item, adapter);
                alert.dismiss();
            }
        });

        alert.show();
    }

    protected void showEditDialogPrivateName(int pos) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
        View promptView = layoutInflater.inflate(R.layout.fragment_edit_section, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity()).setTitle(R.string.edit_section);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);
        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
        alert.setCanceledOnTouchOutside(true);
        EditText editTextTitle = (EditText) promptView.findViewById(R.id.inputTitle_section);
        Button buttonConfirm = (Button) promptView.findViewById(R.id.buttonConfirmSection);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextTitle.getText().toString().matches("")) {
                    tv_private_title.setText(editTextTitle.getText().toString());
                    alert.dismiss();
                } else Toast.makeText(getActivity(),R.string.errorIntroduceTitle,Toast.LENGTH_LONG).show();

            }
        });
        final Button buttonDelete = (Button) promptView.findViewById(R.id.buttonDeleteSection);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayOfArrays.remove(pos);
                updateGlobalList();
                Toast.makeText(getActivity(),R.string.list_deleted,Toast.LENGTH_LONG).show();
                alert.dismiss();
            }
        });
        final Button buttonDuplicate = (Button) promptView.findViewById(R.id.buttonDuplicateSection);
        buttonDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                duplicateItem(item, adapter, arrayList);
                alert.dismiss();
            }
        });

        alert.show();
    }

    public void duplicateItem(Item item, ItemAdapter adapter) {

        Item itemDuplicated = new Item(item.getTitle(),item.getDescription());

        //Comprueba si la cadena contiene algún número
        if (itemDuplicated.getTitle().matches(".*\\d+")) {
            String title = itemDuplicated.getTitle().substring(0, itemDuplicated.getTitle().length()-1);
            int number = Integer.parseInt(itemDuplicated.getTitle().substring(itemDuplicated.getTitle().length()-1)) + 1;
            itemDuplicated.setTitle(title+number);
        }
        addItem(itemDuplicated, adapter);
    }

    public void deleteItem(Item item, ItemAdapter adapter) {
        adapter.notifyItemRemoved(arrayOfItems.indexOf(item));
        arrayOfItems.remove(item);
        updateGlobalList();
        System.out.println("Removed item -> " + item);
    }

    public void editItem(Item item, ItemAdapter adapter) {
        adapter.notifyItemChanged(arrayOfItems.indexOf(item));
        arrayOfItems.set(arrayOfItems.indexOf(item),item);
        updateGlobalList();
        System.out.println("Updated item -> " + item);
    }


}