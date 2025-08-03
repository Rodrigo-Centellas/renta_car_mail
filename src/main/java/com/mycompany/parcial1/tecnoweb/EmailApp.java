package com.mycompany.parcial1.tecnoweb;

import data.*;
import interfaces.ICasoUsoListener;
import interfaces.IEmailListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import librerias.Email;
import librerias.HtmlRes;
import librerias.Interpreter;
import librerias.ParamsAction;
import librerias.analex.Token;
import negocio.*;
import postgresConecction.EmailReceipt;
import postgresConecction.EmailSend;

public class EmailApp implements ICasoUsoListener, IEmailListener {

    private static final int CONSTRAINTS_ERROR = -2;
    private static final int NUMBER_FORMAT_ERROR = -3;
    private static final int INDEX_OUT_OF_BOUND_ERROR = -4;
    private static final int PARSE_ERROR = -5;
    private static final int AUTHORIZATION_ERROR = -6;

    private NMantenimiento nMantenimiento;
    private NFrecuenciaPago nFrecuenciaPago;
    private NNroCuenta nNroCuenta;
    private NRole nRole;
    private NClausula nClausula;
    private NUser nUser;
    private NVehiculo nVehiculo;
    private NGarante nGarante;
    private NVehiculoMantenimiento nVehiculoMantenimiento;
    private EmailReceipt emailReceipt;
    private NContratoClausula nContratoClausula;
    private final NReporteIngresos nReporte;
    private NNotificacion nNotificacion;
    private NContrato nContrato;
    private NContratoPago nContratoPago;
    private NUserContrato nUserContrato;
    private NReserva nReserva;
    private NPago nPago;
    private NUserHasRole nUserHasRole;




    public EmailApp() {
        this.emailReceipt = new EmailReceipt();
        this.emailReceipt.setEmailListener(this);
        this.nUser = new NUser();
        this.nMantenimiento = new  NMantenimiento();
        this.nFrecuenciaPago = new NFrecuenciaPago();
        this.nNroCuenta = new  NNroCuenta();
        this.nRole = new NRole();
        this.nClausula = new NClausula();
        this.nVehiculo = new NVehiculo();
        this.nGarante = new NGarante();
        this.nVehiculoMantenimiento = new NVehiculoMantenimiento();
        this.nContratoClausula = new NContratoClausula();

        this.nNotificacion = new NNotificacion();
        this.nContrato = new NContrato();
        this.nContratoPago = new NContratoPago();
        this.nUserContrato = new NUserContrato();
        this.nReserva = new NReserva();
        this.nPago = new NPago();
        this.nReporte = new NReporteIngresos();
        this.nUserHasRole = new NUserHasRole();
    }

    public void start() {
        Thread thread = new Thread(emailReceipt);
        thread.setName("Mail Receipt");
        thread.start();
    }


    @Override
    public void userHasRole(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    List<String[]> created = nUserHasRole.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "UserHasRole creado correctamente",
                            DUserHasRole.HEADERS,
                            (ArrayList<String[]>) created,
                            event.getCommand()
                    );
                    break;

                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Get by ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> single = nUserHasRole.get(id);
                        tableNotifySuccess(
                                event.getSender(),
                                "Detalle de UserHasRole",
                                DUserHasRole.HEADERS,
                                (ArrayList<String[]>) single,
                                event.getCommand()
                        );
                    } else {
                        // List all
                        List<String[]> all = nUserHasRole.list();
                        tableNotifySuccess(
                                event.getSender(),
                                "Lista de UserHasRole",
                                DUserHasRole.HEADERS,
                                (ArrayList<String[]>) all,
                                event.getCommand()
                        );
                    }
                    break;

                case Token.MODIFY:
                    List<String[]> updated = nUserHasRole.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "UserHasRole actualizado correctamente",
                            DUserHasRole.HEADERS,
                            (ArrayList<String[]>) updated,
                            event.getCommand()
                    );
                    break;

                case Token.DELETE:
                    List<String[]> remaining = nUserHasRole.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "UserHasRole eliminado correctamente",
                            DUserHasRole.HEADERS,
                            (ArrayList<String[]>) remaining,
                            event.getCommand()
                    );
                    break;

                default:
                    simpleNotify(event.getSender(), "Error", "Acción no válida para userHasRole.");
                    break;
            }
        } catch (Exception ex) {
            handleError(
                    CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error al procesar userHasRole: " + ex.getMessage())
            );
        }
    }
    @Override
    public void reporte(ParamsAction event) {
        try {
            // primer parámetro: tipo de reporte
            List<String> p = event.getParams();
            String tipo = p.get(0).toLowerCase();

            switch (tipo) {
                case "ingresos":
                    // esperamos: ingresos(YYYY-MM-DD,YYYY-MM-DD)
                    List<String[]> data = nReporte.ingresos(p.subList(1, 3));
                    tableNotifySuccess(
                            event.getSender(),
                            "Reporte de Ingresos",
                            DReporteIngresos.HEADERS,
                            (ArrayList<String[]>) data,
                            event.getCommand()
                    );
                    break;

                default:
                    simpleNotify(
                            event.getSender(),
                            "Error",
                            "Tipo de reporte desconocido: " + tipo
                    );
            }
        } catch (Exception ex) {
            handleError(
                    CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error al generar reporte: " + ex.getMessage())
            );
        }
    }

    @Override
    public void pago(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    List<String[]> created = nPago.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Pago creado correctamente",
                            DPago.HEADERS,
                            (ArrayList<String[]>) created,
                            event.getCommand()
                    );
                    break;

                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> single = nPago.get(id);
                        tableNotifySuccess(
                                event.getSender(),
                                "Detalle del Pago",
                                DPago.HEADERS,
                                (ArrayList<String[]>) single,
                                event.getCommand()
                        );
                    } else {
                        List<String[]> all = nPago.list();
                        tableNotifySuccess(
                                event.getSender(),
                                "Lista de Pagos",
                                DPago.HEADERS,
                                (ArrayList<String[]>) all,
                                event.getCommand()
                        );
                    }
                    break;

                case Token.MODIFY:
                    List<String[]> updated = nPago.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Pago actualizado correctamente",
                            DPago.HEADERS,
                            (ArrayList<String[]>) updated,
                            event.getCommand()
                    );
                    break;

                case Token.DELETE:
                    List<String[]> remaining = nPago.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Pago eliminado correctamente",
                            DPago.HEADERS,
                            (ArrayList<String[]>) remaining,
                            event.getCommand()
                    );
                    break;

                default:
                    simpleNotify(event.getSender(), "Error", "Acción no válida para pago.");
            }
        } catch (Exception ex) {
            handleError(
                    CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error al procesar pago: " + ex.getMessage())
            );
        }
    }
    @Override
    public void reserva(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // Create
                    List<String[]> created = nReserva.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Reserva creada correctamente",
                            DReserva.HEADERS,
                            (ArrayList<String[]>) created,
                            event.getCommand()
                    );
                    break;

                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Get by ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> single = nReserva.get(id);
                        tableNotifySuccess(
                                event.getSender(),
                                "Detalle de Reserva",
                                DReserva.HEADERS,
                                (ArrayList<String[]>) single,
                                event.getCommand()
                        );
                    } else {
                        // List all
                        List<String[]> all = nReserva.list();
                        tableNotifySuccess(
                                event.getSender(),
                                "Lista de Reservas",
                                DReserva.HEADERS,
                                (ArrayList<String[]>) all,
                                event.getCommand()
                        );
                    }
                    break;

                case Token.MODIFY:
                    // Update
                    List<String[]> updated = nReserva.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Reserva actualizada correctamente",
                            DReserva.HEADERS,
                            (ArrayList<String[]>) updated,
                            event.getCommand()
                    );
                    break;

                case Token.DELETE:
                    // Delete
                    List<String[]> remaining = nReserva.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Reserva eliminada correctamente",
                            DReserva.HEADERS,
                            (ArrayList<String[]>) remaining,
                            event.getCommand()
                    );
                    break;

                default:
                    simpleNotify(event.getSender(), "Error", "Acción no válida para reserva.");
                    break;
            }
        } catch (Exception ex) {
            handleError(
                    CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error al procesar reserva: " + ex.getMessage())
            );
        }
    }

    @Override
    public void userContrato(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    List<String[]> created = nUserContrato.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "UserContrato creado",
                            DUserContrato.HEADERS,
                            (ArrayList<String[]>) created,
                            event.getCommand()
                    );
                    break;
                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        tableNotifySuccess(
                                event.getSender(),
                                "Detalle UserContrato",
                                DUserContrato.HEADERS,
                                (ArrayList<String[]>) nUserContrato.get(id),
                                event.getCommand()
                        );
                    } else {
                        tableNotifySuccess(
                                event.getSender(),
                                "Lista UserContrato",
                                DUserContrato.HEADERS,
                                nUserContrato.list(),
                                event.getCommand()
                        );
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> updated = nUserContrato.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "UserContrato actualizado",
                            DUserContrato.HEADERS,
                            (ArrayList<String[]>) updated,
                            event.getCommand()
                    );
                    break;
                case Token.DELETE:
                    List<String[]> remaining = nUserContrato.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "UserContrato eliminado",
                            DUserContrato.HEADERS,
                            (ArrayList<String[]>) remaining,
                            event.getCommand()
                    );
                    break;
            }
        } catch (Exception ex) {
            handleError(
                    CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage())
            );
        }
    }


    @Override
    public void contratoPago(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    List<String[]> saved = nContratoPago.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "ContratoPago creada",
                            DContratoPago.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand()
                    );
                    break;
                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        tableNotifySuccess(
                                event.getSender(),
                                "Detalle ContratoPago",
                                DContratoPago.HEADERS,
                                (ArrayList<String[]>) nContratoPago.get(id),
                                event.getCommand()
                        );
                    } else {
                        tableNotifySuccess(
                                event.getSender(),
                                "Lista ContratoPago",
                                DContratoPago.HEADERS,
                                nContratoPago.list(),
                                event.getCommand()
                        );
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> upd = nContratoPago.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "ContratoPago actualizada",
                            DContratoPago.HEADERS,
                            (ArrayList<String[]>) upd,
                            event.getCommand()
                    );
                    break;
                case Token.DELETE:
                    List<String[]> rem = nContratoPago.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "ContratoPago eliminada",
                            DContratoPago.HEADERS,
                            (ArrayList<String[]>) rem,
                            event.getCommand()
                    );
                    break;
            }
        } catch (Exception ex) {
            handleError(
                    CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage())
            );
        }
    }

    @Override
    public void contrato(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // Crear un nuevo contrato
                    List<String[]> created = nContrato.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Contrato creado correctamente",
                            DContrato.HEADERS,
                            (ArrayList<String[]>) created,
                            event.getCommand()
                    );
                    break;

                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Obtener contrato por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> single = nContrato.get(id);
                        tableNotifySuccess(
                                event.getSender(),
                                "Detalle del Contrato",
                                DContrato.HEADERS,
                                (ArrayList<String[]>) single,
                                event.getCommand()
                        );
                    } else {
                        // Listar todos los contratos
                        List<String[]> all = nContrato.list();
                        tableNotifySuccess(
                                event.getSender(),
                                "Lista de Contratos",
                                DContrato.HEADERS,
                                (ArrayList<String[]>) all,
                                event.getCommand()
                        );
                    }
                    break;

                case Token.MODIFY:
                    // Modificar un contrato existente
                    List<String[]> updated = nContrato.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Contrato actualizado correctamente",
                            DContrato.HEADERS,
                            (ArrayList<String[]>) updated,
                            event.getCommand()
                    );
                    break;

                case Token.DELETE:
                    // Eliminar un contrato
                    List<String[]> remaining = nContrato.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Contrato eliminado correctamente",
                            DContrato.HEADERS,
                            (ArrayList<String[]>) remaining,
                            event.getCommand()
                    );
                    break;

                default:
                    // Acción no reconocida
                    simpleNotify(event.getSender(), "Error", "Acción no válida para contrato.");
                    break;
            }
        } catch (Exception ex) {
            // Manejo genérico de errores (constraint violations, SQL errors, etc.)
            handleError(
                    CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error al procesar contrato: " + ex.getMessage())
            );
        }
    }

    @Override
    public void notificacion(ParamsAction event) {
        try {
            switch(event.getAction()) {
                case Token.ADD:
                    List<String[]> saved = nNotificacion.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Notificación creada",
                            DNotificacion.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand()
                    );
                    break;
                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        tableNotifySuccess(
                                event.getSender(),
                                "Detalle Notificación",
                                DNotificacion.HEADERS,
                                (ArrayList<String[]>) nNotificacion.get(id),
                                event.getCommand()
                        );
                    } else {
                        tableNotifySuccess(
                                event.getSender(),
                                "Lista Notificaciones",
                                DNotificacion.HEADERS,
                                nNotificacion.list(),
                                event.getCommand()
                        );
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> upd = nNotificacion.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Notificación actualizada",
                            DNotificacion.HEADERS,
                            (ArrayList<String[]>) upd,
                            event.getCommand()
                    );
                    break;
                case Token.DELETE:
                    List<String[]> rem = nNotificacion.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Notificación eliminada",
                            DNotificacion.HEADERS,
                            (ArrayList<String[]>) rem,
                            event.getCommand()
                    );
                    break;
            }
        } catch(Exception ex) {
            handleError(
                    CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage())
            );
        }
    }



    @Override
    public void contratoClausula(ParamsAction event) {
        try {
            switch(event.getAction()) {
                case Token.ADD:
                    List<String[]> saved = nContratoClausula.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Contrato_Clausula creada",
                            DContratoClausula.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand()
                    );
                    break;
                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        tableNotifySuccess(
                                event.getSender(),
                                "Detalle Contrato_Clausula",
                                DContratoClausula.HEADERS,
                                (ArrayList<String[]>) nContratoClausula.get(id),
                                event.getCommand()
                        );
                    } else {
                        tableNotifySuccess(
                                event.getSender(),
                                "Lista Contrato_Clausula",
                                DContratoClausula.HEADERS,
                                nContratoClausula.list(),
                                event.getCommand()
                        );
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> upd = nContratoClausula.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Contrato_Clausula actualizada",
                            DContratoClausula.HEADERS,
                            (ArrayList<String[]>) upd,
                            event.getCommand()
                    );
                    break;
                case Token.DELETE:
                    List<String[]> rem = nContratoClausula.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Contrato_Clausula eliminada",
                            DContratoClausula.HEADERS,
                            (ArrayList<String[]>) rem,
                            event.getCommand()
                    );
                    break;
            }
        } catch (Exception ex) {
            handleError(CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage()));
        }
    }

    @Override
    public void vehiculoMantenimiento(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    List<String[]> saved = nVehiculoMantenimiento.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "VehículoMantenimiento creado",
                            DVehiculoMantenimiento.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand()
                    );
                    break;
                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        tableNotifySuccess(
                                event.getSender(),
                                "Detalle VehiculoMantenimiento",
                                DVehiculoMantenimiento.HEADERS,
                                (ArrayList<String[]>) nVehiculoMantenimiento.get(id),
                                event.getCommand()
                        );
                    } else {
                        tableNotifySuccess(
                                event.getSender(),
                                "Lista VehiculoMantenimientos",
                                DVehiculoMantenimiento.HEADERS,
                                nVehiculoMantenimiento.list(),
                                event.getCommand()
                        );
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> upd = nVehiculoMantenimiento.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "VehículoMantenimiento actualizado",
                            DVehiculoMantenimiento.HEADERS,
                            (ArrayList<String[]>) upd,
                            event.getCommand()
                    );
                    break;
                case Token.DELETE:
                    List<String[]> rem = nVehiculoMantenimiento.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "VehículoMantenimiento eliminado",
                            DVehiculoMantenimiento.HEADERS,
                            (ArrayList<String[]>) rem,
                            event.getCommand()
                    );
                    break;
            }
        } catch (Exception ex) {
            handleError(
                    CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage())
            );
        }
    }

    @Override
    public void garante(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    List<String[]> saved = nGarante.save(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "Garante creado correctamente",
                            DGarante.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand());
                    break;
                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        tableNotifySuccess(event.getSender(),
                                "Detalle Garante",
                                DGarante.HEADERS,
                                (ArrayList<String[]>) nGarante.get(id),
                                event.getCommand());
                    } else {
                        tableNotifySuccess(event.getSender(),
                                "Lista de Garantes",
                                DGarante.HEADERS,
                                nGarante.list(),
                                event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> upd = nGarante.update(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "Garante actualizado correctamente",
                            DGarante.HEADERS,
                            (ArrayList<String[]>) upd,
                            event.getCommand());
                    break;
                case Token.DELETE:
                    List<String[]> rem = nGarante.delete(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "Garante eliminado correctamente",
                            DGarante.HEADERS,
                            (ArrayList<String[]>) rem,
                            event.getCommand());
                    break;
            }
        } catch (Exception ex) {
            handleError(CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage()));
        }
    }

    @Override
    public void user(ParamsAction event) {
        try {
            switch(event.getAction()) {
                case Token.ADD:
                    List<String[]> saved = nUser.save(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "Usuario creado",
                            DUser.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand());
                    break;
                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        tableNotifySuccess(event.getSender(),
                                "Detalle Usuario",
                                DUser.HEADERS,
                                (ArrayList<String[]>) nUser.get(id),
                                event.getCommand());
                    } else {
                        tableNotifySuccess(event.getSender(),
                                "Lista Usuarios",
                                DUser.HEADERS,
                                nUser.list(),
                                event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> upd = nUser.update(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "Usuario actualizado",
                            DUser.HEADERS,
                            (ArrayList<String[]>) upd,
                            event.getCommand());
                    break;
                case Token.DELETE:
                    List<String[]> rem = nUser.delete(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "Usuario eliminado",
                            DUser.HEADERS,
                            (ArrayList<String[]>) rem,
                            event.getCommand());
                    break;
            }
        } catch (Exception ex) {
            handleError(CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage()));
        }
    }

    @Override
    public void clausula(ParamsAction event) {
        try {
            switch(event.getAction()) {
                case Token.ADD:
                    List<String[]> saved = nClausula.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(), "Clausula creada",
                            DClausula.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand()
                    );
                    break;
                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        tableNotifySuccess(
                                event.getSender(), "Detalle Clausula",
                                DClausula.HEADERS,
                                (ArrayList<String[]>) nClausula.get(id),
                                event.getCommand()
                        );
                    } else {
                        tableNotifySuccess(
                                event.getSender(), "Lista Clausulas",
                                DClausula.HEADERS,
                                nClausula.list(),
                                event.getCommand()
                        );
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> upd = nClausula.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(), "Clausula actualizada",
                            DClausula.HEADERS,
                            (ArrayList<String[]>) upd,
                            event.getCommand()
                    );
                    break;
                case Token.DELETE:
                    List<String[]> rem = nClausula.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(), "Clausula eliminada",
                            DClausula.HEADERS,
                            (ArrayList<String[]>) rem,
                            event.getCommand()
                    );
                    break;
            }
        } catch (Exception ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage()));
        }
    }

    @Override
    public void vehiculo(ParamsAction event) {
        try {
            switch(event.getAction()) {
                case Token.ADD:
                    List<String[]> saved = nVehiculo.save(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "Vehículo creado",
                            DVehiculo.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand());
                    break;
                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        tableNotifySuccess(event.getSender(),
                                "Detalle Vehículo",
                                DVehiculo.HEADERS,
                                (ArrayList<String[]>) nVehiculo.get(id),
                                event.getCommand());
                    } else {
                        tableNotifySuccess(event.getSender(),
                                "Lista Vehículos",
                                DVehiculo.HEADERS,
                                nVehiculo.list(),
                                event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> upd = nVehiculo.update(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "Vehículo actualizado",
                            DVehiculo.HEADERS,
                            (ArrayList<String[]>) upd,
                            event.getCommand());
                    break;
                case Token.DELETE:
                    List<String[]> rem = nVehiculo.delete(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "Vehículo eliminado",
                            DVehiculo.HEADERS,
                            (ArrayList<String[]>) rem,
                            event.getCommand());
                    break;
            }
        } catch (Exception ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage()));
        }
    }

    @Override
    public void nroCuenta(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    List<String[]> saved = nNroCuenta.save(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "NroCuenta creada",
                            DNroCuenta.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand());
                    break;
                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        tableNotifySuccess(event.getSender(),
                                "Detalle NroCuenta",
                                DNroCuenta.HEADERS,
                                (ArrayList<String[]>) nNroCuenta.get(id),
                                event.getCommand());
                    } else {
                        tableNotifySuccess(event.getSender(),
                                "Lista NroCuenta",
                                DNroCuenta.HEADERS,
                                nNroCuenta.list(),
                                event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> upd = nNroCuenta.update(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "NroCuenta actualizada",
                            DNroCuenta.HEADERS,
                            (ArrayList<String[]>) upd,
                            event.getCommand());
                    break;
                case Token.DELETE:
                    List<String[]> rem = nNroCuenta.delete(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "NroCuenta eliminada",
                            DNroCuenta.HEADERS,
                            (ArrayList<String[]>) rem,
                            event.getCommand());
                    break;
            }
        } catch (Exception ex) {
            handleError(CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage()));
        }
    }

    @Override
    public void frecuenciaPago(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:                   
                    List<String[]> saved = nFrecuenciaPago.save(event.getParams());
                    tableNotifySuccess(event.getSender(),
                            "FrecuenciaPago creada",
                            DFrecuenciaPago.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand());
                    break;
                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> one = nFrecuenciaPago.get(id);
                        tableNotifySuccess(event.getSender(), "Detalle FrecPago",
                                DFrecuenciaPago.HEADERS,
                                (ArrayList<String[]>) one,
                                event.getCommand());
                    } else {
                        tableNotifySuccess(event.getSender(), "Lista FrecuenciasPago",
                                DFrecuenciaPago.HEADERS,
                                nFrecuenciaPago.list(),
                                event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> updated = nFrecuenciaPago.update(event.getParams());
                    tableNotifySuccess(event.getSender(), "FrecuenciaPago actualizada",
                            DFrecuenciaPago.HEADERS,
                            (ArrayList<String[]>) updated,
                            event.getCommand());
                    break;
                case Token.DELETE:
                    List<String[]> remaining = nFrecuenciaPago.delete(event.getParams());
                    tableNotifySuccess(event.getSender(), "FrecuenciaPago eliminada",
                            DFrecuenciaPago.HEADERS,
                            (ArrayList<String[]>) remaining,
                            event.getCommand());
                    break;
            }
        } catch (Exception ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage()));
        }
    }

    @Override
    public void rol(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // crea un rol nuevo
                    List<String[]> saved = nRole.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Rol creado correctamente",
                            DRole.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand()
                    );
                    break;

                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // get por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> single = nRole.get(id);
                        tableNotifySuccess(
                                event.getSender(),
                                "Detalle de Rol",
                                DRole.HEADERS,
                                (ArrayList<String[]>) single,
                                event.getCommand()
                        );
                    } else {
                        // list all
                        tableNotifySuccess(
                                event.getSender(),
                                "Lista de Roles",
                                DRole.HEADERS,
                                nRole.list(),
                                event.getCommand()
                        );
                    }
                    break;

                case Token.MODIFY:
                    List<String[]> updated = nRole.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Rol actualizado correctamente",
                            DRole.HEADERS,
                            (ArrayList<String[]>) updated,
                            event.getCommand()
                    );
                    break;

                case Token.DELETE:
                    List<String[]> remaining = nRole.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Rol eliminado correctamente",
                            DRole.HEADERS,
                            (ArrayList<String[]>) remaining,
                            event.getCommand()
                    );
                    break;
            }
        } catch (Exception ex) {
            handleError(
                    CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage())
            );
        }
    }

    @Override
    public void mantenimiento(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    List<String[]> saved = nMantenimiento.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Mantenimiento guardado correctamente",
                            DMantenimiento.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand());
                    break;
                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> item = nMantenimiento.get(id);
                        tableNotifySuccess(
                                event.getSender(),
                                "Detalle Mantenimiento",
                                DMantenimiento.HEADERS,
                                (ArrayList<String[]>) item,
                                event.getCommand());
                    } else {
                        tableNotifySuccess(
                                event.getSender(),
                                "Lista Mantenimientos",
                                DMantenimiento.HEADERS,
                                (ArrayList<String[]>) nMantenimiento.list(),
                                event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> updated = nMantenimiento.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Mantenimiento actualizado correctamente",
                            DMantenimiento.HEADERS,
                            (ArrayList<String[]>) updated,
                            event.getCommand());
                    break;
                case Token.DELETE:
                    List<String[]> remaining = nMantenimiento.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Mantenimiento eliminado correctamente",
                            DMantenimiento.HEADERS,
                            (ArrayList<String[]>) remaining,
                            event.getCommand());
                    break;
            }
        } catch (Exception ex) {
            handleError(
                    CONSTRAINTS_ERROR,
                    event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage()));
        }
    }


    @Override
    public void error(ParamsAction event) {
        handleError(event.getAction(), event.getSender(), event.getParams());
    }

    @Override
    public void help(ParamsAction event) {
        try {
            // Permitimos tanto "help" como "help get"
            if (event.getAction() == 0 || event.getAction() == Token.GET) {
                String[] headers = {"Entidad", "Comando"};
                ArrayList<String[]> data = new ArrayList<>();

                /* ═══════════════════════════ TABLAS BÁSICAS ═══════════════════════════ */

                // 🎯 ROL
                data.add(new String[]{"ROL", "rol get() → Lista todos"});
                data.add(new String[]{"ROL", "rol get(id) → Obtiene por ID"});
                data.add(new String[]{"ROL", "rol add(nombre)"});
                data.add(new String[]{"ROL", "rol modify(id,nombre)"});
                data.add(new String[]{"ROL", "rol delete(id)"});

                // 👤 USER
                data.add(new String[]{"USER", "user get() → Lista todos"});
                data.add(new String[]{"USER", "user get(id) → Obtiene por ID"});
                data.add(new String[]{"USER", "user add(apellido,ci,domicilio,nombre,telefono,email,password,doc_frontal_path,doc_trasero_path)"});
                data.add(new String[]{"USER", "user modify(id,apellido,ci,domicilio,nombre,telefono,email,password,doc_frontal_path,doc_trasero_path,verificado)"});
                data.add(new String[]{"USER", "user delete(id)"});

                // 🚗 VEHÍCULO
                data.add(new String[]{"VEHICULO", "vehiculo get() → Lista todos"});
                data.add(new String[]{"VEHICULO", "vehiculo get(id) → Obtiene por ID"});
                data.add(new String[]{"VEHICULO", "vehiculo add(estado,marca,modelo,monto_garantia,placa,precio_dia,tipo,url_imagen)"});
                data.add(new String[]{"VEHICULO", "vehiculo modify(id,estado,marca,modelo,monto_garantia,placa,precio_dia,tipo,url_imagen)"});
                data.add(new String[]{"VEHICULO", "vehiculo delete(id)"});

                // 🔧 MANTENIMIENTO
                data.add(new String[]{"MANTENIMIENTO", "mantenimiento get() → Lista todos"});
                data.add(new String[]{"MANTENIMIENTO", "mantenimiento get(id) → Obtiene por ID"});
                data.add(new String[]{"MANTENIMIENTO", "mantenimiento add(descripcion,nombre)"});
                data.add(new String[]{"MANTENIMIENTO", "mantenimiento modify(id,descripcion,nombre)"});
                data.add(new String[]{"MANTENIMIENTO", "mantenimiento delete(id)"});

                // 💰 FRECUENCIA PAGO
                data.add(new String[]{"FRECUENCIA_PAGO", "frecuenciapago get() → Lista todos"});
                data.add(new String[]{"FRECUENCIA_PAGO", "frecuenciapago get(id) → Obtiene por ID"});
                data.add(new String[]{"FRECUENCIA_PAGO", "frecuenciapago add(frecuencia_dias,nombre)"});
                data.add(new String[]{"FRECUENCIA_PAGO", "frecuenciapago modify(id,frecuencia_dias,nombre)"});
                data.add(new String[]{"FRECUENCIA_PAGO", "frecuenciapago delete(id)"});

                // 🏦 NRO CUENTA
                data.add(new String[]{"NRO_CUENTA", "nrocuenta get() → Lista todos"});
                data.add(new String[]{"NRO_CUENTA", "nrocuenta get(id) → Obtiene por ID"});
                data.add(new String[]{"NRO_CUENTA", "nrocuenta add(banco,nro_cuenta,es_activa)"});
                data.add(new String[]{"NRO_CUENTA", "nrocuenta modify(id,banco,nro_cuenta,es_activa)"});
                data.add(new String[]{"NRO_CUENTA", "nrocuenta delete(id)"});

                // 📋 CLÁUSULA
                data.add(new String[]{"CLAUSULA", "clausula get() → Lista todos"});
                data.add(new String[]{"CLAUSULA", "clausula get(id) → Obtiene por ID"});
                data.add(new String[]{"CLAUSULA", "clausula add(descripcion,activa)"});
                data.add(new String[]{"CLAUSULA", "clausula modify(id,descripcion,activa)"});
                data.add(new String[]{"CLAUSULA", "clausula delete(id)"});

                /* ═══════════════════════════ RELACIONES ═══════════════════════════ */

                // 🔗 USER HAS ROLE
                data.add(new String[]{"USER_HAS_ROLE", "userhasrole get() → Lista todos"});
                data.add(new String[]{"USER_HAS_ROLE", "userhasrole get(id) → Obtiene por ID"});
                data.add(new String[]{"USER_HAS_ROLE", "userhasrole add(user_id,role_id)"});
                data.add(new String[]{"USER_HAS_ROLE", "userhasrole modify(id,user_id,role_id)"});
                data.add(new String[]{"USER_HAS_ROLE", "userhasrole delete(id)"});

                // 🔗 VEHÍCULO MANTENIMIENTO
                data.add(new String[]{"VEHICULO_MANT", "vehiculomantenimiento get() → Lista todos"});
                data.add(new String[]{"VEHICULO_MANT", "vehiculomantenimiento get(id) → Obtiene por ID"});
                data.add(new String[]{"VEHICULO_MANT", "vehiculomantenimiento add(fecha,monto,vehiculo_id,mantenimiento_id)"});
                data.add(new String[]{"VEHICULO_MANT", "vehiculomantenimiento modify(id,fecha,monto,vehiculo_id,mantenimiento_id)"});
                data.add(new String[]{"VEHICULO_MANT", "vehiculomantenimiento delete(id)"});

                // 🔗 CONTRATO CLÁUSULA
                data.add(new String[]{"CONTRATO_CLAUS", "contratoclausula get() → Lista todos"});
                data.add(new String[]{"CONTRATO_CLAUS", "contratoclausula get(id) → Obtiene por ID"});
                data.add(new String[]{"CONTRATO_CLAUS", "contratoclausula add(contrato_id,clausula_id)"});
                data.add(new String[]{"CONTRATO_CLAUS", "contratoclausula modify(id,contrato_id,clausula_id)"});
                data.add(new String[]{"CONTRATO_CLAUS", "contratoclausula delete(id)"});

                /* ═══════════════════════════ ENTIDADES DE NEGOCIO ═══════════════════════════ */

// 📄 CONTRATO
                data.add(new String[]{"CONTRATO", "contrato get() → Lista todos los contratos (tabla completa)"});
                data.add(new String[]{"CONTRATO", "contrato get(id) → Obtiene contrato específico por ID"});
                data.add(new String[]{"CONTRATO", "contrato add(estado,fecha_inicio,fecha_fin,frecuencia_pago_id,nro_cuenta_id,vehiculo_id) → Crea contrato, marca vehículo como alquilado, genera pago garantía + pagos diarios"});
                data.add(new String[]{"CONTRATO", "contrato modify(id,nuevo_estado) → Cambia solo el estado (finalizado/cancelado libera vehículo)"});
                data.add(new String[]{"CONTRATO", "contrato delete(id) → Elimina contrato y libera vehículo automáticamente"});
                // 🔗 USER CONTRATO
                data.add(new String[]{"USER_CONTRATO", "usercontrato get() → Lista todos"});
                data.add(new String[]{"USER_CONTRATO", "usercontrato get(id) → Obtiene por ID"});
                data.add(new String[]{"USER_CONTRATO", "usercontrato add(user_id,contrato_id)"});
                data.add(new String[]{"USER_CONTRATO", "usercontrato modify(id,user_id,contrato_id)"});
                data.add(new String[]{"USER_CONTRATO", "usercontrato delete(id)"});

                // 🔗 CONTRATO PAGO
                data.add(new String[]{"CONTRATO_PAGO", "contratopago get() → Lista todos"});
                data.add(new String[]{"CONTRATO_PAGO", "contratopago get(id) → Obtiene por ID"});
                data.add(new String[]{"CONTRATO_PAGO", "contratopago add(contrato_id,pago_id)"});
                data.add(new String[]{"CONTRATO_PAGO", "contratopago modify(id,contrato_id,pago_id)"});
                data.add(new String[]{"CONTRATO_PAGO", "contratopago delete(id)"});

                // 📅 RESERVA
                data.add(new String[]{"RESERVA", "reserva get() → Lista todos"});
                data.add(new String[]{"RESERVA", "reserva get(id) → Obtiene por ID"});
                data.add(new String[]{"RESERVA", "reserva add(estado,vehiculo_id,user_id,fecha) → Crea pago automático"});
                data.add(new String[]{"RESERVA", "reserva modify(id,estado)"});
                data.add(new String[]{"RESERVA", "reserva delete(id)"});

                // 💳 PAGO
                data.add(new String[]{"PAGO", "pago get() → Lista todos"});
                data.add(new String[]{"PAGO", "pago get(id) → Obtiene por ID"});
                data.add(new String[]{"PAGO", "pago add(desde,fecha,hasta,estado,tipo_pago,pagofacil_transaction_id,reserva_id)"});
                data.add(new String[]{"PAGO", "pago modify(id,estado,metodo_pago)"});
                data.add(new String[]{"PAGO", "pago delete(id)"});

                // 🔔 NOTIFICACIÓN
                data.add(new String[]{"NOTIFICACION", "notificacion get() → Lista todos"});
                data.add(new String[]{"NOTIFICACION", "notificacion get(id) → Obtiene por ID"});
                data.add(new String[]{"NOTIFICACION", "notificacion add(fecha,mensaje,tipo,user_id)"});
                data.add(new String[]{"NOTIFICACION", "notificacion modify(id,fecha,mensaje,tipo,user_id)"});
                data.add(new String[]{"NOTIFICACION", "notificacion delete(id)"});

                /* ═══════════════════════════ REPORTES Y ESPECIALES ═══════════════════════════ */

                data.add(new String[]{"REPORTES", "report(ingresos,fecha_inicio,fecha_fin) → Resumen de ingresos"});
                data.add(new String[]{"AYUDA", "help() o help get() → Muestra esta ayuda"});

                /* ═══════════════════════════ NOTAS IMPORTANTES ═══════════════════════════ */
                data.add(new String[]{"📝 NOTA", "Usar parametros en lo posible en minusculas para evitar fallos"});
                data.add(new String[]{"📝 NOTA", "Fechas formato: YYYY-MM-DD (ej: 2024-01-15)"});
                data.add(new String[]{"📝 NOTA", "Booleanos: true o false (sin comillas)"});
                data.add(new String[]{"📝 NOTA", "Números: sin comillas (ej: 1000, 120.5)"});
                data.add(new String[]{"📝 NOTA", "Texto null: usar null (sin comillas)"});
                data.add(new String[]{"📝 NOTA", "NO usar comillas simples en parámetros"});

                // Envía la tabla al emisor
                tableNotifySuccess(
                        event.getSender(),
                        "🚀 COMANDOS DISPONIBLES - SISTEMA DE GESTIÓN VEHICULAR",
                        headers,
                        data,
                        event.getCommand()
                );
            }
        } catch (Exception ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(),
                    Collections.singletonList("Error: " + ex.getMessage()));
        }
    }


    @Override
    public void onReceiptEmail(List<Email> emails) {
        for (Email email : emails) {
            Interpreter interpreter = new Interpreter(email.getSubject(), email.getFrom());
            interpreter.setCasoUsoListener(this);
            Thread thread = new Thread(interpreter);
            thread.start();
        }
    }

    private void handleError(int type, String email, List<String> args) {
        // Email emailObject = new Email(email, Email.SUBJECT,
        // HtmlRes.generateText(new String[]{
        // "Error de Sistema",
        // "Se ha producido un error al procesar su solicitud."
        // }));
        Email emailObject = new Email(email, Email.SUBJECT,
                HtmlRes.generateText(new String[] {
                        "Error de Sistema",
                        "Se ha producido un error al procesar su solicitud.",
                        args != null ? args.get(0) : ""
                }));
        sendEmail(emailObject);
    }

    private void simpleNotifySuccess(String email, String message) {
        Email emailObject = new Email(email, Email.SUBJECT,
                HtmlRes.generateText(new String[] {
                        "Operación exitosa",
                        message
                }));
        sendEmail(emailObject);
    }

    private void tableNotifySuccess(String email, String title, String[] headers, ArrayList<String[]> data) {
        System.out.println("Email: " + email);
        Email emailObject = new Email(email, Email.SUBJECT,
                HtmlRes.generateTable(title, headers, data));
        sendEmail(emailObject);
    }

    private void tableNotifySuccess(String email, String title, String[] headers, ArrayList<String[]> data,
            String command) {
        System.out.println("Email: " + email);
        Email emailObject = new Email(email, command,
                HtmlRes.generateTable(title, headers, data));
        sendEmail(emailObject);
    }

    private void sendEmail(Email email) {
        EmailSend sendEmail = new EmailSend(email);
        Thread thread = new Thread(sendEmail);
        thread.start();
    }

    private void simpleNotify(String email, String title, String message) {
        Email emailObject = new Email(email, Email.SUBJECT,
                HtmlRes.generateText(new String[] {
                        title,
                        message
                }));
        sendEmail(emailObject);
    }

}
