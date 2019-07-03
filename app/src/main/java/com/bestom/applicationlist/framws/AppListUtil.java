//package com.bestom.applicationlist.framws;
//
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.List;
//import android.util.Slog;
//
//public class AppListUtil {
//
//	private static final String TAG = "AppListUtil";
//    public static final String whitepath = "/data/data/com.bestom.applicationlist/whitepackage.json";
//    public static final String blackpath = "/data/data/com.bestom.applicationlist/blackpackage.json";
//
//    /**
//     * 查询包名
//     * <p>
//     * 文件路径
//     *
//     * @return 返回包数组
//     */
//    public static List<String> queryPackage(String path) {
//        List<String> packages = new ArrayList<>();
//        File file = new File(path);
//        if (!file.exists()) {
//			Slog.d(TAG, "create path"+path);
//            try {
//                file.createNewFile();
//                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
//                BufferedWriter bw = new BufferedWriter(writer);
//                bw.write("[]");
//                bw.close();
//                writer.close();
//            } catch (UnsupportedEncodingException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        } else {
//            try {
//                StringBuffer sb = new StringBuffer();
//                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
//                BufferedReader br = new BufferedReader(read);
//                String lineTxt = null;
//                try {
//                    while ((lineTxt = br.readLine()) != null) {
//						Slog.d(TAG, "lineTxt"+lineTxt);
//                        sb.append(lineTxt);
//                    }
//                    try {
//                        JSONArray list = new JSONArray(sb.toString()); // package数组
//                        for (int i = 0; i < list.length(); i++) {
//                            JSONObject obj = list.getJSONObject(i);// package对象
//                            //String id = obj.getString("id"); // 唯一ID
//                            String name = obj.getString("name"); // 包名
//                            packages.add(name);
//                        }
//                    } catch (JSONException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    br.close();
//                    read.close();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            } catch (UnsupportedEncodingException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        return packages;
//    }
//}
