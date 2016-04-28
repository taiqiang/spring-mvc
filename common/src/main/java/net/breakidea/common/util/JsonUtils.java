package net.breakidea.common.util;

import java.lang.reflect.Type;
import java.util.Map;

import net.breakidea.common.ConfigConstants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.MalformedJsonException;

/**
 * @author apple
 *
 */
public abstract class JsonUtils implements ConfigConstants {

    public static final String DEFAULT_JSON = "{}";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static Gson gson;

    /**
     * @param json
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parse( String json ) {
        return parse(json, Map.class);
    }

    /**
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T parse( String json, Class<T> clazz ) {
        if (json == null) {
            return null;
        }
        return getInstance().fromJson(json, clazz);
    }

    /**
     * @return
     */
    public static Gson getInstance() {
        if (gson == null) {
            GsonBuilder gb = new GsonBuilder();

            gb.serializeNulls();
            gb.disableHtmlEscaping();
            gb.setDateFormat(DATE_FORMAT);

            gson = gb.create();
        }
        return gson;
    }

    /**
     * @param src
     * @param typeOfSrc
     * @return
     * @throws UnsupportedOperationException
     */
    public static String stringify( Object src, Type type ) throws UnsupportedOperationException {
        try {
            return getInstance().toJson(src, type);
        } catch (Throwable e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * @param src
     * @return
     * @throws MalformedJsonException
     */
    public static String stringify( Object src ) throws UnsupportedOperationException {
        return stringify(src, src.getClass());
    }

    /**
     * @param src
     * @param type
     * @return
     */
    public static Object parse( Object src, Type type ) {
        try {
            return getInstance().toJson(src, type);
        } catch (Throwable e) {
            throw new UnsupportedOperationException(e);
        }
    }
}