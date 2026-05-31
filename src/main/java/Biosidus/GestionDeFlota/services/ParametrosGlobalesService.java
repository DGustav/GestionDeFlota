package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.domain.configuracion.ParametroGlobal;
import Biosidus.GestionDeFlota.repositorios.ParametrosGlobalesRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParametrosGlobalesService {
    @Autowired
    private ParametrosGlobalesRepositorio parametrosGlobalesRepositorio;

    @Transactional(readOnly = true)
    public ParametroGlobal obtenerParametroGlobale(String clave) {
        return parametrosGlobalesRepositorio.findById(clave).orElse(null);
    }

    public Integer obtenerValorEntero(String clave, Integer valorPorDefecto) {
        return parametrosGlobalesRepositorio.findById(clave)
                .map(config -> {
                    try {
                        return Integer.parseInt(config.getValor());
                    } catch (NumberFormatException e) {
                        System.err.println("Error: El valor de " + clave + " no es un número válido.");
                        return valorPorDefecto;
                    }
                })
                .orElse(valorPorDefecto);
    }

    @Transactional
    public void guardarOActualizarConfiguracion(String clave, String valor, String descripcion) {
        ParametroGlobal config = parametrosGlobalesRepositorio.findById(clave).orElse(new ParametroGlobal());

        config.setClave(clave);
        config.setValor(valor);

        // Solo actualizamos la descripción si viene un dato nuevo, para no borrarla accidentalmente
        if(descripcion != null && !descripcion.isEmpty()){
            config.setDescripcion(descripcion);
        }

        parametrosGlobalesRepositorio.save(config);
    }
}
