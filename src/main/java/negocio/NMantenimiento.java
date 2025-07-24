package negocio;

import data.DMantenimiento;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NMantenimiento {

    private final DMantenimiento dMantenimiento;

    public NMantenimiento() {
        this.dMantenimiento = new DMantenimiento();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dMantenimiento.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dMantenimiento.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params: descripcion, nombre
        return dMantenimiento.save(
                params.get(0), // descripcion
                params.get(1)  // nombre
        );
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: id, descripcion, nombre
        return dMantenimiento.update(
                Integer.parseInt(params.get(0)), // id
                params.get(1), // descripcion
                params.get(2)  // nombre
        );
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: id
        return dMantenimiento.delete(
                Integer.parseInt(params.get(0)) // id
        );
    }
}
