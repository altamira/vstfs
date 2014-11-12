package br.com.altamira.erp.entity.services;

import br.com.altamira.erp.entity.dao.MaterialDao;
import br.com.altamira.erp.entity.model.Material;
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
@Path("/material")
public class MaterialService {
    
    @Inject 
    private MaterialDao materialDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage list() {
        
        ReturnMessage message = new ReturnMessage();
        
        try {
        
            List<Material> materials = materialDao.list();
            
            message.setError(0);
            message.setMessage("Foram encontrados " + materials.size() + " Materiais.");
            message.setData(materials); 
            
        } catch (Exception e) {

            message.setError(9999);
            message.setMessage("Erro ao consulta o Material ! ");
            message.setData(e);

        }

        return message;
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage getById(@PathParam("id") long id) {

        ReturnMessage message = new ReturnMessage();

        try {

            Material material = materialDao.find(id);

            if (material != null) {

                message.setError(0);
                message.setMessage("O Material foi encontrada.");
                message.setData(material);

            } else {

                message.setError(9999);
                message.setMessage("O Material não foi encontrada");
                
            }

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage("Erro ao consulta o Material ! ");
            message.setData(e);

        }

        return message;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage create(Material material) throws Exception {

        ReturnMessage message = new ReturnMessage();

        try {

            Material existMaterial = materialDao.find(material);

            if (existMaterial == null) {

                materialDao.create(material);

            } else {
                
                material = existMaterial;
                
            }
            
            message.setError(0);
            message.setMessage("O Material " + material.getId() + " foi criado com sucesso.");
            message.setData(material);

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage("Erro ao criar o Material !");
            message.setData(e);

        }

        return message;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage update(Material material) {

        ReturnMessage message = new ReturnMessage();

        try {
            materialDao.update(material);

            message.setError(0);
            message.setMessage("O Material " + material.getId() + " foi alterado com sucesso.");
            message.setData(material);

        } catch (Exception e) {
            
            message.setError(9999);
            message.setMessage("Erro ao alterar o Material !");
            message.setData(e);
            
        }

        return message;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage deleteByObject(Material material) {

        ReturnMessage message = new ReturnMessage();

        try {
            
            materialDao.remove(material);

            message.setError(0);
            message.setMessage("O Material " + material.getId() + " foi excluido com sucesso.");
            message.setData(material);

        } catch (Exception e) {
            
            message.setError(9999);
            message.setMessage("Erro ao excluir o Material !");
            message.setData(e);
            
        }

        return message;
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage deleteById(@PathParam("id") long id) {

        ReturnMessage message = new ReturnMessage();

        Material material;

        try {
            
            material = materialDao.find(id);
            
        } catch (Exception ex) {
            
            message.setError(9999);
            message.setMessage("Erro ao excluir o Material. O Material não foi encontrado !");
            return message;
            
        }

        try {

            materialDao.remove(material);

            message.setError(0);
            message.setMessage("O Material " + material.getId() + " foi excluido com sucesso.");
            message.setData(material);

        } catch (Exception e) {
            
            message.setError(9999);
            message.setMessage("Erro ao excluir o Material !");
            message.setData(e);
            
        }

        return message;
    }
}
