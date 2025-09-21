# .\deploy.ps1
# Caminho do projeto
$projectDir = "C:\Users\conta\Documents\FinControl\fin-control-api"
Set-Location $projectDir

# Variáveis
$proj="C:\Users\conta\Documents\FinControl\fin-control-api";
$img="fin-control-api:latest";
$acr="fincontrolregistry.azurecr.io/fin-control-api:latest";
$dep="fincontrol-api";
$cont="fin-control-api";

cd $proj;
if (mvn clean package) {
    if (docker build -t $img .) {
        if (docker tag $img $acr) {
            if (az acr login --name fincontrolregistry) {
                if (docker push $acr) {
                    if (kubectl set image deployment/$dep $cont=$acr) {
                        kubectl rollout status deployment/$dep
                    } else { Write-Host "❌ Falha no set image"; exit 1 }
                } else { Write-Host "❌ Falha no push"; exit 1 }
            } else { Write-Host "❌ Falha no login ACR"; exit 1 }
        } else { Write-Host "❌ Falha no tag Docker"; exit 1 }
    } else { Write-Host "❌ Falha no build Docker"; exit 1 }
} else { Write-Host "❌ Falha no Maven"; exit 1 }
