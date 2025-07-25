package interfaces;

import librerias.ParamsAction;

/**
 * Interfaz ICasoUsoListener define los métodos para los casos de uso soportados
 * por la aplicación en base a las tablas del modelo de dominio.
 */
public interface ICasoUsoListener {
   //nuevos
    void mantenimiento(ParamsAction event);
    void frecuenciaPago(librerias.ParamsAction event);
    void nroCuenta(ParamsAction event);
    void rol(librerias.ParamsAction event);
    void clausula(librerias.ParamsAction event);
    void user(ParamsAction event);
 void vehiculo(ParamsAction event);
 void garante(librerias.ParamsAction event);
 void error(ParamsAction event); // Método general para manejar errores
    void help(ParamsAction event); // Método para mostrar ayuda

 void vehiculoMantenimiento(librerias.ParamsAction event);
 void contratoClausula(librerias.ParamsAction event);

 void notificacion(ParamsAction event);
 void contrato(ParamsAction event);
 void contratoPago(ParamsAction event);
 void userContrato(ParamsAction event);
 void reserva(ParamsAction event);
 void pago(ParamsAction event);
 void reporte(ParamsAction event);
 void userHasRole(ParamsAction event);


}
