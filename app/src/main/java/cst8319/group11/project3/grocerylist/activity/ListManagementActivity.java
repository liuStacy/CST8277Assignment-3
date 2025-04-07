package cst8319.group11.project3.grocerylist.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import cst8319.group11.project3.grocerylist.R;
import cst8319.group11.project3.grocerylist.models.GroceryList;
import cst8319.group11.project3.grocerylist.repositories.GroceryListRepository;

public class ListManagementActivity extends AppCompatActivity {

    private GroceryListRepository groceryListRepository;
    private List<GroceryList> userLists;
    private ListView listView;
    private EditText editListName;
    private long currentUserId; // 从 Intent 获取

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_management);

        // 启用 ActionBar 返回按钮，点击后返回主页
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        groceryListRepository = new GroceryListRepository(getApplication());

        // 注意：这里必须使用与 MainActivity 中传递的一致的键 "USER_ID"
        currentUserId = getIntent().getLongExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listView = findViewById(R.id.listViewLists);
        editListName = findViewById(R.id.editListName);
        Button buttonAddList = findViewById(R.id.buttonAddList);

        // 加载当前用户的所有列表
        loadUserLists();

        // 添加新列表
        buttonAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String listName = editListName.getText().toString().trim();
                if (!listName.isEmpty()) {
                    GroceryList newList = groceryListRepository.createList(currentUserId, listName);
                    if (newList != null) {
                        Toast.makeText(ListManagementActivity.this, "Added list successfully!", Toast.LENGTH_SHORT).show();
                        loadUserLists(); // 刷新列表显示
                        editListName.setText("");
                    } else {
                        Toast.makeText(ListManagementActivity.this, "Failed to add list", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListManagementActivity.this, "Please enter list name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 短点击列表项：跳转到 ListDetailActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                GroceryList selectedList = userLists.get(position);
                Intent intent = new Intent(ListManagementActivity.this, ListDetailActivity.class);
                intent.putExtra("LIST_ID", selectedList.getListID());
                intent.putExtra("USER_ID", currentUserId); // 如果需要传递用户ID
                startActivity(intent);
            }
        });

        // 长按列表项：弹出对话框进行更新或删除操作
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final GroceryList selectedList = userLists.get(position);
                final String[] options = {"Update List Name", "Delete List"};
                new AlertDialog.Builder(ListManagementActivity.this)
                        .setTitle("Manage List")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    // 更新列表名称
                                    final EditText input = new EditText(ListManagementActivity.this);
                                    input.setText(selectedList.getListName());
                                    new AlertDialog.Builder(ListManagementActivity.this)
                                            .setTitle("Update List Name")
                                            .setMessage("Please enter the new list name:")
                                            .setView(input)
                                            .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    String newName = input.getText().toString().trim();
                                                    if (!newName.isEmpty()) {
                                                        selectedList.setListName(newName);
                                                        groceryListRepository.updateList(selectedList);
                                                        Toast.makeText(ListManagementActivity.this, "The list name has been updated!", Toast.LENGTH_SHORT).show();
                                                        loadUserLists();
                                                    } else {
                                                        Toast.makeText(ListManagementActivity.this, "List name cannot be empty", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                } else if (which == 1) {
                                    // 删除列表
                                    groceryListRepository.deleteList(selectedList.getListID());
                                    Toast.makeText(ListManagementActivity.this, "List deleted!", Toast.LENGTH_SHORT).show();
                                    loadUserLists();
                                }
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    private void loadUserLists() {
        userLists = groceryListRepository.getListsForUser((int) currentUserId);
        if (userLists != null && !userLists.isEmpty()) {
            String[] listNames = new String[userLists.size()];
            for (int i = 0; i < userLists.size(); i++) {
                listNames[i] = userLists.get(i).getListName();
            }
            android.widget.ArrayAdapter<String> adapter =
                    new android.widget.ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listNames);
            listView.setAdapter(adapter);
        } else {
            listView.setAdapter(null);
            Toast.makeText(this, "No lists found, please add one!", Toast.LENGTH_SHORT).show();
        }
    }

    // 处理 ActionBar 返回按钮，返回 MainActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(ListManagementActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
