package com.adnivak.newfile.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.CharFilter;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.textCompletion.TextCompletionProvider;
import com.intellij.util.textCompletion.TextFieldWithCompletion;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.adnivak.newfile.util.SuggestionUtils.getSuggestions;

public class AutocompleteProvider {
    public static TextCompletionProvider getAutocompleteProvider(TextFieldWithCompletion fileNameInput) {
        return new TextCompletionProvider() {
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
    }
}
