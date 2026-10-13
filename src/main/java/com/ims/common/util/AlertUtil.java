package com.ims.common.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Convenience wrappers for JavaFX {@link Alert} dialogs.
 *
 * <p>All methods must be called from the JavaFX Application Thread.
 * Stack traces are never shown in the UI — only the supplied message string.
 */
public final class AlertUtil {

    private AlertUtil() {}

    /**
     * Shows an informational dialog and waits for the user to dismiss it.
     *
     * @param title   dialog title bar text
     * @param message body message
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an error dialog and waits for the user to dismiss it.
     *
     * @param title   dialog title bar text
     * @param message body message (UI-safe, no stack trace)
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a warning dialog and waits for the user to dismiss it.
     *
     * @param title   dialog title bar text
     * @param message body message
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a confirmation dialog and returns the button the user pressed.
     *
     * @param title   dialog title bar text
     * @param message body message
     * @return {@code Optional<ButtonType>} — check {@code .orElse(ButtonType.CANCEL)}
     */
    public static Optional<ButtonType> showConfirm(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
