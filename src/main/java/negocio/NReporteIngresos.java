package negocio;

import data.DReporteIngresos;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class NReporteIngresos {

    private final DReporteIngresos dao;
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

    public NReporteIngresos() {
        this.dao = new DReporteIngresos();
    }

    /**
     * Parámetros:
     * params.get(0) = fecha_inicio ("YYYY-MM-DD")
     * params.get(1) = fecha_fin ("YYYY-MM-DD")
     */
    public List<String[]> ingresos(List<String> params) throws SQLException, ParseException {        
        Date from = new Date(fmt.parse(params.get(0)).getTime());        
        Date to = new Date(fmt.parse(params.get(1)).getTime());
        
        return dao.listIngresos(from, to);
    }
}
