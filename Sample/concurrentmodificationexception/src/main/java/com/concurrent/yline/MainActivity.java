package com.concurrent.yline;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.yline.test.BaseTestActivity;
import com.yline.utils.FileUtil;
import com.yline.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends BaseTestActivity {
    private SampleModel sampleModel;

    @Override
    public void testStart(View view, Bundle savedInstanceState) {
        genData();

        addButton("null", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object data = null;
                String temp = (String) data;

                Map<String, String> map = new ArrayMap<String, String>();
                map.put("", temp);
            }
        });

        addButton("ConcurrentModificationException", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long time = System.currentTimeMillis();
                        LogUtil.v("modifyData start");

                        for (int i = 0; i < 20; i++) {
                            modifyData();

                            long timeA = System.currentTimeMillis();
                            try {
                                bytes = ModelDaoUtil.objectToByte(sampleModel);
                            } catch (NotSerializableException e) {
                                LogUtil.e("", e);
                            }
                            LogUtil.i("objectToByte diffTime = " + (System.currentTimeMillis() - timeA));

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        LogUtil.v("modifyData end diffTime = " + (System.currentTimeMillis() - time));
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long time = System.currentTimeMillis();
                        LogUtil.v("testStart start");
                        for (int i = 0; i < 20; i++) {
                            saveToFile();
                            SampleModel.log(sampleModel);

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        LogUtil.v("testStart end diffTime = " + (System.currentTimeMillis() - time));
                    }
                }).start();

            }
        });
    }

    private void genData() {
        List<String> locationList = new ArrayList<>();
        for (int i = 0; i < 12800; i++) {
            locationList.add("location-" + i);
        }

        List<Long> phoneList = new ArrayList<>();
        for (int i = 0; i < 25600; i++) {
            phoneList.add((long) i);
        }

        List<DemoModel> demoModelList = new ArrayList<>();
        for (int i = 0; i < 51200; i++) {
            demoModelList.add(new DemoModel("name-" + i, "age-" + i));
        }

        sampleModel = new SampleModel("yline", "23", locationList, phoneList, demoModelList);
    }

    private void modifyData() {
        sampleModel.setName(sampleModel.getName() + System.currentTimeMillis());
        sampleModel.setAge(sampleModel.getAge() + System.currentTimeMillis());

        int randomInt = new Random().nextInt(Integer.MAX_VALUE);

        List<String> locationList = sampleModel.getLocation();
        if (randomInt % locationList.size() % 2 == 0) {
            locationList.remove(randomInt % locationList.size());
        } else {
            locationList.add(System.currentTimeMillis() + "");
        }

        List<Long> phoneList = sampleModel.getPhone();
        if (randomInt % phoneList.size() % 2 == 0) {
            phoneList.remove(randomInt % phoneList.size());
        } else {
            phoneList.add(System.currentTimeMillis());
        }

        List<DemoModel> demoModelList = sampleModel.getDemoList();
        if (randomInt % phoneList.size() % 2 == 0) {
            demoModelList.remove(randomInt % phoneList.size());
        } else {
            String name = demoModelList.get(randomInt % phoneList.size()).getName();
            demoModelList.add(new DemoModel(name + System.currentTimeMillis(), "" + System.currentTimeMillis()));
        }
    }

    private byte[] bytes;

    private void saveToFile() {
        String fileName = FileUtil.getPathTop();
        if (!TextUtils.isEmpty(fileName)) {
            fileName += "location_switch_cache.txt";

            FileOutputStream fileOutputStream = null;
            try {
                File loadFile = new File(fileName);
                if (!loadFile.exists()) {
                    loadFile.createNewFile();
                }

                fileOutputStream = new FileOutputStream(loadFile);
                // byte[] bytes = ModelDaoUtil.objectToByte(sampleModel);

                if (null != bytes) {
                    fileOutputStream.write(bytes);
                }
            } catch (FileNotFoundException e) {
                LogUtil.v("saveDownLoadModel" + " FileNotFoundException " + Log.getStackTraceString(e));
            } catch (IOException e) {
                LogUtil.v("saveDownLoadModel" + " IOException " + Log.getStackTraceString(e));
            } finally {
                try {
                    if (null != fileOutputStream) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    LogUtil.v("saveDownLoadModel" + " IOException " + Log.getStackTraceString(e));
                }
            }
        }

        try {
            float size = new FileInputStream(fileName).available();
            LogUtil.v("size = " + size / 1024 + "kb");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SampleModel implements Serializable {
        private String name;
        private String age;
        private List<String> location;
        private List<Long> phone;
        private List<DemoModel> demoList;

        public static void log(SampleModel sampleModel) {
            LogUtil.v("name = " + ",age = " + ", location = " + sampleModel.getLocation().size() + ", phone = " + sampleModel.getPhone().size() + ", demoModel = " + sampleModel.getDemoList().size());
        }

        public SampleModel(String name, String age, List<String> location, List<Long> phone, List<DemoModel> demoList) {
            this.name = name;
            this.age = age;
            this.location = location;
            this.phone = phone;
            this.demoList = demoList;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public List<String> getLocation() {
            return location;
        }

        public void setLocation(List<String> location) {
            this.location = location;
        }

        public List<Long> getPhone() {
            return phone;
        }

        public void setPhone(List<Long> phone) {
            this.phone = phone;
        }

        public List<DemoModel> getDemoList() {
            return demoList;
        }

        public void setDemoList(List<DemoModel> demoList) {
            this.demoList = demoList;
        }
    }

    private static class DemoModel implements Serializable {
        private String name;
        private String age;

        public DemoModel(String name, String age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }
}
