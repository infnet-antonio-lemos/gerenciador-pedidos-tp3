package auth;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleTokenManager {
    private static final Map<String, Integer> tokenToUserId = new ConcurrentHashMap<>();

    public static String generateToken(int userId) {
        String token = UUID.randomUUID().toString();
        tokenToUserId.put(token, userId);
        return token;
    }

    public static Integer getUserIdFromToken(String token) {
        return tokenToUserId.get(token);
    }

    public static boolean isValidToken(String token) {
        return token != null && tokenToUserId.containsKey(token);
    }

    public static void removeToken(String token) {
        tokenToUserId.remove(token);
    }
}
