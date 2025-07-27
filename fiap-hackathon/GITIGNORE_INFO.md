# Configuração do .gitignore

## Pastas Ignoradas

### `outputs/`
- **Propósito**: Armazena arquivos ZIP com frames extraídos dos vídeos
- **Conteúdo**: Arquivos gerados automaticamente pelo `VideoProcessConsumer`
- **Formato**: `frames_{uuid}.zip`
- **Motivo para ignorar**: Arquivos gerados dinamicamente, não devem ser versionados

### `uploads/`
- **Propósito**: Armazena vídeos enviados pelos usuários
- **Conteúdo**: Arquivos de vídeo em diversos formatos (MP4, AVI, MOV, etc.)
- **Formato**: `{timestamp}_{nome_original}`
- **Motivo para ignorar**: Arquivos de usuário, podem ser grandes e não devem ser versionados

## Estrutura Mantida

As pastas `outputs/` e `uploads/` são mantidas no repositório através dos arquivos `README.md` dentro delas:

```
outputs/
├── README.md          # ✅ Commitado (documentação)
└── *.zip             # ❌ Ignorado (arquivos gerados)

uploads/
├── README.md          # ✅ Commitado (documentação)
└── *.mp4, *.avi, etc # ❌ Ignorado (arquivos de usuário)
```

## Outros Arquivos Ignorados

### Temporários de Processamento
- `video_frames_*/` - Diretórios temporários durante extração de frames
- `logs/` - Logs da aplicação
- `*.log` - Arquivos de log individuais

### Configurações Locais
- `application-local.yml`
- `application-local.yaml`
- `application-local.properties`

## Verificação

Para verificar se o .gitignore está funcionando:

```bash
# Criar arquivos de teste
echo "test" > outputs/test.zip
echo "test" > uploads/test.mp4

# Verificar status (não devem aparecer)
git status

# Limpar arquivos de teste
rm outputs/test.zip uploads/test.mp4
```

## Benefícios

1. **Repositório limpo**: Apenas código fonte e configurações são versionados
2. **Performance**: Evita commit de arquivos grandes (vídeos e ZIPs)
3. **Segurança**: Evita versionamento acidental de conteúdo de usuários
4. **Estrutura preservada**: Pastas são mantidas com documentação
