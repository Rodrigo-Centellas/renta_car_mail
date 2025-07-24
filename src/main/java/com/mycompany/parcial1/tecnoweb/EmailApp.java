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
    private NReservaVehiculo nReservaVehiculo;
    private final NReporteIngresos nReporte;
    private NNotificacion nNotificacion;
    private NContrato nContrato;
    private NContratoPago nContratoPago;
    private NUserContrato nUserContrato;
    private NReserva nReserva;
    private NPago nPago;




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
        this.nReservaVehiculo = new NReservaVehiculo();
        this.nNotificacion = new NNotificacion();
        this.nContrato = new NContrato();
        this.nContratoPago = new NContratoPago();
        this.nUserContrato = new NUserContrato();
        this.nReserva = new NReserva();
        this.nPago = new NPago();
        this.nReporte = new NReporteIngresos();
    }

    public void start() {
        Thread thread = new Thread(emailReceipt);
        thread.setName("Mail Receipt");
        thread.start();
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
    public void reservaVehiculo(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    List<String[]> saved = nReservaVehiculo.save(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Reserva_Vehiculo creada",
                            DReservaVehiculo.HEADERS,
                            (ArrayList<String[]>) saved,
                            event.getCommand()
                    );
                    break;

                case Token.GET:
                    if (!event.getParams().isEmpty()) {
                        int id = Integer.parseInt(event.getParams().get(0));
                        tableNotifySuccess(
                                event.getSender(),
                                "Detalle Reserva_Vehiculo",
                                DReservaVehiculo.HEADERS,
                                (ArrayList<String[]>) nReservaVehiculo.get(id),
                                event.getCommand()
                        );
                    } else {
                        tableNotifySuccess(
                                event.getSender(),
                                "Lista Reserva_Vehiculo",
                                DReservaVehiculo.HEADERS,
                                nReservaVehiculo.list(),
                                event.getCommand()
                        );
                    }
                    break;

                case Token.MODIFY:
                    List<String[]> upd = nReservaVehiculo.update(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Reserva_Vehiculo actualizada",
                            DReservaVehiculo.HEADERS,
                            (ArrayList<String[]>) upd,
                            event.getCommand()
                    );
                    break;

                case Token.DELETE:
                    List<String[]> rem = nReservaVehiculo.delete(event.getParams());
                    tableNotifySuccess(
                            event.getSender(),
                            "Reserva_Vehiculo eliminada",
                            DReservaVehiculo.HEADERS,
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
                String[] headers = {"Categoría", "Comando", "Descripción"};
                ArrayList<String[]> data = new ArrayList<>();

                /* ─────────────── TABLAS BÁSICAS ─────────────── */

                // Rol
                data.add(new String[]{"Rol", "rol get()",                         "Lista todos los roles"});
                data.add(new String[]{"Rol", "rol get(<id>)",                    "Obtiene rol por ID"});
                data.add(new String[]{"Rol", "rol add(<nombre>)",                "Crea un rol"});
                data.add(new String[]{"Rol", "rol modify(<id>,<nombre>)",        "Modifica un rol"});
                data.add(new String[]{"Rol", "rol delete(<id>)",                 "Elimina un rol"});

                // User
                data.add(new String[]{"User", "user get()",                      "Lista todos los usuarios"});
                data.add(new String[]{"User", "user get(<id>)",                  "Obtiene usuario por ID"});
                data.add(new String[]{"User", "user add(<apellido>,<ci>,<domicilio>,<nombre>,<telefono>)",
                        "Crea un usuario"});
                data.add(new String[]{"User", "user modify(<id>,<apellido>,<ci>,<domicilio>,<nombre>,<telefono>)",
                        "Modifica un usuario"});
                data.add(new String[]{"User", "user delete(<id>)",               "Elimina un usuario"});

                // Vehiculo
                data.add(new String[]{"Vehiculo", "vehiculo get()",                      "Lista todos los vehículos"});
                data.add(new String[]{"Vehiculo", "vehiculo get(<id>)",                 "Obtiene vehículo por ID"});
                data.add(new String[]{"Vehiculo", "vehiculo add(<estado>,<monto_garantia>,<precio_dia>,<tipo>)",
                        "Crea un vehículo"});
                data.add(new String[]{"Vehiculo", "vehiculo modify(<id>,<estado>,<monto_garantia>,<precio_dia>,<tipo>)",
                        "Modifica un vehículo"});
                data.add(new String[]{"Vehiculo", "vehiculo delete(<id>)",              "Elimina un vehículo"});

                // Garante
                data.add(new String[]{"Garante", "garante get()",                      "Lista todos los garantes"});
                data.add(new String[]{"Garante", "garante get(<id>)",                 "Obtiene garante por ID"});
                data.add(new String[]{"Garante", "garante add(<apellido>,<ci>,<domicilio>,<nombre>,<telefono>)",
                        "Crea un garante"});
                data.add(new String[]{"Garante", "garante modify(<id>,<apellido>,<ci>,<domicilio>,<nombre>,<telefono>)",
                        "Modifica un garante"});
                data.add(new String[]{"Garante", "garante delete(<id>)",              "Elimina un garante"});

                // Mantenimiento
                data.add(new String[]{"Mantenimiento", "mantenimiento get()",                      "Lista todos los mantenimientos"});
                data.add(new String[]{"Mantenimiento", "mantenimiento get(<id>)",                 "Obtiene mantenimiento por ID"});
                data.add(new String[]{"Mantenimiento", "mantenimiento add(<descripcion>,<nombre>)",
                        "Crea un mantenimiento"});
                data.add(new String[]{"Mantenimiento", "mantenimiento modify(<id>,<descripcion>,<nombre>)",
                        "Modifica un mantenimiento"});
                data.add(new String[]{"Mantenimiento", "mantenimiento delete(<id>)",              "Elimina un mantenimiento"});

                // FrecuenciaPago
                data.add(new String[]{"FrecuenciaPago", "frecuenciapago get()",                      "Lista todas las frecuencias de pago"});
                data.add(new String[]{"FrecuenciaPago", "frecuenciapago get(<id>)",                 "Obtiene frecuencia por ID"});
                data.add(new String[]{"FrecuenciaPago", "frecuenciapago add(<frecuencia_dias>,<nombre>)",
                        "Crea una frecuencia de pago"});
                data.add(new String[]{"FrecuenciaPago", "frecuenciapago modify(<id>,<frecuencia_dias>,<nombre>)",
                        "Modifica una frecuencia de pago"});
                data.add(new String[]{"FrecuenciaPago", "frecuenciapago delete(<id>)",              "Elimina una frecuencia de pago"});

                // NroCuenta
                data.add(new String[]{"NroCuenta", "nrocuenta get()",                      "Lista todas las cuentas bancarias"});
                data.add(new String[]{"NroCuenta", "nrocuenta get(<id>)",                 "Obtiene cuenta por ID"});
                data.add(new String[]{"NroCuenta", "nrocuenta add(<banco>,<nro_cuenta>)",
                        "Crea una cuenta bancaria"});
                data.add(new String[]{"NroCuenta", "nrocuenta modify(<id>,<banco>,<nro_cuenta>)",
                        "Modifica una cuenta bancaria"});
                data.add(new String[]{"NroCuenta", "nrocuenta delete(<id>)",              "Elimina una cuenta bancaria"});

                // Clausula
                data.add(new String[]{"Clausula", "clausula get()",                      "Lista todas las cláusulas"});
                data.add(new String[]{"Clausula", "clausula get(<id>)",                 "Obtiene cláusula por ID"});
                data.add(new String[]{"Clausula", "clausula add(<descripcion>)",        "Crea una cláusula"});
                data.add(new String[]{"Clausula", "clausula modify(<id>,<descripcion>)", "Modifica una cláusula"});
                data.add(new String[]{"Clausula", "clausula delete(<id>)",               "Elimina una cláusula"});

                /* ─────────────── RELACIONES / TABLAS INTERMEDIAS ─────────────── */

                // VehiculoMantenimiento
                data.add(new String[]{"VehiculoMantenimiento", "vehiculomantenimiento get()",
                        "Lista registros de vehículo-mantenimiento"});
                data.add(new String[]{"VehiculoMantenimiento",
                        "vehiculomantenimiento add(<fecha>,<monto>,<vehiculo_id>,<mantenimiento_id>)",
                        "Registra un mantenimiento realizado"});
                data.add(new String[]{"VehiculoMantenimiento", "vehiculomantenimiento delete(<id>)",
                        "Elimina un registro de mantenimiento"});

                // Contrato_Clausula
                data.add(new String[]{"Contrato_Clausula", "contratoclausula get()",
                        "Lista asociaciones contrato-cláusula"});
                data.add(new String[]{"Contrato_Clausula",
                        "contratoclausula add(<contrato_id>,<clausula_id>)",
                        "Asocia una cláusula a un contrato"});
                data.add(new String[]{"Contrato_Clausula", "contratoclausula delete(<id>)",
                        "Quita la asociación contrato-cláusula"});

                // Reserva_Vehiculo
                data.add(new String[]{"Reserva_Vehiculo", "reserva_vehiculo get()",
                        "Lista asociaciones reserva-vehículo"});
                data.add(new String[]{"Reserva_Vehiculo",
                        "reserva_vehiculo add(<fecha>,<reserva_id>,<vehiculo_id>)",
                        "Asocia un vehículo a una reserva"});
                data.add(new String[]{"Reserva_Vehiculo", "reserva_vehiculo delete(<id>)",
                        "Elimina la asociación reserva-vehículo"});

                // UserContrato
                data.add(new String[]{"UserContrato", "usercontrato get()",
                        "Lista asociaciones usuario-contrato"});
                data.add(new String[]{"UserContrato", "usercontrato add(<user_id>,<contrato_id>)",
                        "Asocia un usuario a un contrato"});
                data.add(new String[]{"UserContrato", "usercontrato delete(<id>)",
                        "Elimina la asociación usuario-contrato"});

                /* ─────────────── ENTIDADES DE NEGOCIO ─────────────── */

                // Contrato
                data.add(new String[]{"Contrato", "contrato get()",
                        "Lista todos los contratos"});
                data.add(new String[]{"Contrato", "contrato get(<id>)",
                        "Obtiene contrato por ID"});
                data.add(new String[]{"Contrato",
                        "contrato add(<estado>,<fecha_inicio>,<fecha_fin>,<frecuencia_pago_id>,<nro_cuenta_id>,<garante_id>,<vehiculo_id>)",
                        "Crea un contrato"});
                data.add(new String[]{"Contrato", "contrato modify(<id>,<estado>,<fecha_fin>)",
                        "Modifica un contrato"});
                data.add(new String[]{"Contrato", "contrato delete(<id>)",
                        "Elimina un contrato"});

                // ContratoPago
                data.add(new String[]{"ContratoPago", "contratopago get()",
                        "Lista todos los pagos de contrato"});
                data.add(new String[]{"ContratoPago", "contratopago get(<id>)",
                        "Obtiene vínculo contrato-pago por ID"});
                data.add(new String[]{"ContratoPago", "contratopago add(<contrato_id>,<pago_id>)",
                        "Registra un pago en un contrato"});
                data.add(new String[]{"ContratoPago", "contratopago delete(<id>)",
                        "Elimina un vínculo contrato-pago"});

                // Reserva
                data.add(new String[]{"Reserva", "reserva get()",                      "Lista todas las reservas"});
                data.add(new String[]{"Reserva", "reserva get(<id>)",                 "Obtiene reserva por ID"});
                data.add(new String[]{"Reserva", "reserva add(<estado>,<vehiculo_id>,<user_id>)",
                        "Crea una reserva"});
                data.add(new String[]{"Reserva", "reserva modify(<id>,<estado>)",     "Modifica el estado de una reserva"});
                data.add(new String[]{"Reserva", "reserva delete(<id>)",              "Elimina una reserva"});

                // Pago
                data.add(new String[]{"Pago", "pago get()",                      "Lista todos los pagos"});
                data.add(new String[]{"Pago", "pago get(<id>)",                 "Obtiene pago por ID"});
                data.add(new String[]{"Pago",
                        "pago add(<desde>,<fecha>,<hasta>,<estado>,<tipo_pago>,<reserva_id>)",
                        "Crea un pago y marca la reserva como pagada"});
                data.add(new String[]{"Pago",
                        "pago modify(<id>,<desde>,<fecha>,<hasta>,<estado>,<tipo_pago>,<reserva_id>)",
                        "Modifica un pago existente"});
                data.add(new String[]{"Pago", "pago delete(<id>)",              "Elimina un pago"});

                // Notificación
                data.add(new String[]{"Notificación", "notificacion get()",          "Lista todas las notificaciones"});
                data.add(new String[]{"Notificación", "notificacion get(<id>)",     "Obtiene notificación por ID"});
                data.add(new String[]{"Notificación",
                        "notificacion add(<fecha>,<mensaje>,<tipo>,<user_id>)",
                        "Crea una notificación"});
                data.add(new String[]{"Notificación", "notificacion delete(<id>)",  "Elimina una notificación"});

                /* ─────────────── REPORTES ─────────────── */

                data.add(new String[]{"Reportes",
                        "report(ingresos,<fecha_inicio>,<fecha_fin>)",
                        "Resumen de ingresos entre dos fechas"});

                // Envía la tabla al emisor
                tableNotifySuccess(event.getSender(), "Comandos disponibles detallados", headers, data);
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
