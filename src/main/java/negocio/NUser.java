package negocio;

import data.DUser;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Business logic layer for User operations.
 * <p>
 * Now handles additional fields: email, password,
 * documento_frontal_path, documento_trasero_path.
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

    public List<String[]> save(List<String> params) throws SQLException {
        // params: 0=apellido,1=ci,2=domicilio,3=nombre,4=telefono,5=email,6=password,7=documento_frontal_path,8=documento_trasero_path
        String apellido             = params.get(0);
        int    ci                   = Integer.parseInt(params.get(1));
        String domicilio            = params.get(2);
        String nombre               = params.get(3);
        int    telefono             = Integer.parseInt(params.get(4));
        String email                = params.get(5);
        String password             = params.get(6);
        String documentoFrontalPath = params.get(7);
        String documentoTraseroPath = params.get(8);
        return dUser.save(
                apellido,
                ci,
                domicilio,
                nombre,
                telefono,
                email,
                password,
                documentoFrontalPath,
                documentoTraseroPath
        );
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id,1=apellido,2=ci,3=domicilio,4=nombre,5=telefono,6=email,7=password,8=documento_frontal_path,9=documento_trasero_path
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
                documentoTraseroPath
        );
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dUser.delete(id);
    }
}
