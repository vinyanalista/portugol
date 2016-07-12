Portugol Online
===============

O [Portugol Online][1] é um ambiente de desenvolvimento integrado que suporta a linguagem Portugol, inspirada no livro
[Fundamentos da Programação de Computadores][2].

Esta página contém instruções para aqueles que desejam **compilar** o Portugol Online em seus computadores. Note que não é necessário baixar, compilar ou mesmo instalar o Portugol Online, que pode ser executado *online* diretamente do navegador.

Se você deseja **conhecer** e/ou **utilizar** o Portugol Online, acesse o [*site* oficial do projeto][1].

## Pré-requisitos

Para compilar o Portugol Online em seu computador, você precisará:

- do [Java SE Development Kit (JDK)][3], de preferência em sua versão mais atual, mas no mínimo na versão 6 (caso ainda não tenha o JDK instalado em seu computador, você pode encontrar instruções de como instalá-lo [aqui][4]);
- do [Eclipse IDE][5], de preferência em sua versão mais atual, mas no mínimo em uma versão que suporte a compilação de programas para a plataforma Java SE 6; e
- opcionalmente, do [Git][6], para [clonar][7], [criar um *fork*][8] e [contribuir com o projeto][9].

Note que todas essas ferramentas são multiplataforma. Assim, você pode utilizar o sistema operacional de sua preferência para compilar o Portugol Online: [Windows][10], [Linux][11] ou [Mac OS X][12].

Nestas instruções, sempre que eu mencionar um comando para ser executado em uma interface de linha de comando, utilizarei como referência o Linux [Ubuntu 16.04][13], mas os comandos variam pouco de sistema para sistema. Além disso, assumo que você está familiarizado com a programação em Java e com o Eclipse IDE.

Se o Git é uma novidade e você deseja conhecê-lo, recomendo os excelentes [cursos de Git da Code School][14]. O primeiro deles, [Try Git][15], é completamente gratuito e oferece uma visão básica do que é o Git e como utilizá-lo no dia-a-dia. Uma parte do segundo curso, [Git Real][16], também é gratuita.

## Obtendo o código-fonte

Instaladas as ferramentas necessárias, você pode obter o código-fonte do Portugol Online clicando [aqui][17]. Esse *link* aponta para um arquivo compactado ZIP que contém todos os arquivos do código-fonte da versão mais recente do Portugol Online (a que está disponível para uso no [*site*][1]). Extraia o conteúdo desse arquivo ZIP para uma pasta de sua preferência.

Caso tenha instalado o Git, é mais fácil obter o código-fonte do Portugol Online [clonando o repositório][7] do projeto. Para isso, utilizando uma interface de linha de comando, execute o comando a seguir:

```
$ git clone https://github.com/vinyanalista/portugol.git
```

Esse comando cria uma pasta chamada `portugol`. Ela contém um clone do [repositório *online* do projeto][18]. Entre nela:

```
$ cd portugol
```

Sempre antes de começar a trabalhar no código-fonte do Portugol Online, verifique se há atualizações executando:

```
$ git pull
```

## Compilando a gramática da linguagem

O Portugol Online utiliza o [SableCC][19], que é um "compilador de compiladores", para gerar as classes Java referentes às estruturas da linguagem, os analisadores léxico e sintático e algumas classes auxiliares. O SableCC gera essas classes Java na pasta `sablecc` com base na gramática da linguagem, que está no arquivo [`src/portugol.sablecc`][20].

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

Para importar o projeto no Eclipse IDE, inicie o Eclipse e vá em **File** -> **Import**.

Na tela que aparece, expanda as opções em **General**, selecione **Existing Projects into Workspace** e clique em **Next**.

Selecione a opção **Select root directory** e clique em **Browse**.

Procure a pasta `portugol` e clique em **OK**. Para terminar, clique em **Finish**.

O projeto `PortugolOnline` deve aparecer na *view* **Package Explorer** do Eclipse IDE.

## Configurando o *classpath*

Na janela principal do Eclipse, vá em **Window** -> **Preferences**.

Na tela que aparece, à esquerda, expanda as opções **Java** e **Build Path**, e selecione a opção **Classpath Variables**.

Na parte direita da tela, clique no botão **New**.

No campo **Name**, digite `JAVA_HOME`.

No campo **Path**, informe o local onde foi instalado o JDK. Você pode clicar no botão **Browse** para procurá-lo. Se você seguiu [o tutorial indicado][4], você deve ter instalado o JDK em uma pasta como `/opt/jdk1.8.0_91`.

Clique em **OK**. De volta à tela anterior, clique em **OK** de novo.

Aparece a seguinte mensagem na tela, sugerindo que o projeto seja analisado e construído novamente:

*The classpath variables have changed. A full rebuild is recommended for changes to take effect. Do the full build now?*

Clique em **Yes** para aceitar.

Feito isso, é possível compilar e executar o Portugol Online como qualquer outro projeto Java do Eclipse.

## Executando o Portugol Online a partir do Eclipse

Como você deve ter visto no [*site* do Portugol Online][1], é possível executá-lo como um aplicativo Java ou como um [*applet*][21].

### Como um aplicativo Java

Na *view* **Package Explorer**, clique com o botão direito no projeto `PortugolOnline` e vá em **Run As** -> **Java Application**.

O Eclipse mostra algumas classes Java que implementam um método `main()`. Selecione a classe `Aplicativo` e clique em **OK**.

O Portugol Online deve ser iniciado como um aplicativo Java.

### Como um *applet*

Na *view* **Package Explorer**, clique com o botão direito no projeto `PortugolOnline` e vá em **Run As** -> **Java Applet**.

O Portugol Online deve ser iniciado como um *applet*.

## Produzindo o pacote JAR do Portugol Online

O Portugol Online está disponível para *download* no seu [*site*][1] na forma de um pacote JAR. De uma forma ou de outra, seja baixando o Portugol Online para uso *offline* no seu computador, seja executando-o no navegador como um *applet* ou a partir do [Java Web Start][22], você baixa esse pacote JAR para o seu computador para poder executar o Portugol Online.

As versões mais recentes do Java (mais especificamente, da versão 7 Update 21, lançada em fevereiro de 2013, em diante) [requerem que o *applet* seja assinado][23] para que possa ser executado no navegador.

Para produzir um pacote JAR do Portugol Online assinado com um certificado auto-assinado (a única maneira gratuita de se produzir um pacote JAR assinado), proceda como descrito a seguir.

Se essa é a primeira vez que você está empacotando o Portugol Online, execute antes, via interface de linha de comando, a ferramenta [`keytool`][24] para produzir uma chave de criptografia:

```
$ keytool -genkey -alias portugol
```

A ferramenta fará algumas perguntas, que você deve responder de acordo. Aqui segue um exemplo:

```
Informe a senha da área de armazenamento de chaves:  
Informe novamente a nova senha: 
Qual é o seu nome e o seu sobrenome?
  [Unknown]:  Antonio Vinicius Menezes Medeiros
Qual é o nome da sua unidade organizacional?
  [Unknown]:  DCOMP
Qual é o nome da sua empresa?
  [Unknown]:  UFS
Qual é o nome da sua Cidade ou Localidade?
  [Unknown]:  Sao Cristovao
Qual é o nome do seu Estado ou Município?
  [Unknown]:  SE
Quais são as duas letras do código do país desta unidade?
  [Unknown]:  BR
CN=Antonio Vinicius Menezes Medeiros, OU=DCOMP, O=UFS, L=Sao Cristovao, ST=SE, C=BR Está correto?
  [não]:  sim

Informar a senha da chave de <portugol>
        (RETURN se for igual à senha da área do armazenamento de chaves):  
```

Informe a senha definida na última pergunta no arquivo `build.xml`:

```
<signjar alias="portugol" jar="portugol.jar" signedjar="portugol.jar" storepass="123456" />
```

Para produzir o pacote JAR do Portugol Online, no Eclipse IDE, na *view* **Package Explorer**, expanda o projeto `PortugolOnline`, clique com o botão direito no arquivo `build.xml` e vá em **Run As** -> **Ant Build**.

O **Console** do Eclipse mostra a produção e assinatura do pacote JAR:

```
Buildfile: /home/vinicius/dev/projects/portugol/build.xml
create_run_jar:
  [signjar] Signing JAR: /home/vinicius/dev/projects/portugol/portugol.jar to /home/vinicius/dev/projects/portugol/portugol.jar as portugol
  [signjar] jar signed.
  [signjar] Warning: 
  [signjar] The signer certificate will expire within six months.
  [signjar] No -tsa or -tsacert is provided and this jar is not timestamped. Without a timestamp, users may not be able to validate this jar after the signer certificate's expiration date (2016-10-09) or after any future revocation date.
BUILD SUCCESSFUL
Total time: 3 seconds
```

O pacote JAR `portugol.jar` é criado na pasta `portugol`.

## Código-fonte do *site* do Portugol Online

O [*site* do Portugol Online][1] é armazenado no [GitHub Pages][25].

Se você obteve o código-fonte do Portugol Online usando a ferramenta Git, é possível consultar o código-fonte do *site* mudando para o *branch* `gh-pages`:

```
$ git checkout gh-pages
```

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
[14]: https://www.codeschool.com/learn/git
[15]: https://www.codeschool.com/courses/try-git
[16]: https://www.codeschool.com/courses/git-real
[17]: https://github.com/vinyanalista/portugol/archive/master.zip
[18]: https://github.com/vinyanalista/portugol
[19]: http://www.sablecc.org/
[20]: https://github.com/vinyanalista/portugol/blob/master/src/portugol.sablecc
[21]: http://www.criarweb.com/artigos/198.php
[22]: https://www.java.com/pt_BR/download/faq/java_webstart.xml
[23]: http://www.oracle.com/technetwork/java/javase/tech/java-code-signing-1915323.html
[24]: https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html
[25]: https://pages.github.com/
