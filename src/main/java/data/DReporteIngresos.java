package data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DReporteIngresos {

    public static final String[] HEADERS = {
            "fecha", "total_pagos", "cantidad_pagos"
    };

    private final SqlConnection connection;

    public DReporteIngresos() {
        this.connection = new SqlConnection(
                DBConnection.database,
                DBConnection.server,
                DBConnection.port,
                DBConnection.user,
                DBConnection.password
        );
    }

    /**
     * Devuelve una fila por cada día entre from y to:
     *  [ fecha, suma_de_monto, conteo_de_pagos ]
     */
    public List<String[]> listIngresos(Date from, Date to) throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql =
                "SELECT date_trunc('day', fecha) AS dia, "
                        + "       SUM(monto)            AS total, "
                        + "       COUNT(*)              AS cantidad "
                        + "  FROM \"Pago\" "
                        + " WHERE fecha BETWEEN ? AND ? "
                        + " GROUP BY dia "
                        + " ORDER BY dia";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String dia       = rs.getTimestamp("dia").toLocalDateTime().toLocalDate().toString();
                    String total     = String.format("%.2f", rs.getDouble("total"));
                    String cantidad  = String.valueOf(rs.getInt("cantidad"));
                    rows.add(new String[]{ dia, total, cantidad });
                }
            }
        }
        return rows;
    }
}
