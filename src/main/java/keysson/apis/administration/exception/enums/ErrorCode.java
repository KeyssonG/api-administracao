package keysson.apis.administration.exception.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
        ERROR_CADASTRO_DEPARTAMENTO("Erro ao cadastrar departamento", HttpStatus.BAD_REQUEST),
        ERROR_BUSCAR_DEPARTAMENTO("Não foi encontrado departamentos parametrizados para está empresa", HttpStatus.NOT_FOUND),
        ERROR_DELETAR_DEPARTAMENTO("Erro ao deletar departamento", HttpStatus.BAD_REQUEST),
    ;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
