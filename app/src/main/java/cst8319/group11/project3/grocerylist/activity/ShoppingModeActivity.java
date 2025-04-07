package cst8319.group11.project3.grocerylist.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import cst8319.group11.project3.grocerylist.R;
import cst8319.group11.project3.grocerylist.models.Item;
import cst8319.group11.project3.grocerylist.repositories.ItemRepository;

public class ShoppingModeActivity extends AppCompatActivity {

    private int listID;
    private ItemRepository itemRepository;
    private List<Item> itemList;
    private ListView listViewItems;
    private Button buttonCompleteShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_mode);

        listID = getIntent().getIntExtra("listID", -1);
        itemRepository = new ItemRepository(getApplication());

        listViewItems = findViewById(R.id.listViewItems);
        buttonCompleteShopping = findViewById(R.id.buttonCompleteShopping);

        loadItemsForList(listID);

        // 点击后可模拟“购物完成”，比如给出提示
        buttonCompleteShopping.setOnClickListener(view -> {
            Toast.makeText(this, "Shopping Completed!", Toast.LENGTH_SHORT).show();
            // 可以在此进行更多业务处理，比如更新数据库中的状态
            finish(); // 返回上一页
        });

        // 如果想切换 purchased 状态，可以在 ListView Adapter 中设置点击事件
        listViewItems.setOnItemClickListener((parent, view, position, id) -> {
            Item selectedItem = itemList.get(position);
            // 切换 purchased
            selectedItem.togglePurchased();
            itemRepository.updateItem(selectedItem);
            Toast.makeText(this, selectedItem.getItemName() + " purchased = " + selectedItem.isPurchased(),
                    Toast.LENGTH_SHORT).show();
            // 重新刷新
            loadItemsForList(listID);
        });
    }

    private void loadItemsForList(int listID) {
        itemList = itemRepository.getItemsForList(listID);
        if (itemList != null) {
            String[] itemNames = new String[itemList.size()];
            for (int i = 0; i < itemList.size(); i++) {
                Item it = itemList.get(i);
                itemNames[i] = it.getItemName()
                        + " | Brand: " + it.getBrand()
                        + " | Price: $" + it.getPrice()
                        + " | Qty: " + it.getQuantity()
                        + " | " + (it.isPurchased() ? "[Purchased]" : "[Not Purchased]");
            }
            android.widget.ArrayAdapter<String> adapter =
                    new android.widget.ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemNames);
            listViewItems.setAdapter(adapter);
        }
    }
}
