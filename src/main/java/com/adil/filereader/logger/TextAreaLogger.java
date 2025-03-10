package com.adil.filereader.logger;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TextAreaLogger {

    private static final TextAreaLogger instance = new TextAreaLogger();
    private TextArea textArea;

    private TextAreaLogger() {
    }

    public static TextAreaLogger getInstance() {
        return instance;
    }

    public void log(String text) {
        Platform.runLater(() -> textArea.appendText(text + "\n"));
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
        this.textArea.setEditable(false);
    }
}
