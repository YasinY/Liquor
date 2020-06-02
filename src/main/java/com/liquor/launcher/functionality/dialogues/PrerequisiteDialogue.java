package com.liquor.launcher.functionality.dialogues;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

@Builder
public class PrerequisiteDialogue implements IDialogue {

    @Override
    public String title() {
        return null;
    }

    @Override
    public Alert.AlertType type() {
        return null;
    }

    @Override
    public String contentText() {
        return null;
    }


}
