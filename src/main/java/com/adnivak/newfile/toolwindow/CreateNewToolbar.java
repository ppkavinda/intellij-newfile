package com.adnivak.newfile.toolwindow;

import com.adnivak.newfile.action.CreateNewPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.SideBorder;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CreateNewToolbar {
    private final JBPanel contentToolWindow;

    public JBPanel getContent()
    {
        return this.contentToolWindow;
    }

    public CreateNewToolbar(@NotNull Project project) {
        this.contentToolWindow = new SimpleToolWindowPanel(true, true);

        CreateNewPanel createNewPanel = new CreateNewPanel(project);
        createNewPanel.setBorder(IdeBorderFactory.createBorder(SideBorder.TOP | SideBorder.RIGHT));
//        OnePixelSplitter horizontalSplitter = new OnePixelSplitter(true, 0.0f);
//        horizontalSplitter.setBorder(BorderFactory.createEmptyBorder());
//        horizontalSplitter.setDividerPositionStrategy(Splitter.DividerPositionStrategy.KEEP_FIRST_SIZE);
//        horizontalSplitter.setResizeEnabled(false);
//        horizontalSplitter.setFirstComponent(label);
//        horizontalSplitter.setSecondComponent(createNewPanel);
        this.contentToolWindow.add(createNewPanel);
    }

}
