package librerias.analex;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;

/**
 * Clase Token para manejar los identificadores y acciones en el análisis
 * léxico.
 */
public class Token {
    private int name; // Si es CU, ACTION o ERROR
    private int attribute; // Tipo específico, ya sea CU, ACTION o ERROR

    // Constantes numéricas para manejar el análisis léxico
    public static final int CU = 0;
    public static final int ACTION = 1;
    public static final int PARAMS = 2;
    public static final int END = 3;
    public static final int ERROR = 4;

    // NUEVO\
    public static final int ROL = 108;
    public static final int MANTENIMIENTO = 112;
    public static final int FRECUENCIAPAGO = 113;
    public static final int NROCUENTA = 114;
    public static final int CLAUSULA = 115;
    public static final int USERTABLE = 116;
    public static final int VEHICULO = 117;
    public static final int GARANTE = 118;
    public static final int VEHICULOMANTENIMIENTO = 119;
    public static final int CONTRATO_CLAUSULA = 120;

    public static final int USERRESERVA = 122;
    public static final int NOTIFICACION = 123;
    public static final int CONTRATO = 124;
    public static final int CONTRATO_PAGO = 125;
    public static final int USERCONTRATO = 126;

    public static final int RESERVA = 127;// siguiente valor libre
    public static final int PAGO = 128;
    public static final int USERHASROLE = 129;



    // Constantes para los títulos de casos de uso
    public static final int USUARIO = 100;
    public static final int EVENTO = 101;

    public static final int PROVEEDOR = 104;
    public static final int PROMOCION = 105;
    public static final int PATROCINADOR = 106;
    public static final int PATROCINIO = 107;
    public static final int SERVICIO = 109;
    public static final int DETALLEEVENTO = 110;

    public static final int HELP = 111;

    // Constantes para las acciones generales
    public static final int ADD = 200;
    public static final int DELETE = 201;
    public static final int MODIFY = 202;
    public static final int GET = 203;
    public static final int VERIFY = 204;
    public static final int CANCEL = 205;
    public static final int REPORT = 199;

    // Constantes de errores
    public static final int ERROR_COMMAND = 300;
    public static final int ERROR_CHARACTER = 301;

    // Constructor por defecto
    public Token() {
    }

    /**
     * Constructor parametrizado 2.
     * 
     * @param name
     */
    public Token(int name) {
        this.name = name;
    }

    /**
     * Constructor parametrizado 3.
     * 
     * @param name
     * @param attribute
     */
    public Token(int name, int attribute) {
        this.name = name;
        this.attribute = attribute;
    }

    // Constructor parametrizado por el literal del token
    public Token(String token) {
        int id = findByLexeme(token);
        if (id != -1) {
            if (100 <= id && id < 200) {
                this.name = CU;
                this.attribute = id;
            } else if (200 <= id && id < 300) {
                this.name = ACTION;
                this.attribute = id;
            }
        } else {
            this.name = ERROR;
            this.attribute = ERROR_COMMAND;
            System.err.println(
                    "Error: El lexema enviado al constructor no es reconocido como un token. Lexema: " + token);
        }
    }

    // Getters y setters
    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    // Método para obtener el string asociado a un token
    public String getStringToken(int token) {
        switch (token) {
            case CU:
                return "caso de uso";
            case ACTION:
                return "action";
            case PARAMS:
                return "params";
            case END:
                return "end";
            case ERROR:
                return "error";
            case USERCONTRATO:
                return "usercontrato";
            case MANTENIMIENTO:
                return "mantenimiento";
            case FRECUENCIAPAGO:
                return "frecuenciapago";
            case CLAUSULA:
                return "clausula";
            case ROL:
                return "rol";
            case USERTABLE:
                return "user";
            case VEHICULO:
                return "vehiculo";
            case USERHASROLE:
                return "userhasrole";
            case GARANTE:
                return "garante";
            case VEHICULOMANTENIMIENTO:
                return "vehiculomantenimiento";
            case CONTRATO_CLAUSULA:
                return "contratoclausula";
            case NOTIFICACION:
                return "notificacion";
            case CONTRATO:
                return "contrato";
            case CONTRATO_PAGO:
                return "contrato_pago";
            case PAGO:
                return "pago";
            case RESERVA:
                return "reserva";        
            case HELP:
                return "help";
            case ADD:
                return "add";
            case DELETE:
                return "delete";
            case MODIFY:
                return "modify";
            case GET:
                return "get";
            case VERIFY:
                return "verify";
            case CANCEL:
                return "cancel";
            case REPORT:
                return "report";
            case ERROR_COMMAND:
                return "UNKNOWN COMMAND";
            case ERROR_CHARACTER:
                return "UNKNOWN CHARACTER";
            default:
                return "N: " + token;
        }
    }

    // Método para encontrar un token por lexema
    private int findByLexeme(String lexeme) {
        switch (lexeme.toLowerCase()) {
            case "caso de uso":
                return CU;
            case "action":
                return ACTION;
            case "params":
                return PARAMS;
            case "end":
                return END;
            case "error":
                return ERROR;
            case "usercontrato":
                return USERCONTRATO;
            case "mantenimiento":
                return MANTENIMIENTO;
            case "frecuenciapago":
                return FRECUENCIAPAGO;
            case "nrocuenta":
                return NROCUENTA;
            case "userhasrole":
                return USERHASROLE;
            case "rol":
                return ROL;
            case "clausula":
                return CLAUSULA;
            case "user":
                return USERTABLE;
            case "vehiculo":
                return VEHICULO;
            case "garante":
                return GARANTE;
            case "vehiculomantenimiento":
                return VEHICULOMANTENIMIENTO;
            case "contratoclausula":
                return CONTRATO_CLAUSULA;
            case "userreserva":
                return USERRESERVA;
            case "notificacion":
                return NOTIFICACION;
            case "contrato":
                return CONTRATO;
            case "contratopago":
                return CONTRATO_PAGO;
            case "pago":
                return PAGO;
            case "reserva":
                return RESERVA;
            case "help":
                return HELP;
            case "add":
                return ADD;
            case "delete":
                return DELETE;
            case "modify":
                return MODIFY;
            case "get":
                return GET;
            case "verify":
                return VERIFY;
            case "cancel":
                return CANCEL;
            case "report":
                return REPORT;
            case "UNKNOWN COMMAND":
                return ERROR_COMMAND;
            case "UNKNOWN CHARACTER":
                return ERROR_CHARACTER;
            default:
                return -1;
        }
    }
}
