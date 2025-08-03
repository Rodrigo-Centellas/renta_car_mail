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

    /**
     * Convierte un string a boolean de manera segura
     */
    private boolean parseBoolean(String value) {
        if (value == null) return false;

        // Normalizar el string
        String normalized = value.trim().toLowerCase();

        // Valores que se consideran true
        return normalized.equals("true") ||
                normalized.equals("1") ||
                normalized.equals("yes") ||
                normalized.equals("sí") ||
                normalized.equals("si") ||
                normalized.equals("verdadero") ||
                normalized.equals("activa") ||
                normalized.equals("activo");
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dNroCuenta.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dNroCuenta.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params.get(0) = banco, params.get(1) = nro_cuenta, params.get(2) = es_activa
        String banco = params.get(0);
        int nroCuenta = Integer.parseInt(params.get(1));
        boolean esActiva = parseBoolean(params.get(2)); // CORRECCIÓN: usar parseBoolean

        System.out.println("💳 Procesando nueva cuenta bancaria:");
        System.out.println("   Banco: " + banco);
        System.out.println("   Número: " + nroCuenta);
        System.out.println("   Activa (string): " + params.get(2));
        System.out.println("   Activa (boolean): " + esActiva);

        return dNroCuenta.save(banco, nroCuenta, esActiva);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params.get(0) = id, params.get(1) = banco, params.get(2) = nro_cuenta, params.get(3) = es_activa
        int id = Integer.parseInt(params.get(0));
        String banco = params.get(1);
        int nroCuenta = Integer.parseInt(params.get(2));
        boolean esActiva = parseBoolean(params.get(3)); // CORRECCIÓN: usar parseBoolean

        System.out.println("💳 Actualizando cuenta bancaria ID " + id + ":");
        System.out.println("   Banco: " + banco);
        System.out.println("   Número: " + nroCuenta);
        System.out.println("   Activa (string): " + params.get(3));
        System.out.println("   Activa (boolean): " + esActiva);

        return dNroCuenta.update(id, banco, nroCuenta, esActiva);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params.get(0) = id
        int id = Integer.parseInt(params.get(0));
        return dNroCuenta.delete(id);
    }
}