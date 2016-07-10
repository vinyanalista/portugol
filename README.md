Portugol Online
===============

O [Portugol Online][1] é um ambiente de desenvolvimento integrado que suporta a linguagem Portugol, inspirada no livro
[Fundamentos da Programação de Computadores][2].

Este arquivo contém instruções para aqueles que desejam **compilar** o Portugol Online em seus computadores. Note que não é necessário baixar, compilar ou mesmo instalar o Portugol Online, que pode ser executado *online* diretamente do navegador.

Se você deseja **conhecer** e/ou **utilizar** o Portugol Online, acesse o [*site* oficial do projeto][1].

## Pré-requisitos

Para compilar o Portugol Online em seu computador, você precisará:

- do [Java SE Development Kit (JDK)][3], de preferência em sua versão mais atual, mas no mínimo na versão 6 (caso ainda não tenha o JDK instalado em seu computador, você pode encontrar instruções de como instalá-lo [aqui][4]);
- do [Eclipse IDE][5], de preferência em sua versão mais atual, mas no mínimo em uma versão que suporte a compilação de programas para a plataforma Java SE 6; e
- opcionalmente, do [Git][6], para [clonar][7], [criar um *fork*][8] e [contribuir com o projeto][9].

Note que todas essas ferramentas são multiplataforma. Assim, você pode utilizar o sistema operacional de sua preferência para compilar o Portugol Online: [Windows][10], [Linux][11] ou [Mac OS X][12].

Nestas instruções, sempre que eu mencionar um comando para ser executado em uma interface de linha de comando, utilizarei como referência o Linux [Ubuntu 16.04][13], mas os comandos variam pouco de sistema para sistema.

## Obtendo o código-fonte

Instaladas as ferramentas necessárias, você pode obter o código-fonte do Portugol Online clicando [aqui][14]. Esse *link* aponta para um arquivo compactado ZIP que contém todos os arquivos do código-fonte da versão mais recente do Portugol Online (a que está disponível para uso no [*site*][1]). Extraia o conteúdo desse arquivo ZIP para uma pasta de sua preferência.

Caso tenha instalado o Git, é mais fácil obter o código-fonte do Portugol Online [clonando o repositório][7] do projeto. Para isso, utilizando uma interface de linha de comando, execute o comando a seguir:

```
$ git clone https://github.com/vinyanalista/portugol.git
```

Esse comando cria uma pasta chamada `portugol`. Ela contém um clone do [repositório *online* do projeto][15]. Entre nela:

```
$ cd portugol
```

Sempre antes de começar a trabalhar no código-fonte do Portugol Online, verifique se há atualizações executando:

```
$ git pull
```

## Compilando a gramática da linguagem

O Portugol Online utiliza o [SableCC][16], que é um "compilador de compiladores", para gerar as classes Java referentes às estruturas da linguagem, os analisadores léxico e sintático e algumas classes auxiliares. O SableCC gera essas classes Java na pasta `sablecc` com base na gramática da linguagem, que está no arquivo [`src/portugol.sablecc`][17].

Não é necessário baixar ou instalar o SableCC, ele acompanha o código-fonte do Portugol Online: é o pacote JAR `sablecc.jar` na pasta `lib`.

Há dois *scripts* que facilitam a execução do SableCC: `sablecc.bat` para quem usa Windows e `sablecc.sh` para quem usa Linux (na verdade, usando o Git Bash, deve ser possível executar o *script*  `sablecc.sh` em qualquer sistema operacional).

Para executar o SableCC no Windows, basta dar um duplo-clique no arquivo `sablecc.bat`.

Para executar o SableCC no Linux, estando na pasta `portugol`, execute o seguinte comando:

```
$ sh sablecc.sh
```

Ele deve produzir uma saída como:

```
SableCC version 3.7                                                                                                                  
Copyright (C) 1997-2012 Etienne M. Gagnon <egagnon@j-meg.com> and                                                                    
others.  All rights reserved.                                                                                                        
                                                                                                                                     
This software comes with ABSOLUTELY NO WARRANTY.  This is free software,                                                             
and you are welcome to redistribute it under certain conditions.                                                                     
                                                                                                                                     
Type 'sablecc -license' to view                                                                                                      
the complete copyright notice and license.                                                                                           
                                                                                                                                     
                                                                                                                                     
 -- Generating parser for portugol.sablecc in /home/vinicius/portugol/sablecc                                                        
Verifying identifiers.                                                                                                               
Verifying ast identifiers.                                                                                                           
Adding empty productions and empty alternative transformation if necessary.                                                          
Adding productions and alternative transformation if necessary.                                                                      
computing alternative symbol table identifiers.                                 
Verifying production transform identifiers.                                     
Verifying ast alternatives transform identifiers.                               
Generating token classes.                                                       
Generating production classes.                                                  
Generating alternative classes.                                                 
Generating analysis classes.                                                    
Generating utility classes.                                                     
Generating the lexer.                                                           
 State: INITIAL                                                                 
 - Constructing NFA.                                                            
..........................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................
 - Constructing DFA.
.........................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................
.....................................................................................................................................................................................................................................................................................................................................
 - resolving ACCEPT states.
Generating the parser.
....................................................................................................................................................................................................
....................................................................................................................................................................................................
....................................................................................................................................................................................................
..
...........................
Inlining.
......................................................................................................................................................................................................
......................................................................................................................................................................................................
......................................................................................................................................................................................................
..
......................................................................................................................................................................................................
```

Se tudo ocorrer bem, depois da execução desse comando, a pasta `sablecc` não estará mais vazia.

Sempre que fizer qualquer alteração na gramática da linguagem (o arquivo `src/portugol.sablecc`), execute novamente o SableCC para atualizar as classes Java na pasta `sablecc`.

Se o SableCC encontrar erros na gramática da linguagem, ele os informará quando for executado e não gerará as classes Java na pasta `sablecc`.

**Nunca** altere manualmente as classes Java geradas pelo SableCC. Ele sempre recria essas classes quando é executado.

## Importando o projeto no Eclipse IDE

Aqui ainda vem texto...

[1]: https://vinyanalista.github.io/portugol/
[2]: http://wps.prenhall.com/br_ascencio_3/
[3]: http://www.oracle.com/technetwork/pt/java/javase/downloads/
[4]: https://vinyanalista.github.io/blog/2012/07/10/instalacao-do-java-development-kit-jdk/
[5]: https://eclipse.org/
[6]: https://git-scm.com/
[7]: https://help.github.com/articles/cloning-a-repository/
[8]: https://help.github.com/articles/fork-a-repo/
[9]: https://help.github.com/categories/collaborating-on-projects-using-issues-and-pull-requests/
[10]: http://windows.microsoft.com/
[11]: http://www.vivaolinux.com.br/linux/
[12]: https://www.apple.com/br/osx/what-is/
[13]: http://www.ubuntu.com/
[14]: https://github.com/vinyanalista/portugol/archive/master.zip
[15]: https://github.com/vinyanalista/portugol
[16]: http://www.sablecc.org/
[17]: https://github.com/vinyanalista/portugol/blob/master/src/portugol.sablecc
