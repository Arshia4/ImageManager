package ap.photo;

import com.google.gson.Gson;
import java.util.*;

public class ResponseBuilder {
    private static final Gson gson = new Gson();

    public static String success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return gson.toJson(response);
    }

    public static String error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", message);
        return gson.toJson(response);
    }
}
