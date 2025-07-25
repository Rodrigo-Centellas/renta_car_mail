package librerias;

import interfaces.ICasoUsoListener;
import librerias.analex.Analex;
import librerias.analex.Token;

/**
 * Clase Interpreter se encarga de interpretar los comandos entrantes y enviar
 * las acciones a los manejadores correspondientes.
 */
public class Interpreter implements Runnable {

    private ICasoUsoListener IcasoUsoListener; // Listener para los casos de uso
    private Analex analex; // Analizador léxico

    private String instruccion; // Comando recibido
    private String sender; // Remitente del comando

    public Interpreter(String instruccion, String sender) {
        this.instruccion = instruccion;
        this.sender = sender;
    }

    public ICasoUsoListener getCasoUsoListener() {
        return IcasoUsoListener;
    }

    public void setCasoUsoListener(ICasoUsoListener casoUsoListener) {
        this.IcasoUsoListener = casoUsoListener;
    }

    public String getInstruccion() {
        return instruccion;
    }

    public void setInstruccion(String instruction) {
        this.instruccion = instruccion;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    private void filterEvent(Instruccion instruccion) {
        ParamsAction paramsAction = new ParamsAction(this, sender, this.instruccion);
        paramsAction.setAction(instruccion.getAction());

        int count_params = instruccion.countParams();
        for (int i = 0; i < count_params; i++) {
            int pos = instruccion.getParams(i);
            paramsAction.addParams(analex.getParam(pos));
        }

        switch (instruccion.getCasoUso()) {
            case Token.MANTENIMIENTO:
                IcasoUsoListener.mantenimiento(paramsAction);
                break;
            case Token.FRECUENCIAPAGO:
                IcasoUsoListener.frecuenciaPago(paramsAction);
                break;
            case Token.NROCUENTA:
                IcasoUsoListener.nroCuenta(paramsAction);
                break;
            case Token.ROL:
                IcasoUsoListener.rol(paramsAction);
                break;
            case Token.CLAUSULA:
                IcasoUsoListener.clausula(paramsAction);
                break;
            case Token.USERTABLE:
                IcasoUsoListener.user(paramsAction);
                break;
            case Token.VEHICULO:
                IcasoUsoListener.vehiculo(paramsAction);
                break;
            case Token.GARANTE:
                IcasoUsoListener.garante(paramsAction);
                break;
            case Token.VEHICULOMANTENIMIENTO:
                IcasoUsoListener.vehiculoMantenimiento(paramsAction);
                break;
            case Token.CONTRATO_CLAUSULA:
                IcasoUsoListener.contratoClausula(paramsAction);
                break;
            case Token.NOTIFICACION:
                IcasoUsoListener.notificacion(paramsAction);
                break;
            case Token.CONTRATO:
                IcasoUsoListener.contrato(paramsAction);
                break;
            case Token.USERHASROLE:
                IcasoUsoListener.userHasRole(paramsAction);
                break;
            case Token.CONTRATO_PAGO:
                IcasoUsoListener.contratoPago(paramsAction);
                break;
            case Token.USERCONTRATO:
                IcasoUsoListener.userContrato(paramsAction);
                break;
            case Token.RESERVA:
                IcasoUsoListener.reserva(paramsAction);
                break;
            case Token.PAGO:
                IcasoUsoListener.pago(paramsAction);
                break;
            case Token.REPORT:
                IcasoUsoListener.reporte(paramsAction);
                break;
            case Token.HELP:
                IcasoUsoListener.help(paramsAction);
                break;
            default:
                IcasoUsoListener.error(paramsAction);
                break;
        }
    }

    private void tokenError(Token token, String error) {
        ParamsAction paramsAction = new ParamsAction(this, sender);
        paramsAction.setAction(token.getAttribute());
        paramsAction.addParams(instruccion);
        paramsAction.addParams(error);
        IcasoUsoListener.error(paramsAction);
    }

    @Override
    public void run() {
        if (instruccion == null || instruccion.trim().isEmpty()) {
            System.out.println("No se proporcionó un comando válido.");
            return;
        }
        analex = new Analex(instruccion);
        Instruccion instruccionObj = new Instruccion(); // TokenCommand
        Token token;

        while ((token = analex.Preanalisis()).getName() != Token.END && token.getName() != Token.ERROR) {
            if (token.getName() == Token.CU) {
                instruccionObj.setCasoUso(token.getAttribute()); // ID del CU
            } else if (token.getName() == Token.ACTION) {
                instruccionObj.setAction(token.getAttribute()); // ID de la acción
            } else if (token.getName() == Token.PARAMS) {
                instruccionObj.addParams(token.getAttribute()); // La posición del parámetro
            }
            analex.next();
        }

        if (token.getName() == Token.END) {
            filterEvent(instruccionObj); // Se analizó el comando con éxito
        } else if (token.getName() == Token.ERROR) {
            tokenError(token, analex.lexeme()); // Se produjo un error en el análisis
        }
    }

}
