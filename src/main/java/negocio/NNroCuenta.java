// File: negocio/NNroCuenta.java
package negocio;

import data.DNroCuenta;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NNroCuenta {

    private final DNroCuenta dNroCuenta;

    public NNroCuenta() {
        this.dNroCuenta = new DNroCuenta();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dNroCuenta.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dNroCuenta.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params.get(0) = banco, params.get(1) = nro_cuenta
        String banco = params.get(0);
        int nroCuenta = Integer.parseInt(params.get(1));
        return dNroCuenta.save(banco, nroCuenta);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params.get(0) = id, params.get(1) = banco, params.get(2) = nro_cuenta
        int id = Integer.parseInt(params.get(0));
        String banco = params.get(1);
        int nroCuenta = Integer.parseInt(params.get(2));
        return dNroCuenta.update(id, banco, nroCuenta);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params.get(0) = id
        int id = Integer.parseInt(params.get(0));
        return dNroCuenta.delete(id);
    }
}
