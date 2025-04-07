package cst8319.group11.project3.grocerylist.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import cst8319.group11.project3.grocerylist.R;
import cst8319.group11.project3.grocerylist.models.Item;
import cst8319.group11.project3.grocerylist.repositories.ItemRepository;

public class ListDetailActivity extends AppCompatActivity {
    private int listID;
    private ItemRepository itemRepository;
    private List<Item> itemList;
    private ListView listViewItems;
    private EditText editItemName, editItemPrice, editItemQuantity, editItemBrand;
    private Button buttonAddItem;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listID = getIntent().getIntExtra("LIST_ID", -1);
        if (listID == -1) {
            Toast.makeText(this, "Invalid List", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        itemRepository = new ItemRepository(getApplication());
        listViewItems = findViewById(R.id.listViewItems);
        editItemName = findViewById(R.id.editItemName);
        editItemPrice = findViewById(R.id.editItemPrice);
        editItemQuantity = findViewById(R.id.editItemQuantity);
        editItemBrand = findViewById(R.id.editItemBrand);
        buttonAddItem = findViewById(R.id.buttonAddItem);

        // 初始化适配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewItems.setAdapter(adapter);

        setupButtonListeners();
        loadItemsForList(listID);
    }

    private void setupButtonListeners() {
        buttonAddItem.setOnClickListener(view -> handleAddItem());

        listViewItems.setOnItemLongClickListener((parent, view, position, id) -> {
            showEditDeleteDialog(itemList.get(position));
            return true;
        });

        listViewItems.setOnItemClickListener((parent, view, position, id) ->
                togglePurchaseStatus(itemList.get(position))
        );
    }

    private void handleAddItem() {
        String itemName = editItemName.getText().toString().trim();
        String brand = editItemBrand.getText().toString().trim();
        String priceStr = editItemPrice.getText().toString().trim();
        String quantityStr = editItemQuantity.getText().toString().trim();

        if (itemName.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                Item newItem = new Item(
                        listID,
                        itemName,
                        brand,
                        Double.parseDouble(priceStr),
                        false,
                        Integer.parseInt(quantityStr),
                        0
                );

                long itemId = itemRepository.addItem(newItem);
                runOnUiThread(() -> {
                    if (itemId != -1) {
                        clearInputFields();
                        loadItemsForList(listID);
                        Toast.makeText(this, "Added item successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (NumberFormatException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void togglePurchaseStatus(Item item) {
        new Thread(() -> {
            boolean newStatus = !item.isPurchased();
            itemRepository.updatePurchaseStatus(item.getItemID(), newStatus);
            runOnUiThread(() -> {
                // 刷新显示的项
                item.setPurchased(newStatus);  // Update the item's purchased status
                refreshListDisplay();          // Refresh the list display
            });
        }).start();
    }

    private void loadItemsForList(int listID) {
        new Thread(() -> {
            itemList = itemRepository.getItemsForList(listID);
            runOnUiThread(this::refreshListDisplay);
        }).start();
    }

    private void refreshListDisplay() {
        if (itemList == null || itemList.isEmpty()) {
            adapter.clear();
            Toast.makeText(this, "This list is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] displayItems = new String[itemList.size()];
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            displayItems[i] = item.getItemName() + " | " +
                    (item.isPurchased() ? "[Purchased]" : "[Not Purchased]");
        }

        adapter.clear();
        adapter.addAll(displayItems);
        adapter.notifyDataSetChanged();
    }

    private void showEditDeleteDialog(Item item) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_item, null);

        EditText dialogName = dialogView.findViewById(R.id.editDialogItemName);
        EditText dialogBrand = dialogView.findViewById(R.id.editDialogItemBrand);
        EditText dialogPrice = dialogView.findViewById(R.id.editDialogItemPrice);
        EditText dialogQuantity = dialogView.findViewById(R.id.editDialogItemQuantity);

        dialogName.setText(item.getItemName());
        dialogBrand.setText(item.getBrand());
        dialogPrice.setText(String.valueOf(item.getPrice()));
        dialogQuantity.setText(String.valueOf(item.getQuantity()));

        new AlertDialog.Builder(this)
                .setTitle("Edit or Delete Item")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String updatedName = dialogName.getText().toString().trim();
                    String updatedBrand = dialogBrand.getText().toString().trim();
                    double updatedPrice = Double.parseDouble(dialogPrice.getText().toString().trim());
                    int updatedQuantity = Integer.parseInt(dialogQuantity.getText().toString().trim());

                    item.setItemName(updatedName);
                    item.setBrand(updatedBrand);
                    item.setPrice(updatedPrice);
                    item.setQuantity(updatedQuantity);

                    itemRepository.updateItem(item);
                    loadItemsForList(listID);
                    Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    itemRepository.deleteItem(item.getItemID());
                    loadItemsForList(listID);
                    Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void clearInputFields() {
        editItemName.setText("");
        editItemPrice.setText("");
        editItemQuantity.setText("");
        editItemBrand.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
