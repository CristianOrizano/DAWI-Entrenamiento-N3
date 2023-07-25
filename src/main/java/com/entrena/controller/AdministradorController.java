package com.entrena.controller;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.entrena.entity.Administrador;
import com.entrena.entity.Ciudad;
import com.entrena.entity.Estado;
import com.entrena.service.AdministradorService;
import com.entrena.service.CiudadService;
import com.entrena.utils.Libreria;

import jakarta.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.io.File;

//import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


@Controller
@RequestMapping("/admin")
public class AdministradorController {
	
	@Autowired
	private AdministradorService seradm;
	
	@Autowired
	private CiudadService serCiu;
	

	
	@RequestMapping("/lista")
	public String ListarAdmi(Model model) {
		
		List<Administrador> lista= seradm.listadoActivos(1);
		model.addAttribute("listaAd",lista);
		
		List<Ciudad> listaciu= serCiu.listCiuda();
		Administrador admi= new Administrador();
		
		Estado esta = new Estado();
		esta.setEstado(1);
		admi.setEstado(esta);


		model.addAttribute("admin",admi);
		model.addAttribute("lisCiu",listaciu);
		
		return "Principal2";
	}
	
	//consulta para reporte
	@RequestMapping("/formconsulta")
	public String formconsulta(Model model) {
		
		List<Administrador> lista= new ArrayList<Administrador>();
		model.addAttribute("listaAd",lista);
		
		return "Reporte";
	}
	
	@RequestMapping("/consultaRep")
	public String consultaRe(Model model,@RequestParam("nombre") String nom) {
		
		List<Administrador> lista= seradm.consultaRepo(nom+"%");
		model.addAttribute("listaAd",lista);
		
		return "Reporte";
	}
	
	@RequestMapping("/reporteAdm")
	public void inicio(HttpServletResponse response,@RequestParam("nombre") String nom) {
		try {
			//obtener todos los medicamentos
			List<Administrador> data=seradm.consultaRepo(nom+"%");
			//acceder al reporte "reporte_medicamento"
			File file = ResourceUtils.getFile("classpath:Administrador.jrxml");
			//origen de datos convertir data a tipo JRBeanCollectionDatasource
			JRBeanCollectionDataSource info= new JRBeanCollectionDataSource(data);
			//invocar al metodo generar reporte 
			JasperPrint print= Libreria.generarReporte(file, info);
			//salida en el navegador en formato pdf
			response.setContentType("application/pdf");
			//salida del flujo
			OutputStream salida= response.getOutputStream();
			//exportar a pdf
			JasperExportManager.exportReportToPdfStream(print, salida);
				
		} catch (Exception e) {
			System.out.println("Error:"+e);
			e.printStackTrace();
		}
	}
	
	

	@RequestMapping("/registrar")
	public String registrar(Model model,@ModelAttribute("admin") Administrador adminf,RedirectAttributes redirect){
	
		try {
			/*Estado esta= new Estado();
			esta.setEstado(0);
			admin.setEstado(esta);*/
			int cod=adminf.getCodigoAd();
			seradm.guardar(adminf);
			if(cod==0) {
				
				redirect.addFlashAttribute("MENSAJE","Registro exitoso");
			}else {
				
				redirect.addFlashAttribute("MENSAJE","Actualizado exitoso");
			}
			
		} catch (Exception e) {
			System.out.println("error al grabar"+e.getMessage());
		}

		return "redirect:/admin/lista";
	}
	
	
	 @RequestMapping("/buscar")
	 @ResponseBody
	public Administrador listaadmin(Model model,@RequestParam("cody") int cod) {
		
		Administrador admi= seradm.BuscarAdmin(cod);
         System.out.println("dir==="+admi.getDireccion());
		return admi;
	} 
	
	
	/*@RequestMapping("/actualizar/{id}")
	public String formActu(Model model,@PathVariable(value="id") int cod) {
		
		Administrador admi= seradm.BuscarAdmin(cod);
		
		
		List<Ciudad> lista= serCiu.listCiuda();
		
		model.addAttribute("admin",admi);
		model.addAttribute("lisCiu",lista);
		
		return "PaginaRegistro";
	}  */
	
	@RequestMapping("/eliminar")
	public String formelimi(Model model,@RequestParam("codig") int cod,RedirectAttributes redirect) {
		
		redirect.addFlashAttribute("MENSAJE","Eliminado exitoso");
		int estado=0;
		seradm.eliminar(estado,cod);

		return "redirect:/admin/lista";
	}


}
