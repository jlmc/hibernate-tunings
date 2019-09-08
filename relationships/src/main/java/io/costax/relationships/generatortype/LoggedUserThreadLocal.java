package io.costax.relationships.generatortype;

/**
 * In a web application Servlet Filter, the {@code LoggedUserThreadLoacal.logIn} method can be called using the
 * currently authenticated user, and the {@code LoggedUserThreadLoacal.logOut } method is called right after returning
 * from the inner FilterChain.doFilter invocation.
 */
public class LoggedUserThreadLocal {

    private static final ThreadLocal<String> userHolder =
            new ThreadLocal<>();

    public static void logIn(String user) {
        userHolder.set(user);
    }

    public static void logOut() {
        userHolder.remove();
    }

    public static String get() {
        return userHolder.get();
    }

}
