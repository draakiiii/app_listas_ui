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

import android.provider.ContactsContract;
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
    private ArrayList<Item> arrayOfItems;
    private ArrayList<Section> arrayOfArrays;
    private ArrayList<Button> arrayOfButtons;
    private ArrayList<ItemAdapter> arrayOfAdapters;
    private ArrayList<RecyclerView> arrayOfRecycler;
    private ItemTouchHelper itemTouchHelper;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private Button tv_private_title;
    private ScrollView scrollView;
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
        arrayOfArrays = DataConverter.fromStringSection(globalList.getLists());
        arrayOfButtons = new ArrayList<>();
        arrayOfRecycler = new ArrayList<>();
        arrayOfAdapters = new ArrayList<>();
        mColumnCount = arrayOfArrays.size();
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

        return view;
    }

    // Actualiza los datos de la lista global en la base de datos
    public void updateGlobalList(int pos, ArrayList arrayIndividual) {
        arrayOfArrays.get(pos).setListOfItems(arrayIndividual);
        globalList.setLists(DataConverter.fromArrayListSection(arrayOfArrays));
        database.updateItem(globalList);
    }

    // Método que genera una lista desde cero
    public void generateNewListView(String title) {
        arrayOfArrays.add(new Section(title, new ArrayList<>()));
        int pos = arrayOfArrays.size() - 1;
        ArrayList<Item> arrayOfItemsPrivate = DataConverter.changeItemType(arrayOfArrays.get(pos).getListOfItems());
        Toast.makeText(getActivity(),R.string.list_created,Toast.LENGTH_SHORT).show();
        generateGlobalView(pos, arrayOfItemsPrivate, arrayOfArrays.get(pos).getTitle());
    }

    public void generateGlobalView(int pos, ArrayList<Item> arrayIndividual, String title) {
        scrollView = new ScrollView(getActivity());
        tv_private_title = new Button(getActivity());
        tv_private_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialogPrivateName(pos, arrayIndividual);
            }
        });
        tv_private_title.setText(title);
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
        arrayOfRecycler.get(lastButton).setLayoutManager(layoutManager);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        arrayOfRecycler.get(lastButton).setLayoutParams(lp);

        linearLayout.addView(scrollView);
        scrollView.addView(linearLayoutPrivate);
        linearLayoutPrivate.addView(tv_private_title);
        linearLayoutPrivate.addView(arrayOfRecycler.get(lastButton));
        linearLayoutPrivate.addView(arrayOfButtons.get(lastButton));
        linearLayoutPrivate.setOrientation(LinearLayout.VERTICAL);
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

        simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN , 0) {
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
    }


    public void addItem(Item item, ArrayList arrayIndividual, ItemAdapter adapter, int pos) {
        arrayIndividual.add(item);
        adapter.notifyItemInserted(adapter.getItemCount());
        updateGlobalList(pos, arrayIndividual);
    }

    protected void showInputDialog(ArrayList arrayIndividual, ItemAdapter adapter, int pos) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
        View promptView = layoutInflater.inflate(R.layout.fragment_add_global_item, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity()).setTitle(R.string.dialogAddItem);
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
                    addItem(new Item(editTextTitle.getText().toString(), editTextDescription.getText().toString()), arrayIndividual, adapter, pos);
                    alert.dismiss();
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
    }


    protected void showEditDialog(Item item, ItemAdapter adapter, ArrayList listOfItems, int pos) {
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
                    editItem(item, adapter, listOfItems, pos);
                    alert.dismiss();
                } else Toast.makeText(getActivity(),R.string.errorIntroduceTitle,Toast.LENGTH_SHORT).show();

            }
        });
        final Button buttonDelete = (Button) promptView.findViewById(R.id.buttonDeleteAddItem);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(item, adapter, listOfItems, pos);
                alert.dismiss();
            }
        });
        final Button buttonDuplicate = (Button) promptView.findViewById(R.id.buttonDuplicateAddItem);
        buttonDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duplicateItem(item, adapter, listOfItems, pos);
                alert.dismiss();
            }
        });

        alert.show();
    }

    protected void showEditDialogPrivateName(int pos, ArrayList list) {
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
        final Button buttonDuplicate = (Button) promptView.findViewById(R.id.buttonDuplicateSection);
        buttonDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                duplicateItem(item, adapter, );
                Toast.makeText(getActivity(),R.string.section_duplicated,Toast.LENGTH_SHORT).show();
                alert.dismiss();
            }
        });

        alert.show();
    }

    public void duplicateItem(Item item, ItemAdapter adapter, ArrayList listOfItems, int pos) {

        Item itemDuplicated = new Item(item.getTitle(),item.getDescription());

        //Comprueba si la cadena contiene algún número
        if (itemDuplicated.getTitle().matches(".*\\d+")) {
            String title = itemDuplicated.getTitle().substring(0, itemDuplicated.getTitle().length()-1);
            int number = Integer.parseInt(itemDuplicated.getTitle().substring(itemDuplicated.getTitle().length()-1)) + 1;
            itemDuplicated.setTitle(title+number);
        }
        addItem(itemDuplicated, listOfItems, adapter, pos);
    }

    public void deleteItem(Item item, ItemAdapter adapter, ArrayList list, int pos) {
        adapter.notifyItemRemoved(list.indexOf(item));
        list.remove(item);
        updateGlobalList(pos, list);
        System.out.println("Removed item -> " + item);
    }

    public void editItem(Item item, ItemAdapter adapter, ArrayList list, int pos) {
        adapter.notifyItemChanged(list.indexOf(item));
        list.set(list.indexOf(item),item);
        updateGlobalList(pos, list);
        System.out.println("Updated item -> " + item);
    }

    protected void showDialogCreateSection() {
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
    }


}