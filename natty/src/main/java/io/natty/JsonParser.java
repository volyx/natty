package io.natty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

/**
 * Date: 21.11.13
 * Time: 15:31
 */
public final class JsonParser {

    private static final Logger log = LoggerFactory.getLogger(JsonParser.class);

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
