package br.com.altamira.erp.entity.services;

import br.com.altamira.erp.entity.dao.StandardDao;
import br.com.altamira.erp.entity.model.Standard;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Stateless
@Path("/standard")
public class StandardService {

    @Inject
    private StandardDao standardDao;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage list() throws Exception {

        ReturnMessage message = new ReturnMessage();

        try {

            List<Standard> standards = standardDao.list();

            message.setError(!standards.isEmpty() ? 0 : 9998);
            message.setMessage(!standards.isEmpty() ? "Uma ou mais normas forão encontradas." : "Nenhuma Norma foi encontrada !");

            message.setData(standards);

        } catch (Exception e) {
            message.setError(9999);
            message.setMessage("Erro ao consultar a norma. " + e != null ? e.getMessage() : "");
        }

        return message;
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage getById(@PathParam("id") long id) {

        ReturnMessage message = new ReturnMessage();

        try {

            Standard standard = standardDao.find(id);

            if (standard != null) {

                message.setError(0);
                message.setMessage("A Norma foi encontrado.");
                message.setData(standard);

            } else {

                message.setError(9999);
                message.setMessage("A Norma não foi encontrado !");
            }

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage(e.getMessage());

        }

        return message;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage create(Standard standard) throws Exception {

        ReturnMessage message = new ReturnMessage();

        try {

            standardDao.create(standard);

            message.setError(0);
            message.setMessage("A norma " + standard.getId() + " foi criada com sucesso.");

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage("Erro ao criar a Norma !" + e != null ? e.getMessage() : "");

        }

        message.setData(standard);

        return message;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage update(Standard standard) {

        ReturnMessage message = new ReturnMessage();

        try {
            standardDao.update(standard);

            message.setError(0);
            message.setMessage("A norma " + standard.getId() + " foi alterada com sucesso.");
            message.setData(standard);

        } catch (Exception e) {
            
            message.setError(9999);
            message.setMessage("Erro ao alterar a Norma !" + e != null ? e.getMessage() : "");
            
        }

        return message;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage remove(Standard standard) {

        ReturnMessage message = new ReturnMessage();

        try {
            standardDao.remove(standard);

            message.setError(0);
            message.setMessage("A Norma " + standard.getId() + " foi excluida com sucesso.");

        } catch (Exception e) {
            message.setError(9999);
            message.setMessage("Erro ao excluir a Norma !" + e != null ? e.getMessage() : "");
        }

        message.setData(standard);

        return message;
    }

//    @DELETE
//    @Path("{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage remove(@PathParam("id") long id) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        Standard standard;
//
//        try {
//            standard = entityManager.find(Standard.class, id);
//        } catch (Exception ex) {
//            message.setError(9999);
//            message.setMessage("Erro ao excluir a Norma. A Norma não foi encontrada !");
//            return message;
//        }
//
//        try {
//
//            entityManager.remove(entityManager.contains(standard) ? standard : entityManager.merge(standard));
//
//            message.setError(0);
//            message.setMessage("A Norma " + standard.getId() + " foi excluida com sucesso.");
//
//        } catch (Exception e) {
//            message.setError(9999);
//            message.setMessage("Erro ao excluir a Norma !" + e != null ? e.getMessage() : "");
//        }
//
//        message.setData(standard);
//
//        return message;
//    }
}
