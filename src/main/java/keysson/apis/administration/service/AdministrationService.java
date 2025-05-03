package keysson.apis.administration.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import keysson.apis.administration.dto.response.EmpresaPendenteDTO;
import keysson.apis.administration.exception.BusinessRuleException;
import keysson.apis.administration.repository.AdministrationRepository;
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
    public AdministrationService(AdministrationRepository administrationRepository) {
        this.administrationRepository = administrationRepository;
    }

    public ResponseEntity<List<EmpresaPendenteDTO>> pendingCompany(int conta) throws BusinessRuleException {

        List<EmpresaPendenteDTO> empresasStatusPendente = administrationRepository.findPendingCompanies(conta);
        return ResponseEntity.ok(empresasStatusPendente);
    };

    public void changeStatus(int newStatus, int conta) throws BusinessRuleException {
        administrationRepository.newAccontStatus(newStatus, conta);
    }

}
