package com.bestom.applicationlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bestom.applicationlist.bean.IPackage;
import com.bestom.applicationlist.core.ListService;
import com.bestom.applicationlist.view.MyAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , RadioGroup.OnCheckedChangeListener ,ListView.OnItemLongClickListener {
    private Context mContext;
    private Activity mActivity;

    TextView tv_title;
    EditText et_pkname;
    Button bt_add;
    RadioGroup mRadioGroup;
    RadioButton rb_white,rb_black;
    ListView mListView;

    Drawable white_drawable,black_drawable;
    private MyAdapter myAdapter;
    private List<IPackage> mIPackages;

    private int flag=0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        //After LOLLIPOP not translucent status bar
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //Then call setStatusBarColor.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        setContentView(R.layout.activity_main);

        init();

//        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//                drawable.getMinimumHeight());
//
        initview();
        getPackages(flag);

    }

    private void init(){
        mContext=this;
        mActivity=this;

        mIPackages = new ArrayList<IPackage>();
        myAdapter = new MyAdapter(this, mIPackages);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initview(){
        tv_title=findViewById(R.id.tv_title);
        et_pkname=findViewById(R.id.edit_text);
        mListView=findViewById(R.id.list_view);
        bt_add=findViewById(R.id.add_btn);
        bt_add.setOnClickListener(this);
        rb_white=findViewById(R.id.main_white);
        rb_black=findViewById(R.id.main_black);
        mRadioGroup=findViewById(R.id.radioGrop);
        mRadioGroup.setOnCheckedChangeListener(this);

        mListView.setAdapter(myAdapter);
        mListView.setOnItemLongClickListener(this);

        white_drawable=getResources().getDrawable(R.mipmap.list_whiteimg);
        // / 这一步必须要做,否则不会显示.
        white_drawable.setBounds(0, 0, white_drawable.getMinimumWidth(),
                white_drawable.getMinimumHeight());
        black_drawable=getResources().getDrawable(R.mipmap.list_blueimg);
        black_drawable.setBounds(0, 0, black_drawable.getMinimumWidth(),
                black_drawable.getMinimumHeight());

        //初始化界面默认进入白名单
        tv_title.setText("WhiteList");
        rb_white.setBackgroundColor( getResources().getColor(R.color.colorPrimaryDark));
        rb_white.setTextColor(getResources().getColor(R.color.colorWhite));
        rb_white.setCompoundDrawables(null,white_drawable ,null,null );

        rb_black.setBackgroundColor( getResources().getColor(R.color.colorWhite));
        rb_black.setTextColor(getResources().getColor(R.color.colorBlack));
        rb_black.setCompoundDrawables(null,black_drawable ,null,null );

    }

    private void getPackages(final int flag){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<IPackage> packages = ListService.queryPackage(flag);
                mIPackages.clear();
                mIPackages.addAll(packages);
                packages.clear();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.notifyDataSetChanged();
                    }
                });

            }
        }).start();
    }

    private void insertPackage() {
        final String appName = et_pkname.getText().toString().trim();
        if (isEmpty(appName)) {
            Toast.makeText(MainActivity.this, "Please enter the packagename", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                IPackage pkg = new IPackage();
                String uuid = UUID.randomUUID().toString();
                pkg.setId(uuid);
                pkg.setName(appName);
                int result = ListService.insertPackage(flag, pkg);
                if (result == 0) {
                    mIPackages.add(pkg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            et_pkname.setText("");
                            et_pkname.setHint(getString(R.string.fill_package));
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (flag==0){
                                Toast.makeText(MainActivity.this, "The package name that add the white list failed", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "The package name that add the black list failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
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
                IPackage pkg = mIPackages.get(position);
                int result = ListService.deletePackage(flag, pkg.getId());
                if (result == 0) {
                    mIPackages.remove(position);
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
                            if (flag==0){
                                Toast.makeText(MainActivity.this, "The package name that removed the white list failed", Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(MainActivity.this, "The package name that removed the black list failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_btn:
                insertPackage();
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.main_white:
                //UI界面
                flag=0;
                tv_title.setText("WhiteList");
                rb_white.setBackgroundColor( getResources().getColor(R.color.colorPrimaryDark));
                rb_white.setTextColor(getResources().getColor(R.color.colorWhite));
                rb_white.setCompoundDrawables(null,white_drawable ,null,null );

                rb_black.setBackgroundColor( getResources().getColor(R.color.colorWhite));
                rb_black.setTextColor(getResources().getColor(R.color.colorBlack));
                rb_black.setCompoundDrawables(null,black_drawable ,null,null );

                //绑定list
                getPackages(flag);

                break;
            case R.id.main_black:
                //UI界面
                flag=1;
                tv_title.setText("BlackList");
                rb_white.setBackgroundColor( getResources().getColor(R.color.colorWhite));
                rb_white.setTextColor(getResources().getColor(R.color.colorBlack));
                rb_white.setCompoundDrawables(null,black_drawable ,null,null );

                rb_black.setBackgroundColor( getResources().getColor(R.color.colorPrimaryDark));
                rb_black.setTextColor(getResources().getColor(R.color.colorWhite));
                rb_black.setCompoundDrawables(null,white_drawable ,null,null );

                //绑定list
                getPackages(flag);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
        //删除选中的包名
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //    指定下拉列表的显示数据
        final String[] cities = { "删除"};
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which){
                    case 0:
                        deletePackage(i);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
        return false;
    }

}
