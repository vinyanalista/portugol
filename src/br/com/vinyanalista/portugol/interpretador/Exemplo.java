package br.com.vinyanalista.portugol.interpretador;

public enum Exemplo {

	ESTRUTURA_SEQUENCIAL {
		@Override
		public String getNome() {
			return "Estrutura sequencial";
		}

		@Override
		public String getProgramaFonte() {
			StringBuilder programaFonte = new StringBuilder();
			
			programaFonte.append("// Faça um algoritmo para mostrar o resultado da multiplicação de dois\n");
			programaFonte.append("// números.\n\n");
			programaFonte.append("algoritmo\n");
			programaFonte.append("declare n1, n2, m numerico\n");
			programaFonte.append("escreva \"Digite dois números:\"\n");
			programaFonte.append("leia n1, n2\n");
			programaFonte.append("m <- n1 * n2\n");
			programaFonte.append("escreva \"Multiplicação = \", m\n");
			programaFonte.append("fim_algoritmo.\n\n");
			programaFonte.append("// Adaptado de:\n");
			programaFonte.append("// ASCENCIO, A. F. G.; CAMPOS, E. A. V. Fundamentos da programação de\n");
			programaFonte.append("// computadores. 2a. ed. São Paulo: Pearson Prentice Hall, 2007. p. 4-5.");

			return programaFonte.toString();
		}
	}, ESTRUTURA_CONDICIONAL_SIMPLES {
		@Override
		public String getNome() {
			return "Estrutura condicional simples";
		}

		@Override
		public String getProgramaFonte() {
			StringBuilder programaFonte = new StringBuilder();
			
			programaFonte.append("// Faça um programa que receba dois números e mostre o maior.\n\n");
			programaFonte.append("algoritmo\n");
			programaFonte.append("declare num1, num2 numerico\n");
			programaFonte.append("escreva \"Digite o primeiro número:\"\n");
			programaFonte.append("leia num1\n");
			programaFonte.append("escreva \"Digite o segundo número:\"\n");
			programaFonte.append("leia num2\n");
			programaFonte.append("se num1 > num2 entao\n");
			programaFonte.append("\tescreva \"O maior número é: \", num1\n");
			programaFonte.append("se num2 > num1 entao\n");
			programaFonte.append("\tescreva \"O maior número é: \", num2\n");
			programaFonte.append("se num1 = num2 entao\n");
			programaFonte.append("\tescreva \"Os números são iguais\"\n");
			programaFonte.append("fim_algoritmo.\n\n");
			programaFonte.append("// Adaptado de:\n");
			programaFonte.append("// ASCENCIO, A. F. G.; CAMPOS, E. A. V. Fundamentos da programação de\n");
			programaFonte.append("// computadores. 2a. ed. São Paulo: Pearson Prentice Hall, 2007. p. 60.");

			return programaFonte.toString();
		}
	}, ESTRUTURA_CONDICIONAL_COMPOSTA {
		@Override
		public String getNome() {
			return "Estrutura condicional composta";
		}

		@Override
		public String getProgramaFonte() {
			StringBuilder programaFonte = new StringBuilder();
			
			programaFonte.append("// Faça um programa que receba um número inteiro e verifique se é par ou\n");
			programaFonte.append("// ímpar.\n\n");
			programaFonte.append("algoritmo\n");
			programaFonte.append("declare num, r numerico\n");
			programaFonte.append("escreva \"Digite um número:\"\n");
			programaFonte.append("leia num\n");
			programaFonte.append("r <- resto(num, 2)\n");
			programaFonte.append("se r = 0 entao\n");
			programaFonte.append("\tescreva \"O número é par\"\n");
			programaFonte.append("senao\n");
			programaFonte.append("\tescreva \"O número é ímpar\"\n");
			programaFonte.append("fim_algoritmo.\n\n");
			programaFonte.append("// Adaptado de:\n");
			programaFonte.append("// ASCENCIO, A. F. G.; CAMPOS, E. A. V. Fundamentos da programação de\n");
			programaFonte.append("// computadores. 2a. ed. São Paulo: Pearson Prentice Hall, 2007. p. 62.");

			return programaFonte.toString();
		}
	}, ESTRUTURA_DE_REPETICAO_PARA {
		@Override
		public String getNome() {
			return "Estrutura de repetição PARA";
		}

		@Override
		public String getProgramaFonte() {
			StringBuilder programaFonte = new StringBuilder();
			
			programaFonte.append("// Faça um programa que leia um valor N inteiro e positivo, calcule e\n");
			programaFonte.append("// mostre o valor de E, conforme a fórmula a seguir:\n");
			programaFonte.append("//\n");
			programaFonte.append("// E = 1 + 1/1! + 1/2! + 1/3! + ... + 1/N!\n\n");
			programaFonte.append("algoritmo\n");
			programaFonte.append("declare n, euler, i, j, fat numerico\n");
			programaFonte.append("leia n\n");
			programaFonte.append("euler <- 1\n");
			programaFonte.append("para i <- 1 ate n faca\n");
			programaFonte.append("inicio\n");
			programaFonte.append("\tfat <- 1\n");
			programaFonte.append("\tpara j <- 1 ate i faca\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tfat <- fat * j\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("\teuler <- euler + 1/fat\n");
			programaFonte.append("fim\n");
			programaFonte.append("escreva euler\n");
			programaFonte.append("fim_algoritmo.\n\n");
			programaFonte.append("// Adaptado de:\n");
			programaFonte.append("// ASCENCIO, A. F. G.; CAMPOS, E. A. V. Fundamentos da programação de\n");
			programaFonte.append("// computadores. 2a. ed. São Paulo: Pearson Prentice Hall, 2007. p. 109.");

			return programaFonte.toString();
		}
	}, ESTRUTURA_DE_REPETICAO_ENQUANTO {
		@Override
		public String getNome() {
			return "Estrutura de repetição ENQUANTO";
		}

		@Override
		public String getProgramaFonte() {
			StringBuilder programaFonte = new StringBuilder();
			
			programaFonte.append("// Faça um programa que leia um conjunto não determinado de valores, um de\n");
			programaFonte.append("// cada vez, e escreva uma tabela com cabeçalho, que deve ser repetido a\n");
			programaFonte.append("// cada vinte linhas. A tabela deverá conter o valor lido, seu quadrado,\n");
			programaFonte.append("// seu cubo e sua raiz quadrada. Finalize a entrada de dados com um valor\n");
			programaFonte.append("// negativo ou zero.\n\n");
			programaFonte.append("algoritmo\n");
			programaFonte.append("declare linhas, num, quad, cubo, raiz numerico\n");
			programaFonte.append("leia num\n");
			programaFonte.append("escreva \"Valor - Quadrado - Cubo - Raiz\"\n");
			programaFonte.append("linhas <- 1\n");
			programaFonte.append("enquanto num > 0 faca\n");
			programaFonte.append("inicio\n");
			programaFonte.append("\tquad <- num * num\n");
			programaFonte.append("\tcubo <- num * num * num\n");
			programaFonte.append("\traiz <- raiz_quadrada(num)\n");
			programaFonte.append("\tse linhas < 20 entao\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tlinhas <- linhas + 1\n");
			programaFonte.append("\t\tescreva num, \" - \", quad, \" - \", cubo, \" - \", raiz\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("\tsenao\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tlimpar_tela()\n");
			programaFonte.append("\t\tlinhas <- 1\n");
			programaFonte.append("\t\tescreva \"Valor - Quadrado - Cubo - Raiz\"\n");
			programaFonte.append("\t\tlinhas <- linhas + 1\n");
			programaFonte.append("\t\tescreva num, \" - \", quad, \" - \", cubo, \" - \", raiz\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("\tleia num\n");
			programaFonte.append("fim\n");
			programaFonte.append("fim_algoritmo.\n\n");
			programaFonte.append("// Adaptado de:\n");
			programaFonte.append("// ASCENCIO, A. F. G.; CAMPOS, E. A. V. Fundamentos da programação de\n");
			programaFonte.append("// computadores. 2a. ed. São Paulo: Pearson Prentice Hall, 2007. p. 129.");

			return programaFonte.toString();
		}
	}, ESTRUTURA_DE_REPETICAO_REPITA {
		@Override
		public String getNome() {
			return "Estrutura de repetição REPITA";
		}
		
		@Override
		public String getProgramaFonte() {
			StringBuilder programaFonte = new StringBuilder();
			
			programaFonte.append("// Faça um programa que conte regressivamente de 10 até 1.\n\n");
			programaFonte.append("algoritmo\n");
			programaFonte.append("declare contador numerico\n");
			programaFonte.append("contador <- 10\n");
			programaFonte.append("repita\n");
			programaFonte.append("\tescreva contador\n");
			programaFonte.append("\tcontador <- contador - 1\n");
			programaFonte.append("ate contador = 0\n");
			programaFonte.append("escreva \"Fim!\"\n");
			programaFonte.append("fim_algoritmo.");
		
			return programaFonte.toString();
		}
	}, VETORES {
		@Override
		public String getNome() {
			return "Vetores";
		}

		@Override
		public String getProgramaFonte() {
			StringBuilder programaFonte = new StringBuilder();
			
			programaFonte.append("// Faça um programa que preencha um vetor com nove números inteiros,\n");
			programaFonte.append("// calcule e mostre os números primos e suas respectivas posições.\n\n");
			programaFonte.append("algoritmo\n");
			programaFonte.append("declare\n");
			programaFonte.append("\tnum[9] numerico\n");
			programaFonte.append("\ti, j, cont numerico\n");
			programaFonte.append("para i <- 1 ate 9 faca\n");
			programaFonte.append("inicio\n");
			programaFonte.append("\tleia num[i]\n");
			programaFonte.append("fim\n");
			programaFonte.append("para i <- 1 ate 9 faca\n");
			programaFonte.append("inicio\n");
			programaFonte.append("\tcont <- 0\n");
			programaFonte.append("\tpara j <- 1 ate num[i] faca\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tse resto(num[i], j) = 0 entao\n");
			programaFonte.append("\t\t\tcont <- cont + 1\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("\tse cont <= 2 entao\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tescreva num[i], \" - \", i\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("fim\n");
			programaFonte.append("fim_algoritmo.\n\n");
			programaFonte.append("// Adaptado de:\n");
			programaFonte.append("// ASCENCIO, A. F. G.; CAMPOS, E. A. V. Fundamentos da programação de\n");
			programaFonte.append("// computadores. 2a. ed. São Paulo: Pearson Prentice Hall, 2007. p. 151.");

			return programaFonte.toString();
		}
	}, MATRIZES {
		@Override
		public String getNome() {
			return "Matrizes";
		}

		@Override
		public String getProgramaFonte() {
			StringBuilder programaFonte = new StringBuilder();
			
			programaFonte.append("// Faça um programa que preencha uma matriz M(2x2), calcule e mostre a\n");
			programaFonte.append("// matriz R, resultante da multiplicação dos elementos de M pelo seu maior\n");
			programaFonte.append("// elemento.\n\n");
			programaFonte.append("algoritmo\n");
			programaFonte.append("declare mat[2,2], resultado[2,2], i, j, maior numerico\n");
			programaFonte.append("para i <- 1 ate 2 faca\n");
			programaFonte.append("inicio\n");
			programaFonte.append("\tpara j <- 1 ate 2 faca\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tleia mat[i,j]\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("fim\n");
			programaFonte.append("maior <- mat[1,1]\n");
			programaFonte.append("para i <- 1 ate 2 faca\n");
			programaFonte.append("inicio\n");
			programaFonte.append("\tpara j <- 1 ate 2 faca\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tse mat[i,j] > maior entao\n");
			programaFonte.append("\t\t\tmaior <- mat[i,j]\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("fim\n");
			programaFonte.append("para i <- 1 ate 2 faca\n");
			programaFonte.append("inicio\n");
			programaFonte.append("\tpara j <- 1 ate 2 faca\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tresultado[i,j] <- maior * mat[i,j]\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("fim\n");
			programaFonte.append("para i <- 1 ate 2 faca\n");
			programaFonte.append("inicio\n");
			programaFonte.append("\tpara j <- 1 ate 2 faca\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tescreva resultado[i,j]\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("fim\n");
			programaFonte.append("fim_algoritmo.\n\n");
			programaFonte.append("// Adaptado de:\n");
			programaFonte.append("// ASCENCIO, A. F. G.; CAMPOS, E. A. V. Fundamentos da programação de\n");
			programaFonte.append("// computadores. 2a. ed. São Paulo: Pearson Prentice Hall, 2007. p. 198.");

			return programaFonte.toString();
		}
	}, SUBROTINA_COM_PASSAGEM_DE_PARAMETROS_POR_REFERENCIA {
		@Override
		public String getNome() {
			return "Sub-rotina com passagem de parâmetros por referência";
		}

		@Override
		public String getProgramaFonte() {
			StringBuilder programaFonte = new StringBuilder();
			
			programaFonte.append("// Crie um programa que carregue uma matriz 3x4 com números reais. Utilize\n");
			programaFonte.append("// uma função para copiar todos os valores da matriz para um vetor de doze\n");
			programaFonte.append("// posições. Este vetor deverá ser mostrado no programa principal.\n\n");
			programaFonte.append("algoritmo\n");
			programaFonte.append("declare mat[3,4], vet[12], i, j numerico\n");
			programaFonte.append("para i <- 1 ate 3 faca\n");
			programaFonte.append("inicio\n");
			programaFonte.append("\tpara j <- 1 ate 4 faca\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tleia mat[i,j]\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("fim\n");
			programaFonte.append("gera_vetor(mat, vet)\n");
			programaFonte.append("para i <- 1 ate 12 faca\n");
			programaFonte.append("inicio\n");
			programaFonte.append("\tescreva vet[i]\n");
			programaFonte.append("fim\n");
			programaFonte.append("fim_algoritmo\n\n");
			programaFonte.append("sub-rotina gera_vetor(m[3,4], v[12] numerico)\n");
			programaFonte.append("declare i, j, k numerico\n");
			programaFonte.append("k <- 1\n");
			programaFonte.append("para i <- 1 ate 3 faca\n");
			programaFonte.append("inicio\n");
			programaFonte.append("\tpara j <- 1 ate 4 faca\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tv[k] <- m[i,j]\n");
			programaFonte.append("\t\tk <- k + 1\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("fim\n");
			programaFonte.append("fim_sub_rotina gera_vetor\n\n");
			programaFonte.append("// Adaptado de:\n");
			programaFonte.append("// ASCENCIO, A. F. G.; CAMPOS, E. A. V. Fundamentos da programação de\n");
			programaFonte.append("// computadores. 2a. ed. São Paulo: Pearson Prentice Hall, 2007. p. 265.");

			return programaFonte.toString();
		}
	}, SUBROTINA_QUE_RETORNA_VALOR {
		@Override
		public String getNome() {
			return "Sub-rotina que retorna valor";
		}

		@Override
		public String getProgramaFonte() {
			StringBuilder programaFonte = new StringBuilder();
			
			programaFonte.append("// Faça um programa contendo uma sub-rotina que retorne 1 se o número\n");
			programaFonte.append("// digitado for positivo ou 0 se for negativo.\n\n");
			programaFonte.append("algoritmo\n");
			programaFonte.append("declare num, x numerico\n");
			programaFonte.append("leia num\n");
			programaFonte.append("x <- verifica(num)\n");
			programaFonte.append("se x = 1 entao\n");
			programaFonte.append("\tescreva \"Número positivo\"\n");
			programaFonte.append("senao\n");
			programaFonte.append("\tescreva \"Número negativo\"\n");
			programaFonte.append("fim_algoritmo\n\n");
			programaFonte.append("sub-rotina verifica(num numerico)\n");
			programaFonte.append("declare res numerico\n");
			programaFonte.append("se num >= 0 entao\n");
			programaFonte.append("\tres <- 1\n");
			programaFonte.append("senao\n");
			programaFonte.append("\tres <- 0\n");
			programaFonte.append("retorne res\n");
			programaFonte.append("fim_sub_rotina verifica\n\n");
			programaFonte.append("// Adaptado de:\n");
			programaFonte.append("// ASCENCIO, A. F. G.; CAMPOS, E. A. V. Fundamentos da programação de\n");
			programaFonte.append("// computadores. 2a. ed. São Paulo: Pearson Prentice Hall, 2007. p. 246.");

			return programaFonte.toString();
		}
	}, CAPITULO_10 {
		@Override
		public String getNome() {
			return "Registro";
		}

		@Override
		public String getProgramaFonte() {
			StringBuilder programaFonte = new StringBuilder();
			
			programaFonte.append("// Faça um programa que realize o cadastro de contas bancárias com as\n");
			programaFonte.append("// seguintes informações: número da conta, nome do cliente e saldo. O\n");
			programaFonte.append("// banco permitirá o cadastramento de apenas quinze contas e não poderá\n");
			programaFonte.append("// haver mais que uma conta com o mesmo número. Crie o menu de opções a\n");
			programaFonte.append("// seguir.\n");
			programaFonte.append("// \n");
			programaFonte.append("// Menu de opções:\n");
			programaFonte.append("// 1. Cadastrar contas.\n");
			programaFonte.append("// 2. Visualizar todas as contas de determinado cliente.\n");
			programaFonte.append("// 3. Excluir a conta com menor saldo (supondo a não-existência de saldos\n");
			programaFonte.append("// iguais).\n");
			programaFonte.append("// 4. Sair.\n\n");
			programaFonte.append("algoritmo\n");
			programaFonte.append("declare\n");
			programaFonte.append("\tconta[15] registro (num, saldo numerico nome literal)\n");
			programaFonte.append("\ti, op, posi, achou, num_conta, menor_saldo numerico\n");
			programaFonte.append("\tnome_cliente literal\n");
			programaFonte.append("para i <- 1 ate 15 faca\n");
			programaFonte.append("inicio\n");
			programaFonte.append("\tconta[i].num <- 0\n");
			programaFonte.append("\tconta[i].nome <- \"\"\n");
			programaFonte.append("\tconta[i].saldo <- 0\n");
			programaFonte.append("fim\n");
			programaFonte.append("posi <- 1\n");
			programaFonte.append("repita\n");
			programaFonte.append("\tescreva \"Menu de opções:\"\n");
			programaFonte.append("\tescreva \"1. Cadastrar contas\"\n");
			programaFonte.append("\tescreva \"2. Visualizar todas as contas de determinado cliente\"\n");
			programaFonte.append("\tescreva \"3. Excluir a conta com menor saldo\"\n");
			programaFonte.append("\tescreva \"4. Sair\"\n");
			programaFonte.append("\tescreva \"Digite sua opção:\"\n");
			programaFonte.append("\tleia op\n");
			programaFonte.append("\tse op < 1 ou op > 4 entao\n");
			programaFonte.append("\t\tescreva \"Opção inválida!\"\n");
			programaFonte.append("\tse op = 1 entao\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tse posi > 15 entao\n");
			programaFonte.append("\t\t\tescreva \"Todas as contas já foram cadastradas!\"\n");
			programaFonte.append("\t\tsenao\n");
			programaFonte.append("\t\tinicio\n");
			programaFonte.append("\t\t\tachou <- 0\n");
			programaFonte.append("\t\t\tescreva \"Digite o número da conta a ser incluída:\"\n");
			programaFonte.append("\t\t\tleia num_conta\n");
			programaFonte.append("\t\t\tpara i <- 1 ate posi - 1 faca\n");
			programaFonte.append("\t\t\tinicio\n");
			programaFonte.append("\t\t\t\tse num_conta = conta[i].num entao\n");
			programaFonte.append("\t\t\t\t\tachou <- 1\n");
			programaFonte.append("\t\t\tfim\n");
			programaFonte.append("\t\t\tse achou = 1 entao\n");
			programaFonte.append("\t\t\t\tescreva \"Já existe conta cadastrada com esse número!\"\n");
			programaFonte.append("\t\t\tsenao\n");
			programaFonte.append("\t\t\tinicio\n");
			programaFonte.append("\t\t\t\tconta[posi].num <- num_conta\n");
			programaFonte.append("\t\t\t\tescreva \"Digite o nome do cliente:\"\n");
			programaFonte.append("\t\t\t\tleia conta[posi].nome\n");
			programaFonte.append("\t\t\t\tescreva \"Digite o saldo do cliente:\"\n");
			programaFonte.append("\t\t\t\tleia conta[posi].saldo\n");
			programaFonte.append("\t\t\t\tescreva \"Conta cadastrada com sucesso\"\n");
			programaFonte.append("\t\t\t\tposi <- posi + 1\n");
			programaFonte.append("\t\t\tfim\n");
			programaFonte.append("\t\tfim\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("\tse op = 2 entao\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tescreva \"Digite o nome do cliente a ser consultado:\"\n");
			programaFonte.append("\t\tleia nome_cliente\n");
			programaFonte.append("\t\tachou <- 0\n");
			programaFonte.append("\t\tpara i <- 1 ate posi - 1 faca\n");
			programaFonte.append("\t\tinicio\n");
			programaFonte.append("\t\t\tse conta[i].nome = nome_cliente entao\n");
			programaFonte.append("\t\t\tinicio\n");
			programaFonte.append("\t\t\t\tescreva conta[i].num, \" - \", conta[i].saldo\n");
			programaFonte.append("\t\t\t\tachou <- 1\n");
			programaFonte.append("\t\t\tfim\n");
			programaFonte.append("\t\tfim\n");
			programaFonte.append("\t\tse achou = 0 entao\n");
			programaFonte.append("\t\t\tescreva \"Não existe conta cadastrada para este cliente!\"\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("\tse op = 3 entao\n");
			programaFonte.append("\tinicio\n");
			programaFonte.append("\t\tse posi = 1 entao\n");
			programaFonte.append("\t\t\tescreva \"Nenhuma conta foi cadastrada!\"\n");
			programaFonte.append("\t\tsenao\n");
			programaFonte.append("\t\tinicio\n");
			programaFonte.append("\t\t\tmenor_saldo <- conta[1].saldo\n");
			programaFonte.append("\t\t\tachou <- 1\n");
			programaFonte.append("\t\t\ti <- 2\n");
			programaFonte.append("\t\t\tenquanto i < posi faca\n");
			programaFonte.append("\t\t\tinicio\n");
			programaFonte.append("\t\t\t\tse conta[i].saldo < menor_saldo entao\n");
			programaFonte.append("\t\t\t\tinicio\n");
			programaFonte.append("\t\t\t\t\tmenor_saldo <- conta[i].saldo\n");
			programaFonte.append("\t\t\t\t\tachou <- i\n");
			programaFonte.append("\t\t\t\tfim\n");
			programaFonte.append("\t\t\t\ti <- i + 1\n");
			programaFonte.append("\t\t\tfim\n");
			programaFonte.append("\t\t\tse achou <> posi - 1 entao\n");
			programaFonte.append("\t\t\t\tpara i <- achou + 1 ate posi - 1 faca\n");
			programaFonte.append("\t\t\t\tinicio\n");
			programaFonte.append("\t\t\t\t\tconta[i - 1].num <- conta[i].num\n");
			programaFonte.append("\t\t\t\t\tconta[i - 1].nome <- conta[i].nome\n");
			programaFonte.append("\t\t\t\t\tconta[i - 1].saldo <- conta[i].saldo\n");
			programaFonte.append("\t\t\t\tfim\n");
			programaFonte.append("\t\t\tescreva \"Conta excluída com sucesso!\"\n");
			programaFonte.append("\t\t\tposi <- posi - 1\n");
			programaFonte.append("\t\tfim\n");
			programaFonte.append("\tfim\n");
			programaFonte.append("ate op = 4\n");
			programaFonte.append("fim_algoritmo.\n\n");
			programaFonte.append("// Adaptado de:\n");
			programaFonte.append("// ASCENCIO, A. F. G.; CAMPOS, E. A. V. Fundamentos da programação de\n");
			programaFonte.append("// computadores. 2a. ed. São Paulo: Pearson Prentice Hall, 2007. p. 313-\n");
			programaFonte.append("// 314.");
			
			return programaFonte.toString();
		}
	};

	public abstract String getNome();

	public abstract String getProgramaFonte();
}
