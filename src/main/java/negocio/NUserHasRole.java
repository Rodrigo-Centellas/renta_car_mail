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

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dao.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dao.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params: 0=user_id, 1=role_id
        int userId = Integer.parseInt(params.get(0));
        int roleId = Integer.parseInt(params.get(1));
        return dao.save(userId, roleId);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id, 1=user_id, 2=role_id
        int id     = Integer.parseInt(params.get(0));
        int userId = Integer.parseInt(params.get(1));
        int roleId = Integer.parseInt(params.get(2));
        return dao.update(id, userId, roleId);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dao.delete(id);
    }
}