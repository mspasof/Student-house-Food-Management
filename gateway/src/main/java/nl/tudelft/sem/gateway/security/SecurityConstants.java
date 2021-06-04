package nl.tudelft.sem.gateway.security;

public class SecurityConstants {
    public static final String SECRET = "TU_Delft";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/user/register";
    public static final String LOGIN_URL = "/user/login";
    public static final String HASH_URL = "/hash";
    public static final String SECRET_HEADER = "e91e6348157868de9dd8b25c81aebfb9";
    public static final String SECRET_VALUE = "0ad210d6fa2d8f4f2775dd31f7a0ca0c";
}