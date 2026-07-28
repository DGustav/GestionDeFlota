package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.domain.asignacion.EstadoAsignacion;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionRepositorio extends JpaRepository<Asignacion, Long>, JpaSpecificationExecutor<Asignacion> {
    Optional<Asignacion> findFirstByVehiculoIdAndFechaDevolucionIsNullAndEstadoOrderByFechaAsignacionDesc(Long vehiculoId, EstadoAsignacion estadoAsignacion);

    List<Asignacion> findByConductorId(Long conductorId);

    Optional<Asignacion> findFirstByConductorUsuarioNicknameAndEstadoOrderByIdDesc(String nickname, EstadoAsignacion estado);

    Optional<Asignacion> findFirstByVehiculoIdAndEstadoOrderByIdDesc(Long vehiculoId, EstadoAsignacion estado);
}
