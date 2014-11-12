package br.com.altamira.erp.entity.services;

import br.com.altamira.erp.entity.dao.SupplierDao;
import br.com.altamira.erp.entity.dao.SupplierPriceListDao;
import br.com.altamira.erp.entity.model.Supplier;
import br.com.altamira.erp.entity.model.SupplierPriceList;
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
import org.joda.time.DateTime;

@Stateless
@Path("/supplier")
public class SupplierService {

    @Inject
    private SupplierDao supplierDao;
    
    
    @Inject
    private SupplierPriceListDao supplierPriceListDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage list() {
      
        ReturnMessage message = new ReturnMessage();
        
        try {
        
            List<Supplier> suppliers = supplierDao.list();
            
            message.setError(0);
            message.setMessage("Foram encontrados " + suppliers.size() + " Fornecedores.");
            message.setData(suppliers); 
            
        } catch (Exception e) {

            message.setError(9999);
            message.setMessage("Erro ao listar os Fornecedores ! " + e != null ? e.getMessage() : "");

        }

        return message;
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage getById(@PathParam("id") long id) {

        ReturnMessage message = new ReturnMessage();

        try {

            Supplier supplier = supplierDao.find(id);

            if (supplier != null) {

                message.setError(0);
                message.setMessage("O Fornecedor foi encontrada.");
                message.setData(supplier);

            } else {

                message.setError(9999);
                message.setMessage("O Fornecedor não foi encontrada");
                
            }

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage("Erro ao consulta o Fornecedor ! " + e != null ? e.getMessage() : "");

        }

        return message;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage create(Supplier supplier) throws Exception {

        ReturnMessage message = new ReturnMessage();

        try {

            Supplier existSupplier = supplierDao.find(supplier);

            if (existSupplier == null) {

                supplierDao.create(supplier);

            } else {
                
                supplier = existSupplier;
                
            }
            
            message.setError(0);
            message.setMessage("O Fornecedor " + supplier.getId() + " foi criado com sucesso.");
            message.setData(supplier);

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage("Erro ao criar o Fornecedor !" + e != null ? e.getMessage() : "");

        }

        return message;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage update(Supplier supplier) {

        ReturnMessage message = new ReturnMessage();

        try {
            supplierDao.update(supplier);

            message.setError(0);
            message.setMessage("O Fornecedor " + supplier.getId() + " foi alterado com sucesso.");
            message.setData(supplier);

        } catch (Exception e) {
            
            message.setError(9999);
            message.setMessage("Erro ao alterar o Fornecedor !" + e != null ? e.getMessage() : "");
            
        }

        return message;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage remove(Supplier supplier) {

        ReturnMessage message = new ReturnMessage();

        try {
            
            supplierDao.remove(supplier);

            message.setError(0);
            message.setMessage("O Fornecedor " + supplier.getId() + " foi excluido com sucesso.");
            message.setData(supplier);

        } catch (Exception e) {
            
            message.setError(9999);
            message.setMessage("Erro ao excluir o Fornecedor !" + e != null ? e.getMessage() : "");
            
        }

        return message;
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage remove(@PathParam("id") long id) {

        ReturnMessage message = new ReturnMessage();

        Supplier supplier;

        try {
            
            supplier = supplierDao.find(id);
            
        } catch (Exception ex) {
            
            message.setError(9999);
            message.setMessage("O Fornecedor não foi encontrado !");
            return message;
            
        }

        try {

            supplierDao.remove(supplier);

            message.setError(0);
            message.setMessage("O Fornecedor " + supplier.getId() + " foi excluido com sucesso.");
            message.setData(supplier);

        } catch (Exception e) {
            
            message.setError(9999);
            message.setMessage("Erro ao excluir o Fornecedor !" + e != null ? e.getMessage() : "");
            
        }

        return message;
    }
    
    @GET
    @Path("{id}/pricelist")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage getSupplierPriceListBySupplier(@PathParam("id") long id) {
        ReturnMessage message = new ReturnMessage();

        try {
            Supplier supplier = supplierDao.find(id);

            if (supplier != null) {
                List<SupplierPriceList> supplierPriceLists = supplierPriceListDao.find(supplier);

                if (supplierPriceLists != null) {
                    message.setError(0);
                    message.setMessage("Pricelist found for given Supplier.");
                    message.setData(supplierPriceLists);
                } else {
                    message.setError(9999);
                    message.setMessage("Pricelist not found for given Supplier.");
                }
            } else {
                message.setError(9999);
                message.setMessage("O Fornecedor não foi encontrado !");
            }


        } catch (Exception e) {
            message.setError(9999);
            message.setMessage("Erro ao consultar o fornecedor. " + e != null ? e.getMessage() : "");
        }

        return message;
    }
    
    @GET
    @Path("{SupplierID}/pricelist/{SupplierPriceListID}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage getUniqueSupplierPriceListBySupplier(@PathParam("SupplierID") long supplierId,
                                                              @PathParam("SupplierPriceListID") long supplierPriceListId) {
        ReturnMessage message = new ReturnMessage();

        try {
            Supplier supplier = supplierDao.find(supplierId);

            if (supplier != null) {
                SupplierPriceList supplierPriceList = supplierPriceListDao.find(supplier, supplierPriceListId);

                if (supplierPriceList != null) {
                    message.setError(0);
                    message.setMessage("Pricelist found for given Supplier and SupplierPriceListID.");
                    message.setData(supplierPriceList);
                } else {
                    message.setError(9999);
                    message.setMessage("Pricelist not found for given Supplier and SupplierPriceListID.");
                }
            } else {
                message.setError(9999);
                message.setMessage("O Fornecedor não foi encontrado !");
            }
        } catch (Exception e) {
            message.setError(9999);
            message.setMessage("Erro ao consultar o fornecedor. " + e != null ? e.getMessage() : "");
        }

        return message;
    }
    
    @POST
    @Path("pricelist")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage createSupplierPriceList(SupplierPriceList supplierPriceList) {
        ReturnMessage message = new ReturnMessage();

        try {
            
            supplierPriceList.setChangeDate(DateTime.now().toDate());

            supplierPriceList = supplierPriceListDao.create(supplierPriceList);

            message.setError(0);
            message.setMessage("SupplierPriceList is inserted with id: " + supplierPriceList.getId());
            message.setData(supplierPriceList);
            
        } catch (Exception e) {
            
            message.setError(9999);
            message.setMessage("Error occurred. " + e != null ? e.getMessage() : "");
            
        }

        return message;
    }
    
    @PUT
    @Path("pricelist")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage updateSupplierPriceList(SupplierPriceList supplierPriceList) {
        ReturnMessage message = new ReturnMessage();

        try {
            supplierPriceListDao.update(supplierPriceList);

            message.setError(0);
            message.setMessage("SupplierPriceList updated successfully");
            message.setData(supplierPriceList);
        } catch (Exception e) {
            message.setError(9999);
            message.setMessage("Error occurred. " + e != null ? e.getMessage() : "");
        }

        return message;
    }
    
    @DELETE
    @Path("pricelist")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage deleteSupplierPriceList(SupplierPriceList supplierPriceList) {
        ReturnMessage message = new ReturnMessage();

        try {
            SupplierPriceList supplierPriceListToDelete = supplierPriceListDao.find(supplierPriceList);
            
            if(supplierPriceListToDelete != null)
            {
                supplierPriceListDao.remove(supplierPriceListToDelete);
                message.setError(0);
                message.setMessage("SupplierPriceList deleted successfully");
            }
            else
            {
                message.setError(9999);
                message.setMessage("Could not find relevant record.");
            }
        } catch (Exception e) {
            message.setError(9999);
            message.setMessage("Error occurred. " + e != null ? e.getMessage() : "");
        }

        return message;
    }
    
    @DELETE
    @Path("{SupplierID}/pricelist/{SupplierPriceListID}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage deleteSupplierPriceList(@PathParam("SupplierID") long supplierId,
                                                 @PathParam("SupplierPriceListID") long supplierPriceListId) {
        ReturnMessage message = new ReturnMessage();

        try {
            Supplier supplier = supplierDao.find(supplierId);

            if (supplier != null) {
                SupplierPriceList supplierPriceList = supplierPriceListDao.find(supplier, supplierPriceListId);

                if (supplierPriceList != null) {
                    supplierPriceListDao.remove(supplierPriceList);

                    message.setError(0);
                    message.setMessage("SupplierPriceList deleted successfully");
                } else {
                    message.setError(9999);
                    message.setMessage("Pricelist not found for given Supplier and SupplierPriceListID.");
                }
            } else {
                message.setError(9999);
                message.setMessage("O Fornecedor não foi encontrado !");
            }
        } catch (Exception e) {
            message.setError(9999);
            message.setMessage("Erro ao consultar o fornecedor. " + e != null ? e.getMessage() : "");
        }

        return message;
    }
}
