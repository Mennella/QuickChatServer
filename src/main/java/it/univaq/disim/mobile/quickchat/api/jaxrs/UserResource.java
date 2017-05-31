/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.api.jaxrs;

import it.univaq.disim.mobile.quickchat.business.UserService;
import it.univaq.disim.mobile.quickchat.business.impl.JDBCUserService;
import it.univaq.disim.mobile.quickchat.business.model.Chatroom;
import it.univaq.disim.mobile.quickchat.business.model.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
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
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author Mennella
 */
@Path("user")
public class UserResource {

    private final UserService userService = new JDBCUserService();

    @GET
    @Path("profile")
    @Produces({MediaType.APPLICATION_JSON})
    public Response profile(@QueryParam("token") String token) {
        User user = userService.find(token);
        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok(user).build();
    }

    @PUT
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response updateUser(
            @FormDataParam("file") InputStream file,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @HeaderParam("token") String token, @FormDataParam("name") String name) {
        String UPLOAD_PATH = "C:/Users/Mennella/Documents/NetBeansProjects/QuickChatWorkspace/QuickChatMaven/src/main/webapp/media/user/" + token;
        User user = userService.find(token);

        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
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

        user.setName(name);
        user.setUrlImg(fileDetail.getFileName());

        userService.update(user);

        return Response.ok(user).build();
    }

    @POST
    @Path("validation")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response validation(@FormDataParam("number") String phone) {

        System.out.println("number0: " + phone);
        int code = userService.genCode(phone);

        return Response.ok(code).build();

    }

    @POST
    @Path("signup")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response signup(@Context UriInfo ctx, @FormDataParam("number") String phone, @FormDataParam("code") String code) {
        if (userService.validation(phone, code)) {
            User user = new User();
            user.setPhone(phone);
            user = userService.create(user);

            return Response.ok(user).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();

    }

    @POST
    @Path("contact")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getContact(@Context UriInfo ctx, @HeaderParam("token") String token, @HeaderParam("timestamp") Long t, Set<String> numbers) {
        Date date = null;
        if (t != null) {
            date = new java.util.Date(t.longValue());
        }
        Set<User> users = userService.getContact(numbers, date);
        return Response.ok(users).build();

    }

}
