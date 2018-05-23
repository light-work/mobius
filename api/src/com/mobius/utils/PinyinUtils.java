package com.mobius.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.guiceside.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: gbcp
 * Date: 12-6-16
 * Time: 下午8:01
 * To change this template use File | Settings | File Templates.
 */
public class PinyinUtils {
    /**
     * 将字符串转换成拼音数组
     *
     * @param src
     * @return
     */
    public static String[] stringToPinyin(String src) {
        return stringToPinyin(src, false, null);
    }

    /**
     * 将字符串转换成拼音数组
     *
     * @param src
     * @return
     */
    public static String[] stringToPinyin(String src, String separator) {
        return stringToPinyin(src, true, separator);
    }

    /**
     * 将字符串转换成拼音数组
     *
     * @param src
     * @param isPolyphone 是否查出多音字的所有拼音
     * @param separator   多音字拼音之间的分隔符
     * @return
     */
    public static String[] stringToPinyin(String src, boolean isPolyphone,
                                          String separator) {
        // 判断字符串是否为空
        if ("".equals(src) || null == src) {
            return null;
        }
        char[] srcChar = src.toCharArray();
        int srcCount = srcChar.length;
        String[] srcStr = new String[srcCount];

        for (int i = 0; i < srcCount; i++) {
            srcStr[i] = charToPinyin(srcChar[i], isPolyphone, separator);
        }
        return srcStr;
    }

    /**
     * 将单个字符转换成拼音
     *
     * @param src
     * @return
     */
    public static String charToPinyin(char src, boolean isPolyphone,
                                      String separator) {
        // 创建汉语拼音处理类
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        // 输出设置，大小写，音标方式
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuffer tempPinying = new StringBuffer();


        // 如果是中文
        if (src > 128) {
            try {
                // 转换得出结果
                String[] strs = PinyinHelper.toHanyuPinyinStringArray(src,
                        defaultFormat);


                // 是否查出多音字，默认是查出多音字的第一个字符
                if (isPolyphone && null != separator) {
                    for (int i = 0; i < strs.length; i++) {
                        tempPinying.append(strs[i]);
                        if (strs.length != (i + 1)) {
                            // 多音字之间用特殊符号间隔起来
                            tempPinying.append(separator);
                        }
                    }
                } else {
                    if (null != strs) tempPinying.append(strs[0]);
                }

            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        } else {
            tempPinying.append(src);
        }

        return tempPinying.toString();

    }


    public static String hanziToPinyin(String hanzi) {
        return hanziToPinyin(hanzi, " ");
    }

    /**
     * 将汉字转换成拼音
     *
     * @param hanzi
     * @param separator
     * @return
     */
    public static String hanziToPinyin(String hanzi, String separator) {
        // 创建汉语拼音处理类
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        // 输出设置，大小写，音标方式
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        String pinyingStr = "";
        try {
            pinyingStr = PinyinHelper.toHanyuPinyinString(hanzi, defaultFormat, separator);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pinyingStr;
    }

    /**
     * 将字符串数组转换成字符串
     *
     * @param str
     * @param separator 各个字符串之间的分隔符
     * @return
     */
    public static String stringArrayToString(String[] str, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length; i++) {
            sb.append(str[i]);
            if (str.length != (i + 1)) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * 简单的将各个字符数组之间连接起来
     *
     * @param str
     * @return
     */
    public static String stringArrayToString(String[] str) {
        return stringArrayToString(str, "");
    }

    /**
     * 将字符数组转换成字符串
     *
     * @param ch
     * @param separator 各个字符串之间的分隔符
     * @return
     */
    public static String charArrayToString(char[] ch, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ch.length; i++) {
            sb.append(ch[i]);
            if (ch.length != (i + 1)) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * 将字符数组转换成字符串
     *
     * @param ch
     * @return
     */
    public static String charArrayToString(char[] ch) {
        return charArrayToString(ch, " ");
    }

    /**
     * 取汉字的首字母
     *
     * @param src
     * @param isCapital 是否是大写
     * @return
     */
    public static char[] getHeadByChar(char src, boolean isCapital) {
        //如果不是汉字直接返回
        if (src <= 128) {
            return new char[]{src};
        }
        //获取所有的拼音
        String[] pinyingStr = PinyinHelper.toHanyuPinyinStringArray(src);
        //过滤中文符号
        if (pinyingStr == null) {
            return new char[]{src};
        }
        //创建返回对象
        int polyphoneSize = pinyingStr.length;
        char[] headChars = new char[polyphoneSize];
        int i = 0;
        //截取首字符
        for (String s : pinyingStr) {
            char headChar = s.charAt(0);
            //首字母是否大写，默认是小写
            if (isCapital) {
                headChars[i] = Character.toUpperCase(headChar);
            } else {
                headChars[i] = headChar;
            }
            i++;
        }

        return headChars;
    }

    /**
     * 取汉字的首字母(默认是大写)
     *
     * @param src
     * @return
     */
    public static char[] getHeadByChar(char src) {
        return getHeadByChar(src, true);
    }

    /**
     * 查找字符串首字母
     *
     * @param src
     * @return
     */
    public static String[] getHeadByString(String src) {
        return getHeadByString(src, true);
    }

    /**
     * 查找字符串首字母
     *
     * @param src
     * @param isCapital 是否大写
     * @return
     */
    public static String[] getHeadByString(String src, boolean isCapital) {
        return getHeadByString(src, isCapital, null);
    }

    /**
     * 查找字符串首字母
     *
     * @param src
     * @param isCapital 是否大写
     * @param separator 分隔符
     * @return
     */
    public static String[] getHeadByString(String src, boolean isCapital, String separator) {
        char[] chars = src.toCharArray();
        String[] headString = new String[chars.length];
        int i = 0;
        for (char ch : chars) {

            char[] chs = getHeadByChar(ch, isCapital);
            StringBuffer sb = new StringBuffer();
            if (null != separator) {
                int j = 1;

                for (char ch1 : chs) {
                    sb.append(ch1);
                    if (j != chs.length) {
                        sb.append(separator);
                    }
                    j++;
                }
            } else {
                sb.append(chs[0]);
            }
            headString[i] = sb.toString();
            i++;
        }
        return headString;
    }

    public static String getUID(String str, int i) {
        String no = "";
        if (StringUtils.isNotBlank(str)) {
            String[] pinyins = PinyinUtils.stringToPinyin(str);
            if (pinyins != null && pinyins.length > 0) {
                for (String pinyin : pinyins) {
                    int strLength = pinyin.length();
                    int tempI = i;
                    if (tempI > strLength) {
                        tempI = strLength;
                    }
                    no += pinyin.substring(0, tempI);
                }
            }
        }
        return no.toUpperCase();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getUID("西湖区", 1));
        System.out.println(getJPinYin("西湖区"));
        System.out.println(getQPinYin("西湖区"));
        System.out.println(getUID("西湖区", 10));
    }


    private static Random random = null;


    private static String xingStr[] = new String[]{
            "李", "张", "王", "刘", "陈", "杨", "赵", "黄",
            "周", "吴", "徐", "孙", "胡", "朱", "高", "林",
            "何", "郭", "马", "罗", "梁", "宋", "郑", "谢",
            "韩", "唐", "冯", "于", "董", "萧"
    };

    private static Random getRandomInstance() {
        if (random == null) {
            random = new Random(new Date().getTime());
        }
        return random;
    }


    public static String getChinese() {
        String str = null;
        int highPos, lowPos;
        Random random = getRandomInstance();
        highPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = 161 + Math.abs(random.nextInt(93));
        byte[] b = new byte[2];
        b[0] = (new Integer(highPos)).byteValue();
        b[1] = (new Integer(lowPos)).byteValue();
        try {
            str = new String(b, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getFixedLengthChinese(int length) {
        String str = "";
        for (int i = length; i > 0; i--) {
            str = str + getChinese();
        }
        return str;
    }

    public static String getRandomLengthChiness(int start, int end) {
        String str = "";
        int length = new Random().nextInt(end + 1);
        if (length < start) {
            str = getRandomLengthChiness(start, end);
        } else {
            for (int i = 0; i < length; i++) {
                str = str + getChinese();
            }
        }
        return str;
    }

    public static String getChineseName(int length) {
        String str = "";
        if (length > 1) {
            length = length - 1;
            Random random = getRandomInstance();
            int index = random.nextInt(30);
            str = xingStr[index];
            str += getFixedLengthChinese(length);
        }
        return str;
    }


    /**
     * 获取简拼
     * 模块名称： 通用方法
     */
    public static String getJPinYin(String userName) throws Exception {
        if (null == userName || userName.trim().equals("")) return "";
        String results = "";
        userName = userName.trim();
        String[] strs = PinyinUtils.getHeadByString(userName);
        if (strs != null && strs.length > 0) {
            for (String item : strs) {
                results += item;
            }
        }
        return results;
    }

    /**
     * 获取简拼
     * 模块名称： 通用方法
     */
    public static String getQPinYin(String userName) throws Exception {
        if (null == userName || userName.trim().equals("")) return "";
        String results = "";
        userName = userName.trim();
        String[] strs = PinyinUtils.stringToPinyin(userName);
        if (strs != null && strs.length > 0) {
            for (String item : strs) {
                results += item;
            }
        }
        return results;
    }
}