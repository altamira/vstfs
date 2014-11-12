package br.com.altamira.erp.entity.services.wbccad;

import br.com.altamira.erp.entity.model.wbccad.Material;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
//@TransactionManagement(TransactionManagementType.BEAN)
@Path("/material")
public class MaterialService {

    @PersistenceContext(name="persistence/wbccad", unitName="wbccad-PU")
    private EntityManager entityManager;
//    @Resource
//    private UserTransaction userTransaction;
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage getMaterialById(@PathParam("id") long id) {

        ReturnMessage message = new ReturnMessage();

        try {

            Material material = entityManager.find(Material.class, id);

            if (material != null) {

                message.setError(0);
                message.setMessage("O Material foi encontrada.");
                message.setData(material);

            } else {

                message.setError(9999);
                message.setMessage("O Material n�o foi encontrada");
            }

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage("Erro ao consulta o Material ! " + e != null ? e.getMessage() : "");

        }

        return message;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage createMaterial(Material material) throws Exception {

        ReturnMessage message = new ReturnMessage();

        try {

            entityManager.persist(material);

            entityManager.flush();
            
            message.setError(0);
            message.setMessage("O Material " + material.getCode() + " foi criado com sucesso.");

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage("Erro ao criar o Material !" + e != null ? e.getMessage() : "");

        }

        message.setData(material);

        return message;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage updateMaterial(Material material) {

        ReturnMessage message = new ReturnMessage();

        try {
            entityManager.merge(material);

            message.setError(0);
            message.setMessage("O Material " + material.getCode() + " foi alterado com sucesso.");

        } catch (Exception e) {
            message.setError(9999);
            message.setMessage("Erro ao alterar o Material !" + e != null ? e.getMessage() : "");
        }

        message.setData(material);

        return message;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage deleteMaterialByObject(Material material) {

        ReturnMessage message = new ReturnMessage();

        try {
            entityManager.remove(entityManager.contains(material) ? material : entityManager.merge(material));

            message.setError(0);
            message.setMessage("O Material " + material.getCode() + " foi excluido com sucesso.");

        } catch (Exception e) {
            message.setError(9999);
            message.setMessage("Erro ao excluir o Material !" + e != null ? e.getMessage() : "");
        }

        message.setData(material);

        return message;
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage deleteMaterialById(@PathParam("id") long id) {

        ReturnMessage message = new ReturnMessage();

        Material material;

        try {
            material = entityManager.find(Material.class, id);
        } catch (Exception ex) {
            message.setError(9999);
            message.setMessage("Erro ao excluir o Material. O Material n�o foi encontrado !");
            return message;
        }

        try {

            entityManager.remove(entityManager.contains(material) ? material : entityManager.merge(material));

            message.setError(0);
            message.setMessage("O Material " + material.getCode() + " foi excluido com sucesso.");

        } catch (Exception e) {
            message.setError(9999);
            message.setMessage("Erro ao excluir o Material !" + e != null ? e.getMessage() : "");
        }

        message.setData(material);

        return message;
    }
}
