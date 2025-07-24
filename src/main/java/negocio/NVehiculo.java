// File: negocio/NVehiculo.java
package negocio;

import data.DVehiculo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NVehiculo {

    private final DVehiculo dVehiculo;

    public NVehiculo() {
        this.dVehiculo = new DVehiculo();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dVehiculo.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dVehiculo.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params: 0=estado,1=monto_garantia,2=precio_dia,3=tipo
        String estado        = params.get(0);
        float  montoGarantia = Float.parseFloat(params.get(1));
        float  precioDia     = Float.parseFloat(params.get(2));
        String tipo          = params.get(3);
        return dVehiculo.save(estado, montoGarantia, precioDia, tipo);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id,1=estado,2=monto_garantia,3=precio_dia,4=tipo
        int    id            = Integer.parseInt(params.get(0));
        String estado        = params.get(1);
        float  montoGarantia = Float.parseFloat(params.get(2));
        float  precioDia     = Float.parseFloat(params.get(3));
        String tipo          = params.get(4);
        return dVehiculo.update(id, estado, montoGarantia, precioDia, tipo);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dVehiculo.delete(id);
    }
}
