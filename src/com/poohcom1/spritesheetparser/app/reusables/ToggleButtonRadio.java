package com.poohcom1.spritesheetparser.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToggleButtonRadio extends JPanel {
    private final List<JToggleButton> toolButtons;

    public ToggleButtonRadio() {
        setLayout(new FlowLayout());

        toolButtons = new ArrayList<>();
    }


    public void addButton(String name, ButtonToggledListener listener) {
        JToggleButton newButton = new JToggleButton(name);
        if (toolButtons.size() == 0) newButton.setSelected(true);
        newButton.setFocusable(false);
        newButton.addActionListener(e -> {
            toolButtons.forEach(otherButton -> otherButton.setSelected(false));
            newButton.setSelected(true);
            listener.buttonToggled();
        });
        add(newButton);
        toolButtons.add(newButton);
    }



    public void setButtonsEnabled(boolean enabled) {
        if (enabled) {
            toolButtons.forEach(button -> {
                button.setSelected(toolButtons.indexOf(button) == 0);
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
        void buttonToggled();
    }
}
