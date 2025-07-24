// File: negocio/NNotificacion.java
package negocio;

import data.DNotificacion;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NNotificacion {

    private final DNotificacion dao;

    public NNotificacion() {
        this.dao = new DNotificacion();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dao.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dao.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params: 0=fecha (YYYY-MM-DD), 1=mensaje, 2=tipo, 3=user_id
        Date fecha     = Date.valueOf(params.get(0));
        String mensaje = params.get(1);
        String tipo    = params.get(2);
        int userId     = Integer.parseInt(params.get(3));
        return dao.save(fecha, mensaje, tipo, userId);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id,1=fecha,2=mensaje,3=tipo,4=user_id
        int id         = Integer.parseInt(params.get(0));
        Date fecha     = Date.valueOf(params.get(1));
        String mensaje = params.get(2);
        String tipo    = params.get(3);
        int userId     = Integer.parseInt(params.get(4));
        return dao.update(id, fecha, mensaje, tipo, userId);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dao.delete(id);
    }
}
