package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.alerta.Alerta;
import Biosidus.GestionDeFlota.domain.alerta.EstadoAlerta;
import Biosidus.GestionDeFlota.domain.alerta.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertaRepositorio extends JpaRepository<Alerta, Long> {
    public Boolean existsByDocumentoIdAndTipoAndEstado(Long id, TipoAlerta tipo, EstadoAlerta estado);
    public Boolean existsByVehiculoIdAndTipoAndEstado(Long id, TipoAlerta tipo, EstadoAlerta estado);

    public Optional<Alerta> findFirstByDocumentoIdAndEstado(Long id, EstadoAlerta estado);
    public Optional<Alerta> findFirstByVehiculoIdAndTipoAndEstado(Long id, TipoAlerta tipo, EstadoAlerta estado);
}
