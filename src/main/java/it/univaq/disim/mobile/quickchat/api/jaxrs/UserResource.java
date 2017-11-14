/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.api.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.univaq.disim.mobile.quickchat.api.jaxrs.common.ListWrapper;
import it.univaq.disim.mobile.quickchat.business.UserService;
import it.univaq.disim.mobile.quickchat.business.impl.JDBCUserService;
import it.univaq.disim.mobile.quickchat.business.model.User;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import sun.misc.BASE64Decoder;

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

    @POST
    @Path("profile")
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
//    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response validation(@FormParam("number") String phone) {

        System.out.println("number0: " + phone);
        int code = userService.genCode(phone);

        return Response.ok(code).build();

    }

    @POST
    @Path("signup")
//    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response signup(@Context UriInfo ctx, @FormParam("number") String phone, @FormParam("code") String code) throws IOException {
        if (userService.validation(phone, code)) {
            User user = new User();
            System.out.println("exits: " + userService.exists(phone));
            if (!userService.exists(phone)) {
                user.setPhone(phone);
                user.setUrlImg("icon.jpg");
                user = userService.create(user);
                String BASE_PATH = "C:/Users/Mennella/Documents/NetBeansProjects/QuickChatWorkspace/QuickChatMaven/src/main/webapp/media/user/" + user.getToken();

                new File(BASE_PATH).mkdirs();
                String s = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBw4QEA0NDhANDg0NEA8OEBAPDRANDhIQFREWFhURFRMYHSggGBolGxUVITEhJSkrLi4uFx8zODYsNygtLisBCgoKDg0NDw8QDjcdFRkrLTc3NysrKzc3KystNzcrLSs3LSsrKy0rLTctLTc3Ny0rKy0rKysrKysrLSsrKysrK//AABEIALcAtwMBIgACEQEDEQH/xAAbAAEAAgMBAQAAAAAAAAAAAAAAAgQBAwUGB//EADMQAQACAAQDBQMNAQAAAAAAAAABAgMEETEhQVEFYZGxwRIiMhMjM1JxcoGCobLR4fFC/8QAFwEBAQEBAAAAAAAAAAAAAAAAAAIBA//EABYRAQEBAAAAAAAAAAAAAAAAAAABEf/aAAwDAQACEQMRAD8A+rgOrkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAxMgyjNksPCtbbbrOy7hYNa989ebLWyKnyN9dNP4MXCtXjPGO7kvjNbjmRZnV0L0ieExEq1sn0n8Jj1brMaQvWazpOmpEtYAAAAAAAAAAAAASDEynlcKL6zO0frLXWs2tFY5790OjSkRERHCIZa2RmtYiNI4RDIJUAAAA142FFo058p6OfpMTNZ3h1FXPYese3G9d/sbKyxoEaSkpIAAAAAAAAAAjaUkLg29ncbXnpER4/4vuZ2Zf3716xr4T/AG6aKuAAAAAADFo1iY6xMMsXnSJnpEyDkYNuDerZfZZhaAAAAAAAAAABG6SNgR7Pj538s+jquX2fHzk91Z84dRFXAAAAAABrzE+5f7tvJsJjlyBxcvssw1WrFb2rG0TwbIUlkBrAAAAAAAABGySNgRyF9MXT60THr6Oq4+W+mp9s/tl2E1cAGAAAAADXj41aVm1to/WegOdmfpb/AJf2wnVWpeb2m872nX+lmqomsgNYAAAAAAAAMSyAqY9Z3jhMcYWcp2rrNaXifamYrExtMz16I3rqpYtPZmLRvWYnwZY2PRDFZ1iJjaeLKVAAAAI4k6VtPSJ8nmsKL209q1rabe1abeb0eZtpS89K28nHy1GxlbcKmjdDEQypIAAAAAAAAAAABKtmKrLXiQDd2XmYmvycz71OEd9V9xuz8P56J+rFp9PV2UVcAAAAcvtTNaz8jXum/pCGDHBHOYWmNaZ/60mPDRtpCompgNYAAAAAAAAAADEyzSlrbRw68gY1QtOvCOMzyW6ZT60690cG+mFWu0RHmnW405PL+xEzPxW37o6LIMUAAAA14+BW8aTvG084Ur4Nqb8Y6w6IaY5sWZW75as90938K98taNvej9fBWpxARiUmsAAAAAAEZktZZymBp71t52joy1shgZbnfw/laBKgAAAAAAAAAAAGvFwa233681HEpNZ0n8J5S6SGLhxaJif8JSxQiWUNJiZrO8JrQAAMSyjeQZy9PavEco4y6Sj2bHxz3xC8irgAAAAAAAAAAAAAAACl2hT4b/ln0aayu5yutL90a+HFz8KVRNbQGsGvEZAbuyp4X+96LwIq4AAAAAAAAAAAAAAAAhjfDb7J8nJy88AbGVYAUl//2Q==";
                BufferedImage bi = this.decodeToImage(s);

                File f = new File(BASE_PATH + "/icon.jpg");
                f.createNewFile();
                System.out.println("path" + BASE_PATH + "/icon.jpg");
                ImageIO.write(bi, "jpg", f);
            } else {
                user = userService.findByPhone(phone);
            }

            return Response.ok(user).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();

    }

    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    @POST
    @Path("contacts")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getContact(@Context UriInfo ctx, @HeaderParam("token") String token, @HeaderParam("timestamp") Long t, String numbers) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ListWrapper<String> w = objectMapper.readValue(numbers, ListWrapper.class);
        List<String> nums = w.getList();
        System.out.println("list size" + nums.size());
        Date date = null;
        if (t != null) {
            date = new java.util.Date(t.longValue());
        }
        List<User> users = userService.getContact(nums, date);
        return Response.ok(users).build();

    }

}
