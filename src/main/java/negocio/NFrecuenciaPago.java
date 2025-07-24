// File: negocio/NFrecuenciaPago.java
package negocio;

import data.DFrecuenciaPago;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NFrecuenciaPago {

    private final DFrecuenciaPago dFrecuenciaPago;

    public NFrecuenciaPago() {
        this.dFrecuenciaPago = new DFrecuenciaPago();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dFrecuenciaPago.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dFrecuenciaPago.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params.get(0) = frecuencia_dias, params.get(1) = nombre
        int freqDias = Integer.parseInt(params.get(0));
        String nombre = params.get(1);
        return dFrecuenciaPago.save(freqDias, nombre);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params.get(0) = id, params.get(1) = frecuencia_dias, params.get(2) = nombre
        int id = Integer.parseInt(params.get(0));
        int freqDias = Integer.parseInt(params.get(1));
        String nombre = params.get(2);
        return dFrecuenciaPago.update(id, freqDias, nombre);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params.get(0) = id
        int id = Integer.parseInt(params.get(0));
        return dFrecuenciaPago.delete(id);
    }
}
