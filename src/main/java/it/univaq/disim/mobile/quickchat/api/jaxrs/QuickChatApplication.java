/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.api.jaxrs;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 *
 * @author Mennella
 */
@ApplicationPath("rest")
public class QuickChatApplication extends Application{

    private final Set<Class<?>> classes;

    public QuickChatApplication() {
        HashSet<Class<?>> c = new HashSet<Class<?>>();
        c.add(MessageResource.class);
        c.add(ChatResource.class);
        c.add(MediaResource.class);
        c.add(UserResource.class);
        c.add(TestResource.class);
        c.add(JacksonJsonProvider.class);
        c.add(MultiPartFeature.class);
        classes = Collections.unmodifiableSet(c);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}

