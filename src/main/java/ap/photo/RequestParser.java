package ap.photo;

import com.google.gson.Gson;
import java.util.*;

public class RequestParser {
    private static final Gson gson = new Gson();

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parse(String json) {
        return gson.fromJson(json, Map.class);
    }

    public static String getType(Map<String, Object> request) {
        return (String) request.get("type");
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getData(Map<String, Object> request) {
        return (Map<String, Object>) request.get("data");
    }
}
