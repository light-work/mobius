package com.mobius.utils;

import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Lara Croft on 2016/12/22.
 */
public class ImgVerifyCodeUtils {

    /**
     * 验证码图片的宽度。
     */
    private static int WIDTH = 110;

    /**
     * 验证码图片的高度。
     */
    private static int HEIGHT = 40;

    /**
     * 验证码字符个数
     */
    private static int codeCount = 4;


    public static void main(String[] args) throws Exception {
        buildImg();
    }

    public static JSONObject buildImg() throws IOException {
        JSONObject result = new JSONObject();
        result.put("randCode", -1);
        result.put("img", -1);
        //在内存中创建图象
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        //获取图形上下文
        Graphics g = image.getGraphics();
        //生成随机类
        Random random = new Random();
        //设定背景色
        g.setColor(getRandColor(220, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        //设定字体
        g.setFont(new Font("Times New Roman", Font.PLAIN, 32));
        //画边框
        //g.drawRect(0,0,WIDTH-1,HEIGHT-1);
        g.draw3DRect(30, 30, WIDTH - 20, HEIGHT - 20, true);
        //随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        // 取随机产生的认证码(6位数字)
        String randCode = "";
        String s = "012345678901234567890123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ012345678901234567890123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < codeCount; i++) {
            char rand = s.charAt(random.nextInt(s.length()));
            randCode += rand;
            // 将认证码显示到图象中
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            //调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            g.drawString(String.valueOf(rand), (WIDTH-10) / 4 * i + 6, HEIGHT - 5);
        }
        result.put("randCode", randCode.toLowerCase());
        g.drawOval(0, 12, 60, 11);
        // 图象生效
        g.dispose();
        ByteArrayOutputStream tempOs = null;
        try {
            // 创建字符流
            tempOs = new ByteArrayOutputStream();
            // 写入字符流
            ImageIO.write(image, "jpg", tempOs);
            // 转码成字符串
            BASE64Encoder encoder = new BASE64Encoder();
            String img64 = encoder.encode(tempOs.toByteArray());
            result.put("img", "data:image/png;base64," + img64);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (tempOs != null) {
                tempOs.close();
            }
        }

        return result;
    }


    /**
     * 生成随机颜色
     */
    private static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
