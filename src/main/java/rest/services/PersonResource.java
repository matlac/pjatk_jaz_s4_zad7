package rest.services;

import domain.Person;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("people")
@Stateless
public class PersonResource {

    @PersistenceContext
    EntityManager em;
    private static int per_page = 10;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getPage(@DefaultValue("1")@QueryParam("page") int page) {
        int start = (per_page*page)-10;
        int end = (start+per_page)-1;

        return em.createNamedQuery("person.all", Person.class).
                setFirstResult(start).
                setMaxResults(per_page).getResultList();
    }

    @GET
    @Path("/pagesCount")
    @Produces(MediaType.TEXT_PLAIN)
    public long getPagesCount() {

        return (Long) em.createNamedQuery("person.count").getSingleResult();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response Add(Person p){
        em.persist(p);
        em.flush();
        return Response.ok(p.getId()).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, Person p){
        Person result = get_person(id);
        if(result==null){
            return Response.status(404).build();
        }

        result.setFirstName(p.getFirstName());
        result.setLastName(p.getLastName());
        result.setAge(p.getAge());
        result.setBirthday(p.getBirthday());
        result.setEmail(p.getEmail());
        result.setGender(p.getGender());

        em.persist(result);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response removePerson(@PathParam("id") int id){
        Person result = get_person(id);
        if(result == null)
            return Response.status(404).build();

        em.remove(result);
        return Response.ok().build();
    }

    private Person get_person(int id){
        return em.createNamedQuery("person.id", Person.class)
                .setParameter("personId", id)
                .getSingleResult();
    }

}
