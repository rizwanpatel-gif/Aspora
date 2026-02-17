$ErrorActionPreference = "Stop"

$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$mavenVersion = "3.9.6"
$mavenDir = Join-Path $projectDir ".mvn\apache-maven-$mavenVersion"
$mavenBin = Join-Path $mavenDir "bin\mvn.cmd"
$mavenZip = Join-Path $projectDir ".mvn\maven.zip"
$mavenUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$mavenVersion/apache-maven-$mavenVersion-bin.zip"

if (-not (Test-Path $mavenBin)) {
    Write-Host "Downloading Apache Maven $mavenVersion..." -ForegroundColor Cyan
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    (New-Object Net.WebClient).DownloadFile($mavenUrl, $mavenZip)

    Write-Host "Extracting Maven..." -ForegroundColor Cyan
    Expand-Archive -Path $mavenZip -DestinationPath (Join-Path $projectDir ".mvn") -Force
    Remove-Item $mavenZip -Force

    if (-not (Test-Path $mavenBin)) {
        Write-Host "ERROR: Maven extraction failed." -ForegroundColor Red
        exit 1
    }
    Write-Host "Maven ready." -ForegroundColor Green
}

& $mavenBin $args
exit $LASTEXITCODE
