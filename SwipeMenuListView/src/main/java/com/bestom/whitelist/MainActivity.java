package com.bestom.whitelist;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends Activity {

    EditText mEditText;
    Button mButton;
    SwipeMenuListView mListView;

    private MyAdapter myAdapter;
    private List<IPackage> mPackages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        getPackages();
    }

    private void initView() {
        mEditText = (EditText) findViewById(R.id.edit_text);
        mButton = (Button) findViewById(R.id.add_btn);
        mListView = (SwipeMenuListView) findViewById(R.id.list_view);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPackage();
            }
        });
    }

    private void initData() {
        mPackages = new ArrayList<IPackage>();
        myAdapter = new MyAdapter(this, mPackages);
        mListView.setAdapter(myAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.mipmap.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        break;
                    case 1:
                        // delete
                        deletePackage(position);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        // Left
        mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
    }

    /**
     * 获取包列表
     */
    private void getPackages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<IPackage> packages = WhiteListService.queryPackage();
                mPackages.clear();
                mPackages.addAll(packages);
                packages.clear();
                myAdapter.notifyDataSetChanged();
            }
        }).start();
    }

    /**
     * 删除包名
     */
    private void deletePackage(final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                IPackage pkg = mPackages.get(position);
                int result = WhiteListService.deletePackage(pkg.getId());
                if (result == 0) {
                    mPackages.remove(position);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "The package name that removed the white list failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 新增包名
     */
    private void insertPackage() {
        final String appName = mEditText.getText().toString().trim();
        if (isEmpty(appName)) {
            Toast.makeText(MainActivity.this, "Please enter the", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                IPackage pkg = new IPackage();
                String uuid = UUID.randomUUID().toString();
                pkg.setId(uuid);
                pkg.setName(appName);
                int result = WhiteListService.insertPackage(pkg);
                if (result == 0) {
                    mPackages.add(pkg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEditText.setText("");
                            mEditText.setHint(getString(R.string.fill_package));
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "The package name that add the white list failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    /**
     * return if str is empty
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || str.equalsIgnoreCase("null") || str.isEmpty() || str.equals("")) {
            return true;
        } else {
            return false;
        }
    }
}
