package com.poohcom1.spritesheetparser.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToggleButtonRadio extends JPanel {
    private int activeTool;
    private final List<JToggleButton> toolButtons;
    private List<ButtonToggledListener> listeners;

    public ToggleButtonRadio() {
        setLayout(new FlowLayout());

        toolButtons = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public int getActiveTool() {return activeTool;}

    public void addButton(String name, int index) {
        JToggleButton newButton = new JToggleButton(name);
        if (toolButtons.size() == 0) newButton.setSelected(true);
        buttonInit(newButton);
        while (toolButtons.size() < index + 1) {
            toolButtons.add(null);
        }
        toolButtons.set(index, newButton);
    }

    private void buttonInit(JToggleButton button) {
        button.setFocusable(false);
        button.addActionListener(e -> {
            toolButtons.forEach(otherButton -> otherButton.setSelected(false));
            button.setSelected(true);
            activeTool = toolButtons.indexOf(button);
            listeners.forEach(listener -> listener.buttonToggled(activeTool));
        });
        add(button);
    }

    public void addButtonToggledListener(ButtonToggledListener buttonToggledListener) {
        listeners.add(buttonToggledListener);
    }

    public void removeButtonToggledListener(ButtonToggledListener buttonToggledListener) {
        listeners.remove(buttonToggledListener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public void setButtonsEnabled(boolean enabled) {
        if (enabled) {
            toolButtons.forEach(button -> {
                if (toolButtons.indexOf(button) == 0) button.setSelected(true);
                button.setEnabled(true);
            });
        } else {
            toolButtons.forEach(button -> {
                button.setSelected(false);
                button.setEnabled(false);
            });
        }
    }


    public interface ButtonToggledListener {
        void buttonToggled(int buttonIndex);
    }
}
