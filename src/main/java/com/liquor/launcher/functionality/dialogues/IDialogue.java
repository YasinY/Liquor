package com.liquor.launcher.functionality.dialogues;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.function.Predicate;

public interface IDialogue {

    String title();
    Alert.AlertType type();
    String contentText();
}
