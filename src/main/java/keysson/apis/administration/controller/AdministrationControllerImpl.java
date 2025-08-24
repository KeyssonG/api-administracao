package keysson.apis.administration.controller;

import keysson.apis.administration.dto.request.RequestAlteraStatusConta;
import keysson.apis.administration.dto.request.RequestCadastrarDepartamento;
import keysson.apis.administration.dto.request.RequestDeletarDepartamento;
import keysson.apis.administration.dto.response.EmpresasStatusDTO;
import keysson.apis.administration.dto.response.PendingCompanyDTO;
import keysson.apis.administration.dto.response.ResponseDepartamento;
import keysson.apis.administration.exception.BusinessRuleException;
import keysson.apis.administration.service.AdministrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<List<PendingCompanyDTO>> getPendingCompany(int accountNumber) throws BusinessRuleException {
        return administrationService.pendingCompany(accountNumber);
    }

    @Override
    public void putStatusAccount(int accountNumber, RequestAlteraStatusConta requestBody) throws BusinessRuleException {
        administrationService.changeStatus(requestBody.getNewStatus(), accountNumber);
    }

    @Override
    public EmpresasStatusDTO getStatusCompanies() throws BusinessRuleException {
        return administrationService.StatusCompanies();
    }

    @Override
    public void postDepartment(@RequestBody RequestCadastrarDepartamento requestBody) throws BusinessRuleException {
        administrationService.registerDepartment(requestBody);
    }

    @Override
    public List<ResponseDepartamento> getAllDepartments() throws BusinessRuleException {
        return administrationService.searchAllDepartments();
    }

    @Override
    public void deleteDepartmentById(@RequestBody RequestDeletarDepartamento requestBody) throws BusinessRuleException {
        administrationService.deleteDepartment(requestBody);
    }


}
