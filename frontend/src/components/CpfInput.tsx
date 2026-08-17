import { useState } from "react";
import { TextInput, type TextInputProps } from "@mantine/core";

function CpfInput({
  value,
  setValue,
  ...props
}: {
  value: string;
  setValue: (value: string) => unknown;
} & TextInputProps) {
  const formatarCpf = () => {
    const digitos = [...value.replaceAll(/\D/g, "")];
    if (digitos.length > 11) {
      return;
    }
    const formatado = [];
    for (let i = 0; i < 9 && i < digitos.length; i++) {
      formatado.push(digitos[i]);
      if ((i + 1) % 3 == 0 && i != 8) {
        formatado.push(".");
      }
    }
    if (digitos.length > 9) {
      formatado.push("-");
      formatado.push(...digitos.slice(9));
    }
    setValue(formatado.join(""));
  };

  return (
    <TextInput
      value={value}
      onChange={(event) => setValue(event.target.value)}
      onBlur={formatarCpf}
      {...props}
    />
  );
}

export default CpfInput;
