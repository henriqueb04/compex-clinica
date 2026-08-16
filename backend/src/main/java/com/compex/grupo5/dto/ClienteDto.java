package com.compex.grupo5.dto;

import com.compex.grupo5.model.Cliente;
import com.compex.grupo5.misc.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.compex.grupo5.model.Cliente}
 */
public record ClienteDto(

        @NotNull
        @Pattern(
                regexp = "^\\d{11}$",
                message = "Formato de CPF inválido"
        )
        String cpf,

        @NotBlank(message = "Nome completo é obrigatório")
        @Size(min = 3, message = "Nome deve possuir pelo menos 3 caracteres")
        String nomeCompleto,

        @NotNull(message = "Data de nascimento é obrigatória")
        LocalDate dataNascimento,

        @NotNull(message = "Sexo é obrigatório")
        Sexo sexo,

        @NotBlank(message = "Endereço é obrigatório")
        String endereco,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(
                regexp = "^\\d{11}$",
                message = "Telefone deve possuir exatamente 11 dígitos"
        )
        String telefone

) implements Serializable {

    public Cliente toEntity() {
        Cliente cliente = new Cliente();

        cliente.setCpf(this.cpf);
        cliente.setNomeCompleto(this.nomeCompleto);
        cliente.setDataNascimento(this.dataNascimento);
        cliente.setSexo(this.sexo);
        cliente.setEndereco(this.endereco);
        cliente.setTelefone(this.telefone);

        return cliente;
    }

    public static ClienteDto fromEntity(Cliente cliente) {
        return new ClienteDto(
                cliente.getCpf(),
                cliente.getNomeCompleto(),
                cliente.getDataNascimento(),
                cliente.getSexo(),
                cliente.getEndereco(),
                cliente.getTelefone()
        );
    }
}

