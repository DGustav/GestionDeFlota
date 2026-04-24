package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.empleado.Conductor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConductorRepositorio extends JpaRepository<Conductor, Long> {
    Optional<Conductor> findByDni(String dni);
    Optional<Conductor> findByLegajo(String legajo);

    Page<Conductor> findByNomApeContainingIgnoreCaseOrDniContainingOrLegajoContainingOrUsuarioNicknameContainingIgnoreCaseOrMailContainingIgnoreCase(
            String nomApe,
            String dni,
            String legajo,
            String nickname,
            String mail,
            Pageable pageable
    );

    Page<Conductor> findAll(Pageable pageable);
}
