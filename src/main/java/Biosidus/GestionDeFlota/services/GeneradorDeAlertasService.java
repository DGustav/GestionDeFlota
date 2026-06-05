    package Biosidus.GestionDeFlota.services;


    import Biosidus.GestionDeFlota.domain.alerta.Alerta;
    import Biosidus.GestionDeFlota.domain.alerta.EstadoAlerta;
    import Biosidus.GestionDeFlota.domain.alerta.TipoAlerta;
    import Biosidus.GestionDeFlota.domain.documento.Documento;
    import Biosidus.GestionDeFlota.domain.empleado.Conductor;
    import Biosidus.GestionDeFlota.domain.empleado.Rol;
    import Biosidus.GestionDeFlota.domain.mantenimientoVehicular.MantenimientoVehicular;
    import Biosidus.GestionDeFlota.domain.medioDeNotificacion.MedioDeNotificacion;
    import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
    import Biosidus.GestionDeFlota.repositorios.DocumentoRepositorio;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.scheduling.annotation.Scheduled;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.Comparator;
    import java.util.List;

    @Service
    public class GeneradorDeAlertasService {
        @Autowired
        private ParametrosGlobalesService parametrosGlobalesService;

        @Autowired
        private DocumentoService documentoService;

        @Autowired
        private AlertaService alertaService;

        @Autowired
        private MedioDeNotificacion medioDeNotificacion;

        @Autowired
        private ConductorService conductorService;

        @Autowired
        private MantenimientoVehicularService mantenimientoVehicularService;

        @Autowired
        private VehiculoService vehiculoService;

        @Scheduled(cron = "0 0 23 * * ?")
        @Transactional
        public void generarAlertasPorDocumentosVencidos(){
            // Dato de cuántos días de anticipación de vencimiento se quiere para generar una alerta para los documentos
            Integer diasAnticipacion = parametrosGlobalesService.obtenerValorEntero("DIAS_ALERTA_VENCIMIENTO", 30);

            // Calculamos la fecha límite (Hoy + 30 días)
            LocalDate fechaLimite = LocalDate.now().plusDays(diasAnticipacion);

            // Buscamos todos los documentos cuya fecha de vencimiento sea anterior o igual a la límite
            List<Documento> documentosPorVencer = documentoService.buscarDocumentosAntesDeFecha(fechaLimite);

            // Interesados a los que notificar sobre los documentos por vencer
            List<Conductor> administradorOperadoresConductor = new java.util.ArrayList<>(conductorService.listarConductores().stream().filter(conductor -> conductor.getUsuario().getRol().equals(Rol.ADMINISTRADOR) || conductor.getUsuario().getRol().equals(Rol.OPERARIO)).toList());

            // Analizamos los documentos encontrados
            for (Documento doc : documentosPorVencer) {

                // Verificamos si YA enviamos una alerta para este documento puntual
                boolean alertaYaEnviada = alertaService.existeAlertaPorDocumentoYtipo(doc.getId(), TipoAlerta.VENCIMIENTO_DE_DOCUMENTO, EstadoAlerta.ACTIVA);

                if (!alertaYaEnviada) {
                    // a. Armamos el mensaje
                    String msj;

                    if (doc.getVehiculo() != null){
                        msj = String.format(
                                "ALERTA DEL SISTEMA: El documento '%s' correspondiente al vehículo con patente %s vencerá el %s. " +
                                        "Por favor, gestione su renovación a la brevedad.",
                                doc.getTipoDocumento().toString(),
                                doc.getVehiculo().getPatente(),
                                doc.getFechaVencimiento().toString()
                        );
                        doc.getVehiculo().setDisponible(false);
                        //  Disparamos el correo
                        administradorOperadoresConductor.forEach(conductor -> {
                            medioDeNotificacion.notificar(conductor.getMail(), msj, "Sistema de Gestión de Flota - Notificación por Documento de vehiculo por vencer");
                        });
                    }else if (doc.getConductor() != null){
                        administradorOperadoresConductor.add(doc.getConductor());
                        msj = String.format(
                                "ALERTA DEL SISTEMA: El documento de '%s' correspondiente al conductor con LEGAJO: %s vencerá el %s. " +
                                        "Por favor, gestione su renovación a la brevedad.",
                                doc.getTipoDocumento().toString(),
                                doc.getConductor().getLegajo(),
                                doc.getFechaVencimiento().toString()
                        );
                        //  Disparamos el correo
                        administradorOperadoresConductor.forEach(conductor -> {
                            medioDeNotificacion.notificar(conductor.getMail(), msj, "Sistema de Gestión de Flota - Notificación por Registro por vencer");
                        });
                        administradorOperadoresConductor.remove(doc.getConductor());
                    }


                    // Guardamos el registro en el historial para no volver a notificarlo mañana
                    Alerta nuevaAlerta = new Alerta();
                    nuevaAlerta.setDescripcion("Aviso de vencimiento enviado: " + doc.getTipoDocumento().toString());
                    nuevaAlerta.setTipo(TipoAlerta.VENCIMIENTO_DE_DOCUMENTO);
                    nuevaAlerta.setFechaEnvio(LocalDateTime.now());
                    nuevaAlerta.setDocumento(doc);
                    nuevaAlerta.setEstado(EstadoAlerta.ACTIVA);
                    nuevaAlerta.setVehiculo(doc.getVehiculo());

                    alertaService.guardarAlerta(nuevaAlerta);

                    System.out.println("Alerta generada y registrada para el documento ID: " + doc.getId());
                }
            }

        }
        @Scheduled(cron = "0 0 21 * * ?")
        @Transactional
        public void generardorDeAlertasPorKilometrajeSuperado(){
            List<Conductor> administradorOperadoresConductor = new java.util.ArrayList<>(conductorService.listarConductores().stream().filter(conductor -> conductor.getUsuario().getRol().equals(Rol.ADMINISTRADOR) || conductor.getUsuario().getRol().equals(Rol.OPERARIO)).toList());

            Integer kmLimite =
                    parametrosGlobalesService.obtenerValorEntero(
                            "KILOMETRAJE_SUPERADO",
                            10000
                    );

            List<Vehiculo> vehiculos =
                    vehiculoService.listarVehiculos();
            for (Vehiculo vehiculo : vehiculos) {

                Integer kmActual =
                        vehiculo.getKilometrajeActual();

                Integer kmUltimoService =
                        mantenimientoVehicularService.listarMantenimientosPorVehiculo(vehiculo).stream()
                                .max(Comparator.comparing(MantenimientoVehicular::getFechaAsignacion))
                                .map(MantenimientoVehicular::getKilometrajeActual)
                                .orElse(0);;

                if (
                        kmActual - kmUltimoService >= kmLimite
                ) {

                    boolean alertaExistente =
                            alertaService.existeAlertaPorVehiculoYtipo(
                                    vehiculo.getId(),
                                    TipoAlerta.KILOMETRAJE_SUPERADO,
                                    EstadoAlerta.ACTIVA
                            );

                    if (!alertaExistente) {

                        Alerta alerta = new Alerta();

                        alerta.setVehiculo(vehiculo);

                        alerta.setTipo(TipoAlerta.KILOMETRAJE_SUPERADO);

                        alerta.setEstado(EstadoAlerta.ACTIVA);

                        alerta.setFechaEnvio(LocalDateTime.now());

                        alerta.setDescripcion(
                                STR."El vehículo \{vehiculo.getPatente()} superó el límite de \{kmLimite} km sin mantenimiento."
                        );

                        alertaService.guardarAlerta(alerta);

                        administradorOperadoresConductor.forEach(conductor -> {
                            medioDeNotificacion.notificar(conductor.getMail(), STR."El vehículo \{vehiculo.getPatente()} superó el límite de \{kmLimite} km sin mantenimiento.", "Sistema de Gestión de Flota - Notificación por Kilometraje Superado");
                        });
                    }
                }
            }
        }
    }
