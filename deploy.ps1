# .\deploy.ps1
# Caminho do projeto
$projectDir = "C:\Users\conta\Documents\FinControl\fin-control-api"
Set-Location $projectDir

# Variáveis
$localImage = "fin-control-api:latest"
$acrName = "fincontrolregistry.azurecr.io"
$acrImage = "$acrName/fin-control-api:latest"
$deploymentName = "fincontrol-api"
$containerName = "fin-control-api"

Write-Host "`n=== Compilando projeto Maven ==="
mvn clean package

if ($LASTEXITCODE -ne 0) {
    Write-Host "Erro na compilação. Abortando."
    exit 1
}

Write-Host "`n=== Buildando imagem Docker ==="
docker build -t $localImage .

Write-Host "`n=== Taggeando imagem para o ACR ==="
docker tag $localImage $acrImage

Write-Host "`n=== Logando no Azure Container Registry ==="
az acr login --name fincontrolregistry

Write-Host "`n=== Fazendo push da imagem para o ACR ==="
docker push $acrImage

Write-Host "`n=== Atualizando deployment no Kubernetes ==="
kubectl set image deployment/$deploymentName $containerName=$acrImage

Write-Host "`n=== Verificando rollout ==="
kubectl rollout status deployment/$deploymentName

Write-Host "`n=== Deploy concluído com sucesso! ==="
