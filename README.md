# Cabe no Bolso

Planejador de despesas compartilhadas em Java Swing. A aplicação cadastra participantes e despesas de um passeio, divide os custos, mostra o total de cada pessoa e avisa quando o orçamento é ultrapassado. Também salva e recupera o planejamento em arquivo local.

Repositório: https://github.com/MarcosApollo/cabe-no-bolso.git

## 1. Requisitos

- **JDK 17 ou superior** (JDK 21 também funciona).
- **Nenhuma dependência externa**: Swing faz parte do próprio JDK.
- Opcional: IntelliJ IDEA, Eclipse ou VS Code.
- Opcional: Maven, pois existe um `pom.xml` configurado para Java 17.

> No Windows, use `javac -encoding UTF-8` para evitar problemas com acentos.

## 2. Organização do projeto

```text
cabe-no-bolso/
├── pom.xml
├── planejamento.dat              # arquivo gerado pelos botões Salvar/Carregar
├── src/
│   └── main/
│       └── java/
│           └── com/mycompany/
│               ├── TestePersistencia.java        # teste simples de console
│               └── cabenobolso/
│                   ├── PlanejamentoController.java  # classe principal (main) + controle MVC
│                   ├── TelaResumo.java              # visão: resumo
│                   ├── modelo/
│                   │   ├── Planejamento.java
│                   │   ├── Participante.java
│                   │   ├── Despesa.java             # classe abstrata
│                   │   ├── DespesaFixa.java
│                   │   └── DespesaPorPessoa.java
│                   ├── Persistencia/
│                   │   ├── Persistencia.java       # interface
│                   │   └── ArquivoPersistencia.java
│                   └── TelasParticipantes/
│                       ├── TelaParticipantes.java
│                       └── TelaDespesas.java
```

Papéis por camada (MVC):

- **Modelo**: pacote `modelo` — dados, validações, rateio e totais.
- **Visão**: `TelaParticipantes`, `TelaDespesas` e `TelaResumo`.
- **Controle**: `PlanejamentoController` — monta as abas, liga os botões e sincroniza as telas.
- **Persistência**: interface `Persistencia` e implementação `ArquivoPersistencia`.

## 3. Como compilar e executar

### 3.1 PowerShell (sem Maven)

Abra o PowerShell dentro da pasta do projeto e rode:

```powershell
cd "C:\Users\marco\Desktop\Java POO\cabe-no-bolso"

Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
$arquivos = Get-ChildItem -Recurse -Filter *.java .\src\main\java | ForEach-Object { $_.FullName }

javac -encoding UTF-8 -d out $arquivos
java -cp out com.mycompany.cabenobolso.PlanejamentoController
```

Na primeira execução, se não houver `planejamento.dat`, o programa abre com um planejamento vazio. Use o botão **Novo planejamento** para dar um nome.

### 3.2 IntelliJ IDEA

1. `File` → `Open...` e selecione a pasta `cabe-no-bolso`.
2. Confirme que `src/main/java` está marcada como *Sources Root*.
3. `Run` → `Edit Configurations...` → `+` → **Application**.
4. Em **Main class**, informe:

   ```text
   com.mycompany.cabenobolso.PlanejamentoController
   ```

5. Selecione um JDK 17 ou superior e clique em **Run**.

### 3.3 Maven (opcional)

O `pom.xml` já define `maven.compiler.release = 17` e `UTF-8`.

```bash
mvn compile
mvn exec:java -Dexec.mainClass=com.mycompany.cabenobolso.PlanejamentoController
```

> Se `mvn` não estiver instalado, use os comandos do PowerShell da seção 3.1.

## 4. Como usar

A janela possui três abas:

1. **Participantes**: cadastre com código único, nome e orçamento. É possível editar nome e orçamento. A exclusão é bloqueada enquanto a pessoa estiver em alguma despesa.
2. **Despesas**: informe descrição, valor, tipo e selecione os participantes.
   - **Fixa**: o valor é dividido entre os selecionados; os centavos restantes vão para os códigos menores primeiro.
   - **Por pessoa**: cada selecionado paga o valor unitário informado.
3. **Resumo**: mostra custo total, total individual, orçamento, situação e valor excedido.

Botões rodapé:

- **Salvar**: grava em `planejamento.dat` na raiz do projeto.
- **Carregar**: recupera participantes, despesas e vínculos.
- **Novo planejamento**: cria outro planejamento vazio.

## 5. Dados para reproduzir os testes

### 5.1 Caso de referência da proposta

Participantes:

| Código | Nome | Orçamento |
|---|---|---|
| 1 | Ana | R$ 70,00 |
| 2 | Bruno | R$ 60,00 |
| 3 | Carla | R$ 70,00 |

Despesas:

| Despesa | Tipo | Valor | Participantes |
|---|---|---|---|
| Transporte | Fixa | R$ 90,00 | Ana, Bruno e Carla |
| Almoço | Por pessoa | R$ 20,00 | Ana, Bruno e Carla |
| Atividade recreativa | Fixa | R$ 30,00 | Ana e Carla |
| Sobremesa | Por pessoa | R$ 10,00 | Ana, Bruno e Carla |

Resultado esperado: custo total **R$ 210,00**; Ana **R$ 75,00** (excede R$ 5,00); Bruno **R$ 60,00** (no limite); Carla **R$ 75,00** (excede R$ 5,00).

### 5.2 Cenário da equipe — viagem à praia

| Código | Nome | Orçamento |
|---|---|---|
| 1 | João | R$ 250,00 |
| 2 | Carla | R$ 200,00 |
| 3 | Paulo | R$ 200,00 |
| 4 | Ana | R$ 100,00 |

| Despesa | Tipo | Valor | Participantes |
|---|---|---|---|
| Transporte ida e vinda | Fixa | R$ 160,00 | Todos (R$ 40,00 cada) |
| Alimentação | Por pessoa | R$ 80,00 | Todos |
| Passeio de buggy | Fixa | R$ 100,00 | João e Carla (R$ 50,00 cada) |
| Aula de surf | Por pessoa | R$ 80,00 | João e Paulo |

Resultado esperado: custo total **R$ 740,00**; João **R$ 250,00** (no limite); Carla **R$ 170,00** (dentro); Paulo **R$ 200,00** (no limite); Ana **R$ 120,00** (excede R$ 20,00).

## 6. Roteiro dos testes obrigatórios

| # | Teste | Como reproduzir | Resultado esperado |
|---|---|---|---|
| 1 | Centavos no rateio | Cadastre 3 participantes e inclua uma despesa fixa de R$ 100,00 para os três. | R$ 33,34 / R$ 33,33 / R$ 33,33; soma R$ 100,00. |
| 2 | Entrada ou vínculo inválido | Tente descrição vazia, valor zero/negativo, despesa sem participante, participante inexistente (99) e código duplicado. | Mensagem clara e nenhum dado alterado. |
| 3 | Alteração de orçamento | No caso de referência, edite o orçamento da Ana para R$ 80,00. | Total da Ana continua R$ 75,00 e passa a ficar dentro do limite. |
| 4 | Exclusão de despesa | No caso de referência original, exclua “Atividade recreativa”. | R$ 60,00 por pessoa e total R$ 180,00. |
| 5 | Salvar e reiniciar | Salve, feche a aplicação, abra de novo e clique em **Carregar**. | 3 participantes, 4 despesas e totais R$ 75,00 / R$ 60,00 / R$ 75,00, sem duplicação. |
| 6 | Problema no arquivo | Renomeie/remova `planejamento.dat` e tente carregar; depois coloque um arquivo inválido com esse nome. | Aviso “Não foi possível carregar planejamento.dat.”; o planejamento em uso permanece intacto. |

Também existe um teste de console simples:

```powershell
java -cp out com.mycompany.TestePersistencia
```

> Esse teste grava `planejamento.dat`. Faça uma cópia do arquivo antes, se quiser preservar seus dados.

## 7. Persistência e precisão

- O arquivo `planejamento.dat` fica na raiz do projeto e é gravado por serialização de objetos.
- Classes de dados implementam `Serializable` e declaram `serialVersionUID`.
- Valores monetários são guardados como `long` em centavos, evitando erro de ponto flutuante.
- A interface aceita entradas como `1500`, `1500,50` e `1.500,50`.
- Falhas de leitura são tratadas com mensagem e não apagam o planejamento aberto.

## 8. Solução de problemas

| Sintoma | Causa provável | Solução |
|---|---|---|
| `UnsupportedClassVersionError` | JDK mais antigo que 17 | Instale JDK 17+ e recompile. |
| `ClassNotFoundException` | Compilação incompleta | Rode novamente o `javac` com todos os `.java` e execute com `-cp out`. |
| “Não foi possível carregar planejamento.dat.” | Arquivo ausente ou inválido | É normal na primeira execução. Use **Salvar** ou **Novo planejamento**. |
| Acentos aparecem errados no console | Codificação diferente de UTF-8 | Compile com `-encoding UTF-8`. |

## 9. Relatório

O relatório técnico com UML, resultados, figuras e referências está em PDF na entrega da atividade. Cada integrante deve enviar o mesmo PDF no AVA até **27/11/2026 às 23h59**.
