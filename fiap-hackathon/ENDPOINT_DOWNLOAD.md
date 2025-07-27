# Endpoint de Download do ZIP

## Novo Endpoint Adicionado

### `GET /api/videos/download/{id}`

Este endpoint permite fazer o download do arquivo ZIP contendo os frames extraídos do vídeo após o processamento ser concluído.

#### Parâmetros
- `id` (path parameter): UUID do vídeo processado

#### Respostas

**200 OK**
- **Content-Type**: `application/octet-stream`
- **Content-Disposition**: `attachment; filename="frames_{id}.zip"`
- **Body**: Arquivo ZIP binário contendo os frames extraídos

**404 Not Found**
- Vídeo não encontrado
- Processamento ainda não concluído
- Arquivo ZIP não disponível

#### Exemplo de Uso

```bash
# Fazer upload do vídeo
curl -X POST "http://localhost:8080/api/videos/upload" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@video.mp4"

# Resposta:
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "message": "Video processed successfully",
  ...
}

# Verificar status do processamento
curl "http://localhost:8080/api/videos/status/123e4567-e89b-12d3-a456-426614174000"

# Resposta quando concluído:
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "status": "CONCLUIDO"
}

# Fazer download do ZIP
curl -O -J "http://localhost:8080/api/videos/download/123e4567-e89b-12d3-a456-426614174000"
```

#### Estados do Processamento

1. **"Uploaded"**: Vídeo foi enviado, aguardando processamento
2. **"processando"**: Vídeo está sendo processado (extração de frames)
3. **"CONCLUIDO"**: Processamento finalizado, ZIP disponível para download
4. **"erro"**: Erro durante o processamento

#### Validações

- O vídeo deve existir no sistema
- O status deve ser "CONCLUIDO"
- O arquivo ZIP deve existir no sistema de arquivos

#### Tratamento de Erros

- **Vídeo não encontrado**: Retorna 404
- **Processamento incompleto**: Retorna 404 
- **Arquivo ZIP não encontrado**: Retorna 404

#### Fluxo Completo

1. **Upload**: `POST /api/videos/upload`
2. **Monitoramento**: `GET /api/videos/status/{id}` (polling até status = "CONCLUIDO")
3. **Download**: `GET /api/videos/download/{id}`

#### Swagger UI

Acesse `http://localhost:8080/swagger-ui.html` para testar os endpoints interativamente.
