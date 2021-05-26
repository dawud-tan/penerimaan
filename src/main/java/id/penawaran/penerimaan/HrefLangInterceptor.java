package id.penawaran.penerimaan;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

@WebFilter("/*")
public class HrefLangInterceptor implements Filter {

        @Override
	    public void doFilter(
	      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) 
	      throws IOException, ServletException {
			  HttpServletRequest request = (HttpServletRequest) servletRequest;
			  HttpServletResponse response = (HttpServletResponse) servletResponse;
			  String tipeDokumen = request.getHeader("Accept");
		      if(tipeDokumen != null && tipeDokumen.startsWith("text/html")){//perlu menggunakan pelokalan yang baku dari Spring :D
			     response.setHeader("Link", "<"+request.getRequestURL().toString()+">; rel=\"alternate\"; hreflang=\"id-Latn-ID\", <"+ request.getRequestURL().toString()+">; rel=\"alternate\"; hreflang=\"x-default\"");
			     response.setHeader("Content-Language", "id-Latn-ID");

			     String requestURI = request.getRequestURI();
			     if(requestURI != null && requestURI.endsWith(".pdf"))
					 response.setHeader("Link", "<"+request.getRequestURL().toString().replaceFirst(".pdf",".html")+">; rel=\"canonical\"");
				 else if(requestURI != null && requestURI.endsWith(".docx"))
					 response.setHeader("Link", "<"+request.getRequestURL().toString().replaceFirst(".docx",".html")+">; rel=\"canonical\"");
				 else if(requestURI != null && requestURI.endsWith(".doc"))
					 response.setHeader("Link", "<"+request.getRequestURL().toString().replaceFirst(".doc",".html")+">; rel=\"canonical\"");
				 else if(requestURI != null && requestURI.endsWith(".odt"))
					 response.setHeader("Link", "<"+request.getRequestURL().toString().replaceFirst(".odt",".html")+">; rel=\"canonical\"");
				 else if(requestURI != null && requestURI.contains("/amp/"))
					 response.setHeader("Link", "<"+request.getRequestURL().toString().replaceFirst("/amp/","/")+">; rel=\"canonical\"");
			     else
			         response.setHeader("Link", "<"+request.getRequestURL().toString()+">; rel=\"canonical\"");
	          }
	          filterChain.doFilter(request, response);
	    }
}