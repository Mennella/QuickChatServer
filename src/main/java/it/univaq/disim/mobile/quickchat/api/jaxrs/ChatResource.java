/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.api.jaxrs;

import it.univaq.disim.mobile.quickchat.business.ChatroomService;
import it.univaq.disim.mobile.quickchat.business.UserService;
import it.univaq.disim.mobile.quickchat.business.impl.JDBCChatroomService;
import it.univaq.disim.mobile.quickchat.business.impl.JDBCUserService;
import it.univaq.disim.mobile.quickchat.business.model.Chatroom;
import it.univaq.disim.mobile.quickchat.business.model.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author Mennella
 */
@Path("chat")
public class ChatResource {

    private final UserService userService = new JDBCUserService();
    private final ChatroomService chatroomService = new JDBCChatroomService();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUpdatedChatrooms(@Context UriInfo context, @HeaderParam("token") String token, @QueryParam("time") long time) {
        System.out.println("token " + token);
        User user = userService.find(token);
        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        System.out.println("data " + new Date(time));
        System.out.println("data timestamp " + time);
        Set<Chatroom> chat = chatroomService.getUpdatedChat(new Date(time), user);
        return Response.ok(chat).build();
    }

    @GET
    @Path("{chat_token: [a-zA-Z0-9 -]+ }/people")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getParticipants(@PathParam("chat_token") String chat_token, @QueryParam("token") String token) {
        User user = userService.find(token);
        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Chatroom c = chatroomService.find(chat_token);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(c.getParticipants()).build();
    }

    @POST
    @Path("{chat_token: [a-zA-Z0-9 -]+ }/people")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addParticipant(@PathParam("chat_token") String chat_token, @HeaderParam("token") String token, String payload) {
        User user = userService.find(token);
        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Chatroom c = chatroomService.find(chat_token);
        User u = userService.find(Integer.parseInt(payload));
        if (c == null || u == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        chatroomService.addParticipant(u, c);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{chat_token: [a-zA-Z0-9 -]+ }")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeParticipant(@PathParam("chat_token") String chat_token, @HeaderParam("token") String token) {
        User user = userService.find(token);
        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Chatroom c = chatroomService.find(chat_token);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        chatroomService.removeParticipant(user, c);
        return Response.noContent().build();
    }

    @POST
    @Path("update/{chat_token: [a-zA-Z0-9 -]+ }")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response updateChatrooms(
            @FormDataParam("file") InputStream file,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @PathParam("chat_token") String chat_token, @HeaderParam("token") String token, @FormDataParam("name") String name) {
        System.out.println("token chat: " + chat_token);
        System.out.println("token user: " + token);
//        System.out.println("token name: " + name);
//        String UPLOAD_PATH = "QuickChatMaven\\src\\main\\webapp\\media\\";//        UPLOAD_PATH += chat_token + "/";
        String UPLOAD_PATH = "C:/Users/Mennella/Documents/NetBeansProjects/QuickChatWorkspace/QuickChatMaven/src/main/webapp/media/chat/" + chat_token + "/icon";
        User user = userService.find(token);

        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Chatroom c = chatroomService.find(chat_token);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            int read = 0;
            byte[] bytes = new byte[1024];
            new File(UPLOAD_PATH).mkdirs();

            OutputStream out = new FileOutputStream(new File(UPLOAD_PATH + "/" + fileDetail.getFileName()));
            while ((read = file.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        c.setName(name);
        c.setUrlImg(fileDetail.getFileName());

        chatroomService.update(c);

        return Response.ok(c).build();
    }


}
