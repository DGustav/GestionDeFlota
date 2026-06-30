    package Biosidus.GestionDeFlota.services;


    import Biosidus.GestionDeFlota.domain.alerta.Alerta;
    import Biosidus.GestionDeFlota.domain.alerta.EstadoAlerta;
    import Biosidus.GestionDeFlota.domain.alerta.TipoAlerta;
    import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
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
    import java.time.temporal.ChronoUnit;
    import java.util.Comparator;
    import java.util.List;
    import java.util.Optional;

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

        @Autowired
        private AsignacionService asignacionService;

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
        @Scheduled(cron = "0 0 8 * * ?")
        @Transactional
        public void verificarServicesPeriodicos() {

            List<Vehiculo> vehiculos = vehiculoService.listarVehiculos();// Interesados a los que notificar sobre los documentos por vencer
            List<Conductor> destinatarios = new java.util.ArrayList<>(conductorService.listarConductores().stream().filter(conductor -> conductor.getUsuario().getRol().equals(Rol.ADMINISTRADOR) || conductor.getUsuario().getRol().equals(Rol.OPERARIO)).toList());
            String mensaje = "";

            for (Vehiculo vehiculo : vehiculos) {

                Optional<MantenimientoVehicular> ultimoServiceOpt =
                        mantenimientoVehicularService
                                .ultimoServicePorVehiculoId(
                                        vehiculo.getId());
                Optional<Asignacion> ultimaAsignacionOpt =
                        asignacionService.obtenerUltimaAsignacionActivaPorVehiculo(vehiculo);

                if (ultimoServiceOpt.isEmpty() || ultimaAsignacionOpt.isEmpty()) {
                    continue;
                }

                destinatarios.add(ultimaAsignacionOpt.get().getConductor());

                MantenimientoVehicular ultimoService = ultimoServiceOpt.get();

                long mesesTranscurridos = ChronoUnit.MONTHS.between(
                        ultimoService.getFechaAsignacion().toLocalDate(),
                        LocalDate.now()
                );

                boolean alertaCreada = false;

                System.out.println("--------------------------------");
                System.out.println("Vehiculo: " + vehiculo.getPatente());
                System.out.println("Fecha mantenimiento: " +
                        ultimoService.getFechaAsignacion().toLocalDate());
                System.out.println("Hoy: " + LocalDate.now());
                System.out.println("Meses transcurridos: " + mesesTranscurridos);

                if (mesesTranscurridos >= 12) {
                    alertaCreada = noExisteAlerta(
                            vehiculo,
                            TipoAlerta.SERVICE_VENCIDO
                    );
                    mensaje=String.format(
                            "Vehículo %s. Ha transcurrido 1 año desde el último service realizado el día: %s. Gestione el próximo service a la brevedad",
                            vehiculo.getPatente(),
                            ultimoService.getFechaAsignacion().toLocalDate()
                    );
                    if (alertaCreada){
                        for (Conductor destinatario : destinatarios) {
                            medioDeNotificacion.notificar(destinatario.getMail(), mensaje, "Service Preventivo");
                        }
                        destinatarios.remove(ultimaAsignacionOpt.get().getConductor());
                    }
                }
                else if (mesesTranscurridos >= 9) {
                    alertaCreada = noExisteAlerta(
                            vehiculo,
                            TipoAlerta.SERVICE_9_MESES
                    );
                    mensaje=String.format(
                            "Vehículo %s. Han transcurrido 9 meses desde el último service realizado el %s. El próximo mantenimiento debe realizarse al superar los: %d km o al cumplirse un año, el día: %s. Lo que ocurra primero.",
                            vehiculo.getPatente(),
                            ultimoService.getFechaAsignacion().toLocalDate(),
                            ultimoService.getKilometrajeActual() + 10000,
                            ultimoService.getFechaAsignacion().plusYears(1).toLocalDate()
                    );
                    if (alertaCreada){
                        for (Conductor destinatario : destinatarios) {
                            medioDeNotificacion.notificar(destinatario.getMail(), mensaje, "Service Preventivo");
                        }
                        destinatarios.remove(ultimaAsignacionOpt.get().getConductor());
                    }
                }
                else if (mesesTranscurridos >= 6) {
                    alertaCreada = noExisteAlerta(
                            vehiculo,
                            TipoAlerta.SERVICE_6_MESES
                    );
                    mensaje=String.format(
                            "Vehículo %s. Han transcurrido 6 meses desde el último service realizado el %s. El próximo mantenimiento debe realizarse al superar los: %d km o al cumplirse un año, el día: %s. Lo que ocurra primero.",
                            vehiculo.getPatente(),
                            ultimoService.getFechaAsignacion().toLocalDate(),
                            ultimoService.getKilometrajeActual() + 10000,
                            ultimoService.getFechaAsignacion().plusYears(1).toLocalDate()
                    );
                    if (alertaCreada){
                        for (Conductor destinatario : destinatarios) {
                            medioDeNotificacion.notificar(destinatario.getMail(), mensaje, "Service Preventivo");
                        }
                        destinatarios.remove(ultimaAsignacionOpt.get().getConductor());
                    }
                }
                else if (mesesTranscurridos >= 3) {
                    alertaCreada = noExisteAlerta(
                            vehiculo,
                            TipoAlerta.SERVICE_3_MESES
                    );
                    mensaje=String.format(
                            "Vehículo %s. Han transcurrido 3 meses desde el último service realizado el %s. El próximo mantenimiento debe realizarse al superar los: %d km o al cumplirse un año, el día: %s. Lo que ocurra primero.",
                            vehiculo.getPatente(),
                            ultimoService.getFechaAsignacion().toLocalDate(),
                            ultimoService.getKilometrajeActual() + 10000,
                            ultimoService.getFechaAsignacion().plusYears(1).toLocalDate()
                    );
                    if (alertaCreada){
                        for (Conductor destinatario : destinatarios) {
                            medioDeNotificacion.notificar(destinatario.getMail(), mensaje, "Service Preventivo");
                        }
                        destinatarios.remove(ultimaAsignacionOpt.get().getConductor());
                    }
                }

            }
        }

        private boolean noExisteAlerta(
                Vehiculo vehiculo,
                TipoAlerta tipo) {

            boolean existe = alertaService.existeAlertaPorVehiculoYtipo
                    (
                            vehiculo.getId(),
                            tipo,
                            EstadoAlerta.ACTIVA
                    );

            if (existe) {
                return false;
            }

            Alerta alerta = new Alerta();

            alerta.setVehiculo(vehiculo);
            alerta.setTipo(tipo);
            alerta.setEstado(EstadoAlerta.ACTIVA);
            alerta.setFechaEnvio(LocalDateTime.now());


            switch (tipo) {

                case SERVICE_3_MESES ->
                        alerta.setDescripcion(
                                "Han transcurrido 3 meses desde el último service."
                        );

                case SERVICE_6_MESES ->
                        alerta.setDescripcion(
                                "Han transcurrido 6 meses desde el último service."
                        );

                case SERVICE_9_MESES ->
                        alerta.setDescripcion(
                                "Han transcurrido 9 meses desde el último service."
                        );

                case SERVICE_VENCIDO ->
                        alerta.setDescripcion(
                                "El service anual se encuentra vencido."
                        );
            }

            alertaService.guardarAlerta(alerta);
            return true;
        }
    }
