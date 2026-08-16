package com.compex.grupo5.dto;

import com.compex.grupo5.misc.Especialidade;
import com.compex.grupo5.misc.Sexo;
import com.compex.grupo5.model.Profissional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.compex.grupo5.model.Profissional}
 */
public record ProfissionalDto(

        @NotNull
        @Pattern(
                regexp = "^\\d{11}$",
                message = "Formato de CPF inválido"
        )
        String cpf,

        @NotBlank(message = "Nome completo é obrigatório")
        @Size(
                min = 3,
                message = "Nome deve possuir pelo menos 3 caracteres"
        )
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
                message = "Telefone deve possuir 11 dígitos"
        )
        String telefone,

        @NotBlank(message = "CRM é obrigatório")
        String crm,

        @NotNull(message = "Especialidade é obrigatória")
        Especialidade especialidade,

        @NotNull(message = "Tempo médio de consulta é obrigatório")
        @Min(
                value = 1,
                message = "Tempo médio de consulta deve ser maior que zero"
        )
        Integer tempoMedioConsulta

) implements Serializable {

    public Profissional toEntity() {
        Profissional profissional = new Profissional();

        profissional.setCpf(this.cpf);
        profissional.setNomeCompleto(this.nomeCompleto);
        profissional.setDataNascimento(this.dataNascimento);
        profissional.setSexo(this.sexo);
        profissional.setEndereco(this.endereco);
        profissional.setTelefone(this.telefone);

        profissional.setCrm(this.crm);
        profissional.setEspecialidade(this.especialidade);
        profissional.setTempoMedioConsulta(this.tempoMedioConsulta);

        return profissional;
    }

    public static ProfissionalDto fromEntity(Profissional profissional) {
        return new ProfissionalDto(
                profissional.getCpf(),
                profissional.getNomeCompleto(),
                profissional.getDataNascimento(),
                profissional.getSexo(),
                profissional.getEndereco(),
                profissional.getTelefone(),
                profissional.getCrm(),
                profissional.getEspecialidade(),
                profissional.getTempoMedioConsulta()
        );
    }
}
