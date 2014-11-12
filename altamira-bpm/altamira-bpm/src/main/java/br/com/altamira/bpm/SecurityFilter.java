package br.com.altamira.bpm;

import br.com.altamira.erp.entity.services.ReturnMessage;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.camunda.bpm.engine.impl.util.json.JSONObject;

/**
 *
 * @author PARTH
 */
public class SecurityFilter implements Filter {
    
    public void doFilter(ServletRequest request, 
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpReq = (HttpServletRequest)request;
        HttpServletResponse httpRes = (HttpServletResponse)response;
        
        Principal principal = httpReq.getUserPrincipal();
        
        if(principal!=null || httpReq.getRequestURI().contains("/login"))
        {
            chain.doFilter(request, response);
        }
        else if(httpReq.getRequestURI().contains("/quotation/test"))
        {
            chain.doFilter(request, response);
        }
        else
        {
            // send json response
            httpRes.setContentType("application/json");
            PrintWriter printout = httpRes.getWriter();
            
            ReturnMessage message = new ReturnMessage();
            message.setError(9999);
            message.setMessage("Access Denied: Authorization required");
            message.setData(null);

            JSONObject jobj = new JSONObject(message);
            
            printout.write(jobj.toString());
        }
            
    }

    public void destroy() {        
    }

    public void init(FilterConfig filterConfig) {
    }
}
