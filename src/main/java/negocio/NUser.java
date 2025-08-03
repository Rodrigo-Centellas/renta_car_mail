package negocio;

import data.DUser;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Business logic layer for User operations.
 * <p>
 * ⭐ ACTUALIZADO para incluir el campo 'verificado':
 * <ul>
 *     <li>email</li>
 *     <li>password (hasheado automáticamente)</li>
 *     <li>documento_frontal_path</li>
 *     <li>documento_trasero_path</li>
 *     <li>verificado (pendiente/verificado/rechazado)</li>
 * </ul>
 * </p>
 */
public class NUser {

    private final DUser dUser;

    public NUser() {
        this.dUser = new DUser();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dUser.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dUser.get(id);
    }

    /**
     * ⭐ ACTUALIZADO: Ahora incluye el campo 'verificado' como parámetro 9
     * Parámetros esperados:
     * 0=apellido, 1=ci, 2=domicilio, 3=nombre, 4=telefono,
     * 5=email, 6=password, 7=documento_frontal_path,
     * 8=documento_trasero_path, 9=verificado
     */
    public List<String[]> save(List<String> params) throws SQLException {
        String apellido             = params.get(0);
        int    ci                   = Integer.parseInt(params.get(1));
        String domicilio            = params.get(2);
        String nombre               = params.get(3);
        int    telefono             = Integer.parseInt(params.get(4));
        String email                = params.get(5);
        String password             = params.get(6);
        String documentoFrontalPath = params.get(7);
        String documentoTraseroPath = params.get(8);

        // ⭐ NUEVO: Manejo del campo verificado
        String verificado = "pendiente"; // Valor por defecto
        if (params.size() > 9 && !params.get(9).isEmpty()) {
            verificado = params.get(9);
        }

        return dUser.save(
                apellido,
                ci,
                domicilio,
                nombre,
                telefono,
                email,
                password,
                documentoFrontalPath,
                documentoTraseroPath,
                verificado
        );
    }

    /**
     * ⭐ ACTUALIZADO: Ahora incluye el campo 'verificado' como parámetro 10
     * Parámetros esperados:
     * 0=id, 1=apellido, 2=ci, 3=domicilio, 4=nombre, 5=telefono,
     * 6=email, 7=password, 8=documento_frontal_path,
     * 9=documento_trasero_path, 10=verificado
     */
    public List<String[]> update(List<String> params) throws SQLException {
        int    id                   = Integer.parseInt(params.get(0));
        String apellido             = params.get(1);
        int    ci                   = Integer.parseInt(params.get(2));
        String domicilio            = params.get(3);
        String nombre               = params.get(4);
        int    telefono             = Integer.parseInt(params.get(5));
        String email                = params.get(6);
        String password             = params.get(7);
        String documentoFrontalPath = params.get(8);
        String documentoTraseroPath = params.get(9);

        // ⭐ NUEVO: Manejo del campo verificado
        String verificado = "pendiente"; // Valor por defecto
        if (params.size() > 10 && !params.get(10).isEmpty()) {
            verificado = params.get(10);
        }

        return dUser.update(
                id,
                apellido,
                ci,
                domicilio,
                nombre,
                telefono,
                email,
                password,
                documentoFrontalPath,
                documentoTraseroPath,
                verificado
        );
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        int id = Integer.parseInt(params.get(0));
        return dUser.delete(id);
    }

    /**
     * ⭐ NUEVO MÉTODO: Para cambiar solo el estado de verificación
     * Útil para operaciones administrativas de verificación de usuarios.
     *
     * Parámetros esperados:
     * 0=id, 1=nuevo_estado_verificacion
     */
    public List<String[]> updateVerificationStatus(List<String> params) throws SQLException {
        int id = Integer.parseInt(params.get(0));
        String nuevoEstado = params.get(1);

        // Validar estados permitidos
        if (!isValidVerificationStatus(nuevoEstado)) {
            throw new IllegalArgumentException(
                    "Estado de verificación inválido: " + nuevoEstado +
                            ". Estados válidos: pendiente, verificado, rechazado"
            );
        }

        return dUser.updateVerificationStatus(id, nuevoEstado);
    }

    /**
     * Valida si un estado de verificación es válido.
     */
    private boolean isValidVerificationStatus(String estado) {
        return estado != null &&
                (estado.equals("pendiente") ||
                        estado.equals("verificado") ||
                        estado.equals("rechazado"));
    }

    /**
     * ⭐ NUEVO MÉTODO: Verificar credenciales de login
     * Útil para validaciones de autenticación.
     */
    public boolean verifyCredentials(String email, String password) throws SQLException {
        return dUser.verifyCredentials(email, password);
    }
}