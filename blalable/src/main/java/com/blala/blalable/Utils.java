package com.blala.blalable;


import android.util.Log;

import org.apache.commons.lang.StringUtils;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

/**
 * Created by Admin
 * Date 2021/9/16
 */
public class Utils {
    private static final String HEX_CHARS = "0123456789ABCDEF";

    public static int indexPosition = 0;

    public static void setIndexPosition(int indexPosition) {
        Utils.indexPosition = indexPosition;
    }

    public static int getIndexPosition() {
        return indexPosition;
    }

    //一个byte表示
    public static String file1 = "flashcloudpic";
    //两个字节表示
    public  static String file2 = "flashcloudaddr";
    //四个字节表示
    public  static String file3 = "flashcloudwid";
    //字节转换字节
    public static String file4 = "flashcloudcoo";


    public static int byte2Int(byte bt) {
        return bt & 0x000000ff;
    }

    public static int byteToInt(byte data) {
        return (data & 0x00ff);
    }


    public static String formatBtArrayToString(byte[] bt){
        StringBuilder stringBuilder = new StringBuilder();
        for(Byte b : bt){
            stringBuilder.append(String.format("%02x",b));
        }
        return stringBuilder.toString();
    }

    /**
     * 4位字节数组转换为整型
     * @param b
     * @return
     */
    public static int byte2Int(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }


    private static byte toByte(char c) {
        return (byte) HEX_CHARS.indexOf(c);
    }
    /**
     * 把16进制字符串转换成字节数组 *     @param hex * @return
     */
    public static byte[] hexStringToByte(String hex) {
        if (hex == null ){
            return null;
        }
        if(hex.length()%2!=0)
            hex="0"+hex;
        hex = hex.trim();
        hex =  hex.toUpperCase();
        int len = (hex.length() / 2);
        char[] hexChars = hex.toCharArray();
        byte[] result = new byte[len];

        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(hexChars[pos]) << 4 | toByte(hexChars[pos + 1]));
        }
        return result;
    }

    /**
     * c无符号的值，转换成java-int值
     */
    public static int cbyte2Int(byte byteNum) {
        return byteNum & 0xff;
    }

    public static String byteToHex(byte b){
        return String.format("%02x",b).toUpperCase();
    }

    /**
     * 十六进制打印数组
     */
    public static String getHexString(byte[] buffer) {
        StringBuilder sb = new StringBuilder();
        if(buffer == null || buffer.length == 0)
            return sb.toString();
        for (byte b : buffer) {
            String s = byteToHex(b);
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * 数组长度值为8，每个值代表bit，即8个bit。bit7 -> bit0
     * 	bit数组，bit3 -> bit0
     */
    public static byte[] byteToBitOfArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte)(b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }


    /**
     * 4个byte转int，不够补0
     * @param high_h
     * @param high_l
     * @param low_h
     * @param low_l
     * @return
     */
    public static   int getIntFromBytes(byte high_h, byte high_l, byte low_h, byte low_l) {
        return (high_h & 0xff) << 24 | (high_l & 0xff) << 16 | (low_h & 0xff) << 8 | low_l & 0xff;
    }

    public static   int getIntFromBytes( byte low_h, byte low_l) {
        return (low_h & 0xff) << 8 | low_l & 0xff;
    }


    /**
     * Byte转Bit
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 0) & 0x1) +
                (byte) ((b >> 1) & 0x1) +
                (byte) ((b >> 2) & 0x1) +
                (byte) ((b >> 3) & 0x1) +
                (byte) ((b >> 4) & 0x1) +
                (byte) ((b >> 5) & 0x1) +
                (byte) ((b >> 6) & 0x1) +
                (byte) ((b >> 7) & 0x1);
    }


    /**
     * Bit转Byte
     */
    public static byte bitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }



    public static String[] faceNameArray = new String[]{file1,file2,file3,file4};


    public static byte[] intToByteArray(int value){
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value & 0xff);
        bytes[1] = (byte) (value >> 8);
        bytes[2] = (byte) (value >> 16);
        bytes[3] = (byte) (value >> 24);
        return bytes;

    }

    public static byte[] intToSecondByteArray(int value){
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (value& 0xff);
        bytes[1] = (byte) (value >> 8);
        return bytes;

    }


    //表盘第二个文件转数组
    public static byte[] listStrToForthByt(List<String> list){
        byte[] resultByte = new byte[list.size()*4];
        for(int i = 0;i<list.size();i++){
            String str = list.get(i);
            int int1 = Integer.parseInt(str.trim());

            byte[] tmpByte = intToByteArray(int1);
            System.arraycopy(tmpByte,0,resultByte,i*4,4);
        }
        return resultByte;
    }


    //表盘第三个文件转byte数组
    public static byte[] onStrToByte(List<String> list){
        byte[] btArrays = new byte[list.size()];
        for(int i = 0;i<list.size();i++){
            String str = list.get(i);
            int it1 = Integer.parseInt(str.trim());
            btArrays[i] = (byte) it1;
        }
        return btArrays;
    }


    //表盘第一个文件转数组
    public static byte[] listStrToByte(List<String> list){
        byte[] btArrays = new byte[list.size()];
        for(int i = 0;i<list.size();i++){
            String tmpStr = list.get(i);
            byte bt;
            if(tmpStr.contains("x")){
                String hexStr = StringUtils.substringAfter(tmpStr,"x");
                 bt = (byte) Integer.parseInt(hexStr.trim(),16);
            }else{
                bt = 0;
            }
            btArrays[i] = bt;
        }
        return btArrays;
    }


    //表盘第四个文件转数组 两个byte
    public static byte[] fourthFaceToArrays(List<String> list){
        byte[] forthByte = new byte[list.size()*2];

        for(int i = 0;i<list.size();i++){
            String currV = list.get(i);
            int tmpCurrV = Integer.parseInt(currV.trim());
            byte[] tmpByte = intToSecondByteArray(tmpCurrV);
            System.arraycopy(tmpByte,0,forthByte,i*2,2);
        }
        return forthByte;

    }


    //处理表盘数据，将总的byte包转换为每64个byte集合
    public static ArrayList<byte[]> dialByteList(byte[] arrays){
        ArrayList<byte[]> listBytes = new ArrayList<>();

        int countIndex = arrays.length/ 64;
        int countRemainder = arrays.length % 64;

        for(int i = 0;i<countIndex;i++){
            byte[] faceBytes = new byte[64];
            System.arraycopy(arrays,i*64,faceBytes,0,64);
            listBytes.add(faceBytes);
        }
      //  Log.e(TAG,"--11--表盘文件有多少包="+listBytes.size());
        if(countRemainder>0){
            byte[] faceBytes = new byte[64];
            System.arraycopy(arrays,countIndex*64,faceBytes,0,countRemainder);
            listBytes.add(faceBytes);
        }
        return listBytes;
    }
    static ArrayList<byte[]> list = new ArrayList<>();


    final static StringBuilder msgSb = new StringBuilder();

    public static ArrayList<byte[]> sendMessageData(int messageType, String title, String content) {
        list.clear();
        msgSb.delete(0,msgSb.length());

      /*  if (true) {
            list.add(new byte[]{0x01, 0x10, 0x01, 0x00, (byte) 0x0B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
            list.add(new byte[]{0x02, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
            list.add(new byte[]{0x04, 0x10, 0x54, 0x65, 0x73, 0x74, 0x20, 0x6E, 0x6F, 0x74, 0x69, 0x66, 0x79, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
            list.add(new byte[]{0x03, 0x10, (byte) 0xE5, 0x07, 0x04, 0x14, 0x54, 0x16, 0x02, 0x19, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});

            return list;
        }*/

        byte[] titleByte = null;
        byte[] srccontentByte = null;
        byte[] descontentByte = null;
        byte[] contentlen = null;
        byte[] timeByte = null;

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String strMonth = formatTwoStr(calendar.get(Calendar.MONTH) + 1);
        String strDay = formatTwoStr(calendar.get(Calendar.DAY_OF_MONTH));
        String strHour = formatTwoStr(calendar.get(Calendar.HOUR_OF_DAY));
        String strMin = formatTwoStr(calendar.get(Calendar.MINUTE));
        String strSecond = formatTwoStr(calendar.get(Calendar.SECOND));
        //String time = "20200110T1036";
        String time = year + strMonth + strDay + "T" + strHour + strMin + strSecond;
        int conntentLen = 0;
        if (title != null) {
            try {
                titleByte = title.getBytes(StandardCharsets.UTF_8);
                srccontentByte = content.getBytes(StandardCharsets.UTF_8);

                Log.e("内容直接",content+"内="+formatBtArrayToString(srccontentByte));


                for (byte byteChar : srccontentByte) {
                    msgSb.append(String.format("%02X ", byteChar));
                }
                byte[] cBarr = hexStringToByte(msgSb.toString());
                conntentLen = srccontentByte.length;
                Log.e("消息内容" , title + ",srccontentByte:" + msgSb.toString() + "conntentLen:" + conntentLen+" "+formatBtArrayToString(cBarr));
                if (conntentLen > 91) {
                    conntentLen = 91;
                }
                descontentByte = new byte[conntentLen + 1];
                for (int i = 0; i < conntentLen; i++) {
                    descontentByte[i] = srccontentByte[i];
                }
                descontentByte[conntentLen] = 0;

                String contentByteLen = String.valueOf(conntentLen);
                contentlen = contentByteLen.getBytes(StandardCharsets.UTF_8);
                timeByte = time.getBytes(StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        list.add(sendNotiCmd(contentlen, 1, messageType));
        list.add(sendNotiCmd(titleByte, 2));
        int num1 = (conntentLen + 1) / 18;
        int num2 = (conntentLen + 1) % 18;
        int sumNum = 0;
        if (num2 != 0) {
            sumNum = num1 + 1;
        } else {
            sumNum = num1;
        }
        /**
         * src：源数组

         srcPos：源数组要复制的起始位置

         dest：目标数组

         destPos：目标数组复制的起始位置

         length：复制的长度
         */
        list.add(sendNotiCmd(timeByte, 3));
        try {
            for (int i = 0; i < sumNum; i++) {
                byte[] notiContent = new byte[18];
                if (i * 18 + 18 < descontentByte.length) {
                    System.arraycopy(descontentByte, i * 18, notiContent, 0, 18);
                } else {
                    System.arraycopy(descontentByte, i * 18, notiContent, 0, (descontentByte.length - i * 18));
                }
                list.add(sendNotiCmd(notiContent, i + 4));
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

//            list.add(sendNotiCmd(timeByte, 3));
            return list;
        }

        //时间包格式：20200108T105632


    }

    public static byte[] sendNotiCmd(byte[] notiContent, int packageIndex, int notitype) {
        byte[] btCmd = new byte[20];
        for (int i = 0; i < 20; i++) {
            btCmd[i] = 0x00;
        }
        btCmd[0] = (byte) packageIndex;
        btCmd[1] = (byte) 0x10;
        btCmd[2] = (byte) notitype;
        btCmd[3] = (byte) 0x00;
        if (notiContent != null && notiContent.length <= 16) {
            System.arraycopy(notiContent, 0, btCmd, 4, notiContent.length);
        }
        return btCmd;
    }

    public static byte[] sendNotiCmd(byte[] notiContent, int packageIndex) {
        byte[] btCmd = new byte[20];
        try {
            btCmd[0] = (byte) packageIndex;
            btCmd[1] = (byte) 0x10;
            if (notiContent != null && notiContent.length <= 18) {
                System.arraycopy(notiContent, 0, btCmd, 2, notiContent.length);
            } else {
                System.arraycopy(notiContent, 0, btCmd, 2, 18);
            }
            int length = (notiContent == null ? 0 : notiContent.length);
            if (length < 18 && length >= 0) {
                for (int i = length; i < 18; i++) {
                    btCmd[2 + i] = (byte) 0x00;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return btCmd;
        }

    }

    public static String formatTwoStr(int number) {
        String strNumber = String.format("%02d", number);
        return strNumber;
    }






}
