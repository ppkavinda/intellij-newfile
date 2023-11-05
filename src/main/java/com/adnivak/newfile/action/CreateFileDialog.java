package com.adnivak.newfile.action;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

// todo: create integration plugin, doc tool window in bottom, like in VIM
// improve suggestion list design
public class CreateFileDialog extends DialogWrapper {
    private static final Logger log = Logger.getInstance(CreateFileDialog.class);
    private JPanel contentPane;
    private JTextField fileNameInput;
    private JList<String> suggestionList;
    private JLabel basePathLabel;
    private final DataContext dataContext;

    public CreateFileDialog(DataContext dataContext) {
        super(true);
        this.dataContext = dataContext;
        setTitle("New File");
        init();


        DefaultListModel<String> model = new DefaultListModel<>();
        suggestionList.setModel(model);
//        model.;

        // call onCancel() when cross is clicked
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                onCancel();
//            }
//        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        fileNameInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange("insert");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange("remove");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange("update");
            }
            public void onChange (String e) {
                IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
                if (view == null) {
                    return;
                }
                PsiDirectory directory = view.getOrChooseDirectory();
                if (directory == null) {
                    return;
                }
                List<String> suggestions = getSuggestions(directory, fileNameInput.getText());
                model.clear();
                model.addAll(suggestions);
                // todo: introduce auto fill
            }
        });

        fileNameInput.setFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        fileNameInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB && !model.isEmpty()) {
                    String bestMatch = model.get(0);
                    String inputText = fileNameInput.getText();
                    int lastIndex = inputText.lastIndexOf('/');
                    String path = inputText.substring(0, lastIndex == -1 ? inputText.length() : lastIndex);
                    fileNameInput.setText(path.concat("/").concat(bestMatch).concat("/"));
                }
            }
        });


        IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
        if (view == null) {
            return;
        }
        PsiDirectory directory = view.getOrChooseDirectory();
        if (directory == null) {
            return;
        }

        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        String basePath = project.getBasePath().concat("/");
        String path = directory.getVirtualFile().getCanonicalPath().replaceFirst("^/", "").substring(basePath.length()).concat("/");
        fileNameInput.setText(path);
        basePathLabel.setText("<html><b>" + basePath + "</b></html>");
    }


    // todo: improve suggestions, sort for best match
    private List<String> getSuggestions(PsiDirectory directory, String text) {
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        String basePath = project.getBasePath().concat("/");
        Path path = Paths.get(basePath + fileNameInput.getText());
        boolean isExists = Files.isDirectory(path);
        if (!isExists) {
            path = path.getParent();
        }
        boolean isNewPath = !Files.isDirectory(path);
        if (isNewPath) {    // creating a new path that currently not exists
            return Collections.emptyList();
        }
        List<String> fileList = new ArrayList<>();
        try {
            Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), 1, new SimpleFileVisitor<>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {if (attrs.isDirectory()) {
                        fileList.add(String.valueOf(file.getFileName()));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            log.error("invalid file path", ex);
        }
        return fileList;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();

        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        String basePath = project.getBasePath().concat("/");
        String filePath = basePath.concat(fileNameInput.getText().replaceFirst("^/", ""));
        File newFile = new File(filePath);
        try {
            newFile.getParentFile().mkdirs();
            newFile.createNewFile();
            VirtualFileManager.getInstance().syncRefresh();
        } catch (IOException e) {
            log.error("unable to create file", e);
        } finally {
            dispose();
        }

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
}
