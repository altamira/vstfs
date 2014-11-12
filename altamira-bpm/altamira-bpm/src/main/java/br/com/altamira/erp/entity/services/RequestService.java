package br.com.altamira.erp.entity.services;

import br.com.altamira.erp.entity.dao.MaterialDao;
import br.com.altamira.erp.entity.dao.RequestDao;
import br.com.altamira.erp.entity.model.Request;
import br.com.altamira.erp.entity.model.RequestReportData;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.camunda.bpm.engine.RuntimeService;
import org.joda.time.DateTime;

@Stateless
@Path("/request")
public class RequestService {
    
    @Inject
    private MaterialDao materialDao;
    
    @Inject
    private RequestDao requestDao;

    @Inject
    private RuntimeService runtimeService;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage getCurrent() {

        ReturnMessage message = new ReturnMessage();

        try {

            Request request = requestDao.getCurrent();

            if (request != null) {

                message.setError(0);
                message.setMessage("A Requisição atual foi encontrada.");
                message.setData(request);

            } else {
                
                message.setError(9999);
                message.setMessage("A Requisição atual não foi encontrada.");
                
            }

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage(e.getMessage());

        }

        return message;
    }

//    @GET
//    @Path("{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage getById(@PathParam("id") long id) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//
//            Request request = requestDao.find(id);
//
//            if (request != null) {
//
//                message.setError(0);
//                message.setMessage("A Requisição foi encontrada.");
//                message.setData(request);
//
//            } else {
//                message.setError(9999);
//                message.setMessage("A requisição não foi encontrada");
//            }
//
//        } catch (Exception e) {
//
//            message.setError(9999);
//            message.setMessage(e.getMessage());
//
//        }
//
//        return message;
//    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage close() throws Exception {

        ReturnMessage message = new ReturnMessage();

        try {

            Request request = requestDao.getCurrent();
            
            request.setSendDate(DateTime.now().toDate());
            
            requestDao.update(request);

            Map<String, Object> variables = new HashMap<String, Object>();

            variables.put("requestId", request.getId());

            runtimeService.startProcessInstanceByKey("SteelRawMaterialPurchasingRequest", variables);

            message.setError(0);
            message.setMessage("A Requisição #" + request.getId() + " foi fechada com sucesso.");
            message.setData(request);

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage("Erro ao criar a Requisição !\r\n" + (e != null ? e.getMessage() : ""));

        }

        return message;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage update(Request request) {

        ReturnMessage message = new ReturnMessage();

        try {

            Request currentRequest = requestDao.getCurrent();
            
            if (currentRequest.getId() != request.getId()) {
                
                message.setError(9999);
                message.setMessage("A Requisição #" + request.getId() + " não pode ser atualizada porque não é a Requisição atual.");
                message.setData(request);
                
            } else {

                requestDao.update(request);

                message.setError(0);
                message.setMessage("A Requisição #" + request.getId() + " foi atualizada com sucesso.");
                message.setData(request);
                
            }

        } catch (Exception e) {

            //userTransaction.rollback();
            message.setError(9999);
            message.setMessage("Erro ao atualizar a Requisição !\r\n" + (e != null ? e.getMessage() : ""));

        }

        return message;
    }

//    @DELETE
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage delete(Request request) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//
//            requestDao.remove(request);
//            
//            message.setError(0);
//            message.setMessage("A Requisição " + request.getId() + " foi excluida com sucesso.");
//            message.setData(request);
//
//        } catch (Exception e) {
//
//            message.setError(9999);
//            message.setMessage("Erro ao excluir a Requisição !\r\n" + (e != null ? e.getMessage() : ""));
//
//        }
//
//        return message;
//    }

//    @DELETE
//    @Path("{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage delete(@PathParam("id") long id) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//
//            Request request = requestDao.remove(id);
//            
//            message.setError(0);
//            message.setMessage("A Requisição " + request.getId() + " foi excluida com sucesso.");
//            message.setData(request);
//
//        } catch (Exception e) {
//
//            message.setError(9999);
//            message.setMessage("Erro ao excluir a Requisição !\r\n" + (e != null ? e.getMessage() : ""));
//
//        }
//
//        return message;
//    }
    
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage list() {

        ReturnMessage message = new ReturnMessage();

        try {

            List<Request> requests = requestDao.list();
                    
            message.setError(0);
            message.setMessage("Foram encontradas " + requests.size() + " Requisições.");
            message.setData(requests);

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage(e.getMessage());

        }

        return message;
    }
    
    /****************************************************************************************************
     * Current Request methods
     ***************************************************************************************************/
    
//    @POST
//    @Path("0/item")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage create(RequestItem requestItem) throws Exception {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//
//            Material material = materialDao.find(requestItem.getMaterial());
//            
//            if (material == null) {
//                materialDao.create(material);
//            }
//            
//            requestItem.setMaterial(material);
//            
//            Request request = requestDao.getCurrent();
//            
//            request.getItems().add(requestItem);
//            
//            requestDao.update(request);
//
//            message.setError(0);
//            message.setMessage("O item da Requisição " + request.getId() + " foi incluido com sucesso.");
//            message.setData(requestItem);
//
//
//        } catch (Exception e) {
//
//            message.setError(9999);
//            message.setMessage("Erro ao incluir o item da Requisição !\r\n" + (e != null ? e.getMessage() : ""));
//
//        }
//
//        return message;
//    }
//
//    @PUT
//    @Path("0/item")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage update(RequestItem requestItem) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//
//            Request request = requestDao.getCurrent();
//            
//            int i = request.getItems().indexOf(requestItem);
//            
//            if (i >= 0) {
//                Material material = materialDao.find(requestItem.getMaterial());
//
//                if (material == null) {
//                    materialDao.create(material);
//                }
//
//                requestItem.setMaterial(material);
//                request.getItems().set(i, requestItem);
//
//                requestDao.update(request);
//
//                message.setError(0);
//                message.setMessage("O Item " + i + " da Requisição " + request.getId() + " foi atualizada com sucesso.");
//                
//            } else {
//
//                message.setError(9999);
//                message.setMessage("O Item " + i + " da Requisição " + request.getId() + " não foi atualizado porque o item não foi encontrado.");
//                    
//            }
//            
//            message.setData(requestItem);
//            
//        } catch (Exception e) {
//
//            message.setError(9999);
//            message.setMessage("Erro ao atualizar o item da Requisição !\r\n" + (e != null ? e.getMessage() : ""));
//
//        }
//
//        return message;
//    }
//
//    @DELETE
//    @Path("0/item")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage delete(RequestItem requestItem) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//
//            Request request = requestDao.getCurrent();
//            
//            int i = request.getItems().indexOf(requestItem);
//            
//            if (i >= 0) {
//                
//                request.getItems().remove(i);
//                
//                requestDao.update(request);
//
//                message.setError(0);
//                message.setMessage("O item da Requisição " + request.getId() + " foi excluido. O item não foi encontrado.");
//                message.setData(requestItem);
//
//            } else {
//                
//                message.setError(9999);
//                message.setMessage("O item da Requisição " + request.getId() + " não foi excluido. O item não foi encontrado.");
//                message.setData(requestItem);
//
//            }
//
//        } catch (Exception e) {
//
//            message.setError(9999);
//            message.setMessage("Erro ao excluir a Requisição !\r\n" + (e != null ? e.getMessage() : ""));
//
//        }
//
//        return message;
//    }
    
    /****************************************************************************************************/
    
    @GET
    @Path("{id}/report")
    @Produces("application/pdf")
    public Response getRequestReportInPdf(@PathParam("id") long requestId) {
        
        // generate report
        JasperPrint jasperPrint = null;

        try {
            byte[] requestReportJasper = requestDao.getRequestReportJasperFile();
            byte[] requestReportAltamiraimage = requestDao.getRequestReportAltamiraImage();
            byte[] pdf = null;

            ByteArrayInputStream reportStream = new ByteArrayInputStream(requestReportJasper);
            Map<String, Object> parameters = new HashMap<String, Object>();

            List<Object[]> list = requestDao.selectRequestReportDataById(requestId);

            //Vector requestReportList = new Vector();
            ArrayList requestReportList = new ArrayList();
            List<Date> dateList = new ArrayList<Date>();

            BigDecimal lastMaterialId = new BigDecimal(0);
            int count = 0;
            BigDecimal sumRequestWeight = new BigDecimal(0);
            BigDecimal totalWeight = new BigDecimal(0);

            RequestReportData r = new RequestReportData();
            r.setId(null);
            r.setLamination(null);
            r.setLength(null);
            r.setThickness(null);
            r.setTreatment(null);
            r.setWidth(null);
            r.setArrivalDate(null);
            r.setWeight(null);

            requestReportList.add(r);

            for (Object[] rs : list) {
                RequestReportData rr = new RequestReportData();

                BigDecimal currentMaterialId = new BigDecimal(rs[0].toString());

                if (lastMaterialId.compareTo(currentMaterialId) == 0) {
                    rr.setWeight(new BigDecimal(rs[6].toString()));
                    rr.setArrivalDate((Date) rs[7]);

                    // copy REQUEST_DATE into dateList
                    dateList.add((Date) rs[7]);

                    System.out.println(new BigDecimal(rs[6].toString()));
                    totalWeight = totalWeight.add(new BigDecimal(rs[6].toString()));
                    sumRequestWeight = sumRequestWeight.add(new BigDecimal(rs[6].toString()));
                    count++;
                } else {
                    rr.setId(new BigDecimal(rs[0].toString()));
                    rr.setLamination((String) rs[1]);
                    rr.setTreatment((String) rs[2]);
                    rr.setThickness(new BigDecimal(rs[3].toString()));
                    rr.setWidth(new BigDecimal(rs[4].toString()));

                    if (rs[5] != null) {
                        rr.setLength(new BigDecimal(rs[5].toString()));
                    }

                    rr.setWeight(new BigDecimal(rs[6].toString()));
                    rr.setArrivalDate((Date) rs[7]);

                    // copy ARRIVAL_DATE into dateList
                    dateList.add((Date) rs[7]);

                    totalWeight = totalWeight.add(new BigDecimal(rs[6].toString()));
                    lastMaterialId = currentMaterialId;

                    if (count != 0) {
                        RequestReportData addition = new RequestReportData();
                        addition.setWeight(sumRequestWeight);

                        requestReportList.add(addition);
                    }

                    sumRequestWeight = new BigDecimal(rs[6].toString());
                    count = 0;
                }

                requestReportList.add(rr);
            }

            if (count > 0) {
                RequestReportData addition = new RequestReportData();
                addition.setWeight(sumRequestWeight);

                requestReportList.add(addition);
            }

            BufferedImage imfg = null;
            try {
                InputStream in = new ByteArrayInputStream(requestReportAltamiraimage);
                imfg = ImageIO.read(in);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            Collections.sort(dateList);

            parameters.put("REQUEST_START_DATE", dateList.get(0));
            parameters.put("REQUEST_END_DATE", dateList.get(dateList.size() - 1));
            parameters.put("REQUEST_ID", requestId);
            parameters.put("TOTAL_WEIGHT", totalWeight);
            parameters.put("altamira_logo", imfg);
            parameters.put("USERNAME", "Parth");

            Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
            parameters.put("REPORT_LOCALE", locale);

            JRDataSource dataSource = new JRBeanCollectionDataSource(requestReportList, false);

            jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource);

            pdf = JasperExportManager.exportReportToPdf(jasperPrint);

            ByteArrayInputStream pdfStream = new ByteArrayInputStream(pdf);

            ResponseBuilder response = Response.ok(pdfStream);
            response.header("Content-Disposition", "inline; filename=Request Report.pdf");

            return response.build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (jasperPrint != null) {
                    // store generated report in database
                    requestDao.insertGeneratedRequestReport(jasperPrint);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not insert generated report in database.");
            }
        }
    }

}
