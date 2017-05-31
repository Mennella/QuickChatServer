/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.api.jaxrs;

import it.univaq.disim.mobile.quickchat.api.jaxrs.common.ImageResize;
import it.univaq.disim.mobile.quickchat.business.UserService;
import it.univaq.disim.mobile.quickchat.business.impl.JDBCUserService;
import it.univaq.disim.mobile.quickchat.business.model.Chatroom;
import it.univaq.disim.mobile.quickchat.business.model.User;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
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
@Path("media")
public class MediaResource {

    private final UserService userService = new JDBCUserService();
    private final ImageResize imageResize = new ImageResize();

    @POST
    @Path("{chat_token: [a-zA-Z0-9 -]+ }")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response uploadMedia(
            @Context UriInfo ctx,
            @FormDataParam("file") InputStream file,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @PathParam("chat_token") String chat_token, @HeaderParam("token") String token, @FormDataParam("type") String type) {

        String BASE_PATH = "C:/Users/Mennella/Documents/NetBeansProjects/QuickChatWorkspace/QuickChatMaven/src/main/webapp/media/chat/" + chat_token;
        User user = userService.find(token);
        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        System.out.println("name file: " + fileDetail.getFileName());

        switch (type) {
            case "image":
                BASE_PATH += "/img/";
                break;
            case "video":
                BASE_PATH += "/video";
                break;
            case "audio":
                BASE_PATH += "/audio";
                break;
            default:
                System.out.println("not found");
                return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            int read = 0;
            byte[] bytes = new byte[1024];
            new File(BASE_PATH).mkdirs();

            OutputStream out = new FileOutputStream(new File(BASE_PATH + "/" + fileDetail.getFileName()));
            while ((read = file.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        URI uri = ctx.getBaseUriBuilder().path(getClass(), "getMedia").build(chat_token, fileDetail.getFileName());

        return Response.created(uri).build();
    }

    @GET
    @Path("people/{user_id: [0-9]+ }")
    @Produces("application/octet-stream")
    public Response getPeopleMedia(@PathParam("user_id") int user_id,
            @QueryParam("token") String token,
            @QueryParam("type") String type) {
        
        User u = userService.find(token);
        if (u == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        System.out.println("id: "+ user_id);
        User user = userService.find(user_id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        String BASE_PATH = "C:/Users/Mennella/Documents/NetBeansProjects/QuickChatWorkspace/QuickChatMaven/src/main/webapp/media/user/" + user.getToken() + "/";
        switch (type) {
            case "icon":
                BASE_PATH += user.getUrlImg();
                return getMedia(BASE_PATH);
            case "iconR":
                return getResizeImage(BASE_PATH, user.getUrlImg());
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    
    @GET
    @Path("{chat_token: [a-zA-Z0-9 -]+ }/{file_name: [a-zA-Z0-9 _ .]+}")
//    @Path("{chat_token: [a-zA-Z0-9 -]+ }/{file_name: [a-zA-Z0-9 _ .]}")
    @Produces("application/octet-stream")
    public Response getMedia(@PathParam("chat_token") String chat_token,
            @PathParam("file_name") String filename,
            @QueryParam("token") String token,
            @QueryParam("type") String type) {
        String BASE_PATH = "C:/Users/Mennella/Documents/NetBeansProjects/QuickChatWorkspace/QuickChatMaven/src/main/webapp/media/chat/" + chat_token;
        User user = userService.find(token);
        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        switch (type) {
            case "icon":
                BASE_PATH += "/icon/" + filename;
                return getMedia(BASE_PATH);
            case "iconR":
                BASE_PATH += "/icon/";
                return getResizeImage(BASE_PATH, filename);
            case "imageR":
                BASE_PATH += "/img/";
                return getResizeImage(BASE_PATH, filename);
            case "image":
                BASE_PATH += "/img/" + filename;
                return getMedia(BASE_PATH);
            case "video":
                BASE_PATH += "/video" + filename;
                return getMedia(BASE_PATH);
            case "audio":
                BASE_PATH += "/audio" + filename;
                return getMedia(BASE_PATH);
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private Response getResizeImage(String path, String filename) {
        new File(path).mkdirs();
        try {
            BufferedImage originalImage = ImageIO.read(new File(path + filename));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            File f = File.createTempFile(filename, getExtension(filename));
//            File f = new File("C:/Users/Mennella/Documents/NetBeansProjects/QuickChatWorkspace/QuickChatMaven/src/main/webapp/media/tmp/" + filename);

            BufferedImage resizeImageJpg = imageResize.resizeImage(originalImage, type);
            ImageIO.write(resizeImageJpg, "jpg", f);

            if (f.exists() && !f.isDirectory()) {
                return Response.ok(f).build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (IOException ex) {
            Logger.getLogger(MediaResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private Response getMedia(String path) {
        File f = new File(path);
        if (f.exists() && !f.isDirectory()) {
            return Response.ok(f).build();
        }
        System.out.println("nonn trovato: "+ path);
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private String getExtension(String filename) {
        return filename.substring(filename.indexOf("."));
    }

}
