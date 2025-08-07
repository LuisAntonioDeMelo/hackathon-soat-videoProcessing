# FIAP Hackathon - Video Processor
# Script de inicialização para Windows (PowerShell)

Write-Host "🚀 FIAP Hackathon - Video Processor (COMPLETO)" -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan

# Verificar se Docker está rodando
try {
    docker --version | Out-Null
    Write-Host "✅ Docker encontrado" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker não encontrado. Instale o Docker Desktop primeiro." -ForegroundColor Red
    exit 1
}

# Verificar se Docker Compose está disponível
try {
    docker compose version | Out-Null
    Write-Host "✅ Docker Compose encontrado" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker Compose não encontrado." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "📦 Iniciando TODOS os serviços..." -ForegroundColor Yellow
Write-Host "   🐘 PostgreSQL Database" -ForegroundColor Gray
Write-Host "   🐰 RabbitMQ Message Broker" -ForegroundColor Gray  
Write-Host "   🔴 Redis Cache" -ForegroundColor Gray
Write-Host "   ☕ Backend Spring Boot" -ForegroundColor Gray
Write-Host "   ⚛️  Frontend Next.js" -ForegroundColor Gray
Write-Host "   📊 Prometheus Monitoring" -ForegroundColor Gray
Write-Host "   📈 Grafana Dashboard" -ForegroundColor Gray

# Parar containers existentes (se houver)
docker compose down

# Construir e iniciar todos os serviços
docker compose up --build -d

Write-Host ""
Write-Host "⏳ Aguardando serviços ficarem disponíveis..." -ForegroundColor Yellow

# Aguardar PostgreSQL
Write-Host "   🐘 Aguardando PostgreSQL..." -ForegroundColor Gray
Start-Sleep -Seconds 10

# Aguardar RabbitMQ
Write-Host "   🐰 Aguardando RabbitMQ..." -ForegroundColor Gray
Start-Sleep -Seconds 10

# Aguardar Redis
Write-Host "   🔴 Aguardando Redis..." -ForegroundColor Gray
Start-Sleep -Seconds 5

# Aguardar Prometheus
Write-Host "   📊 Aguardando Prometheus..." -ForegroundColor Gray
Start-Sleep -Seconds 8

# Aguardar Backend
Write-Host "   ☕ Aguardando Backend..." -ForegroundColor Gray
$timeout = 180
$elapsed = 0
$backendReady = $false

while ($elapsed -lt $timeout -and -not $backendReady) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            $backendReady = $true
            Write-Host "   ✅ Backend está pronto!" -ForegroundColor Green
        }
    } catch {
        Start-Sleep -Seconds 5
        $elapsed += 5
        Write-Host "   ⏳ Aguardando backend... ($elapsed/$timeout segundos)" -ForegroundColor Gray
    }
}

if (-not $backendReady) {
    Write-Host "   ❌ Backend não ficou pronto em $timeout segundos" -ForegroundColor Red
    Write-Host "   📋 Verificando logs do backend:" -ForegroundColor Yellow
    docker logs fiap-backend --tail 20
    exit 1
}

# Aguardar Frontend
Write-Host "   ⚛️  Aguardando Frontend..." -ForegroundColor Gray
Start-Sleep -Seconds 20

$frontendReady = $false
$timeout = 90
$elapsed = 0

while ($elapsed -lt $timeout -and -not $frontendReady) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:3000" -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            $frontendReady = $true
            Write-Host "   ✅ Frontend está pronto!" -ForegroundColor Green
        }
    } catch {
        Start-Sleep -Seconds 5
        $elapsed += 5
        Write-Host "   ⏳ Aguardando frontend... ($elapsed/$timeout segundos)" -ForegroundColor Gray
    }
}

# Aguardar Grafana
Write-Host "   📈 Aguardando Grafana..." -ForegroundColor Gray
Start-Sleep -Seconds 10

Write-Host ""
Write-Host "🎉 Aplicação iniciada com sucesso!" -ForegroundColor Green
Write-Host "===================================" -ForegroundColor Green
Write-Host ""
Write-Host "📱 URLs de Acesso:" -ForegroundColor Cyan
Write-Host ""
Write-Host "   🎯 APLICAÇÃO:" -ForegroundColor Yellow
Write-Host "   Frontend:        http://localhost:3000" -ForegroundColor White
Write-Host "   Backend API:     http://localhost:8080" -ForegroundColor White
Write-Host "   Swagger UI:      http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host ""
Write-Host "   📊 MONITORAMENTO:" -ForegroundColor Yellow
Write-Host "   Grafana:         http://localhost:3001 (admin/grafana123)" -ForegroundColor White
Write-Host "   Prometheus:      http://localhost:9090" -ForegroundColor White
Write-Host ""
Write-Host "   🔧 INFRAESTRUTURA:" -ForegroundColor Yellow
Write-Host "   RabbitMQ UI:     http://localhost:15672 (admin/admin)" -ForegroundColor White
Write-Host "   Redis:           localhost:6379 (password: redis123)" -ForegroundColor White
Write-Host "   PostgreSQL:      localhost:5446 (postgres/postgres)" -ForegroundColor White
Write-Host ""
Write-Host "🔧 Comandos úteis:" -ForegroundColor Cyan
Write-Host "   Ver logs:        docker compose logs -f [serviço]" -ForegroundColor White
Write-Host "   Parar tudo:      docker compose down" -ForegroundColor White
Write-Host "   Rebuild:         docker compose up --build" -ForegroundColor White
Write-Host "   Parar script:    .\stop-app.ps1" -ForegroundColor White
Write-Host ""

if ($frontendReady) {
    Write-Host "🌐 Abrindo aplicação principal..." -ForegroundColor Yellow
    Start-Process "http://localhost:3000"
    
    Start-Sleep -Seconds 2
    Write-Host "📊 Abrindo Grafana Dashboard..." -ForegroundColor Yellow
    Start-Process "http://localhost:3001"
} else {
    Write-Host "⚠️  Frontend pode não estar totalmente pronto. Aguarde alguns segundos e acesse:" -ForegroundColor Yellow
    Write-Host "   http://localhost:3000" -ForegroundColor White
}

Write-Host ""
Write-Host "🚀 Sistema completo disponível com:" -ForegroundColor Green
Write-Host "   ✅ Cache com Redis" -ForegroundColor Green  
Write-Host "   ✅ Monitoramento com Prometheus + Grafana" -ForegroundColor Green
Write-Host "   ✅ CI/CD com GitHub Actions" -ForegroundColor Green
Write-Host "   ✅ Todas as funcionalidades originais" -ForegroundColor Green
