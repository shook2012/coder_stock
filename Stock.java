package net.sk.base;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
/**
 * @author sam.fan
 */
public class Stock {

    //股票代码
    public static String YOUR_STOCK_CODE_LIST = "s_sh000001,sz002230,sh600307,sh600844,sz002457,sh600503";
    
    public static final int REFRESH_SECONDS = 5;
    
    public static final String SINA_STOCK_LIST_URL = "http://hq.sinajs.cn/list=";
    
    public static final String RESPONSE_ENCODING = "gbk";
    
    public static final int REFRESH_MILLISECONDS = REFRESH_SECONDS * 1000;

    public static void main(String[] args) {
        String getUrl = SINA_STOCK_LIST_URL + YOUR_STOCK_CODE_LIST;
        try {
            URL u = new URL(getUrl);
            byte[] b = new byte[256];
            InputStream in = null;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            while (true) {
                in = u.openStream();
                int i;
                while ((i = in.read(b)) != -1) {
                    bo.write(b, 0, i);
                }
                String resStr = bo.toString(RESPONSE_ENCODING);
                parseStock(resStr);
                bo.reset();
                Thread.sleep(REFRESH_MILLISECONDS);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private static void parseStock(String resStr) {
        String[] stockString = resStr.split(";");
        for (String stock : stockString) {
            String[] datas = stock.split(",");
            String headStr = datas[0];
            // 上证指数
            if (headStr.indexOf("sh000001") > -1) {
                String stockName = headStr.substring(headStr.indexOf("=") + 2);
                Log.info("-------------------" + stockName + "-------------------");
                Log.info("当前点数:" + datas[1] + "涨跌幅:" + datas[3] + "%  涨跌(点数):" + datas[2]);
                Log.info("成交量:" + datas[4] + "手  成交额:" + datas[5].substring(0, datas[5].length() - 1));
            } else {
                String stockName = headStr.substring(headStr.indexOf("=") + 2);
                if (null == stockName || "".equals(stockName.trim())){
                    continue;
                }
                Log.info("-------------------" + stockName + "-------------------");
                Double currentPrice = Double.valueOf(datas[3]);
                Double yestodayPrice = Double.valueOf(datas[2]);
                
                if ( currentPrice - yestodayPrice < 0) {
                    Log.info("当前价:" + currentPrice + "  跌幅:" + calculateFallRate(currentPrice, yestodayPrice) + "%  今开:" + datas[1] + "  昨收:" + yestodayPrice);
                } else {
                    Log.info("当前价:" + currentPrice + "  涨幅:" + calculateRiseRate(currentPrice, yestodayPrice) + "%  今开:" + datas[1] + "  昨收:" + yestodayPrice);
                }
                Log.info("最高:" + datas[4] + "  最低:" + datas[5] + "  买价:" + datas[6] + "  卖价:" + datas[7]);
                Log.info("总成交:" + datas[8] + "手  成交金额:" + datas[9]);
            }
        }
        Log.info("\n\n");
    }

    private static BigDecimal calculateRiseRate(Double currentPrice, Double yestodayPrice) {
        return (BigDecimal.valueOf(Double.valueOf(currentPrice) - Double.valueOf(yestodayPrice))
                .divide(BigDecimal.valueOf(Double.valueOf(yestodayPrice)), 4, RoundingMode.HALF_UP))
                        .multiply(BigDecimal.valueOf(100)).setScale(2);
    }

    private static BigDecimal calculateFallRate(Double currentPrice, Double yestodayPrice) {
        return (BigDecimal.valueOf(Double.valueOf(yestodayPrice) - Double.valueOf(currentPrice))
                .divide(BigDecimal.valueOf(Double.valueOf(yestodayPrice)), 4, RoundingMode.HALF_UP))
                        .multiply(BigDecimal.valueOf(100)).setScale(2);
    }

    static class Log {
        public  static void info(String string) {
            System.out.println(string);
        }
        public static void rise(String string) {
            System.err.println(string);
        }
    }

}