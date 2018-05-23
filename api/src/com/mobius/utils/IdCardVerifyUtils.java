package com.mobius.utils;

import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lara Croft on 2016/12/22.
 */
public class IdCardVerifyUtils {
    public static String verify(String idCard, String name) throws Exception {
        String host = "http://jisusfzsm.market.alicloudapi.com";
        String path = "/idcardverify/verify";
        String method = "GET";
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE ");
        Map<String, String> querys = new HashMap<>();
        querys.put("idcard", idCard);
        querys.put("realname", name);

        try {
            HttpResponse response = null; //HttpUtils.doGet(host, path, method, headers, querys);
            if (response != null) {
                JSONObject obj = JSONObject.fromObject(response.toString());
                IdCard idCardObj = new IdCard();
                idCardObj.setStatus(getInt(obj, "status"));
                idCardObj.setMsg(getString(obj, "msg"));
                idCardObj.setIdCard(getString(obj, "idcard"));
                idCardObj.setRealName(getString(obj, "realname"));
                idCardObj.setProvince(getString(obj, "province"));
                idCardObj.setCity(getString(obj, "city"));
                idCardObj.setTown(getString(obj, "town"));
                idCardObj.setGender(getString(obj, "sex"));
                idCardObj.setBirth(getDate(obj, "birth"));
                idCardObj.setVerifyStatus(getInt(obj, "verifystatus"));
                idCardObj.setVerifyMsg(getString(obj, "verifymsg"));
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getString(JSONObject obj, String prop) {
        if (obj.containsKey(prop)) {
            return obj.getString(prop);
        }
        return null;
    }

    private static Integer getInt(JSONObject obj, String prop) {
        if (obj.containsKey(prop)) {
            return obj.getInt(prop);
        }
        return null;
    }

    private static Date getDate(JSONObject obj, String prop) {
        if (obj.containsKey(prop)) {
            String date = obj.getString(prop);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM日dd日");
            try {
                Date dateReal = sdf.parse(date);
                return dateReal;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static class IdCard {

        private Integer status;

        private String msg;

        private String idCard;

        private String realName;

        private String province;

        private String city;

        private String town;

        private String gender;

        private Date birth;

        private Integer verifyStatus;

        private String verifyMsg;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getTown() {
            return town;
        }

        public void setTown(String town) {
            this.town = town;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Date getBirth() {
            return birth;
        }

        public void setBirth(Date birth) {
            this.birth = birth;
        }

        public Integer getVerifyStatus() {
            return verifyStatus;
        }

        public void setVerifyStatus(Integer verifyStatus) {
            this.verifyStatus = verifyStatus;
        }

        public String getVerifyMsg() {
            return verifyMsg;
        }

        public void setVerifyMsg(String verifyMsg) {
            this.verifyMsg = verifyMsg;
        }
    }
}
