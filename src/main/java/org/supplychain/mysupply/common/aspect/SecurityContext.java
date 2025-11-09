package org.supplychain.mysupply.common.aspect;

import org.supplychain.mysupply.user.model.User;

public class SecurityContext {

    private final static ThreadLocal<User> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(User user)
    {
        currentUser.set(user);
    }

    public static User getCurrentUser()
    {
        return currentUser.get();
    }
    public static void clear() {
        currentUser.remove();
    }
}
