package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.domain.Empleados.Usuario;
import Biosidus.GestionDeFlota.domain.Flotas.Modelo;
import Biosidus.GestionDeFlota.service.implementacion.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
public class ModeloController {
    //@Autowired
    private ModeloService service;

    public void crearModelo(String nom, String marca, String ano){
        Modelo modelo = new Modelo();
        modelo.setNombreModelo(nom);
        modelo.setMarca(marca);
        modelo.setAno(ano);
        service.saveModelo(modelo);
    }

    public void eliminarUsuario(Long id){
        service.deleteModelo(id);
    }

    public Modelo buscarPorId(Long id){
        return service.findModeloById(id);
    }

    public List<Modelo> buscarModelos(){
        return service.fetchModeloList();
    }

}
