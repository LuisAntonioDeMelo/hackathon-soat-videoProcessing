# 🚀 FIAP Hackathon - Video Processor

Sistema completo de processamento de vídeos que extrai frames e disponibiliza através de uma interface web moderna.

## 📋 Sobre o Projeto

- **Backend**: Spring Boot com PostgreSQL, RabbitMQ e FFmpeg
- **Frontend**: Next.js com TypeScript e Tailwind CSS
- **Infraestrutura**: Docker Compose para orquestração completa

## 🏗️ Arquitetura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │────│   Backend       │────│   PostgreSQL    │
│   (Next.js)     │    │  (Spring Boot)  │    │                 │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 5446    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                       ┌─────────────────┐
                       │    RabbitMQ     │
                       │   Port: 5672    │
                       │ Mgmt: 15672     │
                       └─────────────────┘
```

## 🚦 Início Rápido

### Pré-requisitos

- **Docker**
- **Git**

### 🖱️ Execução com 1 Clique

**Windows (PowerShell):**

```powershell
.\start-app.ps1
```

### 🐳 Docker Compose Manual

```bash
# Iniciar todos os serviços
docker compose up --build -d

# Ver logs
docker compose logs -f

# Parar tudo
docker compose down
```

## 📱 URLs de Acesso

| Serviço                      | URL                                   | Credenciais |
| ----------------------------- | ------------------------------------- | ----------- |
| **Frontend**            | http://localhost:3000                 | -           |
| **Backend API**         | http://localhost:8080                 | -           |
| **Swagger UI**          | http://localhost:8080/swagger-ui.html | -           |
| **RabbitMQ Management** | http://localhost:15672                | admin/admin |

## 🎯 Funcionalidades

### Frontend (Next.js)

- ✅ Upload de vídeos drag-and-drop
- ✅ Lista de vídeos processados
- ✅ Status em tempo real
- ✅ Visualizador de frames minimalista
- ✅ Download de frames individuais ou ZIP
- ✅ Interface responsiva

### Backend (Spring Boot)

- ✅ API REST completa
- ✅ Processamento assíncrono com RabbitMQ
- ✅ Extração de frames com FFmpeg
- ✅ Servir imagens dos ZIPs
- ✅ Health checks e monitoramento

## 🧪 Como Testar

1. **Acesse o Frontend**: http://localhost:3000
2. **Upload de Vídeo**: Arraste um arquivo de vídeo ou clique para selecionar
3. **Acompanhe**: Status é atualizado em tempo real
4. **Visualize**: Quando concluído, veja os frames extraídos
5. **Download**: Baixe frames individuais ou ZIP completo

### Formatos Suportados

- MP4, WebM, AVI, MOV
- Tamanho máximo: 100MB

---

**🎉 Pronto para processar vídeos em containers!** 🚀
