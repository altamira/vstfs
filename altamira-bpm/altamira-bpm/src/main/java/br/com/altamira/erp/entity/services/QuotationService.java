package br.com.altamira.erp.entity.services;

import br.com.altamira.erp.entity.dao.QuotationDao;
import br.com.altamira.erp.entity.model.Quotation;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.camunda.bpm.engine.RuntimeService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.joda.time.DateTime;

@Stateless
@Path("/quotation")
public class QuotationService {
    
    @Inject
    private QuotationDao quotationDao;

    @Inject
    private RuntimeService runtimeService;
    
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage list() {
        
        ReturnMessage message = new ReturnMessage();
        
        try {
        
            List<Quotation> quotations = quotationDao.list();
            
            message.setError(0);
            message.setMessage("Foram encontrados " + quotations.size() + " Cotações.");
            message.setData(quotations); 
            
        } catch (Exception e) {

            message.setError(9999);
            message.setMessage("Erro ao consultar as Cotações ! " + e != null ? e.getMessage() : "");

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
//            Quotation quotation = quotationDao.find(id);
//
//            if (quotation != null) {
//
//                message.setError(0);
//                message.setMessage("A Cotação #" + quotation.getId() + " foi encontrada.");
//                Hibernate.initialize(quotation);
//                message.setData(quotation);
//
//            } else {
//                
//                message.setError(9999);
//                message.setMessage("A Cotação #" + id + " não foi encontrada");
//                
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage getCurrent() {

        ReturnMessage message = new ReturnMessage();

        try {

            Quotation quotation = quotationDao.getCurrent();

            if (quotation != null) {

                message.setError(0);
                message.setMessage("A Cotação atual #" + quotation.getId() + " foi encontrada.");
                message.setData(quotation);

            } else {
                message.setError(9999);
                message.setMessage("A Cotação atual não foi encontrada");
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
    public ReturnMessage close() throws Exception {

        ReturnMessage message = new ReturnMessage();

        try {

            Quotation quotation = quotationDao.getCurrent();
            
            quotation.setClosedDate(DateTime.now().toDate());
            
            quotationDao.update(quotation);

            /*
            Map<String, Object> variables = new HashMap<String, Object>();

            variables.put("quotationId", quotation.getId());

            runtimeService.startProcessInstanceByKey("SteelRawMaterialPurchasingRequest", variables);
            */

            message.setError(0);
            message.setMessage("A Cotação #" + quotation.getId() + " foi encerrada com sucesso.");
            message.setData(quotation);

        } catch (Exception e) {

            message.setError(9999);
            message.setMessage("Erro ao criar a Requisição !\r\n" + (e != null ? e.getMessage() : ""));

        }

        return message;
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage update(Quotation quotation) {

        ReturnMessage message = new ReturnMessage();

        try {

            Quotation currentQuotation = quotationDao.getCurrent();
            
            if (currentQuotation.getId() != quotation.getId()) {

                message.setError(9999);
                message.setMessage("A Cotação #" + quotation.getId() + " não pode ser alterada porque não é a Cotação atual !");
                message.setData(quotation);

            } else {

                quotationDao.update(quotation);

                message.setError(0);
                message.setMessage("A Cotação " + quotation.getId() + " foi alterada com sucesso !");
                message.setData(quotation);
                
            }

        } catch (Exception e) {
            
            message.setError(9999);
            message.setMessage("Erro ao alterar a Cotação. " + (e != null ? e.getMessage() : ""));
            
        }

        return message;
    }

//    @DELETE
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage delete(Quotation quotation) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//            quotationDao.remove(quotation);
//
//            message.setError(0);
//            message.setMessage("A Cotação " + quotation.getId() + " foi excluida com sucesso !");
//            message.setData(quotation);
//
//        } catch (Exception e) {
//            message.setError(0);
//            message.setMessage("Erro ao excluir a Cotação " + quotation.getId() + ". " + (e != null ? e.getMessage() : ""));
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
//        Quotation quotation;
//
//        try {
//            
//            quotation = quotationDao.find(id);
//            
//        } catch (Exception ex) {
//            
//            message.setError(9999);
//            message.setMessage("Erro ao excluir a Cotação. A Cotação não foi encontrada !");
//            return message;
//            
//        }
//
//        try {
//
//            quotationDao.remove(quotation);
//
//            message.setError(0);
//            message.setMessage("A Cotação " + quotation.getId() + " foi excluida com sucesso.");
//            message.setData(quotation);
//
//        } catch (Exception e) {
//            
//            message.setError(9999);
//            message.setMessage("Erro ao excluir a Cotação !" + e != null ? e.getMessage() : "");
//            
//        }
//
//        return message;
//    }

//    @GET
//    @Path("0/item")
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage getQuotationItems(@PathParam("id") long id) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//
//            Quotation quotation = quotationDao.getCurrent();
//
//            if (quotation != null) {
//                
//                message.setError(0);
//                message.setMessage("Os itens da Cotação " + id + " foram encontrados com sucesso !");
//                message.setData(quotation.getItems());
//
//            } else {
//                
//                message.setError(9999);
//                message.setMessage("A Cotação " + id + " não foi encontrada !");
//                
//            }
//
//        } catch (Exception e) {
//
//            message.setError(0);
//            message.setMessage("Erro ao consultar os items da Cotação " + id + ". " + (e != null ? e.getMessage() : ""));
//        }
//
//        return message;
//    }

    /**
     * ************************************** Quotation Items ***************************************
     */
//    @GET
//    @Path("0/item")
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage getQuotationItem(@PathParam("id") long id) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//
//            QuotationItem quotationItem = quotationDao.find(QuotationItem.class, id);
//
//            if (quotationItem != null) {
//
//                message.setError(0);
//                message.setMessage("O item da Cotação " + id + " foi encontrado com sucesso.");
//                message.setData(quotationItem);
//
//            } else {
//                message.setError(9999);
//                message.setMessage("O item da Cotação " + id + " não foi encontrado !");
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
//
//    }
//
//    @POST
//    @Path("{id}/item")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage createQuotationItem(@PathParam("id") long id, QuotationItem quotationItem) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//            Quotation quotation = entityManager.find(Quotation.class, id);
//
//            if (quotation != null) {
//
//                quotation.getItems().add(quotationItem);
//                entityManager.persist(quotation);
//
//                entityManager.flush();
//
//                message.setError(0);
//                message.setMessage("O item da Cotação " + quotation.getId() + " foi criado com sucesso.");
//
//            } else {
//                message.setError(9999);
//                message.setMessage("A Cotação " + id + " não foi encontrada !");
//            }
//
//        } catch (Exception e) {
//
//            message.setError(9999);
//            message.setMessage("Erro ao criar o item a Cotação " + id + ". " + (e != null ? e.getMessage() : ""));
//
//        }
//
//        message.setData(quotationItem);
//
//        return message;
//    }
//
//    @PUT
//    @Path("item")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage updateQuotationItem(QuotationItem quotationItem) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//            entityManager.merge(quotationItem);
//
//            entityManager.flush();
//
//            message.setError(0);
//            message.setMessage("A Cotação " + quotationItem.getId() + " foi alterada com sucesso !");
//
//        } catch (Exception e) {
//            message.setError(9999);
//            message.setMessage("Erro ao alterar a Cotação. " + (e != null ? e.getMessage() : ""));
//        }
//
//        message.setData(quotationItem);
//
//        return message;
//    }
//
//    @DELETE
//    @Path("item")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage deleteQuotationItem(QuotationItem quotationItem) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//            entityManager.remove(entityManager.contains(quotationItem) ? quotationItem : entityManager.merge(quotationItem));
//
//            entityManager.flush();
//
//            message.setError(0);
//            message.setMessage("O item da Cotação foi excluido com sucesso !");
//
//        } catch (Exception e) {
//            message.setError(0);
//            message.setMessage("Erro ao excluir o item da Cotação. " + (e != null ? e.getMessage() : ""));
//        }
//
//        message.setData(quotationItem);
//
//        return message;
//    }
//
//    @DELETE
//    @Path("item/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage deleteQuotationItem(@PathParam("id") long id) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//
//            QuotationItem quotationItem = entityManager.find(QuotationItem.class, id);
//
//            if (quotationItem != null) {
//                entityManager.remove(quotationItem);
//
//                entityManager.flush();
//
//                message.setError(0);
//                message.setMessage("O item da Cotação foi excluido com sucesso !");
//                message.setData(quotationItem);
//
//            } else {
//                message.setError(9999);
//                message.setMessage("Erro ao excluir o item da Cotação. O item da Cotação não foi encontrado !");
//            }
//
//        } catch (Exception e) {
//
//            message.setError(0);
//            message.setMessage("Erro ao excluir o item da Cotação. " + (e != null ? e.getMessage() : ""));
//
//        }
//
//        return message;
//
//    }
//
//    /**
//     * ****************************************** Quotes
//     * ***************************************************
//     */
//    @POST
//    @Path("item/{id}/quote")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public ReturnMessage createQuotationItemQuote(@PathParam("id") long id, QuotationItemQuote quotationItemQuote) {
//
//        ReturnMessage message = new ReturnMessage();
//
//        try {
//
//            QuotationItem quotationItem = entityManager.find(QuotationItem.class, id);
//
//            if (quotationItem != null) {
//
//                quotationItem.getQuotes().add(quotationItemQuote);
//
//                entityManager.persist(quotationItemQuote);
//                //entityManager.merge(quotationItem);
//
//                entityManager.flush();
//
//                message.setError(0);
//                message.setMessage("O item da Cotação " + quotationItem.getId() + " foi criado com sucesso.");
//
//            } else {
//                message.setError(9999);
//                message.setMessage("O item " + id + " da Cotação não foi encontrada !");
//            }
//
//        } catch (Exception e) {
//
//            message.setError(9999);
//            message.setMessage("Erro ao criar o item a Cotação " + id + ". " + (e != null ? e.getMessage() : ""));
//
//        }
//
//        message.setData(quotationItemQuote);
//
//        return message;
//    }
    
    @GET
    @Path("{id}/report")
    @Produces("application/pdf")
    public Response getQuotationReportInPdf(@PathParam("id") long quotationId) {
        
        // generate report
        JasperPrint jasperPrint = null;

        try {
            byte[] quotationReportJasper = quotationDao.getQuotationReportJasperFile();
            byte[] quotationReportAltamiraimage = quotationDao.getQuotationReportAltamiraImage();
            byte[] pdf = null;

            final ByteArrayInputStream reportStream = new ByteArrayInputStream(quotationReportJasper);
            final Map<String, Object> parameters = new HashMap<String, Object>();

            List<Object[]> list = quotationDao.selectQuotationReportDataById(quotationId);
            List<BigDecimal> priceList = new ArrayList<BigDecimal>();

            // set pricelist for quotation items
            for (Object[] rs : list) {
                String materialLamination = rs[0].toString();
                String materialTreatment = rs[1].toString();
                String materialThickness = rs[2].toString();

                String str = "http://localhost:8085/altamira-bpm/rest/quotation/test/priceList?lamination=" + materialLamination + "&treatment=" + materialTreatment + "&thickness=" + materialThickness;

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet get = new HttpGet(str);
                get.addHeader("accept", "application/json");

                HttpResponse httpResponse = httpClient.execute(get);

                BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())));

                StringBuffer jsonObject = new StringBuffer();
                String json = new String();
                System.out.println("Output from Server .... \n");

                while ((json = br.readLine()) != null) {
                    jsonObject.append(json);
                    System.out.println(jsonObject);
                }

                // parse json response
                Map<String, Object> quotationItem = new HashMap<String, Object>();
                try {
                    quotationItem = new ObjectMapper().readValue(jsonObject.toString(), new TypeReference<HashMap<String, Object>>() {
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                List<Map> materialList = (List<Map>) ((Map<String, Object>) quotationItem.get("data")).get("materials");

                Map<String, String> map = materialList.get(0);
                String avgPrice = map.get("averageprice");

                priceList.add(new BigDecimal(avgPrice));

            }

            parameters.put("QUOTATION_ID", new BigDecimal(quotationId));
            parameters.put("PRICELIST", priceList);

            // set current pricelist code
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://localhost:8085/altamira-bpm/rest/quotation/test/current");
            get.addHeader("accept", "application/json");

            HttpResponse httpResponse = httpClient.execute(get);

            BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())));

            StringBuffer jsonObject = new StringBuffer();
            String json = new String();
            System.out.println("Output from Server .... \n");
            while ((json = br.readLine()) != null) {
                jsonObject.append(json);
                System.out.println(jsonObject);
            }

            httpClient.getConnectionManager().shutdown();

            // parse json response
            Map<String, Object> currentPriceList = new HashMap<String, Object>();
            try {
                currentPriceList = new ObjectMapper().readValue(jsonObject.toString(), new TypeReference<HashMap<String, Object>>() {
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            parameters.put("PRICELIST_CODE", ((Map<String, Object>) currentPriceList.get("data")).get("pricelist"));

            // set quotation Date
            Quotation quotation = quotationDao.getQuotationDetailsById(quotationId);
            parameters.put("QUOTATION_DATE", quotation.getCreatedDate());

            Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
            parameters.put("REPORT_LOCALE", locale);

            parameters.put("USERNAME", "Parth");

            BufferedImage imfg = null;
            try {
                InputStream in = new ByteArrayInputStream(quotationReportAltamiraimage);
                imfg = ImageIO.read(in);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            parameters.put("altamira_logo", imfg);

            Session session = quotationDao.unwrap();

            jasperPrint = session.doReturningWork(new ReturningWork<JasperPrint>() {
                @Override
                public JasperPrint execute(Connection connection) {
                    JasperPrint jasperPrint = null;

                    try {
                        jasperPrint = JasperFillManager.fillReport(reportStream, parameters, connection);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return jasperPrint;
                }
            });

            pdf = JasperExportManager.exportReportToPdf(jasperPrint);

            ByteArrayInputStream pdfStream = new ByteArrayInputStream(pdf);

            Response.ResponseBuilder response = Response.ok(pdfStream);
            response.header("Content-Disposition","inline; filename=Quotation Report.pdf");
            
            return response.build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (jasperPrint != null) {
                    // store generated report in database
                    quotationDao.insertGeneratedQuotationReport(jasperPrint);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not insert generated report in database.");
            }
        }
    }
    
    @GET
    @Path("/test/priceList")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage getPriceList(@QueryParam("lamination") String lamination,
                                      @QueryParam("treatment") String treatment,
                                      @QueryParam("thickness") String thickness) {
        
        ReturnMessage message = new ReturnMessage();
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("pricelist", "BL");

        List<Map> materialList = new ArrayList<Map>();

        Map<String, String> material = new HashMap<String, String>();
        material.put("code", "WBO00233");
        material.put("description", "AÇO LAMINADO FINA FRIO 2,65mm");
        material.put("baseprice", "2368");
        material.put("averageprice", new BigDecimal(thickness).multiply(new BigDecimal(1000)).toString());
        material.put("tax", "18");

        materialList.add(material);
        resultMap.put("materials", materialList);

        message.setData(resultMap);
        return message;
    }
    
    @GET
    @Path("/test/current")
    @Produces(MediaType.APPLICATION_JSON)
    public ReturnMessage getCurrentPriceList() {
        
        ReturnMessage message = new ReturnMessage();
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("pricelist", "BL");

        message.setData(resultMap);
        return message;
    }
    
    /**
     * ***********************************************************************************
     * Service methods
     ************************************************************************************
     */
}
