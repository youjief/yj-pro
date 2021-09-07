package cn.thread;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 对整个请求,包括复杂请求体在内 进行结构化 的 签名和验签
 *
 * - 请求内容中禁止包含签名字段：DRP-Signature
 * - 对于浮点数的处理,默认会忽略小数点6位后面的数据,再进行签名
 *
 * @author 13813363200
 * @version V1.0
 * @date: 2020/7/14 16:10
 */
public class SignUtils {

    public static void main(String[] args) {
        Map<String, String> extraData=new HashMap<>();
        extraData.put("DRP-Random","487");
        extraData.put("DRP-AccessKey","5N538D2N");
        extraData.put("DRP-Timestamp","1615887886231");
//        extraData.put("Content-Type","application/json");

        try {
            String sign = sign("M9EyHt46cQKjiJE2%2B1X244GyY3DUr7WynygcgGmSTKw%3D"
                    , "{\"mobile\":\"49U0G8angRITwDE4D0V5Xw==\",\"useTime\":\"2021-03-16 17:21:44\",\"thirdOrderId\":4564321231321,\"couponCode\":\"slkdjflkajj\",\"useStatus\":1}"
                    , extraData);
            System.out.println(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final static Gson GSON = new Gson();
    private final static int SCALE = 6;
    private final static String HEADER_SIGNATURE = "DRP-Signature";


    /**
     * 采用HmacSHA256 算法 签名
     *
     * @param dto       支持嵌套复杂DTO对象的签名,支持json 字符串
     * @param extraData 额外参与签名的数据,例如 path,请求头, 时间戳, 秘钥,秘钥id等
     * @return
     */
    public static String sign(String accessSecret, Object dto, Map<String, String> extraData) throws Exception {
        extraData.remove(HEADER_SIGNATURE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(flatMap(extraData));
        String json;
        if (null == dto) {
            json = "{}";
        } else if (!dto.getClass().equals(String.class)) {
            json = GSON.toJson(dto);
        } else {
            json = (String) dto;
        }
        Type type = new TypeToken<Map<String, ?>>() {
        }.getType();
        Map<String, ?> map = GSON.fromJson(json, type);
        String mapStr = flatMap(map);
        stringBuilder.append(mapStr);

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(accessSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode(new BASE64Encoder().encode(signData), "UTF-8").replace("+", "%20").replace("*", "%2A").replace("~", "%7E");
    }

    /**
     * 关键算法, 需要与不同语言实现保持一致
     * 如javascript,objectc,swift等
     *
     * @param map
     * @return 对数据进行结构化签名, 保证不同结构对应各自唯一签名
     */
    private static String flatMap(Map<String, ?> map) {
        if (null == map || map.isEmpty()) {
            return "";
        }
        Set<String> keys = map.keySet();
        List<String> orderedKey = new ArrayList<>(keys.size());
        orderedKey.addAll(keys);
        orderedKey.sort(String::compareTo);
        StringBuilder stringBuilder = new StringBuilder("(");
        for (String key : orderedKey) {
            Object value = map.get(key);
            if (null != value) {
                stringBuilder.append(key);
                if ((LinkedTreeMap.class.equals(value.getClass()))) {
                    LinkedTreeMap<String, ?> valueMap = (LinkedTreeMap<String, ?>) value;
                    stringBuilder.append(flatMap(valueMap));
                } else if (ArrayList.class.equals(value.getClass())) {
                    ArrayList<?> valueCollection = (ArrayList<?>) value;
                    stringBuilder.append(flatCollections(valueCollection));
                } else if (Double.class.equals(value.getClass())) {
                    Double doubleValue = (Double) value;
                    BigDecimal bigDecimal = BigDecimal.valueOf(doubleValue);
                    BigInteger bigInteger = bigDecimal.movePointRight(SCALE).toBigInteger();
                    stringBuilder.append(bigInteger.toString());
                } else {
                    stringBuilder.append(value);
                }
            }

        }

        return stringBuilder.append(")").toString();
    }

    private static String flatCollections(ArrayList<?> valueCollection) {
        StringBuilder stringBuilder = new StringBuilder("[");
        final List<String> valueList = new ArrayList<>(valueCollection.size());
        valueCollection.forEach(value -> {
            if (LinkedTreeMap.class.equals(value.getClass())) {
                String eleStr = flatMap((LinkedTreeMap) value);
                valueList.add(eleStr);
            } else if (ArrayList.class.equals(value.getClass())) {
                String eleStr = flatCollections((ArrayList) value);
                valueList.add(eleStr);
            } else if (Double.class.equals(value.getClass())) {
                Double doubleValue = (Double) value;
                BigDecimal bigDecimal = BigDecimal.valueOf(doubleValue);
                BigInteger bigInteger = bigDecimal.movePointRight(SCALE).toBigInteger();
                valueList.add(bigInteger.toString());
            } else {
                valueList.add(value.toString());
            }
        });
        valueList.sort(String::compareTo);
        valueList.forEach(stringBuilder::append);
        return stringBuilder.append("]").toString();
    }

    /**
     * 签名校验 支持嵌套复杂DTO对象的签名,支持json 字符串
     *
     * @param sign      收到的签名
     * @param dto       支持嵌套复杂DTO对象的签名,支持json 字符串
     * @param extraData 额外参与签名的数据,例如 path,请求头, 时间戳, 秘钥,秘钥id等
     * @return 签名是否验证正确
     */
    public static boolean checkSign(String sign, String accessSecret, Object dto, Map<String, String> extraData) throws Exception {
        String validSign = sign(accessSecret, dto, extraData);
        return validSign.equals(sign);
    }
}
