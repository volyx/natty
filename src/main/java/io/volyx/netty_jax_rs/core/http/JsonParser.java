package io.volyx.netty_jax_rs.core.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

/**
 * Date: 21.11.13
 * Time: 15:31
 */
public final class JsonParser {

    private static final Logger log = LogManager.getLogger(JsonParser.class);

    private JsonParser() {
    }

    //it is threadsafe
    public static final Gson MAPPER = new GsonBuilder().create();


    private static byte[] writeJsonAsCompressedBytes(Gson objectWriter, Object o) {
        return objectWriter.toJson(o).getBytes(StandardCharsets.UTF_8);
    }

    public static String toJson(Object o) {
        try {
            return MAPPER.toJson(o);
        } catch (Exception e) {
            log.error("Error jsoning object.", e);
        }
        return null;
    }

    public static String valueToJsonAsString(String[] values) {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        for (String value : values) {
            sj.add(makeJsonStringValue(value));
        }
        return sj.toString();
    }

    public static String valueToJsonAsString(String value) {
        return "[\"" + value  + "\"]";
    }

    private static String makeJsonStringValue(String value) {
        return "\"" + value  + "\"";
    }

}
