package com.hights.managerfile;

import com.googlecode.jsonrpc4j.spring.JsonServiceExporter;
import com.hights.managerfile.Rpc.api.MyService;
import com.hights.managerfile.Rpc.impl.MyServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
@ServletComponentScan

public class ManagerfileApplication {

    @Value("${upload.filesize}")
    private String filesize;

    public static void main(String[] args) {
        SpringApplication.run( ManagerfileApplication.class, args );

    }
    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }

    @Bean(name = "/rpc/myservice")
    public JsonServiceExporter jsonServiceExporter() {
        JsonServiceExporter exporter = new JsonServiceExporter();
        exporter.setService(myService());
        exporter.setServiceInterface(MyService.class);
        return exporter;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //单个文件最大filesize
        factory.setMaxFileSize(filesize);
        /// 设置总上传数据总大小
       // factory.setMaxRequestSize("1024000KB");
        return factory.createMultipartConfig();

    }


}
