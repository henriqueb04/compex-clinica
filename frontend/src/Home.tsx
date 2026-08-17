import { Center, Stack, Title, Text } from "@mantine/core";

function Home() {
  return (
    <Center w={800} h={600}>
      <Stack>
        <Title order={1} ta="center">
          Sistema de agendamento
        </Title>
        <Text ta="center">
          Clique no menu no canto superior esquerdo para ver opções
        </Text>
      </Stack>
    </Center>
  );
}

export default Home;
