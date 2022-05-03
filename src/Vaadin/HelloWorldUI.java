package Vaadin;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import java.util.Date;

@Title("Light Vaadin")
@Theme("valo")
public class HelloWorldUI extends UI {

    private static final long serialVersionUID = 1L;
//    private CssLayout layout;

    @Override
    protected void init(VaadinRequest request)
    {
        setContent(new VerticalLayout(
            new Label("Hello Vaadin LIGHT!"),
            new Button("Click me", event ->
            {
                Notification.show("Hello at " + new Date());
            }
            ))
        );
    }
}