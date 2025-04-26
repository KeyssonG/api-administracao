package keysson.apis.administration.controller;

import keysson.apis.administration.dto.response.EmpresaPendenteDTO;
import keysson.apis.administration.exception.BusinessRuleException;
import keysson.apis.administration.service.AdministrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdministrationControllerImpl implements AdministrationController{

    private final AdministrationService administrationService;

    @Autowired
    public AdministrationControllerImpl(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Override
    public ResponseEntity<List<EmpresaPendenteDTO>> getPendingCompany(Integer numeroConta) throws BusinessRuleException {
        return administrationService.pendingCompany(numeroConta);
    }
}
