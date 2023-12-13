package com.adnivak.newfile.action;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.CharFilter;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.util.textCompletion.TextCompletionProvider;
import com.intellij.util.textCompletion.TextFieldWithCompletion;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

import static com.adnivak.newfile.util.SuggestionUtils.getSuggestions;

public class CreateNewPanel extends NonOpaquePanel {
    private final Project project;
    private TextFieldWithCompletion fileNameInput;

    public CreateNewPanel(@NotNull Project project) {
        this.project = project;
        this.init();
    }

    private void init() {
        this.setLayout(new BorderLayout());

        JBLabel label = new JBLabel();
        label.setText("Label");

        this.fileNameInput = getFileNameInput();

        this.add(label, BorderLayout.NORTH);
        this.add(fileNameInput, BorderLayout.SOUTH);
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
        return new TextFieldWithCompletion(project, provider, "", true, true, true, true);
    }
}
