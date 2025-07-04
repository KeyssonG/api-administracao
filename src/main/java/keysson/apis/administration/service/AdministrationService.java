package keysson.apis.administration.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import keysson.apis.administration.dto.AlteraStatusEvent;
import keysson.apis.administration.dto.response.EmpresaPendenteDTO;
import keysson.apis.administration.dto.response.EmpresasStatusDTO;
import keysson.apis.administration.exception.BusinessRuleException;
import keysson.apis.administration.repository.AdministrationRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministrationService {

    @PersistenceContext
    private EntityManager entityManager;

    private AdministrationRepository administrationRepository;

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public AdministrationService(AdministrationRepository administrationRepository, RabbitTemplate rabbitTemplate) {
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

}
