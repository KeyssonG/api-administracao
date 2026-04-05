package keysson.apis.administration.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import keysson.apis.administration.Utils.JwtUtil;
import keysson.apis.administration.dto.ChangeStatusEvent;
import keysson.apis.administration.dto.request.CreateDepartmentRequest;
import keysson.apis.administration.dto.request.DeleteDepartmentRequest;
import keysson.apis.administration.dto.request.LinkCompanyModuloRequest;
import keysson.apis.administration.dto.response.CompanyModuloDTO;
import keysson.apis.administration.dto.response.CompanyModuloResponseDTO;
import keysson.apis.administration.dto.response.CompanyResponseDTO;
import keysson.apis.administration.dto.response.CompanyStatusDTO;
import keysson.apis.administration.dto.response.ModuloResponseDTO;
import keysson.apis.administration.dto.response.PendingCompanyDTO;
import keysson.apis.administration.dto.response.DepartmentResponse;
import keysson.apis.administration.exception.BusinessRuleException;
import keysson.apis.administration.repository.AdministrationRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static keysson.apis.administration.exception.enums.ErrorCode.*;

@Service
public class AdministrationService {

    @PersistenceContext
    private EntityManager entityManager;

    private AdministrationRepository administrationRepository;

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest httpRequest;

    @Autowired
    public AdministrationService(AdministrationRepository administrationRepository, RabbitTemplate rabbitTemplate, JwtUtil jwtUtil) {
        this.administrationRepository = administrationRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public ResponseEntity<List<PendingCompanyDTO>> pendingCompany(int conta) throws BusinessRuleException {

        List<PendingCompanyDTO> empresasStatusPendente = administrationRepository.findPendingCompanies(conta);
        return ResponseEntity.ok(empresasStatusPendente);
    };

    public void changeStatus(int newStatus, int conta) throws BusinessRuleException {

        try {
            administrationRepository.newAccontStatus(newStatus, conta);

            ChangeStatusEvent event = new ChangeStatusEvent(
                    conta,
                    newStatus
            );
            rabbitTemplate.convertAndSend("alteraStatus.fila", event);

        } catch (Exception ex) {
            throw new RuntimeException("Erro ao alterar o status.");
        }

    }

    public CompanyStatusDTO StatusCompanies() {
        return administrationRepository.findStatusCompany();
    }

    public void registerDepartment(CreateDepartmentRequest requestBody) throws BusinessRuleException {

        String token =(String)httpRequest.getAttribute("CleanJwt");

        Integer idEmpresa = jwtUtil.extractCompanyId(token);

        if (idEmpresa == null) {
            throw new IllegalArgumentException("ID da empresa não encontrado no token.");
        }

        if (requestBody.getNomeDepartamento() == null || requestBody.getNomeDepartamento().isEmpty()) {
            throw new IllegalArgumentException("O nome do departamento não pode ser vazio.");
        }

        try {
            administrationRepository.registerNewDepartment(idEmpresa, requestBody.getNomeDepartamento());
        } catch (Exception e) {
            throw new BusinessRuleException(ERROR_CADASTRO_DEPARTAMENTO);
        }
    }

    public List<DepartmentResponse> searchAllDepartments() throws BusinessRuleException {
        String token = (String) httpRequest.getAttribute("CleanJwt");

        Integer idEmpresa = jwtUtil.extractCompanyId(token);

        if (idEmpresa == null) {
            throw new IllegalArgumentException("ID da empresa não encontrado no token.");
        }

        try {
            return administrationRepository.getDepartmentsByCompany(idEmpresa);
        } catch (Exception e) {
            throw new BusinessRuleException(ERROR_BUSCAR_DEPARTAMENTO);
        }
    }

    public void deleteDepartment(DeleteDepartmentRequest request) throws BusinessRuleException {
        try {
            administrationRepository.deleteDepartmentById(request.getIdDepartamento());
        } catch (Exception e) {
            throw new BusinessRuleException(ERROR_DELETAR_DEPARTAMENTO);
        }
    }

    public List<ModuloResponseDTO> listAllModulos(Integer id) throws BusinessRuleException {
        try {
            return administrationRepository.getAllModulos(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar módulos");
        }
    }

    public List<CompanyResponseDTO> getCompaniesByStatus(int statusId) {
        return administrationRepository.findCompaniesByStatus(statusId);
    }

    public void linkCompanyModulo(LinkCompanyModuloRequest requestBody) throws BusinessRuleException {
        try {
            System.out.println("VINCULANDO EMPRESA: " + requestBody.getCompanyId() + " AO MODULO: " + requestBody.getModuloId() + " COM STATUS: " + requestBody.getStatus());
            administrationRepository.linkCompanyModulo(requestBody.getCompanyId(), requestBody.getModuloId(), requestBody.getStatus());
        } catch (Exception e) {
            throw new BusinessRuleException(ERROR_VINCULAR_EMPRESA_MODULO);
        }
    }

    public List<CompanyModuloResponseDTO> getCompanyModulos() {
        return administrationRepository.getCompanyModulos();
    }

    public List<CompanyModuloDTO> listModulosByCompany() {
        String token = (String) httpRequest.getAttribute("CleanJwt");
        Integer idEmpresa = jwtUtil.extractCompanyId(token);

        if (idEmpresa == null) {
            throw new IllegalArgumentException("ID da empresa não encontrado no token.");
        }

        return administrationRepository.getModulosByCompanyId(idEmpresa);
    }

}
