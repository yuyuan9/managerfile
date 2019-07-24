package com.hights.managerfile.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Const {

    public  static String getUploadFileName(){
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random random = new Random();
        int ran = random.nextInt(999999);
        String time=sf.format(new Date())+ran;
        return time;
    }

    public static String pagepath(){

        SimpleDateFormat simleDateFormat = new SimpleDateFormat( "yyyyMMdd" );
        return simleDateFormat.format(new Date(  ));
    }


}
