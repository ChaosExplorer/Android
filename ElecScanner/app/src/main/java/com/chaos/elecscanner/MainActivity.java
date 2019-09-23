package com.chaos.elecscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.github.mjdev.libaums.partition.Partition;
import com.github.mjdev.libaums.fs.FileSystem;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static final String USB_TABLE_NAME = "Table.xls";

    private UsbMassStorageDevice dev;

    private Button scanBtn;
    private TextView userid_view;
    private TextView username_view;
    private TextView address_view;
    private TextView tableId_view;

    private boolean isTableLoaded;
    private Map<String, Info> table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setIcon();
        isTableLoaded = false;
        scanBtn = (Button) findViewById(R.id.scan_button);
        table = new HashMap<String, Info>();

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("扫描条形码");
                integrator.setCameraId(0);  // 使用默认的相机
                integrator.setBeepEnabled(true); // 扫到码后播放提示音
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });

        if (!loadUSB()) {
            Toast.makeText(getApplicationContext(), "没有检测到U盘", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "检测到U盘", Toast.LENGTH_SHORT).show();
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
           // result.setText("CONTENT: " + scanningResult.getContents());
            String tableId = scanningResult.getContents();

            if(!isTableLoaded) {
                if (!loadUSBTable(dev)) {
                    Toast.makeText(getApplicationContext(), "没有检测到存入U盘的电表文件", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Info ret = table.get(tableId);
            if (ret != null) {
                userid_view.setText(ret.getUserId());
                username_view.setText(ret.getName());
                address_view.setText(ret.getAddress());
                tableId_view.setText(tableId);
            }
            else {
                Intent intent2 = new Intent();
                intent2.setClass(this, NoRetActivity.class);
                startActivity(intent2);
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "No data received", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean loadUSB() {
        UsbManager usbM = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(this);
        if (devices.length < 1)
            return false;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        dev = devices[0];
        if (!usbM.hasPermission(dev.getUsbDevice())) {
            usbM.requestPermission(dev.getUsbDevice(), pendingIntent);
        }

        return true;
    }

    private boolean loadUSBTable(UsbMassStorageDevice dev) {

        try {
            //初始化
            dev.init();
            //获取partition
            Partition partition = dev.getPartitions().get(0);
            FileSystem currentFs = partition.getFileSystem();
            //获取根目录
            UsbFile root = currentFs.getRootDirectory();
            for (UsbFile file: root.listFiles()) {
                if(!file.isDirectory() && file.getName().equals(USB_TABLE_NAME)) {
                    UsbFileInputStream uis = new UsbFileInputStream(file);
                    Workbook book = Workbook.getWorkbook(uis);
                    Sheet sheet = book.getSheet(0);
                    int rows = sheet.getRows();

                    Cell cell;
                    String tableid;
                    for (int i = 0; i < rows; ++i){
                        cell = sheet.getCell(7,i);
                        if (cell == null)
                            continue;
                        tableid = cell.getContents();
                        if (tableid.isEmpty() || !Info.isNumeric(tableid))
                            continue;

                        Info line = new Info();
                        line.setUserId(sheet.getCell(2,i).getContents());
                        line.setName(sheet.getCell(3,i).getContents());
                        line.setAddress(sheet.getCell(4,i).getContents());
                        table.put(tableid, line);
                    }

                    isTableLoaded = true;
                    return true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void setIcon(){
        Drawable icon = getResources().getDrawable(R.mipmap.ic_userid_foreground);
        icon.setBounds(0,0,120,110);
        TextView idview = findViewById(R.id.textView1);
        idview.setCompoundDrawables(icon,null,null,null);

        Drawable icon2 = getResources().getDrawable(R.mipmap.ic_username_foreground);
        icon2.setBounds(0,0,120,110);
        TextView idview3 = findViewById(R.id.textView3);
        idview3.setCompoundDrawables(icon2,null,null,null);

        Drawable icon3 = getResources().getDrawable(R.mipmap.ic_address_foreground);
        icon3.setBounds(0,0,120,110);
        TextView idview5 = findViewById(R.id.textView5);
        idview5.setCompoundDrawables(icon3,null,null,null);

        Drawable icon4 = getResources().getDrawable(R.mipmap.ic_table_foreground);
        icon4.setBounds(0,0,120,110);
        TextView idview7 = findViewById(R.id.textView7);
        idview7.setCompoundDrawables(icon4,null,null,null);

        userid_view = findViewById(R.id.textView2);
        username_view = findViewById(R.id.textView4);
        address_view = findViewById(R.id.textView6);
        tableId_view = findViewById(R.id.textView8);
    }
}

class Info {
    private String name;
    private String address;
    private String userId;

    Info() { super();}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
}
