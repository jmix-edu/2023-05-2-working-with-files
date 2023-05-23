package com.company.jmixpm.screen.task;

import com.company.jmixpm.entity.Project;
import com.company.jmixpm.entity.Task;
import com.company.jmixpm.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.FileStorageUploadField;
import io.jmix.ui.component.LinkButton;
import io.jmix.ui.component.SingleFileUploadField;
import io.jmix.ui.download.Downloader;
import io.jmix.ui.screen.*;
import io.jmix.ui.upload.TemporaryStorage;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UiController("Task_.browse")
@UiDescriptor("task-browse.xml")
@LookupComponent("tasksTable")
public class TaskBrowse extends StandardLookup<Task> {
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private Downloader downloader;
    @Autowired
    private TemporaryStorage temporaryStorage;
    @Autowired
    private FileStorageUploadField uploadTasksField;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Install(to = "tasksTable.attachment", subject = "columnGenerator")
    private Component tasksTableAttachmentColumnGenerator(Task task) {
        if (task.getAttachment() == null) {
            return null;
        }
        LinkButton linkButton = uiComponents.create(LinkButton.class);
        linkButton.setCaption(task.getAttachment().getFileName());
        linkButton.addClickListener(clickEvent -> {
            downloader.setShowNewWindow(false);
            downloader.download(task.getAttachment());
        });
        return linkButton;
    }

    @Subscribe("uploadTasksField")
    public void onUploadTasksFieldFileUploadSucceed(SingleFileUploadField.FileUploadSucceedEvent event) throws IOException {
        UUID fileId = uploadTasksField.getFileId();
        if (fileId == null) {
            return;
        }
        File file = temporaryStorage.getFile(fileId);
        if (file == null) {
            return;
        }

        processFile(file);

        temporaryStorage.deleteFile(fileId);
    }

    private void processFile(File file) throws IOException {
        List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
        List<Task> tasks = new ArrayList<>(lines.size());
        for (String line : lines) {
            Task task = dataManager.create(Task.class);
            task.setName(line);
            task.setProject(loadDefaultProject());
            task.setAssignee((User) currentAuthentication.getUser());
            tasks.add(task);
        }

        dataManager.save(tasks.toArray());

        getScreenData().loadAll();
    }

    private Project loadDefaultProject() {
        return  dataManager.load(Project.class)
                .query("select p from Project p where p.defaultProject = :defaultProject")
                .parameter("defaultProject", true)
                .one();
    }
}