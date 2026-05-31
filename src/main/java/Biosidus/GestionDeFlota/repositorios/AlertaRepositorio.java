package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.alerta.Alerta;
import Biosidus.GestionDeFlota.domain.alerta.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepositorio extends JpaRepository<Alerta, Long> {
    public Boolean existsByDocumentoIdAndTipo(Long id, TipoAlerta tipo);
    public Boolean existsByVehiculoIdAndTipo(Long id, TipoAlerta tipo);
}
