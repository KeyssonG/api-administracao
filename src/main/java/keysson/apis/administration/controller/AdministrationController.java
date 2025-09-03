package keysson.apis.administration.controller;

import io.swagger.v3.oas.annotations.Operation;
import keysson.apis.administration.dto.request.ChangeAccountStatusRequest;
import keysson.apis.administration.dto.request.CreateDepartmentRequest;
import keysson.apis.administration.dto.request.DeleteDepartmentRequest;
import keysson.apis.administration.dto.response.CompanyStatusDTO;
import keysson.apis.administration.dto.response.DepartmentResponse;
import keysson.apis.administration.dto.response.PendingCompanyDTO;
import keysson.apis.administration.exception.BusinessRuleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/administracao")
public interface AdministrationController {

    @GetMapping("/empresa/pendente/")
    @Operation(
            summary = "Busca empresas com status pendente.",
            description = "Endpoint para verificar empresas pendentes"
    )
    ResponseEntity<List<PendingCompanyDTO>> getPendingCompany(
            @RequestParam(required = false, defaultValue = "0") int numeroConta
    ) throws BusinessRuleException;

    @PutMapping("/status/conta")
    @Operation(
            summary = "Altera o Status da Conta.",
            description = "Endpoint para atualizar o Status da Conta"
    )
    void putStatusAccount(
            @RequestParam(required = false, defaultValue = "0") int numeroConta,
            @RequestBody ChangeAccountStatusRequest requestBody
    ) throws BusinessRuleException;


    @GetMapping("/empresa/status")
    @Operation(
            summary = "Busca empresas com status pendente.",
            description = "Endpoint para verificar empresas pendentes"
    )
    CompanyStatusDTO getStatusCompanies(
    ) throws BusinessRuleException;

    @PostMapping("/departamento")
    @Operation(
            summary = "Cria um novo departamento.",
            description = "Endpoint para criar um novo departamento"
    )
    void postDepartment(
            @RequestBody CreateDepartmentRequest requestBody
    ) throws BusinessRuleException;

    @GetMapping("/departamento")
    @Operation(
            summary = "Busca todos os departamentos por empresa.",
            description = "Endpoint para buscar todos os departamentos"
    )
    List<DepartmentResponse> getAllDepartments(
    ) throws BusinessRuleException;

    @DeleteMapping("/departamento")
    @Operation(
            summary = "Deleta um departamento por ID.",
            description = "Endpoint para deletar um departamento por ID"
    )
    void deleteDepartmentById(
            @RequestBody DeleteDepartmentRequest requestBody
    ) throws BusinessRuleException;
}
