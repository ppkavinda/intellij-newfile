package com.adnivak.newfile.action.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class AutocompleteTextField extends JTextField {
    private JList<String> suggestionsList;

    public AutocompleteTextField(String[] suggestions) {
        super();
        suggestionsList = new JList<>(suggestions);
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }
        });
    }

    private void updateSuggestions() {
        String input = getText().toLowerCase();
        DefaultListModel<String> model = (DefaultListModel<String>) suggestionsList.getModel();
        model.clear();

        String[] suggestions = {"a", "ab", "abc", "abcd"};

        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(input)) {
                model.addElement(suggestion);
            }
        }

        if (model.getSize() > 0) {
            // Show the suggestion list below the text field
            suggestionsList.setVisibleRowCount(Math.min(model.getSize(), 5));
            suggestionsList.setSelectedIndex(0);
            // Position and display the suggestion list
            Point location = getLocationOnScreen();
            location.y += getHeight();
            suggestionsList.setLocation(location);
            suggestionsList.setVisible(true);
        } else {
            suggestionsList.setVisible(false);
        }
    }
}
