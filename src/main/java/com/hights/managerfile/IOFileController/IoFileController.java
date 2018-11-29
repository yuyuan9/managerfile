package com.hights.managerfile.IOFileController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Controller
public class IoFileController {

    @RequestMapping(value = "/seekExperts")
    @ResponseBody
    public String createFolw(HttpServletRequest request, HttpServletResponse response) throws MalformedURLException {
        String path = "http://localhost:8080/uploadFiles/uploadFile/20181126/";
        URL url;
        try {
            url = new URL(path+"20181126172501_361.png");
            HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
            String messagePic = urlconn.getHeaderField(0);//文件存在‘HTTP/1.1 200 OK’ 文件不存在 ‘HTTP/1.1 404 Not Found’
            if (messagePic.startsWith("HTTP/1.1 200")) {
                OutputStream os = null;
                os = response.getOutputStream();
                InputStream is = url.openStream();
                int count = 0;
                int i = is.available(); // 得到文件大小
                byte[] buffer = new byte[i];
                while ((count = is.read(buffer)) != -1) {
                    os.write( buffer, 0, count );
                    os.flush();

                }
                is.close();
                os.close();
            }else{
                return "文件不存在";
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "ok";
    }

}
