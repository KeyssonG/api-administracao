package keysson.apis.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import keysson.apis.administration.dto.response.EmpresasStatusDTO;


@Component
public class EmpresasStatusMapper implements RowMapper<EmpresasStatusDTO> {

    @Override
    public EmpresasStatusDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        EmpresasStatusDTO  empresasStatusDTO = new EmpresasStatusDTO();
        empresasStatusDTO.setPendente(rs.getInt("pendente"));
        empresasStatusDTO.setAtivo(rs.getInt("ativo"));
        empresasStatusDTO.setRejeitado(rs.getInt("rejeitado"));

        return empresasStatusDTO;
    }
}
