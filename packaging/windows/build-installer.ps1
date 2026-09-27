# Gera o executavel e o instalador para Windows.
#
# Requisitos (ja presentes no runner windows-2025 do GitHub Actions):
#   - JDK 21 (com jpackage) no PATH ou em JAVA_HOME
#   - WiX Toolset 3.x (necessario para o jpackage gerar .exe/.msi)
#
# Uso, na raiz do projeto ou em qualquer pasta:
#   powershell -ExecutionPolicy Bypass -File packaging\windows\build-installer.ps1
#
# Saida em dist\:
#   CG-TP1\                        executavel portatil (CG-TP1\CG-TP1.exe), sem instalacao
#   CG-TP1-<versao>-portable.zip   a mesma pasta compactada
#   CG-TP1-<versao>.exe            instalador

$ErrorActionPreference = 'Stop'

$ProjectRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
Set-Location $ProjectRoot

$AppName     = 'CG-TP1'
$Version     = '1.0.0'
$Vendor      = 'CG TP1'
$Description = 'Computacao Grafica - Algoritmos da Unidade 1 (PUC Minas)'
$MainModule  = 'cg.tp1/cg.tp1.App'
# Identificador fixo: permite que uma versao nova do instalador atualize a anterior
$UpgradeUuid = '56c5f434-30f1-4e14-b017-f0e68a3f701d'
$Dist        = Join-Path $ProjectRoot 'dist'

# jpackage do JDK 21 procura o WiX 3 no PATH; o runner define a variavel WIX
if (-not (Get-Command candle.exe -ErrorAction SilentlyContinue) -and $env:WIX) {
    $env:PATH = "$(Join-Path $env:WIX 'bin');$env:PATH"
}

$JPackage = 'jpackage'
if ($env:JAVA_HOME) {
    $JPackage = Join-Path $env:JAVA_HOME 'bin\jpackage.exe'
}

Write-Host '==> 1/4 Testes e imagem de runtime (jlink: Java + JavaFX + aplicacao)'
& .\mvnw.cmd -B clean test javafx:jlink
if ($LASTEXITCODE -ne 0) { throw 'Falha no build Maven' }

if (Test-Path $Dist) { Remove-Item $Dist -Recurse -Force }

Write-Host '==> 2/4 Executavel portatil (app-image)'
& $JPackage --type app-image `
    --name $AppName `
    --app-version $Version `
    --vendor $Vendor `
    --description $Description `
    --runtime-image target\cgtp1-runtime `
    --module $MainModule `
    --dest $Dist
if ($LASTEXITCODE -ne 0) { throw 'Falha ao gerar o app-image' }

Write-Host '==> 3/4 Zip do executavel portatil'
Compress-Archive -Path (Join-Path $Dist $AppName) `
    -DestinationPath (Join-Path $Dist "$AppName-$Version-portable.zip")

Write-Host '==> 4/4 Instalador (.exe)'
& $JPackage --type exe `
    --app-image (Join-Path $Dist $AppName) `
    --name $AppName `
    --app-version $Version `
    --vendor $Vendor `
    --description $Description `
    --win-dir-chooser `
    --win-menu `
    --win-menu-group $AppName `
    --win-shortcut `
    --win-shortcut-prompt `
    --win-upgrade-uuid $UpgradeUuid `
    --dest $Dist
if ($LASTEXITCODE -ne 0) { throw 'Falha ao gerar o instalador' }

Write-Host "Pronto. Arquivos em $Dist"
Get-ChildItem $Dist
