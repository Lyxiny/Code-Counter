package cem.lyxiny.plugin;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CodeCounterAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(file != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile selectedFile = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (selectedFile == null) {
            showNotification(project, "No file selected!", NotificationType.ERROR);
            return;
        }

        try {
            CountResult result = countLinesAndChars(selectedFile);

            String message = String.format("""
                Count Results for '%s':
                Total Lines: %d
                Total Characters: %d
                """, selectedFile.getName(), result.lines, result.chars);

            showNotification(project, message, NotificationType.INFORMATION);

        } catch (Exception ex) {
            showNotification(project, "Error counting: " + ex.getMessage(), NotificationType.ERROR);
        }
    }

    private CountResult countLinesAndChars(VirtualFile file) throws Exception {
        long totalLines = 0;
        long totalChars = 0;

        if (file.isDirectory()) {

            for (VirtualFile child : file.getChildren()) {
                CountResult childResult = countLinesAndChars(child);
                totalLines += childResult.lines;
                totalChars += childResult.chars;
            }
        } else {

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    totalLines++;
                    totalChars += line.length();
                }
            }
        }
        return new CountResult(totalLines, totalChars);
    }

    private void showNotification(Project project, String content, NotificationType type) {
        Notifications.Bus.notify(
                new com.intellij.notification.Notification(
                        "CodeCounterGroup" +
                                "",
                        "Code Counter",
                        content,
                        type
                ),
                project
        );
    }

    private static class CountResult {
        final long lines;
        final long chars;

        CountResult(long lines, long chars) {
            this.lines = lines;
            this.chars = chars;
        }
    }
}