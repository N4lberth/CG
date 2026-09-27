# CG TP1 — Algoritmos da Unidade 1

Trabalho prático de Computação Gráfica (PUC Minas): aplicação desktop em **Java 21 + JavaFX** que implementa, sobre uma matriz de pixels, os algoritmos de transformações geométricas 2D, rasterização, recorte e preenchimento.

Nenhum algoritmo da disciplina usa primitivas gráficas prontas (`strokeLine`, `fillPolygon` etc.). O JavaFX é usado apenas para a interface e para copiar a matriz de pixels para a tela.

---

## Requisitos do enunciado e onde estão no código

Os caminhos abaixo são relativos a `src/main/java/cg/tp1/`.

| Requisito | Implementação |
|---|---|
| Translação, rotação, escala | `algorithms/transformation/Transformations.java` (matrizes 3×3 homogêneas em `Matrix3.java`) |
| Reflexões X, Y e XY | `Transformations.reflectionX/Y/XY()` |
| Fatores informados pelo usuário | `ui/TransformPanel.java` (Spinners e Slider, sem valores fixos) |
| Retas — DDA | `algorithms/rasterization/DDALine.java` |
| Retas — Bresenham | `algorithms/rasterization/BresenhamLine.java` |
| Circunferência — Bresenham | `algorithms/rasterization/BresenhamCircle.java` (parâmetro inicial d = 3 − 2r) |
| Recorte — Cohen-Sutherland | `algorithms/clipping/CohenSutherland.java` |
| Recorte — Liang-Barsky | `algorithms/clipping/LiangBarsky.java` |
| Boundary-Fill (4 e 8) | `algorithms/filling/BoundaryFill.java` |
| Flood-Fill (4 e 8) | `algorithms/filling/FloodFill.java` |
| Pontos, retas e polígonos | `model/PointShape.java`, `model/Line.java`, `model/Polygon.java` (e `model/Circle.java`) |
| Seleção por região retangular | `model/DrawingModel.selectInside()` e `ui/tools/SelectTool.java` |
| Entrada por cliques | ferramentas em `ui/tools/` |
| Matriz de pixels | `raster/PixelBuffer.java` (`int[linha][coluna]`) |

---

## Como executar

É necessário o **JDK 21**. O Maven não precisa estar instalado, pois o projeto inclui o Maven Wrapper.

```bash
./mvnw javafx:run        # Linux / macOS
mvnw.cmd javafx:run      # Windows
```

Para executar os testes automatizados (66 testes, cobrindo todos os algoritmos):

```bash
./mvnw test
```

> No ambiente de desenvolvimento, o `mise.toml` fixa as versões do Java (21) e do Maven (3.9.16); com o [mise](https://mise.jdx.dev/) instalado, basta usar `mvn javafx:run`.

---

## Como usar

A janela tem a barra de ferramentas (topo), a Área de Desenho (centro), o painel de operações (direita) e a barra de status (base), que mostra a instrução da ferramenta ativa e a posição do mouse em coordenadas cartesianas e na matriz.

| Ferramenta | Uso com o mouse |
|---|---|
| **Selecionar** | Arrastar um retângulo. São selecionados os objetos inteiramente dentro dele. Botão direito limpa a seleção. |
| **Ponto** | Um clique. |
| **Reta** | Clique no ponto inicial e clique no ponto final. Botão direito cancela. |
| **Polígono** | Um clique por vértice. Fecha com o botão direito ou clicando sobre o primeiro vértice. |
| **Circunferência** | Clique no centro e clique em um ponto da borda (define o raio). |
| **Janela de recorte** | Arrastar um retângulo. Botão direito remove a janela. |
| **Preencher** | Clique na semente, dentro da região a preencher. |

Painéis:

- **Desenho:** cor e algoritmo de reta (DDA ou Bresenham) dos próximos objetos.
- **Seleção:** lista dos objetos selecionados.
- **Transformações:** tipo, fatores e ponto de referência (centro da seleção ou origem); exibe a matriz aplicada.
- **Recorte:** algoritmo e botão *Recortar*.
- **Preenchimento:** algoritmo, conectividade (4 ou 8), cor de preenchimento e cor da fronteira.

Os valores numéricos podem ser ajustados apenas com o mouse, pelas setas ou pela roda do mouse; digitar é opcional.

---

## Arquitetura

```
src/main/java/cg/tp1/
├── App.java                  ponto de entrada JavaFX
├── model/                    geometria em coordenadas cartesianas (double), sem JavaFX
│   ├── Point, Rectangle
│   ├── Shape (sealed) → PointShape, Line, Polygon, Circle
│   ├── FillOperation, FillAlgorithm, Connectivity, LineAlgorithm
│   └── DrawingModel          objetos, seleção, preenchimentos e janela de recorte
├── algorithms/               algoritmos da disciplina, sem JavaFX
│   ├── rasterization/        DDALine, BresenhamLine, BresenhamCircle
│   ├── transformation/       Matrix3, Transformations
│   ├── clipping/             CohenSutherland, LiangBarsky, ShapeClipper
│   └── filling/              BoundaryFill, FloodFill
├── raster/                   PixelBuffer (matriz de pixels), CoordinateSystem, Pixel
├── render/                   SceneRenderer: modelo → matriz de pixels
└── ui/                       interface JavaFX
    ├── MainWindow, DrawingCanvas, DrawingEditor
    ├── painéis (DrawSettings, Selection, Transform, Clip, Fill)
    └── tools/                uma classe por ferramenta (padrão Strategy)
```

Fluxo de um clique:

```
mouse → DrawingCanvas (tela → cartesiano) → ferramenta ativa → DrawingModel
      → SceneRenderer (chama os algoritmos) → PixelBuffer → DrawingCanvas (matriz → tela)
```

Os pacotes `model`, `algorithms`, `raster` e `render` não dependem do JavaFX, o que permite testar cada algoritmo isoladamente (`src/test/`).

---

## Decisões de implementação

Pontos que o enunciado não especifica e a escolha adotada em cada um:

1. **Matriz de pixels de 200 × 150**, exibida com cada pixel ampliado (2 a 6 pixels de tela), para que o resultado dos algoritmos fique visível. Grade e eixos são apenas guias visuais e não fazem parte da matriz.
2. **Sistema cartesiano com origem no centro** da Área de Desenho e eixo Y para cima. A inversão do eixo Y em relação à tela acontece só em `CoordinateSystem`.
3. **O modelo é a fonte da verdade:** os objetos guardam coordenadas `double`, e a matriz é recalculada a partir deles a cada alteração. Assim, transformações sucessivas não acumulam erro de arredondamento.
4. **Camada de sobreposição (overlay):** pré-visualizações, destaque da seleção e janela de recorte são desenhados em uma segunda matriz, com os mesmos algoritmos de rasterização. Eles não alteram a matriz da cena e, portanto, não interferem no preenchimento.
5. **Circunferência** é um objeto selecionável e transformável. A escala não uniforme (sx ≠ sy) é bloqueada para circunferências, pois geraria uma elipse.
6. **Rotação, escala e reflexão** usam, por padrão, o centro da seleção como referência (composição T(c) · M · T(−c)); a origem pode ser escolhida no painel.
7. **Seleção:** um objeto é selecionado quando sua caixa envolvente está inteiramente dentro do retângulo.
8. **Recorte:**
   - Age nos objetos selecionados ou, sem seleção, em todos.
   - Polígonos são recortados aresta por aresta e passam a ser um conjunto de retas.
   - Pontos fora da janela são removidos.
   - Circunferências não são recortadas.
9. **Preenchimento** com pilha explícita em vez de recursão (mesma lógica, sem estouro da pilha do Java). Cada preenchimento é guardado e vinculado ao objeto fechado que contém a semente; ao transformar o objeto, a semente é transformada junto.
10. **Conectividade 8 pode “vazar”** por bordas diagonais, pois as retas rasterizadas são 8-conectadas. É o comportamento esperado do algoritmo.
11. **Fatores de escala** limitados ao intervalo [0,1; 10]: fator zero degeneraria o objeto, e fator negativo equivale a uma reflexão, que já tem opção própria.

---

## Executável e instalador para Windows

O instalador é gerado pelo `jpackage` (JDK 21) a partir de uma imagem de runtime criada pelo `jlink`. **A instalação já inclui o Java e o JavaFX**, portanto não é preciso instalar bibliotecas ou componentes adicionais no computador de destino. O instalador permite escolher a pasta de instalação e criar atalhos no menu Iniciar e na área de trabalho.

O `jpackage` só gera pacotes de Windows em um computador Windows. Há duas formas de gerá-los.

**GitHub Actions** (a partir de qualquer sistema): no repositório, abrir *Actions → Windows installer → Run workflow*. Ao final, os arquivos ficam disponíveis em *Artifacts*.

**Em um computador Windows**, com JDK 21 e [WiX Toolset 3](https://github.com/wixtoolset/wix3/releases) instalados:

```powershell
powershell -ExecutionPolicy Bypass -File packaging\windows\build-installer.ps1
```

Arquivos gerados em `dist\`:

| Arquivo | Conteúdo |
|---|---|
| `CG-TP1-1.0.0.exe` | instalador |
| `CG-TP1-1.0.0-portable.zip` | executável portátil (descompactar e abrir `CG-TP1.exe`), sem instalação |
