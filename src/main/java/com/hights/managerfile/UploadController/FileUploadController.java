package com.hights.managerfile.UploadController;


import com.hights.managerfile.Util.Const;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


@Controller

public class FileUploadController {

    @Value("${upload.maxfile}")
    private String maxfile;

    /*
     * 获取file.html页面
     */
    @RequestMapping("file")
    public String file(){
        return "/file";
    }
    /**
     * 实现文件上传
     */
    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("fileName")MultipartFile file) throws IOException {
        String path = Const.path;
        String fileName = file.getOriginalFilename();
        if(file.isEmpty() &&fileName.isEmpty()){
            return "false";
        }
        int size = (int)file.getSize();

        if (size>Integer.parseInt( maxfile ) ){
            return "文件不能超过85m";
        }

        //根据日期生成文件夹
        String pagepath =pagepath();
        //根据时间生成文件名
        String filepaht =getUploadFileName();
        String extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        filepaht+=extName;
        path=path+"/"+pagepath;
        File dest = new File(path,filepaht);

        if (!dest.exists()) {
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }

        }

        try {
            file.transferTo(dest); //保存文件
            return "true";
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "false";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "false";
        }
    }

    /*
     * 获取multifile.html页面
     */
    @RequestMapping("multifile")
    public String multifile(){
        return "/multifile";
    }

    /**
     * 实现多文件上传
     */

    @RequestMapping(value="multifileUpload")
    @ResponseBody
    public String multifileUpload(HttpServletRequest request){

        List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("fileName");
        if(files.isEmpty()){
            return "false";
        }
        for(MultipartFile file:files){
            String fileName = file.getOriginalFilename();
            int size = (int) file.getSize();
            if (size>Integer.parseInt( maxfile )  ){
                return "文件不能超过85m";
            }
            if(fileName.isEmpty()){
                return "false";
            }
            if(file.isEmpty() ){
                return "false";
            }else{
                //根据日期生成文件夹
                String pagepath =pagepath();
                //根据时间生成文件名
                String filepaht =getUploadFileName();
                String extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                filepaht+=extName;
                String path = Const.path;
                path=path+"/"+pagepath;
                File dest = new File(path,filepaht);
                if (!dest.exists()) {
                    File ff =dest.getParentFile();
                    if (!dest.getParentFile().exists()) {
                        dest.getParentFile().mkdirs();
                    }
                }
                try {
                    file.transferTo(dest);
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "false";
                }
            }
        }
        return "true";
    }

    public  String getUploadFileName(){
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random random = new Random();
        int ran = random.nextInt(999999);
        String time=sf.format(new Date())+ran;
        return time;
    }

    public String pagepath(){

        SimpleDateFormat simleDateFormat = new SimpleDateFormat( "yyyyMMdd" );
        return simleDateFormat.format(new Date(  ));

    }


    @RequestMapping("download")
    public String downLoad(HttpServletResponse response,String filename ){
        String path = Const.path;
        File file = new File(path + "/" + filename);
        if(file.exists()){ //判断文件父目录是否存在
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + filename);

            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;

            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while(i != -1){
                    os.write(buffer);
                    i = bis.read(buffer);
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("----------file download" + filename);
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
}