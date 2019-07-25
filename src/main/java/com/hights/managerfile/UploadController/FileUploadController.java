package com.hights.managerfile.UploadController;


import com.hights.managerfile.Util.Const;
import com.hights.managerfile.Util.ZipUtil;
import org.apache.commons.lang3.StringUtils;
import org.jodconverter.DocumentConverter;
import org.jodconverter.office.OfficeException;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


@Controller
public class FileUploadController {

    @Autowired
    private DocumentConverter documentConverter;


    @Value("${upload.maxfile}")
    private String maxfile;
    @Value("${upload.path}")
    private String rootpath;
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
    public String fileUpload(@RequestParam("file")MultipartFile file) throws IOException {
        String path =rootpath+"/uploadFiles/uploadFile";
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
            String subfix = getExtension(dest.getPath());
            if ((subfix.indexOf("xls") != -1) || (subfix.indexOf("docx") != -1) || (subfix.indexOf("ppt") != -1) || (subfix.indexOf("doc") != -1)) {
                if (!StringUtils.isEmpty(dest.getName()))
                {
                    File target = new File(getExtensiontow(dest.getPath(), "") + ".pdf");
                    if (!target.exists()) {
                        try {
                            documentConverter.convert(dest).to(target).execute();
                            dest.delete();
                            return "/uploadFiles/uploadFile/" + pagepath + "/" + target.getName();
                        } catch (OfficeException e) {
                            e.printStackTrace();
                            return "false";
                        }
                    }
                }
                return "false";
            }
            return "/uploadFiles/uploadFile/" +pagepath + "/" + dest.getName();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
            return "false";
        }
        catch (IOException e) {
            e.printStackTrace();
        }return "false";
    }

    public String getExtension(String filename)
    {
        return getExtension(filename, "");
    }
    public String getExtension(String filename, String defExt) {
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');

            if ((i > -1) && (i < filename.length() - 1)) {
                return filename.substring(i + 1);
            }
        }
        return defExt;
    }
    public String getExtensiontow(String filename, String defExt) {
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');

            if ((i > -1) && (i < filename.length() - 1)) {
                return filename.substring(0, i);
            }
        }
        return defExt;
    }

    //将多个文件打压成一个压缩包
    @RequestMapping("filezip")
    @ResponseBody
    public  String filezip (String pathurl)  {
        List<File> fileList = new ArrayList<>();
        String [] paths = pathurl.split( "," );
        for(String path: paths){
            File dest = new File(rootpath+"/"+path);
            if(dest.exists()){
                fileList.add( dest );
            }else{
               return "false";
            }
        }
        FileOutputStream fos2 = null;
        try {
            String folr = pagepath();
            String pagepath = rootpath+"/uploadFiles/uploadFile/"+folr+"/"+getUploadFileName()+".zip";
            File file = new File(pagepath);
            if(!file.exists()){
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
            }
            fos2 = new FileOutputStream(file);
            ZipUtil.toZip(fileList, fos2);
            return "/uploadFiles/uploadFile/" +folr+ "/" + file.getName();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "false";
        }
    }
       /* for(int i=0;i<paths.length;i++){
            try {
                File source = new File(rootpath+"/"+paths[i]);
                File dest = new File(rootpath+"/"+folder+"/"+folder+"/"+source.getName());
                if(!dest.exists()){
                    if (!dest.getParentFile().exists()) {
                        dest.getParentFile().mkdirs();
                    }
                }
                Files.copy(source.toPath(), dest.toPath());
            }catch (Exception e){
                e.printStackTrace();
            }

        }
*/


  /*  public static void main(String[] arg){
        List<File> fileList = new ArrayList<>();
        fileList.add(new File("C:/Users/lhy/Desktop/启信宝.txt"));
        fileList.add(new File("C:/Users/lhy/Desktop/复习.docx"));
        FileOutputStream fos2 = null;
        try {
            String pagepath = "C:/Users/lhy/Desktop/test.zip";
            File file = new File(pagepath);
            if(!file.exists()){
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
            }
            fos2 = new FileOutputStream(file);
            ZipUtil.toZip(fileList, fos2);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
*/

/*  public static void main(String[] arg){
      File  file = new File("E:/filepath/uploadFiles/uploadFile/20190725/20190725102836378565924.txt");
      System.out.println(file.getParent()  );
      System.out.println(file.getAbsolutePath()  );
      System.out.println(file.getName() );
      System.out.println(file.getPath());

  }*/

//    public static void main(String[] arg){
//      /String folder =getUploadFileName();*/
//        File source =  new File( "E:/filepath/test.txt" );
//        File dest = new File("E:/filepath"+"/"+folder+"/" + source.getName());
//        if(!dest.exists()){
//            if (!dest.getParentFile().exists()) {
//                dest.getParentFile().mkdirs();
//            }
//        }
//        try {
//            Files.copy(source.toPath(), dest.toPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
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
                String path = rootpath;
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
        String path = rootpath;
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
