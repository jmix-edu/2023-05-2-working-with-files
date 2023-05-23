package com.company.jmixpm.screen.task;

import com.company.jmixpm.app.TaskService;
import com.company.jmixpm.entity.Task;
import io.jmix.ui.component.BrowserFrame;
import io.jmix.ui.component.FileStorageResource;
import io.jmix.ui.model.InstanceContainer;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("Task_.edit")
@UiDescriptor("task-edit.xml")
@EditedEntityContainer("taskDc")
public class TaskEdit extends StandardEditor<Task> {
    @Autowired
    private TaskService taskService;
    @Autowired
    private BrowserFrame attachmentBrowseFrame;

    @Subscribe
    public void onInitEntity(InitEntityEvent<Task> event) {
        event.getEntity().setAssignee(taskService.findLeastBusyUser());
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        refreshAttachmentPreview();
    }

    @Subscribe(id = "taskDc", target = Target.DATA_CONTAINER)
    public void onTaskDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<Task> event) {
        if ("attachment".equals(event.getProperty())) {
            refreshAttachmentPreview();
        }
    }

    private void refreshAttachmentPreview() {
        Task task = getEditedEntity();
        if (task.getAttachment() != null) {
            attachmentBrowseFrame.setSource(FileStorageResource.class)
                    .setFileReference(task.getAttachment())
                    .setMimeType(task.getAttachment().getContentType());
        } else {
            attachmentBrowseFrame.reset();
        }
    }


}