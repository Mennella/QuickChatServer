/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.api.jaxrs;


import it.univaq.disim.mobile.quickchat.business.MessageService;
import it.univaq.disim.mobile.quickchat.business.UserService;
import it.univaq.disim.mobile.quickchat.business.impl.JDBCMessageService;
import it.univaq.disim.mobile.quickchat.business.impl.JDBCUserService;
import it.univaq.disim.mobile.quickchat.business.model.Message;
import it.univaq.disim.mobile.quickchat.business.model.User;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("messages")
public class MessageResource {

    private final UserService userService = new JDBCUserService();
    private final MessageService messageService = new JDBCMessageService();

    //Questo metodo verrà chiamato se si esegue una richiesta GET sulla risorsa
    //REST rappresentata da questa classe. JAX-RS viene istruito a convertire
    //il tipo di ritorno in JSON  
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMessages(@Context UriInfo context, @QueryParam("token") String token) {
        User user = userService.find(token);
        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Set<Message> mesages = messageService.getMessages(user);
        messageService.removeRecipient(user);
        messageService.removeMessages();
        return Response.ok(mesages).build();
    }

}
