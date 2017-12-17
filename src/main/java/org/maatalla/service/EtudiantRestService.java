package org.maatalla.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.maatalla.dao.EtudiantRepository;
import org.maatalla.entities.Etudiant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EtudiantRestService {
	@Autowired
	private EtudiantRepository etudiantRepository;
	
	@Secured(value={"ROLE_ADMIN", "ROLE_SCOLARITE"})
	@RequestMapping(value="/etudiants", method=RequestMethod.POST)
	public Object saveEtudiant(@RequestBody @Valid Etudiant e, BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			Map<String, Object> errors = new HashMap<>();
			errors.put("errors", true);
			for(FieldError fe : bindingResult.getFieldErrors()){
				errors.put(fe.getField(), fe.getDefaultMessage());
			}
			return errors;
		}
		return etudiantRepository.save(e);
	}
	
	@Secured(value={"ROLE_ADMIN", "ROLE_SCOLARITE", "ROLE_PROF", "ROLE_ETUDIANT"})
	@RequestMapping(value="/etudiants", method=RequestMethod.GET)
	public Page<Etudiant> listEtudiants(@RequestParam(value="page", defaultValue="0") int page, @RequestParam(value="size", defaultValue="3") int size){
		return etudiantRepository.findAll(new PageRequest(page, size));
	}
	
	@RequestMapping(value="/getlogeduser")
	public Map<String, Object> getLogedUser(HttpSession httpSession){
		SecurityContext securityContext = (SecurityContext) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
		String username = securityContext.getAuthentication().getName();
		List<String> roles = new ArrayList<>();
		for(GrantedAuthority ga : securityContext.getAuthentication().getAuthorities()){
			roles.add(ga.getAuthority());
		}
		Map<String, Object> params = new HashMap<>();
		params.put("username", username);
		params.put("roles", roles);
		return params;
	}

}
