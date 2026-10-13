package com.ims.auth.model;

import com.ims.user.model.User;
import com.ims.common.model.Role;

/**
 * Singleton holding the currently authenticated user for the lifetime of the
 * JavaFX session.  Cleared on logout.
 */
public final class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private User currentUser;

    private SessionManager() {}

    /** Returns the single application-wide session manager. */
    public static SessionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the active user after a successful login.
     *
     * @param user the authenticated user (must not be {@code null})
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Returns the currently logged-in user, or {@code null} if not logged in.
     *
     * @return current {@link User} or {@code null}
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Returns {@code true} when a user is logged in.
     *
     * @return {@code true} if a session is active
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Returns {@code true} when the current user has the given role.
     *
     * @param role the role to check
     * @return {@code true} if the current user's role matches
     */
    public boolean hasRole(Role role) {
        return currentUser != null && currentUser.getRole() == role;
    }

    /**
     * Returns {@code true} when the current user has any of the given roles.
     *
     * @param roles one or more roles to check
     * @return {@code true} if the current user matches any supplied role
     */
    public boolean hasAnyRole(Role... roles) {
        if (currentUser == null) return false;
        for (Role r : roles) {
            if (currentUser.getRole() == r) return true;
        }
        return false;
    }

    /** Clears the session (called on logout). */
    public void logout() {
        currentUser = null;
    }
}
