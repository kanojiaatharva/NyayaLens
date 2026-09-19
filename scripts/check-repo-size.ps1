# PowerShell script to verify repository size is strictly under 10 MB limit
$ErrorActionPreference = "Stop"

Write-Host "=== NyayaLens Repository Size Audit ===" -ForegroundColor Cyan

$maxAllowedMB = 10.0
$gitDir = Join-Path $PSScriptRoot "..\.git"
$rootDir = Join-Path $PSScriptRoot "..\"

# Measure tracked files size if git repo initialized
if (Test-Path $gitDir) {
    $gitObjects = Get-ChildItem -Path (Join-Path $gitDir "objects") -Recurse -File | Measure-Object -Property Length -Sum
    $gitMb = [math]::Round(($gitObjects.Sum / 1MB), 2)
    Write-Host "Git Objects Size: $gitMb MB" -ForegroundColor Green
} else {
    Write-Host "Git repository not yet initialized." -ForegroundColor Yellow
}

# Measure source tree size excluding node_modules, target, .git
$sourceFiles = Get-ChildItem -Path $rootDir -Recurse -File | Where-Object {
    $_.FullName -notmatch "node_modules" -and
    $_.FullName -notmatch "target" -and
    $_.FullName -notmatch "\.git" -and
    $_.FullName -notmatch "dist" -and
    $_.FullName -notmatch "build"
} | Measure-Object -Property Length -Sum

$sourceMb = [math]::Round(($sourceFiles.Sum / 1MB), 2)
Write-Host "Source Tree Size (tracked files): $sourceMb MB" -ForegroundColor Green

if ($sourceMb -lt $maxAllowedMB) {
    Write-Host "SUCCESS: Repository size is safely below ${maxAllowedMB} MB limit." -ForegroundColor Green
    exit 0
} else {
    Write-Host "WARNING: Repository exceeds ${maxAllowedMB} MB limit! Reduce asset sizes." -ForegroundColor Red
    exit 1
}
