package com.company.jmixpm.screen.user;

import com.company.jmixpm.entity.User;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.Image;
import io.jmix.ui.component.StreamResource;
import io.jmix.ui.component.data.value.ContainerValueSource;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;

@UiController("User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("usersTable")
@Route("users")
public class UserBrowse extends StandardLookup<User> {
    @Autowired
    private UiComponents uiComponents;
    /*@Autowired
    private CollectionContainer<User> usersDc;*/

    @Install(to = "usersTable.imageColumn", subject = "columnGenerator")
    private Component usersTableImageColumnColumnGenerator(User user) {
        Image<byte[]> image = uiComponents.create(Image.NAME);
        image.setWidth("30px");
        image.setHeight("30px");
        image.setScaleMode(Image.ScaleMode.CONTAIN);
        if (user.getAvatar() == null) {
            return null;
        }
//        image.setValueSource(new ContainerValueSource<>(usersDc, "avatar"));
        image.setSource(StreamResource.class).setStreamSupplier(() ->
                new ByteArrayInputStream(user.getAvatar()));
        return image;
    }
}