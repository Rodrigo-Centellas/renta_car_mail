// File: negocio/NVehiculoMantenimiento.java
package negocio;

import data.DVehiculoMantenimiento;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NVehiculoMantenimiento {

    private final DVehiculoMantenimiento dao;

    public NVehiculoMantenimiento() {
        this.dao = new DVehiculoMantenimiento();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dao.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dao.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params: 0=fecha (yyyy-[m]m-[d]d), 1=monto, 2=vehiculo_id, 3=mantenimiento_id
        Date fecha = Date.valueOf(params.get(0));
        String monto = params.get(1);
        int vehiculoId = Integer.parseInt(params.get(2));
        int mantenimientoId = Integer.parseInt(params.get(3));
        return dao.save(fecha, monto, vehiculoId, mantenimientoId);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id, 1=fecha, 2=monto, 3=vehiculo_id, 4=mantenimiento_id
        int id = Integer.parseInt(params.get(0));
        Date fecha = Date.valueOf(params.get(1));
        String monto = params.get(2);
        int vehiculoId = Integer.parseInt(params.get(3));
        int mantenimientoId = Integer.parseInt(params.get(4));
        return dao.update(id, fecha, monto, vehiculoId, mantenimientoId);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dao.delete(id);
    }
}
