package com.adnivak.newfile.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class SuggestionUtils {
    private static final Logger log = Logger.getInstance(SuggestionUtils.class);

    // todo: improve suggestions, sort for best match
    public static List<String> getSuggestions(Project project, String text) {
        VirtualFile guessedProjectDir = ProjectUtil.guessProjectDir(project);
        if (guessedProjectDir == null) {
            return Collections.emptyList();
        }
        String basePath = guessedProjectDir.getPath();
        Path path = Paths.get(basePath, text);
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
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (attrs.isDirectory()) {
                        fileList.add(String.valueOf(file.getFileName()));
//                                .concat(File.separator));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            log.error("invalid file path", ex);
        }
        return fileList;
    }
}
