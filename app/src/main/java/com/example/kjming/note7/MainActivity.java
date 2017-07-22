package com.example.kjming.note7;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.app.AlertDialog;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Item> items;
    private MenuItem mAddItem,mSearchItem,mRevertItem,mDeleteItem;
    private int mSelectCount = 0;
    private NoteDatabase mNoteDatabase;
    private RecyclerView itemList;
    private RecyclerView.Adapter itemAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;
    private TextView showAboutApp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        processViews();
        mNoteDatabase = new NoteDatabase(getApplicationContext());
        items = mNoteDatabase.getAll();
        itemList.setHasFixedSize(true);
        rvLayoutManager = new LinearLayoutManager(this);
        itemList.setLayoutManager(rvLayoutManager);
        processControllers();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            Item item =(Item)data.getExtras().getSerializable("com.example.kjming.note7.Item");
            boolean updateAlarm =false;
            if(requestCode == 0) {
                item = mNoteDatabase.insert(item);
                items.add(item);
                itemAdapter.notifyDataSetChanged();
                updateAlarm = true;

            }else if(requestCode == 1) {
                int position = data.getIntExtra("position",-1);
                if(position != -1) {
                    Item ori = mNoteDatabase.get(item.getId());
                    updateAlarm = (item.getAlarmDatetime()!=ori.getAlarmDatetime());
                    mNoteDatabase.update(item);
                    items.set(position,item);
                    itemAdapter.notifyDataSetChanged();
                }
            }

            if(item.getAlarmDatetime()!=0&&updateAlarm) {
                Intent intent = new Intent(this,AlarmReceiver.class);
                intent.putExtra("id",item.getId());
                PendingIntent pi = PendingIntent.getBroadcast(this,(int)item.getId(),
                        intent,PendingIntent.FLAG_ONE_SHOT);
                AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP,item.getAlarmDatetime(),pi);
            }

        }
    }

        private void processViews() {
            itemList = (RecyclerView) findViewById(R.id.itemList);
            showAboutApp = (TextView)findViewById(R.id.showAboutApp);
        }
    private void processControllers() {
        itemAdapter = new ItemAdapterRV(items) {
            @Override
            public void onBindViewHolder(ViewHolder holder, final int position) {
                super.onBindViewHolder(holder, position);
                holder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Item item = items.get(position);
                        if(mSelectCount>0) {
                            processMenu(item);
                            items.set(position,item);
                        }else {
                            Intent intent = new Intent("com.example.kjming.note7.EDIT_ITEM");
                            intent.putExtra("position",position);
                            intent.putExtra("com.example.kjming.note7.Item",item);
                            startActivityForVersion(intent,1);
                        }
                    }
                });
               holder.rootView.setOnLongClickListener(new View.OnLongClickListener(){
                   @Override
                   public boolean onLongClick(View v) {
                       Item item = items.get(position);
                       processMenu(item);
                       items.set(position,item);
                       return true;
                   }
               });
            }
        };
        itemList.setAdapter(itemAdapter);
    }

    private void processMenu(Item item) {
        if (item!=null) {
            item.setSelected(!item.isSelected());
            if (item.isSelected()) {
                mSelectCount++;
            }else {
                mSelectCount--;
            }

        }
        mAddItem.setVisible(mSelectCount == 0);
        mSearchItem.setVisible(mSelectCount == 0);
        mRevertItem.setVisible(mSelectCount > 0);
        mDeleteItem.setVisible(mSelectCount > 0);

        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        mAddItem = menu.findItem(R.id.add_item);
        mSearchItem = menu.findItem(R.id.search_item);
        mRevertItem = menu.findItem(R.id.revert_item);
        mDeleteItem = menu.findItem(R.id.delet_item);
        processMenu(null);
        return true;
    }

    public void aboutApp(View view) {

        Intent intent = new Intent(this,AboutActivity.class);

        startActivity(intent);

    }

    public void clickMenuItem (MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.search_item:
                break;
            case R.id.add_item:
                final Intent intent = new Intent("com.example.kjming.note7.ADD_ITEM");
                startActivityForVersion(intent,0);
                break;
            case R.id.delet_item:
                if (mSelectCount==0) {
                    break;
                }
                AlertDialog.Builder d = new AlertDialog.Builder(this);
                String message = getString(R.string.delete_item);
                d.setTitle(R.string.delete).setMessage(String.format(message,mSelectCount));
                d.setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick (DialogInterface dialog, int which) {
                        //int index = itemAdapter.getCount() -1;
                        int index =items.size()-1;
                        while (index>-1) {
                            //Item item = itemAdapter.get(index);
                            Item item = items.get(index);
                            if(item.isSelected()) {
                                //itemAdapter.remove(item);
                                items.remove(item);
                                mNoteDatabase.delete(item.getId());
                            }
                            index--;
                        }
                        itemAdapter.notifyDataSetChanged();
                        mSelectCount = 0;
                        processMenu(null);
                    }
                });
                d.setNegativeButton(android.R.string.no,null);
                d.show();
                break;
            case R.id.revert_item:
                for (int i =0;i<items.size();i++) {
                    Item ri = items.get(i);
                    if(ri.isSelected()) {
                        ri.setSelected(false);
                    }
                }
                mSelectCount = 0;
                processMenu(null);
                break;
        }
    }

    public void clickPreferences(MenuItem item) {
        startActivityForVersion(new Intent(this,PrefActivity.class));
    }

    private void startActivityForVersion(Intent intent,int requestCode) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(intent,requestCode,
                    ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
        }else {
            startActivityForResult(intent,requestCode);
        }
    }
    private void startActivityForVersion(Intent intent) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
        }else {
            startActivity(intent);
        }
    }

    public void clickAdd(View view) {
        Intent intent = new Intent("com.example.kjming.note7.ADD_ITEM");
        startActivityForVersion(intent,0);
    }


}
