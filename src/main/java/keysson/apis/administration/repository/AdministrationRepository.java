package keysson.apis.administration.repository;

import keysson.apis.administration.dto.response.CompanyModuloResponseDTO;
import keysson.apis.administration.dto.response.CompanyResponseDTO;
import keysson.apis.administration.dto.response.CompanyStatusDTO;
import keysson.apis.administration.dto.response.ModuloResponseDTO;
import keysson.apis.administration.dto.response.PendingCompanyDTO;
import keysson.apis.administration.dto.response.DepartmentResponse;

import keysson.apis.administration.mapper.CompanyStatusMapper;
import keysson.apis.administration.mapper.DepartmentsRowMapper;
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
    private DepartmentsRowMapper departamentosRowMapper;

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

    private String SQL_GET_MODULOS = """
            SELECT ID, NOME FROM MODULOS
            """;

    private String SQL_GET_COMPANIES_BY_STATUS = """
            SELECT ID, NAME FROM COMPANIES WHERE STATUS = ?
            """;

    private String SQL_LINK_COMPANY_MODULO = """
            INSERT INTO public.empresa_modulos (company_id, modulo_id, status) VALUES (?, ?, ?)
            """;

    private String SQL_GET_COMPANY_MODULOS = """
            select em.id, em.company_id, c.name, em.modulo_id, m.nome, em.status, ts.descricao 
            from empresa_modulos em
            join companies c on em.company_id = c.id 
            join modulos m on m.id = em.modulo_id 
            join tipos_status ts on ts.status = em.status
            """;


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

    public CompanyStatusDTO findStatusCompany() {
        try {
            return jdbcTemplate.queryForObject(FIND_STATUS_COMPANY, new CompanyStatusMapper());
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

    public List<DepartmentResponse> getDepartmentsByCompany(int idEmpresa) {
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

    public List<ModuloResponseDTO> getAllModulos(Integer id) {
        try {
            StringBuilder sql = new StringBuilder(SQL_GET_MODULOS);
            List<Object> params = new ArrayList<>();

            if (id != null && id != 0) {
                sql.append(" WHERE ID = ?");
                params.add(id);
            }

            return jdbcTemplate.query(
                    sql.toString(),
                    params.toArray(),
                    (rs, rowNum) -> ModuloResponseDTO.builder()
                            .id(rs.getInt("ID"))
                            .nome(rs.getString("NOME"))
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar módulos: " + e.getMessage(), e);
        }
    }

    public List<CompanyResponseDTO> findCompaniesByStatus(int statusId) {
        try {
            return jdbcTemplate.query(
                    SQL_GET_COMPANIES_BY_STATUS,
                    new Object[]{statusId},
                    (rs, rowNum) -> CompanyResponseDTO.builder()
                            .id(rs.getInt("ID"))
                            .name(rs.getString("NAME"))
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar empresas por status: " + e.getMessage(), e);
        }
    }

    public void linkCompanyModulo(int companyId, int moduloId, int status) {
        try {
            jdbcTemplate.update(SQL_LINK_COMPANY_MODULO, companyId, moduloId, status);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao vincular empresa ao módulo: " + e.getMessage(), e);
        }
    }

    public List<CompanyModuloResponseDTO> getCompanyModulos() {
        try {
            return jdbcTemplate.query(
                    SQL_GET_COMPANY_MODULOS,
                    (rs, rowNum) -> CompanyModuloResponseDTO.builder()
                            .id(rs.getInt("id"))
                            .companyId(rs.getInt("company_id"))
                            .companyName(rs.getString("name"))
                            .moduloId(rs.getInt("modulo_id"))
                            .moduloName(rs.getString("nome"))
                            .status(rs.getInt("status"))
                            .statusDescription(rs.getString("descricao"))
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar vínculos de empresas e módulos: " + e.getMessage(), e);
        }
    }


}