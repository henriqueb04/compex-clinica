import { useQuery } from "@tanstack/react-query";
import api from "./api";
import { List } from "@mantine/core";

const fetchAgendamentos = async () => {
  const response = await api.get("/listagem");
  return response.data;
};

function Listagem() {
  const { data, isLoading, error } = useQuery({
    queryKey: ["listagem"],
    queryFn: fetchAgendamentos,
  });

  if (isLoading) {
    return "Buscando agendamentos...";
  }

  if (error) {
    return `Erro ao buscar agendamentos: ${error.message}`;
  }

  return (
    <List>
      {data.map((consulta) => {
        return <List.Item>{consulta.id}</List.Item>;
      })}
    </List>
  );
}

export default Listagem;
