package com.example.new_list.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.new_list.R;
import com.example.new_list.database.GlobalMethods;
import com.example.new_list.database.ItemAdapter;
import com.example.new_list.helper.DataConverter;
import com.example.new_list.model.GlobalList;
import com.example.new_list.model.Item;

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
    private ArrayList<ArrayList> lists;
    private ArrayList<Button> listButtons;
    private ArrayList<Item> listItems;
    private ArrayList<ItemAdapter> listAdapter;
    private ArrayList<RecyclerView> listView;
    private ItemTouchHelper itemTouchHelper;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private static AtomicInteger countButton;
    private int toPos;
    private int fromPos;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PrivateListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
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

    @SuppressLint("MissingInflatedId")
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
        lists = DataConverter.fromString(globalList.getLists());
        mColumnCount = lists.size();
        listButtons = new ArrayList<>();
        listView = new ArrayList<>();
        listAdapter = new ArrayList<>();
        listItems= new ArrayList<>();
        System.out.println("Número de listas: " + mColumnCount);
        addButton = view.findViewById(R.id.addPrivateList);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateNewListView();
            }
        });

        if (mColumnCount > 0) {
            System.out.println("--------- LISTAS ----------");
            for (int i = 0; i < mColumnCount; i++) {
                String data = DataConverter.fromArrayListItem(lists.get(i));
                ArrayList dataList = DataConverter.fromStringItem(data);
                generateListView(dataList, i);
            }
        }
        System.out.println(listItems.size());

        return view;
    }

    public void updateGlobalList(GlobalList globalList, ArrayList lists) {
        globalList.setLists(DataConverter.fromArrayList(lists));
        database.updateItem(globalList);
    }

    public void generateNewListView() {
        lists.add(new ArrayList());
        int positionNew = lists.size() - 1;

        ScrollView scrollView = new ScrollView(getActivity());
        LinearLayout linearLayoutPrivate = new LinearLayout(getActivity(), null, R.style.Theme_App_listas);
        listView.add(new RecyclerView(getActivity()));
        listButtons.add(new Button(getActivity()));
        int lastButton = listButtons.size()-1;

        RelativeLayout.LayoutParams lpButtons = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpButtons.setMargins(10,0,20,0);
        listButtons.get(lastButton).setId(countButton.incrementAndGet());
        listButtons.get(lastButton).setMinimumWidth(250);
        listButtons.get(lastButton).setLayoutParams(lpButtons);
        listButtons.get(lastButton).setText(R.string.addItem);
        listButtons.get(lastButton).setMinHeight(60);
        listButtons.get(lastButton).setPadding(0,0,250,0);
        listButtons.get(lastButton).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_add_24,0,0,0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listView.get(positionNew).setLayoutManager(layoutManager);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        listView.get(positionNew).setLayoutParams(lp);

        linearLayout.addView(scrollView);
        scrollView.addView(linearLayoutPrivate);
        linearLayoutPrivate.addView(listView.get(positionNew));
        linearLayoutPrivate.addView(listButtons.get(lastButton));
        linearLayoutPrivate.setOrientation(LinearLayout.VERTICAL);
        listAdapter.add(new ItemAdapter(lists.get(positionNew), new ItemAdapter.OnItemClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemClick(Item item) {
                showEditDialog(item, listAdapter.get(positionNew), lists.get(listButtons.get(positionNew).getId()-1));
            }
        }));
        listView.get(positionNew).setAdapter(listAdapter.get(positionNew));

        listButtons.get(lastButton).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                showInputDialog(lists.get(lists.size()-1), listAdapter.get(listAdapter.size()-1));

            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN , 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();
                Collections.swap(lists.get(lists.size()-1), fromPosition, toPosition);
                updateGlobalList(globalList, lists);
                listAdapter.get(listAdapter.size()-1).notifyItemMoved(fromPosition,toPosition);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(listView.get(positionNew));

        updateGlobalList(globalList, lists);
    }

    @SuppressLint("ResourceType")
    public void generateListView(ArrayList arrayIndividual, int pos) {
        System.out.println("LISTA CREADA " + pos);
        lists.set(pos, arrayIndividual);

        ScrollView scrollView = new ScrollView(getActivity());
        LinearLayout linearLayoutPrivate = new LinearLayout(getActivity(), null, R.style.Theme_App_listas);
        listView.add(new RecyclerView(getActivity()));

        listButtons.add(new Button(getActivity()));
        int lastButton = listButtons.size()-1;

        RelativeLayout.LayoutParams lpButtons = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpButtons.setMargins(10,0,20,0);
        listButtons.get(lastButton).setId(countButton.incrementAndGet());
        listButtons.get(lastButton).setLayoutParams(lpButtons);
        listButtons.get(lastButton).setMinimumWidth(250);
        listButtons.get(lastButton).setText(R.string.addItem);
        listButtons.get(lastButton).setMinHeight(60);
        listButtons.get(lastButton).setPadding(0,0,250,0);
        listButtons.get(lastButton).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_add_24,0,0,0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listView.get(pos).setLayoutManager(layoutManager);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        listView.get(pos).setLayoutParams(lp);

        linearLayout.addView(scrollView);
        scrollView.addView(linearLayoutPrivate);
        linearLayoutPrivate.addView(listView.get(pos));
        linearLayoutPrivate.addView(listButtons.get(lastButton));
        linearLayoutPrivate.setOrientation(LinearLayout.VERTICAL);
        listAdapter.add(new ItemAdapter(arrayIndividual, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                showEditDialog(item, listAdapter.get(pos), lists.get(listButtons.get(pos).getId()-1));
            }
        }));

        listView.get(pos).setAdapter(listAdapter.get(pos));

        listButtons.get(lastButton).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                showInputDialog(lists.get(listButtons.get(pos).getId()-1), listAdapter.get(pos));
            }
        });

        simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN , 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int toPos = target.getBindingAdapterPosition();
                int fromPos = viewHolder.getBindingAdapterPosition();
                Collections.swap(lists.get(pos), fromPos, toPos);
                listAdapter.get(pos).notifyItemMoved(fromPos,toPos);
                updateGlobalList(globalList, lists);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(listView.get(pos));

    }

    public void addItem(Item item, ArrayList arrayList, ItemAdapter adapter) {
        arrayList.add(item);
        adapter.notifyItemInserted(adapter.getItemCount());
        updateGlobalList(globalList, lists);
    }

    protected void showInputDialog(ArrayList arrayIndividual, ItemAdapter adapter) {
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
                    addItem(new Item(editTextTitle.getText().toString(), editTextDescription.getText().toString()), arrayIndividual, adapter);
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

    protected void showEditDialog(Item item, ItemAdapter adapter, ArrayList arrayList) {
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
                    editItem(item, adapter, arrayList);
                    alert.dismiss();
                } else Toast.makeText(getActivity(),R.string.errorIntroduceTitle,Toast.LENGTH_LONG).show();

            }
        });
        final Button buttonDelete = (Button) promptView.findViewById(R.id.buttonDeleteAddItem);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(item, adapter, arrayList);
                alert.dismiss();
            }
        });
        final Button buttonDuplicate = (Button) promptView.findViewById(R.id.buttonDuplicateAddItem);
        buttonDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duplicateItem(item, adapter, arrayList);
                alert.dismiss();
            }
        });

        alert.show();
    }

    private void moveItem(int oldPos, int newPos) {

    }

    public void duplicateItem(Item item, ItemAdapter adapter, ArrayList arrayList) {

        Item itemDuplicated = new Item(item.getTitle(),item.getDescription());

        //Comprueba si la cadena contiene algún número
        if (itemDuplicated.getTitle().matches(".*\\d+")) {
            String title = itemDuplicated.getTitle().substring(0, itemDuplicated.getTitle().length()-1);
            int number = Integer.parseInt(itemDuplicated.getTitle().substring(itemDuplicated.getTitle().length()-1)) + 1;
            itemDuplicated.setTitle(title+number);
        }
        addItem(itemDuplicated, arrayList, adapter);
    }

    public void deleteItem(Item item, ItemAdapter adapter, ArrayList arrayList) {
        adapter.notifyItemRemoved(arrayList.indexOf(item));
        arrayList.remove(item);
        updateGlobalList(globalList, lists);
        System.out.println("Removed item -> " + item);
    }

    public void editItem(Item item, ItemAdapter adapter, ArrayList arrayList) {
        adapter.notifyItemChanged(arrayList.indexOf(item));
        arrayList.set(arrayList.indexOf(item),item);
        updateGlobalList(globalList, lists);
        System.out.println("Updated item -> " + item);
    }


}