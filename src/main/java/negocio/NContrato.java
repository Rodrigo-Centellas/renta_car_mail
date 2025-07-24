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
        //         3=frecuencia_pago_id, 4=nro_cuenta_id, 5=garante_id, 6=vehiculo_id
        String estado              = params.get(0);
        Date fechaInicio           = Date.valueOf(params.get(1));
        Date fechaFin              = Date.valueOf(params.get(2));
        int frecuenciaPagoId       = Integer.parseInt(params.get(3));
        int nroCuentaId            = Integer.parseInt(params.get(4));
        int garanteId              = Integer.parseInt(params.get(5));
        int vehiculoId             = Integer.parseInt(params.get(6));
        return dao.save(estado,
                fechaInicio,
                fechaFin,
                frecuenciaPagoId,
                nroCuentaId,
                garanteId,
                vehiculoId);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id, 1=estado, 2=fecha_inicio, 3=fecha_fin,
        //         4=frecuencia_pago_id, 5=nro_cuenta_id, 6=garante_id, 7=vehiculo_id
        int id                     = Integer.parseInt(params.get(0));
        String estado              = params.get(1);
        Date fechaInicio           = Date.valueOf(params.get(2));
        Date fechaFin              = Date.valueOf(params.get(3));
        int frecuenciaPagoId       = Integer.parseInt(params.get(4));
        int nroCuentaId            = Integer.parseInt(params.get(5));
        int garanteId              = Integer.parseInt(params.get(6));
        int vehiculoId             = Integer.parseInt(params.get(7));
        return dao.update(id,
                estado,
                fechaInicio,
                fechaFin,
                frecuenciaPagoId,
                nroCuentaId,
                garanteId,
                vehiculoId);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dao.delete(id);
    }
}
