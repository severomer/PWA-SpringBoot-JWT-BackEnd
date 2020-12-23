package com.springist.demo.config;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

public class IncomeFilter extends BasicAuthenticationFilter {

	public IncomeFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
		// TODO Auto-generated constructor stub
	}
	  
    @Override
    protected void doFilterInternal(HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws IOException, ServletException {

        String authorizationHeader = req.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
       
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        System.out.println("authentication"+ authentication);
 /*       
		HttpSession session = req.getSession();
		session.setAttribute("user", authentication.getPrincipal());
 */       
        chain.doFilter(req, res);
    }

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
		// TODO Auto-generated method stub
	      String authorizationHeader = req.getHeader("Authorization");
	      
	         if (authorizationHeader == null) {
	             return null;
	         }

	         String token = authorizationHeader.replace("Bearer", "");

	         String userId = Jwts.parser()
	                 .setSigningKey("mytoken")
	                 .parseClaimsJws(token)
	                 .getBody()
	                 .getSubject();

	         if (userId == null) {
	             return null;
	         }
	   
	         return new UsernamePasswordAuthenticationToken(userId, "EMPLOYEE", new ArrayList<>());

	     }

}
