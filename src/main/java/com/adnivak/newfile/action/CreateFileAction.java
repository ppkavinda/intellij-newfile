package com.adnivak.newfile.action;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

public class CreateFileAction extends AnAction {
    private DataContext dataContext;
    private static final Logger log = Logger.getInstance(CreateFileAction.class);
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CreateFileDialog createFileDialog = new CreateFileDialog(dataContext);
        createFileDialog.show();
    }
    @Override
    public void update(@NotNull AnActionEvent e) {
        dataContext = e.getDataContext();
        Presentation presentation = e.getPresentation();
        presentation.setEnabled(true);
    }
}
