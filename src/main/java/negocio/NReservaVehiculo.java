// File: negocio/NReservaVehiculo.java
package negocio;

import data.DReservaVehiculo;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NReservaVehiculo {

    private final DReservaVehiculo dao;

    public NReservaVehiculo() {
        this.dao = new DReservaVehiculo();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dao.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dao.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params: 0=fecha (YYYY-MM-DD), 1=reserva_id, 2=vehiculo_id
        Date fecha       = Date.valueOf(params.get(0));
        int reservaId    = Integer.parseInt(params.get(1));
        int vehiculoId   = Integer.parseInt(params.get(2));
        return dao.save(fecha, reservaId, vehiculoId);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id, 1=fecha, 2=reserva_id, 3=vehiculo_id
        int id           = Integer.parseInt(params.get(0));
        Date fecha       = Date.valueOf(params.get(1));
        int reservaId    = Integer.parseInt(params.get(2));
        int vehiculoId   = Integer.parseInt(params.get(3));
        return dao.update(id, fecha, reservaId, vehiculoId);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dao.delete(id);
    }
}
