import { Combobox, InputBase, useCombobox, Highlight } from "@mantine/core";
import { useQuery } from "@tanstack/react-query";
import api from "../api";
import { useEffect, useState } from "react";
import { notifications } from "@mantine/notifications";

export interface Profissional {
  cpf: string;
  nomeCompleto: string;
  dataNascimento: string;
  sexo: string;
  endereco: string;
  crm: string;
  especialidade: string;
  tempoMedioConsulta: number;
}

function ProfissionalPicker({
  value,
  onChange,
}: {
  value: Profissional | null;
  onChange: (newValue: Profissional | null) => unknown;
}) {
  const { data, isLoading, isError, error } = useQuery({
    queryKey: ["fetch_profissional"],
    queryFn: async () => {
      return (await api.get("/profissionais")).data as Profissional[];
    },
  });

  useEffect(() => {
    if (isError) {
      notifications.show({
        title: "Erro",
        message: "Não foi possível encontrar os profissionais disponíveis.",
        color: "red",
      });
    }
  }, [isError, error]);

  const [search, setSearch] = useState<string>("");

  const shouldFilter = data && data.every((p) => p.nomeCompleto !== search);
  const filteredOptions =
    (shouldFilter
      ? data?.filter((p) =>
          p.nomeCompleto.toLowerCase().includes(search.toLowerCase().trim()),
        )
      : data) ?? [];

  const combobox = useCombobox({
    onDropdownClose: () => combobox.resetSelectedOption(),
  });

  const options = filteredOptions.map((p) => (
    <Combobox.Option value={p.cpf} key={p.cpf}>
      <Highlight highlight={search} size="sm">
        {p.nomeCompleto}
      </Highlight>
    </Combobox.Option>
  ));

  return (
    <Combobox
      store={combobox}
      onOptionSubmit={(cpf) => {
        if (data) {
          const p = data.find((p) => p.cpf == cpf);
          onChange(p ?? null);
          setSearch(p?.nomeCompleto ?? "");
        }
      }}
    >
      <Combobox.Target>
        <InputBase
          rightSection={<Combobox.Chevron />}
          value={search}
          onChange={(event) => {
            combobox.openDropdown();
            combobox.updateSelectedOptionIndex();
            setSearch(event.target.value);
          }}
          onClick={() => combobox.openDropdown()}
          onFocus={() => combobox.openDropdown()}
          onBlur={() => {
            combobox.closeDropdown();
            setSearch(value?.nomeCompleto || "");
          }}
          placeholder={isLoading ? "Carregando..." : "Profissional..."}
          rightSectionPointerEvents="none"
        />
      </Combobox.Target>
      <Combobox.Dropdown>
        <Combobox.Options>
          {options.length > 0 ? (
            options
          ) : (
            <Combobox.Empty>
              {isLoading ? "Carregando..." : "Sem resultados..."}
            </Combobox.Empty>
          )}
        </Combobox.Options>
      </Combobox.Dropdown>
    </Combobox>
  );
}

export default ProfissionalPicker;
