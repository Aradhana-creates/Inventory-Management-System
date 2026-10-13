package com.pms.ui;

import com.pms.model.User;

/**
 * Session manager holding the currently authenticated user in com.pms.ui.
 */
public final class UserSession {

    private static UserSession instance;
    private User currentUser;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasRole(String role) {
        return currentUser != null && role != null && role.equalsIgnoreCase(currentUser.getRole());
    }

    public boolean hasAnyRole(String... roles) {
        if (currentUser == null || roles == null) {
            return false;
        }
        for (String r : roles) {
            if (r.equalsIgnoreCase(currentUser.getRole())) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        this.currentUser = null;
    }
}
