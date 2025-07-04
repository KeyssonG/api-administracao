package keysson.apis.administration.repository;

import keysson.apis.administration.dto.response.EmpresaPendenteDTO;

import keysson.apis.administration.dto.response.EmpresasStatusDTO;
import keysson.apis.mapper.EmpresasStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;



@Repository
public class AdministrationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String PEDING_COMPANIES = """
            SELECT ID, CNPJ, NAME, STATUS, DESCRICAO_STATUS, NUMERO_CONTA FROM COMPANIES WHERE STATUS = 1
            """;

    private String UPDATE_ACCOUNT_STATUS = """
            UPDATE companies SET status = ? WHERE numero_conta = ?
            """;

    private String FIND_STATUS_COMPANY = """
             select 
              count(case when status = 1 then 1 end) as pendente,
              count(case when status = 2 then 1 end) as ativo,
              count(case when status = 3 then 1 end) as rejeitado
            from companies	
            """;


    public List<EmpresaPendenteDTO> findPendingCompanies(int numeroConta) {
        StringBuilder sql = new StringBuilder(PEDING_COMPANIES);
        List<Object> params = new ArrayList<>();

        if (numeroConta != 0) {
            sql.append(" AND NUMERO_CONTA = ?");
            params.add(numeroConta);
        }

        return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> EmpresaPendenteDTO.builder()
                        .id(rs.getInt("ID"))
                        .cnpj(rs.getString("CNPJ"))
                        .nome(rs.getString("NAME"))
                        .status(rs.getInt("STATUS"))
                        .descricao(rs.getString("DESCRICAO_STATUS"))
                        .numeroConta(rs.getInt("NUMERO_CONTA"))
                        .build()
        );
    }

    public void newAccontStatus(int status, int conta) {
        jdbcTemplate.update(UPDATE_ACCOUNT_STATUS, status, conta);
    }

    public EmpresasStatusDTO findStatusCompany() {
        try {
            return jdbcTemplate.queryForObject(FIND_STATUS_COMPANY, new EmpresasStatusMapper());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar status das empresas", e);
        }
    }
}