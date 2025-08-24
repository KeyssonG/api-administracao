package keysson.apis.administration.repository;

import keysson.apis.administration.dto.response.EmpresasStatusDTO;
import keysson.apis.administration.dto.response.PendingCompanyDTO;
import keysson.apis.administration.dto.response.ResponseDepartamento;
import keysson.apis.administration.mapper.DepartamentosRowMapper;
import keysson.apis.administration.mapper.EmpresasStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;



@Repository
public class AdministrationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DepartamentosRowMapper departamentosRowMapper;

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

    private String SQL_NEW_DEPARTMENT = """
            INSERT INTO DEPARTAMENTOS (company_id, departamento) VALUES (?, ?)
            """;

    private String SQL_GET_DEPARTMENTS_BY_COMPANY = """
            SELECT DEPARTAMENTO, ID FROM DEPARTAMENTOS WHERE COMPANY_ID = ?
            """;

    // SQL para deletar departamento por id
    private static final String SQL_DELETE_DEPARTMENT_BY_ID = "DELETE FROM DEPARTAMENTOS WHERE ID = ?";


    public List<PendingCompanyDTO> findPendingCompanies(int numeroConta) {
        StringBuilder sql = new StringBuilder(PEDING_COMPANIES);
        List<Object> params = new ArrayList<>();

        if (numeroConta != 0) {
            sql.append(" AND NUMERO_CONTA = ?");
            params.add(numeroConta);
        }

        return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> PendingCompanyDTO.builder()
                        .id(rs.getInt("ID"))
                        .cnpj(rs.getString("CNPJ"))
                        .name(rs.getString("NAME"))
                        .status(rs.getInt("STATUS"))
                        .description(rs.getString("DESCRICAO_STATUS"))
                        .accountNumber(rs.getInt("NUMERO_CONTA"))
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

    public void registerNewDepartment(int idEmpresa, String nomeDepartamento) {
        try {
            jdbcTemplate.update(SQL_NEW_DEPARTMENT, idEmpresa, nomeDepartamento);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar novo departamento: " + e.getMessage(), e);
        }
    }

    public List<ResponseDepartamento> getDepartmentsByCompany(int idEmpresa) {
        try {
            return jdbcTemplate.query(SQL_GET_DEPARTMENTS_BY_COMPANY, new Object[]{idEmpresa}, departamentosRowMapper);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar departamentos por empresa: " + e.getMessage(), e);
        }
    }

    public void deleteDepartmentById(int idDepartamento) {
        try {
            jdbcTemplate.update(SQL_DELETE_DEPARTMENT_BY_ID, idDepartamento);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar departamento: " + e.getMessage(), e);
        }
    }


}