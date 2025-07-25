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
        // params: 0=estado,1=marca,2=modelo,3=monto_garantia,4=placa,5=precio_dia,6=tipo,7=url_foto
        String estado        = params.get(0);
        String marca         = params.get(1);
        String modelo        = params.get(2);
        float  montoGarantia = Float.parseFloat(params.get(3));
        String placa         = params.get(4);
        float  precioDia     = Float.parseFloat(params.get(5));
        String tipo          = params.get(6);
        String urlFoto       = params.get(7);

        return dVehiculo.save(
                estado,
                marca,
                modelo,
                montoGarantia,
                placa,
                precioDia,
                tipo,
                urlFoto
        );
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id,1=estado,2=marca,3=modelo,4=monto_garantia,5=placa,6=precio_dia,7=tipo,8=url_foto
        int    id            = Integer.parseInt(params.get(0));
        String estado        = params.get(1);
        String marca         = params.get(2);
        String modelo        = params.get(3);
        float  montoGarantia = Float.parseFloat(params.get(4));
        String placa         = params.get(5);
        float  precioDia     = Float.parseFloat(params.get(6));
        String tipo          = params.get(7);
        String urlFoto       = params.get(8);

        return dVehiculo.update(
                id,
                estado,
                marca,
                modelo,
                montoGarantia,
                placa,
                precioDia,
                tipo,
                urlFoto
        );
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dVehiculo.delete(id);
    }
}
