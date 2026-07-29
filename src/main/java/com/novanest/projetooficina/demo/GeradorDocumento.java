package com.novanest.projetooficina.demo;

// Gera CPF/CNPJ com digito verificador valido (o Hibernate valida
// @CPF/@CNPJ automaticamente ao salvar a entidade, mesmo fora do fluxo
// normal da API), a partir de uma base numerica sequencial.
class GeradorDocumento {

    static String cpf(long base) {
        int[] d = new int[9];
        String baseStr = String.format("%09d", base % 1_000_000_000L);
        for (int i = 0; i < 9; i++) d[i] = baseStr.charAt(i) - '0';

        int dv1 = digitoVerificador(d, 10);
        int[] d10 = adicionar(d, dv1);
        int dv2 = digitoVerificador(d10, 11);

        StringBuilder sb = new StringBuilder(baseStr);
        sb.append(dv1).append(dv2);
        return sb.toString();
    }

    static String cnpj(long base) {
        int[] d = new int[12];
        String baseStr = String.format("%012d", base % 1_000_000_000_000L);
        for (int i = 0; i < 12; i++) d[i] = baseStr.charAt(i) - '0';

        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int dv1 = digitoVerificadorPesos(d, pesos1);
        int[] d13 = adicionar(d, dv1);

        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int dv2 = digitoVerificadorPesos(d13, pesos2);

        return baseStr + dv1 + dv2;
    }

    private static int digitoVerificador(int[] digitos, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (int digito : digitos) {
            soma += digito * peso;
            peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static int digitoVerificadorPesos(int[] digitos, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < digitos.length; i++) {
            soma += digitos[i] * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static int[] adicionar(int[] original, int novo) {
        int[] resultado = new int[original.length + 1];
        System.arraycopy(original, 0, resultado, 0, original.length);
        resultado[original.length] = novo;
        return resultado;
    }
}
