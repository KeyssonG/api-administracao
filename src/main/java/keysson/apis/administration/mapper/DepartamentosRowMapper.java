package keysson.apis.administration.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import keysson.apis.administration.dto.response.ResponseDepartamento;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DepartamentosRowMapper implements RowMapper<ResponseDepartamento> {
    @Override
    public ResponseDepartamento mapRow(ResultSet rs, int rowNum) throws SQLException {
        String nomeDepartamento = rs.getString("DEPARTAMENTO");
        int idDepartamento = rs.getInt("ID");
        return new ResponseDepartamento(nomeDepartamento, idDepartamento);
    }
}
