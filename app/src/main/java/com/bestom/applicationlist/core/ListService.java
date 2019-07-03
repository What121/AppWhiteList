package com.bestom.applicationlist.core;

import com.bestom.applicationlist.bean.IPackage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ListService {

    private static final String whitepath = "/data/data/com.bestom.applicationlist/whitepackage.json";
    private static final String blackpath = "/data/data/com.bestom.applicationlist/blackpackage.json";
    /**
     * 新增包名
     *
     * @param mPackage 包
     * @return 0:新增成功 -1:新增失败
     */
    public static int insertPackage(int flag, IPackage mPackage) {
        int result = 0;
        // 1、先解析Json数据
        List<IPackage> packages = queryPackage(flag);
        for (IPackage bean : packages) {
            if ((mPackage.getId().equals(bean.getId()))||(mPackage.getName().equals(bean.getName()))){
                result = -1;
            }
        }
        if (result == -1) {
            // Log.e("insertPackage", "the new package name is the same");
            System.err.println("insertPackage-----------------the new package name is the same");
            return result;
        }
        // 2、添加入新的数据
        packages.add(mPackage);
        // 3、生成新的Json
        result = createJson(flag,packages);
        return result;
    }

    /**
     * 删除包名
     *
     * @param id id
     * @return 0:删除成功 -1:删除失败
     */
    public static int deletePackage(int flag, String id) {
        // 1、先解析Json数据
        List<IPackage> packages = queryPackage(flag);

        IPackage p = null;
        for (IPackage bean : packages) {
            if (id.equals(bean.getId())) {
                p = bean;
            }
        }
        if (p == null) {
            System.err.println("deletePackage-----------------delete error not package");
            return -1;
        }
        // 2、删除Package
        packages.remove(p);
        // 3、生成新的Json
        return createJson(flag, packages);
    }

    /**
     * 更新包名
     *
     * @param mPackage 包
     * @return 0:更新成功 -1:更新失败
     */
    public static int updatePackage(int flag, IPackage mPackage) {
        // 1、先解析Json数据
        List<IPackage> packages = queryPackage(flag);
        IPackage p = null;
        for (IPackage bean : packages) {
            if (bean.getId().equals(mPackage.getId())) {
                p = bean;
            }
        }
        if (p == null) {
            System.err.println("updatePackage-----------------update error not package");
            return -1;
        }
        // 2、修改Package
        packages.remove(p);
        packages.add(mPackage);
        // 3、生成新的Json
        return createJson(flag, packages);
    }

    /**
     * 查询包名
     * <p>
     * 文件路径
     *
     * @return 返回包数组
     */
    public static List<IPackage> queryPackage(int flag ) {
        List<IPackage> packages = new ArrayList<IPackage>();
        File file;
        if (flag==0){
            file= new File(whitepath);
        }else {
            file=new File(blackpath);
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
                BufferedWriter bw = new BufferedWriter(writer);
                bw.write("[]");
                bw.close();
                writer.close();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                StringBuffer sb = new StringBuffer();
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader br = new BufferedReader(read);
                String lineTxt = null;
                try {
                    while ((lineTxt = br.readLine()) != null) {
                        sb.append(lineTxt);
                    }
                    try {
                        JSONArray list = new JSONArray(sb.toString()); // package数组
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject obj = list.getJSONObject(i);// package对象
                            String id = obj.getString("id"); // 唯一ID
                            String name = obj.getString("name"); // 包名
                            IPackage pkg = new IPackage();
                            pkg.setId(id);
                            pkg.setName(name);
                            packages.add(pkg);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    br.close();
                    read.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return packages;
    }

    /**
     * 生成package的json文件
     *
     * @return result 0:生成成功 -1:生成失败
     */
    public static int createJson(int flag,List<IPackage> packages) {
        int result = -1;
        try {
            File file;
            if (flag==0){
                file= new File(whitepath);
            }else{
                file= new File(blackpath);
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            try {
                BufferedWriter bw = new BufferedWriter(writer);
                String jsonStr = packages.toString();
                bw.write(jsonStr);
                bw.close();
                writer.close();
                result = 0;
                return result;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
