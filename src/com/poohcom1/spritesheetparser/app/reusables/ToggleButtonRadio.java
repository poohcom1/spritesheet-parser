package com.poohcom1.spritesheetparser.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToggleButtonRadio extends JPanel {
    private List<JToggleButton> tools;
    private int activeTool;
    private ButtonToggledListener listener;

    public ToggleButtonRadio() {
        setLayout(new FlowLayout());

        tools = new ArrayList<>();
    }

    public int getActiveTool() {return activeTool;}

    public void addButton(String name) {
        JToggleButton newButton = new JToggleButton(name);
        if (tools.size() == 0) newButton.setSelected(true);
        buttonInit(newButton);
        tools.add(newButton);
    }

    private void buttonInit(JToggleButton button) {
        button.setFocusable(false);
        button.addActionListener(e -> {
            tools.forEach(otherButton -> otherButton.setSelected(false));
            button.setSelected(true);
            activeTool = tools.indexOf(button);
            listener.buttonToggled(activeTool);
        });
        add(button);
    }

    public void addButtonToggledListener(ButtonToggledListener buttonToggledListener) {
        this.listener = buttonToggledListener;
    }

    public interface ButtonToggledListener {
        void buttonToggled(int buttonIndex);
    }
}
