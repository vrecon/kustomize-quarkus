package nl.logius.resource;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/Hello")
@Produces("text/plain")
public class HelloResource {

    @GET
    public String hello() {
        return "Hello REST new docker tag via temp 4";
    }
}
