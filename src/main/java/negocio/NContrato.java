// File: negocio/NContrato.java
package negocio;

import data.DContrato;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NContrato {

    private final DContrato dao;

    public NContrato() {
        this.dao = new DContrato();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dao.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dao.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params: 0=estado, 1=fecha_inicio (YYYY-MM-DD), 2=fecha_fin,
        //         3=frecuencia_pago_id, 4=nro_cuenta_id, 5=vehiculo_id
        String estado              = params.get(0);
        Date fechaInicio           = Date.valueOf(params.get(1));
        Date fechaFin              = Date.valueOf(params.get(2));
        int frecuenciaPagoId       = Integer.parseInt(params.get(3));
        int nroCuentaId            = Integer.parseInt(params.get(4));
        int vehiculoId             = Integer.parseInt(params.get(5));
        return dao.save(estado,
                fechaInicio,
                fechaFin,
                frecuenciaPagoId,
                nroCuentaId,
                vehiculoId);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id, 1=nuevo_estado
        // Solo se puede cambiar el estado del contrato
        int id                     = Integer.parseInt(params.get(0));
        String nuevoEstado         = params.get(1);
        return dao.update(id, nuevoEstado);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dao.delete(id);
    }
}