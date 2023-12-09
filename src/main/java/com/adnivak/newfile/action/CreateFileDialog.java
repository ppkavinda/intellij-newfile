package com.adnivak.newfile.action;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.CharFilter;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.util.textCompletion.TextCompletionProvider;
import com.intellij.util.textCompletion.TextFieldWithCompletion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.adnivak.newfile.util.SuggestionUtils.getSuggestions;

// todo: create integration plugin, doc tool window in bottom, like in VIM
// improve suggestion list design
public class CreateFileDialog extends DialogWrapper {
    private static final Logger log = Logger.getInstance(CreateFileDialog.class);
    private JPanel contentPane;
    private TextFieldWithCompletion fileNameInput;
    private JLabel basePathLabel;
    private final DataContext dataContext;

    public CreateFileDialog(DataContext dataContext) {
        super(true);
        this.dataContext = dataContext;

        setTitle("New File");
        init();

        // call onCancel() when cross is clicked
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                onCancel();
//            }
//        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(
                e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );


        IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
        if (view == null) {
            return;
        }
        PsiDirectory directory = view.getOrChooseDirectory();
        if (directory == null) {
            return;
        }

        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        if (project == null) {
            return;
        }
        VirtualFile guessedProjectDir = ProjectUtil.guessProjectDir(project);
        if (guessedProjectDir == null) {
            return;
        }
        String basePath = guessedProjectDir.getPresentableUrl().concat(File.separator);
        String path = directory.getVirtualFile().getPresentableUrl().substring(basePath.length()).concat(File.separator);
        fileNameInput.setText(path);
        basePathLabel.setText("<html><b>" + basePath + "</b></html>");
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        createNew();
    }

    private boolean createNew() {
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        if (project == null) {
            return false;
        }
        String basePath = project.getBasePath();
        String fileNameInputText = fileNameInput.getText();
        Path filePath = Paths.get(basePath, fileNameInputText);
        boolean isDirectory = fileNameInputText.endsWith("\\") || fileNameInputText.endsWith("/");
        boolean created;
        try {
            if (isDirectory) {
                Files.createDirectories(filePath);
                created = true;
            } else {
                File newFile = new File(filePath.toString());
                newFile.getParentFile().mkdirs();
                created = newFile.createNewFile();
            }
            VirtualFileManager.getInstance().syncRefresh();
        } catch (IOException e) {
            created = false;
            log.error("unable to create file", e);
        } finally {
            dispose();
        }
        return created;
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return fileNameInput;
    }

    private void createUIComponents() {
        this.fileNameInput = getFileNameInput();
    }

    private TextFieldWithCompletion getFileNameInput() {
        TextCompletionProvider provider = new TextCompletionProvider() {
            @Override
            public String getAdvertisement() {
                return null;
            }

            @Override
            public String getPrefix(@NotNull String text, int offset) {
                return text;
            }

            @Override
            public @NotNull CompletionResultSet applyPrefixMatcher(@NotNull CompletionResultSet result, @NotNull String prefix) {
                return result.caseInsensitive();
            }

            @Override
            public CharFilter.Result acceptChar(char c) {
                return CharFilter.Result.ADD_TO_PREFIX;
            }

            @Override
            public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull String prefix, @NotNull CompletionResultSet result) {
                Project project = parameters.getPosition().getProject();
                List<String> suggestions = getSuggestions(project, fileNameInput.getText());
                for (String suggestion : suggestions) {
                    result.addElement(LookupElementBuilder.create(suggestion));
                }
            }
        };
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        TextFieldWithCompletion textFieldWithCompletion = new TextFieldWithCompletion(project, provider, "", true, true, true, true);
        textFieldWithCompletion.getComponentPopupMenu();
        return textFieldWithCompletion;
    }
}
