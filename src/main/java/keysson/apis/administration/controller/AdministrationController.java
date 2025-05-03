package keysson.apis.administration.controller;

import io.swagger.v3.oas.annotations.Operation;

import keysson.apis.administration.dto.request.RequestAlteraStatusConta;
import keysson.apis.administration.dto.response.EmpresaPendenteDTO;
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
    ResponseEntity<List<EmpresaPendenteDTO>> getPendingCompany(
            @RequestParam(required = false, defaultValue = "0") int numeroConta
    )throws BusinessRuleException;

    @PutMapping("/status/conta")
    @Operation(
            summary = "Altera o Status da Conta.",
            description = "Endpoint para atualizar o Status da Conta"
    )
    void putStatusAccount(
            @RequestParam(required = false, defaultValue = "0") int numeroConta,
            @RequestBody RequestAlteraStatusConta requestBody
    ) throws BusinessRuleException;
}
