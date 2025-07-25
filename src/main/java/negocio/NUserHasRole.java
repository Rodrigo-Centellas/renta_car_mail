// File: negocio/NUserHasRole.java
package negocio;

import data.DUserHasRole;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NUserHasRole {

    private final DUserHasRole dao;

    public NUserHasRole() {
        this.dao = new DUserHasRole();
    }

    /**
     * Lista todas las asignaciones de roles
     */
    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dao.list();
    }

    /**
     * Obtiene los roles de un usuario por su ID
     * params: 0=user_id
     */
    public List<String[]> get(int userId) throws SQLException {
        return dao.getUserRoles(userId);
    }

    /**
     * Asigna un rol a un usuario
     * params: 0=user_id, 1=role_id
     */
    public List<String[]> save(List<String> params) throws SQLException {
        int userId = Integer.parseInt(params.get(0));
        int roleId = Integer.parseInt(params.get(1));
        return dao.save(userId, roleId);
    }

    /**
     * Actualiza una asignación de rol (elimina el anterior y asigna el nuevo)
     * params: 0=old_role_id, 1=user_id, 2=new_role_id
     */
    public List<String[]> update(List<String> params) throws SQLException {
        int oldRoleId = Integer.parseInt(params.get(0));
        int userId = Integer.parseInt(params.get(1));
        int newRoleId = Integer.parseInt(params.get(2));
        return dao.update(oldRoleId, userId, newRoleId);
    }

    /**
     * Elimina una asignación de rol específica
     * params: 0=role_id, 1=user_id
     */
    public List<String[]> delete(List<String> params) throws SQLException {
        if (params.size() == 1) {
            // Solo user_id - elimina todos los roles del usuario
            int userId = Integer.parseInt(params.get(0));
            return dao.delete(userId);
        } else {
            // role_id y user_id - elimina asignación específica
            int roleId = Integer.parseInt(params.get(0));
            int userId = Integer.parseInt(params.get(1));
            return dao.delete(roleId, userId);
        }
    }
}