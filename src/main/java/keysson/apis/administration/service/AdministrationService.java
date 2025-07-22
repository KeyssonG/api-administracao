package keysson.apis.administration.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import keysson.apis.administration.dto.AlteraStatusEvent;
import keysson.apis.administration.dto.request.RequestCadastrarDepartamento;
import keysson.apis.administration.dto.response.EmpresaPendenteDTO;
import keysson.apis.administration.dto.response.EmpresasStatusDTO;
import keysson.apis.administration.dto.response.ResponseDepartamento;
import keysson.apis.administration.exception.BusinessRuleException;
import keysson.apis.administration.repository.AdministrationRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static keysson.apis.administration.exception.enums.ErrorCode.ERROR_BUSCAR_DEPARTAMENTO;
import static keysson.apis.administration.exception.enums.ErrorCode.ERROR_CADASTRO_DEPARTAMENTO;

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

    public ResponseEntity<List<EmpresaPendenteDTO>> pendingCompany(int conta) throws BusinessRuleException {

        List<EmpresaPendenteDTO> empresasStatusPendente = administrationRepository.findPendingCompanies(conta);
        return ResponseEntity.ok(empresasStatusPendente);
    };

    public void changeStatus(int newStatus, int conta) throws BusinessRuleException {

        try {
            administrationRepository.newAccontStatus(newStatus, conta);

            AlteraStatusEvent event = new AlteraStatusEvent(
                    conta,
                    newStatus
            );
            rabbitTemplate.convertAndSend("alteraStatus.fila", event);

        } catch (Exception ex) {
            throw new RuntimeException("Erro ao alterar o status.");
        }

    }

    public EmpresasStatusDTO StatusCompanies() {
        return administrationRepository.findStatusCompany();
    }

    public void registerDepartment(RequestCadastrarDepartamento requestBody) throws BusinessRuleException {

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

    public List<ResponseDepartamento> searchAllDepartments() throws BusinessRuleException {
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

}
